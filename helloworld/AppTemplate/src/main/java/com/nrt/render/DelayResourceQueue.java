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
	public ConcurrentLinkedQueue<RenderResource> m_queueResources = new ConcurrentLinkedQueue<RenderResource>();
	public int m_nbResources = 1;
	
	public int m_nbMaxResources = 0;

	public int m_nbAppliedResources = 0;

	public List<RenderResource> m_listResources = new ArrayList<RenderResource>();
	public List<DelayResourceQueueMarker> m_listMarkers = new ArrayList<DelayResourceQueueMarker>();

	private static class TextTag extends RenderResource
	{
		private String Text = null;
		
		public TextTag( String text )
		{
			Text = text;
		}

		@Override public void Generate()
		{
			com.nrt.basic.DebugLog.Error.WriteLine( Text );
		}

		@Override public void Delete()
		{}
	}

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

	public synchronized void Add( RenderResource resource )
	{
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
	}
	
	public synchronized void Remove( RenderResource resource )
	{
		if(m_queueResources.contains(resource))
		{
			m_queueResources.remove(resource);
		}

		if(m_listResources.contains(resource))
		{
			m_listResources.remove(resource);
		}
	}

	public synchronized void Add( String strText )
	{
		/*
		if( java.lang.Thread.currentThread().isInterrupted() )
		{
			throw( new ThreadForceDestroyException() );
		}
		*/
	}

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
		
		SubSystem.Log.WriteLine( this, String.format( "Reload resources %d", m_listResources.size() ));
	}

	public boolean Update( float fElapsedTime )
	{
		for( int i = 0 ; i < m_nbResources ; i++ )
		{
			if( ApplyResource() == false )
			{
				return false;
			}
		}
		
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

		return true;
	}
	
	private boolean ApplyResource()
	{		
		if( m_queueResources.isEmpty() )
		{
			return false;
		}
		
		RenderResource resource = m_queueResources.poll();
		
		if( resource != null )
		{
			resource.Generate();
			m_nbAppliedResources++;
			//SubSystem.Log.WriteLine( resource.toString() + " Applied" );
		}

		return true;
	}
}

