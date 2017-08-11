package com.nrt.math;

import com.nrt.math.Float3;
import java.nio.*;

public class Vec3
{
	public float X = 0.0f;
	public float Y = 0.0f;
	public float Z = 0.0f;
	public float W = 0.0f;

	public Vec3()
	{}
	public Vec3(float xyz)
	{ X = Y = Z = xyz; }
	public Vec3(float x, float y, float z)
	{ X = x; Y = y; Z = z; }
	public Vec3(Vec3 xyz)
	{ X = xyz.X; Y = xyz.Y; Z = xyz.Z; }

	public Vec3 Load(float xyz)
	{ X = Y = Z = xyz; return this; }
	public Vec3 Load(float x, float y, float z)
	{ X = x; Y = y; Z = z; return this; }
	public Vec3 Load(Vec3 xyz)
	{ X = xyz.X; Y = xyz.Y; Z = xyz.Z; return this; }
	public Vec3 Load(Float3 xyz)
	{ X = xyz.X; Y = xyz.Y; Z = xyz.Z; return this; }
	public Vec3 Load(float[] v, int o)
	{ X = v[o + 0]; Y = v[o + 1]; Z = v[o + 2]; return this; }

	public Vec3 Store(float[] v, int o)
	{ v[o + 0] = X; v[o + 1] = Y; v[o + 2] = Z; return this; }


	public static Vec3 Add(Vec3 r, Vec3 a, Vec3 b)
	{ r.X = a.X + b.X; r.Y = a.Y + b.Y; r.Z = a.Z + b.Z; return r; }
	public static Vec3 Sub(Vec3 r, Vec3 a, Vec3 b)
	{ r.X = a.X - b.X; r.Y = a.Y - b.Y; r.Z = a.Z - b.Z; return r; }
	public static Vec3 Mul(Vec3 r, Vec3 a, Vec3 b)
	{ r.X = a.X * b.X; r.Y = a.Y * b.Y; r.Z = a.Z * b.Z; return r; }
	public static Vec3 Div(Vec3 r, Vec3 a, Vec3 b)
	{ r.X = a.X / b.X; r.Y = a.Y / b.Y; r.Z = a.Z / b.Z; return r; }

	public static Vec3 Add(Vec3 r, float a, Vec3 b)
	{ r.X = a + b.X; r.Y = a + b.Y; r.Z = a + b.Z; return r; }
	public static Vec3 Sub(Vec3 r, float a, Vec3 b)
	{ r.X = a - b.X; r.Y = a - b.Y; r.Z = a - b.Z; return r; }
	public static Vec3 Mul(Vec3 r, float a, Vec3 b)
	{ r.X = a * b.X; r.Y = a * b.Y; r.Z = a * b.Z; return r; }
	public static Vec3 Div(Vec3 r, float a, Vec3 b)
	{ r.X = a / b.X; r.Y = a / b.Y; r.Z = a / b.Z; return r; }

	public static Vec3 Add(Vec3 r, Vec3 a, float b)
	{ r.X = a.X + b; r.Y = a.Y + b; r.Z = a.Z + b; return r; }
	public static Vec3 Sub(Vec3 r, Vec3 a, float b)
	{ r.X = a.X - b; r.Y = a.Y - b; r.Z = a.Z - b; return r; }
	public static Vec3 Mul(Vec3 r, Vec3 a, float b)
	{ r.X = a.X * b; r.Y = a.Y * b; r.Z = a.Z * b; return r; }
	public static Vec3 Div(Vec3 r, Vec3 a, float b)
	{ r.X = a.X / b; r.Y = a.Y / b; r.Z = a.Z / b; return r; }

	public static Vec3 Mad(Vec3 r, Vec3 a, Vec3 b, Vec3 c)
	{ r.X = a.X * b.X + c.X; r.Y = a.Y * b.Y + c.Y; r.Z = a.Z * b.Z + c.Z; return r; }
	public static Vec3 Mad(Vec3 r, Vec3 a, float b, Vec3 c)
	{ r.X = a.X * b + c.X; r.Y = a.Y * b + c.Y; r.Z = a.Z * b + c.Z; return r; }
	public static Vec3 Mad(Vec3 r, Vec3 a, float b, float c)
	{ r.X = a.X * b + c; r.Y = a.Y * b + c; r.Z = a.Z * b + c; return r; }

	public static Vec3 Min(Vec3 r, Vec3 a, Vec3 b)
	{
		r.X = (a.X < b.X ? a.X : b.X);
		r.Y = (a.Y < b.Y ? a.Y : b.Y);
		r.Z = (a.Z < b.Z ? a.Z : b.Z);
		return r;
	}

