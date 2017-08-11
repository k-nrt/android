package com.nrt.render;

import java.util.*;
import java.util.concurrent.*;

//import android.app.job.*;

import com.nrt.basic.TextViewLog;
import com.nrt.basic.*;

import com.nrt.framework.SubSystem;

public class DelayResourceLoader
{
	static class JobItem
	{
		public String Name = null;
		public DelayResourceQueue DelayResourceQueue = null;
		public Job Job = null;
		
		public JobItem( String name, DelayResourceQueue drq, Job job )
		{
			Name = name;
			DelayResourceQueue = drq;
			Job = job;
		}
	}
	
	public interface Job
	{
		public void OnLoadContent( DelayResourceQueue drq );
	}
	
	Queue<JobItem> m_queueJobs = new ConcurrentLinkedQueue<JobItem>();
	List<JobItem> m_listJobs = new ArrayList<JobItem>();
	public TextViewLog m_log = null;		
	
	class LoaderThread extends Thread
	{		
		public LoaderThread( ThreadGroup threadGroup, String strName )
		{
			super( threadGroup, strName );
		}
		
		@Override
		public void run()
		{			
			for(;;)
			{		
				while( 0 < m_queueJobs.size() )
				{
					JobItem jobItem = m_queueJobs.poll();
					m_log.WriteLine( String.format("job %s start", jobItem.Name ) );
					jobItem.Job.OnLoadContent( jobItem.DelayResourceQueue );
					m_log.WriteLine( String.format("job %s end", jobItem.Name ) );
					
					Thread.yield();
				}
				
				if( isInterrupted() )
				{
					break;
				}
			}

			m_log.WriteLine(this.getName() + " thread end.");
			super.run();
		}		
	}
	
	ThreadGroup m_threadGroup = new ThreadGroup( "delay loader thread group" );
	LoaderThread[] m_loaderThreads = null;
	
	public DelayResourceLoader( int nbThreads, TextViewLog log )
	{
		m_log = log;
		m_loaderThreads = new LoaderThread[nbThreads];
		for( int i = 0 ; i < m_loaderThreads.length ; i++ )
		{
			m_loaderThreads[i] = new LoaderThread( m_threadGroup, "loader"+i );
			m_loaderThreads[i].start();
		}
	}
	
	public void RegisterJob( String strName, DelayResourceQueue drq, Job job )
	{
		JobItem jobItem = new JobItem( strName, drq, job );
		m_queueJobs.offer( jobItem );
		m_listJobs.add( jobItem );
	}
	
	public int GetAllJobCount()
	{
		return m_listJobs.size();
	}
	
	public int GetLeftJobCount()
	{
		return m_queueJobs.size();
	}
}
