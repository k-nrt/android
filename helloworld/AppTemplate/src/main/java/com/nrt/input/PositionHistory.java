package com.nrt.input;

import com.nrt.math.Float3;

public class PositionHistory
{
	public Float3[] Positions = null;
	public float[] ElapsedTimes = null;
	public float[] Speeds = null; 
	public float AverageSpeed = 0.0f;
	public int Pos = 0;
	public int Histories = 0;

	public PositionHistory( int nbHistories )
	{
		Positions = new Float3[nbHistories];
		ElapsedTimes = new float[nbHistories];
		Speeds = new float[nbHistories];
		for( int i = 0 ; i < Positions.length ; i++ )
		{
			Positions[i] = new Float3();
		}
	}

	public void Reset()
	{
		AverageSpeed = 0.0f;
		Pos = 0;
		Histories = 0;
	}

	public void UpdatePosition( Float3 f3Position, float fElapsedTime )
	{
		if( Histories <= 0 )
		{
			Positions[Pos].Set( f3Position );
			ElapsedTimes[Pos] = 0.0f;
			Speeds[Pos] = 0.0f;
		}
		else
		{
			int Prev = (Pos - 1 + Positions.length)%Positions.length;
			Positions[Pos].Set( f3Position );
			ElapsedTimes[Pos] = fElapsedTime;
			Speeds[Pos] = Float3.Distance( Positions[Pos], Positions[Prev] )/fElapsedTime;
		}

		if( Histories < Positions.length )
		{
			Histories++;
		}

		Pos = (Pos+1)%Positions.length;

		AverageSpeed = 0.0f;

		for( int i = 0 ; i < Histories ; i++ )
		{
			AverageSpeed += Speeds[i];
		}
		AverageSpeed /= (float) Histories;
	}
}
	
