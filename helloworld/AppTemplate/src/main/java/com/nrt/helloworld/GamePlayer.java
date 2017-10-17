package com.nrt.helloworld;

import com.nrt.basic.*;
import com.nrt.framework.RenderContext;
import com.nrt.render.*;
import com.nrt.input.*;
import com.nrt.ui.*;
import com.nrt.font.BitmapFont;
import com.nrt.collision.Sphere;
import com.nrt.math.FMath;
import com.nrt.math.Float3;
import com.nrt.math.Float4;
import com.nrt.math.Float3x3;
import com.nrt.math.Float4x4;

import com.nrt.framework.SubSystem;
import com.nrt.collision.*;


public final class GamePlayer
{
	public final Float4x4 Transform = new Float4x4();
	public final Float3 AngularVelocity = new Float3();
	public final Float3 Velocity = new Float3();

	public final Float4x4 TransformView = Float4x4.Identity();

	public Float3 Forward( Float3 dst )
	{ return Transform.GetAxisZ( dst ); }
	public Float3 Position( Float3 dst )
	{ return Transform.GetAxisW( dst ); }

	public float m_fLaunchTime;
	public boolean m_isLaunch;

	//. ヘディング、ピッチ、ロール 各左右
	//. 	角加速度、加速時フラッター
	//.		最大角速度

	//. 移動 前後上下左右
	//. 	加速度、加速時フラッター
	//.		最大速度、速度回転トリム

	//. シーカー
	//.			誘導ミサイル xN
	//.			自動追尾機銃 x1

	//. 光学センサー
	//.		位置表示
	//.		ダメージ量推定

	//. ハードポイント
	//. 	装備個数

	//. 武器損壊

	//. 武器

	//. アサルトライフル.
	//. ビームライフル.
	//. ショットガン.
	//. グレネードランチャー.
	//. 対空ミサイルランチャー.

	//. 対艦ロケットランチャー.
	//. 対艦ビームランチャー.


	public static float MaxSpeed = 500.0f;

	private UiForm m_form = null;
	public UiTrackBall m_trackBall = null;
//	private Ui.RoundButton m_buttonForward = null;
	private UiDirectionPad m_directionPad = null;

//	private Ui.RoundButton m_buttonSelect = null;
	
	
	

	private GamePlayerUiWeaponButton m_buttonWeapon0 = null;
	private GamePlayerUiWeaponButton m_buttonWeapon1 = null;
	private GamePlayerUiWeaponButton m_buttonWeapon2 = null;
	
	private UiLayout m_layout = null;

	public int m_nbFireRemains = 0;
	public float m_fFireTime = 0.0f;


	public GameShot[] Shots = null;
	public GameMissile[] Missiles = null;
	public GameShot[] Beams = null;



	public Sphere m_sphere = new Sphere(new Float3(0.0f), 50.0f);

	//public final int kWeapon_MachineGun = 0;
	//public final int kWeapon_AirToAirMissile = 1;
	//public final int kWeapon_AntiFleetBeam = 2;
	//public final int kMaxWeapons = 3;

	public enum EWeapon
	{
		MachineGun,
		AirToAirMissile,
		AntiFleetBeam,
		Max
	};
	
	public EWeapon m_eWeapon = EWeapon.MachineGun;

	public float ShieldMax = 100.0f; 
	public float Shield = ShieldMax;

	public final Float3 m_f3ShieldCenter = new Float3(0.0f, 0.0f, 50.0f);
	public final float m_fShieldRadius = 30.0f;
	public final Float3 m_f3Intersection = new Float3(0.0f);
	public final Float3 m_f3IntersectionLocal = new Float3(0.0f);

	public static class DamagePosition
	{
		public final Float3 Position = new Float3(0.0f);
		public float Damage = 0.0f;
		public boolean IsActive = false;
		
		public float Time = 0.0f;
		
		public final float kRadiusStart = 10.0f;
		public final float kRadiusEnd = 20.0f;
		
		public float Radius = 0.0f;
		public float Alpha = 0.0f;
		
		public void Activate( final Float3 f3Position, float fDamage )
		{
			Position.Set( f3Position );
			Damage = fDamage;
			IsActive = true;			
			Time = 0.0f;			
		}
		
		public void Update( float fElapsedTime )
		{
			if( IsActive )
			{
				final float kTimeOut = 3.0f;
				if( Time < kTimeOut )
				{
					Radius = FMath.Lerp( kRadiusStart, kRadiusEnd, Time/kTimeOut );
					Alpha = FMath.Lerp( 1.0f, 0.0f, Time );
					Time += fElapsedTime;
				}
				else
				{
					Radius = kRadiusEnd;
					Alpha = 0.0f;
				}
			}
		}		
	}

	public DamagePosition[] DamagePositions = new DamagePosition[20];

	public GamePlayerWeapon[] m_weaponDescs = new GamePlayerWeapon[EWeapon.Max.ordinal()];

	public float[] m_shieldDirection = new float[32];

