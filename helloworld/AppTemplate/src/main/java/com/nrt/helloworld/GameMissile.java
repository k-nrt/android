package com.nrt.helloworld;

import com.nrt.collision.Edge;
import com.nrt.math.FMath;
import com.nrt.math.Float3;
import com.nrt.math.Float4;
import com.nrt.math.Float4x4;

class GameMissile extends GameShotBase
{
	public final Float3 Position = new Float3(0.0f);
	public final Float3 PrevPosition = new Float3(0.0f);

	public GameTarget Target = null;

	public final Float3 Velocity = new Float3(0.0f);

	public float Time = 0.0f;

	public float TimeSeek = 0.0f;
	public float TimeRelease = 0.0f;
	public float TimeOut = 0.0f;

//	public float Radius = 0.0f;

	public LightTail LightTail = null;
/*
	public enum EStatus
	{
		Idle,
		Busy,
	};
*/
	//public EStatus Status = EStatus.Idle;

	public final Float4 ColorIn = new Float4( 1.0f, 1.0f, 3.0f, 1.0f );
	public final Float4 ColorOut = new Float4( 0.0f, 0.0f, 0.0f, 1.0f );
	
	public static float[] s_radiusKernel = 
	{
		0.0f, 1.5f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
		1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
		1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
		1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,
	};

	public GameMissile()
	{
		//Matrix4 matrix = Matrix4.Identity;
		LightTail = new LightTail(s_radiusKernel, Float4x4.Identity(Float4x4.Local()));
		BaseRadius = 5.0f;
		RadiusKernel = s_radiusKernel;
	}

	public void Spawn(Float4x4 matrixPosition, Float3 v3Velocity)
	{
		//Position = PrevPosition = matrixPosition.AxisW;
		matrixPosition.GetAxisW(Position);
		matrixPosition.GetAxisW(PrevPosition);
		Velocity.Set(v3Velocity);
		Status = EStatus.Busy;

		Time = 0.0f;
		TimeSeek = 1.0f;
		TimeRelease = 8.0f;
		TimeOut = 8.5f;

		RenderRadius = BaseRadius;

		LightTail.Reset(matrixPosition);
	}
/*
	public boolean IsIdle()
	{
		return (Status == EStatus.Idle ? true : false);
	}

	public boolean IsBusy()
	{
		return (Status == EStatus.Busy ? true : false);
	}

	public boolean IsActive()
	{
		return (Status == EStatus.Busy && Time < TimeRelease ? true : false);
	}
*/
	public void Update(float fElapsedTime)
	{
		if (Status == EStatus.Idle)
		{
			return;
		}

		//PrevPosition = Position;
		PrevPosition.Set(Position);
		//Position += Velocity*fElapsedTime;
		Float3.Mad(Position, Velocity, fElapsedTime, Position); 
		Time += fElapsedTime;

		LightTail.Update(Position, PrevPosition);

		m_edge.Set(Position, PrevPosition);

		if (Time < TimeSeek)
		{
			//Vector3 v3Acc = Vector3.Normalize( Velocity )*10.0f*60.0f*fElapsedTime;
			//Velocity += v3Acc;
		}
		else if (Time < TimeRelease)
		{
			if (Target != null)
			{
				//Vector3 v3Direction = Target.GetWorldTransform().AxisW - Position;
				Float3 v3Direction = Float3.Local();
				Target.GetWorldTransform().GetAxisW(v3Direction);
				//v3Direction.Set( 0.0f );
				//System.Error.WriteLine( String.format( "%f %f %f", v3Direction.X, v3Direction.Y, v3Direction.Z ) );
				
				Float3.Sub(v3Direction, v3Direction, Position);

				float fDistance = Float3.Length(v3Direction);

				float fLerp = 0.9f;
				if (fDistance < 1000.0f)
				{
					//. 無理矢理当てる.
					fLerp = 0.2f;
				}

				fLerp = FMath.Pow( fLerp, fElapsedTime/(1.0f/60.0f) );
				
				//v3Direction = Vector3.Normalize( v3Direction );
				Float3.Normalize(v3Direction, v3Direction);
				float fSpeed = Float3.Length(Velocity);


				//Velocity = Vector3.Normalize( Vector3.Slerp( Vector3.Normalize( Velocity ), v3Direction, fLerp ) );

				Float3.Normalize(Velocity, Velocity);
				Float3.Lerp(Velocity, fLerp, v3Direction, Velocity );
				Float3.Normalize(Velocity, Velocity);

				//Velocity *= FMath.Lerp( fSpeed, 300.0f*60.0f, 0.1f );
				
				fLerp = FMath.Pow( 0.95f, fElapsedTime/(1.0f/60.0f) );
				

				Float3.Mul(Velocity, Velocity, FMath.Lerp( 300.0f * 60.0f, fSpeed, fLerp ));

			//	System.Error.WriteLine( String.format( "%f %f %f", Velocity.X, Velocity.Y, Velocity.Z ) );
				//System.Error.WriteLine( String.format( "%f %f %f", Velocity.X, Velocity.Y, Velocity.Z ) );
				
				if (Target.Transform != null && Target.IsIdle())
				{
				//	Target = null;
				}

			}
		}
		else if (Time < TimeOut)
		{
			RenderRadius = (1.0f - (Time - TimeRelease) / (TimeOut - TimeRelease)) * BaseRadius;
		}
		else
		{
			Status = EStatus.Idle;
		}
	}

	//public Edge Edge = new Edge();
	/*
	 public Edge GetEdge()
	 {

	 return new Collision.Edge( PrevPosition, Position );
	 }
	 */
	public void Release()
	{
		if (IsBusy())
		{
			Time = TimeRelease;
			Status = EStatus.Release;
		}
	}



}


class GameMissileCluster
{
	public GameMissile[] Missiles = null;
	public int Next = 0;

	public GameMissileCluster(int nbMissiles)
	{
		Missiles = new GameMissile[nbMissiles];
		for (int i = 0 ; i < Missiles.length ; i++)
		{
			Missiles[i] = new GameMissile();
		}
	}

	public GameMissile Spawn( Float4x4 matrixPosition, Float3 v3Velocity )
	{
		for (int i = 0 ; i < Missiles.length ; i++)
		{
			int ii = (Next + i) % Missiles.length;

			if (Missiles[ii].IsIdle())
			{
				Missiles[ii].Spawn( matrixPosition, v3Velocity );
				Next = (ii + 1) % Missiles.length;
				return Missiles[ii];
			}
		}

		return null;
	}

	public void Update(float fElapsedTime)
	{
		for( GameMissile missile : Missiles )
		{
			missile.Update(fElapsedTime);
		}
	}
}



	
	
