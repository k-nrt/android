package com.nrt.render;

import android.opengl.GLES20;

public enum EPrimitive
{
	Points(GLES20.GL_POINTS), 
	LineStrip(GLES20.GL_LINE_STRIP), 
	LineLoop(GLES20.GL_LINE_LOOP), 
	Lines(GLES20.GL_LINES), 
	TriangleStrip(GLES20.GL_TRIANGLE_STRIP),
	TriangleFan(GLES20.GL_TRIANGLE_FAN),
	Triangles(GLES20.GL_TRIANGLES);
	
	public int Value = 0;
	
	EPrimitive(int value )
	{
		Value = value;
	}
}