	public GamePlayer()
	{
		//Transform = Matrix4.Translation( 0.0f, 0.0f, 2000.0f )*Matrix4.RotationY( FMath.Radians(180.0f) );
		Float4x4.Mul(Transform, Float4x4.RotationY(FMath.ToRad(180.0f)), Float4x4.Translation(0.0f, 0.0f, 2000.0f));
		AngularVelocity.Set( 0.0f );
		Velocity.Set( 0.0f );

		m_fLaunchTime = 0.0f;
		m_isLaunch = false;

		
		float[] arrayShotRadiusKernel =
		{
			0.0f,1.0f,0.5f, 0.0f, //0.25f, 0.25f, 0.25f, 0.0f,
		};

		Shots = new GameShot[20];
		for (int i = 0 ; i < Shots.length ; i++)
		{
			Shots[i] = new GameShot(5.0f, arrayShotRadiusKernel);
			Shots[i].Type = GameShotBase.EType.Physical;
			Shots[i].Power = 4.0f;
			Shots[i].ParticleRadius = 50.0f;
			Shots[i].ColorIn.Set(2.0f, 1.6f, 0.8f, 1.0f); 
			Shots[i].ColorOut.Set(0.5f, 0.0f, 0.0f, 0.0f); 
		}

		Missiles = new GameMissile[16];
		for (int i = 0 ; i < Missiles.length ; i++)
		{
			Missiles[i] = new GameMissile();
			Missiles[i].Type = GameShotBase.EType.Physical;
			Missiles[i].Power = 4.0f;
			Missiles[i].ParticleRadius = 50.0f;
		}

		float[] arrayBeamRadiusKernel =
		{
			0.0f, 1.5f, 1.25f, 1.125f, 1.0f, 1.0f, 1.0f, 1.0f,
			1.0f, 1.0f, 1.0f, 1.0f, 0.75f, 0.5f, 0.25f, 0.0f,
		};

		Beams = new GameShot[4];
		for (int i = 0 ; i < Beams.length ; i++)
		{
			Beams[i] = new GameShot(30.0f, arrayBeamRadiusKernel);
			Beams[i].Type = GameShotBase.EType.Beam;
			Beams[i].Power = 400.0f;
			Beams[i].ParticleRadius = 200.0f;
		}

		m_weaponDescs = new GamePlayerWeapon[]
		{
			new GamePlayerWeapon("GUN", 3, 0.05f, 8000.0f, 400.0f, Shots),
			new GamePlayerWeapon("AAM", 4, 0.15f, 500.f, 0.0f, Missiles),
			new GamePlayerWeapon("AFC", 1, 0.5f, 6000.0f, 0.0f, Beams),
		};

		for (int i = 0 ; i < DamagePositions.length ; i++)
		{
			DamagePositions[i] = new DamagePosition();
		}
		
		
		
		
		m_form = new UiForm();
		float w = 1280.0f;//800.0f;//SubSystem.Render.ScanOutWidth;
		float h = 720.0f;//1280.0f;//SubSystem.Render.ScanOutHeight;

		m_layout = new UiLayout( new Rect( 0, 0, w, h ));
		
		DebugLog.Error.WriteLine(String.format("%dx%d", (int) w, (int) h));

		m_form.Add((m_trackBall = new UiTrackBall(170, h - 170, 150)));
		m_layout.Add( m_trackBall,
			UiLayout.Item.EOrigin.BottomLeft, UiLayout.Item.EFlow.VerticalFirst,
			UiLayout.Item.EFill.None, UiLayout.Item.ESize.Pixel );
		//m_form.Add( (m_buttonForward = new Ui.RoundButton( w - 110, h - 110, 100 ) ) );
		m_form.Add((m_directionPad = new UiDirectionPad(w - 170, h - 170, 150)));
		m_layout.Add( m_directionPad,
			UiLayout.Item.EOrigin.BottomRight, UiLayout.Item.EFlow.VerticalFirst,
			UiLayout.Item.EFill.None, UiLayout.Item.ESize.Pixel );
		//m_form.Add((m_buttonSelect = new Ui.RoundButton(w - 110, h - 110 - 60, 50)));
		//m_buttonSelect.EnableEnter = false;

		m_form.Add((m_buttonWeapon0 = new GamePlayerUiWeaponButton(
					   new Rect(w - 110, h - 110 - 160 - 100, 100, 50),
					   m_weaponDescs[EWeapon.MachineGun.ordinal()])));
		m_layout.Add( m_buttonWeapon0,
					 UiLayout.Item.EOrigin.BottomRight, UiLayout.Item.EFlow.VerticalFirst,
					 UiLayout.Item.EFill.None, UiLayout.Item.ESize.Pixel );
	
		m_form.Add((m_buttonWeapon1 = new GamePlayerUiWeaponButton(
					   new Rect(w - 110, h - 110 - 160 - 110 - 60, 100, 50),
					   m_weaponDescs[EWeapon.AirToAirMissile.ordinal()])));
		m_layout.Add( m_buttonWeapon1,
					 UiLayout.Item.EOrigin.BottomRight, UiLayout.Item.EFlow.VerticalFirst,
					 UiLayout.Item.EFill.None, UiLayout.Item.ESize.Pixel );
		
		m_form.Add((m_buttonWeapon2 = new GamePlayerUiWeaponButton(
					   new Rect(w - 110, h - 110 - 160 - 110 - 60 * 2, 100, 50),
					   m_weaponDescs[EWeapon.AntiFleetBeam.ordinal()] )));
		m_layout.Add( m_buttonWeapon2,
					 UiLayout.Item.EOrigin.BottomRight, UiLayout.Item.EFlow.VerticalFirst,
					 UiLayout.Item.EFill.None, UiLayout.Item.ESize.Pixel );
	}

	public void OnSurfaceChanged(int w, int h)
	{
		m_layout.Resize( new Rect( 0, 0, w, h ) );
	}

