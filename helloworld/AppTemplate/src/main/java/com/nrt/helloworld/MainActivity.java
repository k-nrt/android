package com.nrt.helloworld;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.AttributeSet;

import android.view.MotionEvent;

//import android.graphics.*;

import android.widget.TextView;
//public class TryGLES2002Activity extends Activity

import com.nrt.basic.*;
import com.nrt.render.*;
import com.nrt.font.*;
import com.nrt.input.*;
import com.nrt.ui.*;
import com.nrt.framework.*;

import com.nrt.math.Float3;
import com.nrt.math.Float4x4;
import java.util.logging.*;
import java.util.concurrent.atomic.*;

public class MainActivity extends Activity
{
	private RenderSurfaceView m_renderView;
	private android.os.Handler m_handler = new android.os.Handler();
	
	static int m_nbOnCreated = 0;
	
	// Called when the activity is first created.
	@Override 
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//. Initialize minimum subsystem instances.		
		SubSystem.Initialize
		(
			getResources().getAssets(), 
			(TextView) findViewById(R.id.logview),
			m_handler,
			getApplicationContext(),
			new AppFrameFactory()
			{
				@Override public AppFrame Create()
				{
					return new GameMain();
				}
			}	
		);

		//. Initialize render surface view.
		m_renderView = (RenderSurfaceView)this.findViewById(R.id.glview);
		m_renderView.Initialize();		
		
		SubSystem.Log.WriteLine(this,"onCreate() =" + m_nbOnCreated);
		m_nbOnCreated++;
	}

	@Override public void onResume() 
	{
		super.onResume();
		m_renderView.onResume();
		SubSystem.Log.WriteLine(this,"onResume()");
	}

	@Override public void onPause()
	{
		super.onPause();
		m_renderView.onPause();
		SubSystem.Log.WriteLine(this,"onPause()");
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		SubSystem.Log.WriteLine(this,"onStart()");
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		SubSystem.Log.WriteLine(this,"onStop()");
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();		
		SubSystem.Log.WriteLine(this,"onDestroy()");		
		SubSystem.Exit();
	}
}



//////////////////////////////////////////////////////////////////////////
///GLSurfaceViewの拡張 

class RenderSurfaceView extends GLSurfaceView
{
	public MainSurfaceRenderer m_surfaceRenderer = null;

	public RenderSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public RenderSurfaceView(Context context) 
	{ 
		super(context);
	}

	public void Initialize()
	{
		this.setEGLContextClientVersion(2);
		m_surfaceRenderer = new MainSurfaceRenderer(SubSystem.m_threadAppFrame);
		this.setRenderer(m_surfaceRenderer);
	}

	@Override
	public boolean onTouchEvent(MotionEvent me)
	{
		DevicePointer.OnTouchEvent(me);
		return true;
	}

	@Override
	public void onResume()
	{
		SubSystem.Log.WriteLine( this,"onResume()" );
		super.onResume();
	}

	@Override
	public void onPause()
	{
		SubSystem.Log.WriteLine( this,"onPause()" );
		super.onPause();
	}

	@Override
	protected void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
		
		if( SubSystem.Log != null )
		{
			SubSystem.Log.WriteLine( this, "onDetachedFromWindow" );
		}
	}
}


