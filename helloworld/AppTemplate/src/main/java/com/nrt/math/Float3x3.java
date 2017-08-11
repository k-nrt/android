package com.nrt.math;

import com.nrt.basic.DebugLog;

public final class Float3x3
{
	public final float[] Values = 
	{
		0.0f, 0.0f, 0.0f,
		0.0f, 0.0f, 0.0f,
		0.0f, 0.0f, 0.0f,		
	};
	//public Float3 X() { return new Float3( Values[0], Values[1], Values[2] ); }
	//public Float3 Y() { return new Float3( Values[3], Values[4], Values[5] ); }
	//public Float3 Z() { return new Float3( Values[6], Values[7], Values[8] ); }

	public Float3x3()
	{}

	public Float3x3( float[] values )
	{
		for( int i = 0 ; i < Values.length ; i++ )
		{
			Values[i] = values[i];
		}
	}

	public Float3x3( float xx, float xy, float xz, float yx, float yy, float yz, float zx, float zy, float zz )
	{
		Values[0] = xx; Values[1] = xy; Values[2] = xz; 
		Values[3] = yx; Values[4] = yy; Values[5] = yz; 
		Values[6] = zx; Values[7] = zy; Values[8] = zz; 
	}

	public Float3x3( Float3 x, Float3 y, Float3 z )
	{
		Values[0] = x.X; Values[1] = x.Y; Values[2] = x.Z;
		Values[3] = y.X; Values[4] = y.Y; Values[5] = y.Z;
		Values[6] = z.X; Values[7] = z.Y; Values[8] = z.Z;
	}

	public Float3x3 Set( Float3 x, Float3 y, Float3 z )
	{
		Values[0] = x.X; Values[1] = x.Y; Values[2] = x.Z;
		Values[3] = y.X; Values[4] = y.Y; Values[5] = y.Z;
		Values[6] = z.X; Values[7] = z.Y; Values[8] = z.Z;
		return this;
	}

	public Float3x3( Float4x4 a )
	{
		Values[0] = a.Values[0]; Values[1] = a.Values[1]; Values[2] = a.Values[2];
		Values[3] = a.Values[4]; Values[4] = a.Values[5]; Values[5] = a.Values[6];
		Values[6] = a.Values[8]; Values[7] = a.Values[9]; Values[8] = a.Values[10];
	}

	/*
	 public Float3x3 Clone()
	 {
	 return new Float3x3( Values );
	 }
	 */
	public Float3x3 Set( float xx, float xy, float xz, float yx, float yy, float yz, float zx, float zy, float zz )
	{
		Values[0] = xx; Values[1] = xy; Values[2] = xz; 
		Values[3] = yx; Values[4] = yy; Values[5] = yz; 
		Values[6] = zx; Values[7] = zy; Values[8] = zz; 
		return this;
	}

	public Float3x3 Set( Float4x4 a )
	{
		Values[0] = a.Values[0]; Values[1] = a.Values[1]; Values[2] = a.Values[2];
		Values[3] = a.Values[4]; Values[4] = a.Values[5]; Values[5] = a.Values[6];
		Values[6] = a.Values[8]; Values[7] = a.Values[9]; Values[8] = a.Values[10];
		return this;
	}

	public Float3x3 Set( Float3x3 a )
	{
		Values[0] = a.Values[0]; Values[1] = a.Values[1]; Values[2] = a.Values[2];
		Values[3] = a.Values[3]; Values[4] = a.Values[4]; Values[5] = a.Values[5];
		Values[6] = a.Values[6]; Values[7] = a.Values[7]; Values[8] = a.Values[8];
		return this;
	}

	/*
	 public static Float3 Mul(Float3 a, Float3x3 b)
	 {
	 return new Float3(
	 a.X * b.Values[0] + a.Y * b.Values[3] + a.Z * b.Values[6],
	 a.X * b.Values[1] + a.Y * b.Values[4] + a.Z * b.Values[7],
	 a.X * b.Values[2] + a.Y * b.Values[5] + a.Z * b.Values[8] );
	 }
	 */

