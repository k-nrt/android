package com.nrt.basic;

import android.util.Log;
import android.os.Environment;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import android.widget.*;

public class TextViewLog
{
	public android.os.Handler m_handler = null;
	public android.widget.TextView m_textView = null;
	public FileWriter m_writer = null;
	public FrameTimer m_timer = null;
	
	public static class TextHandler implements Runnable
	{
		public String Text = null;
		public android.widget.TextView m_textView = null;
		
		public TextHandler( String strText, android.widget.TextView textView )
		{
			Text = strText;
			m_textView = textView;
		}
		
		public void run()
		{
			if( m_textView != null )
			{
				m_textView.append(Text);
			}
		}
	};
	
	public TextViewLog
	(
		android.os.Handler handler,
		android.widget.TextView textView,
		FrameTimer timer,
		android.content.Context context,
		String logFilename
	)
	{
		m_handler = handler;
		m_textView = textView;
		m_timer = timer;
		
		if( textView != null )
		{
			textView.append( "\n" );
		}
		
		if( context != null && logFilename != null )
		{			
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) 
			{
				File file = new File
				(
					context.getExternalFilesDir
					(
						Environment.DIRECTORY_DOCUMENTS
					), 
					logFilename
				);
				
				try
				{
					
					file.setWritable(true);
					file.createNewFile();
					
					
					m_writer = new FileWriter(file, true);
				}
				catch( IOException ex )
				{
				}
			}
		}
		
		Date date = new Date();
		
		Write("\n");
		WriteLine("-----------------");
		WriteLine("Start logging " + date.toLocaleString());
	}
	
	public void Attach
	(
		android.os.Handler handler,
		android.widget.TextView textView
	)
	{
		m_handler = handler;
		m_textView = textView;
	}
	
	public void Close()
	{
		if(m_writer != null )
		{
			try
			{
				m_writer.close();
			}
			catch(IOException ex)
			{
			}
		}
		m_writer = null;
	}
	
	public void Write( String strText )
	{
		if( m_textView != null )
		{
			m_handler.post( new TextHandler( strText, m_textView ) );
		}
		if( m_writer != null )
		{
			try
			{
				m_writer.write(strText);
				m_writer.flush();
			}
			catch( IOException ex )
			{
			}
		}
		
		Log.d( "log", strText );
	}
	
	public void WriteLine( Object sender, String strText )
	{
		String strClassName = sender.getClass().getName();
		int pos = strClassName.lastIndexOf(".");
		strClassName = strClassName.substring(pos+1);
		WriteLine( String.format("%s [%08x]:%s",strClassName,sender.hashCode(),strText));
	}
	
	public void WriteLine( String strText )
	{
		String strOutput = String.format("[%6.3f] %s\n", m_timer.GetCurrentTime(), strText);
		if( m_textView != null )
		{
			m_handler.post( new TextHandler( strOutput, m_textView ) );
		}
		if( m_writer != null )
		{
			try
			{
				m_writer.write(strOutput);
				m_writer.flush();
			}
			catch( IOException ex )
			{
			}
		}
		Log.d( "log", strOutput );
	}
}

