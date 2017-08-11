package com.nrt.math;
import android.graphics.*;
import android.opengl.Matrix;
//import android.animation.*;

import com.nrt.basic.*;


public final class Float4x4
{
	public final float[] Values =
	{
		0.0f, 0.0f, 0.0f, 0.0f, 
		0.0f, 0.0f, 0.0f, 0.0f, 
		0.0f, 0.0f, 0.0f, 0.0f, 
		0.0f, 0.0f, 0.0f, 0.0f, 
	};

	public Float4x4()
	{}

	public Float4x4(float[] values)
	{
		for( int i = 0 ; i < Values.length ; i++ )
		{
			Values[i] = values[i];
		}
	}

	public Float4x4(
		float xx, float xy, float xz, float xw,
		float yx, float yy, float yz, float yw,
		float zx, float zy, float zz, float zw,
		float wx, float wy, float wz, float ww)
	{
		Values[0] = xx; Values[1] = xy; Values[2] = xz; Values[3] = xw;
		Values[4] = yx; Values[5] = yy; Values[6] = yz; Values[7] = yw;
		Values[8] = zx; Values[9] = zy; Values[10] = zz; Values[11] = zw;
		Values[12] = wx; Values[13] = wy; Values[14] = wz; Values[15] = ww;
	}

	public Float4x4( Float4 x, Float4 y, Float4 z, Float4 w )
	{
		Values[0] = x.X; Values[1] = x.Y; Values[2] = x.Z; Values[3] = x.W;
		Values[4] = y.X; Values[5] = y.Y; Values[6] = y.Z; Values[7] = y.W;
		Values[8] = z.X; Values[9] = z.Y; Values[10] = z.Z; Values[11] = z.W;
		Values[12] = w.X; Values[13] = w.Y; Values[14] = w.Z; Values[15] = w.W;
	}

	public Float4x4( Float3x3 m, Float3 t )
	{
		Values[0] = m.Values[0]; Values[1] = m.Values[1]; Values[2] = m.Values[2]; Values[3] = 0.0f;
		Values[4] = m.Values[3]; Values[5] = m.Values[4]; Values[6] = m.Values[5]; Values[7] = 0.0f;
		Values[8] = m.Values[6]; Values[9] = m.Values[7]; Values[10] = m.Values[8]; Values[11] = 0.0f;
		Values[12] = t.X; Values[13] = t.Y; Values[14] = t.Z; Values[15] = 1.0f;
	}

	public Float4x4 Set(
		float xx, float xy, float xz, float xw,
		float yx, float yy, float yz, float yw,
		float zx, float zy, float zz, float zw,
		float wx, float wy, float wz, float ww)
	{
		Values[0] = xx; Values[1] = xy; Values[2] = xz; Values[3] = xw;
		Values[4] = yx; Values[5] = yy; Values[6] = yz; Values[7] = yw;
		Values[8] = zx; Values[9] = zy; Values[10] = zz; Values[11] = zw;
		Values[12] = wx; Values[13] = wy; Values[14] = wz; Values[15] = ww;
		return this;
	}

	public Float4x4 Set( Float4x4 a )
	{
		for( int i = 0 ; i < Values.length ; i++ )
		{
			Values[i]=a.Values[i];
		}
		return this;
	}

	public Float4x4 Set( Float3x3 m, Float3 t )
	{
		Values[0] = m.Values[0]; Values[1] = m.Values[1]; Values[2] = m.Values[2]; Values[3] = 0.0f;
		Values[4] = m.Values[3]; Values[5] = m.Values[4]; Values[6] = m.Values[5]; Values[7] = 0.0f;
		Values[8] = m.Values[6]; Values[9] = m.Values[7]; Values[10] = m.Values[8]; Values[11] = 0.0f;
		Values[12] = t.X; Values[13] = t.Y; Values[14] = t.Z; Values[15] = 1.0f;
		return this;
	}

	/*
	public Float3 AxisX() { return new Float3( Values[0], Values[1], Values[2] ); }
	public Float3 AxisY() { return new Float3( Values[4], Values[5], Values[6] ); }
	public Float3 AxisZ() { return new Float3( Values[8], Values[9], Values[10] ); }
	public Float3 AxisW() { return new Float3( Values[12], Values[13], Values[14] ); }
	*/
	
