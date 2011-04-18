package com.senstastic;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;

public class FileUtility
{	
	public synchronized static void writeFile(Context context, String fileName, String content)
	{
		try
		{
			FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			fileOutputStream.write(content.getBytes());
			fileOutputStream.close();
		}
		catch(Exception e)
		{
			Logger.e("Could not write file!");
			return;
		}
	}
	
	public synchronized static String readFile(Context context, String fileName)
	{
		try
		{
			FileInputStream fileInputStream = context.openFileInput(fileName);
			
			StringBuilder stringBuilder = new StringBuilder();
			int ch;
			while((ch = fileInputStream.read()) != -1)
				stringBuilder.append((char)ch);
			
			fileInputStream.close();
			
			return stringBuilder.toString();
		}
		catch(Exception e)
		{
			Logger.e("Could not read file!");
			return null;
		}
	}
	
	public synchronized static void deleteFile(Context context, String fileName)
	{
		
	}
}
