package com.nrt.render;
import android.opengl.*;

public enum EMagFilter
{
	Nearest(GLES20.GL_NEAREST),
	Linear(GLES20.GL_LINEAR);
	
	public int Value;
	EMagFilter(int value)
	{
		Value=value;
	}
}
