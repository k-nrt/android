package com.nrt.helloworld;

import java.nio.ByteBuffer;
import java.nio.*;
import android.opengl.*;
import java.security.*;

import com.nrt.math.*;
import com.nrt.render.*;
import com.nrt.framework.*;


class ADSR
{
	public float AttackTime = 0.0f;
	public float DecayTime = 0.0f;
	public float SustainTime = 0.0f;
	public float ReleaseTime = 0.0f;
	public float AttackLevel = 0.0f;
	public float DecayLevel = 0.0f;
	public float SustainLevel = 0.0f;
	
	public float Time = 0.0f;
	public float Level = 0.0f;
	
	private enum EStatus
	{
		Stop,
		Play,
	}
	
	private EStatus m_eStatus = EStatus.Stop;
	
	public boolean IsStop() { return ((m_eStatus==EStatus.Stop) ? true : false ); }
	public boolean IsPlay() { return ((m_eStatus==EStatus.Play) ? true : false ); }
	
	
	public ADSR() {}

	public ADSR(
		float fAttackTime, float fAttackLevel,
		float fDecayTime, float fDecayLevel,
		float fSustainTime, float fSustainLevel,
		float fReleaseTime )
	{
		AttackTime = fAttackTime;
		AttackLevel = fAttackLevel;
		DecayTime = fDecayTime;
		DecayLevel = fDecayLevel;
		SustainTime = fSustainTime;
		SustainLevel = fSustainLevel;
		ReleaseTime = fReleaseTime;
	}
	
	public void Load(
		float fAttackTime, float fAttackLevel,
		float fDecayTime, float fDecayLevel,
		float fSustainTime, float fSustainLevel,
		float fReleaseTime )
	{
		AttackTime = fAttackTime;
		DecayTime = AttackTime + fDecayTime;
		SustainTime = DecayTime + fSustainTime;
		ReleaseTime = SustainTime + fReleaseTime;

		AttackLevel = fAttackLevel;
		DecayLevel = fDecayLevel;
		SustainLevel = fSustainLevel;
	}
		
	public void Reset()
	{
		Time = 0.0f;
		m_eStatus = EStatus.Stop;
	}
	
	public void Release()
	{
		if( Time < SustainTime )
		{
			Time = SustainTime;
		}
	}
	
	public void Start()
	{
		//if( m_eStatus == EStatus.Stop )
		{
			Time = 0.0f;
			m_eStatus = EStatus.Play;
		}
	}
	
	public void Update( float fElapsedTime )
	{
		if( m_eStatus == EStatus.Stop )
		{
			return;
		}
		
		if( Time < AttackTime )
		{
			Level = AttackLevel*(Time/AttackTime);
		}
		else if( Time < DecayTime )
		{
			Level = FMath.Lerp( AttackLevel,DecayLevel,(Time - AttackTime)/(DecayTime - AttackTime));
		}
		else if( Time < SustainTime )
		{
			Level = FMath.Lerp( DecayLevel,SustainLevel,(Time - DecayTime)/(SustainTime - DecayTime));
		}
		else if( Time < ReleaseTime )
		{
			Level = FMath.Lerp( SustainLevel, 0.0f, (Time - SustainTime)/(ReleaseTime - SustainTime));
		}
		else
		{
			Level = 0.0f;
			Time = 0.0f;
			m_eStatus = EStatus.Stop;
			return;
		}
		
		Time += fElapsedTime;
	}
}
	
class GameAntiBeamFieldDamage
{
	public final ADSR ADSR = new ADSR(
		0.1f, 1.0f,
		0.5f, 0.75f,
		0.5f, 0.75f,
		1.0f );
	
	public final Float3 Position = new Float3();
	
	public float Radius = 600.0f;
	public float Alpha = 1.0f;
	public float Animation = 0.0f;
	public float Time = 0.0f;
	public float TimeOut = 2.0f;
	
	public final Float4x4 SphereTransform = new Float4x4();
	
	
	public boolean IsIdle() { return ADSR.IsStop(); }
	