	public final Float3 GetAxisX( Float3 dst ) { return dst.Set( Values[0], Values[1], Values[2] ); }
	public final Float3 GetAxisY( Float3 dst ) { return dst.Set( Values[4], Values[5], Values[6] ); }
	public final Float3 GetAxisZ( Float3 dst ) { return dst.Set( Values[8], Values[9], Values[10] ); }
	public final Float3 GetAxisW( Float3 dst ) { return dst.Set( Values[12], Values[13], Values[14] ); }

	/*
	public Float4 X() { return new Float4( Values[0], Values[1], Values[2], Values[3] ); }
	public Float4 Y() { return new Float4( Values[4], Values[5], Values[6], Values[7] ); }
	public Float4 Z() { return new Float4( Values[8], Values[9], Values[10], Values[11] ); }
	public Float4 W() { return new Float4( Values[12], Values[13], Values[14], Values[15] ); }
	*/
	public final Float4 X( Float4 dst ) { return dst.Set( Values[0], Values[1], Values[2], Values[3] ); }
	public final Float4 Y( Float4 dst ) { return dst.Set( Values[4], Values[5], Values[6], Values[7] ); }
	public final Float4 Z( Float4 dst ) { return dst.Set( Values[8], Values[9], Values[10], Values[11] ); }
	public final Float4 W( Float4 dst ) { return dst.Set( Values[12], Values[13], Values[14], Values[15] ); }


	public final void SetAxisX( Float3 a ) { Values[0] = a.X; Values[1] = a.Y; Values[2] = a.Z; Values[3] = 0.0f; }
	public final void SetAxisY( Float3 a ) { Values[4] = a.X; Values[5] = a.Y; Values[6] = a.Z; Values[7] = 0.0f; }
	public final void SetAxisZ( Float3 a ) { Values[8] = a.X; Values[9] = a.Y; Values[10] = a.Z; Values[11] = 0.0f; }
	public final void SetAxisW( Float3 a ) { Values[12] = a.X; Values[13] = a.Y; Values[14] = a.Z; Values[15] = 1.0f; }

	public final Float3 TransformProjection( Float3 a )
	{
		float x = a.X * Values[0] + a.Y * Values[4] + a.Z * Values[8] + Values[12];
		float y = a.X * Values[1] + a.Y * Values[5] + a.Z * Values[9] + Values[13];
		float z = a.X * Values[2] + a.Y * Values[6] + a.Z * Values[10] + Values[14];
		float w = a.X * Values[3] + a.Y * Values[7] + a.Z * Values[11] + Values[15];

		if( w != 0.0f )
		{
			return new Float3( x/w, y/w, z/w );
		}
		else
		{
			return new Float3( 0.0f, 0.0f, 0.0f );
		}
	}

	public final Float3 TransformProjection( Float3 dst, Float3 a )
	{
		float x = a.X * Values[0] + a.Y * Values[4] + a.Z * Values[8] + Values[12];
		float y = a.X * Values[1] + a.Y * Values[5] + a.Z * Values[9] + Values[13];
		float z = a.X * Values[2] + a.Y * Values[6] + a.Z * Values[10] + Values[14];
		float w = a.X * Values[3] + a.Y * Values[7] + a.Z * Values[11] + Values[15];

		if( w != 0.0f )
		{
			return dst.Set( x/w, y/w, z/w );
		}
		else
		{
			return dst.Set( 0.0f, 0.0f, 0.0f );
		}
	}

	public static final Float4 Mul(Float4 a, Float4x4 b)
	{
		return new Float4(
			a.X * b.Values[0] + a.Y * b.Values[4] + a.Z * b.Values[8] + a.W * b.Values[12],
			a.X * b.Values[1] + a.Y * b.Values[5] + a.Z * b.Values[9] + a.W * b.Values[13],
			a.X * b.Values[2] + a.Y * b.Values[6] + a.Z * b.Values[10] + a.W * b.Values[14],
			a.X * b.Values[3] + a.Y * b.Values[7] + a.Z * b.Values[11] + a.W * b.Values[15]);
	}

