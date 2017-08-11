package com.nrt.render;

import android.opengl.GLES20;

public enum BlendEquation
{
	Add(GLES20.GL_FUNC_ADD),
	Subtract(GLES20.GL_FUNC_SUBTRACT),
	ReverseSubtract(GLES20.GL_FUNC_REVERSE_SUBTRACT);
	//Min(GLES20.GL_MIN), 
	//Max(GLES20.GL_MAX);
	
	public int Value = 0;
	BlendEquation(int value)
	{
		Value = value;
	}
}
