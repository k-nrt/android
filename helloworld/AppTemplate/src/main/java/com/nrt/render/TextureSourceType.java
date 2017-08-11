package com.nrt.render;
import android.opengl.GLES20;

public enum TextureSourceType
{
	Unknown(0),
	UnsignedByte(GLES20.GL_UNSIGNED_BYTE),
	UnsignedShort_5_6_5(GLES20.GL_UNSIGNED_SHORT_5_6_5),
	UnsignedShort_4_4_4_4(GLES20.GL_UNSIGNED_SHORT_4_4_4_4),
	UnsignedShort_5_5_5_1(GLES20.GL_UNSIGNED_SHORT_5_5_5_1);
	
	public int Value = 0;
	TextureSourceType( int value )
	{
		Value = value;
	}
	
	public static TextureSourceType Convert( int value )
	{
		for( TextureSourceType type : values() )
		{
			if( type.Value == value )
			{
				return type;
			}
		}
		
		return Unknown;
	}
}

