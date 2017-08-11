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


public class MainActivity extends Activity
{
	private RenderSurfaceView m_renderView;

	private android.os.Handler m_handler = new android.os.Handler();

	private static GameMain m_gameMain = new GameMain();
	
	static class LoadingThread extends Thread
	{
		public DelayResourceQueue m_delayResourceQueue = null;
		public GameMain m_gameMain = null;
		public int m_iJob=0;
		public LoadingThread(DelayResourceQueue drq, ThreadGroup threadGroup, GameMain gameMain, int iJobIndex)
		{
			super(threadGroup, String.format("thread%d", iJobIndex));
			m_delayResourceQueue = drq;
			m_gameMain = gameMain;
			m_iJob = iJobIndex;
		}

		@Override
		public void run()
		{
			String strThreadName = Thread.currentThread().getName();
			SubSystem.Log.WriteLine( String.format("start thread %s", strThreadName));

			int nbThreads = Thread.activeCount();
			SubSystem.Log.WriteLine( String.format("thread count %d", nbThreads));
			if( 0 < nbThreads )
			{

				Thread[] threads = new Thread[nbThreads];
				//Thread.enumerate( threads );
				for( Thread thread : threads )
				{
					if( thread == null )
					{
						continue;
					}
					if( strThreadName.equals( thread.getName() ) )
					{
						SubSystem.Log.WriteLine
						( 
							String.format("Detect thread duplicate %s",
										  strThreadName)
						);
						return;
					}
				}
			}

			/*
			if (m_iJob == 0)
			{
				//SubSystem.OnLoadContent(m_delayResourceQueue,0);
				//m_gameMain.OnLoadContent(m_delayResourceQueue, 0);
				//m_gameMain.OnLoadContent(m_delayResourceQueue, 1);
			}
			else
			{
				//SubSystem.OnLoadContent(m_delayResourceQueue,1);
			}
			*/

			SubSystem.Log.WriteLine(this.getName() + " thread end.");
		}
	}
	

	
	static ThreadGroup m_threadGroup0 = new ThreadGroup( "loading0" );
	static ThreadGroup m_threadGroup1 = new ThreadGroup( "loading1" );
	static LoadingThread m_loadingThread1 = null;
	static LoadingThread m_loadingThread2 = null;
	static int m_nbOnCreated = 0;
	/*
	static class Loader implements Runnable
	{
		public GameMain m_gameMain = null;
		public int m_iJobIndex = 0;
		
		public Loader( GameMain gameMain, int iJobIndex )
		{
			m_gameMain = gameMain;
			m_iJobIndex = iJobIndex;			
		}

		@Override
		public void run()
		{
			m_gameMain.OnLoadContent( SubSystem.DelayResourceQueue, m_iJobIndex );
		}		
	}
	*/
	// Called when the activity is first created.
	@Override public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		//. Initialize minimum subsystem instances.		
		SubSystem.Initialize
		(
			getResources().getAssets(), 
			(TextView) findViewById(R.id.logview),
			m_handler
		);

		//. Initialize render surface view.
		m_renderView = (RenderSurfaceView)this.findViewById(R.id.glview);
		m_renderView.Initialize(m_gameMain);
		
		if( m_nbOnCreated <= 0 )
		{
			//. Initialize minimum rendering resources.
			SubSystem.OnCreate(m_renderView.m_surfaceRenderer);
			DelayResourceQueue drq = SubSystem.DelayResourceQueue;
	
			m_gameMain.OnCreate(drq);
			
			//SubSystem.DelayResourceLoaderThreads[0].RegisterRunnable( new Loader(m_gameMain,0));
			//SubSystem.DelayResourceLoaderThreads[1].RegisterRunnable( new Loader(m_gameMain,1));

			//m_loadingThread1 = new LoadingThread(drq,m_threadGroup0, m_gameMain, 0);
			//m_loadingThread2 = new LoadingThread(drq,m_threadGroup1, m_gameMain, 1);

			//SubSystem.DelayResourceQueue.RegisterThread(m_loadingThread1);
			//SubSystem.DelayResourceQueue.RegisterThread(m_loadingThread2);

			//m_loadingThread1.start();
			//m_loadingThread2.start();
			
			//SubSystem.DelayResourceQueue.RegisterThread( SubSystem.DelayResourceLoader.
		}
		else
		{
			SubSystem.DelayResourceQueue.ReloadResources();
		}
		
