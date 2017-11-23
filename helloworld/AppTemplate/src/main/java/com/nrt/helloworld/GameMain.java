package com.nrt.helloworld;

import java.io.InputStream;

import com.nrt.basic.*;
import com.nrt.math.FMath;
import com.nrt.math.Float3;
import com.nrt.math.Float4x4;
import com.nrt.anim.*;
import com.nrt.render.*;
import com.nrt.model.*;
import com.nrt.collision.*;
import com.nrt.ui.*;
import com.nrt.framework.*;
import com.nrt.clipper.*;
import com.nrt.font.*;

import com.nrt.framework.AppFrame;

public class GameMain implements AppFrame
{
	public GamePlayer m_player = null;
	public GameStarShip[] m_starShips = null;
	public GameViewPoint m_viewPoint = null;
	public GameParticle m_particle = null;	

	public LightTailRender m_lightTailRender = null;
	public StarField m_starField = null;

	public Model m_modelStarShip = null;
	public CollisionMesh m_collisionStarShip = null;

	public Model m_modelEnemy0 = null;
	public Model m_modelEnemy1 = null;
	public Model m_modelEnemy2 = null;
	public Model m_modelEnemy3 = null;

	public AnimRig m_rig = null;
	public AnimPose m_pose0 = null;
	public AnimPose m_pose1 = null;
	public AnimPoseStack m_poseStack = null;

	public int ScanOutWidth = 0;
	public int ScanOutHeight = 0;

	public UiForm m_formDebug = null;
	public UiRectButton m_button0 = null;
	public UiRectButton m_button1 = null;
	public UiRectButton m_buttonPause = null;

	public final int kMaxEnemies = 8;
	public GameEnemy[] m_enemies = null;
	public GameShot m_shotEnemies[] = null;

	public final int kMaxStarShips = 4;
	public GameShot m_shotStarShips[] = null;
	public GameAntiBeamFieldRender m_antiBeamField = null;
	public GameAntiBeamFieldDamageList m_antiBeamFieldDamages = null;

	public GameTargetList m_targetList = new GameTargetList();

	public boolean IsPause = false;

	//public GameHudFont m_hudFont = null;

	//public StaticTexture m_texture = null;
	
	public int Score = 0;
	
	public GameEnemy.TypeDesc[] m_enemyTypes = null;

	public final Frustum m_frustum = new Frustum();
	public final Box m_box = new Box();
	public final Box m_boxScissor = new Box();
	
	public FrameBuffer m_frameBuffer = null;
	//public NpotRenderTexture m_renderTexture = null;
	//public RenderBuffer m_depthBuffer = null;
	public SamplerState m_samplerNearest = null;
	public SamplerState m_samplerLinear = null;
	public GameModelExplosionRender m_explosionRender = null;
	
	public RenderTexture m_textureShadow = null;
	public RenderBuffer m_bufferShadowDepth = null;
	public FrameBuffer m_frameBufferShadow = null;
	
	public StaticTexture m_textureNoise = null;
	
	public double m_fStartTime = 0.0f;
	public DelayResourceQueueMarker HudFontMarker = new DelayResourceQueueMarker( "HudFont" );
	
	public DelayResourceQueueMarker Marker0 = new DelayResourceQueueMarker( "Game0" );
	public DelayResourceQueueMarker Marker1 = new DelayResourceQueueMarker( "Game1" );
//	
	//public OcclusionQuery m_occlusionQuery = null;
	public GameLightGlare m_lightGlare = null;
	public PostEffectBloom m_postEffectBloom = null;
	
	public GameMain()
	{
		if( SubSystem.Log != null )
		{
			SubSystem.Log.WriteLine(this, "new GameMain()" );
		}
	}

	public void SetModelDiffuse(Model model, float r, float  g, float b)
	{
		for (ModelMaterial material : model.Materials)
		{
			material.Diffuse.Set(r, g, b, 1.0f);
		}
	}

