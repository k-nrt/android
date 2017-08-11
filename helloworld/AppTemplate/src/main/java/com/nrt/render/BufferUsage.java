package com.nrt.render;

import android.opengl.*;

public enum BufferUsage
{
	Unknown(0),
	StreamDraw(GLES20.GL_STREAM_DRAW),
	StaticDraw(GLES20.GL_STATIC_DRAW),
	DynamicDraw(GLES20.GL_DYNAMIC_DRAW);

	public int Value = 0;
	private BufferUsage(int glEnum)
	{
		Value = glEnum;
	}
}