	public static final Float4 Mul( Float4 dst, final Float4 a, final Float4x4 b )
	{
		return dst.Set(
			a.X * b.Values[0] + a.Y * b.Values[4] + a.Z * b.Values[8] + a.W * b.Values[12],
			a.X * b.Values[1] + a.Y * b.Values[5] + a.Z * b.Values[9] + a.W * b.Values[13],
			a.X * b.Values[2] + a.Y * b.Values[6] + a.Z * b.Values[10] + a.W * b.Values[14],
			a.X * b.Values[3] + a.Y * b.Values[7] + a.Z * b.Values[11] + a.W * b.Values[15]);

	}

	public static final Float3 Mul( Float3 dst, final Float4 a, final Float4x4 b )
	{
		return dst.Set(
			a.X * b.Values[0] + a.Y * b.Values[4] + a.Z * b.Values[8] + a.W * b.Values[12],
			a.X * b.Values[1] + a.Y * b.Values[5] + a.Z * b.Values[9] + a.W * b.Values[13],
			a.X * b.Values[2] + a.Y * b.Values[6] + a.Z * b.Values[10] + a.W * b.Values[14] );
	}

	public static final Float3 MulXYZ1( Float3 dst, final Float3 a, final Float4x4 b )
	{
		return dst.Set(
			a.X * b.Values[0] + a.Y * b.Values[4] + a.Z * b.Values[8] + b.Values[12],
			a.X * b.Values[1] + a.Y * b.Values[5] + a.Z * b.Values[9] + b.Values[13],
			a.X * b.Values[2] + a.Y * b.Values[6] + a.Z * b.Values[10] + b.Values[14] );
	}
	
	public static final Float4 MulXYZ1( Float4 dst, final Float3 a, final Float4x4 b )
	{
		return dst.Set(
			a.X * b.Values[0] + a.Y * b.Values[4] + a.Z * b.Values[8] + b.Values[12],
			a.X * b.Values[1] + a.Y * b.Values[5] + a.Z * b.Values[9] + b.Values[13],
			a.X * b.Values[2] + a.Y * b.Values[6] + a.Z * b.Values[10] + b.Values[14],
			a.X * b.Values[3] + a.Y * b.Values[7] + a.Z * b.Values[11] + b.Values[15]
		);
	}
	
	
	
	public static final Float4x4 Mul( Float4x4 a, Float4x4 b)
	{
		return new Float4x4(
			a.Values[0] * b.Values[0] + a.Values[1] * b.Values[4] + a.Values[2] * b.Values[8] + a.Values[3] * b.Values[12],
			a.Values[0] * b.Values[1] + a.Values[1] * b.Values[5] + a.Values[2] * b.Values[9] + a.Values[3] * b.Values[13],
			a.Values[0] * b.Values[2] + a.Values[1] * b.Values[6] + a.Values[2] * b.Values[10] + a.Values[3] * b.Values[14],
			a.Values[0] * b.Values[3] + a.Values[1] * b.Values[7] + a.Values[2] * b.Values[11] + a.Values[3] * b.Values[15],

			a.Values[4] * b.Values[0] + a.Values[5] * b.Values[4] + a.Values[6] * b.Values[8] + a.Values[7] * b.Values[12],
			a.Values[4] * b.Values[1] + a.Values[5] * b.Values[5] + a.Values[6] * b.Values[9] + a.Values[7] * b.Values[13],
			a.Values[4] * b.Values[2] + a.Values[5] * b.Values[6] + a.Values[6] * b.Values[10] + a.Values[7] * b.Values[14],
			a.Values[4] * b.Values[3] + a.Values[5] * b.Values[7] + a.Values[6] * b.Values[11] + a.Values[7] * b.Values[15],

			a.Values[8] * b.Values[0] + a.Values[9] * b.Values[4] + a.Values[10] * b.Values[8] + a.Values[11] * b.Values[12],
			a.Values[8] * b.Values[1] + a.Values[9] * b.Values[5] + a.Values[10] * b.Values[9] + a.Values[11] * b.Values[13],
			a.Values[8] * b.Values[2] + a.Values[9] * b.Values[6] + a.Values[10] * b.Values[10] + a.Values[11] * b.Values[14],
			a.Values[8] * b.Values[3] + a.Values[9] * b.Values[7] + a.Values[10] * b.Values[11] + a.Values[11] * b.Values[15],

			a.Values[12] * b.Values[0] + a.Values[13] * b.Values[4] + a.Values[14] * b.Values[8] + a.Values[15] * b.Values[12],
			a.Values[12] * b.Values[1] + a.Values[13] * b.Values[5] + a.Values[14] * b.Values[9] + a.Values[15] * b.Values[13],
			a.Values[12] * b.Values[2] + a.Values[13] * b.Values[6] + a.Values[14] * b.Values[10] + a.Values[15] * b.Values[14],
			a.Values[12] * b.Values[3] + a.Values[13] * b.Values[7] + a.Values[14] * b.Values[11] + a.Values[15] * b.Values[15]);
	}