	public void OnCreate( DelayResourceQueue drq ) //throws ThreadForceDestroyException
	{
		SubSystem.Log.WriteLine(this, "OnCreate():Begin" );
		
		m_fStartTime = SubSystem.Timer.FrameTime;
		//RawImage rawImage = SubSystem.Loader.LoadPng("smokes.png");
		//m_texture = new StaticTexture(rawImage);
		

		m_player = new GamePlayer();
		m_viewPoint = new GameViewPoint();
		
		m_starShips = new GameStarShip[kMaxStarShips];
		for (int i = 0 ; i < m_starShips.length ; i++)
		{
			m_starShips[i] = new GameStarShip();
		}
		
		//. shadow map.		
		m_textureShadow = new RenderTexture( drq, RenderTextureFormat.RGBA, 512, 512 );
		m_bufferShadowDepth = new RenderBuffer( drq, ERenderBufferFormat.DEPTH_COMPONENT16, 512, 512 );
		m_frameBufferShadow = new FrameBuffer( drq, m_textureShadow, m_bufferShadowDepth, null );

		for( GameStarShip starShip : m_starShips )
		{
			RenderTexture textureShadow = new RenderTexture( drq, RenderTextureFormat.RGBA, 512, 512 );
			FrameBuffer frameBufferShadow = new FrameBuffer( drq, textureShadow, m_bufferShadowDepth, null );

			starShip.Model.SetShadowBuffer( frameBufferShadow );
		}

		m_enemies = new GameEnemy[kMaxEnemies];
		for (int i = 0 ; i < m_enemies.length ; i++)
		{
			m_enemies[i] = new GameEnemy();
		}
		
		float[] arrayEnemyRadiusKernel = 
		{
			0.0f,1.0f,0.5f, 0.0f, //0.25f, 0.25f, 0.25f, 0.0f,
		};

		m_shotEnemies = new GameShot[32];
		for (int i = 0 ; i < m_shotEnemies.length ; i++)
		{
			m_shotEnemies[i] = new GameShot(10.0f, arrayEnemyRadiusKernel);
			m_shotEnemies[i].ColorIn.Set(2.0f, 1.0f, 1.5f, 1.0f);
			m_shotEnemies[i].ColorOut.Set(0.0f, 0.0f, 0.0f, 1.0f);
			m_shotEnemies[i].Power = 10.0f;
		}

		m_shotStarShips = new GameShot[32];
		for (int i = 0 ; i < m_shotStarShips.length ; i++)
		{
			m_shotStarShips[i] = new GameShot(10.0f, arrayEnemyRadiusKernel);
			m_shotStarShips[i].ColorIn.Set(2.0f, 1.0f, 1.5f, 1.0f);
			m_shotStarShips[i].ColorOut.Set(0.0f, 0.0f, 0.0f, 1.0f);
			m_shotStarShips[i].Power = 10.0f;
		}

		RenderTexture renderTexture = new RenderTexture( drq, RenderTextureFormat.RGBA,640,400 );
		RenderBuffer depthBuffer = new RenderBuffer( drq, ERenderBufferFormat.DEPTH_COMPONENT16,
			renderTexture.GetPotWidth(),
			renderTexture.GetPotHeight() );
		
		m_frameBuffer = new FrameBuffer( drq, renderTexture, depthBuffer, null );
		
		m_samplerNearest = new SamplerState( EMagFilter.Nearest, EMinFilter.Nearest, EWrap.Repeat, EWrap.Repeat );
		m_samplerLinear = new SamplerState( EMagFilter.Linear, EMinFilter.Linear, EWrap.ClampToEdge, EWrap.ClampToEdge );
		
		//m_occlusionQuery = new OcclusionQuery( drq, 1 );//, 640, 400 );
		m_lightGlare = new GameLightGlare( drq, kMaxStarShips, new StaticTexture( drq, SubSystem.Loader.LoadPng( "lens_flare.png" ), true ), m_frameBuffer );
		m_postEffectBloom = new PostEffectBloom( drq, m_frameBuffer.Width, m_frameBuffer.Height, 3 );
		
		drq.Add( HudFontMarker );
		
		DelayResourceLoader drl = SubSystem.DelayResourceLoader;
		
		drl.RegisterJob( "game0", drq, new DelayResourceLoader.Job(){ @Override public void OnLoadContent( DelayResourceQueue drq )
		{
			//. starship model.
			m_modelStarShip = LoadModel(drq, "starship1.ssgmodel");
			m_collisionStarShip = LoadCollision("starship1.ssgcollision");

			//. enemy model.
			m_modelEnemy0 = LoadModel(drq, "meka.ssgmodel");
			m_modelEnemy1 = LoadModel(drq, "meka.ssgmodel");
			m_modelEnemy2 = LoadModel(drq, "meka.ssgmodel");
			m_modelEnemy3 = LoadModel(drq, "meka.ssgmodel");

			SetModelDiffuse(m_modelEnemy0, 1.0f, 0.8f, 0.1f);		
			SetModelDiffuse(m_modelEnemy1, 0.5f, 1.0f, 0.6f);		
			SetModelDiffuse(m_modelEnemy2, 1.0f, 0.2f, 0.7f);		
			SetModelDiffuse(m_modelEnemy3, 0.2f, 0.1f, 1.0f);		

			//. enemy type.
			m_enemyTypes = new GameEnemy.TypeDesc[]
			{
				new GameEnemy.TypeDesc(GameEnemy.EType.AWACS, "MS14", m_modelEnemy0),
				new GameEnemy.TypeDesc(GameEnemy.EType.Assault, "MS06R", m_modelEnemy1),
				new GameEnemy.TypeDesc(GameEnemy.EType.LightArmor, "MS06S", m_modelEnemy2),
				new GameEnemy.TypeDesc(GameEnemy.EType.HeavyGunner, "MS09R", m_modelEnemy3),
			};

			//. enemy pose.
			m_rig = new AnimRig(m_modelEnemy0);
			m_pose0 = new AnimPose(LoadModel(drq, "meka_fly.ssgmodel").Joints);
			m_pose1 = new AnimPose(LoadModel(drq, "meka_standby.ssgmodel").Joints);

			m_poseStack = new AnimPoseStack(4, m_rig);
			m_poseStack.Push(m_pose0, 5.0f);			
			drq.Add( Marker0 );
		}});
		
		drl.RegisterJob( "game1", drq, new DelayResourceLoader.Job(){ @Override public void OnLoadContent( DelayResourceQueue drq )
		{			
			//. starship beam field.
			m_antiBeamField = new GameAntiBeamFieldRender(drq,16, 32);
			m_antiBeamFieldDamages = new GameAntiBeamFieldDamageList();

			//. light tail fx.
			m_lightTailRender = new LightTailRender( drq, 16, 8);

			//. back ground star field.
			m_starField = new StarField(drq);

			//. particles.
			m_particle = new GameParticle(drq);		

			//, starship explosion.
			m_explosionRender = new GameModelExplosionRender( drq );
			PerlinNoise noise = new PerlinNoise( 64, 64, 64 );
			m_textureNoise = new StaticTexture( drq,
											   TextureInternalFormat.Luminance, noise.SurfaceWidth, noise.SurfaceHeight,
											   TextureSourceFormat.Luminance, TextureSourceType.UnsignedByte, noise.Pixels, true );
			m_explosionRender.Parameters.Noise = m_textureNoise;
			drq.Add( Marker1 );
		}});
		SubSystem.Log.WriteLine(this, "OnCreate():End" );
	}
	/*
	public void OnLoadContent( DelayResourceQueue drq, int t ) //throws ThreadForceDestroyException
	{
		if( t == 0 )
		{
			//. starship model.
			m_modelStarShip = LoadModel(drq, "starship1.ssgmodel");
			m_collisionStarShip = LoadCollision("starship1.ssgcollision");
			
			//. enemy model.
			m_modelEnemy0 = LoadModel(drq, "meka.ssgmodel");
			m_modelEnemy1 = LoadModel(drq, "meka.ssgmodel");
			m_modelEnemy2 = LoadModel(drq, "meka.ssgmodel");
			m_modelEnemy3 = LoadModel(drq, "meka.ssgmodel");
	
			SetModelDiffuse(m_modelEnemy0, 1.0f, 0.8f, 0.1f);		
			SetModelDiffuse(m_modelEnemy1, 0.5f, 1.0f, 0.6f);		
			SetModelDiffuse(m_modelEnemy2, 1.0f, 0.2f, 0.7f);		
			SetModelDiffuse(m_modelEnemy3, 0.2f, 0.1f, 1.0f);		
		
			//. enemy type.
			m_enemyTypes = new GameEnemy.TypeDesc[]
			{
				new GameEnemy.TypeDesc(GameEnemy.EType.AWACS, "MS14", m_modelEnemy0),
				new GameEnemy.TypeDesc(GameEnemy.EType.Assault, "MS06R", m_modelEnemy1),
				new GameEnemy.TypeDesc(GameEnemy.EType.LightArmor, "MS06S", m_modelEnemy2),
				new GameEnemy.TypeDesc(GameEnemy.EType.HeavyGunner, "MS09R", m_modelEnemy3),
			};
				
			//. enemy pose.
			m_rig = new AnimRig(m_modelEnemy0);
			m_pose0 = new AnimPose(LoadModel(drq, "meka_fly.ssgmodel").Joints);
			m_pose1 = new AnimPose(LoadModel(drq, "meka_standby.ssgmodel").Joints);

			m_poseStack = new AnimPoseStack(4, m_rig);
			m_poseStack.Push(m_pose0, 5.0f);			
		}
		else
		{
			//. starship beam field.
			m_antiBeamField = new GameAntiBeamFieldRender(drq,16, 32);
			m_antiBeamFieldDamages = new GameAntiBeamFieldDamageList();

			//. light tail fx.
			m_lightTailRender = new LightTailRender( drq, 16, 8);

			//. back ground star field.
			m_starField = new StarField(drq);

			//. particles.
			m_particle = new GameParticle(drq);		
		
			//, starship explosion.
			m_explosionRender = new GameModelExplosionRender( drq );
			PerlinNoise noise = new PerlinNoise( 64, 64, 64 );
			m_textureNoise = new StaticTexture( drq,
										   TextureInternalFormat.Luminance, noise.SurfaceWidth, noise.SurfaceHeight,
										   TextureSourceFormat.Luminance, TextureSourceType.UnsignedByte, noise.Pixels, true );
			m_explosionRender.Parameters.Noise = m_textureNoise;
		}
	}
	*/
	public void OnSurfaceChanged(int w, int h)
	{
		SubSystem.Log.WriteLine(this, "OnSurfaceChanged");
		ScanOutWidth = w;
		ScanOutHeight = h;
		
		//m_frameBuffer.OnSurfaceChanged(w*2/3,h*2/3);
		m_frameBuffer.OnSurfaceChanged(w/2,h/2);
		//m_occlusionQuery.OnSurfaceChanged(w/2,h/2);
		m_player.OnSurfaceChanged(w, h);
		m_viewPoint.OnSurfaceChanged(w, h);

		m_postEffectBloom.OnSurfaceChanged(w/2,h/2);
		m_lightGlare.OnSurfaceChanged( m_frameBuffer );
		
		m_formDebug = new UiForm();
		m_formDebug.Add((m_button0 = new UiRectButton(new Rect(w - 10 - 40, 10, 40, 40)))); 
		m_formDebug.Add((m_button1 = new UiRectButton(new Rect(w - 10 - 40, 10 + 40 + 10, 40, 40))));
		m_formDebug.Add((m_buttonPause = new UiRectButton(new Rect(w / 2 - 40, h - 50, 80, 40))));
	}

