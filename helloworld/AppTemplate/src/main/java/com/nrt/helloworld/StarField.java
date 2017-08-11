package com.nrt.helloworld;

import java.util.Random;
//import android.opengl.GLES20;

import com.nrt.render.*;
import com.nrt.math.Float4x4;
import com.nrt.framework.SubSystem;
import java.nio.*;


public class StarField
{
	private Program m_program = null;
	private VertexStream m_streamLines = null;
	private StaticVertexBuffer m_vbLines = null;
	private StaticIndexBuffer m_ibLines = null;
	private Uniform u_worldViews = null;
	private Uniform u_projections = null;
	private Uniform u_viewInverse = null;
	private Uniform u_fogNear = null;
	private Uniform u_fogFar = null;
	private Uniform u_fIntensities = null;
	private EIndexFormat m_eLineIndexFormat = EIndexFormat.UnsignedShort;
	private int m_nbLineIndices = 0;
	
	private Program m_programDot = null;
	private VertexStream m_streamDots = null;
	private StaticVertexBuffer m_vbDots = null;
	private Uniform u_viewInverseDot = null;
	private Uniform u_worldViewProjectionDot = null;
	private Uniform u_fogNearDot = null;
	private Uniform u_fogFarDot = null;
	
	

	private final int m_nbStars = 1024;
	private final int m_nbHistories = 8;
	private float[] m_projections = new float[m_nbHistories * 16];
	private float[] m_worldViews = new float[m_nbHistories * 16];
	
	private float[] m_intensities = new float[m_nbHistories];
	private float[] m_elapsedTimes = new float[m_nbHistories];
	
	public StarField( DelayResourceQueue drq )
		//throws ThreadForceDestroyException
	{
		// 頂点シェーダーのコンパイル
		VertexShader vs = new VertexShader( drq, SubSystem.Loader.LoadTextFile( "star_field.vs" ));
		VertexShader vsDot = new VertexShader( drq, SubSystem.Loader.LoadTextFile( "star_field_dot.vs" ));
		
		// フラグメントシェーダーのコンパイル
	
		FragmentShader fs = new FragmentShader( drq, SubSystem.Loader.LoadTextFile( "star_field.fs" ) );

		{
			AttributeBinding[] ab = 
			{
				new AttributeBinding(0, "a_pos"),
				new AttributeBinding(1, "a_index"),
			};
			m_program = new Program( drq, ab, vs, fs);

			u_worldViews = new Uniform( drq, m_program, "u_worldViews");
			u_projections = new Uniform( drq, m_program, "u_projections");
			u_viewInverse = new Uniform( drq, m_program, "u_viewInverse" );
			u_fogNear = new Uniform( drq, m_program, "u_fogNear" );
			u_fogFar = new Uniform( drq, m_program, "u_fogFar" );
			u_fIntensities = new Uniform( drq, m_program, "u_fIntensities" );
		}

		{
			AttributeBinding[] ab = 
			{
				new AttributeBinding(0, "a_pos"),
				//new AttributeBinding(1, "a_index"),
			};
			m_programDot = new Program( drq, ab, vsDot, fs);

			u_worldViewProjectionDot = new Uniform(drq, m_programDot, "u_worldViewProjection");
			u_viewInverseDot = new Uniform( drq,m_programDot, "u_viewInverse" );
			u_fogNearDot = new Uniform( drq, m_programDot, "u_fogNear" );
			u_fogFarDot = new Uniform( drq, m_programDot, "u_fogFar" );
			
		}
		
		
		Random rand = new Random();

		//float[] vertices = new float[m_nbStars * m_nbHistories * 4];
		//float[] dotVertices = new float[m_nbStars*3];
		//short[] indices = new short[m_nbStars * (m_nbHistories - 1) * 2];
		int nbVertices = m_nbStars * m_nbHistories;
		int nbStride = 16;

		m_eLineIndexFormat = EIndexFormat.UnsignedShort;
		int nbIndices = m_nbLineIndices = m_nbStars * (m_nbHistories - 1) * 2;
		ByteBuffer bufferVertices = ByteBuffer.allocateDirect(nbVertices*nbStride);
		bufferVertices.order(ByteOrder.nativeOrder());
		ByteBuffer bufferDots = ByteBuffer.allocateDirect(m_nbStars*3*4);
		bufferDots.order(ByteOrder.nativeOrder());
		
		ByteBuffer bufferIndices = ByteBuffer.allocateDirect(nbIndices*2);
		bufferIndices.order(ByteOrder.nativeOrder());
		
		for (int i = 0 ; i < m_nbStars ; i++)
		{
			float min = -1.0f;
			float max = 1.0f;
			float r = max - min;

			float x = rand.nextFloat() * r + min;
			float y = rand.nextFloat() * r + min;
			float z = rand.nextFloat() * r + min;

			int start = (i*m_nbHistories);
			
			for (int j = 0 ; j < m_nbHistories ; j++)
			{
				bufferVertices.putFloat( x );
				bufferVertices.putFloat( y );
				bufferVertices.putFloat( z );
				bufferVertices.putFloat( j );
			}
			
			for( int j = 0 ; j < (m_nbHistories-1) ; j++ )
			{
				bufferIndices.putShort((short) (start + j));
				bufferIndices.putShort((short) (start + j+1));
			}
			
			bufferDots.putFloat( x );
			bufferDots.putFloat( y );
			bufferDots.putFloat( z );			
		}

		bufferVertices.position(0);
		bufferIndices.position(0);
		bufferDots.position(0);
		
		{
			VertexAttribute[] va =
			{
				new VertexAttribute(0, 3, EVertexAttributeFormat.Float, false, 0),
				new VertexAttribute(1, 1, EVertexAttributeFormat.Float, false, 12),
			};
			m_streamLines = new VertexStream( va, 0 );
				
			m_vbLines = new StaticVertexBuffer(drq, bufferVertices );
			m_ibLines = new StaticIndexBuffer( drq, bufferIndices);
		}
		
		{
			VertexAttribute[] va =
			{
				new VertexAttribute(0, 3, EVertexAttributeFormat.Float, false, 0),
			};

			m_streamDots = new VertexStream( va, 0 );
			m_vbDots = new StaticVertexBuffer( drq, bufferDots );
		}
		
	}
	
