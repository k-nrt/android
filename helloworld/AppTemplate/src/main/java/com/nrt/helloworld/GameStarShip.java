package com.nrt.helloworld;

import com.nrt.math.Transform3;
import com.nrt.model.*;
import com.nrt.collision.*;
import com.nrt.math.FMath;
import com.nrt.math.Float3;
import com.nrt.math.Float4;
import com.nrt.math.Float4x4;

import com.nrt.framework.SubSystem;


class GameStarShip
{
	public final Float4x4 Transform = Float4x4.Identity();
	public final Float4x4 TransformInverse = Float4x4.Identity();

	public final Float3 Velocity = new Float3();
	public final Float3 AngularVelocity = new Float3();

	public enum EStatus
	{
		Idle,
		Busy,
		Explosion,
		ExplosionFinal,
	};

	public EStatus Status = EStatus.Idle;

	//public Model Model = null;
	public GameModel Model = new GameModel();
	public CollisionMesh Collision = null;

	//public float Shield = 400.0f;

	public float m_fTime = 0.0f;
	public float m_fTimeOut = 0.0f;

	public float DiscardRadius = 0.0f;

	public int m_nbFireRemains = 0;
	public float m_fFireTime = 0.0f;
	
	//public float ShieldRadius = 0.0f;
	//public final Sphere ShieldSphere = new Sphere();
	/*
	public float AntiBeamFieldRadius = 1200.0f;
	public float AntiBeamFieldDamage = 0.0f;
	public float AntiBeamFieldDamageLimit = 100.0f;
	*/
	public final Float3 Position = new Float3( 0.0f );
	
	public final GameAntiBeamField AntiBeamField = new GameAntiBeamField();
	public float BodyDamage = 0.0f;
	public float BodyDamageLimit = 1000.0f;

	public GameStarShip()
	{}

	public boolean IsIdle()
	{ return ((Status == EStatus.Idle) ? true : false); }
	public boolean IsBusy()
	{ return ((Status == EStatus.Busy) ? true : false); }
	public boolean IsExplosion()
	{ return ((Status == EStatus.Explosion) ? true : false); }
	public boolean IsExplosionFinal()
	{ return ((Status == EStatus.ExplosionFinal) ? true : false); }

	public void Spawn(Float4x4 transform, Model model, CollisionMesh collisionMesh)
	{
		Transform.Set(transform);
		Float4x4.Invert(TransformInverse, Transform);

		Velocity.Set(0.0f);
		AngularVelocity.Set(0.0f);

		Status = EStatus.Busy;

		Model.Model = model;
		Collision = collisionMesh;

		//Shield = 1000.0f;

		DiscardRadius = 0.0f;
		
		AntiBeamField.Spawn( Transform3.LoadRotationX( Transform3.Local(), -FMath.PI/2.0f ), Transform3.Local().Load( Transform ) );
		//AntiBeamField.Spawn( new Transform3().Load( transform.Values ) );
		BodyDamage = 0.0f;
		
		
		//ShieldSphere.Radius = 1500.0f;
	}