	public void OnUpdate()
	{
		if( Marker0.Done == false || Marker1.Done == false )
		{
			//. Loading.
			return;
		}

		float fElapsedTime = SubSystem.Timer.SafeFrameElapsedTime;

		m_formDebug.Update(SubSystem.FramePointer, fElapsedTime);

		if (m_buttonPause.IsPush())
		{
			IsPause = !IsPause;
		}

		if (IsPause)
		{
			return;
		}

		//. プレイヤー更新.
		m_player.Update(fElapsedTime);

		//. 戦艦と自機のあたり.
		for (GameStarShip ship : m_starShips)
		{
			Intersection intersection = Intersection.Local();
			if (ship.Intersect(m_player.GetSphere(), intersection))
			{
				Float3 f3 = Float3.Local();
				//Float3 f3Normal = Float3.Normalize( Float3.Local(), intersection.Normal );
				Float3.Mad(f3, intersection.Normal, m_player.GetSphere().Radius, intersection.Position);
				m_player.Transform.SetAxisW(f3);

				float fDot = Float3.Dot(intersection.Normal, m_player.Velocity);

				Float3.Mad(m_player.Velocity, intersection.Normal, -fDot, m_player.Velocity);


			}
		}

		m_targetList.Player.Update(m_player.Transform, m_player.Velocity, true);


		float fAspect = (float) ScanOutWidth / (float) ScanOutHeight;
		m_viewPoint.Update( 
			m_player.TransformView,
			FMath.ToRad(90.0f),
			10.0f, 20000.0f);

		m_targetList.ViewPoint.Update(m_viewPoint.Transform, null, true);

		m_player.PostUpdate(fElapsedTime, m_viewPoint);

		m_starField.Update(fElapsedTime, m_viewPoint.View.Values, m_viewPoint.Projection.Values);


		int nbIdleEnemies = 0;

		for (GameEnemy enemy : m_enemies)
		{
			//enemy.Target.Transform.Set( m_player.Transform );
			//enemy.Target.Velocity.Set( m_player.Velocity );
			//enemy.Target = m_targetList.Player;

			enemy.Update(fElapsedTime, m_particle);

			if (enemy.Fire)
			{
				for (GameShot shot : m_shotEnemies)
				{
					if (shot.IsIdle())
					{
						shot.Spawn(enemy.Transform,
								   Float3.Mul(Float3.Local(), enemy.Transform.GetAxisZ(Float3.Local()), 8000.0f));
						break;
					}
				}
			}

			if (enemy.IsIdle())
			{
				nbIdleEnemies++;
			}
		}

		if (nbIdleEnemies >= m_enemies.length)
		{
			GenerateWave();
		}

		for (int i = 0 ; i < m_enemies.length ; i++)
		{
			m_targetList.Enemies[i].Update(m_enemies[i].Transform, m_enemies[i].Velocity, m_enemies[i].IsBusy());
		}

		for (GameShot shot : m_shotEnemies)
		{
			shot.Update(fElapsedTime);

			if (shot.IsBusy())
			{
				if (m_player.AddDamage(shot.m_edge.Start, shot.m_edge.End, shot.Power))
				{
					shot.Release();
				}
			}

		}


		int nbIdleShips = 0;
		for (GameStarShip ship : m_starShips)
		{
			if (ship.IsIdle())
			{
				nbIdleShips++;
			}
		}


		for (int i = 0 ; i < m_starShips.length ; i++)
		{
			if (nbIdleShips >= m_starShips.length)
			{
				m_starShips[i].Spawn(
					Float4x4.Translation(
						2000.0f * i + SubSystem.Rand.Float(-1000.0f, 1000.0f),
						SubSystem.Rand.Float(-2000.0f, 2000.0f),
						SubSystem.Rand.Float(-2000.0f, 2000.0f)),
					m_modelStarShip, m_collisionStarShip);
			}


			m_starShips[i].Update(fElapsedTime, m_particle, m_targetList.Player, m_shotStarShips);
			
			Float3 f3 = Float4x4.MulXYZ1( Float3.Local(), Float3.Local( 0.0f, 0.0f, 1000.0f ), m_starShips[i].Transform );
						
			m_lightGlare.Update
			(
				i,
				(m_starShips[i].IsBusy() || m_starShips[i].IsExplosion()),
				f3 
			);
		}

		m_antiBeamFieldDamages.Update(fElapsedTime);

		for (GameShot shot : m_shotStarShips)
		{
			shot.Update(fElapsedTime);

			if (shot.IsBusy())
			{
				if (m_player.AddDamage(shot.m_edge.Start, shot.m_edge.End, shot.Power))
				{
					shot.Release();
				}
			}

		}


		GameTarget targetMissile = null;
		float fNearest = -1.0f;
		for (int i = 0 ; i < m_enemies.length; i++)
		{
			if (m_targetList.Enemies[i].IsIdle())
			{
				continue;
			}
			GameTarget targetPlayer = m_targetList.Player;
			GameTarget target = m_targetList.Enemies[i];
			Edge edge = Edge.Local();
			edge.Set(targetPlayer.Transform.GetAxisW(Float3.Local()),
					 Float3.Mad(Float3.Local(),
								targetPlayer.Transform.GetAxisZ(Float3.Local()),
								10000.0f,
								targetPlayer.Transform.GetAxisW(Float3.Local())));

			Float3 f3PlayerPos = targetPlayer.Transform.GetAxisW(Float3.Local());
			Float3 f3EnemyPos = target.Transform.GetAxisW(Float3.Local());

			float dot = Float3.Dot(
				edge.Direction,
				Float3.Sub(Float3.Local(), f3EnemyPos, f3PlayerPos)
			);

			if (dot > 0.0f)
			{
				Float3 f3Nearest = Float3.Mad(Float3.Local(),
											  edge.Direction, dot, edge.Start);

				float fDistance = Float3.Distance(f3Nearest, f3EnemyPos);

				if ((fNearest < 0.0f || fDistance < fNearest))
				{
					targetMissile = target;
					fNearest = fDistance;

				}
			}

		}

		for (GameMissile missile : m_player.Missiles)
		{
			if (missile.IsBusy())
			{
				missile.Target = targetMissile;//m_targetList.Enemies[0];
			}
		}
		for (GameMissile missile : m_player.Missiles)
		{
			if (missile.IsBusy())
			{
				TestShotCollisions(missile);
			}
		}
		for (GameShot shot : m_player.Shots)
		{
			if (shot.IsBusy())
			{
				TestShotCollisions(shot);
			}
		}

		for (GameShot shot : m_player.Beams)
		{
			if (shot.IsBusy())
			{
				TestShotCollisions(shot);
			}
		}

		m_particle.Update(fElapsedTime);



		if (m_button0.IsPush())
		{
			m_poseStack.Push(m_pose0, 0.5f);
		}
		if (m_button1.IsPush())
		{
			m_poseStack.Push(m_pose1, 0.5f);
		}

		m_poseStack.Update(fElapsedTime, Float4x4.Translation(0.0f, 0.0f, 0.0f));

		//java.lang.System.gc();
	}


