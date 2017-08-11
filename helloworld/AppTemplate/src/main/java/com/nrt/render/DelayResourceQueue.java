package com.nrt.render;
import java.util.concurrent.*;
import android.content.res.*;
import android.content.pm.*;
import android.app.*;

import java.util.List;
import java.util.ArrayList;
import com.nrt.framework.*;

public class DelayResourceQueue
{
	//public List<Thread> m_listThreads = new ArrayList<Thread>();
	public ConcurrentLinkedQueue<RenderResource> m_queueResources = new ConcurrentLinkedQueue<RenderResource>();
	public int m_nbResources = 1;
	
	public int m_nbMaxResources = 0;
	//public int m_nbMaxThreads = 0;
	
	public int m_nbAppliedResources = 0;

	public List<RenderResource> m_listResources = new ArrayList<RenderResource>();
	public List<DelayResourceQueueMarker> m_listMarkers = new ArrayList<DelayResourceQueueMarker>();
	//boolean m_isAllMarkerDone = false;
	
	private static class TextTag extends RenderResource
	{
		private String Text = null;
		
		public TextTag( String text )
		{
			Text = text;
		}

		@Override
		public void Apply()
		{
			com.nrt.basic.DebugLog.Error.WriteLine( Text );
		}
	}
	/*
	public int GetThreadCount()
	{
		return m_listThreads.size();
	}
	*/
	/*
	public int GetRunningThreadCount()
	{
		return m_listThreads.size();
	}
	
	public synchronized void RegisterThread( Thread thread )
	{
		m_listThreads.add( thread );
		m_nbMaxThreads++;
	}
	*/
	/*
	private synchronized void RemoveTerminatedThreads()
	{
		for( int i = 0 ; i < m_listThreads.size() ; i++ )
		{
			if( m_listThreads.get(i).getState() == Thread.State.TERMINATED )
			{
				m_listThreads.remove(i);
				i--;
			}
		}
	}
	*/
	
	/*
	public synchronized boolean IsThreadTerminated()
	{
		for( Thread thread : m_listThreads )
		{
			if( thread.getState() != Thread.State.TERMINATED )
			{
				return false;
			}
		}

		return true;
	}
	*/
	/*
	public synchronized boolean IsQueued()
	{
		if( m_queueResources.isEmpty() && IsThreadTerminated() )
		{
			return false;
		}
		
		return true;
	}
	*/
	
	public synchronized boolean IsAllMarkerDone()
	{
		for( DelayResourceQueueMarker marker : m_listMarkers )
		{
			if( marker.Done == false )
			{
				return false;
			}
		}
		
		return true;
	}

	public synchronized void Add( RenderResource resource ) //throws ThreadForceDestroyException
	{
		//if( IsRegisteredThread() )
		{
			m_queueResources.offer( resource );
			m_nbMaxResources++;
			
			m_listResources.add( resource );
			
			//. 
			if( resource instanceof DelayResourceQueueMarker )
			{
				m_listMarkers.add( (DelayResourceQueueMarker) resource );
			}
		}
		/*
		if( java.lang.Thread.currentThread().isInterrupted() )
		{
			throw( new ThreadForceDestroyException() );
		}
		*/
	}
	
//	public synchronized void TestInterrupted() //throws ThreadForceDestroyException
//	{
//		/*
//		if( java.lang.Thread.currentThread().isInterrupted() )
//		{
//			throw( new ThreadForceDestroyException() );
//		}
//		*/
//	}
	
	public synchronized void Add( String strText ) //throws ThreadForceDestroyException
	{
		/*
		if( java.lang.Thread.currentThread().isInterrupted() )
		{
			throw( new ThreadForceDestroyException() );
		}
		*/
	}
	
	/*
	public synchronized void TerminateAllThreads()
	{
		for( Thread thread : m_listThreads )
		{
			thread.interrupt();
		}
		
		m_listThreads.clear();
		m_nbMaxThreads = 0;
		m_queueResources.clear();
		m_nbResources = 0;
		m_nbMaxResources = 0;
	}
	*/
	
	public synchronized void ReloadResources()
	{
		for( DelayResourceQueueMarker marker : m_listMarkers )
		{
			marker.Done = false;
		}
		
		for( RenderResource resource : m_listResources )
		{
			resource.Name = 0;
		}
		m_nbAppliedResources = 0;
		m_queueResources.clear();
		m_queueResources.addAll( m_listResources );
		
		SubSystem.Log.WriteLine( String.format( "reload resources %d", m_listResources.size() ));
	}
	
	/*
	private synchronized boolean IsRegisteredThread()
	{
		Thread threadCurrent = Thread.currentThread();
		
		for( Thread thread : m_listThreads )
		{
			if( threadCurrent == thread )
			{
				return true;
			}
		}
		
		return false;
	}
	*/
	
	public boolean Update( float fElapsedTime )
	{
		for( int i = 0 ; i < m_nbResources ; i++ )
		{
			if( ApplyResource() == false )
			{
				return false;
			}
		}
		
		//if( fElapsedTime < 1.0f/59.0f )
		if( fElapsedTime < 1.0f/30.0f )
		{
			m_nbResources++;
		}
		else
		{
			m_nbResources /= (int)(fElapsedTime/(1.0f/30.0f));			
		}
		
		if( m_nbResources < 1 )
		{
			m_nbResources = 1;
		}
		
		//RemoveTerminatedThreads();		
		
		return true;
	}
	
	private boolean ApplyResource()
	{		
		//if( IsQueued() == false )
		if( m_queueResources.isEmpty() )
		{
			return false;
		}
		
		RenderResource resource = m_queueResources.poll();
		
		if( resource != null )
		{
			resource.Apply();
			m_nbAppliedResources++;
			//SubSystem.Log.WriteLine( resource.toString() + " Applied" );
			/*
			try
			{
				Thread.sleep(1,0);
			}
			catch( Exception ex )
			{
				
			}
			*/
		}

		return true;
	}
}

