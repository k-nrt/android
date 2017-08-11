package com.nrt.helloworld;

import com.nrt.model.Model;
import com.nrt.collision.Sphere;
import com.nrt.collision.Edge;
import com.nrt.collision.Intersection;
import com.nrt.collision.Collision;
import com.nrt.math.FMath;
import com.nrt.math.Float3;
import com.nrt.math.Float4;
import com.nrt.math.Float4x4;

import com.nrt.framework.SubSystem;


public class GameEnemy
{
	/*
	 static float Randf ()
	 {
	 return Rand.Float ();
	 }

	 static float Randf (float fMin, float fMax)
	 {
	 return Rand.Float (fMin, fMax);
	 }
	 */
	static float Clamp(float a, float fMin, float fMax)
	{
		return (a < fMin ? fMin : (a > fMax ? fMax : a));
	}

	static float Lerp(float f0, float f1, float fLerp)
	{
		return f0 * (1.0f - fLerp) + f1 * fLerp;
	}

	//. 位置と姿勢と速度.
	public final Float4x4 Transform = new Float4x4();
	public final Float3 Velocity = new Float3();
	public final Float3 AngularVelocity = new Float3();

	public final Float3 GetPosition(Float3 dst)
	{ return Transform.GetAxisW(dst);  }

	//. 目標と速度と方向.
	public final Float3 m_v3TargetVelocity = new Float3();;
	public final Float3 m_v3TargetDirection = new Float3();;
	public final Float3 m_v3TargetUp = new Float3();;

	//. 使用中の戦闘機インデクス.
	//int m_iStarFighter;

	float m_fTime;
	float m_fTimeOut;

	enum EStatus
	{
		Idle,
		Busy,
		Explosion,
	};

	EStatus m_eStatus;
	float m_fDamage;
	float m_fShield;




	//. 敵の動きとか.
	enum EAi
	{
		Move,
		Boost,
		Attack,
	};

	EAi m_eAi;
	public int SquadPosition = 0;
	public GameEnemy Leader = null;
	public final Float3 m_v3AiTarget = new Float3(); //. プレイヤー位置.
	float m_fAiTime;
	float m_fAiTimeOut;
	int m_nAiCount;
	public boolean Fire = false;

	/// <summary>
	/// 追跡目標.
	/// </summary>
	public GameTarget LocationTarget = null;
	public GameTarget AttackTarget = null;

	public final Float3 MaxLocalAngularVelocity = new Float3(FMath.PI * 2.0f, FMath.PI * 2.0f, FMath.PI * 1.0f);
	public final Float3 MaxLocalVelocity = new Float3(1000.0f * 2.0f, 1000.0f * 2.0f, 2000.0f * 2.0f);


	public enum EType
	{
		AWACS,
		Assault,
		LightArmor,
		HeavyGunner,
		Max,
	};

	public EType Type = EType.AWACS;
	public String HudName = "";
	public Model Model = null;

	public static class TypeDesc
	{
		public GameEnemy.EType Type = EType.AWACS;
		public String HudName = "";
		public Model Model = null;

		public TypeDesc()
		{}
		public TypeDesc(EType eType, String strHudName, Model model)
		{
			Type = eType;
			HudName = strHudName;
			Model = model;
		}
	}

	public void SetType(EType type, String strHudName, Model model)
	{
		Type = type;
		HudName = strHudName;
		Model = model;
	}

	public void SetType(TypeDesc typeDesc)
	{
		Type = typeDesc.Type;
		HudName = typeDesc.HudName;
		Model = typeDesc.Model;		
	}

	public GameEnemy()
	{
		m_fTime = 0.0f;
		m_fTimeOut = 0.0f;

		Float4x4.Translation(Transform, Float3.Rand(Float3.Local(), -2000.0f, 2000.0f));
		Velocity.Set(0.0f);
		AngularVelocity.Set(0.0f);

		m_v3TargetVelocity.Set(0.0f);
		m_v3TargetDirection.Set(0.0f);

		m_eStatus = EStatus.Idle;

		//	m_object.Clean();
		//m_iStarFighter = -1;

		m_fDamage = 0.0f;
		m_fShield = 10.0f;

		m_v3AiTarget.Set(0.0f);
		m_eAi = EAi.Move;
		m_fAiTime = 0.0f;
		m_fAiTimeOut = 0.0f;
	}

