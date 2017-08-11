package com.nrt.math;

import java.util.List;
import java.util.*;

import com.nrt.basic.DebugLog;

public class Local
{
	public static List<LocalList> m_listLocals = new ArrayList();
	
	public static void RegisterLocalList( LocalList localList )
	{
		m_listLocals.add( localList );
		
		DebugLog.Error.WriteLine( String.format( "size %d %s", m_listLocals.size(), localList.getClass().toString()) );		
	}
}
