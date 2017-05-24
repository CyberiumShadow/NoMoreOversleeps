package com.tinytimrob.ppse.nmo.integrations;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import com.google.gson.annotations.Expose;
import com.tinytimrob.ppse.nmo.MainDialog;
import com.tinytimrob.ppse.nmo.NMOConfiguration;

public class IntegrationKeyboard extends Integration
{
	NativeKeyListener keyboardHook;

	public static class KeyboardConfiguration
	{
		@Expose
		public boolean enabled;
	}

	@Override
	public boolean isEnabled()
	{
		return NMOConfiguration.instance.integrations.keyboard.enabled;
	}

	@Override
	public void init()
	{
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		try
		{
			GlobalScreen.registerNativeHook();
		}
		catch (NativeHookException e)
		{
			throw new RuntimeException(e);
		}
		this.keyboardHook = new NativeKeyListener()
		{
			@Override
			public void nativeKeyTyped(NativeKeyEvent nativeEvent)
			{
				MainDialog.resetActivityTimer();
			}

			@Override
			public void nativeKeyPressed(NativeKeyEvent nativeEvent)
			{
				MainDialog.resetActivityTimer();
			}

			@Override
			public void nativeKeyReleased(NativeKeyEvent nativeEvent)
			{
				MainDialog.resetActivityTimer();
			}
		};
		GlobalScreen.addNativeKeyListener(this.keyboardHook);
	}

	@Override
	public void update() throws Exception
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void shutdown()
	{
		if (this.keyboardHook != null)
		{
			GlobalScreen.removeNativeKeyListener(this.keyboardHook);
		}
		try
		{
			GlobalScreen.unregisterNativeHook();
		}
		catch (NativeHookException e)
		{
			throw new RuntimeException(e);
		}
	}
}