	public static final Float4x4 Mul( Float4x4 dst, Float4x4 a, Float4x4 b)
	{
		return dst.Set(
			a.Values[0] * b.Values[0] + a.Values[1] * b.Values[4] + a.Values[2] * b.Values[8] + a.Values[3] * b.Values[12],
			a.Values[0] * b.Values[1] + a.Values[1] * b.Values[5] + a.Values[2] * b.Values[9] + a.Values[3] * b.Values[13],
			a.Values[0] * b.Values[2] + a.Values[1] * b.Values[6] + a.Values[2] * b.Values[10] + a.Values[3] * b.Values[14],
			a.Values[0] * b.Values[3] + a.Values[1] * b.Values[7] + a.Values[2] * b.Values[11] + a.Values[3] * b.Values[15],

			a.Values[4] * b.Values[0] + a.Values[5] * b.Values[4] + a.Values[6] * b.Values[8] + a.Values[7] * b.Values[12],
			a.Values[4] * b.Values[1] + a.Values[5] * b.Values[5] + a.Values[6] * b.Values[9] + a.Values[7] * b.Values[13],
			a.Values[4] * b.Values[2] + a.Values[5] * b.Values[6] + a.Values[6] * b.Values[10] + a.Values[7] * b.Values[14],
			a.Values[4] * b.Values[3] + a.Values[5] * b.Values[7] + a.Values[6] * b.Values[11] + a.Values[7] * b.Values[15],

			a.Values[8] * b.Values[0] + a.Values[9] * b.Values[4] + a.Values[10] * b.Values[8] + a.Values[11] * b.Values[12],
			a.Values[8] * b.Values[1] + a.Values[9] * b.Values[5] + a.Values[10] * b.Values[9] + a.Values[11] * b.Values[13],
			a.Values[8] * b.Values[2] + a.Values[9] * b.Values[6] + a.Values[10] * b.Values[10] + a.Values[11] * b.Values[14],
			a.Values[8] * b.Values[3] + a.Values[9] * b.Values[7] + a.Values[10] * b.Values[11] + a.Values[11] * b.Values[15],

			a.Values[12] * b.Values[0] + a.Values[13] * b.Values[4] + a.Values[14] * b.Values[8] + a.Values[15] * b.Values[12],
			a.Values[12] * b.Values[1] + a.Values[13] * b.Values[5] + a.Values[14] * b.Values[9] + a.Values[15] * b.Values[13],
			a.Values[12] * b.Values[2] + a.Values[13] * b.Values[6] + a.Values[14] * b.Values[10] + a.Values[15] * b.Values[14],
			a.Values[12] * b.Values[3] + a.Values[13] * b.Values[7] + a.Values[14] * b.Values[11] + a.Values[15] * b.Values[15]);
	}

	/*
	 public static Float4x4 Mul( Float4x4...m )
	 {
	 Float4x4 result = Float4x4.Mov( m[0] );
	 for( int i = 1 ; i < m.length ; i++ )
	 {
	 result = Float4x4.Mul( result, m[i] );
	 }

	 return result;
	 }
	 */

	//private static Float4x4 temp = new Float4x4();
	public static final Float4x4 Mul( Float4x4 dst, Float4x4 a, Float4x4 b, Float4x4...m )
	{

		Float4x4 result = Mul( a, b );

		for( int i = 0 ; i < m.length ; i++ )
		{
			result = Float4x4.Mul( result, m[i] );
		}

		dst.Set( result );
		return dst;
	}


