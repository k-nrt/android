package com.nrt.render;

public class FrameLinearVertexBuffer extends FrameLinearBuffer implements VertexBuffer
{
	@Override
	public int GetVertexBufferName()
	{
		return Name;
	}

	public FrameLinearVertexBuffer( DelayResourceQueue drq, int initialSize, int expandSize)
	{
		super( drq, EBufferType.Vertex, initialSize, expandSize);
	}
}