	private boolean TestShotCollisions(GameShotBase shot)
	{
		Edge edge = shot.GetEdge();
		Intersection intersectionNearest = null;
		GameStarShip starShipNearest = null;

		boolean isReflect = false;

		for (GameStarShip starShip : m_starShips)
		{
			if ( 
				starShip.AntiBeamField.Damage < starShip.AntiBeamField.DamageLimit
				&& shot.Type == GameShotBase.EType.Beam
				)//&& ((GameShot)shot).Reflect == false )
			{
				//.
				Intersection intersection = Intersection.Local();
				float fDistanceStart = Float3.Distance(edge.Start, starShip.Position); 
				float fDistanceEnd = Float3.Distance(edge.End, starShip.Position); 
				float fRadius = starShip.AntiBeamField.Radius;
				if (fDistanceEnd <= fRadius && fDistanceStart > fRadius)
				{
					Float3.SubNormalize(intersection.Normal, shot.Position, starShip.Position);
					Float3.Mad(intersection.Position, intersection.Normal, starShip.AntiBeamField.Radius, starShip.Position);
					intersection.Distance = fDistanceEnd;
					if (intersectionNearest == null)
					{
						intersectionNearest = intersection;
						starShipNearest = starShip;
					}
					else if (intersection.Distance < intersectionNearest.Distance)
					{
						intersectionNearest = intersection;
						starShipNearest = starShip;
					}
					isReflect = true;
				}
				else if (fDistanceStart <= fRadius)
				{
					intersection = starShip.Intersect(edge);
					if (intersection != null)
					{
						if (intersectionNearest == null)
						{
							intersectionNearest = intersection;
							starShipNearest = starShip;
						}
						else if (intersection.Distance < intersectionNearest.Distance)
						{
							intersectionNearest = intersection;
							starShipNearest = starShip;
						}
					}

				}

				/*
				 Intersection intersection = starShip.Intersect(edge);
				 if (intersection != null)
				 {
				 if (intersectionNearest == null)
				 {
				 intersectionNearest = intersection;
				 starShipNearest = starShip;
				 }
				 else if (intersection.Distance < intersectionNearest.Distance)
				 {
				 intersectionNearest = intersection;
				 starShipNearest = starShip;
				 }

				 isReflect = true;
				 }
				 */
			}
			else
			{
				Intersection intersection = starShip.Intersect(edge);
				if (intersection != null)
				{
					if (intersectionNearest == null)
					{
						intersectionNearest = intersection;
						starShipNearest = starShip;
					}
					else if (intersection.Distance < intersectionNearest.Distance)
					{
						intersectionNearest = intersection;
						starShipNearest = starShip;
					}
				}
			}
		}

		if (intersectionNearest != null)
		{
			if (isReflect)
			{
				/*
				 Float3 f3 = Float3.Local();
				 Float3.Sub( f3, shot.GetEdge().Start,  starShipNearest.ShieldSphere.Position );
				 Float3.Normalize( f3, f3 );
				 Float3.Mad( f3, f3, starShipNearest.ShieldSphere.Radius + 10.0f, starShipNearest.ShieldSphere.Position );
				 */
				//shot.Position.Set( intersectionNearest.Position );
				//shot.Release();

				//m_particle.SpawnSmallExplosion(intersectionNearest.Position, 100.0f);

				//Float3 f3 = Float3.Local();
				//Float3.Mad( f3, intersectionNearest.Normal, -starShipNearest.AntiBeamFieldRadius + 500.0f, intersectionNearest.Position );
				//((GameShot)shot).Reflect( f3, starShipNearest.AntiBeamFieldRadius );


				((GameShot)shot).Reflect(starShipNearest.Position, starShipNearest.AntiBeamField.Radius);
				//starShipNearest.AntiBeamField.DamagePosition.Load( intersectionNearest.Position.X, intersectionNearest.Position.Y, intersectionNearest.Position.Z );
				//starShipNearest.AntiBeamField.m_adsr.Start();
				//starShipNearest.AntiBeamField.SetDamagePosition( intersectionNearest.Position );
				m_antiBeamFieldDamages.Spawn(intersectionNearest.Position, starShipNearest.Transform, starShipNearest.AntiBeamField.Radius);
				m_particle.SpawnFlash( intersectionNearest.Position, starShipNearest.Velocity, 1000.0f );
			}
			else
			{
				m_particle.SpawnSmallExplosion(intersectionNearest.Position, shot.ParticleRadius );

				if (starShipNearest.AntiBeamField.Damage < starShipNearest.AntiBeamField.DamageLimit)
				{
					starShipNearest.AddAntiBeamFieldDamage(shot, m_particle);
					shot.Release();
				}
				else
				{
					if( starShipNearest.AddBodyDamage(shot, m_particle) )
					{
						Score += 1000;
					}
					shot.Release();
				}
				
				//System.Error.WriteLine( "hit" );
				//m_intersection = intersection;
			}
		}

		for (GameEnemy enemy : m_enemies)
		{
			if (enemy.IsActive())
			{
				Intersection intersection = Intersection.Local();
				//if( FMath.Distance( edge.Start, edge.End, enemy.GetSphere().Position, intersection )
				//	< enemy.GetSphere().Radius + shot.BaseRadius )
				Sphere sphere = enemy.GetSphere();
				if (Collision.Intersect(edge, sphere.Position, sphere.Radius + shot.BaseRadius, intersection))
				{
					if( enemy.Damage(edge, shot.Power, m_particle) )
					{
						Score += 10;
					}
					shot.Release();
				}
			}
		}
		return false;
	}

	private void GenerateWave()
	{
		float r = SubSystem.Rand.Float();
		if( r < 0.95f )
		{
			GenerateWaveDefault();
		}
		else
		{
			GenerateWaveLevel0();
		}
	}
	
	private void GenerateWaveDefault()
	{
		for( int i = 0 ; i < m_enemies.length ; i++ )
		//for( int i = 0 ; i < 6 ; i++ )
		{
			GameEnemy enemy = m_enemies[i];

			
			if( (i%5) == 0 )
			{
				enemy.Spawn(Float4x4.Translation(Float3.Rand(-8000.0f, 8000.0f)));
				enemy.LocationTarget = m_targetList.Player;
				enemy.AttackTarget = m_targetList.Player;
				enemy.SetType( m_enemyTypes[2]);
				enemy.SquadPosition = 0;
				enemy.Leader = null;
				
			}
			else
			{
				GameEnemy leader = m_enemies[(i/5)*5];
				
				Float3 pos[] =
				{
					new Float3( -500.0f, 0.0f, 0.0f ),
					new Float3( -500.0f, 0.0f, 0.0f ),
					new Float3(  500.0f, 0.0f, 0.0f ),
					new Float3( -1000.0f, 0.0f, 0.0f ),
					new Float3(  1000.0f, 0.0f, 0.0f ),
				};
				
				enemy.Spawn(Float4x4.Mul( Float4x4.Local(), Float4x4.Translation( pos[i%5] ), leader.Transform ));
				enemy.LocationTarget = m_targetList.Enemies[(i/5)*3];
				enemy.AttackTarget = m_targetList.Player;
				enemy.SetType( m_enemyTypes[1]);
				enemy.SquadPosition = i%5;
				enemy.Leader = leader;
				enemy.m_v3AiTarget.Set( pos[i%5] );
			}
			//enemy.SetType( m_enemyTypes[SubSystem.Rand.Integer()%m_enemyTypes.length]);
		}
		
	}
	
	private void GenerateWaveLevel0()
	{
		for( int i = 0 ; i < m_enemies.length ; i++ )
		{
			GameEnemy enemy = m_enemies[i];

			enemy.Spawn(Float4x4.Translation(Float3.Rand(-8000.0f, 8000.0f)));
			enemy.LocationTarget = m_targetList.Player;
			enemy.AttackTarget = m_targetList.Player;
			enemy.SquadPosition = 0;
			enemy.Leader = null;
			
			enemy.SetType( m_enemyTypes[1] );
		}		
	}
	