	public void Fire(GameShot[] shots, Float3 f3Target, Float3 f3Position)
	{
		for (GameShot shot : shots)
		{
			if (shot.IsIdle())
			{
				Float3 f3Velocity = Float3.Local();
				Float3.SubNormalize(f3Velocity, f3Target, f3Position);
				Float3.Mul(f3Velocity, f3Velocity, 2000.0f);


				shot.Spawn(Transform, f3Velocity);
				break;
			}
		}
	}
	public void Update(float fElapsedTime, GameParticle particle, GameTarget targetPlayer, GameShot[] shots)
	{
		switch (Status)
		{
			case Idle:
				break;
				
			case Busy:
				if (m_nbFireRemains <= 0 && SubSystem.Rand.Float() < 1.0f / 60.0f && targetPlayer.Busy)
				{
					Float3 f3Target = Float3.Local();
					targetPlayer.GetWorldTransform().GetAxisW(f3Target);
					Float3 f3Position = Float3.Local();
					Transform.GetAxisW(f3Position);

					if (Float3.Distance(f3Target, f3Position) < 4000.0f)
					{
						m_nbFireRemains = 4;
					}
				}


				if (m_nbFireRemains > 0)
				{
					if (m_fFireTime <= 0.0f)
					{
						Float3 f3Target = Float3.Local();
						targetPlayer.GetWorldTransform().GetAxisW(f3Target);
						Float3 f3Position = Float3.Local();
						Transform.GetAxisW(f3Position);

						Float3.Mad( f3Target, targetPlayer.Velocity, 0.2f*(float)m_nbFireRemains, f3Target );
						
						
						Fire(shots, f3Target, f3Position);
		
						m_nbFireRemains--;
						m_fFireTime += 0.1f;
					}
					else
					{
						m_fFireTime -= fElapsedTime;
					}
				}
				break;

			case Explosion:
				{
					Float4 f4 = Float4.Local(0.0f, 0.0f, 0.0f, 1.0f);

					Float3 f3 = Float3.Local();
					if (SubSystem.Rand.Float() < 0.5f)
					{
						//Transform.AxisW(f3);
						f4.X = SubSystem.Rand.Float(Model.Model.Min.X, Model.Model.Max.X);
						f4.Y = SubSystem.Rand.Float(Model.Model.Min.Y, Model.Model.Max.Y);

						float c = (Model.Model.Min.Z + Model.Model.Max.Z) * 0.5f;
						float r = (Model.Model.Max.Z - Model.Model.Min.Z) * 0.5f * m_fTime / m_fTimeOut;
						f4.Z = SubSystem.Rand.Float(c - r, c + r);
						Float4x4.Mul(f3, f4, Transform);
						particle.SpawnSmallExplosion(f3, SubSystem.Rand.Float(100.0f, 200.0f));

						Float3 f3Size = Float3.SubNormalize(Float3.Local(), Model.Model.Max, Model.Model.Min);

						Float3.Mul(f3Size, 0.05f, f3Size);

						Float3.Add(AngularVelocity, AngularVelocity,
								   Float3.Local(
									   SubSystem.Rand.Float(-f3Size.X, f3Size.X), 
									   SubSystem.Rand.Float(-f3Size.Y, f3Size.Y), 
									   SubSystem.Rand.Float(-f3Size.Z, f3Size.Z)));

						Float3.Add(Velocity, Velocity,
								   Float3.Rand(Float3.Local(), -10.0f, 10.0f));
					}

					m_fTime += fElapsedTime;
					if (m_fTimeOut < m_fTime)
					{
						Status = EStatus.ExplosionFinal;
						m_fTime = 0.0f;
						m_fTimeOut = 2.0f;


						//Float3 f3 = Float3.Local();
						//Transform.AxisW(f3);
						particle.SpawnFlash(f3, Float3.Local(0.0f), 2000.0f);

						for (int i = 0 ; i < 4 ; i++)
						{
							//Float3 f3 = Float3.Local();
							//Transform.AxisW(f3);
							f4.X = SubSystem.Rand.Float(Model.Model.Min.X, Model.Model.Max.X);
							f4.Y = SubSystem.Rand.Float(Model.Model.Min.Y, Model.Model.Max.Y);
							f4.Z = SubSystem.Rand.Float(Model.Model.Min.Z, Model.Model.Max.Z);
							Float4x4.Mul(f3, f4, Transform);

							particle.SpawnSmallExplosion(f3, SubSystem.Rand.Float(200.0f, 400.0f));
						}

					}
				}
				break;

			case ExplosionFinal:
				{
					DiscardRadius = m_fTime / m_fTimeOut;

					m_fTime += fElapsedTime;
					if (m_fTimeOut < m_fTime)
					{
						Status = EStatus.Idle;
					}
				}
				break;

		}

		Float3 v3X = Transform.GetAxisX(Float3.Local());
		Float3 v3Y = Transform.GetAxisY(Float3.Local());
		Float3 v3Z = Transform.GetAxisZ(Float3.Local());
		Float3 v3W = Transform.GetAxisW(Float3.Local());

		//. 回転の積分

		Float4x4 matrix = Float4x4.Local().Set(Transform);
		//matrix.AxisW = Vector3.Zero;
		//matrix = Matrix4.RotationAxis( v3Y, AngularVelocity.Y*fElapsedTime )*matrix;
		//matrix = Matrix4.RotationAxis( v3X, AngularVelocity.X*fElapsedTime )*matrix;
		//matrix = Matrix4.RotationAxis( v3Z, AngularVelocity.Z*fElapsedTime )*matrix;
		matrix.SetAxisW(Float3.Local().Set(0.0f));
		Float4x4.Mul(
			matrix, 
			matrix,
			Float4x4.Rotation(Float4x4.Local(), v3Z, AngularVelocity.Z * fElapsedTime),
			Float4x4.Rotation(Float4x4.Local(), v3X, AngularVelocity.X * fElapsedTime),
			Float4x4.Rotation(Float4x4.Local(), v3Y, AngularVelocity.Y * fElapsedTime)
		);

		Transform.Set(matrix);

		//. 速度の変更.

		//. 位置積分.
		//v3W += Velocity * fElapsedTime;
		v3W = Float3.Mad(v3W, Velocity, fElapsedTime, v3W);

		//. 姿勢へ戻す.
		//Transform.AxisW = v3W;
		Transform.SetAxisW(v3W);
		
		Position.Set( v3W );

		AntiBeamField.OnUpdate( fElapsedTime, Transform3.Local().Load( Transform ) );
		//AntiBeamField.Transform.Load( Transform.Values );

	}