	public void Update(float fElapsedTime, GameParticle particle)
	{
		switch (m_eStatus)
		{
			case Idle:
				break;

			case Busy:
				{
					switch (m_eAi)
					{
						case Move:
							m_eAi = _DoAiMove(fElapsedTime);
							break;
						case Boost:
							m_eAi = _DoAiBoost(fElapsedTime);
							break;
						case Attack:
							m_eAi = _DoAiAttack(fElapsedTime);
							break;
					}

					//. 基本機動.
					DoBasicManeuver(fElapsedTime);
				}
				break;

			case Explosion:
				{
					if (m_fTime >= m_fTimeOut)
					{
						m_fTime = 0.0f;
						m_fTimeOut = 5.0f + SubSystem.Rand.Float() * 5.0f;
						m_eStatus = EStatus.Idle;

						particle.SpawnSmallExplosion(Transform.GetAxisW(Float3.Local()), 400.0f);
					}
					else
					{
						if (SubSystem.Rand.Float() < 0.5f)
						{
							Float3 v3Position = //Transform.AxisW + (Rand.Vector3()*2.0f - 1.0f)*50.0f - Transform.AxisZ*100.0f;
								Float3.Add(
								Float3.Local(),
								Transform.GetAxisW(Float3.Local()),
								Float3.Sub(
									Float3.Local(),
									Float3.Rand(-50.0f, 50.0f),
									Float3.Mul(Float3.Local(), Transform.GetAxisZ(Float3.Local()), 100.0f) 
								)
							);
							particle.SpawnSmoke(v3Position, Float3.Local().Set(0.0f), SubSystem.Rand.Float(100.0f, 150.0f));
						}
						m_fTime += fElapsedTime;
					}
				}
				break;
		}

		EvaluateLeaderStatus();

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
	}

