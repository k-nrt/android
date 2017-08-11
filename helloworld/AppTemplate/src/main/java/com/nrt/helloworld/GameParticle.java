package com.nrt.helloworld;

import com.nrt.render.*;
import com.nrt.math.Float3;
import com.nrt.math.Float4x4;
import com.nrt.framework.*;


class GameParticle
{
	SimpleParticle m_particles0 = null;
	SimpleParticle m_particles1 = null;
	StaticTexture m_texture = null;

	public GameParticle( DelayResourceQueue drq )
		//throws ThreadForceDestroyException
	{
		//SubSystem.Loader.LoadPng( "smokes.png" );
		
		m_texture = new StaticTexture( drq, SubSystem.Loader.LoadPng( "smokes.png" ), true );
		//m_texture = new StaticTexture();
		//GameMain.PngTextureLoader l = new GameMain.PngTextureLoader(m_texture,"smokes.png");
		//SubSystem.Render.DelayResourceQueue.Add( SubSystem.Loader.LoadPng( "smokes.png" ) );
		//StaticTexture.Create( m_texture, l.m_rawImage );
		//StaticTexture.Create( m_texture, SubSystem.Loader.LoadPng( "smokes.png" ) );
		//SubSystem.Render.DelayResourceQueue.Add( new GameMain.PngTextureLoader(m_texture,"smokes.png"));
		
		m_particles0 = new SimpleParticle( drq, 128, m_texture, 4, 4 );
		m_particles1 = new SimpleParticle( drq, 128, m_texture, 4, 4 );
	}

	public void Update( float fElapsedTime )
	{
		m_particles0.Update( fElapsedTime );
		m_particles1.Update( fElapsedTime );
	}

	public void Render( final RenderContext rc )
	{
		final GfxCommandContext r = rc.GetCommandContext();
		final MatrixCache mc = rc.GetMatrixCache();

		/*
		if( m_texture.Name <= 0 )
		{
			return;
		}
		*/
		
		//Render r = SubSystem.Render;
		//MatrixCache mc = SubSystem.MatrixCache;
		mc.SetWorld( Float4x4.Identity() );
		
		//GLES20.glDepthMask( false );
		DepthStencilState depthEnableWriteDisable = new DepthStencilState(true,EDepthFunc.Less,false);
		r.SetDepthStencilState(depthEnableWriteDisable);

		//GLES20.glEnable( GLES20.GL_BLEND );
		//GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA );
		BlendState blendSrcAlphaOneMinusSrcAlpha = new BlendState(true,BlendFunc.SrcAlpha, BlendFunc.OneMinusSrcAlpha);
		r.SetBlendState(blendSrcAlphaOneMinusSrcAlpha);

		if( 0 < m_texture.Name )
		{
			m_particles0.Render(rc);

			//GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE );
			BlendState blendSrcAlphaOne = new BlendState(true,BlendFunc.SrcAlpha, BlendFunc.One);
			r.SetBlendState(blendSrcAlphaOne);

			m_particles1.Render(rc);
		}

		//GLES20.glDisable( GLES20.GL_BLEND );
		//GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA );
		BlendState blendDisableOneZero = new BlendState(false,BlendFunc.One,BlendFunc.Zero);
		r.SetBlendState(blendDisableOneZero);

		//GLES20.glDepthMask( true );
		DepthStencilState depthEnableLessWriteEnable = new DepthStencilState(true,EDepthFunc.Less,true);
		r.SetDepthStencilState(depthEnableLessWriteEnable);

	}
	

	public void SpawnSmoke( Float3 v3Position, Float3 v3Velocity, float fRadius )
	{
		/*
		if( System.Rand.Float() < 0.0f )
		{
			//return;
		}
		*/

		float r = SubSystem.Rand.Float( 0.5f, 1.5f )*fRadius;
		float t = SubSystem.Rand.Float( 0.8f, 2.0f );
		SimpleParticle.ParticleDescriptor desc = SimpleParticle.ParticleDescriptor.Local(
			v3Position, v3Velocity,
			SubSystem.Rand.Float()*(float)Math.PI, SubSystem.Rand.Float( (float)Math.PI*0.15f, (float)Math.PI*0.6f ),
			SubSystem.Rand.Integer( 0, 2 ), 0,
			0.1f*t, r,
			0.0f*t, r*1.2f,
			0.1f*t,
			1.5f*t,1.4f*r
		);

		m_particles0.Spawn( desc );

		//desc = Simpledesc.Clone();
		desc.AngularVelocity = - desc.AngularVelocity;
		desc.Angle = SubSystem.Rand.Float()*(float)Math.PI;
		m_particles0.Spawn( desc );
	}


