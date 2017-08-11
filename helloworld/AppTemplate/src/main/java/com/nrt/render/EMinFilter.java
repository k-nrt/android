package com.nrt.render;
import android.opengl.GLES20;
public enum EMinFilter
{
	Nearest(GLES20.GL_NEAREST),
	Linear(GLES20.GL_LINEAR),
	NearestMipmapNearest(GLES20.GL_NEAREST_MIPMAP_NEAREST),
	NearestMipmapLinear(GLES20.GL_NEAREST_MIPMAP_LINEAR),
	LinearMipmapNearest(GLES20.GL_LINEAR_MIPMAP_NEAREST),
	LinearMipmapLinear(GLES20.GL_LINEAR_MIPMAP_LINEAR);
	
	public int Value;
	EMinFilter( int value )
	{
		Value=value;
	}
}
