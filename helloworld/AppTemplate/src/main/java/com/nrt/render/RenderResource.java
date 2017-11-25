package com.nrt.render;

import android.opengl.GLES20;

public class RenderResource
{
	public int Name=0;

	public RenderResource()
	{}

	public RenderResource(int iName)
	{
		Name = iName;
	}
	
	public void Generate()
	{
	}

	public void Delete()
	{}

	protected static int CreateBuffer(EBufferType bufferType, java.nio.Buffer buffer, EBufferUsage bufferUsage)
	{
		int[] names = { 0 };
		GLES20.glGenBuffers(1, names, 0);

		GLES20.glBindBuffer(bufferType.Value, names[0]);
		buffer.position(0);
		GLES20.glBufferData(bufferType.Value,  buffer.capacity(), buffer, bufferUsage.Value);
		GLES20.glBindBuffer(bufferType.Value, 0);
		return names[0];
	}

	protected static void DeleteBuffer(int name)
	{
		int[] names = { name };
		GLES20.glDeleteBuffers(1,names,0);
	}
}

