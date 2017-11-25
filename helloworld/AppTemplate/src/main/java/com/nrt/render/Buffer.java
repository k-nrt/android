package com.nrt.render;
import android.opengl.*;
import android.widget.TextView;

//import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Buffer extends RenderResource
{
	public EBufferType Type = EBufferType.Unknown;
	public java.nio.Buffer Buffer = null;
	public EBufferUsage Usage = EBufferUsage.StaticDraw;

	public Buffer(DelayResourceQueue drq, EBufferType bufferType, java.nio. Buffer buffer, EBufferUsage bufferUsage )
	{
		Type = bufferType;
		Buffer = buffer;
		Usage = bufferUsage;

		if( drq != null )
		{
			drq.Add( this );
		}
		else
		{
			Generate();
		}
	}

	@Override public void Generate()
	{
		Name = CreateBuffer( Type, Buffer, Usage );
	}

	@Override public void Delete()
	{
		if( 0 < Name )
		{
			DeleteBuffer(Name);
 		}
		Name = 0;
	}

	public Buffer(DelayResourceQueue drq, EBufferType eType, byte[] data, EBufferUsage eUsage )
	{
		this( drq, eType, CreateByteBuffer(data), eUsage );
	}

	public Buffer(DelayResourceQueue drq, EBufferType eType, short[] data, EBufferUsage eUsage )
	{
		this( drq, eType, CreateByteBuffer(data), eUsage );
	}
	
	public Buffer(DelayResourceQueue drq, EBufferType eType, int[] data, EBufferUsage eUsage )
	{
		this( drq, eType, CreateByteBuffer(data), eUsage );
	}
	
	public Buffer(DelayResourceQueue drq, EBufferType eType, float[] data, EBufferUsage eUsage )
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

