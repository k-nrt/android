package com.nrt.collision;

import com.nrt.math.Float3;

public class BoundingBox
{
	public final Float3 Min = new Float3(0.0f);
	public final Float3 Max = new Float3(0.0f);
	public BoundingBox () {}
	
	public BoundingBox( Float3 f3Min, Float3 f3Max )
	{
		Min.Set( f3Min );
		Max.Set( f3Max );
	}
	
	public void Set( BoundingBox box )
	{
		Min.Set( box.Min );
		Max.Set( box.Max );
	}
	
	public static final Float3 f3Min = new Float3();
	public static final Float3 f3Max = new Float3();
	
	public boolean IntersectToSphere( Sphere sphere )
	{
		Float3.Sub( f3Min, sphere.Position, sphere.Radius );
		Float3.Add( f3Max, sphere.Position, sphere.Radius );
		
		if( (f3Max.X < Min.X) || (Max.X < f3Min.X) ||
			(f3Max.Y < Min.Y) || (Max.Y < f3Min.Y) ||
			(f3Max.Z < Min.Z) || (Max.Z < f3Min.Z) )
		{
			return false;
		}
		else
		{
			return true;
		}
	}
}
