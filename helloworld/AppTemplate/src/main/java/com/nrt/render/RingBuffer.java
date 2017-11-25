package com.nrt.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import android.opengl.*;
import com.nrt.math.*;

public class RingBuffer extends RenderResource
{
	public EBufferType BufferType = EBufferType.Index;
 	public int Size = 0;
	
	public int Position = 0;
	public ByteBuffer m_buffer = null;

	public int Write = 0;
	
	public RingBuffer(DelayResourceQueue drq, EBufferType eType, int size)
		//throws ThreadForceDestroyException
	{
		BufferType = eType;
		Size = size;
		
		m_buffer = ByteBuffer.allocateDirect(Size);
		m_buffer.order(ByteOrder.nativeOrder());

		if( drq != null )
		{
			drq.Add(this);
		}
		else
		{
			Generate();
		}
	}

	@Override public void Generate()
	{
		int[] names = { 0 };
		GLES20.glGenBuffers(1, names, 0);
		Name = names[0];

		GLES20.glBindBuffer(BufferType.Value, Name);
		m_buffer.position(0);
		GLES20.glBufferData(BufferType.Value, m_buffer.capacity(), m_buffer, GLES20.GL_DYNAMIC_DRAW);
		GLES20.glBindBuffer(BufferType.Value, 0 );
	}

	@Override public void Delete()
	{
		if( 0 < Name )
		{
			int[] names = { Name };
			GLES20.glDeleteBuffers(1, names,0);
		}
		Name = 0;
	}

	public void Begin()
	{
		//m_buffer.rewind();
		m_buffer.position( 0 );
		Write = 0;
	}

	public void Add(short x)
	{
		m_buffer.putShort(x);
		Write += 2;
	}

	public void Add(int x)
	{
		m_buffer.putInt(x);
		Write += 4;
	}

	public void Add(float x)
	{
		m_buffer.putFloat(x);
		Write += 4;
	}

	public void Add(float x, float y)
	{
		m_buffer.putFloat(x);
		Write += 4;
		m_buffer.putFloat(y);
		Write += 4;
	}

	public void Add(float x, float y, float z)
	{
		m_buffer.putFloat(x);
		Write += 4;
		m_buffer.putFloat(y);
		Write += 4;
		m_buffer.putFloat(z);
		Write += 4;
	}

	public void Add(float x, float y, float z, float w)
	{
		m_buffer.putFloat(x);
		Write += 4;
		m_buffer.putFloat(y);
		Write += 4;
		m_buffer.putFloat(z);
		Write += 4;
		m_buffer.putFloat(w);
		Write += 4;
	}

	public void Add( Float3 xyz )
	{
		m_buffer.putFloat(xyz.X);
		Write += 4;
		m_buffer.putFloat(xyz.Y);
		Write += 4;
		m_buffer.putFloat(xyz.Z);
		Write += 4;
	}
	
	public int End()
	{
		int size = m_buffer.position();
		if( size <= 0 )
		{
			return 0;
		}
		int remain = Size - Position;
		if (remain < size)
		{
//			System.Error.WriteLine( "rewind" );
			Position = 0;
		}
		m_buffer.position(0);
		GLES20.glBindBuffer(BufferType.Value, Name);
		GLES20.glBufferSubData(BufferType.Value, Position, size, m_buffer);
		GLES20.glBindBuffer(BufferType.Value, 0);
		

		int start = Position;
		Position += size;

		return start;
	}
	
	public void Align( int alignment )
	{
		if( (Position % alignment) > 0 )
		{
			Position += alignment - (Position % alignment);
		}
	}
}