	public static Vec3 Min(Vec3 r, Vec3 a, float b)
	{
		r.X = (a.X < b ? a.X : b);
		r.Y = (a.Y < b ? a.Y : b);
		r.Z = (a.Z < b ? a.Z : b);
		return r;
	}

	public static Vec3 Min(Vec3 r, float a, Vec3 b)
	{
		r.X = (a < b.X ? a : b.X);
		r.Y = (a < b.Y ? a : b.Y);
		r.Z = (a < b.Z ? a : b.Z);
		return r;
	}

	public static Vec3 Max(Vec3 r, Vec3 a, Vec3 b)
	{
		r.X = (a.X > b.X ? a.X : b.X);
		r.Y = (a.Y > b.Y ? a.Y : b.Y);
		r.Z = (a.Z > b.Z ? a.Z : b.Z);
		return r;
	}

	public static Vec3 Max(Vec3 r, Vec3 a, float b)
	{
		r.X = (a.X > b ? a.X : b);
		r.Y = (a.Y > b ? a.Y : b);
		r.Z = (a.Z > b ? a.Z : b);
		return r;
	}

	public static Vec3 Max(Vec3 r, float a, Vec3 b)
	{
		r.X = (a > b.X ? a : b.X);
		r.Y = (a > b.Y ? a : b.Y);
		r.Z = (a > b.Z ? a : b.Z);
		return r;
	}

	public static boolean IsEqual(Vec3 a, Vec3 b)
	{
		if (a.X == b.X && a.Y == b.Y && a.Z == b.Z)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static Vec3 Clamp(Vec3 r, Vec3 a, Vec3 min, Vec3 max)
	{
		r.X = (a.X < min.X ? min.X : (a.X > max.X ? max.X : a.X));
		r.Y = (a.Y < min.Y ? min.Y : (a.Y > max.Y ? max.Y : a.Y));
		r.Z = (a.Z < min.Z ? min.Z : (a.Z > max.Z ? max.Z : a.Z));
		return r;	
	}

	public static Vec3 Clamp(Vec3 r, Vec3 a, float min, float max)
	{
		r.X = (a.X < min ? min : (a.X > max ? max : a.X));
		r.Y = (a.Y < min ? min : (a.Y > max ? max : a.Y));
		r.Z = (a.Z < min ? min : (a.Z > max ? max : a.Z));
		return r;	
	}

	public static Vec3 Lerp(Vec3 r, Vec3 v0, Vec3 v1, float fLerp)
	{
		r.X = v0.X * (1.0f - fLerp) + v1.X * fLerp;
		r.Y = v0.Y * (1.0f - fLerp) + v1.Y * fLerp;
		r.Z = v0.Z * (1.0f - fLerp) + v1.Z * fLerp;
		return r;
	}

	public static float Dot(Vec3 a, Vec3 b)
	{
		return a.X * b.X + a.Y * b.Y + a.Z * b.Z;
	}

	public static float Length(Vec3 a)
	{
		return FMath.Sqrt(a.X * a.X + a.Y * a.Y + a.Z * a.Z);
	}

	public static Vec3 Normalize(Vec3 r, Vec3 a)
	{
		float lsq = a.X * a.X + a.Y * a.Y + a.Z * a.Z;
		if (lsq > 0.0f)
		{
			float rsqrt = 1.0f / FMath.Sqrt(lsq);

			r.X = a.X * rsqrt;
			r.Y = a.Y * rsqrt;
			r.Z = a.Z * rsqrt;
		}
		else
		{
			r.X = r.Y = r.Z = 0.0f;
		}
		return r;
	}

	public static Vec3 Cross(Vec3 r, Vec3 a, Vec3 b)
	{
		float x = a.Y * b.Z - a.Z * b.Y;
		float y = a.Z * b.X - a.X * b.Z;
		float z = a.X * b.Y - a.Y * b.X;
		return r.Load(x, y, z);
	}

	public static Vec3 CrossNormalize(Vec3 r, Vec3 a, Vec3 b)
	{
		float x = a.Y * b.Z - a.Z * b.Y;
		float y = a.Z * b.X - a.X * b.Z;
		float z = a.X * b.Y - a.Y * b.X;
		r.Load(x, y, z);
		return Normalize(r, r);
	}

	public static Vec3 SubNormalize(Vec3 r, Vec3 a, Vec3 b)
	{
		float x = a.X - b.X;
		float y = a.Y - b.Y;
		float z = a.Z - b.Z;
		r.Load(x, y, z);
		return Normalize(r, r);
	}

	public static Vec3 Mul(Vec3 r, Vec3 v, float w, Mat4 m)
	{
		r.X = v.X * m.X.X + v.Y * m.Y.X + v.Z * m.Z.X + w * m.W.X;
		r.Y = v.X * m.X.Y + v.Y * m.Y.Y + v.Z * m.Z.Y + w * m.W.Y;
		r.Z = v.X * m.X.Z + v.Y * m.Y.Z + v.Z * m.Z.Z + w * m.W.Z;
		return r;
	}

	public static Vec3 Mul(Vec3 r, Vec3 v, Transform3 m)
	{
		r.X = v.X * m.X.X + v.Y * m.Y.X + v.Z * m.Z.X + m.W.X;
		r.Y = v.X * m.X.Y + v.Y * m.Y.Y + v.Z * m.Z.Y + m.W.Y;
		r.Z = v.X * m.X.Z + v.Y * m.Y.Z + v.Z * m.Z.Z + m.W.Z;
		return r;
	}

	public static Vec3 Mul(Vec3 r, Vec3 v, Mat3 m)
	{
		r.X = v.X * m.X.X + v.Y * m.Y.X + v.Z * m.Z.X;
		r.Y = v.X * m.X.Y + v.Y * m.Y.Y + v.Z * m.Z.Y;
		r.Z = v.X * m.X.Z + v.Y * m.Y.Z + v.Z * m.Z.Z;
		return r;		
	}

	public static Vec3 Local()
	{ return Vec4.Local(); }	
}

class PseudoBuffer
{
}

class VMath
{	
	public static java.nio.ByteBuffer s_locals = null;
	public static int s_pos = 0;

