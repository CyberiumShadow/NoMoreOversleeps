package com.tinytimrob.ppse.nmo.integration.webui;

import org.apache.logging.log4j.Logger;
import com.tinytimrob.common.LogWrapper;
import com.tinytimrob.ppse.nmo.config.NMOConfiguration;
import com.tinytimrob.ppse.nmo.utils.Communicator;

public class UltiwakerRecoveryThread extends Thread
{
	private static final Logger log = LogWrapper.getLogger();

	public UltiwakerRecoveryThread()
	{
		this.setDaemon(true);
		this.setName("Ultiwaker Recovery Thread");
	}

	int connectLoop = 105;

	@Override
	public void run()
	{
		while (true)
		{
			this.connectLoop++;
			if ((this.connectLoop % 120) == 0)
			{
				this.connectLoop = 0;
				try
				{
					if (UltiwakerWebSocketHandler.connections.isEmpty())
					{
						log.info("Ultiwaker is not connected. Attempting recovery");
						String hostname = NMOConfiguration.INSTANCE.integrations.webUI.ddns.domain;
						if (hostname == null || hostname.isEmpty())
						{
							hostname = PortForwarding.getExternalIP();
						}
						int port = NMOConfiguration.INSTANCE.integrations.webUI.jettyPort;
						String key = NMOConfiguration.INSTANCE.integrations.webUI.webcamSecurityKey;
						int camID = 0;
						String url = NMOConfiguration.INSTANCE.integrations.webUI.ultiwakerAPI.server + "api/v1/" + hostname + "/" + port + "/" + key + "/" + camID;
						Communicator.basicJsonMessage("Ultiwaker connect", url, "", null, false, null);
					}
				}
				catch (Throwable t)
				{
					// TODO Auto-generated catch block
					t.printStackTrace();
				}
			}
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}