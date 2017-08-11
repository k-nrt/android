package com.nrt.render;
import android.opengl.*;

public enum EBufferType
{
	Unknown(0),
	Vertex(GLES20.GL_ARRAY_BUFFER),
	Index(GLES20.GL_ELEMENT_ARRAY_BUFFER);

	public int Value = 0;
	private EBufferType(int glEnum )
	{
		Value = glEnum;
	}
}

