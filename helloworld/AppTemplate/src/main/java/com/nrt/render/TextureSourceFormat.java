package com.nrt.render;
import android.opengl.GLES20;

public enum TextureSourceFormat
{
	Unknown(0),
	Alpha(GLES20.GL_ALPHA),
	Luminance(GLES20.GL_LUMINANCE),
	LuminanceAlpha(GLES20.GL_LUMINANCE_ALPHA),
	RGB(GLES20.GL_RGB),
	RGBA(GLES20.GL_RGBA);

	public int Value = 0;
	TextureSourceFormat(int value)
	{
		Value = value;
	}
	
	public static TextureSourceFormat Convert( int value )
	{
		for( TextureSourceFormat format : values() )
		{
			if( format.Value == value )
			{
				return format;
			}
		}
		return Unknown;
	}
}
