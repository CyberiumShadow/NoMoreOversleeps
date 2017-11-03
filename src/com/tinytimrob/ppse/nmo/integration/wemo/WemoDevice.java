package com.tinytimrob.ppse.nmo.integration.wemo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.commons.io.IOUtils;
import com.tinytimrob.common.CommonUtils;
import com.tinytimrob.ppse.nmo.Main;

public class WemoDevice
{
	private String ipAddress;

	public WemoDevice(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}

	public void toggle(boolean state) throws IOException
	{
		int i = state ? 1 : 0;
		String data = "<?xml version=\"1.0\" encoding=\"utf-8\"?><s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><s:Body><u:SetBinaryState xmlns:u=\"urn:Belkin:service:basicevent:1\"><BinaryState>" + i + "</BinaryState></u:SetBinaryState></s:Body></s:Envelope>";
		String soapAction = "\"urn:Belkin:service:basicevent:1#SetBinaryState\"";
		this.send("setting WeMo switch status", data, soapAction);
	}

	public boolean isOn() throws IOException
	{
		String data = "<?xml version=\"1.0\" encoding=\"utf-8\"?><s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><s:Body><u:GetBinaryState xmlns:u=\"urn:Belkin:service:basicevent:1\"></u:GetBinaryState></s:Body></s:Envelope>";
		String soapAction = "\"urn:Belkin:service:basicevent:1#GetBinaryState\"";
		String ret = this.send("getting WeMo switch status", data, soapAction);
		return ret != null && ret.contains("<BinaryState>1</BinaryState>");
	}

	protected String send(String action, String datastr, String soapAction)
	{
		try
		{
			HttpURLConnection connection = null;
			OutputStream out = null;
			InputStream in = null;

			try
			{
				connection = (HttpURLConnection) new URL("http://" + this.ipAddress + ":49153/upnp/control/basicevent1").openConnection();
				connection.setConnectTimeout(15000);
				connection.setReadTimeout(15000);
				connection.setUseCaches(false);
				connection.setRequestProperty("User-Agent", "NoMoreOversleeps/" + Main.VERSION);
				byte[] data = datastr.getBytes(CommonUtils.charsetUTF8);
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
				connection.setRequestProperty("Content-Length", Integer.toString(data.length));
				connection.setRequestProperty("Content-Language", "en-US");
				connection.setRequestProperty("Accept", "");
				connection.setRequestProperty("SOAPACTION", soapAction);
				connection.setDoOutput(true);
				connection.connect();
				out = connection.getOutputStream();
				out.write(data);
				int responseCode = connection.getResponseCode();
				in = connection.getInputStream();
				String responseString = IOUtils.toString(in, CommonUtils.charsetUTF8);
				return responseString;
			}
			catch (Throwable t)
			{
				throw new Exception("Communication error while " + action + " on IP address '" + this.ipAddress + "'", t);
			}
			finally
			{
				if (out != null)
				{
					try
					{
						out.close();
					}
					catch (IOException e)
					{
					}
				}

				if (in != null)
				{
					try
					{
						in.close();
					}
					catch (IOException e)
					{
					}
				}

				if (connection != null)
				{
					connection.disconnect();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}