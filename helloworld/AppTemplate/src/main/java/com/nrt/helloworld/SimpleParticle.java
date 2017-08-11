package com.nrt.helloworld;

import com.nrt.framework.RenderContext;
import com.nrt.render.*;
import com.nrt.math.Float3;

import com.nrt.framework.SubSystem;


class SimpleParticle
{
	public Program m_program = null;
	public Uniform u_worldView = null;
	public Uniform u_projection = null;
	public Uniform u_targetSize = null;
	public Sampler u_texture = null;
	SamplerState m_samplerState = null;
	
	//public BasicRender m_basicRender = null;
	public StaticTexture m_texture = null;
	
	public static class ParticleDescriptor
	{
		public final Float3 Position = new Float3( 0.0f );
		public final Float3 Velocity = new Float3( 0.0f );
		public float Angle = 0.0f;
		public float AngularVelocity = 0.0f;
		public int U = 0;
		public int V = 0;

		public float AttackTime = 0.0f;
		public float AttackRadius = 0.0f;
		public float DecayTime = 0.0f;
		public float DecayRadius = 0.0f;
		public float SustainTime = 0.0f;
		public float ReleaseTime = 0.0f;
		public float ReleaseRadius = 0.0f;
		
		public ParticleDescriptor() {}
		
		public ParticleDescriptor Set(
			Float3 f3Position, Float3 f3Velocity,
			float fAngle, float fAngularVelocity,
			int nU, int nV,
			float fAttackTime, float fAttackRadius,
			float fDecayTime, float fDecayRadius,
			float fSustainTime,
			float fReleaseTime, float fReleaseRadius )
		{
			Position.Set( f3Position );
			Velocity.Set( f3Velocity );
			Angle = fAngle;
			AngularVelocity = fAngularVelocity;
			U = nU;
			V = nV;
			AttackTime = fAttackTime;
			AttackRadius = fAttackRadius;
			DecayTime = fDecayTime;
			DecayRadius = fDecayRadius;
			SustainTime = fSustainTime;
			ReleaseTime = fReleaseTime;
			ReleaseRadius = fReleaseRadius;
			
			return this;
		}
		
		public ParticleDescriptor Set( ParticleDescriptor src )
		{
			return Set(
				src.Position, src.Velocity,
				src.Angle, src.AngularVelocity,
				src.U, src.V,
				src.AttackTime, src.AttackRadius,
				src.DecayTime, src.DecayRadius,
				src.SustainTime,
				src.ReleaseTime, src.ReleaseRadius );
		}
		

		public static final class ParticleDescriptorList
		{
			public final ParticleDescriptor[] Values = new ParticleDescriptor[256];
			public ParticleDescriptorList()
			{
				for( int i = 0 ; i < Values.length ; i++ )
				{
					Values[i] = new ParticleDescriptor();
				}
			}

			private int m_iPos = 0;
			public ParticleDescriptor Get()
			{
				if( m_iPos < Values.length )
				{
					m_iPos++;
					return Values[m_iPos-1];
				}
				else
				{
					m_iPos = 1;
					return Values[0];
				}
			}
		}

		private static final ParticleDescriptorList m_locals = new ParticleDescriptorList();

		public static ParticleDescriptor Local() { return m_locals.Get(); }
		public static ParticleDescriptor Local(
			Float3 f3Position, Float3 f3Velocity,
			float fAngle, float fAngularVelocity,
			int nU, int nV,
			float fAttackTime, float fAttackRadius,
			float fDecayTime, float fDecayRadius,
			float fSustainTime,
			float fReleaseTime, float fReleaseRadius )
			{
				return m_locals.Get().Set(
					f3Position, f3Velocity,
					fAngle, fAngularVelocity,
					nU, nV,
					fAttackTime, fAttackRadius,
					fDecayTime, fDecayRadius,
					fSustainTime,
					fReleaseTime, fReleaseRadius );
			}
	}
	
	public static class Particle
	{
		public boolean m_isBusy = false;
		public final Float3 m_f3Position = new Float3( 0.0f );
		public float m_fAngle = 0.0f;
		
		public float m_fAlpha = 0.0f;
		public float m_fRadius = 0.0f;
		
		public float m_fTime = 0.0f;