	public GameAntiBeamFieldDamage(){}
	
	public void Spawn( Float3 f3Position, Float4x4 matrixSphere, float fSphereRadius )
	{
		Position.Set( f3Position );
		ADSR.Start();
		Alpha = 0.0f;
		Animation = 0.0f;
		Float4x4.Mul(SphereTransform, Float4x4.Scaling( SphereTransform, fSphereRadius ), matrixSphere );
	}
	
	public void Update( float fElapsedTime )
	{
		if( IsIdle() )
		{
			return;
		}
		
		Time+=fElapsedTime;
		ADSR.Update( fElapsedTime );
		Alpha = ADSR.Level;
		Animation += fElapsedTime;
	}
}

class GameAntiBeamFieldDamageList
{
	public final GameAntiBeamFieldDamage[] Damages = new GameAntiBeamFieldDamage[8];
	public int Next = 0;
	
	public GameAntiBeamFieldDamageList()
	{
		for( int i = 0 ; i < Damages.length ; i++ )
		{
			Damages[i] = new GameAntiBeamFieldDamage();
		}
	}
	
	public void Spawn( Float3 f3Position, Float4x4 matrixSphere, float fSphereRadius )
	{
		Damages[Next].Spawn( f3Position, matrixSphere, fSphereRadius );
		Next++;
		if( Damages.length <= Next )
		{
			Next = 0;
		}
	}
	
	public void Update( float fElapsedTime )
	{
		for( GameAntiBeamFieldDamage damage : Damages )
		{
			damage.Update( fElapsedTime );
		}
	}
}
	
class GameAntiBeamField
{
	public final Transform3 LocalTransform = new Transform3();
	public final Transform3 Transform = new Transform3();
	public float Anim = 0.0f;
	
	public float Damage = 0.0f;
	
	public float Radius = 1200.0f;
	public float RadiusIn = 0.0f;
	
	public float DamageLimit = 100.0f;
	public float InitialRadius = 1200.0f;
	
	public final Float4 ColorIn = new Float4( 0.2f, 0.0f, 0.5f, 0.0f );
	public final Float4 ColorOut = new Float4( 0.5f, 1.0f, 2.0f, 1.0f );
	public final Float4 FieldColorIn = new Float4( 0.2f, 0.0f, 0.5f, 0.0f );
	public final Float4 FieldColorOut = new Float4( 0.5f, 1.0f, 2.0f, 1.0f );
	public final Float4 DamageColorIn = new Float4( 1.0f, 0.0f, 0.1f, 0.0f );
	public final Float4 DamageColorOut = new Float4( 1.5f, 0.75f, 0.5f, 1.0f );
	
	public final Float3 DamagePosition = new Float3( 0.0f );
	public float DamageRadius = 600.0f;
	public float DamageAlpha = 1.0f;
	public float m_fDamageTime = 0.0f;
	public float m_fDamageTimeOut = 2.0f;
	
	public final ADSR m_adsr = new ADSR(
		0.02f, 1.0f,
		0.05f, 0.75f,
		0.1f, 0.75f,
		0.2f );
	
	public enum EStatus
	{
		Idle,
		Busy,
		Destroy,
	}
	
	public EStatus Status = EStatus.Idle;
	
	public boolean IsIdle() { return ( (Status == EStatus.Idle) ? true : false ); }
	public boolean IsBusy() { return ( (Status == EStatus.Busy) ? true : false ); }
	public boolean IsDestroy() { return ( (Status == EStatus.Destroy) ? true : false ); }
	
	public GameAntiBeamField() {}
	
	public void Spawn( Transform3 transformLocal, Transform3 transformParent )
	{
		LocalTransform.Load( transformLocal );
		Transform3.Mul( Transform, LocalTransform, transformParent );
		
		Damage = 0.0f;
		Anim = SubSystem.Rand.Float();
		Status = EStatus.Busy;
		
		Float3.Mad( DamagePosition, Transform.Y, InitialRadius, Transform.W );
		
		m_adsr.Reset();
	}
	
