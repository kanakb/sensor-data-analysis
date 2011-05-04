package com.senstastic;

import java.io.StringWriter;

import org.xmlpull.v1.XmlSerializer;

import android.util.Base64;
import android.util.Xml;

public class XMLStringGenerator 
{
	StringWriter stringWriter;
	XmlSerializer xmlSerializer;
	String rootTagName;
	
	public void begin(String rootTagName)
	{
		try
		{
			this.rootTagName = rootTagName;
			
			stringWriter = new StringWriter();
			
			xmlSerializer = Xml.newSerializer();
			xmlSerializer.setOutput(stringWriter);
			xmlSerializer.startDocument("UTF-8", true);
			xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
		
			xmlSerializer.startTag(null, rootTagName);
		}
		catch(Exception e)
		{
			error();
		}
	}
	
	public void startTag(String tagName)
	{
		try
		{
			xmlSerializer.startTag(null, tagName);
		}
		catch(Exception e)
		{
			error();
		}
	}
	
	public void endTag(String tagName)
	{
		try
		{
			xmlSerializer.endTag(null, tagName);
		}
		catch(Exception e)
		{
			error();
		}
	}	
	
	public void addTag(String tagName, Object value)
	{
		try
		{
			if (value == null)
				return;
			
			xmlSerializer.startTag(null, tagName);
			
			String text;
			if (value instanceof byte[])
				text = Base64.encodeToString((byte[])value, Base64.DEFAULT);
			else
				text = value.toString();
			
			xmlSerializer.text(text);
			xmlSerializer.endTag(null, tagName);
		}
		catch(Exception e)
		{
			error();
		}
	}
	
	public String end()
	{
		try
		{
			xmlSerializer.endTag(null, rootTagName);
			xmlSerializer.endDocument();
			return stringWriter.toString();
		}
		catch(Exception e)
		{
			error();
			return null;
		}
	}
	
	private void error()
	{
		Logger.e("Error generating xml string!");
	}
}