	public void Update(float fElapsedTime)
	{

		m_form.Update(SubSystem.FramePointer, fElapsedTime);

		//Transform = Float4x4.Mul( Transform, Float4x4.Rotation( Quaternion.Mul( m_trackBall.AngularVelocity, fElapsedTime ) ) );

		Float3x3 m3 = Float3x3.Local().Set(Transform);

		//m3 = Float3x3.Mul( Float3x3.Rotation( Quaternion.Mul( m_trackBall.AngularVelocity, fElapsedTime ) ), m3 );
		//m3 = Float3x3.Rotation( m_trackBall.AngularVelocity );
		//m3.Dump();
		float fFrameTime = 1.0f / 60.0f;
		float fLerp = FMath.Pow(0.95f, fElapsedTime / fFrameTime); 
		Float3.Lerp(AngularVelocity, fLerp, Float3.Mul( Float3.Local(), Float3.Local().Set(1.0f, -1.0f, 1.0f), m_trackBall.EulerAngularVelocity), AngularVelocity);
		Float3 f3AngularVelocity = Float3.Clamp( Float3.Local(), AngularVelocity, Float3.Local(-FMath.PI * 0.25f), Float3.Local(FMath.PI * 0.25f));

		Float3 rot = Float3.Mul( Float3.Local(), f3AngularVelocity, fElapsedTime * 2.0f);// m3.GetEulerRotationXYZ();
		//fElapsedTime = 1.0f;
		m3 = Float3x3.Mul( Float3x3.Local(), Float3x3.RotationY( Float3x3.Local(), rot.Y), Float3x3.RotationX( Float3x3.Local(), rot.X));
		m3 = Float3x3.Mul( Float3x3.Local(), Float3x3.RotationZ( Float3x3.Local(), rot.Z), m3);
		//m3 = Float3x3.RotationZYX( rot );
		m3 = Float3x3.Mul( Float3x3.Local(), m3, Float3x3.Local().Set( Transform ));
		/*
		 Float4x4 matrix = new Float4x4(
		 Transform.X(),
		 Transform.Y(),
		 Transform.Z(), new Float4( 0.0f, 0.0f, 0.0f, 1.0f ) );

		 matrix = Float4x4.Mul( matrix, Float4x4.Rotation( Quaternion.Mul( m_trackBall.AngularVelocity, fElapsedTime ) ) );
		 */
		Transform.Set( m3, Transform.GetAxisW( Float3.Local() ) );

		float fVX = Float3.Dot(Velocity, Transform.GetAxisX( Float3.Local() ));
		float fVY = Float3.Dot(Velocity, Transform.GetAxisY(Float3.Local()));
		float fVZ = Float3.Dot(Velocity, Transform.GetAxisZ(Float3.Local()));

		Float3 f3Position = Transform.GetAxisW( Float3.Local() );

		//if( pad.IsDown( GamePadButtons.Cross ) && pad.IsDown( GamePadButtons.Circle ) )
		/*if (m_directionPad.Hold)
		 {
		 //. ストップ.
		 fVX = BreakControl(fVX, 10.0f * 60.0f * fElapsedTime);
		 fVY = BreakControl(fVY, 10.0f * 60.0f * fElapsedTime);
		 fVZ = BreakControl(fVZ, 10.0f * 60.0f * fElapsedTime);
		 }
		 //else if( m_buttonForward.IsDown() )
		 else*/ //if (m_directionPad.Slide && m_directionPad.Direction.Y < 0.0f)
		if (m_directionPad.Hold || (m_directionPad.Slide && m_directionPad.Direction.Y < 0.0f))
		{
			//. 前進.
			fVZ += 15.0f * 60.0f * fElapsedTime;

			fVX = BreakControl(fVX, 5.0f * 60.0f * fElapsedTime);
			fVY = BreakControl(fVY, 5.0f * 60.0f * fElapsedTime);
		}
		//else if( pad.IsDown( GamePadButtons.Cross ) )

		else if (m_directionPad.Slide && m_directionPad.Direction.Y > 0.0f)
		{
			//. 後退.
			fVZ -= 10.0f * 60.0f * fElapsedTime;

			fVX = BreakControl(fVX, 5.0f * 60.0f * fElapsedTime);
			fVY = BreakControl(fVY, 5.0f * 60.0f * fElapsedTime);
		}

		else if (m_directionPad.ReleaseFlick || m_directionPad.LeaveFlick)
		{
			//. 後退.
			//fVZ -= 10.0f * 60.0f * fElapsedTime;

			//fVX = BreakControl(fVX, 5.0f * 60.0f * fElapsedTime);
			//fVY = BreakControl(fVY, 5.0f * 60.0f * fElapsedTime);
			fVX -= m_directionPad.Direction.X * 1000.0f;
			fVY -= m_directionPad.Direction.Y * 1000.0f;
		}
		else
		{
			fVX = BreakControl(fVX, 0.05f * 60.0f * fElapsedTime);
			fVY = BreakControl(fVY, 0.05f * 60.0f * fElapsedTime);
			fVZ = BreakControl(fVZ, 0.05f * 60.0f * fElapsedTime);
		}

		Float3x3.Mul(Velocity, new Float3(fVX, fVY, fVZ), new Float3x3(Transform));

		if (Float3.Length(Velocity) > MaxSpeed)
		{
			Float3.Mul(Velocity, Float3.Normalize( Float3.Local(), Velocity), MaxSpeed);
		}

		Transform.SetAxisW(Float3.Mad( Float3.Local(), Velocity, fElapsedTime, Transform.GetAxisW( Float3.Local())));

		Transform.GetAxisW(m_sphere.Position);

		/*
		 if( m_buttonSelect.IsPush() && m_nbFireRemains <= 0 )
		 {
		 m_iWeapon = (m_iWeapon + 1)%kMaxWeapons;
		 }
		 */
		if (m_buttonWeapon0.IsPush())
		{
			m_nbFireRemains = 0;
			m_eWeapon = EWeapon.MachineGun;
		}
		if (m_buttonWeapon1.IsPush())
		{
			m_nbFireRemains = 0;
			m_eWeapon = EWeapon.AirToAirMissile;
		}
		if (m_buttonWeapon2.IsPush())
		{
			m_nbFireRemains = 0;
			m_eWeapon = EWeapon.AntiFleetBeam;
		}

		GamePlayerWeapon weapon = m_weaponDescs[m_eWeapon.ordinal()];

		if (m_directionPad.Tap)
		{	
			m_nbFireRemains = weapon.Bursts;
		}

		if (m_nbFireRemains > 0)
		{	
			if (weapon.Spawn(Transform,Velocity))
			{
				m_nbFireRemains--;
			}
		}

		for (GamePlayerWeapon itWeapon : m_weaponDescs)
		{
			itWeapon.Update(fElapsedTime);
		}

		for (GameShot shot : Shots)
		{
			shot.Update(fElapsedTime);
		}

		for (GameMissile missile : Missiles)
		{
			missile.Update(fElapsedTime);
		}

		for (GameShot shot : Beams)
		{
			shot.Update(fElapsedTime);
		}
		
		for( DamagePosition damagePosition : DamagePositions )
		{
			damagePosition.Update( fElapsedTime );
		}

		/*
		if (m_directionPad.ReleaseFlick || m_directionPad.LeaveFlick)
		{
			DebugLog.Error.WriteLine(String.format("Flick %f %f",
												 m_directionPad.Direction.X,
												 m_directionPad.Direction.Y));
		}
		*/
		
		/*
		 if( Shield < ShieldMax )
		 {
		 Shield += 100.0f*fElapsedTime;
		 }

		 if( ShieldMax < Shield )
		 {
		 Shield = ShieldMax;
		 }
		 */
		/*
		 Pad pad = InterfaceList.Pad;

		 AngularVelocity.X = AngularControl( AngularVelocity.X, GamePadButtons.Up,   GamePadButtons.Down,  FMath.Radians( 8.0f )*60.0f*fElapsedTime, FMath.Radians( 8.0f )*60.0f*fElapsedTime );
		 AngularVelocity.Y = AngularControl( AngularVelocity.Y, GamePadButtons.Left, GamePadButtons.Right, FMath.Radians( 8.0f )*60.0f*fElapsedTime, FMath.Radians( 8.0f )*60.0f*fElapsedTime );
		 AngularVelocity.Z = AngularControl( AngularVelocity.Z, GamePadButtons.R,    GamePadButtons.L,     FMath.Radians( 8.0f )*60.0f*fElapsedTime, FMath.Radians( 8.0f )*60.0f*fElapsedTime );

		 AngularVelocity = Vector3.Max ( AngularVelocity, FMath.Radians( -180.0f ) );
		 AngularVelocity = Vector3.Min ( AngularVelocity, FMath.Radians(  180.0f ) );

		 float fVX = Vector3.Dot( Velocity, Transform.AxisX );
		 float fVY = Vector3.Dot( Velocity, Transform.AxisY );
		 float fVZ = Vector3.Dot( Velocity, Transform.AxisZ );

		 if( pad.IsDown( GamePadButtons.Cross ) && pad.IsDown( GamePadButtons.Circle ) )
		 {
		 //. ストップ.
		 fVX = BreakControl( fVX, 10.0f*60.0f*fElapsedTime );
		 fVY = BreakControl( fVY, 10.0f*60.0f*fElapsedTime );
		 fVZ = BreakControl( fVZ, 10.0f*60.0f*fElapsedTime );
		 }
		 else if( pad.IsDown( GamePadButtons.Circle ) )
		 {
		 //. 前進.
		 fVZ += 10.0f*60.0f*fElapsedTime;

		 fVX = BreakControl( fVX, 5.0f*60.0f*fElapsedTime );
		 fVY = BreakControl( fVY, 5.0f*60.0f*fElapsedTime );
		 }
		 else if( pad.IsDown( GamePadButtons.Cross ) )
		 {
		 //. 後退.
		 fVZ -= 10.0f*60.0f*fElapsedTime;

		 fVX = BreakControl( fVX, 5.0f*60.0f*fElapsedTime );
		 fVY = BreakControl( fVY, 5.0f*60.0f*fElapsedTime );
		 }
		 else
		 {
		 fVX = BreakControl( fVX, 1.0f*60.0f*fElapsedTime );
		 fVY = BreakControl( fVY, 1.0f*60.0f*fElapsedTime );
		 fVZ = BreakControl( fVZ, 1.0f*60.0f*fElapsedTime );
		 }

		 Velocity = Transform.AxisX*fVX + Transform.AxisY*fVY + Transform.AxisZ*fVZ;

		 if( Vector3.Length( Velocity ) > MaxSpeed )
		 {
		 Velocity = Vector3.Normalize( Velocity )*MaxSpeed;
		 }


		 Matrix4 matrixAngularVelocity
		 = Matrix4.RotationAxis( Transform.AxisX, AngularVelocity.X*fElapsedTime )
		 * Matrix4.RotationAxis( Transform.AxisY, AngularVelocity.Y*fElapsedTime )
		 * Matrix4.RotationAxis( Transform.AxisZ, AngularVelocity.Z*fElapsedTime );

		 Matrix4 matrixRotation = new Matrix4( Transform.ColumnX, Transform.ColumnY, Transform.ColumnZ, new Vector4( 0.0f, 0.0f, 0.0f, 1.0f ) );

		 matrixRotation = matrixAngularVelocity*matrixRotation;

		 Transform = new Matrix4( matrixRotation.ColumnX, matrixRotation.ColumnY, matrixRotation.ColumnZ, Transform.ColumnW + new Vector4( Velocity*fElapsedTime, 0.0f ) );

		 m_isLaunch = false;
		 if( pad.IsDown( GamePadButtons.Triangle ) && m_fLaunchTime <= 0.0f )
		 {
		 m_isLaunch = true;
		 m_fLaunchTime = 0.1f;
		 }
		 else if( m_fLaunchTime > 0.0f )
		 {
		 m_fLaunchTime -= fElapsedTime;
		 }
		 */

		{
			fLerp = FMath.Pow(0.80f, fElapsedTime / fFrameTime); 

			Float3 v3VX = TransformView.GetAxisX(Float3.Local());
			Float3 v3VY = TransformView.GetAxisY(Float3.Local());
			Float3 v3VZ = TransformView.GetAxisZ(Float3.Local());

			Float3 v3TX = Transform.GetAxisX(Float3.Local());
			Float3 v3TY = Transform.GetAxisY(Float3.Local());
			Float3 v3TZ = Transform.GetAxisZ(Float3.Local());

			Float3.Lerp(v3VX, fLerp, v3TX, v3VX);
			Float3.Lerp(v3VY, fLerp, v3TY, v3VY);
			Float3.Lerp(v3VZ, fLerp, v3TZ, v3VZ);

			Float3.Normalize(v3VZ, v3VZ);
			Float3.Normalize(v3VY, v3VY);

			Float3.CrossNormalize(v3VX, v3VY, v3VZ);
			Float3.CrossNormalize(v3VY, v3VZ, v3VX);

			TransformView.SetAxisX(v3VX);
			TransformView.SetAxisY(v3VY);
			TransformView.SetAxisZ(v3VZ);
			TransformView.SetAxisW(Transform.GetAxisW(Float3.Local()));
		}
	}


