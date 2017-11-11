package com.tinytimrob.ppse.nmo.integration.webui;

import java.net.InetAddress;
import org.apache.logging.log4j.Logger;
import com.tinytimrob.common.CommonUtils;
import com.tinytimrob.common.LogWrapper;
import com.tinytimrob.ppse.nmo.Action;
import com.tinytimrob.ppse.nmo.Integration;
import com.tinytimrob.ppse.nmo.config.NMOConfiguration;
import com.tinytimrob.ppse.nmo.utils.Communicator;

public class IntegrationWebUI extends Integration
{
	private static final Logger log = LogWrapper.getLogger();
	public static final IntegrationWebUI INSTANCE = new IntegrationWebUI();
	private String ddnsLastIP = "";
	private int ddnsUpdateTimer = -1; // forces immediate update on startup

	private IntegrationWebUI()
	{
		super("webUI");
	}

	@Override
	public boolean isEnabled()
	{
		return NMOConfiguration.INSTANCE.integrations.webUI.enabled;
	}

	@Override
	public void init() throws Exception
	{
		if (CommonUtils.isNullOrEmpty(NMOConfiguration.INSTANCE.integrations.webUI.webcamSecurityKey))
		{
			NMOConfiguration.INSTANCE.integrations.webUI.webcamSecurityKey = CommonUtils.generateAsciiCryptoKey(64);
			NMOConfiguration.save();
		}
		WebcamCapture.init();
		WebServer.initialize();
		if (NMOConfiguration.INSTANCE.integrations.webUI.ddns.enabled)
		{
			try
			{
				InetAddress address = InetAddress.getByName(NMOConfiguration.INSTANCE.integrations.webUI.ddns.domain);
				this.ddnsLastIP = address.getHostAddress();
			}
			catch (Throwable t)
			{
				this.ddnsLastIP = "unavailable";
			}
			log.info("DDNS Last IP:  " + this.ddnsLastIP);
		}
		this.actions.put("/webUI/cameraprivacy/on", new Action()
		{
			@Override
			public void onAction() throws Exception
			{
				WebcamCapture.privacyMode = true;
			}

			@Override
			public boolean isHiddenFromWebUI()
			{
				return true;
			}

			@Override
			public boolean isHiddenFromFrontend()
			{
				return false;
			}

			@Override
			public boolean isBlockedFromWebUI()
			{
				return true;
			}

			@Override
			public String getName()
			{
				return "TURN ON CAMERA PRIVACY";
			}

			@Override
			public String getDescription()
			{
				return "Enables camera privacy mode. (Greys out the webcam image)";
			}
		});
		this.actions.put("/webUI/cameraprivacy/off", new Action()
		{
			@Override
			public void onAction() throws Exception
			{
				WebcamCapture.privacyMode = false;
			}

			@Override
			public boolean isHiddenFromWebUI()
			{
				return true;
			}

			@Override
			public boolean isHiddenFromFrontend()
			{
				return false;
			}

			@Override
			public boolean isBlockedFromWebUI()
			{
				return true;
			}

			@Override
			public String getName()
			{
				return "TURN OFF CAMERA PRIVACY";
			}

			@Override
			public String getDescription()
			{
				return "Disables camera privacy mode.";
			}
		});
		if (NMOConfiguration.INSTANCE.integrations.webUI.ultiwakerAPI.enabled)
		{
			new UltiwakerRecoveryThread().start();
		}
	}

	@Override
	public void update() throws Exception
	{
		WebcamCapture.update();
		if (NMOConfiguration.INSTANCE.integrations.webUI.ddns.enabled)
		{
			this.ddnsUpdateTimer++;
			if (this.ddnsUpdateTimer == 0 || this.ddnsUpdateTimer >= (NMOConfiguration.INSTANCE.integrations.webUI.ddns.updateFrequency * 60))
			{
				try
				{
					String currentIP = PortForwarding.getExternalIP();
					if (!currentIP.equals(this.ddnsLastIP))
					{
						log.info("IP MISMATCH DETECTED. Contacting google domains service...");
						log.info("DDNS Last IP:  " + this.ddnsLastIP);
						log.info("Current IP:  " + currentIP);
						String userpass = NMOConfiguration.INSTANCE.integrations.webUI.ddns.username + ":" + NMOConfiguration.INSTANCE.integrations.webUI.ddns.password;
						String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
						String response = Communicator.basicJsonMessage("update IP", NMOConfiguration.INSTANCE.integrations.webUI.ddns.provider + "nic/update?hostname=" + NMOConfiguration.INSTANCE.integrations.webUI.ddns.domain + "&myip=" + currentIP, null, String.class, false, basicAuth);
						if (response.startsWith("good") || response.startsWith("nochg"))
						{
							System.out.println("... Everything OK now");
							this.ddnsLastIP = currentIP;
						}
						else
						{
							// cancel further updates
							System.out.println("... failed :/");
							this.ddnsUpdateTimer = Integer.MIN_VALUE;
						}
					}
					else
					{
						log.info("External IP has not changed. Skipping dynamic DNS update");
					}
				}
				catch (Throwable t)
				{
					throw new RuntimeException("Failed to update DDNS entry", t);
				}
				this.ddnsUpdateTimer = 0;
			}
		}
	}

	@Override
	public void shutdown() throws Exception
	{
		WebServer.shutdown();
		WebcamCapture.shutdown();
	}
}