	public Intersection Intersect(Edge edgeWorld)
	{
		if (Status == EStatus.Idle)
		{
			return null;
		}

		if (Collision == null)
		{
			return null;
		}

		Edge edge = Edge.Local();

		Float4x4.Mul(edge.Start, Float4.Local().Set( edgeWorld.Start, 1.0f ), TransformInverse);
		Float4x4.Mul(edge.End,   Float4.Local().Set( edgeWorld.End, 1.0f ),   TransformInverse);

		//Intersection intersection = Collision.Intersect( edge );
		//if( intersection == null )
		Intersection intersection = Intersection.Local();// Collision.Intersect( edge );
		if (Collision.Intersect(edge, intersection) == false)
		{
			return null;
		}

		Float4x4.Mul(intersection.Position, Float4.Local().Set( intersection.Position, 1.0f ), Transform);
		Float4x4.Mul(intersection.Normal,   Float4.Local().Set( intersection.Normal, 0.0f ),   Transform);

		//System.Error.WriteLine( "hit" );
		return intersection;
	}

	public boolean Intersect(Sphere sphereWorld, Intersection intersectionWorld)
	{
		if (Status == EStatus.Idle)
		{
			return false;
		}

		if (Collision == null)
		{
			return false;
		}

		Sphere sphere = new Sphere();
		Float4x4.Mul(sphere.Position, Float4.Local().Set( sphereWorld.Position, 1.0f ), TransformInverse);
		sphere.Radius = sphereWorld.Radius;
		Intersection intersection = Intersection.Local().Clean();// Collision.Intersect( edge );
		
		if (Collision.IntersectToSphere(sphere, intersection) == false)
		{
			return false;
		}

		Float4x4.Mul(intersectionWorld.Position, Float4.Local().Set( intersection.Position, 1.0f ), Transform);
		Float4x4.Mul(intersectionWorld.Normal,   Float4.Local().Set( intersection.Normal, 0.0f ),   Transform);
		intersectionWorld.Distance = intersection.Distance;

		return true;
	}

	public boolean AddBodyDamage( GameShotBase shot, GameParticle particle)
	{
		if( IsBusy() )
		{
			BodyDamage += shot.Power;

			if( BodyDamage >= BodyDamageLimit )
			{
				Status = EStatus.Explosion;
				m_fTime = 0.0f;
				m_fTimeOut = 5.0f;

				BodyDamage = BodyDamageLimit;

				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	public boolean AddAntiBeamFieldDamage( GameShotBase shot, GameParticle particle )
	{
		if( IsBusy() )
		{
			AntiBeamField.Damage += shot.Power;

			AntiBeamField.m_adsr.Start();
			
			if( AntiBeamField.Damage >= AntiBeamField.DamageLimit )
			{
				AntiBeamField.Damage = AntiBeamField.DamageLimit;

				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}		
	}
}

