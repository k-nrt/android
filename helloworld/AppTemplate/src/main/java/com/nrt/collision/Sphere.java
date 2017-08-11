package com.nrt.collision;

import com.nrt.math.Float3;

public class Sphere
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
