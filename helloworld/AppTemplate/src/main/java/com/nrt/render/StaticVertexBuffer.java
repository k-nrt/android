package com.nrt.render;

public class StaticVertexBuffer extends Buffer implements VertexBuffer
{
	@Override
	public int GetVertexBufferName()
	{
		return this.Name;
	}

	public StaticVertexBuffer( DelayResourceQueue drq, byte[] data )
	{
		super( drq, EBufferType.Vertex, data, EBufferUsage.StaticDraw );
	}	

	public StaticVertexBuffer( DelayResourceQueue drq, short[] data )
	{
		super( drq, EBufferType.Vertex, data, EBufferUsage.StaticDraw );
	}	
	
	public StaticVertexBuffer( DelayResourceQueue drq, int[] data )
	{
		super( drq, EBufferType.Vertex, data, EBufferUsage.StaticDraw );
	}	
	
	public StaticVertexBuffer( DelayResourceQueue drq, float[] data )
	{
		super( drq, EBufferType.Vertex, data, EBufferUsage.StaticDraw );
	}	
	
	public StaticVertexBuffer( DelayResourceQueue drq, java.nio.Buffer data )
	{
		super( drq, EBufferType.Vertex, data, EBufferUsage.StaticDraw );
	}	
}
	

