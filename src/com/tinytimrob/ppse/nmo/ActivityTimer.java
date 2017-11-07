package com.tinytimrob.ppse.nmo;

import com.google.gson.annotations.Expose;

public class ActivityTimer
{
	@Expose
	public String name = "";

	@Expose
	public long secondsForFirstWarning = 300;

	@Expose
	public long secondsForSubsequentWarnings = 10;

	@Expose
	public long zombiePenaltyForFirstWarning = 120;
	
	@Expose
	public long zombiePenaltyForOversleepWarning = 10;

	@Expose
	public long zombiePenaltyForOtherWarnings = 10;

	@Expose
	public long zombiePenaltyLimit = 300;
}
