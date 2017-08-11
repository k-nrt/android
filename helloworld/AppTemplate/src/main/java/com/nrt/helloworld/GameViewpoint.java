package com.nrt.helloworld;
import com.nrt.math.FMath;
import com.nrt.math.Float3;
import com.nrt.math.Float4x4;

class GameViewPoint
{
	public Float4x4 Transform = Float4x4.Identity();

	public Float4x4 View = Float4x4.Identity();
	public Float4x4 Projection = Float4x4.Identity();
	public Float4x4 ViewProjection = Float4x4.Identity();

	public float BufferWidth  = 940.0f;
	public float BufferHeight = 544.0f;

	public final float HudBaseWidth = 940.0f;
	public final float HudBaseHeight = 544.0f;
	
	public float HudWidth = 960.0f;
	public float HudHeight = 544.0f; //960.0f*16.0f/9.0f;
	
	public float HudLeft = 0.0f;
	public float HudRight = 960.0f;
	public float HudTop = 0.0f;
	public float HudBottom = 544.0f;
	
	public float PixelWidth = 1.0f;
	public float PixelHeight = 1.0f;

	public Float4x4 HudProjection = Float4x4.Identity();

	public float HFov = 0.0f;
	public float VFov = 0.0f;

	public float Near = 0.0f;
	public float Far  = 0.0f;

	public GameViewPoint () {}

	public void OnSurfaceChanged( float w, float h )
	{
		BufferWidth = w;
		BufferHeight = h;
	}
	
	//. 視点の更新.
	public void Update( Float4x4 matrixTransform, float fHFov, float fNear, float fFar )
	{
		//BufferWidth  = fBufferWidth;
		//BufferHeight = fBufferHeight;

		Transform = matrixTransform;
		Float3 v3Position = matrixTransform.GetAxisW( Float3.Local() );
		float fDistance = Float3.Length( v3Position );

		Float3 v3Forward = Float3.Mul( Float3.Local(), matrixTransform.GetAxisZ( Float3.Local()), (fDistance <= 0.0f ? 1.0f : fDistance ) );

		View = Float4x4.LookAt( v3Position, Float3.Add( Float3.Local(), v3Position, v3Forward ), matrixTransform.GetAxisY( Float3.Local() ) );

		float fAspect = BufferWidth/BufferHeight;
		HFov = fHFov;
		float c = FMath.Acos( fHFov/2.0f );
		float s = FMath.Asin( fHFov/2.0f );
		VFov = FMath.Atan2( s/fAspect, c )*2.0f;

		Near = fNear;
		Far = fFar;


		ViewProjection = Float4x4.Mul( View, Projection );

		HudWidth = HudBaseWidth;
		HudHeight = HudWidth/fAspect;
		//float fOffset = (fHeight - HudWidth)*0.5f;
		
		HudLeft = 0.0f;
		HudRight = HudWidth;
		HudTop = -(HudHeight - HudBaseHeight)*0.5f;
		HudBottom = HudHeight + HudTop;
		
		if( HudTop > 0.0f )
		{
			HudHeight = HudBaseHeight;
			HudWidth = HudHeight*fAspect;
			//float fOffset = (fHeight - HudWidth)*0.5f;

			HudTop = 0.0f;
			HudBottom = HudHeight;
			
			HudLeft = -(HudWidth - HudBaseWidth)*0.5f;
			HudRight = HudWidth + HudLeft;
			//HudTop = -(HudHeight - HudBaseHeight)*0.5f;
			//HudBottom = HudHeight + HudTop;
			
			VFov = FMath.Atan2( s/(HudBaseWidth/HudBaseHeight), c )*2.0f;
			
		}
		
		Projection = Float4x4.Perspective( VFov, fAspect, fNear, fFar );
		
		//HudProjection = Matrix4.Ortho( -HudWidth*0.5f, HudWidth*0.5f, -HudHeight*0.5f, HudHeight*0.5f, -1.0f, 1.0f );
		HudProjection = Float4x4.Ortho( HudLeft, HudRight, HudBottom, HudTop, -1.0f, 1.0f );
		
		PixelWidth = HudWidth/BufferWidth;
		PixelHeight = HudHeight/BufferHeight;
	}

	/// <summary>
	/// HUD スクリーン上の座標に変換.
	/// </summary>
	/// <returns>
	/// The hud screen.
	/// </returns>
	/// <param name='v3Position'>
	/// V3 position.
	/// </param>
	public void ProjectHudScreen( Float3 f3Result, Float3 v3Position )
	{
		Float3 v3Screen = Float3.Local();
		ViewProjection.TransformProjection( v3Screen, v3Position );
		//v3Screen = v3Screen*new Vector3( HudWidth*0.5f, -HudHeight*0.5f, 1.0f ) + new Vector3( HudWidth*0.5f, HudHeight*0.5f, 0.0f );
		Float3.Mad( 
			f3Result, 
			v3Screen, Float3.Local( HudWidth*0.5f, -HudHeight*0.5f, 1.0f ), 
			Float3.Local( HudWidth*0.5f + HudLeft, HudHeight*0.5f + HudTop, 0.0f ) );
		
	//	return v3Screen;
	}

	/// <summary>
	/// タッチ座標 ( -0.5 ～ 0.5) をスクリーン座標に変換する.
	/// </summary>
	/// <returns>
	/// The hud screen.
	/// </returns>
	/// <param name='v2TouchPosition'>
	/// V2 touch position.
	/// </param>
	public void ProjectHudScreenFromTouchPoint( Float3 f3Result, Float3 f3TouchPoint )
	{
		Float3 f3 = Float3.Local();
		Float3.Mad( f3, f3TouchPoint,
			Float3.Local( HudWidth/BufferWidth, HudHeight/BufferHeight, 1.0f ),
			Float3.Local( HudLeft, HudTop, 0.0f ) );
			
		f3Result.Set( f3 );
		
		
		
		//return (v2TouchPosition + 0.5f)*new Vector2( HudWidth, HudHeight );
	}	
}


