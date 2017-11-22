package com.nrt.framework;


import android.content.res.*;

import android.widget.TextView;
import android.os.Handler;

import com.nrt.basic.*;
import com.nrt.render.*;
import com.nrt.model.*;
import com.nrt.font.*;
import com.nrt.input.*;

import java.lang.ThreadGroup;

public class SubSystem
{
	//. アプリで唯一じゃないとじゃないとマズいもの.
	public static DelayResourceQueue DelayResourceQueue = null;

	//. アクティビティから貰ってくるもの.
	public static Loader Loader = null;
	public static TextViewLog Log = null;
	
	public static AppFrame m_appFrame = null;

	public static ThreadGroup m_threadGroupAppFrame = null;
	public static UpdateThread m_threadAppFrame = null;

	public static JobScheduler JobScheduler = null;

	public static void Initialize( AssetManager assetManager, TextView textView, Handler handler, AppFrameFactory appFrameFactory )
	{
		DelayResourceQueue = new DelayResourceQueue();
		Loader = new Loader( assetManager );
		Log = new TextViewLog( handler, textView );

        Render = new Render();
        FramePointer = new FramePointer();
        Timer = new FrameTimer();

		JobScheduler = new JobScheduler(4);
		DelayResourceLoader = new DelayResourceLoader( JobScheduler, Log );
		
		m_appFrame = appFrameFactory.Create();
		m_threadGroupAppFrame = new ThreadGroup("AppFrame");

		m_threadAppFrame = new UpdateThread(m_threadGroupAppFrame,m_appFrame);
		m_threadAppFrame.start();
		OnCreate();
	}
	
	public static void Exit()
	{
		m_threadAppFrame.InterruptAndJoin();
		m_threadAppFrame = null;
		m_appFrame = null;
		
		m_threadGroupAppFrame = null;
		
		DelayResourceLoader = null;
		
		JobScheduler.DestroyAllWorkers();
		JobScheduler = null;

		Timer = null;
		FramePointer = null;
		Render = null;
		
		Log = null;
		Loader = null;
		DelayResourceQueue = null;
	}
	
	public static Render Render = null;
	public static RenderSystem RenderSystem = null;

	public static ModelRender ModelRender = null;

	public static FramePointer FramePointer = null;

	public static Rand Rand = new Rand();
	public static FrameTimer Timer = null; 

	public static Debug Debug = null;
	
	public static DelayResourceLoader DelayResourceLoader = null;
	public static DelayResourceQueueMarker MinimumMarker = new DelayResourceQueueMarker("MinimumSubSystem" );
	public static DelayResourceQueueMarker SubSystemReadyMarker = new DelayResourceQueueMarker("SubSystemReady");
	public static Font DebugFont = null;

	private static void OnCreate()
	{

		//final DelayResourceQueue drq = DelayResourceQueue;
		
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
				}
			}
		);

		DelayResourceLoader.RegisterJob
		( 
			"SubSystem GameMain", DelayResourceQueue,
			new DelayResourceLoader.Job()
			{
				@Override public void OnLoadContent(DelayResourceQueue drq)
				{
					
					m_appFrame.OnCreate(drq);
				}
			}
		);
	}
	/*
	public static void OnLoadContent( DelayResourceQueue drq, int iJob )
	{
		if( iJob == 0 )
		{
			Debug = new Debug( Render, drq, MatrixCache );
			ModelRender = new ModelRender( drq, BasicRender, Loader);
			drq.Add( SubSystemReadyMarker );
		}
		else
		{
			Font fontDebug = new Font(drq, 1024, 16, 1, 1);		
			DebugFont = new FontRender(drq, BasicRender);
			DebugFont.SetFont(fontDebug);
			DebugFont.SetSize(16);
			DebugFont.SetFontColor(0xffffffff);
			DebugFont.SetBoarderColor(0xff000000);		
			drq.Add( DebugFontReadyMarker );		
		}
	}
	*/
	
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










	