	public void PostUpdate(float fElapsedTime, GameViewPoint viewPoint)
	{
		for (FramePointer.Pointer p : SubSystem.FramePointer.Pointers)
		{
			if (p.Down == false)
			{
				continue;
			}
			Float3 f3Touch = Float3.Local();
			viewPoint.ProjectHudScreenFromTouchPoint(f3Touch, p.Position);


			//br.Arc( f3.X, f3.Y, 64.0f, 8 );

			for (DamagePosition dp : DamagePositions)
			{
				if (dp.IsActive == false)
				{
					continue;
				}

				Float3 f3Damage = Float4x4.Mul(Float3.Local(), Float4.Local(dp.Position, 1.0f), Transform);
				viewPoint.ProjectHudScreen(f3Damage, f3Damage);

				if (Float3.Distance(f3Touch, f3Damage) < 64.0f)
				{
					Shield += dp.Damage;
					dp.IsActive = false;
				}

			}
		}

	}
	
	public final void RenderHud3D(final RenderContext rc, final BitmapFont bf, final FrameBuffer frameBuffer, final GameViewPoint viewPoint, final GameStarShip[] starShips, final GameEnemy[] enemies)
	{
		final BasicRender br = rc.GetBasicRender();
		final GfxCommandContext r = rc.GetCommandContext();

		bf.SetColor( RgbaColor.FromRgba( 0xffffffff ));

		br.SetColor(1.0f, 0.0f, 0.0f, 1.0f);

		//GLES20.glEnable( GLES20.GL_BLEND );
		//GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE );
		BlendState blendSrcAlphaOne = new BlendState(true, BlendFunc.SrcAlpha, BlendFunc.One);
		r.SetBlendState( blendSrcAlphaOne );
		
		for (DamagePosition dp : DamagePositions)
		{
			if (dp.IsActive)
			{
				Float3 f3Z = Float3.Normalize( Float3.Local(), dp.Position );
				Float3 f3Y = Float3.Local( 0.0f, 1.0f, 0.0f );
				Float3 f3X = Float3.CrossNormalize( Float3.Local(), f3Y, f3Z);
				Float3.CrossNormalize( f3Y, f3Z, f3X );

				Float4x4 matrixWorld = Float4x4.Mul( Float4x4.Local(), Float4x4.Local( f3X, f3Y, f3Z, dp.Position ), Transform );
				br.GetMatrixCache().SetWorld( matrixWorld );
				float fRadius = 10.0f;
				br.Arc(0.0f, 0.0f, fRadius, 0xffff00ff, fRadius-5.0f, 0xff000000, 6); 
				
				if( 0 < dp.Alpha )
				{
					int alpha = (int)(dp.Alpha*255.0f);
					br.Arc(0.0f, 0.0f, dp.Radius, 0xffffff00 | alpha, dp.Radius - 5.0f, 0xff000000, 6 );
				}
			}
		}
		/*
		float d = 200.0f;
		float r = FMath.Sin( FMath.ToRad( 5.0f ) )*d;
		br.GetMatrixCache().SetWorld( TransformView );
		br.SetColor( 0x00ff00ff );
		br.Begin( GLES20.GL_LINE_STRIP, BasicRender.EShader.Color );
		for( int i = 0 ; i <= 6 ; i++ )
		{
			float rad0 = ((float)i/6.0f)*FMath.RD;
			//float rad1 = (float)(i+1)/6.0f;
			float c = FMath.Cos( rad0 );
			float s = FMath.Sin( rad0 );
			br.SetVertex( c*r, s*r, d );
			
		}
		br.End();
		*/
		
		/*
		 MatrixCache mc = br.GetMatrixCache();
		 mc.SetProjection( viewPoint.Projection );
		 mc.SetView( viewPoint.View );
		 mc.SetWorld( Float4x4.Mul( Float4x4.Local(), Float4x4.Translation( Float4x4.Local(), m_f3ShieldCenter ), Transform ) );
		 mc.Update();
		 br.SetColor( 0.0f, 1.0f, 0.0f, 1.0f );
		 */
		 
		 //GLES20.glDisable( GLES20.GL_BLEND );
		BlendState blendDisable = new BlendState( false, BlendFunc.One, BlendFunc.Zero );
		r.SetBlendState( blendDisable );
	}
	

