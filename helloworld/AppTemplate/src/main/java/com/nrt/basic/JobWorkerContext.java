package com.nrt.basic;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JobWorkerContext
{
	public Queue<Job> m_queueMain = new ConcurrentLinkedQueue<Job>();
	public Queue<Job> m_queueYield = new ConcurrentLinkedQueue<Job>();
	
	JobWorkerContext()
	{
	}
	
	public Queue<Job> GetMainQueue()
	{
		return m_queueMain;
	}
	
	public Queue<Job> GetYieldQueue()
	{
		return m_queueYield;
	}
}
