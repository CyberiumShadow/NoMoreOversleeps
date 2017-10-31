package com.tinytimrob.ppse.nmo.integration.webui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.logging.log4j.Logger;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamImageTransformer;
import com.github.sarxos.webcam.ds.buildin.WebcamDefaultDevice;
import com.github.sarxos.webcam.util.jh.JHGrayFilter;
import com.tinytimrob.common.CommonUtils;
import com.tinytimrob.common.LogWrapper;
import com.tinytimrob.ppse.nmo.MainDialog;
import com.tinytimrob.ppse.nmo.config.NMOConfiguration;
import com.tinytimrob.ppse.nmo.config.NMOStatistics;
import com.tinytimrob.ppse.nmo.utils.FormattingHelper;

public class WebcamCapture
{
	private static final Logger log = LogWrapper.getLogger();
	public static WebcamData[] webcams;

	public static class WebcamTransformer implements WebcamImageTransformer
	{
		private static final JHGrayFilter GRAY = new JHGrayFilter();

		@Override
		public BufferedImage transform(BufferedImage image)
		{
			image = GRAY.filter(image, null);
			Graphics2D graphics = image.createGraphics();
			Font font = new Font("ARIAL", Font.PLAIN, 11);
			graphics.setFont(font);
			graphics.setColor(Color.BLACK);
			graphics.fillRect(0, 0, 320, 20);
			graphics.setColor(Color.WHITE);
			long now = System.currentTimeMillis();
			String str = CommonUtils.convertTimestamp(now);
			if (NMOStatistics.INSTANCE.scheduleStartedOn != 0)
			{
				str = str + "   " + FormattingHelper.formatTimeElapsedWithDays(NMOStatistics.INSTANCE.scheduleStartedOn == 0 ? 0 : now, NMOStatistics.INSTANCE.scheduleStartedOn) + "   " + FormattingHelper.formatTimeElapsedWithDays(NMOStatistics.INSTANCE.scheduleStartedOn == 0 ? 0 : now, NMOStatistics.INSTANCE.scheduleLastOversleep);
			}
			graphics.drawString(str, 4, 14);
			if (MainDialog.isCurrentlyPaused.get() && !((MainDialog.scheduleStatusShort.startsWith("CORE ") || MainDialog.scheduleStatusShort.startsWith("NAP ")) && MainDialog.pauseReason.startsWith("Sleep block: ")))
			{
				graphics.setColor(Color.BLACK);
				graphics.fillRect(0, 204, 320, 36);
				graphics.setColor(Color.WHITE);
				graphics.drawString("PAUSED for \"" + MainDialog.pauseReason + "\"\n", 4, 218);
				graphics.drawString("until " + CommonUtils.dateFormatter.format(MainDialog.pausedUntil), 4, 234);
			}
			else
			{
				graphics.setColor(Color.BLACK);
				graphics.fillRect(0, 220, 320, 20);
				graphics.setColor(Color.WHITE);
				graphics.drawString(MainDialog.scheduleStatus, 4, 234);
			}
			image.flush();
			graphics.dispose();
			return image;
		}
	}

	public static void init()
	{
		LinkedHashMap<String, Webcam> detectedCameras = new LinkedHashMap<String, Webcam>();
		List<Webcam> cams = Webcam.getWebcams();
		for (Webcam cam : cams)
		{
			log.info("Found webcam: " + cam.getName());
			detectedCameras.put(cam.getName(), cam);
		}
		if (detectedCameras.size() == 0)
		{
			throw new RuntimeException("The WebUI module has been enabled but no webcams are connected to the machine");
		}
		if (NMOConfiguration.INSTANCE.integrations.webUI.webcams.size() == 0)
		{
			throw new RuntimeException("You must specify at least one webcam in the NMO config file if the WebUI module is enabled");
		}
		webcams = new WebcamData[NMOConfiguration.INSTANCE.integrations.webUI.webcams.size()];
		int id = 0;
		for (String cc : NMOConfiguration.INSTANCE.integrations.webUI.webcams.keySet())
		{
			String camName = NMOConfiguration.INSTANCE.integrations.webUI.webcams.get(cc);
			initCamera(id, cc, camName);
			id++;
		}
	}

	static void initCamera(int id, String cc, String camName)
	{
		LinkedHashMap<String, Webcam> detectedCameras = new LinkedHashMap<String, Webcam>();
		List<Webcam> cams = Webcam.getWebcams();
		for (Webcam cam : cams)
		{
			detectedCameras.put(cam.getName(), cam);
		}
		Webcam webcam = detectedCameras.get(camName);
		if (webcam == null)
		{
			throw new RuntimeException("Webcam listed in config was not found: " + camName);
		}
		WebcamData data = new WebcamData(cc, webcam);
		webcam.setImageTransformer(new WebcamTransformer());
		log.info("Initializing webcam: " + camName);
		Dimension[] sizes = webcam.getViewSizes();
		Dimension dimension = null;
		for (Dimension d : sizes)
		{
			log.info("Found image dimension: " + d.getWidth() + "x" + d.getHeight());
			if (d.getWidth() == 320)
			{
				dimension = d;
			}
		}
		if (dimension == null)
		{
			dimension = sizes[0];
		}
		log.info("Selected image dimension: " + dimension.getWidth() + "x" + dimension.getHeight());
		webcam.setViewSize(dimension);
		webcam.open(true);
		webcam.addWebcamListener(data);
		System.out.println(webcam.getViewSize());
		((WebcamDefaultDevice) webcam.getDevice()).FAULTY = false;
		webcams[id] = data;
	}

	public static synchronized void update()
	{
		for (int i = 0; i < webcams.length; i++)
		{
			WebcamData data = webcams[i];
			BufferedImage img = data.webcam.getImage();
			if (img == null || ((WebcamDefaultDevice) data.webcam.getDevice()).FAULTY)
			{
				String camName = data.webcam.getName();
				data.webcam.removeWebcamListener(data);
				data.webcam.close();
				ArrayList<WebcamWebSocketHandler> handlers = (ArrayList<WebcamWebSocketHandler>) data.socketHandlers.clone();
				for (WebcamWebSocketHandler handler : handlers)
				{
					data.socketHandlers.remove(handler);
				}
				data.webcam = null;
				initCamera(i, data.cc, camName);
				webcams[i].image = data.image;
				webcams[i].imageBase64 = data.imageBase64;
				for (WebcamWebSocketHandler handler : handlers)
				{
					webcams[i].socketHandlers.add(handler);
				}
			}
			else
			{
				data.image = img;
			}
		}
	}

	public static BufferedImage getImage()
	{
		return webcams[0].image;
	}

	public static String getCameraName()
	{
		return webcams[0].webcam == null ? "null" : webcams[0].webcam.getName();
	}

	public static void shutdown()
	{
		for (WebcamData data : webcams)
		{
			data.webcam.removeWebcamListener(data);
			data.webcam.close();
			ArrayList<WebcamWebSocketHandler> handlers = (ArrayList<WebcamWebSocketHandler>) data.socketHandlers.clone();
			for (WebcamWebSocketHandler handler : handlers)
			{
				data.socketHandlers.remove(handler);
			}
		}
	}
}