		SubSystem.Log.WriteLine("MainActivity.onCreate() " + m_nbOnCreated);
		m_nbOnCreated++;
	}

	@Override public void onResume() 
	{
		super.onResume();
		m_renderView.onResume();
		SubSystem.Log.WriteLine("MainActivity.onResume()");
	}

	@Override public void onPause()
	{
		super.onPause();
		m_renderView.onPause();
		SubSystem.Log.WriteLine("MainActivity.onPause()");
	}

	@Override
	protected void onStart()
	{
		// TODO: Implement this method
		super.onStart();
		SubSystem.Log.WriteLine("MainActivity.onStart()");

	}

	@Override
	protected void onStop()
	{
		// TODO: Implement this method
		super.onStop();
		SubSystem.Log.WriteLine("MainActivity.onStart()");

	}

	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		super.onDestroy();
		m_renderView.onDestroy();
		SubSystem.Log.WriteLine("MainActivity.onDestroy()");
	}
}



//////////////////////////////////////////////////////////////////////////
///GLSurfaceViewの拡張 

class RenderSurfaceView extends GLSurfaceView
{
	public SurfaceRenderer m_surfaceRenderer = null;

	public RenderSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		//Initialize();
	}

	public RenderSurfaceView(Context context) 
	{ 
		super(context);
		//Initialize();
	}

	public void Initialize(GameMain gameMain )
	{
		this.setEGLContextClientVersion(2);
		m_surfaceRenderer = new SurfaceRenderer( gameMain );
		this.setRenderer(m_surfaceRenderer);
	}

	public boolean onTouchEvent(MotionEvent me)
	{
		
		DevicePointer.OnTouchEvent(me);
		return true;
	}

	@Override
	public void onResume()
	{
		// TODO: Implement this method
		//SubSystem.Log.Write( "onResume()" );
		super.onResume();
	}

	@Override
	public void onPause()
	{
		// TODO: Implement this method
		//SubSystem.Log.Write( "onPause()" );
		super.onPause();
	}

	@Override
	protected void onDetachedFromWindow()
	{
		// TODO: Implement this method
		super.onDetachedFromWindow();
		
		if( SubSystem.Log != null )
		{
			SubSystem.Log.WriteLine( "onDetachedFromWindow" );
		}
	}

	
	
	public void onDestroy()
	{
		//super.onDestroy();
	}
}


//////////////////////////////////////////////////////////////////////////
///ビュー用のレンダラ―
class  SurfaceRenderer extends Render implements GLSurfaceView.Renderer
{
	private int SurfaceWidth = 0;
	private int SurfaceHeight = 0;

	public UiForm m_form = new UiForm();

	public UiRectButton m_buttonDebug = null;
	public boolean m_isDispError = true;

	public GameMain m_gameMain = null;//new GameMain();

