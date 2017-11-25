package com.nrt.render;

import java.nio.Buffer;
import android.opengl.*;
import android.graphics.Bitmap;

import com.nrt.basic.RawImage;
//import android.media.effect.*;
import com.nrt.framework.*;
import java.nio.*;

public class StaticTexture extends RenderResource implements Texture
{
	@Override
	public int GetTextureName()
	{
		// TODO: Implement this method
		return Name;
	}

	@Override
	public int GetPotWidth()
	{
		// TODO: Implement this method
		return PotWidth;
	}

	@Override
	public int GetPotHeight()
	{
		// TODO: Implement this method
		return PotHeight;
	}

	@Override
	public int GetNpotWidth()
	{
		// TODO: Implement this method
		return 0;
	}

	@Override
	public int GetNpotHeight()
	{
		// TODO: Implement this method
		return 0;
	}
	
	public int m_internalFormat = 0;
	public int m_sourceFormat = 0;
	public int m_sourceType = 0;
	public java.nio.Buffer m_buffer = null;
	public Bitmap m_bitmap = null;
	
	public int PotWidth = 0;
	public int PotHeight = 0;
	public int NpotWidth = 0;
	public int NpotHeight = 0;
	
	public boolean m_isGenerateMipmap;
	
	public void Initialize
	(
		DelayResourceQueue queue, 
		int internalFormat,
		int nPotWidth,
		int nPotHeight,
		int sourceFormat,
		int sourceType,
		java.nio.Buffer buffer,
		boolean isGenarateMipmap 
	)
	{
		m_internalFormat = internalFormat;
		PotWidth = nPotWidth;
		PotHeight = nPotHeight;
		m_sourceFormat = sourceFormat;
		m_sourceType = sourceType;
		m_buffer = buffer;
		m_isGenerateMipmap = isGenarateMipmap;
		
		if( queue != null )
		{
			queue.Add( this );
		}
		else
		{
			SubSystem.Log.WriteLine( String.format( "texture null queue %dx%d", PotWidth, PotHeight ));
		}
	}

	public void Initialize
	(
		DelayResourceQueue queue, 
		Bitmap bitmap,
		boolean isGenarateMipmap
	)
	{
		PotWidth = bitmap.getWidth();
		PotHeight = bitmap.getHeight();
		m_bitmap = bitmap;
		m_isGenerateMipmap = isGenarateMipmap;
		
		if( queue != null )
		{
			queue.Add( this );
		}
		else
		{
			SubSystem.Log.WriteLine( String.format( "texture null queue %dx%d", m_bitmap.getWidth(), m_bitmap.getHeight() ));
		}
		
	}
	
	public StaticTexture()
	{}

	public StaticTexture
	(
		DelayResourceQueue queue,
		RawImage image,
		boolean isGenerateMipmap
	)
	{
		Initialize
		( 
			queue,
			image.Format,
			image.Width,
			image.Height,
			image.Format,
			image.Type,
			image.Pixels,
			isGenerateMipmap
		);
	}

	public StaticTexture
	(
		DelayResourceQueue queue,
		Bitmap bitmap,
		boolean isGenerateMipmap 
	)
	{
		Initialize
		( 
			queue,
			bitmap,
			isGenerateMipmap 
		);
	}

	public StaticTexture
	(
		DelayResourceQueue queue, 
		TextureInternalFormat eFormat, int nWidth, int nHeight,
		TextureSourceFormat eSrcFormat, TextureSourceType eSrcType, Buffer buffer, boolean isGenerateMipmap
	)
	{
		Initialize( queue,
			eFormat.Value, nWidth, nHeight,
			eSrcFormat.Value, eSrcType.Value, buffer, isGenerateMipmap );
	}
	
	public static int GetPowerOfTwo( int a )
	{
		int result = 0;
		for( result = 1 ; result < a ; result <<= 1 );
		return result;
	}

	@Override public void Generate()
	{		
		int[] names = { 0 };
		GLES20.glGenTextures(1, names, 0);
		Name = names[0];

		SubSystem.Log.WriteLine( String.format( "StaticTexture applied %d %dx%d", Name, PotWidth, PotHeight ) );
		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, Name);

		if( m_bitmap != null )
		{
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, m_bitmap, 0);
			SubSystem.Log.WriteLine( "Texture source is bitmap" );
		}
		else
		{
			m_buffer.position(0);
			GLES20.glTexImage2D( GLES20.GL_TEXTURE_2D,
							0, m_internalFormat,
							PotWidth, PotHeight, 0,
							m_sourceFormat, m_sourceType, m_buffer );
							
			SubSystem.Log.WriteLine( String.format( "Texture source is buffer %d", m_buffer.capacity() ) );
		}

		if( m_isGenerateMipmap )
		{
			GLES20.glGenerateMipmap( GLES20.GL_TEXTURE_2D );
		}

		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
	}

	@Override public void Delete()
	{
		if( 0 < Name )
		{
			int[] names = {Name};
			GLES20.glDeleteTextures(1, names, 0);
		}
		Name = 0;
	}
}