	//. 基本機動.
	//. m_v3TargetDirection を向く.
	//. m_v3TargetUp を上向きにする.
	void DoBasicManeuver(float fElapsedTime)
	{
		final Float3 v3X = Transform.GetAxisX(Float3.Local());
		final Float3 v3Y = Transform.GetAxisY(Float3.Local());
		final Float3 v3Z = Transform.GetAxisZ(Float3.Local());
		final Float3 v3W = Transform.GetAxisW(Float3.Local());

		//. 向きの変更.
		final Float3 v3Forward = Float3.Local();
		Float3.Normalize(v3Forward, m_v3TargetDirection);
		final Float3 v3Up = Float3.Local();
		Float3.Normalize(v3Up, m_v3TargetUp);
		if (!v3Forward.IsZero() && !v3Up.IsZero())
		{
			final Float3 v3Heading = Float3.Local();
			Float3.SubNormalize(v3Heading,  v3Forward, Float3.Mul(v3Heading, Float3.Dot(v3Y, v3Forward), v3Y));
			final Float3 v3Pitch   = Float3.Local();
			Float3.SubNormalize(v3Pitch, v3Forward, Float3.Mul(v3Pitch, Float3.Dot(v3X, v3Forward), v3X));
			final Float3 v3Bank    = Float3.Local();
			Float3.SubNormalize(v3Bank, v3Up, Float3.Mul(v3Bank, Float3.Dot(v3Z, v3Up), v3Z));

			float fMinAngleY = (FMath.PI / 16.0f) * 60.0f * fElapsedTime;//(FMath.PI/16.0f)*60.0f*fElapsedTime;
			float fMinAngleX = (FMath.PI / 16.0f) * 60.0f * fElapsedTime;//(FMath.PI/16.0f)*60.0f*fElapsedTime;
			float fMinAngleZ = (FMath.PI / 16.0f) * 60.0f * fElapsedTime;//(FMath.PI/16.0f)*60.0f*fElapsedTime;

			float fAccelX = (FMath.PI / 16.0f) * 60.0f * fElapsedTime;
			float fAccelY = (FMath.PI / 16.0f) * 60.0f * fElapsedTime;
			float fAccelZ = (FMath.PI / 32.0f) * 60.0f * fElapsedTime;

			float fLerp = FMath.Pow(0.95f, fElapsedTime / (1.0f / 60.0f));

			//. ヘディング.
			float fY  = -FMath.Atan2(Float3.Dot(v3Heading, v3X), Float3.Dot(v3Heading, v3Z));
			if (fY < -fMinAngleY)
			{
				AngularVelocity.Y -= fAccelY;
			}
			else if (fMinAngleY < fY)
			{
				AngularVelocity.Y += fAccelY;
			}
			else
			{
				AngularVelocity.Y = Lerp(fY / fElapsedTime, AngularVelocity.Y, fLerp);
			}

			float fX = FMath.Atan2(Float3.Dot(v3Pitch, v3Y), Float3.Dot(v3Pitch, v3Z));
			if (fX < -fMinAngleX)
			{
				AngularVelocity.X -= fAccelX;
			}
			else if (fMinAngleX < fX)
			{
				AngularVelocity.X += fAccelX;
			}
			else
			{
				AngularVelocity.X = Lerp(fX / fElapsedTime, AngularVelocity.X, fLerp);
			}

			float fZ = FMath.Atan2(Float3.Dot(v3Bank, v3X), Float3.Dot(v3Bank, v3Y));
			if (fZ < -fMinAngleZ)
			{
				AngularVelocity.Z -= fAccelZ;
			}
			else if (fMinAngleZ < fZ)
			{
				AngularVelocity.Z += fAccelZ;
			}
			else
			{
				AngularVelocity.Z = Lerp(fZ / fElapsedTime, AngularVelocity.Z, fLerp);
			}

			Float3.Max(AngularVelocity, Float3.Neg(Float3.Local(), MaxLocalAngularVelocity), AngularVelocity);
			Float3.Min(AngularVelocity, MaxLocalAngularVelocity, AngularVelocity);
		}

		//. 速度変更.
		{
			float fVX = Float3.Dot(Velocity, Transform.GetAxisX(Float3.Local()));
			float fVY = Float3.Dot(Velocity, Transform.GetAxisY(Float3.Local()));
			float fVZ = Float3.Dot(Velocity, Transform.GetAxisZ(Float3.Local()));

			float fTVX = Float3.Dot(m_v3TargetVelocity, Transform.GetAxisX(Float3.Local()));
			float fTVY = Float3.Dot(m_v3TargetVelocity, Transform.GetAxisY(Float3.Local()));
			float fTVZ = Float3.Dot(m_v3TargetVelocity, Transform.GetAxisZ(Float3.Local()));

			//. 加速度.
			float fAZ = 100.0f * 60.0f * fElapsedTime;
			float fAY = 10.0f * 60.0f * fElapsedTime;
			float fAX = 10.0f * 60.0f * fElapsedTime;

			//. 最大速度.
			float fMZ = MaxLocalVelocity.X;
			float fMY = MaxLocalVelocity.Y;
			float fMX = MaxLocalVelocity.Z;

			if (fVZ < fTVZ && fVZ < fMZ)
			{
				fVZ += fAZ;
				if (fTVZ < fVZ)
				{
					fVZ = fTVZ;
				}
			}
			else if (fTVZ < fVZ && -fMZ < fVZ)
			{
				fVZ -= fAZ;
				if (fVZ < fTVZ)
				{
					fVZ = fTVZ;
				}
			}

			if (fVY < fTVY && fVY < fMY)
			{
				fVY += fAY;
				if (fTVY < fVY)
				{
					fVY = fTVY;
				}
			}
			else if (fTVY < fVY && -fMY < fVY)
			{
				fVY -= fAY;
				if (fVY < fTVY)
				{
					fVY = fTVY;
				}
			}

			if (fVX < fTVX && fVX < fMX)
			{
				fVX += fAX;
				if (fTVX < fVX)
				{
					fVX = fTVX;
				}
			}
			else if (fTVX < fVX && -fMX < fVX)
			{
				fVX -= fAX;
				if (fVX < fTVX)
				{
					fVX = fTVX;
				}
			}

			fVX = Clamp(fVX, -MaxLocalVelocity.X, MaxLocalVelocity.X);
			fVY = Clamp(fVY, -MaxLocalVelocity.Y, MaxLocalVelocity.Y);
			fVZ = Clamp(fVZ, -MaxLocalVelocity.Z, MaxLocalVelocity.Z);

			//Velocity = Transform.TransformVector( new Vector3( fVX, fVY, fVZ ) );
			Float4x4.Mul(Velocity, Float4.Local(fVX, fVY, fVZ, 0.0f), Transform);
		}
	}

