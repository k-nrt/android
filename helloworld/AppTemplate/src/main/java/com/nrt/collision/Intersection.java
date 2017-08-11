package com.nrt.collision;

import com.nrt.math.Float3;
import com.nrt.math.LocalList;

public final class Intersection
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

	/*
	public Intersection Clone()
	{
		return new Intersection( Position, Normal, Distance );
	}
	*/

	public Intersection Clean()
	{
		Position.Set( 0.0f );
		Normal.Set( 0.0f );
		Distance = -1.0f;
		return this;
	}

	public Intersection Set( Float3 f3Position, Float3 f3Normal, float fDistance )
	{
		Position.Set( f3Position );
		Normal.Set( f3Normal );
		Distance = fDistance;
		return this;
	}

	public Intersection SetNearest( Float3 f3Position, Float3 f3Normal, float fDistance )
	{
		if( Distance < 0.0f || fDistance < Distance )
		{
			Position.Set( f3Position );
			Normal.Set( f3Normal );
			Distance = fDistance;
		}
		return this;
	}
	
	
	public Intersection Set( Intersection s )
	{
		Position.Set( s.Position );
		Normal.Set( s.Normal );
		Distance = s.Distance;
		return this;
	}
	
	public void ReplaceLess( Intersection a )
	{
		if( Distance < 0.0f || a.Distance < Distance )
		{
			Position.Set( a.Position );
			Normal.Set( a.Normal );
			Distance = a.Distance;
		}
	}

	public void ReplaceGrater( Intersection a )
	{
		if( Distance < 0.0f || Distance < a.Distance )
		{
			Position.Set( a.Position );
			Normal.Set( a.Normal );
			Distance = a.Distance;
		}
	}

	public static final LocalList<Intersection> m_locals = new LocalList<Intersection>( CreateArray( 256 ));
	public static Intersection[] CreateArray( int nbElements )
	{
		Intersection[] result = new Intersection[nbElements];
		
		for( int i = 0 ; i < result.length ; i++ )
		{
			result[i] = new Intersection();
		}
		return result;
	}

	public static Intersection Local() { return m_locals.Local(); }
}
