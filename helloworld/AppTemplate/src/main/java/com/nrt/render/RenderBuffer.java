package com.nrt.render;
import android.opengl.GLES20;

import com.nrt.framework.SubSystem;

public class RenderBuffer extends RenderResource
{
	public ERenderBufferFormat m_renderBufferFormat = ERenderBufferFormat.RGBA4;
	public int Width = 0;
	public int Height = 0;
	
	public RenderBuffer(DelayResourceQueue drq, ERenderBufferFormat eFormat, int width, int height )
	{
		m_renderBufferFormat = eFormat;
		Name = 0;//CreateRenderBuffer( eFormat, width, height );		
		Width = width;
		Height = height;

		if( drq != null )
		{
			drq.Add(this);
		}
		else
		{
			Generate();
		}
	}

	@Override public void Generate()
	{
		DeleteRenderBuffer(Name);
		Name = CreateRenderBuffer( m_renderBufferFormat, Width, Height );
		SubSystem.Log.WriteLine( String.format( "RenderBuffer.Apply() %d %s %dx%d", Name, m_renderBufferFormat.toString(),  Width, Height ) );
	}

	@Override public void Delete()
	{
		if( 0 < Name )
		{
			DeleteRenderBuffer(Name);
		}
		Name = 0;
	}
	
	
	
	
	public void OnSurfaceChanged( int width, int height )
	{
		DeleteRenderBuffer( Name );		
		Name = CreateRenderBuffer( m_renderBufferFormat, width, height );
		Width = width;
		Height = height;
	}
	
	protected static int CreateRenderBuffer(ERenderBufferFormat eFormat, int width, int height )
	{
		int[] names = {0};
		GLES20.glGenRenderbuffers( 1, names, 0 );
		
		GLES20.glBindRenderbuffer( GLES20.GL_RENDERBUFFER, names[0] );
		GLES20.glRenderbufferStorage( 
			GLES20.GL_RENDERBUFFER, 
			eFormat.Value,
			width, height );

		GLES20.glBindRenderbuffer( GLES20.GL_RENDERBUFFER, 0 );
		
		return names[0];
	}
	
	protected static void DeleteRenderBuffer( int name )
	{
		if( name <= 0 )
		{
			return;
		}
		
		GLES20.glBindRenderbuffer( GLES20.GL_RENDERBUFFER, 0 );
		
		int[] names = {name};
		GLES20.glDeleteRenderbuffers( 1, names, 0 );
		
	}
}