	public static int Push()
	{
		return s_pos;
	}

	public static void Pop(int pos)
	{
		s_pos = pos;
	}

	public static int Local(int nbQWords)
	{
		int result = s_pos;
		s_pos += 16 * nbQWords;
		return result;
	}

	public static final class Vec3
	{	
		public static int Load3(int p, float x, float y, float z)
		{
			s_locals.position(p);
			s_locals.putFloat(x);
			s_locals.putFloat(y);
			s_locals.putFloat(z);	
			return p;
		}



		public static float GetX(int p)
		{ return s_locals.getFloat(p); }
		public static float GetY(int p)
		{ return s_locals.getFloat(p + 4); }
		public static float GetZ(int p)
		{ return s_locals.getFloat(p + 8); }
		public static float GetW(int p)
		{ return s_locals.getFloat(p + 12); }

		public static int SetX(int p, float x)
		{ s_locals.putFloat(p, x); return p;}
		public static int SetY(int p, float y)
		{ s_locals.putFloat(p + 4, y); return p;}
		public static int SetZ(int p, float z)
		{ s_locals.putFloat(p + 8, z); return p;}
		public static int SetW(int p, float z)
		{ s_locals.putFloat(p + 12, z); return p;}

		private static float ax = 0.0f;
		private static float ay = 0.0f;
		private static float az = 0.0f;
		private static float aw = 0.0f;

		private static float bx = 0.0f;
		private static float by = 0.0f;
		private static float bz = 0.0f;
		private static float bw = 0.0f;

		private static float cx = 0.0f;
		private static float cy = 0.0f;
		private static float cz = 0.0f;
		private static float cw = 0.0f;

		public static void Load3(float x, float y, float z)
		{
			ax = x; ay = y; az = z;
		}

		public static void Load3(float xyz)
		{
			ax = ay = az = xyz;
		}

		public static void Load3(int a)
		{
			s_locals.position(a);
			ax = s_locals.getFloat();ay = s_locals.getFloat();az = s_locals.getFloat();
		}

		public static void Load3b(int a)
		{
			s_locals.position(a);
			bx = s_locals.getFloat();by = s_locals.getFloat();bz = s_locals.getFloat();
		}
		public static void Load3c(int a)
		{
			s_locals.position(a);
			cx = s_locals.getFloat();cy = s_locals.getFloat();cz = s_locals.getFloat();
		}
		public static void Store3(int d)
		{
			s_locals.position(d);
			s_locals.putFloat(ax);
			s_locals.putFloat(ay);
			s_locals.putFloat(az);
		}

		private static float Min(float a, float b)
		{return((a < b) ?a: b);}
		private static float Max(float a, float b)
		{return((b < a) ?a: b);}

