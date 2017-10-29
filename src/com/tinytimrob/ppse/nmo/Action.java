package com.tinytimrob.ppse.nmo;

public interface Action
{
	/** 
	 * Runs this action
	 * 
	 * @throws Exception
	 */
	public void onAction() throws Exception;

	public String getName();

	public String getDescription();

	public boolean isHiddenFromFrontend();

	public boolean isHiddenFromWebUI();

	public boolean isBlockedFromWebUI();
}
