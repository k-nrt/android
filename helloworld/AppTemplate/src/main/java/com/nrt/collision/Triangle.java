package com.nrt.collision;

import com.nrt.math.Float3;

public class Triangle
{
	public Float3 Normal = new Float3( 0.0f );

	public Edge[] Edges = null;			
	public Triangle ()
	{
	}

	public Triangle( Float3 v3Normal, Float3 v3Vertex0, Float3 v3Vertex1, Float3 v3Vertex2 )
	{
		Float3.Normalize( Normal, v3Normal );
		Edges = new Edge[3];
		Edges[0] = new Edge( v3Vertex0, v3Vertex1 );
		Edges[1] = new Edge( v3Vertex1, v3Vertex2 );
		Edges[2] = new Edge( v3Vertex2, v3Vertex0 );
	}
	
	private static final Float3 f3Sub0 = new Float3();
	private static final Float3 f3Intersection = new Float3();
	
	public boolean IntersectToSphere( Sphere sphere, Intersection intersectionNearest )
	{
		float fDistance = Float3.Dot( Normal, Float3.Sub( f3Sub0, sphere.Position, Edges[0].Start) );

		if (fDistance < 0.0f || sphere.Radius < fDistance)
		{
			//. 面に接していない.
			//. 中心点が裏のときも経験上取らない方が良い.
			//. ※鋭角なエッジで裏側のポリゴンに当たってしまう.
			return false;
		}

		//. 球の中心との距離が最短になる平面上の点.
		Float3.Mad( f3Intersection, Normal, -fDistance, sphere.Position);

		//. ポリゴン内に v33Itersectioin があるかどうか.
		if( IsContain( f3Intersection ) )
		{
			//. ポリゴン内で接触.
			intersectionNearest.SetNearest( f3Intersection, Normal, fDistance );
			return true;
		}

		//. エッジとの当たり.
		boolean isHit = false;
		for (int i = 0 ; i < 3 ; i++)
		{
			if( Edges[i].IntersectToSphere( sphere, intersectionNearest ))
			{
				isHit = true;
			}
		}

		return isHit;
	}

	private static final Float3 f3FirstCross = new Float3();
	private static final Float3 f3Cross = new Float3();

	private boolean IsContain( Float3 f3Target )
	{
		Float3.Cross( f3FirstCross, Edges[0].Direction, Float3.Sub( f3Sub0, f3Intersection, Edges[0].Start));
		for (int i = 1 ; i < 3 ; i++)
		{
			Float3.Cross( f3Cross,
				Edges[i].Direction,
				Float3.Sub( f3Sub0, f3Target, Edges[i].Start));

			if (Float3.Dot(f3FirstCross, f3Cross) < 0.0f)
			{
				return false;
			}
		}

		return true;
	}	
}
