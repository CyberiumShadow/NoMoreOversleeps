package com.tinytimrob.ppse.nmo.integration.input;

import org.jnativehook.GlobalScreen;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import com.tinytimrob.ppse.nmo.Integration;
import com.tinytimrob.ppse.nmo.MainDialog;
import com.tinytimrob.ppse.nmo.config.NMOConfiguration;

public class IntegrationMouse extends Integration
{
	public IntegrationMouse()
	{
		super("mouse");
	}

	public static final IntegrationMouse INSTANCE = new IntegrationMouse();
	NativeMouseInputListener mouseHook;

	@Override
	public boolean isEnabled()
	{
		return NMOConfiguration.INSTANCE.integrations.mouse.enabled;
	}

	@Override
	public void init()
	{
		this.mouseHook = new NativeMouseInputListener()
		{
			@Override
			public void nativeMouseReleased(NativeMouseEvent nativeEvent)
			{
				MainDialog.resetActivityTimer(IntegrationMouse.this.id);
			}

			@Override
			public void nativeMousePressed(NativeMouseEvent nativeEvent)
			{
				MainDialog.resetActivityTimer(IntegrationMouse.this.id);
			}

			@Override
			public void nativeMouseClicked(NativeMouseEvent nativeEvent)
			{
				MainDialog.resetActivityTimer(IntegrationMouse.this.id);
			}

			@Override
			public void nativeMouseMoved(NativeMouseEvent nativeEvent)
			{
				MainDialog.resetActivityTimer(IntegrationMouse.this.id);
			}

			@Override
			public void nativeMouseDragged(NativeMouseEvent nativeEvent)
			{
				MainDialog.resetActivityTimer(IntegrationMouse.this.id);
			}
		};
		GlobalScreen.addNativeMouseListener(this.mouseHook);
		GlobalScreen.addNativeMouseMotionListener(this.mouseHook);
	}

	@Override
	public void update() throws Exception
	{

	}

	@Override
	public void shutdown()
	{
		if (this.mouseHook != null)
		{
			GlobalScreen.removeNativeMouseListener(this.mouseHook);
			GlobalScreen.removeNativeMouseMotionListener(this.mouseHook);
		}
	}
}
