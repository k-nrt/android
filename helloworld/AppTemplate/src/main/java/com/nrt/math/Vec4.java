package com.nrt.math;

public class Vec4 extends Vec3
{
	public Vec4() {}
	public Vec4( float xyzw ) { X=Y=Z=W=xyzw; }
	public Vec4( float[] v, int o ) { X=v[o+0]; Y=v[o+1]; Z=v[o+2]; W=v[o+3]; }
	public Vec4( Vec3 xyz, float w ) { X=xyz.X; Y=xyz.Y; Z=xyz.Z; W=w; }
	public Vec4( float x, float y, float z, float w ) { X=x; Y=y; Z=z; W=w; }
	public Vec4( Vec4 xyzw ) { X=xyzw.X; Y=xyzw.Y; Z=xyzw.Z; W=xyzw.W;}

	public Vec4 Set( float xyzw ){ X=Y=Z=W=xyzw; return this; }
	public Vec4 Set( float[] v, int o ) { X=v[o+0]; Y=v[o+1]; Z=v[o+2]; W=v[o+3]; return this; }
	public Vec4 Set( Vec3 xyz, float w ) { X=xyz.X; Y=xyz.Y; Z=xyz.Z; W=w; return this; }
	public Vec4 Set( float x, float y, float z, float w ) { X=x; Y=y; Z=z; W=w; return this; }
	public Vec4 Set( Vec4 xyzw ) { X=xyzw.X; Y=xyzw.Y; Z=xyzw.Z; W=xyzw.W; return this; }

	public Vec4 Load( float[] v, int o ) { X=v[o+0]; Y=v[o+1]; Z=v[o+2]; W=v[o+3]; return this; }
	public Vec4 Load( float x, float y, float z, float w ) { X=x; Y=y; Z=z; W=w; return this; }
	public Vec4 Load( Vec4 xyzw ) { X=xyzw.X; Y=xyzw.Y; Z=xyzw.Z; W=xyzw.W; return this; }
	public Vec4 Store( float[] v, int o ) { v[o+0]=X; v[o+1]=Y; v[o+2]=Z; v[o+3]=W; return this; }

	//public static LocalList<Vec4> Locals = new LocalList( new Vec4[1024], 32 );

	public static Vec4 Add( Vec4 r, Vec4 a, Vec4 b ) { r.X=a.X+b.X; r.Y=a.Y+b.Y; r.Z=a.Z+b.Z; r.W=a.W+b.W; return r; }
	public static Vec4 Sub( Vec4 r, Vec4 a, Vec4 b ) { r.X=a.X-b.X; r.Y=a.Y-b.Y; r.Z=a.Z-b.Z; r.W=a.W-b.W; return r; }
	public static Vec4 Mul( Vec4 r, Vec4 a, Vec4 b ) { r.X=a.X*b.X; r.Y=a.Y*b.Y; r.Z=a.Z*b.Z; r.W=a.W*b.W; return r; }
	public static Vec4 Div( Vec4 r, Vec4 a, Vec4 b ) { r.X=a.X/b.X; r.Y=a.Y/b.Y; r.Z=a.Z/b.Z; r.W=a.W/b.W; return r; }

	public static Vec4 Mul( Vec4 r, Vec4 v, Mat4 m )
	{
		r.X = v.X*m.X.X + v.Y*m.Y.X + v.Z*m.Z.X + v.W*m.W.X;
		r.Y = v.X*m.X.Y + v.Y*m.Y.Y + v.Z*m.Z.Y + v.W*m.W.Y;
		r.Z = v.X*m.X.Z + v.Y*m.Y.Z + v.Z*m.Z.Z + v.W*m.W.Z;
		r.W = v.X*m.X.W + v.Y*m.Y.W + v.Z*m.Z.W + v.W*m.W.W;
		return r;
	}	
	
	private static LocalList<Vec4> m_locals = new LocalList<Vec4>( CreateLocals( 1024 ) );

	private static Vec4[] CreateLocals( int nbLocals )
	{
		Vec4[] result = new Vec4[nbLocals];
		for( int i = 0 ; i < result.length ; i++ )
		{
			result[i] = new Vec4();
		}

		return result;
	}

	public static Vec4 Local() { return m_locals.Local(); }
}
	