	/*
	class LoadingThread extends Thread
	{
		public DelayResourceQueue m_delayResourceQueue = null;
		public GameMain m_gameMain = null;
		public int m_iJob=0;
		public LoadingThread(DelayResourceQueue drq, ThreadGroup threadGroup, GameMain gameMain, int iJobIndex)
		{
			super(threadGroup, String.format("thread%d", iJobIndex));
			m_delayResourceQueue = drq;
			m_gameMain = gameMain;
			m_iJob = iJobIndex;
		}

		@Override
		public void run()
		{
			String strThreadName = Thread.currentThread().getName();
			SubSystem.Log.WriteLine( String.format("start thread %s", strThreadName));
			
			int nbThreads = Thread.activeCount();
			SubSystem.Log.WriteLine( String.format("thread count %d", nbThreads));
			if( 0 < nbThreads )
			{
				
				Thread[] threads = new Thread[nbThreads];
				//Thread.enumerate( threads );
				for( Thread thread : threads )
				{
					if( thread == null )
					{
						continue;
					}
					if( strThreadName.equals( thread.getName() ) )
					{
						SubSystem.Log.WriteLine
						( 
						String.format("Detect thread duplicate %s",
							strThreadName)
						);
						return;
					}
				}
			}
			
			if (m_iJob == 0)
			{
				SubSystem.OnLoadContent(m_delayResourceQueue,0);
				m_gameMain.OnLoadContent(m_delayResourceQueue, 0);
				m_gameMain.OnLoadContent(m_delayResourceQueue, 1);
			}
			else
			{
				SubSystem.OnLoadContent(m_delayResourceQueue,1);
			}
			
			SubSystem.Log.WriteLine(this.getName() + " thread end.");
		}
	}

	static ThreadGroup m_threadGroup0 = new ThreadGroup( "loading0" );
	static ThreadGroup m_threadGroup1 = new ThreadGroup( "loading1" );
	LoadingThread m_loadingThread1 = null;
	LoadingThread m_loadingThread2 = null;
	*/
	int m_nbOnSurfaceCreated = 0;
	
	public SurfaceRenderer( GameMain gameMain )
	{
		m_gameMain = gameMain;
	}
	