	public static final Float4x4 Identity()
	{
		return new Float4x4(
			1.0f, 0.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f, 0.0f,
			0.0f, 0.0f, 1.0f, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f);
	}


	public static final Float4x4 Identity( Float4x4 dst )
	{
		return dst.Set(
			1.0f, 0.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f, 0.0f,
			0.0f, 0.0f, 1.0f, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f);
	}

	public static final Float4x4 Transpose(Float4x4 a)
	{
		return new Float4x4(
			a.Values[0], a.Values[4], a.Values[8], a.Values[12],
			a.Values[1], a.Values[5], a.Values[9], a.Values[13],
			a.Values[2], a.Values[6], a.Values[10], a.Values[14],
			a.Values[3], a.Values[7], a.Values[11], a.Values[15]);
	}

	public static final Float4x4 Transpose(Float4x4 dst, Float4x4 a)
	{
		return dst.Set(
			a.Values[0], a.Values[4], a.Values[8], a.Values[12],
			a.Values[1], a.Values[5], a.Values[9], a.Values[13],
			a.Values[2], a.Values[6], a.Values[10], a.Values[14],
			a.Values[3], a.Values[7], a.Values[11], a.Values[15]);
	}


	public static final Float4x4 Invert(Float4x4 a)
	{
		Float4x4 result = new Float4x4();
		Matrix.invertM(result.Values, 0, a.Values, 0);
		return result;
	}

	public static final Float4x4 Invert( Float4x4 dst, Float4x4 a )
	{
		Matrix.invertM(dst.Values, 0, a.Values, 0);
		return dst;

	}

	public static final Float4x4 Mov( Float4x4 dst, Float4x4 a )
	{
		return dst.Set( a );
	}

	public static final Float4x4 Rotation( Quaternion q )
	{
		float qw = q.W, qx = q.X, qy = q.Y, qz = q.Z;

		float x2 = 2.0f * qx * qx;
		float y2 = 2.0f * qy * qy;
		float z2 = 2.0f * qz * qz;

		float xy = 2.0f * qx * qy;
		float yz = 2.0f * qy * qz;
		float zx = 2.0f * qz * qx;

		float wx = 2.0f * qw * qx;
		float wy = 2.0f * qw * qy;
		float wz = 2.0f * qw * qz;

		return new Float4x4(
			1.0f - y2 - z2,
			xy - wz,
			zx + wy,
			0.0f,

			xy + wz,
			1.0f - z2 - x2,
			yz - wx,
			0.0f,

			zx - wy,
			yz + wx,
			1.0f - x2 - y2,
			0.0f,

			0.0f, 0.0f, 0.0f, 1.0f );
	}

	public static final Float4x4 Rotation( Float4x4 dst, Quaternion q )
	{
		float qw = q.W, qx = q.X, qy = q.Y, qz = q.Z;

		float x2 = 2.0f * qx * qx;
		float y2 = 2.0f * qy * qy;
		float z2 = 2.0f * qz * qz;

		float xy = 2.0f * qx * qy;
		float yz = 2.0f * qy * qz;
		float zx = 2.0f * qz * qx;

		float wx = 2.0f * qw * qx;
		float wy = 2.0f * qw * qy;
		float wz = 2.0f * qw * qz;

		dst.Set(
			1.0f - y2 - z2,
			xy - wz,
			zx + wy,
			0.0f,

			xy + wz,
			1.0f - z2 - x2,
			yz - wx,
			0.0f,

			zx - wy,
			yz + wx,
			1.0f - x2 - y2,
			0.0f,

			0.0f, 0.0f, 0.0f, 1.0f );

		return dst;
	}


	public static final Float4x4 Rotation( Float4x4 dst, Float3 axis, float angle )
	{
		return Rotation( dst, Quaternion.LoadRotationAxis( Quaternion.Local(), axis, angle ) );
	}

	public static final Float4x4 RotationX( float rad )
	{
		float s = FMath.Sin( rad );
		float c = FMath.Cos( rad );
		return new Float4x4(
			1.0f, 0.0f, 0.0f, 0.0f,
			0.0f, c, s, 0.0f,
			0.0f, -s, c, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f );
	}

