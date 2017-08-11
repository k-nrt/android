package com.nrt.render;

import android.opengl.*;

public class VertexStream
{
	public VertexAttribute[] VertexAttributes = null;
	public int Stride = 0;

	public VertexStream(VertexAttribute[] va, int stride)
	{
		VertexAttributes = va;
		if (stride <= 0)
		{
			CountStride();
		}
		else
		{
			Stride = stride;
		}
	}

	private void CountStride()
	{
		Stride = 0;
		for (int i = 0; i < VertexAttributes.length ; i++)
		{
			VertexAttribute va = VertexAttributes[i];
			switch (va.Format)
			{
				case GLES20.GL_BYTE:
				case GLES20.GL_UNSIGNED_BYTE:
					Stride += 4;
					break;

				case GLES20.GL_UNSIGNED_INT:
					Stride += 4 * va.Components;
					break;

				case GLES20.GL_FLOAT:
					Stride += 4 * va.Components;
					break;
			}
		}
	}
}
