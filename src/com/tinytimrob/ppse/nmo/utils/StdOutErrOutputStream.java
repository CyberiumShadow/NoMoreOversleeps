package com.tinytimrob.ppse.nmo.utils;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

class StdOutErrOutputStream extends OutputStream
{
	private final Logger logger;
	private final Level logLevel;

	public StdOutErrOutputStream(Logger logger, Level logLevel)
	{
		super();
		this.logger = logger;
		this.logLevel = logLevel;
	}

	@Override
	public void write(byte[] b) throws IOException
	{
		String string = new String(b);
		if (!string.trim().isEmpty())
			this.logger.log(this.logLevel, string);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		String string = new String(b, off, len);
		if (!string.trim().isEmpty())
			this.logger.log(this.logLevel, string);
	}

	@Override
	public void write(int b) throws IOException
	{
		String string = String.valueOf((char) b);
		if (!string.trim().isEmpty())
			this.logger.log(this.logLevel, string);
	}
}