package com.nrt.render;

import android.opengl.GLES20;

public enum BlendFunc
{
	Zero(GLES20.GL_ZERO), 
	One(GLES20.GL_ONE), 
	SrcColor(GLES20.GL_SRC_COLOR), 
	OneMinusSrcColor(GLES20.GL_ONE_MINUS_SRC_COLOR),
	DstColor(GLES20.GL_DST_COLOR), 
	OneMinusDstColor(GLES20.GL_ONE_MINUS_DST_COLOR), 
	SrcAlpha(GLES20.GL_SRC_ALPHA), 
	OneMinusSrcAlpha(GLES20.GL_ONE_MINUS_SRC_ALPHA),
	DstAlpha(GLES20.GL_DST_ALPHA), 
	OneMinusDstAlpha(GLES20.GL_ONE_MINUS_DST_ALPHA),
	ConstantColor(GLES20.GL_CONSTANT_COLOR), 
	OneMinusConstantColor(GLES20.GL_ONE_MINUS_CONSTANT_COLOR), 
	ConstantAlpha(GLES20.GL_CONSTANT_ALPHA),
	OneMinusConstantAlpha(GLES20.GL_ONE_MINUS_CONSTANT_ALPHA);

	public int Value = 0;
	BlendFunc(int value)
	{
		Value = value;
	}
}
