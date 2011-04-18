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
	
	public void addTag(String tagName, byte[] value)
	{
		try
		{
			xmlSerializer.startTag(null, tagName);
			xmlSerializer.text(Base64.encodeToString(value, Base64.DEFAULT));
			xmlSerializer.endTag(null, tagName);
		}
		catch(Exception e)
		{
			error();
		}
	}
	
	public void addTag(String tagName, String value)
	{
		try
		{
			xmlSerializer.startTag(null, tagName);
			xmlSerializer.text(value);
			xmlSerializer.endTag(null, tagName);
		}
		catch(Exception e)
		{
			error();
		}
	}
	
	public void addTag(String tagName, int value)
	{
		try
		{
			xmlSerializer.startTag(null, tagName);
			xmlSerializer.text(Integer.toString(value));
			xmlSerializer.endTag(null, tagName);
		}
		catch(Exception e)
		{
			error();
		}
	}
	
	public void addTag(String tagName, long value)
	{
		try
		{
			xmlSerializer.startTag(null, tagName);
			xmlSerializer.text(Long.toString(value));
			xmlSerializer.endTag(null, tagName);
		}
		catch(Exception e)
		{
			error();
		}
	}
	
	public void addTag(String tagName, float value)
	{
		try
		{
			xmlSerializer.startTag(null, tagName);
			xmlSerializer.text(Float.toString(value));
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
		System.err.println("Error generating xml string!");
	}
}