//////////////////////////////////////////////////////////////////////////
///ビュー用のレンダラ―
/*
class SurfaceRenderer implements GLSurfaceView.Renderer
{
	private int SurfaceWidth = 0;
	private int SurfaceHeight = 0;

	public UiForm m_form = new UiForm();

	public UiRectButton m_buttonDebug = null;
	public boolean m_isDispError = true;

	public GameMain m_gameMain = null;

	public Object m_locker = new Object();
	//int m_nbOnSurfaceCreated = 0;
	
	public SurfaceRenderer( GameMain gameMain )
	{
		m_gameMain = gameMain;
	}
	
	///////////////////////////////////////////////////////////////////////////
	// 最初に呼ばれる
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		//SubSystem.Log.WriteLine(String.format("onSurfaceCreated() %d", m_nbOnSurfaceCreated));
		OnSurfaceCreated();
	}

	static class UpdateThread extends Thread
	{
		public SurfaceRenderer m_surfaceRenderer = null;
		
		public UpdateThread(ThreadGroup threadGroup, SurfaceRenderer surfaceRenderer)
		{
			super(threadGroup, String.format("update"));
			m_surfaceRenderer = surfaceRenderer;
		}

		@Override
		public void run()
		{
			double interval = 1.0/60.0;
			double prev = SubSystem.Timer.GetCurrentTime() - interval;
			while(true)
			{
				double time = SubSystem.Timer.GetCurrentTime();
				boolean isInterrupted = false;
				if( interval <= (time - prev) )
				{
					int width = 0;
					int height = 0;
					synchronized(m_surfaceRenderer.m_locker)
					{
						width = m_surfaceRenderer.SurfaceWidth;
						height = m_surfaceRenderer.SurfaceHeight;
					}
					
					if
					( 
						SubSystem.Render.ScanOutWidth != width ||
						SubSystem.Render.ScanOutHeight != height
					)
					{
						SubSystem.Render.ScanOutWidth = width;
						SubSystem.Render.ScanOutHeight = height;

						m_surfaceRenderer.m_gameMain.OnSurfaceChanged(width, height);
					}
					m_surfaceRenderer.OnUpdate();
					
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
					
					m_surfaceRenderer.OnRender();
					SubSystem.RenderSystem.EndBuilderFrame();
					
					prev = time;
				}
			
				if(isInterrupted() || isInterrupted)
				{
					break;
				}
				Thread.yield();
			}
			m_surfaceRenderer = null;
		}
	}
	
	ThreadGroup m_threadGroupUpdate = new ThreadGroup("update");
	UpdateThread m_updateThread;
	private synchronized void OnSurfaceCreated()
	{
		m_form = new UiForm();
		m_form.Add((m_buttonDebug = new UiRectButton(new Rect(10, 30, 50, 50))));

		DebugLog.Error.WriteLines(Shader.Error);
			
		m_updateThread = new UpdateThread(m_threadGroupUpdate,this);
		m_updateThread.start();
	}

	//public AtomicInteger m_locker = new AtomicInteger();
	
	
	///////////////////////////////////////////////////////////////////////////
	// サーフェイスのサイズ変更時とかに呼ばれる
	public void onSurfaceChanged(GL10 gl, int width, int height) 
	{
		SubSystem.Log.WriteLine("\tonSurfaceChanged()" + width + "x" + height);
		synchronized(m_locker)
		{
			SurfaceWidth = width;
			SurfaceHeight = height;
		}
	}
	
	public void OnDestroy()
	{
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
	}
	

	public int m_mem = 0;
	///////////////////////////////////////////////////////////////////////////
	// 毎フレーム呼ばれるやつ
	public void OnUpdate()
	{
		SubSystem.Timer.Update();
		final float fElapsedTime = SubSystem.Timer.SafeFrameElapsedTime;
		//SubSystem.DelayResourceQueue.Update(fElapsedTime);

		//final Render r = SubSystem.Render;

		if(SubSystem.MinimumMarker.Done)
		{
			SubSystem.FramePointer.Update(fElapsedTime);	

			m_form.Update(SubSystem.FramePointer, fElapsedTime);
			if (m_buttonDebug.IsPush())
			{
				m_isDispError = !m_isDispError;
			}

			m_gameMain.OnUpdate();
		}
	}
	
	public void OnRender()
	{
		final float fElapsedTime = SubSystem.Timer.SafeFrameElapsedTime;
		
		if(SubSystem.MinimumMarker.Done)
		{
			final RenderContext rc = SubSystem.RenderSystem.GetRenderContext(0);
			m_gameMain.OnRender(rc);
			
			
			final GfxCommandContext gfxc = rc.GetCommandContext();
			final MatrixCache mc = rc.GetMatrixCache();
			
			gfxc.SetViewport(0,0,SurfaceWidth,SurfaceHeight);

			Float4x4 matrixOrtho = Float4x4.Local();
			Matrix.orthoM(matrixOrtho.Values, 0, 0, SurfaceWidth, SurfaceHeight, 0, -1.0f, 1.0f);

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

				//gfxc.SetClearColor(0.0f, 0.0f, 0.1f, 1.f); 
				//gfxc.Clear(EClearBuffer.ColorDepthStencil);

				

				
				fr.SetSize(16.0f);
				if (m_isDispError )
				{
					fr.Begin();
					Float3 f3Position = new Float3(
						0.0f,
						SurfaceHeight - fr.m_font.m_nFontSize * DebugLog.Error.Buffers.length,
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
																	 (int) SurfaceWidth, (int) SurfaceHeight));

				fr.Draw(0.0f, fr.m_fSize * 3, 0.0f, String.format("BackBuffer:%dx%d %dx%d", 
																  (int) m_gameMain.m_frameBuffer.Width,
																  (int) m_gameMain.m_frameBuffer.Height,
																  (int) ((RenderTexture) m_gameMain.m_frameBuffer.ColorRenderTexture).PotWidth,
																  (int) ((RenderTexture) m_gameMain.m_frameBuffer.ColorRenderTexture).PotHeight));

				fr.Draw(0.0f, fr.m_fSize *4.0f,0.0f,String.format("Core %d",java.lang.Runtime.getRuntime().availableProcessors()));											  
				
				long usemem = totalMem - freeMem;
				if (m_mem <= 0)
				{
					m_mem = (int) usemem;
				}
				else
				{
					if ((usemem - m_mem) > 65536 * 16)
					{
						//java.lang.System.gc();
					}
				}

				//sfr.Draw( 0.0f, fr.m_fSize*2.0f, 0.0f, String.format( "%f %f %f", m_f3Euler.X, m_f3Euler.Y, m_f3Euler.Z ) );
				fr.End();
				
				if( m_form != null )
				{
					m_form.Render(br,bf);
				}
				
			}
			else
			{
				gfxc.SetClearColor(0.0f, 0.4f, 0.1f, 1.f); 
				gfxc.Clear(EClearBuffer.ColorDepthStencil);
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
*/

