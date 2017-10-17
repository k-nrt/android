package com.nrt.framework;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLSurfaceView;

import com.nrt.render.*;


public class MainSurfaceRenderer implements GLSurfaceView.Renderer
{
	
	public UpdateThread m_updateThread = null;

	public Object m_locker = new Object();

	public MainSurfaceRenderer( UpdateThread updateThread )
	{
		m_updateThread = updateThread;
	}

	///////////////////////////////////////////////////////////////////////////
	// 最初に呼ばれる
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		//SubSystem.Log.WriteLine(String.format("onSurfaceCreated() %d", m_nbOnSurfaceCreated));
	}
	
	public void onSurfaceChanged(GL10 gl, int width, int height) 
	{
		SubSystem.Log.WriteLine("\tonSurfaceChanged()" + width + "x" + height);
		synchronized(m_locker)
		{
			m_updateThread.SetSurfaceSize(width,height);
		}
	}

	public void OnDestroy()
	{
		/*
		m_updateThread.interrupt();

		try
		{
			m_updateThread.join();
		}
		catch(java.lang.InterruptedException ex)
		{

		}
		SubSystem.Log.WriteLine("\tOnDestroy()" );
		m_updateThread = null;
		*/
	}
	
	public void onDrawFrame(GL10 gl)
	{
		//SubSystem.Timer.Update();
		final float fElapsedTime = SubSystem.Timer.SafeFrameElapsedTime;
		if( SubSystem.DelayResourceQueue != null )
		{
			SubSystem.DelayResourceQueue.Update(fElapsedTime);
		}

		final Render r = SubSystem.Render;


		if(SubSystem.MinimumMarker.Done)
		{
			if( SubSystem.RenderSystem != null )
			{
				while(SubSystem.RenderSystem.UpdateResources()==false)
				{
					Thread.yield();
				}
				SubSystem.RenderSystem.ProcessCommands(r);
			}

		}
		else
		{
			r.SetClearColor(0.0f,0.0f,1.0f,0.0f);
			r.Clear(EClearBuffer.ColorDepthStencil);
		}
	}

	
}