	void Update( float fElapsedTime, float[] worldView, float[] projection )
	{
		for( int i = (m_nbHistories-2) ; i >= 0 ; i-- )
		{
			for( int j = 0 ; j < 16 ; j++ )
			{
				m_worldViews[(i+1)*16+j] = m_worldViews[i*16+j];
				m_projections[(i+1)*16+j] = m_projections[i*16+j];
			}
			m_elapsedTimes[i+1] = m_elapsedTimes[i];
		}

		for( int i = 0 ; i < 16 ; i++ )
		{
			m_worldViews[i] = worldView[i];
			m_projections[i] = projection[i];
		}		
		m_elapsedTimes[0] = fElapsedTime;
		
		float fTimeOut = 4.0f/60.0f;
		float fTime = 0.0f;
		for( int i = 0 ; i < m_nbHistories ; i++ )
		{
			m_intensities[i] = 1.0f - (fTime/fTimeOut);
			fTime += m_elapsedTimes[i];
		}
	}

	void Render( final GfxCommandContext r, final MatrixCache mc )
	{
		//Render r = basicRender.GetRender();
		//MatrixCache mc = basicRender.GetMatrixCache();
		Float4x4 matrixViewInverse = mc.GetViewInverse();
		Float4x4 matrixWorldViewProjection = mc.GetWorldViewProjection();
		//Float4x4 matrixProjection )
		
		float fFogNear = 100.0f;
		float fFogFar = 1000.0f;
		
		//Float4 f4Near = Float4x4.Mul( Float4.Local(), Float4.Local( 0.0f, 0.0f, -fFogNear, 1.0f ), matrixProjection );
		//Float4 f4Far = Float4x4.Mul( Float4.Local(), Float4.Local( 0.0f, 0.0f, -fFogFar, 1.0f ), matrixProjection );

        BlendState blendSrcAlphaOne = new BlendState(true, BlendFunc.SrcAlpha,BlendFunc.One);
		r.SetBlendState(blendSrcAlphaOne);
		
		//GLES20.glEnable( GLES20.GL_BLEND );
		//GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE );
		
		r.SetProgram(m_program);
		r.SetMatrixArray(u_projections, m_nbHistories, m_projections, 0);
		r.SetMatrixArray(u_worldViews, m_nbHistories, m_worldViews, 0);
		
		r.SetMatrix(u_viewInverse, matrixViewInverse.Values );
		
		r.SetFloatArray(u_fIntensities,m_nbHistories,m_intensities,0);
		
		r.SetFloat( u_fogNear, fFogNear );
		r.SetFloat( u_fogFar, fFogFar);
		//r.SetMatrix(u_projections, m_projections );
		//r.SetMatrix(u_worldViews, m_worldViews );
		r.SetVertexBuffer(m_vbLines);
		r.EnableVertexStream(m_streamLines,0);
		r.SetIndexBuffer(m_ibLines);
		
		r.DrawElements( EPrimitive.Lines, m_nbLineIndices, m_eLineIndexFormat, 0 );
		
		
		r.SetProgram(m_programDot);
		r.SetMatrix( u_viewInverseDot, matrixViewInverse.Values );
		r.SetMatrix( u_worldViewProjectionDot, matrixWorldViewProjection.Values );
		r.SetFloat( u_fogNearDot, fFogNear );
		r.SetFloat( u_fogFarDot, fFogFar);
		
		r.SetVertexBuffer( m_vbDots );
		r.EnableVertexStream( m_streamDots,0 );
		r.DrawArrays( EPrimitive.Points, 0, m_nbStars );

		//GLES20.glDisable( GLES20.GL_BLEND );

        BlendState blendDisable= new BlendState(false,BlendFunc.One,BlendFunc.Zero);
        r.SetBlendState(blendDisable);
		r.DisableVertexStream( m_streamDots );
		//.
		//GLES20.glEnable( GLES20.GL_VERTEX_PROGRAM_POINT_SIZE );

		//GLES20.glDrawArrays(GLES20.GL_POINTS, 0, m_vbLines.Vertices);
	}
}

