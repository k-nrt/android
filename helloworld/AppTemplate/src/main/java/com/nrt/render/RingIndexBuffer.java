package com.nrt.render;

public class RingIndexBuffer extends RingBuffer implements IndexBuffer
{
	@Override
	public int GetIndexBufferName()
	{
		return Name;
	}
	
	public RingIndexBuffer( DelayResourceQueue drq, int size)
	{
		super( drq, EBufferType.Index, size);
	}
}

