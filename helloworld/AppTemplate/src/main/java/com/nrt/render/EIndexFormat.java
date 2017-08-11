package com.nrt.render;
import android.opengl.*;
public enum EIndexFormat
{
	UnsignedShort(GLES20.GL_UNSIGNED_SHORT),
	UnsignedInt(GLES20.GL_UNSIGNED_INT);
	
	public int Value = 0;
	private EIndexFormat(int value)
	{
		Value = value;
	}
}


