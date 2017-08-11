package com.nrt.collision;

import com.nrt.math.Float3;
import com.nrt.math.FMath;

public class Edge
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
	
	private static final Float3 f3StartToSphere = new Float3();
	private static final Float3 f3EndToSphere = new Float3();
	private static final Float3 f3Intersection = new Float3();
	private static final Float3 f3Normal = new Float3();
	
	public boolean IntersectToSphere( Sphere sphere, Intersection intersectionNearest )
	{
		Float3.Sub( f3StartToSphere, sphere.Position, Start );
		float fDot = Float3.Dot( Direction, f3StartToSphere );

		if (fDot <= 0.0f)
		{
			//. 始点より外側.
			float fDistance = Float3.Length( f3StartToSphere );
			if (fDistance  < sphere.Radius)
			{
				//. 始点と接触.
				intersectionNearest.SetNearest(
					Start,
					Float3.Normalize( f3StartToSphere, f3StartToSphere ),
					fDistance);
				return true;
			}
		}
		else if ( Length <= fDot)
		{
			//. 終点より外側.
			Float3.Sub( f3EndToSphere, sphere.Position, End);
			float fDistance = Float3.Length(f3EndToSphere);
			if (fDistance  < sphere.Radius)
			{
				//. 終点と接触.
				intersectionNearest.SetNearest(
					End,
					Float3.Normalize( f3EndToSphere, f3EndToSphere),
					fDistance );
				return true;
			}
		}
		else
		{
			//. 始点と終点の間.
			float fDistance = FMath.Sqrt(Float3.LengthSquared( f3StartToSphere) - fDot * fDot);

			if (fDistance < sphere.Radius)
			{
				Float3.Mad( f3Intersection, Direction, fDot, Start);
				Float3.SubNormalize( f3Normal, sphere.Position, f3Intersection );
				intersectionNearest.SetNearest(
					f3Intersection, f3Normal, fDistance ); //. 線分と中心の距離.
				return true;
			}
		}
		
		return false;
	}
	
}
