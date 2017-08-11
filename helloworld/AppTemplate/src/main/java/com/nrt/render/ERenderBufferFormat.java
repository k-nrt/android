package com.nrt.render;
import android.opengl.GLES20;

public enum ERenderBufferFormat
{
	RGBA4(GLES20.GL_RGBA4),
	RGB565(GLES20.GL_RGB565),
	RGB5_A1(GLES20.GL_RGB5_A1),
	DEPTH_COMPONENT16(GLES20.GL_DEPTH_COMPONENT16),
	STENCIL_INDEX8(GLES20.GL_STENCIL_INDEX8);
	
	public int Value = 0;
	private ERenderBufferFormat(int value )
	{
		Value = value;
	}
}
