package com.nrt.helloworld;

import java.nio.Buffer; 
import java.nio.ByteBuffer; 
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.*;
import android.util.Log;
import java.util.*;

import android.graphics.Bitmap;
import android.graphics.*;
import java.nio.charset.*;
import android.util.*;

import com.nrt.font.*;
import com.nrt.render.*;
/*
class GameHudFont
{
	public FontCharInfo[] m_infos = null;
	public StaticTexture m_texture = null;

	public Program m_program = null;
	public Uniform u_worldViewProjection = null;
	public Sampler u_sampler0 = null;

	public VertexStream m_stream = null;
	
	public BasicRender m_basicRender = null;
	
	public float m_fSize = 8.0f;
	
	public int m_rgbaFont = 0xffffffff;
	
	public int m_nIndexOffset = 0;
	public int m_nbVertices = 0;
	
	public static class Pattern
	{
		public String Targets = "";
		public String[] Image = null;

		public Pattern( String targets, String[] image )
		{
			Targets = targets;
			Image = image;
		}
	}
	
	public GameHudFont( DelayResourceQueue drq, Pattern[] patterns, BasicRender basicRender )
	throws ThreadForceDestroyException
	{
		int nTextureSize = 128;
		
		ByteBuffer bufferFont = ByteBuffer.allocateDirect( nTextureSize*nTextureSize*4 );
		
		
		for( Pattern pattern : patterns )
		{
			for( int i = 0 ; i < pattern.Targets.length() ; i++ )
			{
			int c =  pattern.Targets.charAt(i);
			int ox = (c%16)*8;
			int oy = (c/16)*8;

			for( int y = 0 ; y < 8 ; y++ )
			{
				for( int x = 0 ; x < 6 ; x++ )
				{
					if( pattern.Image[y].charAt( x + i*6 ) == '-' )
					{
						bufferFont.putInt( ((ox+x)+(oy+y)*nTextureSize)*4,  0x0000 );
					}
					else
					{
						bufferFont.putInt( ((ox+x)+(oy+y)*nTextureSize)*4,  0xffffffff );
					}
				}
			}
			}

		}
		
		//m_texture = new StaticTexture( m_bitmap );
		
		m_texture = new StaticTexture(
			drq,
			GLES20.GL_RGBA, nTextureSize, nTextureSize,
			GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, bufferFont, false );
		
		
		
//		m_texture = new StaticTexture( m_bitmap );
//		GLES20.glBindTexture( GLES20.GL_TEXTURE_2D, m_texture.Name );
//		GLES20.glTexSubImage2D(  GLES20.GL_TEXTURE_2D, 0, 0, 0, nTextureSize, nTextureSize, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_SHORT_4_4_4_4, bufferFont );

		String[] strVsColorTexture = 
		{
			"uniform mat4 u_worldViewProjection;",
			"attribute vec3 a_position;",
			"attribute vec4 a_color;",
			"attribute vec2 a_texcoord;",
			"varying vec4 v_color;",
			"varying vec2 v_texcoord;",
			"void main(){",
			"gl_Position = u_worldViewProjection*vec4(a_position,1.0);",
			"v_color = a_color.xyzw;",
			"v_texcoord = a_texcoord;",
			"}",
		};

		String[] strFsColorTexture =
		{
			"precision mediump float;",
			"varying vec4 v_color;",
			"varying vec2 v_texcoord;",
			"uniform sampler2D u_texture0;",
			"void main(){",
			"gl_FragColor = texture2D( u_texture0, v_texcoord )*v_color;",
			"}",
		};

		AttributeBinding[] abColorTextures =
		{
			new AttributeBinding(0, "a_position"),
			new AttributeBinding(1, "a_color"),
			new AttributeBinding(2, "a_texcoord"),
		};


		m_program = new Program( drq,
			abColorTextures,
			new VertexShader( drq, strVsColorTexture),
			new FragmentShader( drq, strFsColorTexture));

		u_worldViewProjection
			= new Uniform(drq, m_program, "u_worldViewProjection");
		u_sampler0 = new Sampler(drq,m_program, 0, "u_texture0");
		
		

		VertexAttribute[] va = 
		{
			new VertexAttribute( 0, 3, GLES20.GL_FLOAT, false, 0 ),
			new VertexAttribute( 1, 4, GLES20.GL_UNSIGNED_BYTE, true, 12 ),
			new VertexAttribute( 2, 2, GLES20.GL_FLOAT, false, 16 ),
		};

		m_stream = new VertexStream( va, 0 );
		
		
		
		m_basicRender = basicRender;
		
	}
	
	public void SetSize( float size )
	{
		m_fSize = size;
	}
	
	public void SetColor( int rgba )
	{
		m_rgbaFont = rgba;

//			((rgba >> 24) & 0xff)|
//			((rgba >> 8) & 0xff00)|
//			((rgba << 8) & 0xff0000)|
//			((rgba << 24) & 0xff000000);
			
	}
	
	public void Begin()
	{
		BasicRender br = m_basicRender;
		br.GetVertexBuffer().Begin();
		br.GetIndexBuffer().Begin();
		m_nbVertices = 0;
		m_nIndexOffset = 0;
	}
	
	public void Draw( float x, float y, float z, String strText )
	{
		SetVertices( x, y, z, strText, m_rgbaFont );
	}
	
	void SetVertices( float fX, float fY, float fZ, String strText, int rgba )
	{
		RingVertexBuffer vb = m_basicRender.GetVertexBuffer();
		RingIndexBuffer ib = m_basicRender.GetIndexBuffer();

		float xsize = (m_fSize*6.0f)/8.0f;
		float ysize = m_fSize;
		float usize = 6.0f/128.0f;
		float vsize = 8.0f/128.0f;
		float x = fX;

		for( int i = 0 ; i < strText.length() ; i++ )
		{
			int c = strText.charAt( i );
			float u = ((float)((c%16)*8))/128.0f;
			float v = ((float)((c/16)*8))/128.0f;
			

			vb.Add( x, fY, fZ );
			vb.Add( rgba );
			vb.Add( u, v );

			vb.Add( x, fY + ysize, fZ );
			vb.Add( rgba );
			vb.Add( u, v+ vsize );

			vb.Add( x + xsize, fY, fZ );
			vb.Add( rgba );
			vb.Add( u + usize, v );

			vb.Add( x + xsize, fY + ysize, fZ );
			vb.Add( rgba );
			vb.Add( u + usize, v + vsize );

			x += xsize;

			ib.Add( (short) (0 + m_nIndexOffset) );
			ib.Add( (short) (1 + m_nIndexOffset) );
			ib.Add( (short) (2 + m_nIndexOffset) );
			ib.Add( (short) (2 + m_nIndexOffset) );
			ib.Add( (short) (1 + m_nIndexOffset) );
			ib.Add( (short) (3 + m_nIndexOffset) );

			m_nbVertices += 6;
			m_nIndexOffset += 4;
		}
		
	}
	
	public void End()
	{
		int nVertexOffset = m_basicRender.GetVertexBuffer().End();
		int nIndexOffset = m_basicRender.GetIndexBuffer().End();

		if( m_nbVertices <= 0 )
		{
			return;
		}

		Render r = m_basicRender.GetRender();

		GLES20.glEnable( GLES20.GL_BLEND );
		GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA );
		//GLES20.glDepthFunc( GLES20.GL_LEQUAL );
		//GLES20.glDisable( GLES20.GL_DEPTH_TEST );
		//GLES20.glDisable( GLES20.GL_CULL_FACE );

		r.SetProgram( m_program );

		r.SetMatrix( u_worldViewProjection, m_basicRender.GetMatrixCache().GetWorldViewProjection().Values );
	
		r.SetTexture( u_sampler0, m_texture );
		r.SetVertexBuffer( m_basicRender.GetVertexBuffer() );
		r.EnableVertexStream( m_stream, nVertexOffset );

		r.SetIndexBuffer( m_basicRender.GetIndexBuffer() );
		GLES20.glDrawElements( GLES20.GL_TRIANGLES, m_nbVertices, GLES20.GL_UNSIGNED_SHORT, nIndexOffset );

		r.DisableVertexStream( m_stream );
		GLES20.glBindBuffer( GLES20.GL_ELEMENT_ARRAY_BUFFER, 0 );
		
	}
	
}
*/
