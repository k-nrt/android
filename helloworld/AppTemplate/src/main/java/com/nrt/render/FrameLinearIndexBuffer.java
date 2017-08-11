package com.nrt.render;

public class FrameLinearIndexBuffer
	extends FrameLinearBuffer 
	implements IndexBuffer
{
	@Override
	public int GetIndexBufferName()
	{
		return Name;
	}

	public FrameLinearIndexBuffer( DelayResourceQueue drq, int initialSize, int expandSize)
	{
		super( drq, EBufferType.Index, initialSize, expandSize);
	}
}