	public void SpawnFlash( Float3 v3Position, Float3 v3Velocity, float fRadius )
	{
		float r = SubSystem.Rand.Float( 0.5f, 1.5f )*fRadius;
		float t = SubSystem.Rand.Float( 1.0f, 1.0f );

		SimpleParticle.ParticleDescriptor desc = SimpleParticle.ParticleDescriptor.Local(
			v3Position,	v3Velocity,
			SubSystem.Rand.Float()*(float)Math.PI, SubSystem.Rand.Float( (float)Math.PI*0.0f, (float)Math.PI*0.0f ),

			0, 2,
			0.1f*t, 1.1f*r,
			0.0f*t, 1.0f*r,
			0.0f*t, 
			0.2f*t, 2.0f*r
		);

		m_particles1.Spawn( desc );
		//particle.AngularVelocity = - particle.AngularVelocity;
		//particle.Angle = Rand.Float()*FMath.PI;
		//m_particles.Spawn( particle );
	}
	

	public void SpawnExplosion( Float3 v3Position, Float3 v3Velocity, float fRadius )
	{
		float r = SubSystem.Rand.Float( 0.5f, 1.5f )*fRadius;
		float t = SubSystem.Rand.Float( 1.0f, 1.2f );

		SimpleParticle.ParticleDescriptor desc = SimpleParticle.ParticleDescriptor.Local(
			v3Position, v3Velocity,
			SubSystem.Rand.Float()*(float)Math.PI, SubSystem.Rand.Float( (float)Math.PI*0.0f, (float)Math.PI*0.1f ),
			SubSystem.Rand.Integer( 0, 3 ), 1,
			0.1f*t, 0.1f*r,
			0.1f*t, 1.0f*r,
			0.0f*t, 
			0.8f*t, 1.2f*r
		);

		m_particles1.Spawn( desc );

		//desc = desc.Clone();
		desc.AngularVelocity = - desc.AngularVelocity;
		desc.Angle = SubSystem.Rand.Float()*(float)Math.PI;
		m_particles1.Spawn( desc );
	}
	

	public void SpawnSmallExplosion( Float3 v3Position, float fRadius )
	{
		fRadius *= 0.01f;
		//Vector3 v30 = v3Position + (Rand.Vector3()*2.0f - 1.0f)*50.0f*fRadius;
		//Vector3 v31 = v3Position + (Rand.Vector3()*2.0f - 1.0f)*50.0f*fRadius;
		Float3 v30 = Float3.Add( Float3.Local(), v3Position, Float3.Rand( -50.0f*fRadius, 50.0f*fRadius ) );
		Float3 v31 = Float3.Add( Float3.Local(), v3Position, Float3.Rand( -50.0f*fRadius, 50.0f*fRadius ) );
		

		SpawnSmoke( v30, Float3.Rand( -50.0f*fRadius, 50.0f*fRadius ), 120.0f*fRadius );
		SpawnSmoke( v31, Float3.Rand( -50.0f*fRadius, 50.0f*fRadius ), 120.0f*fRadius );
		SpawnSmoke( Float3.Add( Float3.Local(), v3Position, Float3.Rand( -50.0f*fRadius, 50.0f*fRadius )), Float3.Rand( -50.0f*fRadius, 50.0f*fRadius ), 120.0f*fRadius );
		SpawnSmoke( Float3.Add( Float3.Local(), v3Position, Float3.Rand( -50.0f*fRadius, 50.0f*fRadius )), Float3.Rand( -50.0f*fRadius, 50.0f*fRadius ), 120.0f*fRadius );

		SpawnFlash( v3Position, new Float3( 0.0f ), 1000.0f*fRadius );

		SpawnExplosion( v30, Float3.Rand( -20.0f*fRadius, 20.0f*fRadius ), 100.0f*fRadius );
		SpawnExplosion( v31, Float3.Rand( -20.0f*fRadius, 20.0f*fRadius ), 100.0f*fRadius );
	}
	
}