	public void OnUpdate( float fElapsedTime, Transform3 transformParent )
	{
		switch( Status )
		{
			case Idle:
				break;
				
			case Busy:
				Anim += fElapsedTime;
				Radius = InitialRadius;
				m_adsr.Update( fElapsedTime );
				break;
				
			case Destroy:
				m_adsr.Update( fElapsedTime );
				break;
		}
		
		Transform3.Mul( Transform, LocalTransform, transformParent );
		RadiusIn = ((Damage/DamageLimit))*Radius;
		
		Float4.Lerp( ColorIn, m_adsr.Level, FieldColorIn, DamageColorIn );
		Float4.Lerp( ColorOut, m_adsr.Level, FieldColorOut, DamageColorOut );
	}
	
	public void SetDamagePosition( Float3 v3Position )
	{
		if( m_adsr.IsStop() )
		{
			DamagePosition.Set( v3Position );
			m_adsr.Start();
		}
	}
}

class GameAntiBeamFieldRender
{
	private int m_nbVertices = 0;
	private int m_nbIndices = 0;
	private StaticVertexBuffer m_vertexBuffer = null;
	private StaticIndexBuffer m_indexBuffer = null;

	private VertexStream m_stream = null;

	static class FieldRender
	{
		public Program m_program = null;

		public Uniform u_world = null;
		public Uniform u_worldViewProjecrion = null;
		public Uniform u_colorIn = null;
		public Uniform u_colorOut = null;
		public Uniform u_position = null;
		public Uniform u_radius = null;
		public Uniform u_radiusIn = null;
		
		public Uniform u_anim = null;
	
		public Uniform u_normal = null;
	
		public Uniform u_viewPosition = null;
		
		public FieldRender( DelayResourceQueue drq ) //throws ThreadForceDestroyException
		{
		AttributeBinding[] ab =
		{
			new AttributeBinding(0, "a_position"),
		};

		
		m_program = new Program( drq, ab,
		new VertexShader( drq, SubSystem.Loader.LoadTextFile("anti_beam_field.vs")),
		new FragmentShader( drq, SubSystem.Loader.LoadTextFile("anti_beam_field.fs")));

		u_world = new Uniform(drq,m_program, "u_world");
		u_worldViewProjecrion = new Uniform(drq, m_program, "u_worldViewProjection");

		u_colorIn = new Uniform( drq, m_program, "u_colorIn");
		u_colorOut = new Uniform( drq, m_program, "u_colorOut");
		u_position = new Uniform( drq, m_program, "u_position" );
		u_radius = new Uniform( drq, m_program, "u_radius" );
		u_radiusIn = new Uniform( drq, m_program, "u_radiusIn" );
		u_normal = new Uniform( drq, m_program, "u_normal" );
		u_viewPosition = new Uniform( drq,m_program, "u_viewPosition" );

		u_anim = new Uniform( drq, m_program, "u_anim" );
		}
	}
	
	FieldRender m_fieldRender = null;
	
	static class DamageRender
	{
		public Program m_program = null;
		
		public Uniform u_world = null;
		public Uniform u_worldViewProjecrion = null;
		
		public Uniform u_damageAnim = null;
		public Uniform u_damagePosition = null;
		public Uniform u_damageRadius = null;
		public Uniform u_damageAlpha = null;
		
		public DamageRender( DelayResourceQueue drq ) //throws ThreadForceDestroyException
		{
		AttributeBinding[] ab =
		{
			new AttributeBinding(0, "a_position"),
		};

		m_program = new Program( drq, ab,
		new VertexShader( drq, SubSystem.Loader.LoadTextFile("anti_beam_field.vs")),
		new FragmentShader( drq, SubSystem.Loader.LoadTextFile("anti_beam_field_damage.fs")));

		u_world = new Uniform( drq, m_program, "u_world");
		u_worldViewProjecrion = new Uniform( drq, m_program, "u_worldViewProjection");

		u_damageAnim = new Uniform( drq, m_program, "u_damageAnim" );
			
		u_damagePosition = new Uniform( drq, m_program, "u_damagePosition" );
		u_damageRadius = new Uniform( drq, m_program, "u_damageRadius" );

		u_damageAlpha = new Uniform( drq, m_program, "u_damageAlpha" );
		}
	}
		