	public static Float3 Mul( Float3 dst, Float3 a, Float3x3 b)
	{
		return dst.Set(
			a.X * b.Values[0] + a.Y * b.Values[3] + a.Z * b.Values[6],
			a.X * b.Values[1] + a.Y * b.Values[4] + a.Z * b.Values[7],
			a.X * b.Values[2] + a.Y * b.Values[5] + a.Z * b.Values[8] );
	}

	/*
	 public static Float3x3 Mul( Float3x3 a, Float3x3 b )
	 {
	 return new Float3x3(
	 a.Values[0] * b.Values[0] + a.Values[1] * b.Values[3] + a.Values[2] * b.Values[6],
	 a.Values[0] * b.Values[1] + a.Values[1] * b.Values[4] + a.Values[2] * b.Values[7],
	 a.Values[0] * b.Values[2] + a.Values[1] * b.Values[5] + a.Values[2] * b.Values[8],

	 a.Values[3] * b.Values[0] + a.Values[4] * b.Values[3] + a.Values[5] * b.Values[6],
	 a.Values[3] * b.Values[1] + a.Values[4] * b.Values[4] + a.Values[5] * b.Values[7],
	 a.Values[3] * b.Values[2] + a.Values[4] * b.Values[5] + a.Values[5] * b.Values[8],

	 a.Values[6] * b.Values[0] + a.Values[7] * b.Values[3] + a.Values[8] * b.Values[6],
	 a.Values[6] * b.Values[1] + a.Values[7] * b.Values[4] + a.Values[8] * b.Values[7],
	 a.Values[6] * b.Values[2] + a.Values[7] * b.Values[5] + a.Values[8] * b.Values[8]
	 );	
	 }
	 */
	public static Float3x3 Mul( Float3x3 dst, Float3x3 a, Float3x3 b )
	{
		return dst.Set(
			a.Values[0] * b.Values[0] + a.Values[1] * b.Values[3] + a.Values[2] * b.Values[6],
			a.Values[0] * b.Values[1] + a.Values[1] * b.Values[4] + a.Values[2] * b.Values[7],
			a.Values[0] * b.Values[2] + a.Values[1] * b.Values[5] + a.Values[2] * b.Values[8],

			a.Values[3] * b.Values[0] + a.Values[4] * b.Values[3] + a.Values[5] * b.Values[6],
			a.Values[3] * b.Values[1] + a.Values[4] * b.Values[4] + a.Values[5] * b.Values[7],
			a.Values[3] * b.Values[2] + a.Values[4] * b.Values[5] + a.Values[5] * b.Values[8],

			a.Values[6] * b.Values[0] + a.Values[7] * b.Values[3] + a.Values[8] * b.Values[6],
			a.Values[6] * b.Values[1] + a.Values[7] * b.Values[4] + a.Values[8] * b.Values[7],
			a.Values[6] * b.Values[2] + a.Values[7] * b.Values[5] + a.Values[8] * b.Values[8]
		);	
	}

	/*
	 public static Float3x3 Rotation( Quaternion q )
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

	 return new Float3x3(
	 1.0f - y2 - z2,
	 xy - wz,
	 zx + wy,


	 xy + wz,
	 1.0f - z2 - x2,
	 yz - wx,


	 zx - wy,
	 yz + wx,
	 1.0f - x2 - y2
	 );
	 }
	 */
	public static Float3x3 Rotation( Float3x3 dst, Quaternion q )
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

