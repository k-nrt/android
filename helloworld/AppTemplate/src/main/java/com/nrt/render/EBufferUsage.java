package com.nrt.render;

import android.opengl.GLES20;

public enum EBufferUsage
{
	StreamDraw(GLES20.GL_STREAM_DRAW),
	StaticDraw(GLES20.GL_STATIC_DRAW),
	DynamicDraw(GLES20.GL_DYNAMIC_DRAW);

	public int Value = 0;

	EBufferUsage(int value)
	{
		Value = value;
	}
}