	DamageRender m_damageRender = null;
	
	
	
	private Float4 m_f4Color = new Float4(1.0f);

	public GameAntiBeamFieldRender( DelayResourceQueue drq, int nbVSplits, int nPolygon) 
		//throws ThreadForceDestroyException
	{
		m_fieldRender = new FieldRender(drq);
		m_damageRender = new DamageRender(drq);
		
		VertexAttribute[] at =
		{
			new VertexAttribute(0, 3, EVertexAttributeFormat.Float, false, 0),
		};

		m_stream = new VertexStream(at, 3 * 4);

		m_nbVertices = 2 + nbVSplits * nPolygon;

		ByteBuffer bufferVertices = ByteBuffer.allocateDirect(m_nbVertices * 12);
		bufferVertices.order(ByteOrder.nativeOrder());

		bufferVertices.putFloat(0.0f);
		bufferVertices.putFloat(1.0f);
		bufferVertices.putFloat(0.0f);

		for (int v = 1 ; v < nbVSplits ; v++)
		{
			float rad = FMath.PI * ((float)v) / ((float)(nbVSplits));
			float y = FMath.Cos(rad);
			float r = FMath.Sin(rad);
			for (int p = 0 ; p < nPolygon ; p++)
			{
				rad = FMath.RD * ((float)p) / ((float)nPolygon);
				float x = r * FMath.Sin(rad);
				float z = r * FMath.Cos(rad);

				bufferVertices.putFloat(x);
				bufferVertices.putFloat(y);
				bufferVertices.putFloat(z);
			}
		}

		bufferVertices.putFloat(0.0f);
		bufferVertices.putFloat(-1.0f);
		bufferVertices.putFloat(0.0f);
		bufferVertices.position(0);
		m_vertexBuffer = new StaticVertexBuffer( drq, bufferVertices);

		short[] indices = new short[nPolygon * 6 + (nbVSplits - 2) * nPolygon * 6];

		int i = 0;
		for (int v = 0 ; v < (nbVSplits) ; v++)
		{
			if (v <= 0)
			{
				for (int p = 0 ; p < nPolygon ; p++)
				{
					int pp = (p + 1) % nPolygon;

					short i0 = 0;
					short i1 = (short) (1 + v * nPolygon + p);
					short i2 = (short) (1 + v * nPolygon + pp);

					indices[i] = i0;
					i++;
					indices[i] = i1;
					i++;
					indices[i] = i2;
					i++;
				}				
			}
			else if (v < (nbVSplits - 1))
			{
				for (int p = 0 ; p < nPolygon ; p++)
				{
					int pp = (p + 1) % nPolygon;

					short i0 = (short) (1 + (v - 1) * nPolygon + p);
					short i1 = (short) (1 + (v - 1) * nPolygon + pp);
					short i2 = (short) (1 + v * nPolygon + p);
					short i3 = (short) (1 + v * nPolygon + pp);

					indices[i] = i0;
					i++;
					indices[i] = i2;
					i++;
					indices[i] = i1;
					i++;
					indices[i] = i2;
					i++;
					indices[i] = i3;
					i++;
					indices[i] = i1;
					i++;

				}
			}
			else
			{
				for (int p = 0 ; p < nPolygon ; p++)
				{
					int pp = (p + 1) % nPolygon;

					short i0 = (short) (1 + (v - 1) * nPolygon + p);
					short i1 = (short) (1 + (v - 1) * nPolygon + pp);
					short i2 = (short) (1 + v * nPolygon);

					indices[i] = i0;
					i++;
					indices[i] = i2;
					i++;
					indices[i] = i1;
					i++;
				}				

			}
		}

		m_nbIndices = i;
		m_indexBuffer = new StaticIndexBuffer(drq,indices);

	}

	public void SetColor(float r, float g, float b, float a)
	{
		m_f4Color.Set(r, g, b, a);
	}

