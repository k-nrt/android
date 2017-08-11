package com.nrt.math;

public final class Mat3
{
	public final Vec3 X = new Vec3(); 
	public final Vec3 Y = new Vec3(); 
	public final Vec3 Z = new Vec3(); 

	public Mat3 Load( 
		float xx, float xy, float xz,
		float yx, float yy, float yz,
		float zx, float zy, float zz )
	{
		X.X=xx; X.Y=xy; X.Z=xz; X.W=0.0f;
		Y.X=yx; Y.Y=yy; Y.Z=yz; Y.W=0.0f;
		Z.X=zx; Z.Y=zy; Z.Z=zz; Z.W=0.0f;
		return this;
	}	

	public Mat3 Load( float[] f16 ) { X.Load(f16,0); Y.Load(f16,4); Z.Load(f16,8); return this; }
	public Mat3 Load( Mat3 m ) { X.Load( m.X ); Y.Load( m.Y ); Z.Load( m.Z ); return this; }
	public Mat3 Store( float[] f16 ) { X.Store(f16,0); Y.Store(f16,4); Z.Store(f16,8); return this; }	

	public Mat3 Mul( Mat3 r, Mat3 a, Mat3 b)
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

		//float wx = a.W.X*b.X.X + a.W.Y*b.Y.X + a.W.Z*b.Z.X + b.W.X;
		//float wy = a.W.X*b.X.Y + a.W.Y*b.Y.Y + a.W.Z*b.Z.Y + b.W.Y;
		//float wz = a.W.X*b.X.Z + a.W.Y*b.Y.Z + a.W.Z*b.Z.Z + b.W.Z;
		//float ww = a.W.X*b.X.W + a.W.Y*b.Y.W + a.W.Z*b.Z.W + a.W.W*b.W.W;

		r.X.Load( xx, xy, xz );
		r.Y.Load( yx, yy, yz );
		r.Z.Load( zx, zy, zz );

		return r;
	}

	public static Mat3 LoadRotationX( Mat3 dst, float rad )
	{
		float s = FMath.Sin( rad );
		float c = FMath.Cos( rad );
		return dst.Load(
			1.0f, 0.0f, 0.0f,
			0.0f, c, s, 
			0.0f, -s, c );
	}

	public static Mat3 LoadRotationY( Mat3 dst, float rad )
	{
		float s = FMath.Sin( rad );
		float c = FMath.Cos( rad );
		return dst.Load(
			c, 0.0f, -s, 
			0.0f, 1.0f, 0.0f,
			s, 0.0f, c );
	}

	public static Mat3 LoadRotationZ( Mat3 dst, float rad )
	{
		float s = FMath.Sin( rad );
		float c = FMath.Cos( rad );
		return dst.Load(
			c, s, 0.0f, 
			-s, c, 0.0f, 
			0.0f, 0.0f, 1.0f );
	}
	
	public static Mat3 LoadScaling( Mat3 dst, float x, float y, float z )
	{
		return dst.Load(
			x, 0.0f, 0.0f,
			0.0f, y, 0.0f,
			0.0f, 0.0f, z );
	}
	
	public static Mat3 LoadScaling( Mat3 dst, float s)
	{
		return dst.Load(
			s, 0.0f, 0.0f,
			0.0f, s, 0.0f,
			0.0f, 0.0f, s );
	}
	private static LocalList<Mat3> m_locals = new LocalList<Mat3>( CreateLocals( 1024 ) );

	private static Mat3[] CreateLocals( int nbLocals )
	{
		Mat3[] result = new Mat3[nbLocals];
		for( int i = 0 ; i < result.length ; i++ )
		{
			result[i] = new Mat3();
		}

		return result;
	}

	public static Mat3 Local() { return m_locals.Local(); }	
	

}
