package com.nrt.framework;


import android.content.res.*;

import android.widget.TextView;
import android.os.Handler;

import com.nrt.basic.*;
import com.nrt.render.*;
import com.nrt.model.*;
import com.nrt.font.*;
import com.nrt.input.*;

public class SubSystem
{
	//. アプリで唯一じゃないとじゃないとマズいもの.
	public static DelayResourceQueue DelayResourceQueue = new DelayResourceQueue();

	//. アクティビティから貰ってくるもの.
	public static Loader Loader = null;
	public static TextViewLog Log = null;
	
	public static void Initialize( AssetManager assetManager, TextView textView, Handler handler )
	{
		Loader = new Loader( assetManager );
		Log = new TextViewLog( handler, textView );
	}
	
	
	//public static BitmapFont BitmapFont = null;
	//public static FontRender DebugFont = null;

	public static Render Render = null;
	//public static BasicRender BasicRender = null;
	//public static MatrixCache MatrixCache = null;
	public static RenderSystem RenderSystem = null;

	public static ModelRender ModelRender = null;

	public static FramePointer FramePointer = null;

	public static Rand Rand = new Rand();
	public static FrameTimer Timer = null; 
	//public static DebugLog Error = new DebugLog(45);

	public static Debug Debug = null;
	
	public static DelayResourceLoader DelayResourceLoader = null;


	public static DelayResourceQueueMarker MinimumMarker = new DelayResourceQueueMarker("MinimumSubSystem" );
	public static DelayResourceQueueMarker DebugFontReadyMarker = new DelayResourceQueueMarker("DebugFont");
	
	public static DelayResourceQueueMarker SubSystemReadyMarker = new DelayResourceQueueMarker("SubSystemReady");
	
	public static void OnCreate(final Render r) //throws ThreadForceDestroyException
	{
		FramePointer = new FramePointer();
		Timer = new FrameTimer();		
		Render = r;
		final DelayResourceQueue drq = DelayResourceQueue;
		
		//MatrixCache = new MatrixCache();
		//BasicRender = new BasicRender(drq);//1024 * 1024 * 4, 1024 * 1024);
		//ModelRender = new ModelRender(null, BasicRender, Loader);
		RenderSystem = new RenderSystem(drq,new RenderSystem.Configuration(), m_patterns);
		//DelayResourceQueue = new DelayResourceQueue();

		//BitmapFont = new BitmapFont( drq, m_patterns );
	
		DelayResourceQueue.Add( MinimumMarker );
		
		DelayResourceLoader = new DelayResourceLoader( 3, Log );

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
			"SubSystem DebugFont", DelayResourceQueue,
			new DelayResourceLoader.Job()
			{
				@Override public void OnLoadContent(DelayResourceQueue drq)
				{
					Font fontDebug = new Font(drq, 1024, 16, 1, 1);
					SubSystem.RenderSystem.SetFont( fontDebug );
					/*
					DebugFont = new FontRender(drq);
					DebugFont.SetFont(fontDebug);
					DebugFont.SetSize(16);
					DebugFont.SetFontColor(0xffffffff);
					DebugFont.SetBoarderColor(0xff000000);
					*/
					drq.Add( DebugFontReadyMarker );		
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










	
