package com.nrt.render;

import com.nrt.math.*;
//import android.opengl.*;
import java.nio.*;
import com.nrt.framework.*;
import org.apache.http.*;

public class OcclusionQuery
{
	public static class Point
	{
		public final Float3 m_f3WorldPosition = new Float3();
		public final Float3 m_f3ScreenPosition = new Float3();
		public boolean Visiblity = false;
		
		public int OcclusionCount = 0;
		
		public void Update( Float3 f3WorldPosition )
		{
			m_f3WorldPosition.Set( f3WorldPosition );
		}
		
		public void Project(Float4x4 f4x4ViewProjection, float fPointSize, float fWidth, float fHeight )
		{
			Float4 f4HPos = Float4x4.MulXYZ1( Float4.Local(), m_f3WorldPosition, f4x4ViewProjection );

			if( f4HPos.W == 0.0f )
			{
				Visiblity = false;
				return;
			}
			
			float rcpw = 1.0f/f4HPos.W;
			Float4.Mul( f4HPos, f4HPos, rcpw );
			if( f4HPos.Z < -1.0f || 1.0f < f4HPos.Z )
			{
				Visiblity = false;
				return;
			}
			
			float fPSizeX = fPointSize*0.5f/fWidth;
			float fPSizeY = fPointSize*0.5f/fHeight;
			
			if( f4HPos.X < -(1.0f+fPSizeX) || (1.0f+fPSizeX) < f4HPos.X ||
				f4HPos.Y < -(1.0f+fPSizeY) || (1.0f+fPSizeY) < f4HPos.Y )

			{
				Visiblity = false;	
				return;
			}
			
			m_f3ScreenPosition.Set
			(
				(f4HPos.X*0.5f+0.5f)*fWidth,
				(f4HPos.Y*0.5f+0.5f)*fHeight,
				f4HPos.Z
			);			
			Visiblity = true;
		}
	}

	//public VertexShader m_vertexShader = null;
	//public FragmentShader m_fragmentShader = null;
	
	public Program m_program = null;
	public Uniform u_matrixWorldViewProjection = null;
	public Uniform u_fPointSize = null;
	public Uniform u_f4Color = null;
	
	public VertexStream m_vertexStream = null;

	//public RingVertexBuffer m_ringVertexBuffer = null;
	
	public Point[] m_points = null;
	
	public float PointSize = 2;
	
	public ByteBuffer m_bufferPixels = null;
	public int BufferWidth = 0;
	public int BufferHeight = 0;
	
	//public FrameBuffer FrameBuffer = null;
	
	public OcclusionQuery( DelayResourceQueue drq, int nbMaxPoints )//, FrameBuffer frameBuffer )
	{
		m_program = new Program
		(
			drq,
			new AttributeBinding[]
			{
				new AttributeBinding( 0, "a_v3Position" ),
			}
			,
			new VertexShader
			(
				drq,
				new  String[]
				{
					"attribute vec3 a_v3Position;",
					"uniform mat4 u_matrixWorldViewProjection;",
        			"uniform float u_fPointSize;",
					"void main()",
					"{",
       		  	 		"gl_Position  = u_matrixWorldViewProjection*vec4(a_v3Position,1.0);",
         			   	"gl_PointSize = u_fPointSize;",
					"}",
				}
			),
			new FragmentShader
			(
				drq,
				new String[]
				{
					"precision mediump float;",
					"uniform vec4 u_f4Color;",
					"void main()",
					"{",
						"gl_FragColor = u_f4Color;",
					"}",
				}
			)
		);
		
		u_matrixWorldViewProjection = new Uniform( drq, m_program, "u_matrixWorldViewProjection" );
		u_fPointSize = new Uniform( drq, m_program, "u_fPointSize" );
		u_f4Color = new Uniform( drq, m_program, "u_f4Color" );
		
		m_vertexStream = new VertexStream
		(
			new VertexAttribute[]
			{
				new VertexAttribute( 0, 3, EVertexAttributeFormat.Float, false, 0 ),
			},
			12
		);
			
		//m_ringVertexBuffer = new RingVertexBuffer( drq, m_vertexStream.Stride*nbMaxPoints*4 );
		
		m_points = new Point[nbMaxPoints];
		for( int i = 0 ; i < m_points.length ; i++ )
		{
			m_points[i] = new Point();
		}
		
		m_bufferPixels = ByteBuffer.allocateDirect( ((int)PointSize*(int)PointSize)*4 );
		//m_bufferPixels = ByteBuffer.allocateDirect( width*height );
		//BufferWidth = width;
		//BufferHeight = height;
		
		/*
		FrameBuffer = new FrameBuffer( drq,
			new RenderTexture( drq, RenderTextureFormat.RGBA, frameBuffer.Width, frameBuffer.Height ),
			frameBuffer.DepthRenderBuffer, null );
		*/
	}
	
