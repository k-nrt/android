package com.nrt.render;

public class RingVertexBuffer extends RingBuffer implements VertexBuffer
{
	@Override
	public int GetVertexBufferName()
	{
		return Name;
	}

	public RingVertexBuffer( DelayResourceQueue drq, int size)
	{
		super( drq, EBufferType.Vertex, size);
	}
}

