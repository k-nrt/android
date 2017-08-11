package com.nrt.vmath;

import com.nrt.math.*;

public class Vec3
{
	public static java.nio.ByteBuffer s_locals = Local.s_locals;
	
	public static final int Local(float xyz)
	{
		return Load(Local.Local(1),xyz);
	}
	
	public static final int Local(float x,float y, float z)
	{
		return Load(Local.Local(1),x,y,z);		
	}

	public static final int Load(int p, float x, float y, float z)
	{
		s_locals.position(p);
		s_locals.putFloat(x);
		s_locals.putFloat(y);
		s_locals.putFloat(z);	
		return p;
	}

	public static final int Load(int p, float xyz)
	{
		s_locals.position(p);
		s_locals.putFloat(xyz);
		s_locals.putFloat(xyz);
		s_locals.putFloat(xyz);	
		return p;
	}
	
	public static final float GetX(int p){ return s_locals.getFloat(p); }
	public static final float GetY(int p){ return s_locals.getFloat(p + 4); }
	public static final float GetZ(int p){ return s_locals.getFloat(p + 8); }
	
	public static final int SetX(int p, float x){ s_locals.putFloat(p, x); return p;}
	public static final int SetY(int p, float y){ s_locals.putFloat(p + 4, y); return p;}
	public static final int SetZ(int p, float z){ s_locals.putFloat(p + 8, z); return p;}
	
	private static float ax = 0.0f;
	private static float ay = 0.0f;
	private static float az = 0.0f;

	private static float bx = 0.0f;
	private static float by = 0.0f;
	private static float bz = 0.0f;

	private static float cx = 0.0f;
	private static float cy = 0.0f;
	private static float cz = 0.0f;

	public static final void Load(float x, float y, float z){ ax=x; ay=y; az=z; }
	public static final void Load(float xyz){ax = ay = az = xyz;}
	public static final void Load(int a)
	{
		s_locals.position(a);
		ax = s_locals.getFloat();ay = s_locals.getFloat();az = s_locals.getFloat();
	}

	public static final void Loadb(int a)
	{
		s_locals.position(a);
		bx = s_locals.getFloat();by = s_locals.getFloat();bz = s_locals.getFloat();
	}
	public static final void Loadc(int a)
	{
		s_locals.position(a);
		cx = s_locals.getFloat();cy = s_locals.getFloat();cz = s_locals.getFloat();
	}
	
	public static final void Store(int d)
	{
		s_locals.position(d);
		s_locals.putFloat(ax);
		s_locals.putFloat(ay);
		s_locals.putFloat(az);
	}

	private static final float Min(float a, float b)
	{return((a < b) ?a: b);}
	private static final float Max(float a, float b)
	{return((b < a) ?a: b);}

	public static final void Add(int a){s_locals.position(a);ax += s_locals.getFloat();ay += s_locals.getFloat();az += s_locals.getFloat();}
	public static final void Sub(int a){s_locals.position(a);ax -= s_locals.getFloat();ay -= s_locals.getFloat();az -= s_locals.getFloat();}
	public static final void Mul(int a){s_locals.position(a);ax *= s_locals.getFloat();ay *= s_locals.getFloat();az *= s_locals.getFloat();}
	public static final void Div(int a){s_locals.position(a);ax /= s_locals.getFloat();ay /= s_locals.getFloat();az /= s_locals.getFloat();}
	public static final void Min(int a){s_locals.position(a);ax = Min(ax, s_locals.getFloat());ay = Min(ay, s_locals.getFloat());az = Min(az, s_locals.getFloat());}
	public static final void Max(int a){s_locals.position(a);ax = Max(ax, s_locals.getFloat());ay = Max(ay, s_locals.getFloat());az = Max(az, s_locals.getFloat());}
	public static final float Dot(int a){Mul(a);return(ax + ay + az);}
	public static final void Cross(int b, int c)
	{
		Loadb(b);Loadc(c);
		Load(by * cz - cz * by, bz * cx - bx * cz, bx * cy - cx * by);
	}
	public static final float Length(){return FMath.Sqrt(ax * ax + ay * ay + az * az);}
	public static final void Normalize(){float l = Length();if (l > 0.0f){Div(l);}}

	public static final void Add(float xyz){ax += xyz;ay += xyz;az += xyz;}
	public static final void Sub(float xyz){ax -= xyz;ay -= xyz;az -= xyz;}
	public static final void Mul(float xyz){ax *= xyz;ay *= xyz;az *= xyz;}
	public static final void Div(float xyz){ax /= xyz;ay /= xyz;az /= xyz;}
	public static final void Min(float xyz){ax = Min(ax, xyz);ay = Min(ay, xyz);az = Min(az, xyz);}
	public static final void Max(float xyz){ax = Max(ax, xyz);ay = Max(ay, xyz);az = Max(az, xyz);}
	public static final void Dot(float xyz){Mul(xyz);Load(ax + ay + az);}

	public static final void Add(float x, float y, float z){ax += x;ay += y;az += z;}
	public static final void Sub(float x, float y, float z){ax -= x;ay -= y;az -= z;}
	public static final void Mul(float x, float y, float z){ax *= x;ay *= y;az *= z;}
	public static final void Div(float x, float y, float z){ax /= x;ay /= y;az /= z;}
	public static final void Min(float x, float y, float z){ax = Min(ax, x);ay = Min(ay, y);az = Min(az, z);}
	public static final void Max(float x, float y, float z){ax = Max(ax, x);ay = Max(ay, y);az = Max(az, z);}
	public static final void Dot(float x, float y, float z){Mul(x, y, z);Load(ax + ay + az);}

	public static final int Add(int dst, int a, int b){Load(a);Add(b);Store(dst);return dst;}
	public static final int Sub(int dst, int a, int b){Load(a);Sub(b);Store(dst);return dst;}
	public static final int Mul(int dst, int a, int b){Load(a);Mul(b);Store(dst);return dst;}
	public static final int Div(int dst, int a, int b){Load(a);Div(b);Store(dst);return dst;}
	public static final int Min(int dst, int a, int b){Load(a);Min(b);Store(dst);return dst;}
	public static final int Max(int dst, int a, int b){Load(a);Max(b);Store(dst);return dst;}
	public static final int Dot(int dst, int a, int b){Load(a);Dot(b);Store(dst);return dst;}
	
	public static final int Add(int dst, int a, float b){Load(a);Add(b);Store(dst);return dst;}
	public static final int Sub(int dst, int a, float b){Load(a);Sub(b);Store(dst);return dst;}
	public static final int Mul(int dst, int a, float b){Load(a);Mul(b);Store(dst);return dst;}
	public static final int Div(int dst, int a, float b){Load(a);Div(b);Store(dst);return dst;}
	public static final int Min(int dst, int a, float b){Load(a);Min(b);Store(dst);return dst;}
	public static final int Max(int dst, int a, float b){Load(a);Max(b);Store(dst);return dst;}
	
	
	public static final int Cross(int dst, int a, int b){Cross(a, b);Store(dst);return dst;}
	public static final int Normalize(int dst, int a){Load(a);Normalize();Store(dst);return dst;}
	public static final int SubNormalize(int dst, int a, int b){Load(a);Sub(b);Normalize();Store(dst);return dst;}
	public static final int CrossNormalize(int dst, int a, int b){Cross(a, b);Normalize();Store(dst);return dst;}

}