	/*
	public void OnSurfaceChanged( FrameBuffer frameBuffer )
	{
		FrameBuffer.OnSurfaceChanged( frameBuffer.Width, frameBuffer.Height, true, false, false );
		
		//m_bufferPixels = ByteBuffer.allocateDirect( width*height );
		//BufferWidth = width;
		//BufferHeight = height;
	}
	*/
	public void Update( int i, Float3 f3WorldPosition )
	{
		m_points[i].Update( f3WorldPosition );
	}
	
	public void RenderPointsToFrameBuffer( GfxCommandContext r, MatrixCache mc, FrameLinearVertexBuffer vb, FrameBuffer frameBuffer )
	{
		float fWidth = frameBuffer.Width;
		float fHeight = frameBuffer.Height;

		vb.Align(16);
		int vertexOffset = vb.GetPosition();
		int nbVertices = 0;
		//vb.Begin();
		for( Point point : m_points )
		{
			point.Project( mc.GetWorldViewProjection(), PointSize, fWidth, fHeight );
			if( point.Visiblity )
			{
				vb.Add
				(
					point.m_f3WorldPosition.X,
					point.m_f3WorldPosition.Y,
					point.m_f3WorldPosition.Z
				);
				nbVertices++;
			}
		}
		//vb.End();
		
		//r.SetFrameBuffer( FrameBuffer );
		//GLES20.glViewport( 0, 0, FrameBuffer.Width, FrameBuffer.Height );
		//GLES20.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f );
		//GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT );
		
		if( nbVertices <= 0 )
		{
			return;
		}

		//GLES20.glColorMask( true, false, false, true );
		BlendState blendDisableMaskA = new BlendState(false,BlendFunc.One,BlendFunc.Zero,false,false,false,true);
		r.SetBlendState(blendDisableMaskA);


		//GLES20.glEnable( GLES20.GL_DEPTH_TEST );
		//GLES20.glDepthMask( false );
		DepthStencilState depthEnableWriteDisable = new DepthStencilState(true,EDepthFunc.Less,false);
		r.SetDepthStencilState(depthEnableWriteDisable);

		r.SetProgram( m_program );
		r.SetMatrix( u_matrixWorldViewProjection, mc.GetWorldViewProjection().Values );
		r.SetFloat( u_fPointSize, PointSize );
		r.SetFloat4( u_f4Color, 1.0f, 1.0f, 1.0f, 1.0f );

		r.SetVertexBuffer( vb );
		r.EnableVertexStream( m_vertexStream, vertexOffset );

		r.DrawArrays( EPrimitive.Points, 0, nbVertices );

		r.DisableVertexStream(m_vertexStream);
		
		//GLES20.glColorMask( true, true, true, true );
		BlendState blendDisableWriteRGBA = new BlendState(false,BlendFunc.One,BlendFunc.Zero,true,true,true,true);
		r.SetBlendState(blendDisableWriteRGBA);
	}
}