	///////////////////////////////////////////////////////////////////////////
	// 最初に呼ばれる
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		SubSystem.Log.WriteLine(String.format("onSurfaceCreated() %d", m_nbOnSurfaceCreated));
		OnSurfaceCreated();
	}

	private synchronized void OnSurfaceCreated()
	{
		if (m_nbOnSurfaceCreated <= 0)
		{			
			/*
			SubSystem.Create(this);
			DelayResourceQueue drq = SubSystem.DelayResourceQueue;
				
			m_gameMain.OnCreate(drq);
			

			m_loadingThread1 = new LoadingThread(drq,m_threadGroup0, m_gameMain, 0);
			m_loadingThread2 = new LoadingThread(drq,m_threadGroup1, m_gameMain, 1);

			SubSystem.DelayResourceQueue.RegisterThread(m_loadingThread1);
			SubSystem.DelayResourceQueue.RegisterThread(m_loadingThread2);

			m_loadingThread1.start();
			m_loadingThread2.start();
			*/
			m_form = new UiForm();
			m_form.Add((m_buttonDebug = new UiRectButton(new Rect(10, 30, 50, 50))));

			DebugLog.Error.WriteLines(Shader.Error);
		}
		else
		{
			m_gameMain.m_fStartTime = SubSystem.Timer.FrameTime;
			SubSystem. DelayResourceQueue.ReloadResources();
		}
		m_nbOnSurfaceCreated++;
	}

	///////////////////////////////////////////////////////////////////////////
	// サーフェイスのサイズ変更時とかに呼ばれる
	public void onSurfaceChanged(GL10 gl, int width, int height) 
	{
		SubSystem.Log.WriteLine("onSurfaceChanged()");
		SurfaceWidth = width;
		SurfaceHeight = height;
		SubSystem.Render.ScanOutWidth = width;
		SubSystem.Render.ScanOutHeight = height;

		m_gameMain.OnSurfaceChanged(width, height);
	}

	public int m_mem = 0;
	///////////////////////////////////////////////////////////////////////////
	// 毎フレーム呼ばれるやつ
	public void onDrawFrame(GL10 gl)
	{
		SubSystem.Timer.Update();
		final float fElapsedTime = SubSystem.Timer.SafeFrameElapsedTime;
		SubSystem.DelayResourceQueue.Update(fElapsedTime);

		final Render r = SubSystem.Render;

		if(SubSystem.MinimumMarker.Done)
		{
			SubSystem.FramePointer.Update(fElapsedTime);	

			
			m_form.Update(SubSystem.FramePointer, fElapsedTime);
			if (m_buttonDebug.IsPush())
			{
				m_isDispError = !m_isDispError;
			}

			m_gameMain.OnUpdate();

			SubSystem.RenderSystem.BeginBuilerFrame();

			final RenderContext rc = SubSystem.RenderSystem.GetRenderContext(0);
			m_gameMain.OnRender(rc);

			if (SubSystem.DebugFontReadyMarker.Done)
			{
				final FontRender fr = rc.GetFontRender();
				final BasicRender br = rc.GetBasicRender();
				final MatrixCache mc = rc.GetMatrixCache();
				final GfxCommandContext gfxc = rc.GetCommandContext();
				final BitmapFont bf = rc.GetBitmapFont();

				//GLES20.glClearColor(0.0f, 0.0f, 0.1f, 1.f); 
				//GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


				Float4x4 matrixOrtho = Float4x4.Local();
				Matrix.orthoM(matrixOrtho.Values, 0, 0, SurfaceWidth, SurfaceHeight, 0, -1.0f, 1.0f);

				mc.SetView(Float4x4.Identity(Float4x4.Local()));
				mc.SetProjection(matrixOrtho);
				mc.SetWorld(Float4x4.Identity(Float4x4.Local()));
				//mc.Update();

				//GLES20.glDisable(GLES20.GL_CULL_FACE);

				final RasterizerState disableCullFace = new RasterizerState(false, ECullFace.Back,EFrontFace.CCW);

				gfxc.SetRasterizerState(disableCullFace);


				if (m_isDispError && fr.m_font.IsReady)
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
						f3Position.Y += fr.m_font.m_nFontSize;
					}
					fr.End();

				}

				fr.Begin();
				fr.Draw(0.0f, 0.0f, 0.0f, String.format("FPS:%3.3f", 1.0f / fElapsedTime));
				long freeMem = Runtime.getRuntime().freeMemory();
				long totalMem = Runtime.getRuntime().totalMemory();
				fr.Draw(0.0f, fr.m_fSize, 0.0f, String.format("Mem:%d/%d", (int) (totalMem - freeMem), (int) totalMem));
				fr.Draw(0.0f, fr.m_fSize * 2.0f, 0.0f, String.format("ScanOut:%dx%d", 
																	 (int) ScanOutWidth, (int) ScanOutHeight));

				fr.Draw(0.0f, fr.m_fSize * 3, 0.0f, String.format("BackBuffer:%dx%d %dx%d", 
																  (int) m_gameMain.m_frameBuffer.Width,
																  (int) m_gameMain.m_frameBuffer.Height,
																  (int) ((RenderTexture) m_gameMain.m_frameBuffer.ColorRenderTexture).PotWidth,
																  (int) ((RenderTexture) m_gameMain.m_frameBuffer.ColorRenderTexture).PotHeight));

				Float3 f3 = m_gameMain.m_player.m_trackBall.EulerAngularVelocity;
				/*
				 m_f3Euler.X = (Float.isNaN( m_f3Euler.X ) ? m_f3Euler.X : f3.X );
				 m_f3Euler.Y = (Float.isNaN( m_f3Euler.Y ) ? m_f3Euler.Y : f3.Y );
				 m_f3Euler.Z = (Float.isNaN( m_f3Euler.Z ) ? m_f3Euler.Z : f3.Z );
				 */
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

				m_form.Render(br,bf);

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

			SubSystem.RenderSystem.EndBuilderFrame();

			SubSystem.RenderSystem.UpdateResources();
			SubSystem.RenderSystem.ProcessCommands(r);
		}
		else
		{
			r.SetClearColor(0.0f,0.0f,1.0f,0.0f);
			r.Clear(EClearBuffer.ColorDepthStencil);
		}

	}
}


