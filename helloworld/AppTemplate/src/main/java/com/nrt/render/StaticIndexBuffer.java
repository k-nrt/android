package com.nrt.render;

public class StaticIndexBuffer extends com.nrt.render.Buffer implements IndexBuffer
{
	@Override
	public int GetIndexBufferName()
	{
		return this.Name;
	}

	public StaticIndexBuffer( DelayResourceQueue drq, short[] data )
	{
		super( drq, EBufferType.Index, data, EBufferUsage.StaticDraw);
	}

	public StaticIndexBuffer( DelayResourceQueue drq, java.nio.Buffer data )
	{
		super( drq, EBufferType.Index, data, EBufferUsage.StaticDraw);
	}
}

