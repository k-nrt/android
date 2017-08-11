package com.nrt.ui;

import com.nrt.font.BitmapFont;
import com.nrt.math.Quaternion;
import com.nrt.math.Float3;
import com.nrt.math.Float3x3;
import com.nrt.math.Float4x4;
import com.nrt.math.FMath;

import com.nrt.input.FramePointer;
import com.nrt.render.BasicRender;
import com.nrt.render.BlendFunc;
import com.nrt.render.BlendState;
import com.nrt.render.ECullFace;
import com.nrt.render.EFrontFace;
import com.nrt.render.GfxCommandContext;
import com.nrt.render.MatrixCache;
import com.nrt.render.RasterizerState;

public class UiTrackBall extends UiRoundButton
{
	private final Quaternion AngularVelocity = Quaternion.LoadIdentity( new Quaternion() );
	public final Float3 EulerAngularVelocity = new Float3(0.0f);

	public final Float4x4 Rotation = Float4x4.Identity();

	public float Time = 0.0f;
	
	public UiTrackBall(float x, float y, float radius)
	{
		super(x, y, radius);
	}

	@Override
	public void OnUpdate(float fElapsedTime)
	{
		super.OnUpdate(fElapsedTime);

		for (int i = 0 ; i < Pointers.length ; i++)
		{
			UpdateAngularVelocity(Pointers[i], Owners[i], fElapsedTime);
		}
		
		Time += fElapsedTime;
	}

	public void UpdateAngularVelocity(FramePointer.Pointer point, boolean Owner, float fElapsedTime)
	{
		if (Owner)
		{
			
			//System.Error.Write( Float.toString( point.PrevPosition.X) + " " );
			//System.Error.WriteLine( Float.toString( point.Position.X) );

			//. 半径 1 で中心 0 の円にする.
			Float3 v3Prev = Float3.Local().Set( 
				(point.PrevPosition.X - EnterCenter.X) / EnterRadius,
				(point.PrevPosition.Y - EnterCenter.Y) / EnterRadius,
				0.0f);//new Vector3( (PrevPoint.Position - Center)/Radius, 0.0f );
			Float3 v3Current = Float3.Local().Set(
				(point.Position.X - EnterCenter.X) / EnterRadius, 
				(point.Position.Y - EnterCenter.Y) / EnterRadius,
				0.0f);//new Float3( (Point.Position - Center)/Radius, 0.0f );

			//. 球の位置にする.
			float len = (v3Prev.X * v3Prev.X + v3Prev.Y * v3Prev.Y);
			if( len > 1.0f )
			{
				Float3.Normalize( v3Prev, v3Prev );
			}
			else
			{
				v3Prev.Z = FMath.Sqrt(1.0f - len );
				Float3.Normalize( v3Prev, v3Prev);
			}

			len = (v3Current.X * v3Current.X + v3Current.Y * v3Current.Y);
			if( len > 1.0f )
			{
				Float3.Normalize( v3Current, v3Current );
			}
			else
			{
				v3Current.Z = FMath.Sqrt(1.0f - len);
				Float3.Normalize( v3Current, v3Current);
			}
			//. 回転軸.
			float fDot = Float3.Dot(v3Prev, v3Current);
			Float3 v3Axis = Float3.CrossNormalize( Float3.Local(), v3Prev, v3Current );
			
			
			
			if ( Float3.Dot( v3Axis, v3Axis ) > 0.0f )
			{
				//v3Axis = Vector3.Normalize( v3Axis );
				//Rotation = Matrix4.RotationAxis( v3Axis, FMath.Acos( fDot ) )*Rotation;
				//System.Error.WriteLine( String.format( " %f %f %f %f", v3Axis.X, v3Axis.Y, v3Axis.Z, (FMath.Acos( fDot )*180.0f/FMath.PI)/fElapsedTime ) );
				Quaternion.LoadRotationAxis( AngularVelocity, v3Axis, FMath.Acos(fDot));//fElapsedTime );
			}
			else
			{
				//System.Error.WriteLine( "zero" );
				Quaternion.LoadIdentity( AngularVelocity );
			}

			Float3.Mul( EulerAngularVelocity, (Float3x3.Rotation( Float3x3.Local(), AngularVelocity)).GetEulerRotationXYZ( Float3.Local() ), 1.0f/fElapsedTime );

			EulerAngularVelocity.X = ( Float.isNaN( EulerAngularVelocity.X ) ? 0.0f : EulerAngularVelocity.X );
			EulerAngularVelocity.Y = ( Float.isNaN( EulerAngularVelocity.Y ) ? 0.0f : EulerAngularVelocity.Y );
			EulerAngularVelocity.Z = ( Float.isNaN( EulerAngularVelocity.Z ) ? 0.0f : EulerAngularVelocity.Z );

			//. 離したときはスレッショルドを上げる.
			float fThreshold = 0.0f;
			if( IsRelease() )
			{
				fThreshold = FMath.PI*600.0f/180.0f;
				if( FMath.Abs( EulerAngularVelocity.X ) < fThreshold )
				{
					EulerAngularVelocity.X = 0.0f;
				}
				
				if( FMath.Abs( EulerAngularVelocity.Y ) < fThreshold )
				{
					EulerAngularVelocity.Y = 0.0f;
				}
				
				if( FMath.Abs( EulerAngularVelocity.Z ) < fThreshold )
				{
					EulerAngularVelocity.Z = 0.0f;
				}
			}
			
			
			Float3.Clamp( EulerAngularVelocity,  EulerAngularVelocity, Float3.Local( -FMath.PI ), Float3.Local( FMath.PI ) );
		}

		Float4x4.Mul( Rotation,
					 Rotation,
					 Float4x4.RotationZ( Float4x4.Local(), EulerAngularVelocity.Z * fElapsedTime*0.5f),
					 Float4x4.RotationY( Float4x4.Local(), EulerAngularVelocity.Y * fElapsedTime*0.5f),
					 Float4x4.RotationX( Float4x4.Local(), EulerAngularVelocity.X * fElapsedTime*0.5f) );
	}

