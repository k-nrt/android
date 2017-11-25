package com.nrt.render;

import android.opengl.GLES20;
import android.opengl.*;
import com.nrt.framework.*;

public class FrameBuffer extends RenderResource
{
	public int Width = 0;
	public int Height = 0;
	
	public RenderBuffer ColorRenderBuffer = null;
	public RenderTexture ColorRenderTexture = null;
	public RenderBuffer DepthRenderBuffer = null;
	public RenderBuffer StencilRenderBuffer = null;
	
	public FrameBuffer
	(
		DelayResourceQueue drq,
		RenderBuffer color,
		RenderBuffer depth, 
		RenderBuffer stencil
	)
	{
		Name = 0;
		
		ColorRenderBuffer = color;
		ColorRenderTexture = null;
		DepthRenderBuffer = depth;
		StencilRenderBuffer = stencil;
		
		if( color != null )
		{				
			Width = color.Width;
			Height = color.Height;
		}
		
		if( depth != null)
		{				
			Width = depth.Width;
			Height = depth.Height;
		}
		
		if( stencil != null )
		{				
			Width = stencil.Width;
			Height = stencil.Height;
		}	
		
		SubSystem.Log.WriteLine( String.format( "new FrameBuffer() %dx%d", Width, Height ) );
		
		drq.Add(this);
	}
	
	public FrameBuffer
	(
		DelayResourceQueue drq,
		RenderTexture color,
		RenderBuffer depth, 
		RenderBuffer stencil
	)
	{
		Name = 0;

		ColorRenderBuffer = null;
		ColorRenderTexture = color;
		DepthRenderBuffer = depth;
		StencilRenderBuffer = stencil;

		if( color != null )
		{				
			Width = color.PotWidth;
			Height = color.PotHeight;
		}

		if( depth != null )
		{				
			Width = depth.Width;
			Height = depth.Height;
		}

		if( stencil != null )
		{				
			Width = stencil.Width;
			Height = stencil.Height;
		}	
		
		SubSystem.Log.WriteLine( String.format( "new FrameBuffer() %dx%d", Width, Height ) );
		
		drq.Add(this);
	}

	@Override public void Generate()
	{
		DeleteFrameBuffer( Name, ColorRenderTexture, ColorRenderBuffer, DepthRenderBuffer, StencilRenderBuffer );
		
		Name = CreateFrameBuffer
		(
			ColorRenderTexture, 
			ColorRenderBuffer,
			DepthRenderBuffer, 
			StencilRenderBuffer
		);
		
		SubSystem.Log.WriteLine( String.format( "FrameBuffer.Apply() %d %dx%d", Name, Width, Height ) );
	}

	@Override public void Delete()
	{
		if( 0 < Name )
		{
			DeleteFrameBuffer( Name, ColorRenderTexture, ColorRenderBuffer, DepthRenderBuffer, StencilRenderBuffer );
		}
		Name = 0;
	}
	
	public void OnSurfaceChanged( int width, int height )
	{
		OnSurfaceChanged( width, height, true, true, true );
	}
	
	public void OnSurfaceChanged
	(
		int width, int height,
		boolean resizeColorBuffer,
		boolean resizeDepthBuffer,
		boolean resizeStencilBuffer
	)
	{
		DeleteFrameBuffer( Name, ColorRenderTexture, ColorRenderBuffer, DepthRenderBuffer, StencilRenderBuffer );
		Width = width;
		Height = height;
		
		if( resizeColorBuffer )
		{
			if(ColorRenderTexture!=null)
			{
				ColorRenderTexture.OnSurfaceChanged(width,height);
			
				width = ColorRenderTexture.PotWidth;
				height = ColorRenderTexture.PotHeight;
			}
		
			if(ColorRenderBuffer!=null)
			{
				ColorRenderBuffer.OnSurfaceChanged(width,height);
			}
		}
		
		if( resizeDepthBuffer )
		{
			if(DepthRenderBuffer!=null)
			{
				DepthRenderBuffer.OnSurfaceChanged(width,height);
			}
		}
		
		if( resizeStencilBuffer )
		{
			if(StencilRenderBuffer!=null)
			{
				StencilRenderBuffer.OnSurfaceChanged(width,height);
			}
		}
		
		Name = CreateFrameBuffer( ColorRenderTexture, ColorRenderBuffer, DepthRenderBuffer, StencilRenderBuffer );
		SubSystem.Log.WriteLine( String.format( "FrameBuffer.OnSurfaceChanged( %d, %d )", Width, Height ) );
	}
	
	protected static int CreateFrameBuffer( RenderTexture colorTexture, RenderBuffer color, RenderBuffer depth, RenderBuffer stencil )
	{
		int[] names = {0};
		GLES20.glGenFramebuffers( 1, names, 0 );

		GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, names[0] );

		if( colorTexture != null )
		{
			GLES20.glFramebufferTexture2D( 
				GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
				GLES20.GL_TEXTURE_2D, colorTexture.Name, 0 );
		}
		else if( color != null )
		{
			GLES20.glFramebufferRenderbuffer( 
				GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
				GLES20.GL_RENDERBUFFER, color.Name );
		}

		if( depth != null )
		{
			GLES20.glFramebufferRenderbuffer(
				GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
				GLES20.GL_RENDERBUFFER, depth.Name );
		}

		if( stencil != null )
		{
			GLES20.glFramebufferRenderbuffer(
				GLES20.GL_FRAMEBUFFER, GLES20.GL_STENCIL_ATTACHMENT,
				GLES20.GL_RENDERBUFFER, stencil.Name );
		}

		GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, 0 );
		
		return names[0];
	}
	
	protected static void DeleteFrameBuffer( int name, RenderTexture colorTexture, RenderBuffer color, RenderBuffer depth, RenderBuffer stencil )
	{		
		if( name <= 0 )
		{
			return;
		}
		
		GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, name );
		
		if( colorTexture != null )
		{
			GLES20.glFramebufferTexture2D( 
				GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
				GLES20.GL_TEXTURE_2D, 0, 0 );
		}
		else if( color != null )
		{
			GLES20.glFramebufferRenderbuffer( 
				GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
				GLES20.GL_RENDERBUFFER, 0 );
		}
		
		if( depth != null )
		{
			GLES20.glFramebufferRenderbuffer( 
				GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
				GLES20.GL_RENDERBUFFER, 0 );
		}
		
		if( stencil != null )
		{
			GLES20.glFramebufferRenderbuffer( 
				GLES20.GL_FRAMEBUFFER, GLES20.GL_STENCIL_ATTACHMENT,
				GLES20.GL_RENDERBUFFER, 0 );
		}
		GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, 0 );
		
		int[] names = {name};
		GLES20.glDeleteFramebuffers( 1, names, 0 );
	}
}