	public static final Float4x4 RotationY( float rad )
	{
		float s = FMath.Sin( rad );
		float c = FMath.Cos( rad );
		return new Float4x4(
			c, 0.0f, -s, 0.0f,
			0.0f, 1.0f, 0.0f, 0.0f,
			s, 0.0f, c, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f );
	}

	public static final Float4x4 RotationZ( float rad )
	{
		float s = FMath.Sin( rad );
		float c = FMath.Cos( rad );
		return new Float4x4(
			c, s, 0.0f, 0.0f,
			-s, c, 0.0f, 0.0f,
			0.0f, 0.0f, 1.0f, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f );
	}

	public static final Float4x4 RotationX( Float4x4 dst, float rad )
	{
		float s = FMath.Sin( rad );
		float c = FMath.Cos( rad );
		return dst.Set(
			1.0f, 0.0f, 0.0f, 0.0f,
			0.0f, c, s, 0.0f,
			0.0f, -s, c, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f );
	}

	public static final Float4x4 RotationY( Float4x4 dst, float rad )
	{
		float s = FMath.Sin( rad );
		float c = FMath.Cos( rad );
		return dst.Set(
			c, 0.0f, -s, 0.0f,
			0.0f, 1.0f, 0.0f, 0.0f,
			s, 0.0f, c, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f );
	}

	public static final Float4x4 RotationZ( Float4x4 dst, float rad )
	{
		float s = FMath.Sin( rad );
		float c = FMath.Cos( rad );
		return dst.Set(
			c, s, 0.0f, 0.0f,
			-s, c, 0.0f, 0.0f,
			0.0f, 0.0f, 1.0f, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f );
	}

	public static final Float4x4 Translation( Float3 a )
	{
		return new Float4x4(
			1.0f, 0.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f, 0.0f,
			0.0f, 0.0f, 1.0f, 0.0f,
			a.X, a.Y, a.Z, 1.0f);

	}

	public static final Float4x4 Translation( float x, float y, float z )
	{
		return new Float4x4(
			1.0f, 0.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f, 0.0f,
			0.0f, 0.0f, 1.0f, 0.0f,
			x, y, z, 1.0f);
	}

	public static final Float4x4 Translation( Float4x4 dst, Float3 a )
	{
		return dst.Set(
			1.0f, 0.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f, 0.0f,
			0.0f, 0.0f, 1.0f, 0.0f,
			a.X, a.Y, a.Z, 1.0f);

	}

	public static final Float4x4 Translation( Float4x4 dst, float x, float y, float z )
	{
		return dst.Set(
			1.0f, 0.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f, 0.0f,
			0.0f, 0.0f, 1.0f, 0.0f,
			x, y, z, 1.0f);
	}


	public static final Float4x4 Scaling( float x, float y, float z )
	{
		return new Float4x4(
			x, 0.0f, 0.0f, 0.0f,
			0.0f, y, 0.0f, 0.0f,
			0.0f, 0.0f, z, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f);
	}

	public static final Float4x4 Scaling( Float3 a )
	{
		return new Float4x4(
			a.X, 0.0f, 0.0f, 0.0f,
			0.0f, a.Y, 0.0f, 0.0f,
			0.0f, 0.0f, a.Z, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f);
	}

	public static final Float4x4 Scaling( Float4x4 dst, float x, float y, float z )
	{
		return dst.Set(
			x, 0.0f, 0.0f, 0.0f,
			0.0f, y, 0.0f, 0.0f,
			0.0f, 0.0f, z, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f);
	}
	
	public static final Float4x4 Scaling( Float4x4 dst, float s )
	{
		return dst.Set(
			s, 0.0f, 0.0f, 0.0f,
			0.0f, s, 0.0f, 0.0f,
			0.0f, 0.0f, s, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f);
	}
	
	public static final Float4x4 Scaling( Float4x4 dst, Float3 a )
	{
		return dst.Set(
			a.X, 0.0f, 0.0f, 0.0f,
			0.0f, a.Y, 0.0f, 0.0f,
			0.0f, 0.0f, a.Z, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f);
	}

