package com.nrt.basic;

import android.util.Log;
import android.os.Environment;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TextViewLog
{
	public android.os.Handler m_handler = null;
	public android.widget.TextView m_textView = null;
	public FileWriter m_writer = null;
	
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
		android.content.Context context,
		String logFilename
	)
	{
		m_handler = handler;
		m_textView = textView;
		
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
					
					
					m_writer = new FileWriter(file, false);
				}
				catch( IOException ex )
				{
				}
			}
		}
	}
	
	public void Write( String strText )
	{
		if( m_textView != null )
		{
			m_handler.post( new TextHandler( strText, m_textView ) );
		}
		Log.d( "log", strText );
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
	}
	
	public void WriteLine( String strText )
	{
		if( m_textView != null )
		{
			m_handler.post( new TextHandler( strText + "\n", m_textView ) );
		}
		Log.d( "log", strText );
		if( m_writer != null )
		{
			try
			{
				m_writer.write(strText+"\n");
				m_writer.flush();
			}
			catch( IOException ex )
			{
			}
		}
	}
}

