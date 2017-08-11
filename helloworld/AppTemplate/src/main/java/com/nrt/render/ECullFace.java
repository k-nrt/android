package com.nrt.render;

import android.opengl.GLES20;

public enum ECullFace
{
	Front(GLES20.GL_FRONT),
	Back(GLES20.GL_BACK),
	FrontAndBack(GLES20.GL_FRONT_AND_BACK);
	
	public int Value = 0;
	
	ECullFace(int value)
	{
		Value = value;
	}
}
