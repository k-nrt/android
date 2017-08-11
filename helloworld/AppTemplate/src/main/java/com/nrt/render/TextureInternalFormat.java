package com.nrt.render;
import android.opengl.GLES20;

public enum TextureInternalFormat
{
	Unknown(0),
	Alpha(GLES20.GL_ALPHA),
	Luminance(GLES20.GL_LUMINANCE),
	LuminanceAlpha(GLES20.GL_LUMINANCE_ALPHA),
	RGB(GLES20.GL_RGB),
	RGBA(GLES20.GL_RGBA);
	
	public int Value = 0;
	
	TextureInternalFormat(int value)
	{
		Value = value;
	}
	
	public static TextureInternalFormat Convert( int value )
	{
		for( TextureInternalFormat format : values() )
		{
			if( format.Value == value )
			{
				return format;
			}
		}
		
		return Unknown;
	}
}
