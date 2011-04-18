package com.senstastic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;

public class FileUtility
{	
	public static void writeFile(Context context, String directoryName, String fileName, String content)
	{
		try
		{
			File directory = getDirectory(context, directoryName);
			if (!directory.exists() && !directory.mkdir())
				throw new Exception();
			
			File file = getFile(context, directoryName, fileName);
			
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(content.getBytes());
			fileOutputStream.close();
		}
		catch(Exception e)
		{
			Logger.e("Could not write file!");
			return;
		}
	}
	
	public static boolean fileExists(Context context, String directoryName, String fileName)
	{
		return getFile(context, directoryName, fileName).exists();
	}
	
	public static String readFile(Context context, String directoryName, String fileName)
	{
		try
		{
			FileInputStream fileInputStream = new FileInputStream(getFile(context, directoryName, fileName));
			
			StringBuilder stringBuilder = new StringBuilder();
			int ch;
			while((ch = fileInputStream.read()) != -1)
				stringBuilder.append((char)ch);
			
			fileInputStream.close();
			
			return stringBuilder.toString();
		}
		catch(Exception e)
		{
			Logger.e("Could not read file!" + e.getMessage());
			return null;
		}
	}
	
	public static void deleteFile(Context context, String directoryName, String fileName)
	{
		getFile(context, directoryName, fileName).delete();
	}
	
	private static File getFile(Context context, String directoryName, String fileName)
	{
		return new File(context.getFilesDir().getAbsolutePath() + File.separator + directoryName + File.separator + fileName);
	}
	
	private static File getDirectory(Context context, String directoryName)
	{
		return new File(context.getFilesDir().getAbsolutePath() + File.separator + directoryName);
	}
}
