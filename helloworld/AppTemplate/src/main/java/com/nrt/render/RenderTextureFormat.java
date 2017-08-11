package com.nrt.render;

import android.opengl.GLES20;

public enum RenderTextureFormat
{
	ALPHA(GLES20.GL_ALPHA),
	RGB(GLES20.GL_RGB),
	RGBA(GLES20.GL_RGBA),
	LUMINANCE(GLES20.GL_LUMINANCE),
	LUMINANCE_ALPHA(GLES20.GL_LUMINANCE_ALPHA);
	
	public int Value = 0;
	private RenderTextureFormat( int value )
	{
		Value = value;
	}
}
