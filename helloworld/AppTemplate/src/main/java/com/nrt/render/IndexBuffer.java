package com.nrt.render;

import android.opengl.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public interface IndexBuffer
{
	public int GetIndexBufferName();
	/*
	public IndexBuffer(short[] arrayIndices)
	{
		ByteBuffer buffer = ByteBuffer.allocateDirect(arrayIndices.length * 2);
		buffer.order(ByteOrder.nativeOrder()); 
		Indices = arrayIndices.length;
		for (int i = 0 ; i < Indices ; i++)
		{
			buffer.putShort(i * 2, arrayIndices[i]);
		}

		buffer.position(0);

		int[] names = {0};
		GLES20.glGenBuffers(1, names, 0);
		Name = names[0];
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, Name);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffer.capacity(), buffer, GLES20.GL_STATIC_DRAW);
		Format = GLES20.GL_UNSIGNED_SHORT;
		ElementSize = 2;
	}

	public IndexBuffer(byte[] arrayIndices)
	{
		ByteBuffer buffer = ByteBuffer.allocateDirect(arrayIndices.length);
		buffer.order(ByteOrder.nativeOrder()); 
		
		Indices = arrayIndices.length;
		buffer.put(arrayIndices);
		buffer.position(0);

		int[] names = {0};
		GLES20.glGenBuffers(1, names, 0);
		Name = names[0];
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, Name);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffer.capacity(), buffer, GLES20.GL_STATIC_DRAW);
		Format = GLES20.GL_UNSIGNED_BYTE;
		ElementSize = 1;
	}
	*/
}
