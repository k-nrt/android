package com.nrt.helloworld;

import com.nrt.math.Float3;
import com.nrt.math.Float4x4;

class GameTarget
{
	public Float4x4 Transform = null;
	public Float3 Velocity = null;
	public boolean Busy = false;

	public boolean IsBusy() { return Busy; }
	public boolean IsIdle() { return !Busy; }
	
	public Float4x4 GetWorldTransform()
	{
		return Transform;
	}
	
	public Float3 GetWorldVelocity()
	{
		return Velocity;
	}
	
	public void Update( Float4x4 transform, Float3 velocity, boolean isBusy )
	{
		Transform = transform;
		Velocity = velocity;
		Busy = isBusy;
	}
}

class GameTargetList
{
	public final GameTarget World = new GameTarget();
	public final GameTarget Player = new GameTarget();
	public final GameTarget ViewPoint = new GameTarget();
	
	public final GameTarget[] Enemies = new GameTarget[20];
	public final GameTarget[] StarShips = new GameTarget[10];
	
	public GameTargetList()
	{
		for( int i = 0 ; i < Enemies.length ; i++ )
		{
			Enemies[i] = new GameTarget();
		}

		for( int i = 0 ; i < StarShips.length ; i++ )
		{
			StarShips[i] = new GameTarget();
		}
	}
	
}
