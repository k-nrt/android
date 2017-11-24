package com.nrt.framework;


import android.content.res.*;

import android.widget.TextView;
import android.os.Handler;
import android.content.Context;

import com.nrt.basic.*;
import com.nrt.render.*;
import com.nrt.model.*;
import com.nrt.font.*;
import com.nrt.input.*;

import java.lang.ThreadGroup;

public class SubSystem
{
	//. アプリで唯一じゃないとじゃないとマズいもの.
	public static FrameTimer Timer = new FrameTimer();
	public static int Initialized = 0;
	public static int Exited = 0;
	
	public static DelayResourceQueue DelayResourceQueue = null;
	public static AppFrame m_appFrame = null;

	//. アクティビティから貰ってくるもの.
	public static Loader Loader = null;
	public static TextViewLog Log = null;
	
	public static ThreadGroup m_threadGroupAppFrame = null;
	public static UpdateThread m_threadAppFrame = null;

	public static JobScheduler JobScheduler = null;
	public static Render Render = null;
	public static RenderSystem RenderSystem = null;

	public static ModelRender ModelRender = null;

	public static FramePointer FramePointer = null;

	public static Rand Rand = new Rand();

	public static Debug Debug = null;

	public static DelayResourceLoader DelayResourceLoader = null;
	public static DelayResourceQueueMarker MinimumMarker = new DelayResourceQueueMarker("MinimumSubSystem" );
	public static DelayResourceQueueMarker SubSystemReadyMarker = new DelayResourceQueueMarker("SubSystemReady");
	public static Font DebugFont = null;
	
	public static void Initialize( AssetManager assetManager, TextView textView, Handler handler, Context context, AppFrameFactory appFrameFactory )
	{
		Log = new TextViewLog( handler, textView, Timer, context, "debug_log.txt" );
		Log.WriteLine("Subsystem : Initialized "+ Initialized);
		
		Loader = new Loader( assetManager );
		
		JobScheduler = new JobScheduler(4);
		DelayResourceLoader = new DelayResourceLoader( JobScheduler, Log );

		m_threadGroupAppFrame = new ThreadGroup("AppFrame");
		
		
		if(Initialized <= 0)
		{
			DelayResourceQueue = new DelayResourceQueue();
        	Render = new Render();
   		    FramePointer = new FramePointer();
			
			DelayResourceLoader.RegisterJob
			(
				"SubSystem RenderSystem", DelayResourceQueue,
				new DelayResourceLoader.Job()
				{
					@Override public void OnLoadContent(DelayResourceQueue drq)
					{
						RenderSystem = new RenderSystem(drq,new RenderSystem.Configuration(), m_patterns);

						DebugFont = new Font(SubSystem.JobScheduler, drq, 1024, 16, 1, 1);
						SubSystem.RenderSystem.SetFont( DebugFont );

						DelayResourceQueue.Add( MinimumMarker );

						SubSystem.Log.WriteLine("Done RenderSystem");
					}
				}
			);

			DelayResourceLoader.RegisterJob
			( 
				"SubSystem Model", DelayResourceQueue,
				new DelayResourceLoader.Job()
				{
					@Override public void OnLoadContent(DelayResourceQueue drq)
					{
						Debug = new Debug( drq );
						ModelRender = new ModelRender( drq, Loader);
						drq.Add( SubSystemReadyMarker );
						SubSystem.Log.WriteLine("Done ModelRender");
					}
				}
			);
		}
		else
		{
			DebugFont = new Font(SubSystem.JobScheduler, DelayResourceQueue, 1024, 16, 1, 1);
			SubSystem.RenderSystem.SetFont( DebugFont );
			DelayResourceQueue.ReloadResources();
		}
		
		if( m_appFrame == null )
		{
			m_threadAppFrame = new UpdateThread(m_threadGroupAppFrame, appFrameFactory);
		}
		else
		{
			m_threadAppFrame = new UpdateThread(m_threadGroupAppFrame, m_appFrame );
			m_appFrame = null;
		}
		m_threadAppFrame.start();
			
		Initialized++;
	}
	
