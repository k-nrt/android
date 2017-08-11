package com.nrt.render;
import java.util.*;

import android.webkit.*;
import java.util.concurrent.*;

//import nrt.framework.SubSystem;
import com.nrt.basic.*;

public class DelayResourceLoaderThread extends Thread
{
	public DelayResourceQueue m_delayResourceQueue = null;	
	public Queue<Runnable> m_queueRunnables = new ConcurrentLinkedQueue<Runnable>();
	
	public TextViewLog Log = null;
	
	public DelayResourceLoaderThread(DelayResourceQueue drq, TextViewLog log, ThreadGroup threadGroup, int iJobIndex)//, GameMain gameMain, int iJobIndex)
	{
		super(threadGroup, String.format("thread%d", iJobIndex));
		m_delayResourceQueue = drq;
		Log = log;
	}
	
	public void RegisterRunnable( Runnable runnable )
	{
		m_queueRunnables.offer( runnable );
	}

	@Override
	public void run()
	{
		String strThreadName = Thread.currentThread().getName();
		Log.WriteLine( String.format("start thread %s", strThreadName));

		int nbThreads = Thread.activeCount();
		Log.WriteLine( String.format("thread count %d", nbThreads));
		if( 0 < nbThreads )
		{
			Thread[] threads = new Thread[nbThreads];
			for( Thread thread : threads )
			{
				if( thread == null )
				{
					continue;
				}
				if( strThreadName.equals( thread.getName() ) )
				{
					Log.WriteLine(String.format
					(
						"Detect thread duplicate %s",
						strThreadName
					));
					return;
				}
			}
		}

		while( 0 < m_queueRunnables.size() )
		{
			Runnable runnable = m_queueRunnables.poll();
			runnable.run();
		}
		
		Log.WriteLine(this.getName() + " thread end.");
	}
}