	public final void RenderHud( final RenderContext rc,  final BitmapFont bf, final FrameBuffer frameBuffer, final GameViewPoint viewPoint, final GameStarShip[] starShips, final GameEnemy[] enemies)
	{
		final BasicRender br = rc.GetBasicRender();
		final GfxCommandContext r = rc.GetCommandContext();

		br.Begin(EPrimitive.Lines, BasicRender.EShader.Color);
		float fIn = 16.0f;
		float fOut = 64.0f;

		Float3 f3Center = Float3.Local();
		Float3.Mad(f3Center, Transform.GetAxisZ(Float3.Local()), 1000.0f, Transform.GetAxisW(Float3.Local()));

		viewPoint.ProjectHudScreen(f3Center, f3Center);
		float x = f3Center.X;
		float y = f3Center.Y;

		br.SetColor(0.3f, 0.95f, 0.6f, 1.0f);
		br.SetVertex(fIn + x, y, 0.0f);
		br.SetVertex(fOut + x, y, 0.0f);
		br.SetVertex(-fIn + x, y, 0.0f);
		br.SetVertex(-fOut + x, y, 0.0f);
		br.SetVertex(x, fIn + y, 0.0f);
		br.SetVertex(x, fOut + y, 0.0f);
		br.SetVertex(x, -fIn + y, 0.0f);
		br.SetVertex(x, -fOut + y, 0.0f);

		/*
		 float fReload = m_weaponDescs[m_iWeapon].GetReloadRate();
		 if (fReload >= 1.0f)
		 {
		 br.SetColor(0.3f, 0.95f, 0.6f, 1.0f);
		 }
		 else
		 {
		 br.SetColor(0.95f, 0.3f, 0.15f, 1.0f);			
		 }
		 br.SetVertex(x + 50.0f, y + 50.0f, 0.0f);
		 br.SetVertex(x + 50.0f + fReload * 100.0f, y + 50.0f, 0.0f);
		 */
		br.End();

		
		br.SetColor(0.3f, 0.95f, 0.6f, 1.0f);
		//br.Arc(x, y, 240.0f, 32);
		//br.Arc(x, y, 240.0f + 32.0f, 32);


		//GLES20.glDisable( GLES20.GL_CULL );
		
		br.SetColor(0.3f, 0.95f, 0.6f, 1.0f);

		bf.SetColor( RgbaColor.FromRgba( 0xffff00ff ));
		bf.SetSize( 8.0f * viewPoint.HudHeight/frameBuffer.Height);
		
		//GLES20.glEnable(GLES20.GL_BLEND);
		//GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
		BlendState blendSrcAlphaOne = new BlendState(true,BlendFunc.SrcAlpha,BlendFunc.One);
		r.SetBlendState( blendSrcAlphaOne );

		for (GameStarShip ship : starShips)
		{
			if (ship.IsBusy())
			{
				ship.Transform.GetAxisW(f3Center);
				viewPoint.ProjectHudScreen(f3Center, f3Center);

				if (-1.0f < f3Center.Z && f3Center.Z < 1.0f)
				{
					//br.Arc(f3Center.X, f3Center.Y, 32.0f, 6);
					r.SetBlendState( blendSrcAlphaOne );
					br.Arc(f3Center.X, f3Center.Y, 16.0f, 0x00000000, 32.0f, 0x00ff0080, 6);

					bf.Begin();
					bf.Draw((int) (f3Center.X - 32.0f), (int)(f3Center.Y + 32.0f), 0.0f, "FLEET");
					bf.End();
				}
			}
		}

		for (GameEnemy enemy : enemies)
		{
			if (enemy.IsBusy())
			{
				enemy.Transform.GetAxisW(f3Center);
				viewPoint.ProjectHudScreen(f3Center, f3Center);

				if (-1.0f < f3Center.Z && f3Center.Z < 1.0f)
				{
					//br.Arc(f3Center.X, f3Center.Y, 32.0f, 4); 
					r.SetBlendState( blendSrcAlphaOne );
					br.Arc(f3Center.X, f3Center.Y, 16.0f, 0x00000000, 32.0f, 0x00ff0080, 4 );

					bf.Begin();
					bf.Draw((int)(f3Center.X - 32.0f), (int)(f3Center.Y + 32.0f), 0.0f, enemy.HudName);
					bf.End();
				}

			}
		}

		//GLES20.glDisable(GLES20.GL_BLEND);
		BlendState blendDisable = new BlendState( false, BlendFunc.One, BlendFunc.Zero );
		r.SetBlendState(blendDisable);
		

		//. Shield.
		{

			viewPoint.ProjectHudScreen(f3Center, f3Center);

			float fShieldOut = 240.0f;//m_fShieldRadius;
			float fShieldIn = fShieldOut * 0.85f;
			float fShieldRemains = fShieldIn + (fShieldOut - fShieldIn) * (Shield / ShieldMax);

			//GLES20.glEnable(GLES20.GL_BLEND);
			//GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);

			r.SetBlendState( blendSrcAlphaOne );

			br.Begin( EPrimitive.TriangleStrip, BasicRender.EShader.Color);
			for (int i = 0 ; i <= 32 ; i++)
			{
				float rad = FMath.RD * ((float)i) / 32.0f;
				float c = FMath.Cos(rad);
				float s = FMath.Sin(rad);
				br.SetColor(0.1f, 0.85f, 1.0f, 0.0f);
				br.SetVertex(x - c * fShieldIn, y + s * fShieldIn, 0.0f);
				br.SetColor(0.1f, 0.85f, 1.0f, 0.5f);
				br.SetVertex(x - c * fShieldRemains, y + s * fShieldRemains, 0.0f);
			}
			br.End();

			//GLES20.glDisable(GLES20.GL_BLEND);
			r.SetBlendState( blendDisable );

			br.SetColor(0.0f, 1.0f, 0.0f, 1.0f);
			br.Arc(x, y, fShieldOut, 32);
			br.Arc(x, y, fShieldIn, 32);
		}

		//. Pointers.
		//GLES20.glEnable( GLES20.GL_BLEND );
		//GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE );
		r.SetBlendState( blendSrcAlphaOne );

		br.SetColor(0.0f, 1.0f, 0.0f, 1.0f);
		for (FramePointer.Pointer p : SubSystem.FramePointer.Pointers)
		{
			Float3 f3 = Float3.Local();
			viewPoint.ProjectHudScreenFromTouchPoint(f3, p.Position);
			if (p.Down)
			{
				float rad = FMath.Fraction( p.Time/1.0f )*FMath.PI;
				float a = -FMath.Cos( rad )*0.5f + 0.5f;
				//br.SetColor(0.0f, 1.0f, 0.0f, a);
				br.Arc(f3.X, f3.Y, 0.0f, 0x00000000, 64.0f, 0x008800ff, 16);
				br.Arc(f3.X, f3.Y, 64.0f*a - 8.0f, 0x00000000, 64.0f*a, 0x008800ff, 16);
			}

		}
		//GLES20.glDisable(GLES20.GL_BLEND);
		r.SetBlendState( blendDisable );

		//. frame.
		br.SetColor(0.0f, 1.0f, 0.0f, 1.0f);
		{			
			float w45 = viewPoint.HudBaseWidth * 0.45f;
			float w40 = viewPoint.HudBaseWidth * 0.40f;
			float h45 = viewPoint.HudBaseHeight * 0.45f;
			float h40 = viewPoint.HudBaseHeight * 0.40f;
			br.Begin(EPrimitive.LineStrip, BasicRender.EShader.Color);
			br.SetVertex( x - w45, y - h40, 0.0f);
			br.SetVertex( x - w40, y - h45, 0.0f);
			br.SetVertex( x + w40, y - h45, 0.0f);
			br.SetVertex( x + w45, y - h40, 0.0f);
			br.End();

			br.Begin(EPrimitive.LineStrip, BasicRender.EShader.Color);
			br.SetVertex( x - w45, y + h40, 0.0f);
			br.SetVertex( x - w40, y + h45, 0.0f);
			br.SetVertex( x + w40, y + h45, 0.0f);
			br.SetVertex( x + w45, y + h40, 0.0f);
			br.End();
		}


		/*
		 for( int i = 0 ; i < DamagePositions.length ; i++ )
		 {
		 GamePlayer.DamagePosition dp = DamagePositions[i];

		 if( dp.IsActive )
		 {
		 mc.SetWorld( Float4x4.Mul( Float4x4.Local(), Float4x4.Translation( Float4x4.Local(), dp.Position ), Transform ) );
		 mc.Update();
		 br.SetColor( 1.0f, 0.0f, 0.0f, 1.0f );
		 br.Arc( 0.0f, 0.0f, 5.0f, 8 );				
		 }
		 }
		 */
		/*
		 mc.SetView(Float4x4.Identity());
		 mc.SetProjection( viewPoint.HudProjection );
		 mc.SetWorld(Float4x4.Identity());
		 mc.Update();
		 */

	}


