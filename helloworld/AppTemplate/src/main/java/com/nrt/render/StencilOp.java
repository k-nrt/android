package com.nrt.render;

import android.opengl.GLES20;

public enum StencilOp
{
	Keep(GLES20.GL_KEEP), //Keeps the current value.
	Zero(GLES20.GL_ZERO), //Sets the stencil buffer value to 0.
	Replace(GLES20.GL_REPLACE), //Sets the stencil buffer value to ref, as specified by glStencilFunc.
	Increment(GLES20.GL_INCR), //Increments the current stencil buffer value. Clamps to the maximum representable unsigned value.
	IncrementWrap(GLES20.GL_INCR_WRAP), //Increments the current stencil buffer value. Wraps stencil buffer value to zero when incrementing the maximum representable unsigned value.
	Decrement(GLES20.GL_DECR), //Decrements the current stencil buffer value. Clamps to 0.
	DecrementWrap(GLES20.GL_DECR_WRAP), //Decrements the current stencil buffer value. Wraps stencil buffer value to the maximum representable unsigned value when decrementing a stencil buffer value of zero.
	Invert(GLES20.GL_INVERT); //Bitwise inverts the cur
	
	public int Value = 0;
	StencilOp( int value )
	{
		Value = value;
	}
}