		public static void Add3(int a)
		{s_locals.position(a);ax += s_locals.getFloat();ay += s_locals.getFloat();az += s_locals.getFloat();}
		public static void Sub3(int a)
		{s_locals.position(a);ax -= s_locals.getFloat();ay -= s_locals.getFloat();az -= s_locals.getFloat();}
		public static void Mul3(int a)
		{s_locals.position(a);ax *= s_locals.getFloat();ay *= s_locals.getFloat();az *= s_locals.getFloat();}
		public static void Div3(int a)
		{s_locals.position(a);ax /= s_locals.getFloat();ay /= s_locals.getFloat();az /= s_locals.getFloat();}
		public static void Min3(int a)
		{s_locals.position(a);ax = Min(ax, s_locals.getFloat());ay = Min(ay, s_locals.getFloat());az = Min(az, s_locals.getFloat());}
		public static void Max3(int a)
		{s_locals.position(a);ax = Max(ax, s_locals.getFloat());ay = Max(ay, s_locals.getFloat());az = Max(az, s_locals.getFloat());}
		public static float Dot3(int a)
		{Mul3(a);return(ax + ay + az);}
		public static void Cross3(int b, int c)
		{
			Load3b(b);Load3c(c);
			Load3(by * cz - cz * by, bz * cx - bx * cz, bx * cy - cx * by);
		}
		public static float Length3()
		{return FMath.Sqrt(ax * ax + ay * ay + az * az);}
		public static void Normalize3()
		{float l = Length3();if (l > 0.0f)
			{Div3(l);}}

		public static void Add3(float xyz)
		{ax += xyz;ay += xyz;az += xyz;}
		public static void Sub3(float xyz)
		{ax -= xyz;ay -= xyz;az -= xyz;}
		public static void Mul3(float xyz)
		{ax *= xyz;ay *= xyz;az *= xyz;}
		public static void Div3(float xyz)
		{ax /= xyz;ay /= xyz;az /= xyz;}
		public static void Min3(float xyz)
		{ax = Min(ax, xyz);ay = Min(ay, xyz);az = Min(az, xyz);}
		public static void Max3(float xyz)
		{ax = Max(ax, xyz);ay = Max(ay, xyz);az = Max(az, xyz);}
		public static void Dot3(float xyz)
		{Mul3(xyz);Load3(ax + ay + az);}

		public static void Add3(float x, float y, float z)
		{ax += x;ay += y;az += z;}
		public static void Sub3(float x, float y, float z)
		{ax -= x;ay -= y;az -= z;}
		public static void Mul3(float x, float y, float z)
		{ax *= x;ay *= y;az *= z;}
		public static void Div3(float x, float y, float z)
		{ax /= x;ay /= y;az /= z;}
		public static void Min3(float x, float y, float z)
		{ax = Min(ax, x);ay = Min(ay, y);az = Min(az, z);}
		public static void Max3(float x, float y, float z)
		{ax = Max(ax, x);ay = Max(ay, y);az = Max(az, z);}
		public static void Dot3(float x, float y, float z)
		{Mul3(x, y, z);Load3(ax + ay + az);}

		public static int Add3(int dst, int a, int b)
		{Load3(a);Add3(b);Store3(dst);return dst;}
		public static int Sub3(int dst, int a, int b)
		{Load3(a);Sub3(b);Store3(dst);return dst;}
		public static int Mul3(int dst, int a, int b)
		{Load3(a);Mul3(b);Store3(dst);return dst;}
		public static int Div3(int dst, int a, int b)
		{Load3(a);Div3(b);Store3(dst);return dst;}
		public static int Min3(int dst, int a, int b)
		{Load3(a);Min3(b);Store3(dst);return dst;}
		public static int Max3(int dst, int a, int b)
		{Load3(a);Max3(b);Store3(dst);return dst;}
		public static int Dot3(int dst, int a, int b)
		{Load3(a);Dot3(b);Store3(dst);return dst;}
		public static int Cross3(int dst, int a, int b)
		{Cross3(a, b);Store3(dst);return dst;}
		public static int Normalize(int dst, int a)
		{Load3(a);Normalize3();Store3(dst);return dst;}
		public static final int SubNormalize(int dst, int a, int b)
		{Load3(a);Sub3(b);Normalize3();Store3(dst);return dst;}
		public static final int CrossNormalize(int dst, int a, int b)
		{Cross3(a, b);Normalize3();Store3(dst);return dst;}
	}
	public static int MulXYZ0_3x3(int dst, int v, int m)
	{
		return dst;
	}
	
	public static int MulXYZ1_4x4(int dst, int v, int m)
	{
		return dst;
	}

	public static int Mul3x3(int dst, int a, int b)
	{
		return dst;
	}

	public static int Mul4x4(int dst, int a, int b)
	{
		return dst;
	}

	public static int Load3x3(int dst,
							  float xx, float xy, float xz,
							  float yx, float yy, float yz,
							  float zx, float zy, float zz)
	{
		Vec3.Load3(dst, xx, xy, xz);
		Vec3.Load3(dst + 4, yx, yy, yz);
		Vec3.Load3(dst + 8, zx, zy, zz);
		return dst;
	}



}
