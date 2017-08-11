package com.nrt.helloworld;

import com.nrt.math.*;

public final class GamePlayerWeapon
{
	public String Name = "";
	public int Bursts = 0;
	public float Interval = 0.1f;

	public float Speed = 0.0f;
	public float Pop = 0.0f;

	public Float4x4 SpawnPosition = Float4x4.Identity();

	public float Time = 0.0f;

	public GameShotBase[] Shots = null;


	public GamePlayerWeapon(String strName, int nbBursts, float fInterval, float fSpeed, float fPop, GameShotBase[] shots)
	{
		Name = strName;
		Bursts = nbBursts;
		Interval = fInterval;
		Speed = fSpeed;
		Pop = fPop;
		Shots = shots;

	}

	public float GetReloadRate()
	{
		return FMath.Clamp(1.0f - Time / Interval, 0.0f, 1.0f);
	}

	public void Update(float fElapsedTime)
	{
		if (Time >= 0.0f)
		{
			Time -= fElapsedTime;
		}
	}

	public boolean Spawn(Float4x4 matrixRoot, Float3 v3RootVelocity)
	{
		if (Time > 0.0f)
		{
			return false;
		}

		for (GameShotBase shot : Shots)
		{
			if (shot.IsIdle())
			{
				Float4x4 matrix = Float4x4.Local();
				Float4x4.Mul(
					matrix,
					Float4x4.Translation(Float4x4.Local(), -50.0f, -30.0f, 0.0f),
					matrixRoot);

				Float3 f3Velocity = Float3.Local();
				Float3 f3Z = Float3.Local();
				matrixRoot.GetAxisZ(f3Z);
				Float3.Mad(f3Velocity, f3Z, Speed, v3RootVelocity);
				Float3.Add(f3Velocity, Float3.Rand(Float3.Local(), -Pop, Pop), f3Velocity);
				shot.Spawn(matrix, f3Velocity);

				Time = Interval;
				return true;
			}
		}
		return false;
	}
}