	public final void RenderControls(final BasicRender br, final BitmapFont bf)
	{
		UpdateWeaponButton(m_buttonWeapon0,m_weaponDescs[EWeapon.MachineGun.ordinal()], ( (m_eWeapon == EWeapon.MachineGun) ? true : false ) );
		UpdateWeaponButton(m_buttonWeapon1,m_weaponDescs[EWeapon.AirToAirMissile.ordinal()], ( (m_eWeapon == EWeapon.AirToAirMissile) ? true : false ) );
		UpdateWeaponButton(m_buttonWeapon2,m_weaponDescs[EWeapon.AntiFleetBeam.ordinal()], ( (m_eWeapon == EWeapon.AntiFleetBeam) ? true : false ) );

		m_form.Render(br,bf);
		m_layout.Render(br);
	}

	Rect m_rect = new Rect();
	

	public void UpdateWeaponButton( GamePlayerUiWeaponButton button, GamePlayerWeapon weapon, boolean isActive )
	{
		//button.Reload = weapon.GetReloadRate();
		button.IsActive = isActive;
		/*
		m_rect.Set(button.Enter);
		
		GLES20.glEnable( GLES20.GL_BLEND );
		GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA );
		br.SetColor( 0.0f, 0.0f, 0.2f, 0.5f );
		br.FillRectangle( m_rect );
		
		//GLES20
		
		
		Float4 f4Red = Float4.Local().Set( 0.95f, 0.3f, 0.15f, 1.0f );
		Float4 f4Green   = Float4.Local().Set( 0.3f, 0.95f, 0.6f, 1.0f );
		
		if( !isActive )
		{
			Float4.Mul( f4Green, f4Green, 0.5f );
			Float4.Mul( f4Red, f4Red, 0.5f );
			f4Green.W = f4Red.W = 1.0f;
		}
		
		int nGreen = RgbaColor.FromRgba( f4Green );
		int nRed = RgbaColor.FromRgba( f4Red );
		
		//hf.SetSize(8.0f);
		bf.SetColor( nGreen );

		bf.Begin();
		bf.Draw(m_rect.X + 3.0f, FMath.Floor(m_rect.Y + (m_rect.Height * 0.25f) - 4.0f), 0.0f, weapon.Name);
		bf.End();


		float fReload = weapon.GetReloadRate();
		if (fReload >= 1.0f)
		{
			br.SetColor( nGreen );//0.3f, 0.95f, 0.6f, 1.0f);
		}
		else
		{
			br.SetColor( nRed );//0.95f, 0.3f, 0.15f, 1.0f);			
		}

		

		float w = m_rect.Width;
		float h = m_rect.Height;

		m_rect.Y += 2.0f + h * 0.5f;
		m_rect.Height *= 0.5f;
		m_rect.Height -= 4.0f;
		m_rect.X += 2.0f;
		m_rect.Width = (w - 4.0f) * fReload;

		br.FillRectangle(m_rect);
		*/
	}

