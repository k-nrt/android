package com.nrt.framework;

import java.lang.Thread;

import com.nrt.ui.UiForm;
import com.nrt.ui.UiRectButton;
import com.nrt.basic.Rect;
import com.nrt.render.*;

import com.nrt.math.Float3;
import com.nrt.math.Float4x4;
import android.opengl.Matrix;

import com.nrt.font.Font;
import com.nrt.font.FontRender;
import com.nrt.font.BitmapFont;

import com.nrt.basic.DebugLog;
import com.nrt.input.FramePointer;

public class UpdateThread extends Thread
{
	public AppFrame m_appFrame = null;

	public Object m_locker = new Object();
	
	public int m_surfaceWidth = 0;
	public int m_surfaceHeight = 0;
	
	public double m_updateIntervalInSec = 1.0/60.0;
	
	public UiForm m_form = new UiForm();
	public UiRectButton m_buttonDebug = null;

	public boolean m_isDispError = true;	
	public long m_memory = 0;

	public UpdateThread(ThreadGroup threadGroup, AppFrame appFrame)
	{
		super(threadGroup, String.format("update"));

		m_buttonDebug = new UiRectButton(new Rect(10, 30, 50, 50));
		
		m_form = new UiForm();
		m_form.Add(m_buttonDebug);

		m_appFrame = appFrame;
	}
	
	public void InterruptAndJoin()
	{
		interrupt();

		try
		{
			join();
		}
		catch(java.lang.InterruptedException ex)
		{

		}
	}
	
	public void SetSurfaceSize( int width, int height )
	{
		synchronized(m_locker)
		{
			m_surfaceWidth = width;
			m_surfaceHeight = height;
		}
	}
	
	public void SetUpdateInterval( double intervalInSec )
	{
		synchronized(m_locker)
		{
			m_updateIntervalInSec = intervalInSec;
		}
	}

	@Override
	public void run()
	{
		double interval = m_updateIntervalInSec;
		double prev = SubSystem.Timer.GetCurrentTime() - interval;
		while(true)
		{
			synchronized(m_locker)
			{
				interval = m_updateIntervalInSec;
			}
				
			double time = SubSystem.Timer.GetCurrentTime();
			boolean isInterrupted = false;
			if( interval <= (time - prev) )
			{
				int width = 0;
				int height = 0;
				synchronized(m_locker)
				{              
					width = m_surfaceWidth;
					height = m_surfaceHeight;
				}
				
				if(width == 0 || height == 0 )
				{
					continue;
				}

				if
				( 
					SubSystem.Render.ScanOutWidth != width ||
					SubSystem.Render.ScanOutHeight != height
				)
				{
					SubSystem.Render.ScanOutWidth = width;
					SubSystem.Render.ScanOutHeight = height;
					m_appFrame.OnSurfaceChanged(width, height);
				}
				
				OnUpdate();

				while(SubSystem.RenderSystem.BeginBuilderFrame()==false)
				{                                                          
					if(isInterrupted())
					{    
						isInterrupted = true;
						break;
					}
					Thread.yield();
				}

				if(isInterrupted)
				{    
					break;
				}

				OnRender();
				SubSystem.RenderSystem.EndBuilderFrame();

				prev = time;
			}

			if(isInterrupted() || isInterrupted)
			{    
				break;
			}
			Thread.yield();
		}
		m_appFrame = null;
	}
	
	private void OnUpdate()
	{
		SubSystem.Timer.Update();
	 	final float fElapsedTime = SubSystem.Timer.SafeFrameElapsedTime;
		
		SubSystem.FramePointer.Update(fElapsedTime);	

		m_form.Update(SubSystem.FramePointer, fElapsedTime);
		if (m_buttonDebug.IsPush())
		{
			m_isDispError = !m_isDispError;
		}

		if(SubSystem.MinimumMarker.Done)
		{
			m_appFrame.OnUpdate();
		}
	}
	
