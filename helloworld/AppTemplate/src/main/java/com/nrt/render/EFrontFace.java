package com.nrt.render;

import android.opengl.GLES20;

public enum EFrontFace
{
	CCW(GLES20.GL_CCW),
	CW(GLES20.GL_CW);
	
	public int Value = 0;
	EFrontFace(int value)
	{
		Value = value;
	}
}