	float BreakControl(float fTarget, float fBreak)
	{
		if (fTarget > 0.0f)
		{
			fTarget -= fBreak;

			if (fTarget < 0.0f)
			{
				fTarget = 0.0f;
			}
		}
		else if (fTarget < 0.0f)
		{
			fTarget += fBreak;

			if (fTarget > 0.0f)
			{
				fTarget = 0.0f;
			}
		}

		return fTarget;
	}

	public Sphere GetSphere()
	{
		return m_sphere;
	}
	/*
	 float AngularControl( float fTarget, GamePadButtons eButtonUp, GamePadButtons eButtonDown, float fAccel, float fBreak )
	 {
	 Pad pad = InterfaceList.Pad;

	 if( pad.IsDown( eButtonUp ) )
	 {
	 fTarget += fAccel;
	 }
	 else if( pad.IsDown( eButtonDown ) )
	 {
	 fTarget -= fAccel;
	 }
	 else
	 {
	 fTarget = BreakControl( fTarget, fBreak );
	 }

	 return fTarget;
	 }
	 */
	final Edge m_edge = new Edge();
	final Intersection m_intersection = new Intersection();

	public boolean AddDamage(Float3 f3Start, Float3 f3End, float fPower)
	{
		Float3 f3Normal = Transform.GetAxisZ( Float3.Local() );
		Float3 f3Center = Transform.GetAxisW( Float3.Local() );

		final float fShieldRadius = 100.0f;//m_f3Shiel
		final float fShieldAngle = FMath.ToRad(30.0f);

		Float3 f3Intersection = Float3.Local();
		if( Intersect( f3Start, f3End, f3Center, fShieldRadius, f3Intersection) )
		{
			float c = FMath.Cos( fShieldAngle );
			if( c < Float3.Dot( f3Normal, Float3.SubNormalize( Float3.Local(), f3Intersection, f3Center ) )  )
			{
				Float4x4 matrixInverse = Float4x4.Invert(Float4x4.Local(), Transform);

				Float3 f3IntersectionLocal = Float4x4.MulXYZ1
				(
					Float3.Local(), 
					f3Intersection, 
					matrixInverse
				);
				
				//float l = Float3.Length( f3IntersectionLocal );
				float l = Float3.Distance( f3Intersection, f3Center);
				
				//SubSystem.Log.WriteLine( String.format("distance %f",l));

				//Shield -= 50.0f;

				AddDamagePosition(f3IntersectionLocal, fPower);
				
				return true;
			}
			
		}
		
		return false;
	}

