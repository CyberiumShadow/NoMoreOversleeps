package com.tinytimrob.ppse.nmo.integration.webui;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.auth.AuthenticationException;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import com.tinytimrob.common.CommonUtils;
import com.tinytimrob.common.LogWrapper;
import com.tinytimrob.ppse.nmo.MainDialog;
import com.tinytimrob.ppse.nmo.config.NMOConfiguration;

@WebSocket
public class WebcamWebSocketHandler implements Runnable
{
	public static enum AuthSource
	{
		WEBUI, ULTIWAKER;
	}

	private static final Logger log = LogWrapper.getLogger();
	private AuthSource authSource;
	private Session session;
	private int camID = -1;
	public String connectionIP;

	private void teardown()
	{
		if (this.session != null)
		{
			if (this.connectionIP != null)
			{
				log.info("WebSocket for cam" + this.camID + " disconnected from " + this.authSource + " at " + this.connectionIP);
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
		if (this.camID != -1)
		{
			WebcamCapture.webcams[this.camID].socketHandlers.remove(this);
		}
	}

	@OnWebSocketConnect
	public void onConnect(Session session) throws AuthenticationException
	{
		Map<String, List<String>> params = session.getUpgradeRequest().getParameterMap();
		List<String> keys = params.get("key");
		this.connectionIP = session.getRemoteAddress().getAddress().toString();
		if (keys == null || keys.size() != 1)
		{
			throw new AuthenticationException("Not authorized from " + this.connectionIP);
		}
		String key = keys.get(0);
		if (key.equals(NMOConfiguration.INSTANCE.integrations.webUI.webcamSecurityKey))
		{
			this.authSource = AuthSource.WEBUI;
		}
		else if (key.equals(NMOConfiguration.INSTANCE.integrations.webUI.ultiwakerAPI))
		{
			this.authSource = AuthSource.ULTIWAKER;
		}
		else
		{
			throw new AuthenticationException("Not authorized from " + this.connectionIP);
		}
		int camIDval;
		List<String> camID = params.get("camID");
		if (camID == null || camID.size() != 1)
		{
			// legacy
			camIDval = 0;
		}
		else
		{
			try
			{
				camIDval = Integer.parseInt(camID.get(0));
			}
			catch (NumberFormatException e)
			{
				throw new AuthenticationException("Bad camID from " + this.connectionIP);
			}
		}
		if (camIDval < 0 || camIDval >= WebcamCapture.webcams.length)
		{
			throw new AuthenticationException("Bad camID " + camIDval + " from " + this.authSource + " at " + this.connectionIP);
		}
		this.camID = camIDval;
		this.session = session;
		if (NMOConfiguration.INSTANCE.integrations.webUI.readProxyForwardingHeaders)
		{
			String xff = session.getUpgradeRequest().getHeader("X-Forwarded-For");
			if (xff != null && !xff.isEmpty())
			{
				this.connectionIP = "/" + xff.split("\\Q, \\E")[0];
			}
		}
		log.info("WebSocket for cam" + this.camID + " connected from " + this.authSource + " at " + this.connectionIP);
		WebcamCapture.webcams[this.camID].socketHandlers.add(this);
		new Thread(this).start();
	}

	@Override
	public void run()
	{
		log.info(">> Started sending cam" + this.camID + " data to " + this.authSource + " at " + this.connectionIP);
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("type", "image");
		while (this.session != null)
		{
			message.put("image", WebcamCapture.webcams[this.camID].imageBase64);
			if (this.authSource == AuthSource.ULTIWAKER)
			{
				message.put("time", MainDialog.now);
				message.put("paused", MainDialog.isCurrentlyPaused.get());
				message.put("pauseReason", MainDialog.pauseReason);
			}
			try
			{
				this.send(message);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				break;
			}
			catch (WebSocketException e)
			{
				e.printStackTrace();
				break;
			}
			try
			{
				Thread.sleep(60);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		log.info(">> Stopped sending cam" + this.camID + " data to " + this.authSource + " at " + this.connectionIP);
	}

	@OnWebSocketMessage
	public void onMessage(String message)
	{
		log.info("WebSocket message: {}", message);
	}

	@OnWebSocketError
	public void onError(Throwable t)
	{
		log.error("WebSocket error", t);
		this.teardown();
	}

	@OnWebSocketClose
	public void onClose(int status, String reason)
	{
		this.teardown();
	}

	private void send(String message) throws IOException
	{
		if (this.session != null && this.session.isOpen())
		{
			this.session.getRemote().sendString(message);
		}
	}

	private void send(Object object) throws IOException
	{
		this.send(CommonUtils.GSON.toJson(object));
	}
}