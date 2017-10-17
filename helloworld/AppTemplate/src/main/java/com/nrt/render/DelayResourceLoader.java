package com.nrt.render;

import java.util.*;
import java.util.concurrent.*;

//import android.app.job.*;

import com.nrt.basic.TextViewLog;
import com.nrt.basic.*;

import com.nrt.framework.SubSystem;
import com.nrt.basic.Job;

public class DelayResourceLoader
{
	static class JobItem implements com.nrt.basic.Job
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
		
		public JobStatus Run()
		{
			Job.OnLoadContent( DelayResourceQueue );
			return JobStatus.Done;
		}
	}
	
	public interface Job
	{
		public void OnLoadContent( DelayResourceQueue drq );
	}
	
	//Queue<JobItem> m_queueJobs = new ConcurrentLinkedQueue<JobItem>();
	List<JobItem> m_listJobs = new ArrayList<JobItem>();
	public TextViewLog m_log = null;		
	/*
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
					if( jobItem != null )
					{
						m_log.WriteLine( String.format("job %s start", jobItem.Name ) );
						jobItem.Job.OnLoadContent( jobItem.DelayResourceQueue );
						m_log.WriteLine( String.format("job %s end", jobItem.Name ) );
					}
					
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
	*/
	//ThreadGroup m_threadGroup = new ThreadGroup( "delay loader thread group" );
	//LoaderThread[] m_loaderThreads = null;
	JobScheduler m_jobScheduler = null;
	
	public DelayResourceLoader( JobScheduler JobScheduler, TextViewLog log )
	{
		m_log = log;
		//m_loaderThreads = new LoaderThread[nbThreads];
		m_jobScheduler = JobScheduler;
		/*
		for( int i = 0 ; i < m_loaderThreads.length ; i++ )
		{
			m_loaderThreads[i] = new LoaderThread( m_threadGroup, "loader"+i );
			m_loaderThreads[i].start();
		}
		*/
	}
	
	public void RegisterJob( String strName, DelayResourceQueue drq, Job job )
	{
		JobItem jobItem = new JobItem( strName, drq, job );
		//m_queueJobs.offer( jobItem );
		m_jobScheduler.Add( jobItem );
		m_listJobs.add( jobItem );
	}
	
	public int GetAllJobCount()
	{
		return m_listJobs.size();
	}
	
	public int GetLeftJobCount()
	{
		return m_jobScheduler.m_workerContext.GetMainQueue().size() 
		+ m_jobScheduler.m_workerContext.GetYieldQueue().size();
	}
}