	public static final Float4x4 LookAt( Float3 v3Eye, Float3 v3At, Float3 v3Up )
	{
		Float4x4 result = Identity();
		Matrix.setLookAtM( result.Values, 0, 
						  v3Eye.X, v3Eye.Y, v3Eye.Z,
						  v3At.X, v3At.Y, v3At.Z,
						  v3Up.X, v3Up.Y, v3Up.Z );

		return result;
	}
	
	public static final Float4x4 LookAt( Float4x4 dst, Float3 v3Eye, Float3 v3At, Float3 v3Up )
	{
		Matrix.setLookAtM( dst.Values, 0, 
						  v3Eye.X, v3Eye.Y, v3Eye.Z,
						  v3At.X, v3At.Y, v3At.Z,
						  v3Up.X, v3Up.Y, v3Up.Z );

		return dst;
	}

	public static final Float4x4 Perspective( float fov, float aspect, float near, float far) 
	{
		Float4x4 out = Float4x4.Identity();
		float top = near * (float)Math.tan( fov*0.5f );
		float bottom = -top; 
		float left = bottom * aspect; 
		float right = top * aspect; 
		Matrix.frustumM(out.Values, 0, left, right, bottom, top, near, far); 
		return out;
	}
	
	public static final Float4x4 Perspective( Float4x4 dst, float fov, float aspect, float near, float far) 
	{
		//Float4x4 out = Float4x4.Identity();
		float top = near * (float)Math.tan( fov*0.5f );
		float bottom = -top; 
		float left = bottom * aspect; 
		float right = top * aspect; 
		Matrix.frustumM(dst.Values, 0, left, right, bottom, top, near, far); 
		return dst;
	}
	
	public static final Float4x4 Frustum( Float4x4 dst, float left, float right, float bottom, float top, float near, float far )
	{
		Matrix.frustumM(dst.Values, 0, left, right, bottom, top, near, far); 
		return dst;
	}

	public static final Float4x4 Ortho( float l, float r, float b, float t, float n, float f )
	{
		Float4x4 result = Float4x4.Identity();
		Matrix.orthoM( result.Values, 0, l, r, b, t, n, f );
		return result;
	}	
	
	public static final Float4x4 Ortho( Float4x4 dst,  float l, float r, float b, float t, float n, float f )
	{
		Matrix.orthoM( dst.Values, 0, l, r, b, t, n, f );
		return dst;
	}	

	public static final class Float4x4List
	{
		public final Float4x4[] Values = new Float4x4[256];
		public Float4x4List()
		{
			for( int i = 0 ; i < Values.length ; i++ )
			{
				Values[i] = new Float4x4();
			}
		}

		private int m_iPos = 0;
		public Float4x4 Get()
		{
			if( m_iPos < Values.length )
			{
				m_iPos++;
				return Values[m_iPos-1];
			}
			else
			{
				m_iPos = 1;
				return Values[0];
			}
		}
	}

	private static final Float4x4List m_locals = new Float4x4List();

	public static final Float4x4 Local() { return m_locals.Get(); }
	public static final Float4x4 Local( Float3x3 r, Float3 t ) { return m_locals.Get().Set( r, t ); }

	public static final Float4x4 Local( Mat3 m )
	{
		return m_locals.Get().Set(
			m.X.X, m.X.Y, m.X.Z, 0.0f,
			m.Y.X, m.Y.Y, m.Y.Z, 0.0f,
			m.Z.X, m.Z.Y, m.Z.Z, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f );
	}

	public static final Float4x4 Local( Transform3 m )
	{
		return m_locals.Get().Set(
			m.X.X, m.X.Y, m.X.Z, 0.0f,
			m.Y.X, m.Y.Y, m.Y.Z, 0.0f,
			m.Z.X, m.Z.Y, m.Z.Z, 0.0f,
			m.W.X, m.W.Y, m.W.Z, 1.0f );
	}
	
	public static final Float4x4 Local( Float3 f3X, Float3 f3Y, Float3 f3Z, Float3 f3W )
	{
		return m_locals.Get().Set(
			f3X.X, f3X.Y, f3X.Z, 0.0f,
			f3Y.X, f3Y.Y, f3Y.Z, 0.0f,
			f3Z.X, f3Z.Y, f3Z.Z, 0.0f,
			f3W.X, f3W.Y, f3W.Z, 1.0f );
	}
}

	

	