		return dst.Set(
			1.0f - y2 - z2,
			xy - wz,
			zx + wy,


			xy + wz,
			1.0f - z2 - x2,
			yz - wx,


			zx - wy,
			yz + wx,
			1.0f - x2 - y2
		);
	}


	public static Float3x3 RotationX( Float3x3 dst, float rad )
	{
		float s = FMath.Sin( rad );
		float c = FMath.Cos( rad );
		return dst.Set(
			1.0f, 0.0f, 0.0f,
			0.0f, c, s,
			0.0f, -s, c );
	}

	public static Float3x3 RotationY( Float3x3 dst, float rad )
	{
		float s = FMath.Sin( rad );
		float c = FMath.Cos( rad );
		return dst.Set(
			c, 0.0f, -s,
			0.0f, 1.0f, 0.0f,
			s, 0.0f, c );
	}

	public static Float3x3 RotationZ( Float3x3 dst, float rad )
	{
		float s = FMath.Sin( rad );
		float c = FMath.Cos( rad );
		return dst.Set(
			c, s, 0.0f,
			-s, c, 0.0f,
			0.0f, 0.0f, 1.0f );
	}

	public static Float3x3 RotationZYX( Float3x3 dst, Float3 a )
	{
		float sx = FMath.Sin( a.Z );
		float cx = FMath.Cos( a.Z );
		float sy = FMath.Sin( a.Y );
		float cy = FMath.Cos( a.Y );
		float sz = FMath.Sin( a.X );
		float cz = FMath.Cos( a.X );

		return dst.Set(
			cx*cy, cy*sx, -sy,
			-cz*sx + cx*sy*sz, cx*cz+sx*sy*sz, cy*sz,
			cx*cz*sy+sx*sz, cz*sx*sy-cx*sz,cx*cz ); 

	}

	public Float3 GetEulerRotationXYZ( Float3 dst )
	{
		// Assuming the angles are in radians.
		float heading, attitude, bank;
		if (Values[3] > 0.998f)
		{
			// singularity at north pole
			heading = FMath.Atan2(Values[2],Values[8]);
			attitude = FMath.PI/2.0f;
			bank = 0.0f;
		}
		else if (Values[3] < -0.998f)
		{
			// singularity at south pole
			heading = FMath.Atan2(Values[2],Values[8]);
			attitude = -FMath.PI/2.0f;
			bank = 0;
		}
		heading = FMath.Atan2(-Values[6],Values[0]);
		bank = FMath.Atan2(-Values[5],Values[4]);
		attitude = FMath.Asin(Values[3]);
		return dst.Set( bank, heading, attitude );
	}

	public void Dump()
	{
		DebugLog.Error.WriteLine( String.format( "%f %f %f", Values[0], Values[1], Values[2] ) );
		DebugLog.Error.WriteLine( String.format( "%f %f %f", Values[3], Values[4], Values[5] ) );
		DebugLog.Error.WriteLine( String.format( "%f %f %f", Values[6], Values[7], Values[8] ) );
	}

	public static Float3x3[] CreateArray( int nbElements )
	{
		Float3x3[] result = new Float3x3[nbElements];
		for( int i = 0 ; i < result.length ; i++ )
		{
			result[i] = new Float3x3();
		}
		return result;
	}

	public static final LocalList<Float3x3> m_locals = new LocalList<Float3x3>( CreateArray( 128 ) );
	public static Float3x3 Local() { return m_locals.Local(); }
	public static Float3x3 Local( Float3 x, Float3 y, Float3 z ) { return m_locals.Local().Set( x, y, z ); }

	/*
	 public static final class Float3x3List
	 {
	 public final Float3x3[] Values = new Float3x3[256];
	 public Float3x3List()
	 {
	 for( int i = 0 ; i < Values.length ; i++ )
	 {
	 Values[i] = new Float3x3();
	 }
	 }

	 private int m_iPos = 0;
	 public Float3x3 Get()
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

	 private static final Float3x3List m_locals = new Float3x3List();

	 public static Float3x3 Local() { return m_locals.Get(); }
	 public static Float3x3 Local( Float3 x, Float3 y, Float3 z ) { return m_locals.Get().Set( x, y, z ); }
	 */
}
