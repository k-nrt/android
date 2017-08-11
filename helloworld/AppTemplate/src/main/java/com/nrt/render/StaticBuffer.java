package com.nrt.render;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import android.opengl.GLES20;
/*
public class StaticBuffer extends nrt.render.Buffer
{
	public StaticBuffer( nrt.render.Buffer.EType eType, Buffer buffer )
	{
		super( eType, buffer, nrt.render.Buffer.EUsage.StaticDraw );
		int[] names = { 0 };
		GLES20.glGenBuffers(1, names, 0);
		Name = names[0];

		GLES20.glBindBuffer( bufferType, Name );
		GLES20.glBufferData( bufferType, buffer.capacity(), buffer, GLES20.GL_STATIC_DRAW );

	}

	public StaticBuffer( int bufferType, byte[] data )
	{
		int[] names = { 0 };
		GLES20.glGenBuffers(1, names, 0);
		Name = names[0];

		ByteBuffer buffer = ByteBuffer.allocateDirect( data.length );
		buffer.order( ByteOrder.nativeOrder() );
		buffer.put( data );

		buffer.position(0);
		GLES20.glBindBuffer( bufferType, Name );
		GLES20.glBufferData( bufferType, buffer.capacity(), buffer, GLES20.GL_STATIC_DRAW );

	}

	public StaticBuffer( int bufferType, short[] data )
	{
		int[] names = { 0 };
		GLES20.glGenBuffers(1, names, 0);
		Name = names[0];

		ByteBuffer buffer = ByteBuffer.allocateDirect( data.length*2 );
		buffer.order( ByteOrder.nativeOrder() );

		for( int i = 0 ; i < data.length ; i++ )
		{
			buffer.putShort( data[i] );
		}

		buffer.position(0);
		GLES20.glBindBuffer( bufferType, Name );
		GLES20.glBufferData( bufferType, buffer.capacity(), buffer, GLES20.GL_STATIC_DRAW );
	}
}
*/