	public void OnRender()
	{
		final float fElapsedTime = SubSystem.Timer.SafeFrameElapsedTime;
		final RenderContext rc = SubSystem.RenderSystem.GetRenderContext(0);
		final GfxCommandContext gfxc = rc.GetCommandContext();
		
		if(SubSystem.MinimumMarker.Done)
		{
			m_appFrame.OnRender(rc);

			final MatrixCache mc = rc.GetMatrixCache();
			gfxc.SetViewport(0,0,m_surfaceWidth,m_surfaceHeight);

			Float4x4 matrixOrtho = Float4x4.Local();
			Matrix.orthoM(matrixOrtho.Values, 0, 0, m_surfaceWidth, m_surfaceHeight, 0, -1.0f, 1.0f);

			mc.SetView(Float4x4.Identity(Float4x4.Local()));
			mc.SetProjection(matrixOrtho);
			mc.SetWorld(Float4x4.Identity(Float4x4.Local()));

			final RasterizerState disableCullFace = new RasterizerState(false, ECullFace.Back,EFrontFace.CCW);

			gfxc.SetRasterizerState(disableCullFace);

			final BasicRender br = rc.GetBasicRender();

			if (SubSystem.DebugFont.IsReady())
			{
				final FontRender fr = rc.GetFontRender();
				final BitmapFont bf = rc.GetBitmapFont();
				
				fr.SetSize(16.0f);
				if (m_isDispError )
				{
					fr.Begin();
					Float3 f3Position = new Float3(
						0.0f,
						m_surfaceHeight - fr.m_font.m_nFontSize * DebugLog.Error.Buffers.length,
						0.0f);
					for (int i = 0 ; i < DebugLog.Error.Buffers.length - 1 ; i++)
					{
						int rp = (DebugLog.Error.RenderPosition < 0 ? 0 : DebugLog.Error.RenderPosition);
						int ii = (rp + i) % DebugLog.Error.Buffers.length;

						fr.Draw(f3Position, DebugLog.Error.Buffers[ii]);
						f3Position.Y += fr.m_fSize;
					}
					fr.End();
				}

				fr.Begin();
				fr.Draw(0.0f, 0.0f, 0.0f, String.format("こんにちわ世界 FPS:%3.3f", 1.0f / fElapsedTime));
				long freeMem = Runtime.getRuntime().freeMemory();
				long totalMem = Runtime.getRuntime().totalMemory();
				fr.Draw(0.0f, fr.m_fSize, 0.0f, String.format("Mem:%d/%d", (int) (totalMem - freeMem), (int) totalMem));
				fr.Draw(0.0f, fr.m_fSize * 2.0f, 0.0f, String.format("ScanOut:%dx%d", 
																	 m_surfaceWidth, m_surfaceHeight));

				/*
				fr.Draw(0.0f, fr.m_fSize * 3, 0.0f, String.format("BackBuffer:%dx%d %dx%d", 
																  (int) m_gameMain.m_frameBuffer.Width,
																  (int) m_gameMain.m_frameBuffer.Height,
																  (int) ((RenderTexture) m_gameMain.m_frameBuffer.ColorRenderTexture).PotWidth,
																  (int) ((RenderTexture) m_gameMain.m_frameBuffer.ColorRenderTexture).PotHeight));
				*/
				fr.Draw(0.0f, fr.m_fSize *4.0f,0.0f,String.format("Core %d",java.lang.Runtime.getRuntime().availableProcessors()));											  

				long usemem = totalMem - freeMem;
				if (m_memory <= 0)
				{
					m_memory = (int) usemem;
				}
				else
				{
					if ((usemem - m_memory) > 65536 * 16)
					{
						//java.lang.System.gc();
					}
				}

				fr.End();

				if( m_form != null )
				{
					m_form.Render(br,bf);
				}
			}

			for (FramePointer.Pointer pointer : SubSystem.FramePointer.Pointers)
			{
				if (pointer.Push)
				{
					br.SetColor(1.0f, 0.0f, 0.0f, 1.0f);
				}
				else if (pointer.Release)
				{
					br.SetColor(0.0f, 0.0f, 1.0f, 1.0f);
				}
				else if (pointer.Down)
				{
					br.SetColor(0.0f, 1.0f, 0.0f, 1.0f);
				}
				else if (pointer.Up)
				{
					continue;
				}

				br.Arc(pointer.Position.X, pointer.Position.Y, 64.0f, 16);
			}
		}
		else
		{
			gfxc.SetClearColor(0.0f, 0.4f, 0.1f, 1.f);
			gfxc.Clear(EClearBuffer.ColorDepthStencil);
		}
	}
}

