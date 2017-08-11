package com.nrt.helloworld;

import com.nrt.math.Float3;
import com.nrt.math.Float3x3;
import com.nrt.math.Float4x4;

import com.nrt.clipper.Box;

class LightTail
{
	public Float4x4[] Transforms = null;
	public float[] Radius = null;
	public int LastPosition = 0;
	public int Length = 0;
	
	public final Box m_boxMinMax = new Box();
	public final Float3 Min = new Float3();
	public final Float3 Max = new Float3();
	
	private int GetTransformIndex( int index )
	{
		return  (LastPosition - 1 - index + Transforms.length) % Transforms.length;
	}
	
	
	private final void UpdateBoundingBox()
	{
		int ii = GetTransformIndex(0);		
		float r = Radius[0];
		float x = Transforms[ii].Values[12];
		float y = Transforms[ii].Values[13];
		float z = Transforms[ii].Values[14];
		Min.Set( x-r, y-r, z-r );
		Max.Set( x+r, y+r, z+r );
		
		for( int i = 1 ; i < Length ; i++ )
		{			
			ii = GetTransformIndex(i);
			r = Radius[i];
			x = Transforms[ii].Values[12];
			y = Transforms[ii].Values[13];
			z = Transforms[ii].Values[14];
			Float3.Min( Min, Min, Float3.Local(x-r,y-r,z-r));
			Float3.Max( Max, Max, Float3.Local(x+r,y+r,z+r));
		}
		
		m_boxMinMax.Update( Min, Max, Float4x4.Identity() );
	}

	public Float4x4 LastTransform()
	{
		return Transforms[LastPosition];
	}

	public LightTail()
	{
	}

	public LightTail( int nbSegments, float fRadius, Float4x4 matrixInitial )
	{
		Transforms = new Float4x4[nbSegments];
		Radius = new float[nbSegments];

		for (int i = 0 ; i < nbSegments ; i++)
		{
			Transforms[i] = new Float4x4( matrixInitial.Values );
			if( i <= 0 || (nbSegments-1) <= i )
			{
				Radius[i] = 0.0f;
			}
			else
			{
				Radius[i] = fRadius;
			}
		}
		
		UpdateBoundingBox();
	}
	
	public LightTail( float[] arrayRadius, Float4x4 matrixInitial )
	{
		int nbSegments = arrayRadius.length;
		Transforms = new Float4x4[nbSegments];
		Radius = new float[nbSegments];

		for (int i = 0 ; i < nbSegments ; i++)
		{
			Transforms[i] = new Float4x4( matrixInitial.Values );
			Radius[i] = arrayRadius[i];
		}

		UpdateBoundingBox();
	}

	public void Reset( Float4x4 matrixInitial )
	{
		LastPosition = 0;
		Length = 0;
		Update( matrixInitial );
	}

	public void Update( Float4x4 matrixTransform )
	{
		Transforms[LastPosition].Set( matrixTransform );
		LastPosition = (LastPosition + 1) % Transforms.length;
		if (Length < Transforms.length)
		{
			Length++;
		}
		UpdateBoundingBox();
		
	}

	public void Update( Float3 v3Position, Float3 v3PrevPosition )
	{
		Float3 v3Forward = Float3.SubNormalize( Float3.Local(), v3Position, v3PrevPosition );
		Float3 v3Up = Float3.Normalize( Float3.Local(), Transforms[(Transforms.length + LastPosition - 1) % Transforms.length].GetAxisY( Float3.Local() ));
		Float3 v3Left = Float3.CrossNormalize(Float3.Local(), v3Up, v3Forward);
		Float3.Cross( v3Up, v3Forward, v3Left);

		Float4x4 matrix = Float4x4.Local(
			Float3x3.Local( v3Left, v3Up, v3Forward ),
			v3Position );
		
		//new Float4x4(v3Left.Xyz0(), v3Up.Xyz0(), v3Forward.Xyz0(), v3Position.Xyz1());
		Update( matrix  );
		
		
		
		//System.Error.WriteLine( String.format( "update %f %f %f", v3Position.X, v3Position.Y, v3Position.Z ) ); 
	}

	public Float4x4 GetTransform(int index)
	{
		int i = GetTransformIndex( index );
		return Transforms[i];
	}
}