		public final ParticleDescriptor m_descriptor = new ParticleDescriptor();
		public float m_fU = 0.0f;
		public float m_fV = 0.0f;
		
		public void Spawn( ParticleDescriptor descriptor, float fUSize, float fVSize )
		{
			m_isBusy = true;
			m_descriptor.Set( descriptor );
			m_fU = fUSize*(float)descriptor.U;
			m_fV = fVSize*(float)descriptor.V;
			m_f3Position.Set( descriptor.Position );
			m_fAngle = descriptor.Angle;
			m_fAlpha = m_fRadius = 0.0f;
			m_fTime = 0.0f;
		}
	
		void Update( float fElapsedTime )
		{
			if( m_isBusy == false )
			{
				return;
			}
			final ParticleDescriptor d = m_descriptor;
			Float3.Mad( m_f3Position, d.Velocity, fElapsedTime, m_f3Position );
			m_fAngle += d.AngularVelocity*fElapsedTime;
			
			m_fTime += fElapsedTime;
			
			if( m_fTime < d.AttackTime )
			{
				m_fAlpha = m_fTime/d.AttackTime;
				m_fRadius = d.AttackRadius*m_fAlpha;
			}
			else if( m_fTime < (d.AttackTime + d.DecayTime) )
			{
				m_fAlpha = 1.0f;
				m_fRadius = Lerp(
					(m_fTime - d.AttackTime)/d.DecayTime,
					d.AttackRadius, d.DecayRadius );
			}
			else if( m_fTime < (d.AttackTime + d.DecayTime + d.SustainTime) )
			{
				m_fAlpha = 1.0f;
				m_fRadius = d.DecayRadius;
			}
			else if( m_fTime < (d.AttackTime + d.DecayTime + d.SustainTime + d.ReleaseTime) )
			{
				m_fAlpha = 1.0f - (m_fTime - d.AttackTime - d.DecayTime - d.SustainTime)/d.ReleaseTime;
									   
				m_fRadius = Lerp(
					(m_fTime - d.AttackTime - d.DecayTime - d.SustainTime)/d.ReleaseTime,
					d.DecayRadius, d.ReleaseRadius );
			}
			else
			{
				m_fAlpha = 0.0f;
				m_fRadius = d.ReleaseRadius;
				m_isBusy = false;
			}
		}
		
		float Lerp( float fLerp, float f0, float f1 )
		{
			return (1.0f-fLerp)*f0 + fLerp*f1;
		}
	}
	
	Particle[] m_particles = null;
	int m_iPosition = 0;
	
	VertexStream m_stream = null;
	
	float m_fUSize = 0.0f;
	float m_fVSize = 0.0f;
	
	public SimpleParticle
	(
		DelayResourceQueue drq, 
		//BasicRender basicRender,
		int nbParticles, 
		StaticTexture texture, 
		int nbUSplits, 
		int nbVSplits
	)
		//throws ThreadForceDestroyException
	{
		//m_basicRender = basicRender;
		m_texture = texture;
		m_samplerState = new SamplerState( EMagFilter.Linear, EMinFilter.LinearMipmapLinear, EWrap.Repeat, EWrap.Repeat );
		
		AttributeBinding[] ab = 
		{
			new AttributeBinding( 0, "a_positionIndex" ),
			new AttributeBinding( 1, "a_uvOffsetAlphaRadius" ),
			new AttributeBinding( 2, "a_angle" )
		};
		
		m_program = new Program( drq,
			ab,
			new VertexShader( drq, SubSystem.Loader.LoadTextFile( "simple_particle.vs" ) ),
			new FragmentShader( drq, SubSystem.Loader.LoadTextFile( "simple_particle.fs" ) )
		);
		
		u_worldView = new  Uniform( drq, m_program, "u_worldView" );
		u_projection = new  Uniform( drq, m_program, "u_projection" );
		u_targetSize = new  Uniform( drq, m_program, "u_targetSize" );
		
		u_texture = new Sampler( drq, m_program, 0, "u_texture" );
		
		m_particles = new Particle[nbParticles];
		for( int i = 0 ; i < nbParticles ; i++ )
		{
			m_particles[i] = new Particle();
		}
		
		m_iPosition = 0;
		
		VertexAttribute[] va =
		{
			new VertexAttribute( 0, 4, EVertexAttributeFormat.Float, false, 0 ),
			new VertexAttribute( 1, 4, EVertexAttributeFormat.Float, false, 16 ),
			new VertexAttribute( 2, 1, EVertexAttributeFormat.Float, false, 32 ),
		};
		
		m_stream = new VertexStream( va, 36 );
		
		m_fUSize = 1.0f/(float) nbUSplits;
		m_fVSize = 1.0f/(float) nbVSplits;
	}
	