	@Override
	public void OnRender(final BasicRender br, final BitmapFont bf)
	{
		Render(br, Time, EnterCenter, EnterRadius, Rotation );
	}
	
	public static void Render(final BasicRender br, float Time, Float3 EnterCenter, float EnterRadius, Float4x4 Rotation )
	{
		/*
		if (m_nbPrevPointers < m_nbPointers)
		{
			br.SetColor(0.5f, 1.0f, 0.75f, 1);
		}
		else if (m_nbPointers > 0)
		{
			br.SetColor(0.2f, 1.0f, 0.5f, 1);			
		}
		else
		{
			br.SetColor(0, 0.5f, 0, 1);
		}
		*/
		final GfxCommandContext gfxc = br.GetCommandContext();

		BlendState blendSrcAlphaOneMinusSrcAlpha = new BlendState(true, BlendFunc.SrcAlpha,BlendFunc.OneMinusSrcAlpha);
		gfxc.SetBlendState(blendSrcAlphaOneMinusSrcAlpha);

		RasterizerState cullDisable = new RasterizerState(false, ECullFace.Back, EFrontFace.CCW);
		gfxc.SetRasterizerState(cullDisable);

		//GLES20.glEnable( GLES20.GL_BLEND );
		//GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA );
		//GLES20.glDisable( GLES20.GL_CULL_FACE );
		
		
		br.Arc(EnterCenter.X, EnterCenter.Y, 0.0f, 0x00000000, EnterRadius, 0x000066cc, 16);

		MatrixCache mc = br.GetMatrixCache();

		BlendState blendSrcAlphaOne = new BlendState(true,BlendFunc.SrcAlpha,BlendFunc.One);
		gfxc.SetBlendState(blendSrcAlphaOne);
		//GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE );
		
		float r = EnterRadius - 2.0f;
		float fFov = FMath.ToRad( 55.0f );
		float c = 1.0f/FMath.Sin(fFov*0.5f);			
		
		Float4x4 projection = Float4x4.Mul(
			Float4x4.Local(),
			Rotation, 
			Float4x4.Translation( Float4x4.Local(), 0.0f, 0.0f, -c ),
			Float4x4.Perspective( Float4x4.Local(), fFov, 1.0f, c - 1.0f, c+1.0f ),

			Float4x4.Scaling( Float4x4.Local(),  r, r, 1.0f),			
			Float4x4.Translation( Float4x4.Local(), EnterCenter) );
			
			
		for (int i = -3 ; i <= 3 ; i++)
		{
			float z = 0.0f;//(0.0f + (float)i) / 2.0f;
			if( i < 0 )
			{
				z = FMath.Fraction(Time)*(1.0f/3.0f) + ((float)(i+0))/3.0f;
			}
			else if( i > 0 )
			{
				z = -FMath.Fraction(Time)*(1.0f/3.0f) + ((float)(i-0))/3.0f;
			}
			else
			{
				continue;
			}
			
			float s = FMath.Sqrt(1.0f - z * z);
			
			Float4x4 matrix = 
				Float4x4.Mul(
					Float4x4.Local(),
					Float4x4.Scaling( Float4x4.Local(), s, s, 1.0f),
					Float4x4.Translation( Float4x4.Local(), 0.0f, 0.0f, z),
					projection
				);

			mc.SetWorld(matrix);
			br.Arc(0.0f, 0.0f, 1.0f, 0x115566ff, 0.90f, 0x000000ff, 8);
		}
		mc.SetWorld(Float4x4.Identity( Float4x4.Local() ));
		
		//GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA );

		BlendState blendDisable = new BlendState(false, BlendFunc.One,BlendFunc.Zero);
		gfxc.SetBlendState(blendDisable);
	}
}
