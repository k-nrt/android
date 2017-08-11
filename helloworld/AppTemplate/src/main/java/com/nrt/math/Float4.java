package com.nrt.math;

public final class Float4
{
	public float X = 0.0f;
	public float Y = 0.0f;
	public float Z = 0.0f;
	public float W = 0.0f;

	public Float4() { X=Y=Z=W=0.0f; }

	public Float4(float xyzw) { X=Y=Z=W=xyzw; }
	public Float4(float x, float y, float z, float w) { X=x; Y=y; Z=z; W=w; }
	public Float4(float[] v, int o) { X=v[o+0]; Y=v[o+1]; Z=v[o+2]; W=v[o+3]; }

	public Float4 Set( float xyzw ) { X=Y=Z=W=xyzw; return this; }
	public Float4 Set( float x, float y, float z, float w ) { X=x; Y=y; Z=z; W=w; return this; }
	public Float4 Set( Float3 xyz, float w ) { X=xyz.X; Y=xyz.Y; Z=xyz.Z; W=w; return this; }
	public Float4 Set( Float4 xyzw ) { X=xyzw.X; Y=xyzw.Y; Z=xyzw.Z; W=xyzw.W; return this; }


	public static Float4 Lerp( Float4 dst, float lerp, Float4 v0, Float4 v1 )
	{
		return dst.Set(
			v0.X*(1.0f-lerp) + v1.X*lerp,
			v0.Y*(1.0f-lerp) + v1.Y*lerp,
			v0.Z*(1.0f-lerp) + v1.Z*lerp,
			v0.W*(1.0f-lerp) + v1.W*lerp );
	}
	//public Float3 GetXYZ()
	//{
	//	return new Float3( X, Y, Z );
	//}

	
	 public static Float4 Mul(Float4 r, Float4 a, float b)
	 {
		 r.X=a.X*b; r.Y=a.Y*b; r.Z=a.Z*b; r.W=a.W*b;
		 return r;
	 }
	 
	public static Float4 Mul(Float4 r, Float4 a, Float4 b)
	{
		r.X=a.X*b.X; r.Y=a.Y*b.Y; r.Z=a.Z*b.Z; r.W=a.W*b.W;
		return r;
	}
	
	public static Float4 Mul(Float4 r, float a, Float4 b)
	{
		r.X=a*b.X; r.Y=a*b.Y; r.Z=a*b.Z; r.W=a*b.W;
		return r;
	}
	
	/*
	 public static Float4 Mul(float a, Float4 b)
	 {
	 return new Float4(a * b.X, a * b.Y, a * b.Z, a * b.W);
	 }
	 */

	/*
	 public static Float4 Mul(Float4 a, Float4 b)
	 {

	 return new Float4(a.X * b.X, a.Y * b.Y, a.Z * b.Z, a.W * b.W);
	 }

	 public static Float4 Mad(Float4 t, float m, Float4 a)
	 {
	 return new Float4(t.X * m + a.X, t.Y * m + a.Y, t.Z * m + a.Z, t.W * m + a.W);
	 }
	 */

	/*
	 public static final class Float4List
	 {
	 public final Float4[] Values = new Float4[256];
	 public Float4List()
	 {
	 for( int i = 0 ; i < Values.length ; i++ )
	 {
	 Values[i] = new Float4();
	 }
	 }

	 private int m_iPos = 0;
	 public Float4 Get()
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
	 */

	public static final LocalList<Float4> m_locals = new LocalList<Float4>( CreateArray( 256 ) );

	public static Float4[] CreateArray( int nbElements )
	{
		Float4[] result = new Float4[nbElements];

		for( int i = 0 ; i < result.length ; i++ )
		{
			result[i] = new Float4();
		}
		return result;
	}


	public static Float4 Local() { return m_locals.Local(); }
	public static Float4 Local( float xyzw ) { return m_locals.Local().Set( xyzw ); }
	public static Float4 Local( float x, float y, float z, float w ) { return m_locals.Local().Set( x,y,z,w ); }
	public static Float4 Local( Float3 xyz, float w ) { return m_locals.Local().Set( xyz,w ); }

}