	public final void OnRender(final RenderContext rc)
	{		
		if( Marker0.Done == false || Marker1.Done == false )
		{
			RenderLoadingProgress(rc);
		}
		else 
		{
			RenderScene(rc);
		}
	}
		
		
	private final void RenderLoadingProgress(final RenderContext rc)
	{
		final GfxCommandContext r = rc.GetCommandContext();

		if (SubSystem.MinimumMarker.Done == false )//|| HudFontMarker.Done == false)
		{
			//GLES20.glClearColor(0.0f, 0.0f, 0.2f, 1.f);
			//GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
			r.SetClearColor(0.0f, 0.0f, 0.2f, 1.f);
			r.Clear(EClearBuffer.ColorDepth);
			return;
		}

		final FrameTimer timer = com.nrt.framework.SubSystem.Timer;
		final BasicRender br = rc.GetBasicRender();
		final MatrixCache mc = rc.GetMatrixCache();

		//r.SetFrameBuffer(m_frameBuffer);

		//GLES20.glClearColor(0.0f, 0.0f, 0.2f, 1.f);
		//GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		r.SetClearColor(0.0f, 0.0f, 1.0f, 1.f);
		r.Clear(EClearBuffer.ColorDepth);

		//GLES20.glViewport(0, 0, m_frameBuffer.Width, m_frameBuffer.Height);
		//r.SetViewport(0, 0, m_frameBuffer.Width, m_frameBuffer.Height);
		r.SetViewport(0, 0, ScanOutWidth, ScanOutHeight);

		Float4x4 matrixOrtho = Float4x4.Local();
		//Matrix.orthoM(matrixOrtho.Values, 0, 0, m_frameBuffer.Width, m_frameBuffer.Height, 0, -1.0f, 1.0f);
		//Float4x4.Ortho(matrixOrtho, 0, m_frameBuffer.Width, m_frameBuffer.Height, 0, -1.0f, 1.0f);


		Float4x4.Ortho(matrixOrtho, 0, ScanOutWidth/2, ScanOutHeight/2, 0, -1.0f, 1.0f);



		mc.SetWorld(Float4x4.Identity());
		mc.SetView(Float4x4.Identity());
		mc.SetProjection(matrixOrtho);

		final BitmapFont bf = rc.GetBitmapFont();
		//bf.SetCommandContext(rc.GetCommandContext(),rc.GetMatrixCache(),rc.GetVertexBuffer(),rc.GetIndexBuffer());

		bf.SetSize(8.0f);
		bf.SetColor(0xffffffff);
		bf.Begin();
		bf.Draw
		(
			0.0f, 96.0f, 0.0f,
			String.format
			(
				"LOADING RESOURCES %d/%d THREADS %d/%d",
				SubSystem.DelayResourceQueue.m_queueResources.size(),
				SubSystem.DelayResourceQueue.m_nbMaxResources,
				SubSystem.DelayResourceLoader.GetLeftJobCount(),
				SubSystem.DelayResourceLoader.GetAllJobCount()
			)
		);
		bf.Draw(0.0f, 96.0f + 8.0f, 0.0f, String.format("TIME %5.1f", timer.FrameTime - m_fStartTime));
		bf.Draw(0.0f, 96.0f + 8.0f * 2.0f, 0.0f, String.format("FPS %2.1f", 1.0f / timer.FrameElapsedTime));
		bf.Draw(0.0f, 96.0f + 8.0f * 3.0f, 0.0f, String.format("RPF %d", SubSystem.DelayResourceQueue.m_nbResources));

		bf.End();

		//if(true)
		//	return;

		float w = ScanOutWidth/2;//m_frameBuffer.Width;
		float h = ScanOutHeight/2;//m_frameBuffer.Height;
		float size = 8.0f;
		float barSize = w/3;
		String strCaption = "LOADING";
		float ox = (w - strCaption.length()*6.0f)/2.0f;
		float oy = (h - 3.0f*size)/2.0f;
		//ox = ((int)(ox/6.0f))*6;
		//oy = ((int)(oy/8.0f))*8;
		
		//SubSystem.Log.WriteLine( " !" + oy );
		
		bf.Begin();
		/*
		for( int i = 0 ; i < 64 ; i++ )
		{
			bf.Draw( ox, i*8, 0.0f, strCaption );
		}
		*/
		bf.Draw( ox, oy, 0.0f, strCaption );
		bf.End();
		
		br.SetColor( 0.3f, 0.76f, 0.92f, 1.0f );
		//br.Begin( GLES20.GL_TRIANGLE_STRIP, BasicRender.EShader.Color );
		
		ox = (w - barSize)/2.0f;
		final DelayResourceQueue drq = SubSystem.DelayResourceQueue;
				
		int nbMaxResources = 1648;
		float progress = barSize*(((float)drq.m_nbAppliedResources/(float)nbMaxResources));
		
		br.FillRectangle( ox, oy + 8.0f*2.0f, 0.0f, progress, size );
		

		
		/*
		//. Present backbuffer.
		r.SetFrameBuffer(null);
		//GLES20.glViewport(0, 0, ScanOutWidth, ScanOutHeight);
		r.SetViewport(0, 0, ScanOutWidth, ScanOutHeight);

		//Float4x4 matrixOrtho = Float4x4.Local();
		Float4x4.Ortho(matrixOrtho, 0, ScanOutWidth, 0, ScanOutHeight, -1.0f, 1.0f);
		
		mc.SetProjection(matrixOrtho);
		mc.SetView(Float4x4.Identity());
		mc.SetWorld(Float4x4.Identity());

		//GLES20.glDisable(GLES20.GL_BLEND);
		//GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		BlendState blendDisable = new BlendState(false,BlendFunc.One,BlendFunc.Zero);
		r.SetBlendState(blendDisable);


		br.SetTexture(m_frameBuffer.ColorRenderTexture);
		br.SetSamplerState(m_samplerNearest);
		
		br.Begin(EPrimitive.TriangleStrip, BasicRender.EShader.ColorTexture);

		br.SetColor(1.0f, 1.0f, 1.0f, 1.0f);
		float u = ((float)m_frameBuffer.ColorRenderTexture.GetNpotWidth()) / ((float)m_frameBuffer.ColorRenderTexture.GetPotWidth());
		float v = ((float)m_frameBuffer.ColorRenderTexture.GetNpotHeight()) / ((float)m_frameBuffer.ColorRenderTexture.GetPotHeight());
		br.SetTexcoord(0.0f, 0.0f);    br.SetVertex(0.0f, 0.0f, 0.0f);
		br.SetTexcoord(0.0f, v*2); br.SetVertex(0.0f, ScanOutHeight*2, 0.0f);
		br.SetTexcoord(u*2,    0.0f);    br.SetVertex(ScanOutWidth*2, 0.0f, 0.0f);
		//br.SetTexcoord(u,    v); br.SetVertex(ScanOutWidth, ScanOutHeight, 0.0f);
		br.End();
		*/
	}
		
		
	private void RenderScene( final RenderContext rc )
	{
		//Render r = com.nrt.framework.SubSystem.Render;
		FrameTimer timer = com.nrt.framework.SubSystem.Timer;
		//BasicRender br = SubSystem.BasicRender;
		//MatrixCache mc = br.GetMatrixCache();



		ModelRender mr = SubSystem.ModelRender;

		Float4x4 matrixProjection = Float4x4.Identity();
		Float4x4 matrixView = Float4x4.Identity();
		Float4x4 matrixWorld = Float4x4.Identity();

		matrixProjection = m_viewPoint.Projection;
		matrixView = m_viewPoint.View;

		m_frustum.Update(matrixProjection, matrixView);

		GfxCommandContext r = rc.GetCommandContext();
		MatrixCache mc = rc.GetMatrixCache();

		r.SetFrameBuffer(m_frameBufferShadow);
		DepthStencilState depthEnable = new DepthStencilState(true,EDepthFunc.Less, true);
		r.SetDepthStencilState(depthEnable);
		//GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		//GLES20.glDepthMask(true);

		RasterizerState cullBackCCW = new RasterizerState(true,ECullFace.Back,EFrontFace.CCW);
		RasterizerState cullFrontCCW = new RasterizerState(true,ECullFace.Front,EFrontFace.CCW);
		r.SetRasterizerState(cullBackCCW);
		//GLES20.glEnable(GLES20.GL_CULL_FACE);
		//GLES20.glFrontFace(GLES20.GL_CCW);
		//GLES20.glCullFace(GLES20.GL_BACK);

		BlendState blendDisable = new BlendState(false,BlendFunc.SrcAlpha,BlendFunc.OneMinusSrcAlpha);
		//GLES20.glDisable(GLES20.GL_BLEND);

		r.SetClearColor(0.0f,0.0f,0.0f,0.0f);
		r.Clear(EClearBuffer.ColorDepth);
		//GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		//GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		r.SetViewport(0, 0, m_frameBufferShadow.Width, m_frameBufferShadow.Height);
		//GLES20.glViewport(0, 0, m_frameBufferShadow.Width, m_frameBufferShadow.Height);

		ModelShadow shadow = new ModelShadow();
		{

			Float3 f3Center = m_modelEnemy0.Center;
			float fRadius = m_modelEnemy0.Radius;

			Float3 f3Position = Float3.Local(0.0f, -1000.0f, 0.0f);
			matrixWorld = Float4x4.Translation(f3Position);

			double rad = timer.FrameTime;
			rad /= 10.0;
			rad = (rad - Math.floor(rad)) * (double)FMath.RD;

			Float4x4.Mul(matrixWorld, Float4x4.RotationY(Float4x4.Local(), (float)rad), matrixWorld);

			shadow.SetWorld(matrixWorld);
			shadow.SetOrthoProjection(
				Float3.Add(Float3.Local(), f3Center, f3Position),
				mr.DirectionalLight.Direction, fRadius, fRadius);
			//Float3.Local(0.0f,0.0f,1.0f), fRadius, fRadius );

			/*
			 Float4x4 matrixOrtho = Float4x4.Ortho( 
			 -fRadius, fRadius, -fRadius, fRadius, -fRadius, fRadius );

			 Float3 f3At = Float3.Mad( Float3.Local(), f3Center, 1.0f, f3Position );
			 shadow.SetProjection( f3At, Float3.Local( 1.0f, 0.0f, 0.0f ), fRadius, fRadius );

			 Float3 f3Direction = Float3.Local( -1.0f, 0.0f, 0.0f );

			 Float4x4 matrixView = Float4x4.LookAt(
			 Float3.Mad( Float3.Local(), f3Direction, -fRadius, f3At ),
			 f3At,
			 Float3.Local( 0.0f, 1.0f, 0.0f ) );
			 */

			mc.SetWorld(shadow.World);
			mc.SetView(shadow.View);
			mc.SetProjection(shadow.Projection);


			shadow.Texture = m_textureShadow;
			/*
			 shadow.m_matrixShadow.Set( mc.GetWorldViewProjection() );
			 shadow.m_f3ShadowDirection.Set( f3Direction );			
			 */
			//GLES20.glCullFace(GLES20.GL_FRONT);
			r.SetRasterizerState(cullFrontCCW);
			mr.Draw(r,mc,m_modelEnemy0, m_poseStack.ModelMatrices, ModelShaderSet.EShadow.Caster);
			//GLES20.glCullFace(GLES20.GL_BACK);
			r.SetRasterizerState(cullBackCCW);
		}		

		for (GameStarShip starShip : m_starShips)
		{
			starShip.Model.IsVisible = true;
			starShip.Model.UpdateTransform(starShip.Transform);
			starShip.Model.UpdateFrustumCulling(m_frustum);


			if (starShip.Model.IsVible())
			{

				r.SetFrameBuffer(starShip.Model.ShadowFrameBuffer);
				/*
				 GLES20.glEnable(GLES20.GL_DEPTH_TEST);
				 GLES20.glDepthMask(true);

				 GLES20.glEnable(GLES20.GL_CULL_FACE);
				 GLES20.glFrontFace( GLES20.GL_CCW );
				 GLES20.glCullFace( GLES20.GL_BACK );

				 GLES20.glDisable(GLES20.GL_BLEND);
				 */

				//GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
				//GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
				r.SetClearColor(0.0f, 0.0f, 0.0f, 0.0f);
				r.Clear(EClearBuffer.ColorDepth);


				//GLES20.glViewport(0, 0,
				//				  starShip.Model.ShadowFrameBuffer.Width,
				//				  starShip.Model.ShadowFrameBuffer.Height);
				r.SetViewport(0, 0,	starShip.Model.ShadowFrameBuffer.Width,starShip.Model.ShadowFrameBuffer.Height);

				//GLES20.glEnable(GLES20.GL_CULL_FACE);
				//GLES20.glCullFace(GLES20.GL_FRONT);

				r.SetRasterizerState(cullFrontCCW);

				ModelShadow shadowStarShip = starShip.Model.Shadow;

				Float3 f3Center = starShip.Model.Model.Center;
				float fRadius = starShip.Model.Model.Radius;

				Float3 f3Position = starShip.Transform.GetAxisW(Float3.Local());

				shadowStarShip.SetWorld(starShip.Transform);
				shadowStarShip.SetOrthoProjection(
					Float3.Add(Float3.Local(), f3Center, f3Position),
					mr.DirectionalLight.Direction, fRadius, fRadius);

				mc.SetWorld(shadowStarShip.World);
				mc.SetView(shadowStarShip.View);
				mc.SetProjection(shadowStarShip.Projection);

				mr.Draw(r,mc,starShip.Model.Model, ModelShaderSet.EShadow.Caster);

				//GLES20.glCullFace(GLES20.GL_BACK);
				r.SetRasterizerState(cullBackCCW);
			}

		}

		//GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		//GLES20.glDepthMask(true);
		r.SetDepthStencilState(depthEnable);

		//GLES20.glEnable(GLES20.GL_CULL_FACE);
		//GLES20.glFrontFace(GLES20.GL_CCW);
		//GLES20.glCullFace(GLES20.GL_BACK);
		r.SetRasterizerState(cullBackCCW);

		//GLES20.glDisable(GLES20.GL_BLEND);
		r.SetBlendState(blendDisable);

		r.SetFrameBuffer(m_frameBuffer);
		
		/*
		 GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.f); 
		 GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT );

		 GLES20.glViewport(0, 0, m_frameBuffer.Width, m_frameBuffer.Height);

		 mc.SetWorld( Float4x4.Identity() );
		 mc.SetView( Float4x4.Identity() );
		 mc.SetProjection( Float4x4.Identity() );



		 br.Begin( GLES20.GL_TRIANGLES, BasicRender.EShader.Color );
		 br.SetColor( 0xff0000ff );
		 br.SetVertex( -1.0f, 1.0f, 0.0f );
		 br.SetColor( 0x00ff00ff );
		 br.SetVertex( -1.0f, -1.0f, 0.0f );
		 br.SetColor( 0x0000ffff );
		 br.SetVertex( 1.0f, -1.0f, 0.0f );
		 br.End();

		 r.SetFrameBuffer( null );
		 */




		//GLES20.glViewport(0, 0, m_frameBuffer.Width, m_frameBuffer.Height);
		r.SetViewport(0, 0, m_frameBuffer.Width, m_frameBuffer.Height);

		// 画面をクリア

		//GLES20.glDepthMask(true);
		//GLES20.glColorMask( true, true, true, true );
		//GLES20.glColorMask( true, true, true, false );
		//GLES20.glStencilMask(GLES20.GL_FALSE);
		//GLES20.glDisable(GLES20.GL_STENCIL_TEST);

		r.SetDepthStencilState(depthEnable);
		BlendState blendDisableRGBAEnable = new BlendState(false,BlendFunc.One,BlendFunc.Zero,true,true,true,true);
		r.SetBlendState(blendDisableRGBAEnable);

		//GLES20.glClearColor(0.0f, 0.0f, 0.1f, 0.0f);
		//GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_STENCIL_BUFFER_BIT);
		r.SetClearColor(0.0f,0.0f,0.0f,0.0f);
		r.Clear(EClearBuffer.ColorDepthStencil);




		mc.SetProjection(matrixProjection);
		mc.SetView(matrixView);
		mc.SetWorld(matrixWorld);

		{
			mc.SetWorld(shadow.World);

			mr.SetShadowParameter(shadow);
			mr.Draw(r,mc,m_modelEnemy0, m_poseStack.ModelMatrices, ModelShaderSet.EShadow.Reciever);
		}



		for (GameStarShip starShip : m_starShips)
		{
			if (starShip.Model.Model == null)
			{
				continue;
			}

			if (starShip.Model.IsVible() == false)
			{
				continue;
			}

			//m_box.Update( starShip.Model.Model.Min, starShip.Model.Model.Max, starShip.Transform );
			//m_box.ScissorBackFace( m_boxScissor, m_frustum.Surfaces );

			//if( m_boxScissor.PolygonCount <= 0 )
			//{
			//	continue;
			//}

			if (starShip.IsBusy() || starShip.IsExplosion())
			{
				mc.SetWorld(starShip.Transform);
				mr.SetShadowParameter(starShip.Model.Shadow);
				mr.Draw(r,mc,starShip.Model.Model, ModelShaderSet.EShadow.Reciever);
			}
			else if (starShip.IsExplosionFinal())
			{
				mc.SetWorld(starShip.Transform);

				mr.SetShaderSet(m_explosionRender.ShaderSet);
				mr.SetShaderParameter(m_explosionRender.Parameters);

				m_explosionRender.Parameters.DiscardStart = 3000.0f * starShip.DiscardRadius - 2000.0f;
				m_explosionRender.Parameters.DiscardEnd = 3000.0f * starShip.DiscardRadius;
				m_explosionRender.Parameters.Unit = 50.0f;
				m_explosionRender.Parameters.Radius = starShip.DiscardRadius - 0.1f;
				m_explosionRender.Parameters.Start = starShip.DiscardRadius;

				//DebugLog.Error.WriteLine( m_explosionRender.ShaderSet.RigidShaders[0].Uniforms.toString() );

				mr.Draw(r,mc,starShip.Model.Model, ModelShaderSet.EShadow.Disable);
				mr.SetShaderSet(null);
			}

		}

		for (GameEnemy  enemy : m_enemies)
		{
			if (enemy.IsVisible())
			{
				Model model = enemy.Model;

				Float4x4 matrix = Float4x4.Mul(
					Float4x4.Local(),
					Float4x4.Translation(0.0f, -(model.Min.Y + model.Max.Y) * 0.5f, 0.0f),
					Float4x4.Scaling(0.5f, 0.5f, 0.5f),
					enemy.Transform);

				m_box.Update(model.Min, model.Max, matrix);
				m_box.ScissorBackFace(m_boxScissor, m_frustum.Surfaces);
				//mc.SetWorld( Float4x4.Identity() );
				//br.SetColor( 0.0f, 1.0f, 0.0f, 1.0f );
				//m_boxScissor.Draw( br );

				if (m_boxScissor.PolygonCount > 0)
				{
					mc.SetWorld(matrix);
					mr.Draw(r,mc,model, m_poseStack.ModelMatrices, ModelShaderSet.EShadow.Disable);

					//mc.SetWorld(enemy.Transform);
					//br.Axis(0.0f, 0.0f, 0.0f, enemy.GetSphere().Radius);
				}
			}
		}

		final Float4x4 matrixTranslation = Float4x4.Local();
		mc.SetWorld(Float4x4.Translation(matrixTranslation,0.0f, 300.0f, 0.0f));

		mr.SetShaderSet(m_explosionRender.ShaderSet);
		mr.SetShaderParameter(m_explosionRender.Parameters);
		m_explosionRender.Parameters.DiscardStart = 300.0f;
		m_explosionRender.Parameters.DiscardEnd = 600.0f;
		m_explosionRender.Parameters.Unit = 100.0f;
		m_explosionRender.Parameters.Radius = 0.5f;
		m_explosionRender.Parameters.Start = 0.9f;

		mr.Draw(r,mc,m_modelEnemy0, m_poseStack.ModelMatrices, ModelShaderSet.EShadow.Caster);
		mr.SetShaderSet(null);

		mc.SetWorld(Float4x4.Identity());

		for (GameShot shot : m_player.Shots)
		{
			if (shot.IsBusy() || shot.IsRelease())
			{
				//br.SetColor(1.0f, 0.0f, 0.0f, 1.0f);
				//shot.LightTail.m_boxMinMax.Draw(br);
				m_lightTailRender.Render(r,mc, shot.LightTail, m_frustum,  shot.RenderRadius, shot.ColorIn, shot.ColorOut);
			}
		}


		for (GameShot shot : m_player.Beams)
		{
			if (shot.IsBusy() || shot.IsRelease())
			{
				//shot.LightTail.m_boxMinMax.Draw(br);

				m_lightTailRender.Render(r,mc, shot.LightTail, m_frustum, shot.RenderRadius, shot.ColorIn, shot.ColorOut);
			}
		}

		for (GameShot shot : m_shotEnemies)
		{
			if (shot.IsBusy() || shot.IsRelease())
			{
				//shot.LightTail.m_boxMinMax.Draw(br);

				m_lightTailRender.Render(r,mc, shot.LightTail, m_frustum, shot.RenderRadius, shot.ColorIn, shot.ColorOut);
			}
		}

		for (GameShot shot : m_shotStarShips)
		{
			if (shot.IsBusy() || shot.IsRelease())
			{
				//shot.LightTail.m_boxMinMax.Draw(br);

				m_lightTailRender.Render(r,mc, shot.LightTail, m_frustum,  shot.RenderRadius, shot.ColorIn, shot.ColorOut);
			}
		}


		for (GameMissile missile : m_player.Missiles)
		{
			if (missile.IsBusy() || missile.IsRelease())
			{
				m_lightTailRender.Render(r, mc, missile.LightTail, m_frustum,  missile.RenderRadius, missile.ColorIn, missile.ColorOut);
			}
		}
		
		//GLES20.glColorMask( true, true, true, true );
		r.SetBlendState(blendDisableRGBAEnable);
		//m_occlusionQuery.Update(0,Float3.Local( 50.0f,-10.0f,0.0f));
		//m_occlusionQuery.RenderPoints( r, mc, m_frameBuffer.Width, m_frameBuffer.Height );
			
		
		//m_lightGlare.Render( br, m_viewPoint, (float) m_frameBuffer.Width, (float) m_frameBuffer.Height );
		m_lightGlare.Render( rc, m_viewPoint, m_frameBuffer );
		
		r.SetFrameBuffer( m_frameBuffer );
		//GLES20.glViewport(0,0,m_frameBuffer.Width,m_frameBuffer.Height);
		r.SetViewport(0,0,m_frameBuffer.Width,m_frameBuffer.Height);

		//GLES20.glEnable( GLES20.GL_DEPTH_TEST );
		//GLES20.glDepthMask(false);

		DepthStencilState depthEnableWriteDisable = new DepthStencilState(true, EDepthFunc.Less, false );
		r.SetDepthStencilState( depthEnableWriteDisable );
		//GLES20.glEnable(GLES20.GL_BLEND);
		//GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
		BlendState blendSrcAlphaOne = new BlendState(true,BlendFunc.SrcAlpha,BlendFunc.One);
		r.SetBlendState( blendSrcAlphaOne );
		m_starField.Render(r,mc);

		m_particle.Render(rc);


		//. anti beam field.
		//GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		//GLES20.glEnable(GLES20.GL_BLEND);
		//GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);

		r.SetBlendState(blendSrcAlphaOne);

		//GLES20.glDepthMask(false);

		r.SetDepthStencilState(depthEnableWriteDisable);

		for (GameStarShip starShip : m_starShips)
		{
			if (starShip.AntiBeamField.Damage < starShip.AntiBeamField.DamageLimit)
			{
				float radius = starShip.AntiBeamField.Radius;
				mc.SetWorld(Float4x4.Mul(Float4x4.Local(),
										 Float4x4.Scaling(Float4x4.Local(), radius, radius, radius),
										 Float4x4.Local(starShip.AntiBeamField.Transform)));
				//mc.Update();

				m_antiBeamField.SetColor(0.0f, 1.0f, 1.0f, 0.8f);
				m_antiBeamField.DrawField(r,mc,starShip.AntiBeamField);
			}
		}

		for (GameAntiBeamFieldDamage damage : m_antiBeamFieldDamages.Damages)
		{
			mc.SetWorld(damage.SphereTransform);
			//mc.Update();
			m_antiBeamField.DrawDamage(r,mc,damage);
		}

		//GLES20.glDisable(GLES20.GL_BLEND);
		//GLES20.glDepthMask(true);

		r.SetBlendState(blendDisable);
		r.SetDepthStencilState(depthEnable);


		//. 中心.
		mc.SetWorld(Float4x4.Scaling(Float4x4.Local(), 100.0f, 100.0f, 100.0f));
		SubSystem.Debug.DrawSphere(r,mc);

		/*
		 mc.SetWorld( Float4x4.Identity() );
		 Frustum frustum = new Frustum();
		 frustum.Update( Float4x4.Perspective( FMath.PI*20.0f/180.0f, 2.0f, 500.0f, 5000.0f ), Float4x4.Identity() );
		 br.SetColor( 1.0f, 1.0f, 0.0f, 1.0f );
		 frustum.Draw( br );

		 Box box = new Box();
		 box.Update(
		 new Float3(-100.0f,-100.0f,-100.0f),
		 new Float3( 100.0f, 100.0f, 100.0f ),
		 Float4x4.Mul( Float4x4.Local(),
		 Float4x4.RotationX( (float)(nrt.framework.SubSystem.Timer.FrameTime*FMath.PI/16.0)), 
		 Float4x4.RotationY( (float)(nrt.framework.SubSystem.Timer.FrameTime*FMath.PI/16.0)), 

		 Float4x4.Translation( 0.0f, 0.0f, -500.0f )) );
		 Box boxScissor = new Box();
		 box.ScissorBackFace( boxScissor, frustum.Surfaces );
		 boxScissor.Draw(br);
		 */

		/*
		 mc.SetWorld( Float4x4.Mul( Float4x4.Local(), Float4x4.Translation( Float4x4.Local(), m_player.m_f3ShieldCenter ), m_player.Transform ) );
		 mc.Update();
		 br.SetColor( 0.0f, 1.0f, 0.0f, 1.0f );
		 br.Arc( 0.0f, 0.0f, m_player.m_fShieldRadius, 32 );

		 for( int i = 0 ; i < m_player.DamagePositions.length ; i++ )
		 {
		 GamePlayer.DamagePosition dp = m_player.DamagePositions[i];

		 if( dp.IsActive )
		 {
		 mc.SetWorld( Float4x4.Mul( Float4x4.Local(), Float4x4.Translation( Float4x4.Local(), dp.Position ), m_player.Transform ) );
		 mc.Update();
		 br.SetColor( 1.0f, 0.0f, 0.0f, 1.0f );
		 br.Arc( 0.0f, 0.0f, 5.0f, 8 );				
		 }
		 }
		 */
		 
		 //. PostEffectBloom.
		 m_postEffectBloom.Render( rc, m_frameBuffer );
		 
		 r.SetFrameBuffer( m_frameBuffer );
		 
		final BitmapFont bf = rc.GetBitmapFont();
		mc.SetView(m_viewPoint.View);
		mc.SetProjection(m_viewPoint.Projection);
		//mc.SetWorld(Float4x4.Identity());
		m_player.RenderHud3D(rc, bf, m_frameBuffer, m_viewPoint, m_starShips, m_enemies);
		
		//. HUD.
		mc.SetView(Float4x4.Identity());
		mc.SetProjection(m_viewPoint.HudProjection);
		mc.SetWorld(Float4x4.Identity());

		
		m_player.RenderHud(rc, bf, m_frameBuffer, m_viewPoint, m_starShips, m_enemies);


		//. UI Controls.
		Float4x4 matrixOrtho = Float4x4.Local();
		Float4x4.Ortho(matrixOrtho, 0, ScanOutWidth, ScanOutHeight, 0, -1.0f, 1.0f);

		mc.SetView(Float4x4.Identity());
		mc.SetProjection(matrixOrtho);
		mc.SetWorld(Float4x4.Identity());

		//GLES20.glDisable(GLES20.GL_DEPTH_TEST);

		DepthStencilState depthDisable = new DepthStencilState(false, EDepthFunc.Always, false);
		r.SetDepthStencilState( depthDisable );

		bf.Begin();
		bf.SetSize(16.0f);
		bf.SetColor(RgbaColor.FromRgba(0x00ff00ff));
		bf.Draw(100.0f, 100.0f, 0.0f, String.format("SCORE %d", Score));
		bf.End();

		bf.SetSize(16.0f);
		m_player.RenderControls(rc.GetBasicRender(), bf);

		//. Debug UI.
		m_formDebug.Render(rc.GetBasicRender(), rc.GetBitmapFont());

		/*
		 GLES20.glDisable( GLES20.GL_BLEND );
		 GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA );

		 br.SetTexture(m_textureShadow);
		 br.SetSamplerState( m_samplerNearest );
		 br.Begin(GLES20.GL_TRIANGLE_STRIP, BasicRender.EShader.ColorTexture);
		 g
		 br.SetColor(1.0f, 1.0f, 1.0f, 1.0f);

		 br.SetTexcoord(0.0f, 0.0f); br.SetVertex(50.0f, 50.0f, 0.0f);
		 br.SetTexcoord(0.0f, 1.0f); br.SetVertex(50.0f, 50.0f + 256.0f, 0.0f);
		 br.SetTexcoord(1.0f, 0.0f); br.SetVertex(50.0f + 256.0f, 50.0f, 0.0f);
		 br.SetTexcoord(1.0f, 1.0f); br.SetVertex(50.0f + 256.0f, 50.0f + 256.0f, 0.0f);

		 br.End();
		 */

		//. Present backbuffer.
		r.SetFrameBuffer(null);
		//GLES20.glViewport(0, 0, ScanOutWidth, ScanOutHeight);
		r.SetViewport(0, 0, ScanOutWidth, ScanOutHeight);



		mc.SetView(Float4x4.Identity());
		mc.SetProjection(Float4x4.Identity());
		mc.SetWorld(Float4x4.Identity());

		//GLES20.glDisable(GLES20.GL_BLEND);
		//GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		r.SetBlendState( blendDisable );

		final BasicRender br = rc.GetBasicRender();

		br.SetTexture(m_frameBuffer.ColorRenderTexture);
		br.SetSamplerState(m_samplerLinear);
		br.Begin(EPrimitive.TriangleStrip, BasicRender.EShader.ColorTexture);

		br.SetColor(1.0f, 1.0f, 1.0f, 1.0f);
		/*
		 br.SetTexcoord(0.0f, 0.0f); br.SetVertex(100.0f, 100.0f, 0.0f);
		 br.SetTexcoord(0.0f, 1.0f); br.SetVertex(100.0f, 100.0f + 512.0f, 0.0f);
		 br.SetTexcoord(1.0f, 0.0f); br.SetVertex(100.0f + 512.0f, 100.0f, 0.0f);
		 br.SetTexcoord(1.0f, 1.0f); br.SetVertex(100.0f + 512.0f, 100.0f + 512.0f, 0.0f);
		 */
		float u = ((float)m_frameBuffer.ColorRenderTexture.GetNpotWidth()) / ((float)m_frameBuffer.ColorRenderTexture.GetPotWidth());
		float v = ((float)m_frameBuffer.ColorRenderTexture.GetNpotHeight()) / ((float)m_frameBuffer.ColorRenderTexture.GetPotHeight());
		br.SetTexcoord(0.0f, 0.0f); br.SetVertex(-1.0f, -1.0f, 0.0f);
		br.SetTexcoord(0.0f, v*2);    br.SetVertex(-1.0f, 3.0f, 0.0f);
		br.SetTexcoord(u*2,  0.0f);    br.SetVertex(3.0f, -1.0f, 0.0f);
		//br.SetTexcoord(u,    0.0f); br.SetVertex(1.0f, -1.0f, 0.0f);
		br.End();


	}

	public Model LoadModel(DelayResourceQueue drq, String strPath)
	//throws ThreadForceDestroyException
	{
		InputStream stream = SubSystem.Loader.OpenAsset(strPath);
		Model.Loader loader = new Model.Loader(stream);
		Model model = loader.ReadModel(drq);
		loader.Close();	
		return model;
	}

	public CollisionMesh LoadCollision(String strPath)
	{
		InputStream stream = SubSystem.Loader.OpenAsset(strPath);
		CollisionLoader collisionLoader = new CollisionLoader(stream);
		CollisionMesh collisionMesh = collisionLoader.ReadMesh();
		collisionLoader.Close();

		return collisionMesh;
	}
}
