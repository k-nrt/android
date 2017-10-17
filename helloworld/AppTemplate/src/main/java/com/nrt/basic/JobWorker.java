package com.nrt.basic;
import java.lang.Thread;
import java.util.Queue;
public class JobWorker extends Thread
{		
	public JobWorkerContext m_workerContext = null;
	public JobWorker( ThreadGroup threadGroup, String strName, JobWorkerContext workerContext )
	{
		super( threadGroup, strName );
		m_workerContext = workerContext;
	}

	@Override
	public void run()
	{			
		Queue<Job>[] queues = new Queue[]
		{
			m_workerContext.GetYieldQueue(),
			m_workerContext.GetMainQueue()
		};
		
		for(;;)
		{		
			for(int i = 0 ; i < queues.length ; i++ )
			{
				Queue<Job> queue = queues[i];
				if( 0 < queue.size() )
				{
					Job job = queue.poll();
					if( job != null )
					{
						JobStatus status = job.Run();
						if( status == JobStatus.Yield )
						{
							m_workerContext.GetYieldQueue().offer( job );
						}
					}
				}
				
				Thread.yield();
			}

			if( isInterrupted() )
			{
				break;
			}
		}
	}		
}