	//. ダメージ加算.
	public boolean Damage(Edge edge, float fPower, GameParticle particle)
	{
		if (m_eStatus != EStatus.Busy)
		{
			return false;
		}

		Intersection intersection = new Intersection();
		if (Collision.Intersect(edge, GetSphere(), intersection))
		{
			//. 当たった.
			m_fDamage += fPower;
			particle.SpawnSmallExplosion(intersection.Position, SubSystem.Rand.Float(100.0f, 120.0f));

			if (m_fDamage > m_fShield)
			{
				m_eStatus = EStatus.Explosion;
				m_fTime = 0.0f;
				m_fTimeOut = 4.0f;
				Fire = false;
				//Velocity += Vector3.Normalize(Transform.AxisW - intersection.Position)*Rand.Float( 800.0f, 1000.0f );
				Float3.Mad(
					Velocity,
					Float3.SubNormalize(Float3.Local(), Transform.GetAxisW(Float3.Local()), intersection.Position),
					SubSystem.Rand.Float(800.0f, 1000.0f),
					Velocity);
				//AngularVelocity += (Rand.Vector3()*2.0f - 1.0f)*FMath.PI*0.5f;
				Float3.Add(
					AngularVelocity,
					Float3.Rand(Float3.Local(), -FMath.PI * 0.5f, FMath.PI * 0.5f),
					AngularVelocity);
				return true;

			}
		}

		return false;
	}


	//. ステータス取得.
	public boolean IsIdle()
	{
		return (m_eStatus == EStatus.Idle ? true : false);
	}

	public boolean IsBusy()
	{
		return (m_eStatus == EStatus.Busy ? true : false);
	}

	public boolean IsExplosion()
	{
		return (m_eStatus == EStatus.Explosion ? true : false);
	}

	//. 当たりの有無.-
	public boolean IsActive()
	{
		return (m_eStatus == EStatus.Busy ? true : false);
	}

	public boolean IsVisible()
	{
		return !IsIdle();
	}

	//bool IsFire() { return m_isAiFire; }

	public void SetVelocityTarget(Float3 v3Velocity)
	{
		m_v3TargetVelocity.Set(v3Velocity);
	}

	public void SetDirectionTarget(Float3 v3Direction)
	{
		Float3 v3 = Float3.Local();
		Float3.Normalize(v3, v3Direction);
		if (v3.IsZero() == false)
		{
			m_v3TargetDirection.Set(v3);
		}
		else 
		{
			//. 失敗.
			Transform.GetAxisZ(m_v3TargetDirection);
		}
	}

	public void SetUpTarget(Float3 v3Up)
	{
		Float3 v3NormalizedUp = Float3.Local();
		Float3.Normalize(v3NormalizedUp,  v3Up);
		if (v3NormalizedUp.IsZero() == false)
		{
			m_v3TargetUp.Set(v3NormalizedUp);
		}
		else
		{
			//. 失敗.
			Transform.GetAxisY(m_v3TargetUp);
		}
	}

	public void Spawn(Float4x4 matrixTransform)
	{
		Transform.Set(matrixTransform);
		AngularVelocity.Set(0.0f);
		Velocity.Set(0.0f);

		m_fTime = 0.0f;
		m_fTimeOut = 10.0f + SubSystem.Rand.Float() * 10.0f;

		m_eStatus = EStatus.Busy;
		m_fDamage = 0.0f;
	}

	//. てきとう AI.
	private EAi _DoAiMove(float fElapsedTime)
	{
		Fire = false;
		if (m_fAiTime >= m_fAiTimeOut)
		{
			//. なんとなく前に出す.
			m_fAiTime = 0.0f;
			m_fAiTimeOut = 2.0f + SubSystem.Rand.Float() * 5.0f;
			NewAiTarget();			
		}
		else
		{
			m_fAiTime += fElapsedTime;
		}

		Float3 v3Target = GetAiLocationInWorld();
		Float3 v3Direction = 
			Float3.Sub(Float3.Local(), v3Target, GetPosition(Float3.Local()));

		float fDistance = ((SquadPosition <= 0) ? 1000.0f : 400.0f );

		if (Float3.Length(v3Direction) < fDistance )
		{
			if (Leader != null)
			{
				if (Leader.m_eAi == EAi.Attack)
				{
					_ToAiAttack();
					return EAi.Attack;					
				}
			}
			else
			{
				_ToAiAttack();
				return EAi.Attack;
			}
		}
		else
		{
			v3Direction = Float3.Normalize(v3Direction, v3Direction);

			SetVelocityTarget(Float3.Mul(Float3.Local(), v3Direction, MaxLocalVelocity.Z));
			SetDirectionTarget(v3Direction);
			SetUpTarget(Transform.GetAxisY(Float3.Local()));
		}

		return EAi.Move;
	}

	private void _ToAiAttack()
	{
		//. 攻撃.
		m_fAiTime = 0.0f;
		m_fAiTimeOut = 0.0f;
		m_nAiCount = 16;
		SetVelocityTarget(AttackTarget.GetWorldVelocity());
	}