	//. intersection to sphere.
	private boolean Intersect
	(
		Float3 f3Start, Float3 f3End,
		Float3 f3Center, float fRadius,
		Float3 f3Intersection 
	)
	{
		Float3 f3EdgeDirection = Float3.Sub( Float3.Local(), f3End, f3Start );
		float fEdgeLength = Float3.Length( f3EdgeDirection );
		if( fEdgeLength <= 0.0f )
		{
			return false;
		}
		
		Float3.Mul( f3EdgeDirection, f3EdgeDirection, 1.0f/fEdgeLength );
		
		Float3 f3StartToCenter = Float3.Sub( Float3.Local(), f3Center, f3Start );
		
		float fNearestDot = Float3.Dot( f3StartToCenter, f3EdgeDirection );
		if( fNearestDot < 0.0f )
		{
			return false;
		}
		
		Float3 f3Nearest = Float3.Mad( Float3.Local(), f3EdgeDirection, fNearestDot, f3Start );
		
		float fNearestToCenter = Float3.Distance( f3Nearest, f3Center );
		
		if( fRadius < fNearestToCenter )
		{
			return false;			
		}
		
		float fNearestToSphere = FMath.Sqrt( fRadius*fRadius - fNearestToCenter*fNearestToCenter );
		
		Float3.Mad( f3Intersection, f3EdgeDirection, -fNearestToSphere, f3Nearest );
		
		Float3 f3StartToIntersection = Float3.Sub( Float3.Local(), f3Intersection, f3Start );
		
		float fDot = Float3.Dot( f3StartToIntersection, f3EdgeDirection );
		if( 0.0f <= fDot && fDot <= fEdgeLength )
		{
			return true;
		}
		
		return false;
	}
	
	private void AddDamagePosition(Float3 f3LocalPosition, float fPower)
	{
		for (int i = 0 ; i < DamagePositions.length ; i++)
		{
			DamagePosition dp = DamagePositions[i];
			if (dp.IsActive == false)
			{
				dp.Activate(f3LocalPosition,fPower);
				//dp.IsActive = true;
				float fDamage = fPower;//ShieldMax/((float)DamagePositions.length);
				//DebugLog.Error.WriteLine(String.format("shield %f/%f %f %d", Shield, ShieldMax, fDamage, DamagePositions.length));
				//dp.Damage = fDamage;
				Shield -= fDamage;

				break;
			}
		}

		if (Shield < 0.0f)
		{
			Shield = ShieldMax;
			for (DamagePosition dp : DamagePositions)
			{
				dp.Damage = 0.0f;
				dp.IsActive = false;
			}
		}
		return;
	}
}