	public void Spawn( ParticleDescriptor descriptor )
	{
		m_particles[m_iPosition].Spawn( descriptor, m_fUSize, m_fVSize );			
		m_iPosition = (m_iPosition + 1)% m_particles.length;
	}
	
	public void Update( float fElapsedTime )
	{
		for( int i= 0 ; i < m_particles.length ; i++ )
		{
			m_particles[i].Update( fElapsedTime );
		}
	}
	
	public final void Render(final RenderContext rc)
	{
		final GfxCommandContext r = rc.GetCommandContext();
		final MatrixCache mc = rc.GetMatrixCache();
		final FrameLinearVertexBuffer vb = rc.GetVertexBuffer();
		final FrameLinearIndexBuffer ib = rc.GetIndexBuffer();

			/*
		int nbParticles = 0;
		for( int i = 0 ; i < m_particles.length ; i++ )
		{
			if( m_particles[i].m_isBusy == false )
			{
				nbParticles++;
			}
		}
		
		if( nbParticles <= 0 )
		{
			return;
		}
		*/
		int nbIndices = 0;
		int nbVertices = 0;

		ib.Align(4);
		int indexOffset = ib.GetPosition();
		//ib.Begin();
		vb.Align(16);
		int vertexOffset = vb.GetPosition();
		//vb.Begin();

		for( int i = 0 ; i < m_particles.length ; i++ )
		{
			int ii = (m_iPosition+i)%m_particles.length;
			
			Particle particle = m_particles[ii];
			
			if( particle.m_isBusy == false )
			{
				continue;
			}
			
			for( int j = 0 ; j < 4 ; j++ )
			{
				vb.Add( particle.m_f3Position.X,
					particle.m_f3Position.Y,
					particle.m_f3Position.Z,
					(float) j );
					
				vb.Add( particle.m_fU, particle.m_fV,
					particle.m_fAlpha, particle.m_fRadius );
					
				vb.Add( particle.m_fAngle );
			}
			
			ib.Add( (short) (nbVertices + 0) );
			ib.Add( (short) (nbVertices + 1) );
			ib.Add( (short) (nbVertices + 2) );
			ib.Add( (short) (nbVertices + 0) );
			ib.Add( (short) (nbVertices + 2) );
			ib.Add( (short) (nbVertices + 3) );
			nbVertices += 4;
			nbIndices += 6;
		}
		
		//vb.End();
		//ib.End();
		
		if( nbIndices <= 0 )
		{
			return;
		}
		
		r.SetProgram( m_program );
	//	r.SetMatrix( u_worldViewProjection, mc.GetWorldViewProjection().Values );
		r.SetMatrix( u_worldView, mc.GetWorldView().Values );
		r.SetMatrix( u_projection, mc.GetProjection().Values );
		r.SetFloat2( u_targetSize, m_fUSize, m_fVSize );
		r.SetTexture( u_texture, m_texture );
		r.SetSamplerState( u_texture, m_samplerState );
		
		r.SetVertexBuffer( vb );
		r.EnableVertexStream( m_stream, vertexOffset );
		
		r.SetIndexBuffer( ib );
		//GLES20.glDrawElements( GLES20.GL_TRIANGLES, nbIndices, GLES20.GL_UNSIGNED_SHORT, nIndexOffset );
		r.DrawElements( EPrimitive.Triangles, nbIndices, EIndexFormat.UnsignedShort, indexOffset );

		//GLES20.glBindBuffer( GLES20.GL_ELEMENT_ARRAY_BUFFER, 0 );
		r.DisableVertexStream( m_stream );
		
		r.SetTexture( u_texture, null );
	}
}
