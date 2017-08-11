package com.nrt.render;

public class StaticVertexBuffer extends Buffer implements VertexBuffer
{
	@Override
	public int GetVertexBufferName()
	{
		return this.Name;
	}

	public StaticVertexBuffer( DelayResourceQueue drq, byte[] data )
		//throws ThreadForceDestroyException
	{
		super( drq, EBufferType.Vertex, data, BufferUsage.StaticDraw );
	}	

	public StaticVertexBuffer( DelayResourceQueue drq, short[] data )
		//throws ThreadForceDestroyException
	{
		super( drq, EBufferType.Vertex, data, BufferUsage.StaticDraw );
	}	
	
	public StaticVertexBuffer( DelayResourceQueue drq, int[] data )
		//throws ThreadForceDestroyException
	{
		super( drq, EBufferType.Vertex, data, BufferUsage.StaticDraw );
	}	
	
	public StaticVertexBuffer( DelayResourceQueue drq, float[] data )
		//throws ThreadForceDestroyException
	{
		super( drq, EBufferType.Vertex, data, BufferUsage.StaticDraw );
	}	
	
	public StaticVertexBuffer( DelayResourceQueue drq, java.nio.Buffer data )
		//throws ThreadForceDestroyException
	{
		super( drq, EBufferType.Vertex, data, BufferUsage.StaticDraw );
	}	
}
	

