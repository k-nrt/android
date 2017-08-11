package com.nrt.math;

public final class Transform3
{
	public final Float3 X = new Float3(); 
	public final Float3 Y = new Float3(); 
	public final Float3 Z = new Float3(); 
	public final Float3 W = new Float3(); 
	
	public Transform3 Load( float[] v, int o )
	{
		X.X=v[o+0]; X.Y=v[o+1]; X.Z=v[o+2];
		Y.X=v[o+3]; Y.Y=v[o+4]; Y.Z=v[o+5];
		Z.X=v[o+6]; Z.Y=v[o+7]; Z.Z=v[o+8];
		W.X=v[o+9]; W.Y=v[o+10]; W.Z=v[o+11];
		return this;
	}

	public Transform3 Load( Float4x4 a )
	{
		float[] v = a.Values;
		X.X=v[0]; X.Y=v[1]; X.Z=v[2];
		Y.X=v[4]; Y.Y=v[5]; Y.Z=v[6];
		Z.X=v[8]; Z.Y=v[9]; Z.Z=v[10];
		W.X=v[12]; W.Y=v[13]; W.Z=v[14];
		return this;
	}
	
	
	public Transform3 Load( Transform3 t )
	{
		X.X=t.X.X; X.Y=t.X.Y; X.Z=t.X.Z;
		Y.X=t.Y.X; Y.Y=t.Y.Y; Y.Z=t.Y.Z;
		Z.X=t.Z.X; Z.Y=t.Z.Y; Z.Z=t.Z.Z;
		W.X=t.W.X; W.Y=t.W.Y; W.Z=t.W.Z;
		return this;
	}

	public Transform3 Load( 
		float xx, float xy, float xz,
		float yx, float yy, float yz,
		float zx, float zy, float zz,
		float wx, float wy, float wz )
	{
		X.X=xx; X.Y=xy; X.Z=xz;
		Y.X=yx; Y.Y=yy; Y.Z=yz;
		Z.X=zx; Z.Y=zy; Z.Z=zz;
		W.X=wx; W.Y=wy; W.Z=wz;
		return this;
	}	

	public static Transform3 LoadRotationX( Transform3 dst, float rad )
	{
		float s = FMath.Sin( rad );
		float c = FMath.Cos( rad );
		return dst.Load(
			1.0f, 0.0f, 0.0f,
			0.0f, c, s, 
			0.0f, -s, c,
			0.0f, 0.0f, 0.0f );
	}
	
	/*
	public static Transform3 RotationY( Transform3 dst, float rad )
	{
		return (Transform3) Mat3.LoadRotationY( dst, rad );		
	}

	public static Transform3 LoadRotationZ( Transform3 dst, float rad )
	{
		return (Transform3) Mat3.LoadRotationZ( dst, rad );		
	}
	
	public static Transform3 LoadTranslation( Transform3 dst, float x, float y, float z )
	{
		return dst.Load(
			1.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 0.0f, 1.0f,
			x,y,z );
	}
	
	public static Transform3 LoadTranslation( Transform3 dst, Vec3 t )
	{
		return dst.Load(
			1.0f, 0.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 0.0f, 1.0f,
			t.X, t.Y, t.Z );
	}
	
	public static Transform3 LoadScaling( Transform3 dst, float x, float y, float z )
	{
		return (Transform3) Mat3.LoadScaling( (Mat3) dst, x, y, z );
	}
	
	public static Transform3 LoadScaling( Transform3 dst, float s )
	{
		return (Transform3) Mat3.LoadScaling( (Mat3) dst, s );
	}
	
*/
	public static Transform3 Mul( Transform3 r, Transform3 a, Transform3 b)
	{
		float xx = a.X.X*b.X.X + a.X.Y*b.Y.X + a.X.Z*b.Z.X;
		float xy = a.X.X*b.X.Y + a.X.Y*b.Y.Y + a.X.Z*b.Z.Y;
		float xz = a.X.X*b.X.Z + a.X.Y*b.Y.Z + a.X.Z*b.Z.Z;
		//float xw = a.X.X*b.X.W + a.X.Y*b.Y.W + a.X.Z*b.Z.W + a.X.W*b.W.W;

		float yx = a.Y.X*b.X.X + a.Y.Y*b.Y.X + a.Y.Z*b.Z.X;
		float yy = a.Y.X*b.X.Y + a.Y.Y*b.Y.Y + a.Y.Z*b.Z.Y;
		float yz = a.Y.X*b.X.Z + a.Y.Y*b.Y.Z + a.Y.Z*b.Z.Z;
		//float yw = a.Y.X*b.X.W + a.Y.Y*b.Y.W + a.Y.Z*b.Z.W + a.Y.W*b.W.W;

		float zx = a.Z.X*b.X.X + a.Z.Y*b.Y.X + a.Z.Z*b.Z.X;
		float zy = a.Z.X*b.X.Y + a.Z.Y*b.Y.Y + a.Z.Z*b.Z.Y;
		float zz = a.Z.X*b.X.Z + a.Z.Y*b.Y.Z + a.Z.Z*b.Z.Z;
		//float zw = a.Z.X*b.X.W + a.Z.Y*b.Y.W + a.Z.Z*b.Z.W + a.Z.W*b.W.W;

		float wx = a.W.X*b.X.X + a.W.Y*b.Y.X + a.W.Z*b.Z.X + b.W.X;
		float wy = a.W.X*b.X.Y + a.W.Y*b.Y.Y + a.W.Z*b.Z.Y + b.W.Y;
		float wz = a.W.X*b.X.Z + a.W.Y*b.Y.Z + a.W.Z*b.Z.Z + b.W.Z;
		//float ww = a.W.X*b.X.W + a.W.Y*b.Y.W + a.W.Z*b.Z.W + a.W.W*b.W.W;

		r.X.Set( xx, xy, xz );
		r.Y.Set( yx, yy, yz );
		r.Z.Set( zx, zy, zz );
		r.W.Set( wx, wy, wz );

		return r;
	}
	
	private static LocalList<Transform3> m_locals = new LocalList<Transform3>( CreateLocals( 1024 ) );

	private static Transform3[] CreateLocals( int nbLocals )
	{
		Transform3[] result = new Transform3[nbLocals];
		for( int i = 0 ; i < result.length ; i++ )
		{
			result[i] = new Transform3();
		}

		return result;
	}

	public static Transform3 Local() { return m_locals.Local(); }	
}
