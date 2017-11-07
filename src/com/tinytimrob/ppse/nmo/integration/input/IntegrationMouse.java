package com.tinytimrob.ppse.nmo.integration.input;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import org.jnativehook.GlobalScreen;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import com.tinytimrob.ppse.nmo.ActivitySource;
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
	public static final ActivitySource MOUSE_RELEASED = new ActivitySource("mouseReleased");
	public static final ActivitySource MOUSE_PRESSED = new ActivitySource("mousePressed");
	public static final ActivitySource MOUSE_CLICKED = new ActivitySource("mouseClicked");
	public static final ActivitySource MOUSE_MOVED = new ActivitySource("mouseMoved");
	public static final ActivitySource MOUSE_DRAGGED = new ActivitySource("mouseDragged");
	public static final ActivitySource MOUSE_POINTER = new ActivitySource("mousePointer");
	public static volatile Point lastCursorPoint = MouseInfo.getPointerInfo().getLocation();
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
				MainDialog.resetActivityTimer(MOUSE_RELEASED);
			}

			@Override
			public void nativeMousePressed(NativeMouseEvent nativeEvent)
			{
				MainDialog.resetActivityTimer(MOUSE_PRESSED);
			}

			@Override
			public void nativeMouseClicked(NativeMouseEvent nativeEvent)
			{
				MainDialog.resetActivityTimer(MOUSE_CLICKED);
			}

			@Override
			public void nativeMouseMoved(NativeMouseEvent nativeEvent)
			{
				MainDialog.resetActivityTimer(MOUSE_MOVED);
			}

			@Override
			public void nativeMouseDragged(NativeMouseEvent nativeEvent)
			{
				MainDialog.resetActivityTimer(MOUSE_DRAGGED);
			}
		};
		GlobalScreen.addNativeMouseListener(this.mouseHook);
		GlobalScreen.addNativeMouseMotionListener(this.mouseHook);
	}

	@Override
	public void update() throws Exception
	{
		PointerInfo pi = MouseInfo.getPointerInfo();
		Point epoint = pi == null ? lastCursorPoint : pi.getLocation();
		if (!epoint.equals(lastCursorPoint))
		{
			lastCursorPoint = epoint;
			MainDialog.resetActivityTimer(MOUSE_POINTER);
		}
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
