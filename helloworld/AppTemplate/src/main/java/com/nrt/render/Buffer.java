package com.nrt.render;
import android.opengl.*;

//import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Buffer extends RenderResource
{
	public int Type = 0;
	public java.nio.Buffer Buffer = null;
	public int Usage = 0;
	
	public void Initialize( DelayResourceQueue drq, 
		int eType, 
		java.nio.Buffer buffer,
		int eUsage )
		//throws ThreadForceDestroyException
	{
		Type = eType;
		Buffer = buffer;
		Usage = eUsage;
		
		if( drq != null )
		{
			drq.Add( this );
		}
		else
		{
			Apply();
		}
	}

	@Override
	public void Apply()
	{
		int[] names = { 0 };
		GLES20.glGenBuffers(1, names, 0);
		Name = names[0];
		GLES20.glBindBuffer(Type, Name);
		GLES20.glBufferData(Type, Buffer.capacity(), Buffer, Usage);
		GLES20.glBindBuffer(Type, 0 );
	}

	public Buffer(DelayResourceQueue drq, EBufferType eType, java.nio. Buffer buffer, BufferUsage eUsage )
	//throws ThreadForceDestroyException
	{
		Initialize( drq, eType.Value, buffer, eUsage.Value );
		/*
		int[] names = { 0 };
		GLES20.glGenBuffers(1, names, 0);
		Name = names[0];
		GLES20.glBindBuffer(eType.Value, Name);
		GLES20.glBufferData(eType.Value, buffer.capacity(), buffer, eUsage.Value);
		GLES20.glBindBuffer(eType.Value, 0 );
		*/
	}
	
	
	public Buffer(DelayResourceQueue drq, EBufferType eType, byte[] data, BufferUsage eUsage )
	//throws ThreadForceDestroyException
	{
		this( drq, eType, CreateByteBuffer(data), eUsage );
	}

	public Buffer(DelayResourceQueue drq, EBufferType eType, short[] data, BufferUsage eUsage )
	//throws ThreadForceDestroyException
	
	{
		this( drq, eType, CreateByteBuffer(data), eUsage );
	}
	
	public Buffer(DelayResourceQueue drq, EBufferType eType, int[] data, BufferUsage eUsage )
	//throws ThreadForceDestroyException
	{
		this( drq, eType, CreateByteBuffer(data), eUsage );
	}
	
	public Buffer(DelayResourceQueue drq, EBufferType eType, float[] data, BufferUsage eUsage )
	//throws ThreadForceDestroyException
	{
		this( drq, eType, CreateByteBuffer(data), eUsage );
	}
	
	public static ByteBuffer CreateByteBuffer( byte[] data )
	{
		ByteBuffer buffer = ByteBuffer.allocateDirect( data.length );
		buffer.order( ByteOrder.nativeOrder() );
		buffer.put( data );
		buffer.position(0);
		return buffer;
	}
	
	public static ByteBuffer CreateByteBuffer( short[] data )
	{
		ByteBuffer buffer = ByteBuffer.allocateDirect( data.length*2 );
		buffer.order( ByteOrder.nativeOrder() );
		for( int i = 0 ; i < data.length ; i++ )
		{
			buffer.putShort( data[i] );
		}
		buffer.position(0);
		return buffer;
	}
	
	public static ByteBuffer CreateByteBuffer( int[] data )
	{
		ByteBuffer buffer = ByteBuffer.allocateDirect( data.length*4 );
		buffer.order( ByteOrder.nativeOrder() );
		for( int i = 0 ; i < data.length ; i++ )
		{
			buffer.putInt( data[i] );
		}
		buffer.position(0);
		return buffer;
	}
	
	public static ByteBuffer CreateByteBuffer( float[] data )
	{
		ByteBuffer buffer = ByteBuffer.allocateDirect( data.length*4 );
		buffer.order( ByteOrder.nativeOrder() );
		for( int i = 0 ; i < data.length ; i++ )
		{
			buffer.putFloat( data[i] );
		}
		buffer.position(0);
		return buffer;
	}
	
}

