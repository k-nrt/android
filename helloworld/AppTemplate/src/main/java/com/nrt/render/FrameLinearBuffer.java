package com.nrt.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import android.opengl.*;
import com.nrt.math.*;

public class FrameLinearBuffer extends RenderResource
{
	public EBufferType BufferType = EBufferType.Index;
	public int Size = 0;
	
	public int ExpandSize = 0;

	public ByteBuffer m_buffer = null;
	public FrameLinearBuffer(DelayResourceQueue drq, EBufferType eType, int initialSize, int expandSize )
	{
		BufferType = eType;
		Size = initialSize;
		ExpandSize = expandSize;

		m_buffer = ByteBuffer.allocateDirect(Size);
		m_buffer.order(ByteOrder.nativeOrder());

		drq.Add(this);
	}

	@Override
	public void Apply()
	{
		int[] names = { 0 };
		GLES20.glGenBuffers(1, names, 0);
		Name = names[0];

		GLES20.glBindBuffer(BufferType.Value, Name);
		GLES20.glBufferData(BufferType.Value, m_buffer.capacity(), m_buffer, GLES20.GL_STREAM_DRAW);
		GLES20.glBindBuffer(BufferType.Value, 0);
	}

	public void Rewind()
	{
		m_buffer.position(0);
	}
	
	public void UpdateResource()
	{
		GLES20.glBindBuffer(BufferType.Value, Name);
		m_buffer.position(0);
		GLES20.glBufferData(BufferType.Value, m_buffer.capacity(), m_buffer, GLES20.GL_STREAM_DRAW);
		GLES20.glBindBuffer(BufferType.Value, 0);
	}
	
	public int GetPosition()
	{
		return m_buffer.position();
	}
	
	private void CheckCapacityOrExpandBuffer(int appendSize)
	{
		if( (m_buffer.position()+appendSize) < m_buffer.capacity() )
		{
			return;
		}
		
		int newSize = m_buffer.capacity() + ExpandSize;
		for(;;)
		{
			if( (m_buffer.position() + appendSize) < newSize )
			{
				break;
			}

			newSize += ExpandSize;
		}
		ByteBuffer buffer = ByteBuffer.allocateDirect(newSize);
		buffer.order(ByteOrder.nativeOrder());
		buffer.put( m_buffer );
		m_buffer = buffer;
	}
	
	//public void Begin()
	//{
	//}

	public void Add(short x)
	{
		CheckCapacityOrExpandBuffer( 2 );		
		m_buffer.putShort(x);
	}

	public void Add(int x)
	{
		CheckCapacityOrExpandBuffer(4);
		m_buffer.putInt(x);
	}

	public void Add(float x)
	{
		CheckCapacityOrExpandBuffer(4);
		m_buffer.putFloat(x);
	}

	public void Add(float x, float y)
	{
		CheckCapacityOrExpandBuffer(8);
		m_buffer.putFloat(x);
		m_buffer.putFloat(y);
	}

	public void Add(float x, float y, float z)
	{
		CheckCapacityOrExpandBuffer(12);
		m_buffer.putFloat(x);
		m_buffer.putFloat(y);
		m_buffer.putFloat(z);
	}

	public void Add(float x, float y, float z, float w)
	{
		CheckCapacityOrExpandBuffer(16);
		m_buffer.putFloat(x);
		m_buffer.putFloat(y);
		m_buffer.putFloat(z);
		m_buffer.putFloat(w);
	}

	public void Add( final Float3 xyz)
	{
		CheckCapacityOrExpandBuffer(12);
		m_buffer.putFloat(xyz.X);
		m_buffer.putFloat(xyz.Y);
		m_buffer.putFloat(xyz.Z);
	}

	//public void End()
	//{
	//}

	public void Align(int alignment)
	{
		int position = m_buffer.position();
		if ((position % alignment) > 0)
		{
			position += alignment - (position % alignment);
		}
	}
}