	public static void Exit()
	{
		if(DebugFont.IsReady()==false)
		{
			DebugFont.RemoveResources(DelayResourceQueue);
		}
		//MinimumMarker.Reset();
		//SubSystemReadyMarker.Reset();
		
		Log.Attach(null,null);
		m_appFrame = m_threadAppFrame.InterruptAndJoin();
		//m_threadAppFrame.InterruptAndJoin();
		m_threadAppFrame = null;
		m_threadGroupAppFrame = null;
		
		//DelayResourceLoader = null;
		
		JobScheduler.DestroyAllWorkers();
		JobScheduler = null;

		//FramePointer = null;
		//Render = null;
		
		Loader = null;
		//DelayResourceQueue = null;
		
		Log.WriteLine("Subsystem : Exited " + Exited);
		Log.Close();
		Log = null;
		Exited++;
	}
	
	private static final BitmapFont.Pattern[] m_patterns =
	{
		new BitmapFont.Pattern(
			"0123456789",
			new String[]
			{
				"-000----0----000---000-----0--00000--000--00000--000---000--",
				"0---0---0---0---0-0---0---00--0-----0---0-----0-0---0-0---0-",
				"0---0---0-------0-----0--0-0--0-----0--------0--0---0-0---0-",
				"0---0---0-----00----00--0--0--0000--0000----0----000---0000-",
				"0---0---0----0--------0-0--0------0-0---0---0---0---0-----0-",
				"0---0---0---0-----0---0-00000-0---0-0---0---0---0---0-----0-",
				"-000----0---00000--000-----0---000---000----0----000---000--",
				"------------------------------------------------------------",
			}
		),
		new BitmapFont.Pattern(
			"ABCDEFGHIJ",
			new String[]
			{
				"-ooo--0000---000--0000--00000-00000--000--0---0--000----000-",
				"o---o-0---0-0---0-0---0-0-----0-----0---0-0---0---0------0--",
				"o---o-0---0-0-----0---0-0-----0-----0-----0---0---0------0--",
				"ooooo-0000--0-----0---0-0000--0000--0--00-00000---0------0--",
				"o---o-0---0-0-----0---0-0-----0-----0---0-0---0---0------0--",
				"o---o-0---0-0---0-0---0-0-----0-----0---0-0---0---0---0--0--",
				"o---o-0000---000--0000--00000-0------000--0---0--000---00---",
				"------------------------------------------------------------",
			}
		),
		new BitmapFont.Pattern(
			"KLMNOPQRST",
			new String[]
			{
				"0---0-0-----0---0-0---0--000--0000---000--0000---000--00000-",
				"0--0--0-----00-00-00--0-0---0-0---0-0---0-0---0-0---0---0---",
				"0-0---0-----0-0-0-00--0-0---0-0---0-0---0-0---0-0-------0---",
				"00----0-----0---0-0-0-0-0---0-0000--0---0-0000---000----0---",
				"0-0---0-----0---0-0--00-0---0-0-----0-0-0-0-0-------0---0---",
				"0--0--0-----0---0-0--00-0---0-0-----0--0--0--0--0---0---0---",
				"0---0-00000-0---0-0---0--000--0------00-0-0---0--000----0---",
				"------------------------------------------------------------",
			}
		),
		new BitmapFont.Pattern(
			"UVWXYZ./",
			new String[]
			{
				"0---0-0---0-0---0-0---0-0---0-00000-----------0-",
				"0---0-0---0-0---0-0---0-0---0-----0----------0--",
				"0---0-0---0-0---0--0-0---0-0-----0-----------0--",
				"0---0-0---0-0---0---0-----0-----0-----------0---",
				"0---0-0---0-0-0-0--0-0----0----0-----------0----",
				"0---0--0-0--00-00-0---0---0---0------00----0----",
				"-000----0---0---0-0---0---0---00000--00---0-----",
				"------------------------------------------------",
			}
		),
	};
	
}










	
