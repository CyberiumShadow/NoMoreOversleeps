package com.tinytimrob.ppse.nmo.integration.webui;

import java.util.LinkedHashMap;
import com.google.gson.annotations.Expose;

public class WebUIConfiguration
{
	@Expose
	public boolean enabled = false;

	@Expose
	public boolean openUiLocally = false;

	@Expose
	public int jettyPort = 19992;

	@Expose
	public LinkedHashMap<String, String> webcams = new LinkedHashMap<String, String>();

	@Expose
	public String webcamSecurityKey = "";

	@Expose
	public boolean allowRemotePauseControl = false;

	@Expose
	public boolean readProxyForwardingHeaders = false;

	@Expose
	public DynDnsUpdateConfiguration ddns = new DynDnsUpdateConfiguration();

	public static class DynDnsUpdateConfiguration
	{
		@Expose
		public boolean enabled = false;

		@Expose
		public String provider = "https://domains.google.com/";

		@Expose
		public String domain = "";

		@Expose
		public String username = "";

		@Expose
		public String password = "";

		@Expose
		public int updateFrequency = 300;
	}

	@Expose
	public UltiwakerConfiguration ultiwakerAPI = new UltiwakerConfiguration();

	public static class UltiwakerConfiguration
	{
		@Expose
		public boolean enabled = false;

		@Expose
		public int framesToAverage = 20;

		@Expose
		public int matchPercentageToResetTimer = 75;
	}
}