/*
using System;
using Sce.Pss.Core;
using Sce.Pss.Core.Graphics;

namespace Ssg
{
namespace Game
{
public class Particle
{
	SimpleParticles m_particles0 = null;
	SimpleParticles m_particles1 = null;

	public Particle ()
	{
		m_particles0 = new SimpleParticles( new Texture2D( "/Application/textures/smokes.png", true ) );
		m_particles1 = new SimpleParticles( m_particles0.Texture );
	}

	public void Update( float fElapsedTime )
	{
		m_particles0.Update( fElapsedTime );
		m_particles1.Update( fElapsedTime );
	}

	public void Render( ref GraphicsContext gc, ref ModelRender mr )
	{
		mr.SetWorld( Matrix4.Identity );
		gc.SetDepthFunc( DepthFuncMode.Less, false ); 
		gc.Enable( EnableMode.Blend );
//				gc.SetBlendFunc( new BlendFunc( BlendFuncMode.Add, BlendFuncFactor.SrcAlpha, BlendFuncFactor.One ) );
		gc.SetBlendFuncAlpha( BlendFuncMode.Add, BlendFuncFactor.One, BlendFuncFactor.Zero );


		gc.SetBlendFuncRgb( BlendFuncMode.Add, BlendFuncFactor.SrcAlpha, BlendFuncFactor.OneMinusSrcAlpha );
		m_particles0.Render( ref gc, ref mr );

		gc.SetBlendFuncRgb( BlendFuncMode.Add, BlendFuncFactor.SrcAlpha, BlendFuncFactor.One );
		m_particles1.Render( ref gc, ref mr );

		gc.Disable( EnableMode.Blend );

		gc.SetDepthFunc( DepthFuncMode.Less, true );
	}

	public void SpawnSmallExplosion( Vector3 v3Position, float fRadius )
	{
		fRadius *= 0.01f;
		Vector3 v30 = v3Position + (Rand.Vector3()*2.0f - 1.0f)*50.0f*fRadius;
		Vector3 v31 = v3Position + (Rand.Vector3()*2.0f - 1.0f)*50.0f*fRadius;

		SpawnSmoke( v30, (Rand.Vector3()*2.0f - 1.0f)*50.0f*fRadius, 120.0f*fRadius );
		SpawnSmoke( v31, (Rand.Vector3()*2.0f - 1.0f)*50.0f*fRadius, 120.0f*fRadius );
		SpawnSmoke( v3Position + (Rand.Vector3()*2.0f - 1.0f)*50.0f*fRadius, (Rand.Vector3()*2.0f - 1.0f)*50.0f*fRadius, 120.0f*fRadius );
		SpawnSmoke( v3Position + (Rand.Vector3()*2.0f - 1.0f)*50.0f*fRadius, (Rand.Vector3()*2.0f - 1.0f)*50.0f*fRadius, 120.0f*fRadius );

		SpawnFlash( v3Position, Vector3.Zero, 1000.0f*fRadius );

		SpawnExplosion( v30, (Rand.Vector3()*2.0f - 1.0f)*20.0f*fRadius, 100.0f*fRadius );
		SpawnExplosion( v31, (Rand.Vector3()*2.0f - 1.0f)*20.0f*fRadius, 100.0f*fRadius );
	}

	public void SpawnSmoke( Vector3 v3Position, Vector3 v3Velocity, float fRadius )
	{
		if( Rand.Float() < 0.0f )
		{
			//return;
		}

		float r = Rand.Float( 0.5f, 1.5f )*fRadius;
		float t = Rand.Float( 0.8f, 2.0f );
		SimpleParticles.Particle particle = new SimpleParticles.Particle(
			Rand.Integer( 0, 2 ), 0,
			v3Position,
			v3Velocity,
			Rand.Float()*FMath.PI, Rand.Float( FMath.PI*0.15f, FMath.PI*0.3f ),
			0.1f*t, r,1.0f,
			0.0f*t,
			0.1f*t,1.2f*r,1.0f,
			1.5f*t,1.4f*r,0.0f
		);

		m_particles0.Spawn( particle );

		particle.AngularVelocity = - particle.AngularVelocity;
		particle.Angle = Rand.Float()*FMath.PI;
		m_particles0.Spawn( particle );
	}

	public void SpawnExplosion( Vector3 v3Position, Vector3 v3Velocity, float fRadius )
	{
		float r = Rand.Float( 0.5f, 1.5f )*fRadius;
		float t = Rand.Float( 1.0f, 1.2f );

		SimpleParticles.Particle particle = new SimpleParticles.Particle(
			Rand.Integer( 0, 3 ), 1,
			v3Position,
			v3Velocity,
			Rand.Float()*FMath.PI, Rand.Float( FMath.PI*0.0f, FMath.PI*0.1f ),
			0.1f*t, 0.1f*r,1.0f,
			0.1f*t,
			0.0f*t, 1.0f*r,1.0f,
			0.8f*t, 1.2f*r,0.0f
		);

		m_particles1.Spawn( particle );

		particle.AngularVelocity = - particle.AngularVelocity;
		particle.Angle = Rand.Float()*FMath.PI;
		m_particles1.Spawn( particle );
	}

	public void SpawnFlash( Vector3 v3Position, Vector3 v3Velocity, float fRadius )
	{
		float r = Rand.Float( 0.5f, 1.5f )*fRadius;
		float t = Rand.Float( 1.0f, 1.0f );

		SimpleParticles.Particle particle = new SimpleParticles.Particle(
			0, 2,
			v3Position,
			v3Velocity,
			Rand.Float()*FMath.PI, Rand.Float( FMath.PI*0.0f, FMath.PI*0.0f ),
			0.1f*t, 1.1f*r, 1.0f,
			0.0f*t,
			0.0f*t, 1.0f*r, 1.0f,
			0.2f*t, 2.0f*r, 0.0f
		);

		m_particles1.Spawn( particle );

		//particle.AngularVelocity = - particle.AngularVelocity;
		//particle.Angle = Rand.Float()*FMath.PI;
		//m_particles.Spawn( particle );
	}
	}
	}
}

*/
