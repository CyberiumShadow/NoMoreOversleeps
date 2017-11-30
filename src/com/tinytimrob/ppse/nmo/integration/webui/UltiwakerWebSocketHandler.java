package com.tinytimrob.ppse.nmo.integration.webui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.auth.AuthenticationException;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import com.tinytimrob.common.LogWrapper;
import com.tinytimrob.ppse.nmo.ActivitySource;
import com.tinytimrob.ppse.nmo.MainDialog;
import com.tinytimrob.ppse.nmo.config.NMOConfiguration;

@WebSocket
public class UltiwakerWebSocketHandler
{
	static class FaceData
	{
		long timestamp = 0;
		int camID = 0;
		boolean face = false;
		boolean leftEye = false;
		boolean rightEye = false;
	}

	public static final ArrayList<UltiwakerWebSocketHandler> connections = new ArrayList<UltiwakerWebSocketHandler>();
	public static final ActivitySource ULTIWAKER_API = new ActivitySource("ultiwakerAPI");
	private static final Logger log = LogWrapper.getLogger();
	private Session session;
	public String connectionIP;
	private FaceData[] faceData;
	private int nextFaceDataIndex = 0;

	public UltiwakerWebSocketHandler()
	{
		this.faceData = new FaceData[NMOConfiguration.INSTANCE.integrations.webUI.ultiwakerAPI.framesToAverage];
		for (int i = 0; i < this.faceData.length; i++)
		{
			this.faceData[i] = new FaceData();
		}
	}

	private void teardown()
	{
		if (this.session != null)
		{
			if (this.connectionIP != null)
			{
				log.info("UWAPI disconnect from " + this.connectionIP);
			}
			try
			{
				this.session.close();
				this.session = null;
			}
			catch (Throwable t)
			{
				//
			}
		}
		connections.remove(this);
	}

	@OnWebSocketConnect
	public void onConnect(Session session) throws AuthenticationException
	{
		Map<String, List<String>> params = session.getUpgradeRequest().getParameterMap();
		List<String> keys = params.get("key");
		if (keys == null || keys.size() != 1 || !keys.get(0).equals(NMOConfiguration.INSTANCE.integrations.webUI.ultiwakerAPI.apiSecurityKey))
		{
			throw new AuthenticationException("Not authorized");
		}
		this.session = session;
		this.connectionIP = session.getRemoteAddress().getAddress().toString();
		connections.add(this);
		log.info("UWAPI connect from " + this.connectionIP);
	}

	@OnWebSocketMessage
	public void onMessage(String message)
	{
		String[] split = message.split(",");
		if (split.length == 5)
		{
			try
			{
				this.nextFaceDataIndex = (this.nextFaceDataIndex + 1) % this.faceData.length;
				long timestamp = Long.parseLong(split[0]);
				int camID = Integer.parseInt(split[1]);
				boolean face = !split[2].equals("0");
				boolean leftEye = !split[3].equals("0");
				boolean rightEye = !split[4].equals("0");
				this.faceData[this.nextFaceDataIndex].timestamp = timestamp;
				this.faceData[this.nextFaceDataIndex].camID = camID;
				this.faceData[this.nextFaceDataIndex].face = face;
				this.faceData[this.nextFaceDataIndex].leftEye = leftEye;
				this.faceData[this.nextFaceDataIndex].rightEye = rightEye;
				this.runFaceAverager(this.nextFaceDataIndex);
			}
			catch (Throwable e)
			{
				log.info(MainDialog.now + " recieved bad UWAPI message: {}", message);
				e.printStackTrace();
			}
		}
	}

	private void runFaceAverager(int idx)
	{
		if (this.faceData[idx].face && this.faceData[idx].leftEye && this.faceData[idx].rightEye)
		{
			int positiveMatches = 0;
			int results = 0;
			for (int i = 0; i < this.faceData.length; i++)
			{
				if (this.faceData[i].timestamp != 0)
				{
					results += 2;
					if (this.faceData[i].leftEye)
					{
						positiveMatches++;
					}
					if (this.faceData[i].rightEye)
					{
						positiveMatches++;
					}
				}
			}
			int perc = (int) ((positiveMatches / (float) results) * 100);
			//log.info("Trend: " + positiveMatches + "/" + results + " eyes (" + perc + "%) were located");
			if (perc >= NMOConfiguration.INSTANCE.integrations.webUI.ultiwakerAPI.matchPercentageToResetTimer)
			{
				MainDialog.resetActivityTimer(ULTIWAKER_API);
			}
		}
	}

	@OnWebSocketError
	public void onError(Throwable t)
	{
		log.error("UWAPI error", t);
		this.teardown();
	}

	@OnWebSocketClose
	public void onClose(int status, String reason)
	{
		this.teardown();
	}
}
