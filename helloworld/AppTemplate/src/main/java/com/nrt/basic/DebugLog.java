package com.nrt.basic;

import java.util.*;

public class DebugLog
{
	public static DebugLog Error = new DebugLog( 55 );

	public String[] Buffers = null;
	public int Position = 0;
	public int RenderPosition = 0;
	
	public DebugLog(int nBufferSize )
	{
		Buffers = new String[nBufferSize + 1];
		for (int i = 0 ; i < Buffers.length ; i++)
		{
			Buffers[i] = "";
		}		

		RenderPosition = -Buffers.length + 1;
	}

	public synchronized void Write(String strTarget)
	{
		Buffers[Position] += strTarget;
	}

	public synchronized void WriteLine(String strLog)
	{
		Buffers[Position] += strLog;
		NextLine();
	}

	public synchronized void WriteLines(List<String> listLogs)
	{
		for (int i = 0 ; i < listLogs.size() ; i++)
		{
			WriteLine(listLogs.get(i));
		}
	}
	
	public void NextLine()
	{
		Position++;
		if (Position >= Buffers.length)
		{
			Position = 0;
		}
		Buffers[Position] = "";

		RenderPosition++;
		if (RenderPosition >= Buffers.length)
		{
			RenderPosition = 0;
		}
	}
}

