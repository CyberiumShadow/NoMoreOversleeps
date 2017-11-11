package com.tinytimrob.ppse.nmo.integration.webui;

import java.util.Map;
import com.tinytimrob.common.CommonUtils;
import com.tinytimrob.ppse.nmo.Action;
import com.tinytimrob.ppse.nmo.Integration;
import com.tinytimrob.ppse.nmo.MainDialog;
import com.tinytimrob.ppse.nmo.config.NMOConfiguration;
import javafx.application.Platform;

public class PauseFakeIntegration extends Integration
{
	public PauseFakeIntegration()
	{
		super("pause");
	}

	public static PauseFakeIntegration INSTANCE = new PauseFakeIntegration();

	@Override
	public boolean isEnabled()
	{
		return NMOConfiguration.INSTANCE.integrations.webUI.allowRemotePauseControl;
	}

	@Override
	public void init() throws Exception
	{
		this.actions.put("/pause/0", new Action()
		{
			@Override
			public void onAction(Map<String, String[]> parameters) throws Exception
			{
				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						MainDialog.pausedUntil = 0;
						MainDialog.isCurrentlyPaused.set(false);
						MainDialog.triggerEvent("Unpaused via action trigger", NMOConfiguration.INSTANCE.events.pauseCancelled);
					}
				});
			}

			@Override
			public boolean isHiddenFromFrontend()
			{
				return true;
			}

			@Override
			public boolean isHiddenFromWebUI()
			{
				return true;
			}

			@Override
			public boolean isBlockedFromWebUI()
			{
				return false;
			}

			@Override
			public String getName()
			{
				return "TRIGGERED UNPAUSE";
			}

			@Override
			public String getDescription()
			{
				return "Unpause";
			}
		});

		for (int i = 1; i <= 720; i++)
		{
			final int j = i;
			this.actions.put("/pause/" + i, new Action()
			{
				@Override
				public void onAction(final Map<String, String[]> parameters) throws Exception
				{
					Platform.runLater(new Runnable()
					{
						@Override
						public void run()
						{
							if (parameters != null)
							{
								for (String k : parameters.keySet())
								{
									System.out.println(k + " = " + parameters.get(k)[0]);
								}
							}
							String reason = getName();
							;
							if (parameters != null && parameters.containsKey("reason"))
							{
								reason = parameters.get("reason")[0];
							}
							MainDialog.pausedUntil = MainDialog.now + (j * 60000);
							MainDialog.pauseReason = reason;
							MainDialog.triggerEvent("Triggered pause for " + j + " minutes for \"" + reason + "\" (until " + CommonUtils.dateFormatter.format(MainDialog.pausedUntil) + ")", NMOConfiguration.INSTANCE.events.pauseInitiated);
						}
					});
				}

				@Override
				public boolean isHiddenFromFrontend()
				{
					return true;
				}

				@Override
				public boolean isHiddenFromWebUI()
				{
					return true;
				}

				@Override
				public boolean isBlockedFromWebUI()
				{
					return false;
				}

				@Override
				public String getName()
				{
					return "TRIGGERED " + j + " MINUTE PAUSE";
				}

				@Override
				public String getDescription()
				{
					return "Pause for " + j + " minutes";
				}
			});
		}
	}

	@Override
	public void update() throws Exception
	{

	}

	@Override
	public void shutdown() throws Exception
	{

	}
}
