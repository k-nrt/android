package com.nrt.render;
import android.opengl.GLES20;
import android.opengl.*;
public enum EWrap
{
	ClampToEdge(GLES20.GL_CLAMP_TO_EDGE),
	//ClampToBorder(GLES10.GL_CLAMP_TO_BORDER),
	Repeat(GLES20.GL_REPEAT);
	public int Value = 0;
	EWrap( int value )
	{
		Value=value;
	}
}
