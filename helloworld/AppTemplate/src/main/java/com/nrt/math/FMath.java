package com.nrt.math;

import java.lang.Math;
import com.nrt.basic.DebugLog;
import com.nrt.collision.Intersection;
import com.nrt.collision.Sphere;
import com.nrt.collision.Edge;
import com.nrt.collision.Triangle;
import com.nrt.collision.Polygon;
import com.nrt.collision.BoundingBox;

public class FMath
{
	public static final float PI = (float) Math.PI;
	public static final float RD = (float) (Math.PI*2.0);

	public static final float ToRad( float deg ) { return (deg*PI)/180.0f; }

	public static final float Abs( float a ) { return ( (0.0f < a) ? a : -a );} 
	public static final float Pow( float x, float y ) { return (float) Math.pow( (double) x, (double) y ); }
	public static final float Cos( float rad ) { return (float) Math.cos( (double) rad ); }
	public static final float Sin( float rad ) { return (float) Math.sin( (double) rad ); }
	public static final float Acos( float c ) { return (float) Math.acos( (double) c ); }
	public static final float Asin( float s ) { return (float) Math.asin( (double) s ); }
	public static final float Atan2( float y, float x ) { return (float) Math.atan2( y, x ); }
	public static final float Sqrt(float a ) { return (float) Math.sqrt( (double) a ); }
	public static final float Floor( float a ) { return (float) Math.floor( (double) a); }

	public static final float Lerp( float f0, float f1, float fLerp )
	{
		return f0*(1.0f-fLerp)+f1*fLerp;
	}

	public static final float Min( float a, float b ) { return ( (a < b) ? a : b ); }
	public static final float Max( float a, float b ) { return ( (b < a) ? a : b ); }
	
	public static final float Clamp( float a, float fMin, float fMax )
	{
		return ( a < fMin ? fMin : ( a > fMax ? fMax : a ) );
	}

	public static final float Fraction( float a )
	{
		int i = (int) a;
		if( (float) i < a )
		{
			return a - (float)i;
		}
		else
		{
			return -((float)i) - a;
		}
		
	}



}
/*
class Intersection
{
	public final Float3 Position = new Float3(0.0f);
	public final Float3 Normal = new Float3(0.0f);
	public float Distance = -1.0f;

	public Intersection ()
	{
	}

	public Intersection( Float3 f3Position, Float3 f3Normal, float fDistance )
	{
		Position.Set( f3Position );
		Normal.Set( f3Normal );
		Distance = fDistance;
	}

	public Intersection Clone()
	{
		return new Intersection( Position, Normal, Distance );
	}

	public void Clean()
	{
		Position.Set( 0.0f );
		Normal.Set( 0.0f );
		Distance = -1.0f;
	}

	public void Set( Float3 f3Position, Float3 f3Normal, float fDistance )
	{
		Position.Set( f3Position );
		Normal.Set( f3Normal );
		Distance = fDistance;
	}

	public void Set( Intersection s )
	{
		Position.Set( s.Position );
		Normal.Set( s.Normal );
		Distance = s.Distance;
	}

	public static final class IntersectionList
	{
		public final Intersection[] Values = new Intersection[256];
		public IntersectionList()
		{
			for( int i = 0 ; i < Values.length ; i++ )
			{
				Values[i] = new Intersection();
			}
		}

		private int m_iPos = 0;
		public Intersection Get()
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

	private static final IntersectionList m_locals = new IntersectionList();

	public static Intersection Local() { return m_locals.Get(); }
}

class BoundingBox
{
	public Float3 Min = new Float3(0.0f);
	public Float3 Max = new Float3(0.0f);
	public BoundingBox ()
	{}

	public BoundingBox( Float3 f3Min, Float3 f3Max )
	{
		Min.Set( f3Min );
		Max.Set( f3Max );
	}
}

class Edge
{
	public final Float3 Start = new Float3(0.0f);
	public final Float3 End = new Float3(0.0f);
	public final Float3 Direction = new Float3(0.0f);
	public float Length = 0.0f;

	public Edge(){}

	public Edge( Float3 v3Start, Float3 v3End )
	{
		Start.Set( v3Start );
		End.Set( v3End );
		Float3.SubNormalize( Direction, v3End, v3Start );
		Length = Float3.Distance( v3End, v3Start );
	}

	public Edge Set( Float3 v3Start, Float3 v3End )
	{
		Start.Set( v3Start );
		End.Set( v3End );
		//Direction = Float3.Normalize( Float3.Sub( v3End, v3Start ) );
		Float3.SubNormalize( Direction, v3End, v3Start );
		Length = Float3.Distance( v3End, v3Start );

		return this;
	}

	public static final class EdgeList
	{
		public final Edge[] Values = new Edge[256];
		public EdgeList()
		{
			for( int i = 0 ; i < Values.length ; i++ )
			{
				Values[i] = new Edge();
			}
		}

		private int m_iPos = 0;
		public Edge Get()
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

	private static final EdgeList m_locals = new EdgeList();

	public static Edge Local() { return m_locals.Get(); }
}

class Sphere
{
	public Float3 Position = new Float3(0.0f);
	public float Radius = 0.0f;
	public Sphere ()
	{
	}

	public Sphere( Float3 v3Position, float fRadius )
	{
		Position.Set( v3Position );
		Radius = fRadius;
	}

	public Sphere Set( Float3 v3Position, float fRadius )
	{
		Position.Set( v3Position );
		Radius = fRadius;
		return this;
	}

}	

class Triangle
{
	public Float3 Normal = new Float3( 0.0f );

	public Edge[] Edges = null;			
	public Triangle ()
	{
	}

	public Triangle( Float3 v3Normal, Float3 v3Vertex0, Float3 v3Vertex1, Float3 v3Vertex2 )
	{
		Normal = Float3.Normalize( v3Normal );
		Edges = new Edge[3];
		Edges[0] = new Edge( v3Vertex0, v3Vertex1 );
		Edges[1] = new Edge( v3Vertex1, v3Vertex2 );
		Edges[2] = new Edge( v3Vertex2, v3Vertex0 );
	}
}

class Polygon
{
	public Triangle Triangle = new Triangle();
	public Float3[] Vertices = null;

	public Polygon ()
	{
	}
}
	*/
	
