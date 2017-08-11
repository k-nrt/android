package com.nrt.math;

public class Quaternion
{
	public float X = 0.0f;
	public float Y = 0.0f;
	public float Z = 0.0f;
	public float W = 0.0f;

	public Quaternion()
	{}
	public Quaternion(float x, float y, float z, float w)
	{
		X = x; Y = y; Z = z; W = w;
	}
	
	public Quaternion Load(float x, float y, float z, float w)
	{
		X = x; Y = y; Z = z; W = w;
		return this;
	}
	
	public Quaternion Load( Quaternion q )
	{
		X = q.X; Y = q.Y; Z = q.Z; W = q.W;
		return this;		
	}
	
	public static Quaternion LoadIdentity( Quaternion dst )
	{
		return dst.Load( 0.0f, 0.0f, 0.0f, 1.0f);
	}
		
	public float Length()
	{ 
		return FMath.Sqrt(X * X + Y * Y + Z * Z + W * W);
	}

	public static Quaternion Normalize( Quaternion dst, Quaternion p)
	{ 
		float len = p.Length();
		if (len > 0.0f)
		{
			float rcp = 1.0f/len;
			return dst.Load(p.X*rcp, p.Y*rcp, p.Z*rcp, p.W*rcp);
		}
		else
		{
			return new Quaternion(0.0f, 0.0f, 0.0f, 0.0f);
		}
	}

	public static Quaternion Lerp(Quaternion dst, Quaternion q0, Quaternion q1, float t ) 
	{ 
		return dst.Load(
				q0.X * (1.0f - t) + q1.X * t,
				q0.Y * (1.0f - t) + q1.Y * t,
				q0.Z * (1.0f - t) + q1.Z * t,
				q0.W * (1.0f - t) + q1.W * t);
	}


	//. p = post. q = pre.
	public static Quaternion Mul( Quaternion r, Quaternion p, Quaternion q)
	{
		float pw = p.W, px = p.X, py = p.Y, pz = p.Z;
		float qw = q.W, qx = q.X, qy = q.Y, qz = q.Z;

		r.W = pw * qw - px * qx - py * qy - pz * qz;
		r.X = pw * qx + px * qw + py * qz - pz * qy;
		r.Y = pw * qy - px * qz + py * qw + pz * qx;
		r.Z = pw * qz + px * qy - py * qx + pz * qw;

		return r;
	}

	public static Quaternion Mul( Quaternion r, Quaternion p, float q)
	{
		float hrad = FMath.Acos(p.W);
		float s = FMath.Sin(hrad);
		float x = p.X;
		float y = p.Y;
		float z = p.Z;

		if (s != 0.0f)
		{
			x /= s;
			y /= s;
			z /= s;
		}

		hrad *= q;
		s = FMath.Sin(hrad);

		r.W = FMath.Cos(hrad);
		r.X = s * x;
		r.Y = s * y;
		r.Z = s * z;
		return r;
	}

	public static Quaternion LoadRotationAxis( Quaternion q, Float3 f3Axis, float rad)
	{
		float hrad = 0.5f * rad;
		float s = FMath.Sin(hrad);

		q.W = FMath.Cos(hrad);
		q.X = s * f3Axis.X;
		q.Y = s * f3Axis.Y;
		q.Z = s * f3Axis.Z;
		return q;
	}
	
	public static Quaternion LoadRotation( Quaternion r, Float3 a, Float3 b)
	{
		if( a.IsZero() || b.IsZero() )
		{
			return Quaternion.LoadIdentity( r );
		}
		
		Float3 aa = Float3.Normalize( Float3.Local(), a);
		Float3 bb = Float3.Normalize( Float3.Local(), b);
		float fDot = Float3.Dot(aa, bb);
		Float3 v3Axis = Float3.CrossNormalize( Float3.Local(), aa, bb);

		if (Float3.Dot(v3Axis, v3Axis) > 0.0f)
		{
			//v3Axis = Vector3.Normalize( v3Axis );
			//Rotation = Matrix4.RotationAxis( v3Axis, FMath.Acos( fDot ) )*Rotation;
			//System.Error.WriteLine( String.format( " %f %f %f %f", v3Axis.X, v3Axis.Y, v3Axis.Z, (FMath.Acos( fDot )*180.0f/FMath.PI)/fElapsedTime ) );
			return Quaternion.LoadRotationAxis( r, v3Axis, FMath.Acos(fDot));//fElapsedTime );
		}
		else
		{
			//System.Error.WriteLine( "zero" );
			return Quaternion.LoadIdentity( r );
		}

	}	
	