	EAi _DoAiBoost(float fElapsedTime)
	{
		if (m_fAiTime >= m_fAiTimeOut) 
		{
			//. なんとなく前に出す.
			m_fAiTime = 0.0f;
			m_fAiTimeOut = 2.0f + SubSystem.Rand.Float() * 5.0f;
			NewAiTarget();
		}
		else
		{
			m_fAiTime += fElapsedTime;
		}

		Float3 v3Target = GetAiLocationInWorld();//Float3.Add( Float3.Local(), LocationTarget.GetWorldTransform().GetAxisW( Float3.Local() ), m_v3AiTarget );
		Float3 v3Direction = Float3.SubNormalize(Float3.Local(), v3Target, GetPosition(Float3.Local()));
		SetVelocityTarget(Float3.Mul(Float3.Local(), v3Direction, MaxLocalVelocity.Z));
		SetDirectionTarget(v3Direction);
		SetUpTarget(Transform.GetAxisY(Float3.Local()));

		return EAi.Move;
	}

	EAi _DoAiAttack(float fElapsedTime)
	{
		Fire = false;
		m_fAiTime += fElapsedTime;

		if (m_fAiTime >= m_fAiTimeOut)
		{
			//. なんとなく前に出す.
			m_fAiTime = 0.0f;
			m_fAiTimeOut = 0.25f;
			Fire = true;

			if (m_nAiCount <= 0)
			{
				Fire = false;

				m_fAiTime = 0.0f;
				m_fAiTimeOut = 2.0f + SubSystem.Rand.Float() * 5.0f;
				NewAiTarget();
				return EAi.Move;
			}
			else
			{
				m_nAiCount--;
			}
		}


		Float3 v3Target = AttackTarget.GetWorldTransform().GetAxisW(Float3.Local());
		Float3 v3Direction = Float3.SubNormalize(Float3.Local(), v3Target, GetPosition(Float3.Local()));

		SetDirectionTarget(v3Direction);

		//. Up direction
		SetUpTarget(AttackTarget.GetWorldTransform().GetAxisY(Float3.Local()));
		return EAi.Attack;
	}

	private void  EvaluateLeaderStatus()
	{
		if (Leader != null)
		{
			if (Leader.IsActive() == false)
			{
				Leader = null;
				SquadPosition = 0;
				LocationTarget = AttackTarget;
			}
		}
	}

	private void NewAiTarget()
	{
		if (Leader == null)
		{
			Float3.Rand(m_v3AiTarget, -2000.0f, 2000.0f);
		}
		/*
		 switch( SquadPosition )
		 {
		 default:
		 case 0:
		 Float3.Rand( m_v3AiTarget, -2000.0f, 2000.0f );
		 break;

		 case 1:
		 m_v3AiTarget.Set( -1000.0f, 0.0f, 0.0f );
		 break;

		 case 2:
		 m_v3AiTarget.Set( 1000.0f, 0.0f, 0.0f );
		 break;

		 case 3:
		 m_v3AiTarget.Set( -500.0f, 0.0f, 0.0f );
		 break;

		 case 4:
		 m_v3AiTarget.Set( 500.0f, 0.0f, 0.0f );
		 break;
		 }
		 */
	}

	private Float3 GetAiLocationInWorld()
	{
		if (SquadPosition <= 0)
		{
			return Float3.Add(
				Float3.Local(),
				LocationTarget.GetWorldTransform().GetAxisW(Float3.Local()),
				m_v3AiTarget);
		}
		else
		{
			/*
			 Float3 f3W = Float3.Local( 0.0f );
			 if( Leader != null )
			 {
			 f3W = Leader.GetAiLocationInWorld( );
			 }
			 Float3.Mad( f3W, LocarionTarget.GetWorldTransform().GetAxisX( Float3.Local() ), m_v3AiTarget.X, f3W );
			 Float3.Mad( f3W, AttackTarget.GetWorldTransform().GetAxisY( Float3.Local() ), m_v3AiTarget.Y, f3W );

			 return f3W;
			 */
			return Float4x4.MulXYZ1(Float3.Local(), m_v3AiTarget, LocationTarget.GetWorldTransform());
		}

	}

	final Sphere m_sphere = new Sphere();

	public Sphere GetSphere()
	{
		m_sphere.Set(Transform.GetAxisW(Float3.Local()), 200.0f);
		return m_sphere;
	}
}


