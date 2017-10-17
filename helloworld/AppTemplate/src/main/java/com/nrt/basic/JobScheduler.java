package com.nrt.basic;

import java.lang.ThreadGroup;

public class JobScheduler
{
	public JobWorkerContext m_workerContext;
	public JobWorker[] m_workers;
	public ThreadGroup m_threadGroup;
	
	public JobScheduler(int nbWorkers)
	{
		m_workerContext = new JobWorkerContext();
		m_workers = new JobWorker[nbWorkers];
		m_threadGroup = new ThreadGroup("Job worker");
		for(int i = 0 ; i < m_workers.length ; i++ )
		{
			String name = "JobWorker #" + i;
			m_workers[i] = new JobWorker(m_threadGroup, name, m_workerContext);
			m_workers[i].start();	
		}
	}
	
	public void Add( Job job )
	{
		m_workerContext.GetMainQueue().offer( job );
	}
}