	private static float SIGN(float x)
	{
		return (x >= 0.0f) ? +1.0f : -1.0f;
	}
	
	private static float NORM(float a, float b, float c, float d)
	{
		return FMath.Sqrt(a * a + b * b + c * c + d * d);
	}


	public static Quaternion FromFloat4x4( Quaternion dst, Float4x4 a)
	{
		//float r11 = a.Values[0], r12 = a.Values[4], r13 = a.Values[8];
		//float r21 = a.Values[1], r22 = a.Values[5], r23 = a.Values[9];
		//float r31 = a.Values[2], r32 = a.Values[6], r33 = a.Values[10];
		float r11 = a.Values[0], r12 = a.Values[1], r13 = a.Values[2];
		float r21 = a.Values[4], r22 = a.Values[5], r23 = a.Values[6];
		float r31 = a.Values[8], r32 = a.Values[9], r33 = a.Values[10];
		
		//inline float SIGN(float x) {return (x >= 0.0f) ? +1.0f : -1.0f;}
		//inline float NORM(float a, float b, float c, float d) {return sqrt(a * a + b * b + c * c + d * d);}

		float q0 = (r11 + r22 + r33 + 1.0f) / 4.0f;
		float q1 = (r11 - r22 - r33 + 1.0f) / 4.0f;
		float q2 = (-r11 + r22 - r33 + 1.0f) / 4.0f;
		float q3 = (-r11 - r22 + r33 + 1.0f) / 4.0f;
		if (q0 < 0.0f) q0 = 0.0f;
		if (q1 < 0.0f) q1 = 0.0f;
		if (q2 < 0.0f) q2 = 0.0f;
		if (q3 < 0.0f) q3 = 0.0f;
		q0 = FMath.Sqrt(q0);
		q1 = FMath.Sqrt(q1);
		q2 = FMath.Sqrt(q2);
		q3 = FMath.Sqrt(q3);
		if (q0 >= q1 && q0 >= q2 && q0 >= q3)
		{
			q0 *= +1.0f;
			q1 *= SIGN(r32 - r23);
			q2 *= SIGN(r13 - r31);
			q3 *= SIGN(r21 - r12);
		}
		else if (q1 >= q0 && q1 >= q2 && q1 >= q3)
		{
			q0 *= SIGN(r32 - r23);
			q1 *= +1.0f;
			q2 *= SIGN(r21 + r12);
			q3 *= SIGN(r13 + r31);
		}
		else if (q2 >= q0 && q2 >= q1 && q2 >= q3)
		{
			q0 *= SIGN(r13 - r31);
			q1 *= SIGN(r21 + r12);
			q2 *= +1.0f;
			q3 *= SIGN(r32 + r23);
		}
		else if (q3 >= q0 && q3 >= q1 && q3 >= q2)
		{
			q0 *= SIGN(r21 - r12);
			q1 *= SIGN(r31 + r13);
			q2 *= SIGN(r32 + r23);
			q3 *= +1.0f;
		}
		else
		{
			//printf("coding error\n");
		}
		float r = NORM(q0, q1, q2, q3);
		
		if( r > 0.0f )
		{
			float rcp = 1.0f/r;
			q0 *= rcp;
			q1 *= rcp;
			q2 *= rcp;
			q3 *= rcp;
			return dst.Load( q1, q2, q3, q0 );
		}
		else
		{
			return LoadIdentity( dst );
		}			
	}
	
	public static final LocalList<Quaternion> m_locals = new LocalList<Quaternion>( CreateArray( 128 ) );

	public static Quaternion[] CreateArray( int nbLocals )
	{
		Quaternion[] result = new Quaternion[nbLocals];
		for( int i = 0 ; i < result.length ; i++ )
		{
			result[i] = new Quaternion();
		}

		return result;
	}

	public static Quaternion Local() { return m_locals.Local(); }
	
}
