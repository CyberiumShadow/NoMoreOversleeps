package com.tinytimrob.ppse.nmo.integration.webui;

import java.util.LinkedHashMap;
import com.google.gson.annotations.Expose;

public class WebUIConfiguration
{
	@Expose
	public boolean enabled = false;

	@Expose
	public String hostname = "";

	@Expose
	public int jettyPort = 19992;

	@Expose
	public LinkedHashMap<String, String> webcams = new LinkedHashMap<String, String>();

	@Expose
	public String webcamSecurityKey = "";

	@Expose
	public boolean allowRemotePauseControl = false;
}