	public void DrawField( GfxCommandContext r, MatrixCache mc,  GameAntiBeamField abf )
	{
		//BasicRender br = SubSystem.BasicRender;
		//MatrixCache mc = br.GetMatrixCache();
		//Render r = br.GetRender();
		
		//float t = (float)(System.Timer.FrameTime - Math.floor(System.Timer.FrameTime ));

		FieldRender f = m_fieldRender;

		r.SetProgram(f.m_program);
		r.SetMatrix(f.u_worldViewProjecrion, mc.GetWorldViewProjection().Values);
		r.SetMatrix(f.u_world, mc.GetWorld().Values );
		r.SetFloat4(f.u_colorIn, abf.ColorIn );
		r.SetFloat4(f.u_colorOut, abf.ColorOut );
		r.SetFloat3(f.u_position, mc.GetWorld().Values, 12 );
		r.SetFloat(f.u_radius, abf.Radius  );
		r.SetFloat(f.u_radiusIn, abf.RadiusIn  );
		
		r.SetFloat(f.u_anim, abf.Anim );
		
		r.SetFloat3( f.u_normal, abf.Transform.Y );
		
		r.SetFloat3( f.u_viewPosition, mc.GetViewInverse().GetAxisW( Float3.Local() ) );
//		r.SetFloat3( u_damagePosition, abf.DamagePosition );
//		r.SetFloat( u_damageRadius, abf.DamageRadius );

//		r.SetFloat( u_damageAlpha, abf.m_adsr.Level );
		
		r.SetVertexBuffer(m_vertexBuffer);

		r.EnableVertexStream(m_stream,0);

		r.SetIndexBuffer(m_indexBuffer);

		//GLES20.glDrawElements(GLES20.GL_TRIANGLES, m_nbIndices, GLES20.GL_UNSIGNED_SHORT, 0);
		r.DrawElements(EPrimitive.Triangles, m_nbIndices, EIndexFormat.UnsignedShort, 0);


		/*
		DamageRender d = m_damageRender;
		r.Bind(d.m_program);
		r.SetMatrix(d.u_worldViewProjecrion, mc.GetWorldViewProjection().Values);
		r.SetMatrix(d.u_world, mc.GetWorld().Values );
		r.SetFloat(d.u_damageAnim, abf.Anim );
		r.SetFloat3( d.u_damagePosition, abf.DamagePosition );
		r.SetFloat( d.u_damageRadius, abf.DamageRadius );
		r.SetFloat( d.u_damageAlpha, abf.m_adsr.Level );
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, m_nbIndices, GLES20.GL_UNSIGNED_SHORT, 0);
		*/
	}
	
	

	public void DrawDamage( GfxCommandContext r, MatrixCache mc, GameAntiBeamFieldDamage abfd )
	{
		if( abfd.IsIdle() || abfd.Alpha <= 0.0f )
		{
			return;
		}
		//BasicRender br = SubSystem.BasicRender;
		//MatrixCache mc = br.GetMatrixCache();
		//Render r = br.GetRender();

		DamageRender d = m_damageRender;

		r.SetProgram(d.m_program);
		r.SetMatrix(d.u_worldViewProjecrion, mc.GetWorldViewProjection().Values);
		r.SetMatrix(d.u_world, mc.GetWorld().Values );
		r.SetFloat(d.u_damageAnim, abfd.Animation );
		r.SetFloat3( d.u_damagePosition, abfd.Position );
		r.SetFloat( d.u_damageRadius, abfd.Radius );
		r.SetFloat( d.u_damageAlpha, abfd.Alpha );
		r.SetVertexBuffer(m_vertexBuffer);
		r.EnableVertexStream(m_stream,0);
		r.SetIndexBuffer(m_indexBuffer);
		//GLES20.glDrawElements(GLES20.GL_TRIANGLES, m_nbIndices, GLES20.GL_UNSIGNED_SHORT, 0);
		r.DrawElements(EPrimitive.Triangles, m_nbIndices, EIndexFormat.UnsignedShort, 0);
	}
	

}


