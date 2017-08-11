package com.nrt.math;


public class Mat4
{
	public final Float4 X = new Float4();
	public final Float4 Y = new Float4();
	public final Float4 Z = new Float4();
	public final Float4 W = new Float4();
	
	public Mat4 Load( 
		float xx, float xy, float xz, float xw,
		float yx, float yy, float yz, float yw,
		float zx, float zy, float zz, float zw,
		float wx, float wy, float wz, float ww )
	{
		X.X=xx; X.Y=xy; X.Z=xz; X.W=xw;
		Y.X=yx; Y.Y=yy; Y.Z=yz; Y.W=yw;
		Z.X=zx; Z.Y=zy; Z.Z=zz; Z.W=zw;
		W.X=wx; W.Y=wy; W.Z=wz; W.W=ww;
		return this;
	}	



	//public static LocalList<Mat4> Locals = new LocalList<Mat4>( new Mat4[1024], 10 )

	public static Mat4 Mul( Mat4 r, Mat4 a, Mat4 b )
	{
		float xx = a.X.X*b.X.X + a.X.Y*b.Y.X + a.X.Z*b.Z.X + a.X.W*b.W.X;
		float xy = a.X.X*b.X.Y + a.X.Y*b.Y.Y + a.X.Z*b.Z.Y + a.X.W*b.W.Y;
		float xz = a.X.X*b.X.Z + a.X.Y*b.Y.Z + a.X.Z*b.Z.Z + a.X.W*b.W.Z;
		float xw = a.X.X*b.X.W + a.X.Y*b.Y.W + a.X.Z*b.Z.W + a.X.W*b.W.W;

		float yx = a.Y.X*b.X.X + a.Y.Y*b.Y.X + a.Y.Z*b.Z.X + a.Y.W*b.W.X;
		float yy = a.Y.X*b.X.Y + a.Y.Y*b.Y.Y + a.Y.Z*b.Z.Y + a.Y.W*b.W.Y;
		float yz = a.Y.X*b.X.Z + a.Y.Y*b.Y.Z + a.Y.Z*b.Z.Z + a.Y.W*b.W.Z;
		float yw = a.Y.X*b.X.W + a.Y.Y*b.Y.W + a.Y.Z*b.Z.W + a.Y.W*b.W.W;

		float zx = a.Z.X*b.X.X + a.Z.Y*b.Y.X + a.Z.Z*b.Z.X + a.Z.W*b.W.X;
		float zy = a.Z.X*b.X.Y + a.Z.Y*b.Y.Y + a.Z.Z*b.Z.Y + a.Z.W*b.W.Y;
		float zz = a.Z.X*b.X.Z + a.Z.Y*b.Y.Z + a.Z.Z*b.Z.Z + a.Z.W*b.W.Z;
		float zw = a.Z.X*b.X.W + a.Z.Y*b.Y.W + a.Z.Z*b.Z.W + a.Z.W*b.W.W;

		float wx = a.W.X*b.X.X + a.W.Y*b.Y.X + a.W.Z*b.Z.X + a.W.W*b.W.X;
		float wy = a.W.X*b.X.Y + a.W.Y*b.Y.Y + a.W.Z*b.Z.Y + a.W.W*b.W.Y;
		float wz = a.W.X*b.X.Z + a.W.Y*b.Y.Z + a.W.Z*b.Z.Z + a.W.W*b.W.Z;
		float ww = a.W.X*b.X.W + a.W.Y*b.Y.W + a.W.Z*b.Z.W + a.W.W*b.W.W;

		r.X.Set( xx, xy, xz, xw );
		r.Y.Set( yx, yy, yz, yw );
		r.Z.Set( zx, zy, zz, zw );
		r.W.Set( wx, wy, wz, ww );

		return r;
	}
	
	public static Mat4 LoadRotationX( Mat4 dst, float rad )
	{
		float s = FMath.Sin( rad );
		float c = FMath.Cos( rad );
		return dst.Load(
			1.0f, 0.0f, 0.0f, 0.0f,
			0.0f, c, s,  0.0f,
			0.0f, -s, c,  0.0f,
			0.0f, 0.0f, 0.0f, 1.0f );
	}

	public static Mat4 LoadRotationY( Mat4 dst, float rad )
	{
		float s = FMath.Sin( rad );
		float c = FMath.Cos( rad );
		return dst.Load(
			c, 0.0f, -s,  0.0f,
			0.0f, 1.0f, 0.0f, 0.0f,
			s, 0.0f, c,  0.0f,
			0.0f, 0.0f, 0.0f, 1.0f );
	}

	public static Mat4 LoadRotationZ( Mat4 dst, float rad )
	{
		float s = FMath.Sin( rad );
		float c = FMath.Cos( rad );
		return dst.Load(
			c, s, 0.0f,  0.0f,
			-s, c, 0.0f,  0.0f,
			0.0f, 0.0f, 1.0f, 0.0f,
			0.0f, 0.0f, 0.0f, 1.0f );
	}
	
	/*
	public static Mat4 LoadTranslation( Mat4 dst, float x, float y, float z )
	{
		return (Mat4) Transform3.LoadTranslation( dst, x, y, z );
	}
	
	public static Mat4 LoadTranslation( Mat4 dst, Vec3 t )
	{
		return (Mat4) Transform3.LoadTranslation( dst, t );
	}
	
	public static Mat4 LoadScaling( Mat4 dst, float x, float y, float z )
	{
		return (Mat4) Mat3.LoadScaling( dst, x, y, z );
	}
	
	public static Mat4 LoadScaling( Mat4 dst, float s )
	{
		return (Mat4) Mat3.LoadScaling( dst, s );
	}
	*/

	private static LocalList<Mat4> m_locals = new LocalList<Mat4>( CreateLocals( 1024 ) );

	private static Mat4[] CreateLocals( int nbLocals )
	{
		Mat4[] result = new Mat4[nbLocals];
		for( int i = 0 ; i < result.length ; i++ )
		{
			result[i] = new Mat4();
		}

		return result;
	}

	public static Mat4 Local() { return m_locals.Local(); }	
}
	
