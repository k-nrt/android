package com.nrt.ui;

import com.nrt.font.BitmapFont;
import com.nrt.math.Float3;
import com.nrt.math.Float4x4;
import com.nrt.math.Quaternion;
import com.nrt.math.FMath;

import com.nrt.input.FramePointer;

import com.nrt.render.BasicRender;
import com.nrt.render.GfxCommandContext;
import com.nrt.render.MatrixCache;
import com.nrt.input.DevicePointer;

public class UiDirectionPad extends UiRoundButton
{
	public static class PointerInfo
	{
		public float Time = 0.0f;
		public final Float3 FirstPosition = new Float3();
		public final Float3 Position = new Float3();

		public FramePointer.Pointer Pointer = new FramePointer.Pointer();
		public boolean PrevOwner = false;

		public final Float3 Direction = new Float3(0.0f);

		public enum EStatus
		{
			Up,
			Tap,
			Hold,
			Slide,
			LeaveFlick,
			ReleaseFlick,
		};

		public EStatus Status = EStatus.Up;

		public float HoldTime = 0.0f;

		public void Update(float fElapsedTime, FramePointer.Pointer pointer, boolean owner)
		{	
			float fTapTime = 0.15f;
			float fHoldDistance = 16.0f;
			float fFlickSpeed = 4.0f;

			Pointer = pointer;
			if (owner)
			{
				Position.Set( pointer.Position );
				if (pointer.Push)
				{
					Time = 0.0f;
					HoldTime = 0.0f;
					Status = EStatus.Up;
					FirstPosition.Set( pointer.Position);
					Direction.Set( 0.0f );
				}
				else if (pointer.Down)
				{
					Time += fElapsedTime;

					float fDistance = Float3.Distance(FirstPosition, pointer.Position);

					if (fDistance > fHoldDistance)
					{
						Status = EStatus.Slide;
						HoldTime = 0.0f;
					}
					else
					{
						HoldTime += fElapsedTime;
					}

					if (Status == EStatus.Slide)
					{
						Float3.SubNormalize( Direction, pointer.Position, FirstPosition);
					}

					if (HoldTime > fTapTime)
					{
						Status = EStatus.Hold;
					}

				}
			} 
			else if (PrevOwner && pointer.Release)
			{
				//System.Error.WriteLine("release");
				float fDistance = Float3.Distance(FirstPosition, pointer.Position);
				float fSpeed = pointer.History.AverageSpeed;
				fSpeed /= 60.0f;

				if (Status == EStatus.Up && Time <= fTapTime && fDistance < fHoldDistance)
				{
					Status = EStatus.Tap;
					Time = 0.0f;
				}
				else if( fSpeed > fFlickSpeed )
				{
					Float3.SubNormalize( Direction, pointer.Position, FirstPosition);
					//System.Error.WriteLine( String.format("Release flick %f %f %f", fSpeed, Direction.X, Direction.Y) );
					Status = EStatus.ReleaseFlick;
					Time = 0.0f;
				}
				else
				{
					Status = EStatus.Up;
					Time = 0.0f;
				}
			}
			else if(PrevOwner && pointer.Down )
			{
				Float3.SubNormalize( Direction, pointer.Position, FirstPosition);	
				//System.Error.WriteLine( String.format("leave flick %f %f", Direction.X, Direction.Y) );
				Status = EStatus.LeaveFlick;
				Time = 0.0f;					
			}
			else
			{
				Status = EStatus.Up;
			}


			PrevOwner  = owner;
		}
	}

	public PointerInfo[] PointerInfos = new PointerInfo[DevicePointer.Pointers.length];

	public boolean Down = false;
	public boolean Tap = false;
	public boolean Hold = false;

	public final Float3 Direction = new Float3(0.0f);
	public boolean Slide = false;

	public boolean ReleaseFlick = false;
	public boolean LeaveFlick = false;
	
	public float Time = 0.0f;
	

	public UiDirectionPad(float x, float y, float radius)
	{
		super(x, y, radius);

		for (int i = 0 ; i < PointerInfos.length ; i++)
		{
			PointerInfos[i] = new PointerInfo();
		}
	}

	final Quaternion Rotation = Quaternion.LoadIdentity( new Quaternion() );

	@Override
	public void OnUpdate(float fElapsedTime)
	{
		super.OnUpdate(fElapsedTime);

		int nbDowns = 0;
		int nbTaps = 0;			
		int nbHolds = 0;
		int nbSlides = 0;
		int nbReleaseFlicks = 0;
		int nbLeaveFlicks = 0;
		final Float3 f3Direction = Float3.Local(0.0f);
		Quaternion.LoadIdentity( Rotation );
		for (int i = 0 ; i < PointerInfos.length ; i++)
		{
			PointerInfo info = PointerInfos[i];
			PointerInfos[i].Update(fElapsedTime, Pointers[i], Owners[i]);

			if (PointerInfos[i].Status == PointerInfo.EStatus.Tap)
			{
				nbTaps++;

			}

			if(Pointers[i].Down && Owners[i] )
			{
				nbDowns++;
			}

			if (PointerInfos[i].Status == PointerInfo.EStatus.Hold)
			{
				nbHolds++;
			}

			boolean isCalcDirection = false;
			if (PointerInfos[i].Status == PointerInfo.EStatus.Slide)
			{
				nbSlides++;
				isCalcDirection = true;
			}

			if (PointerInfos[i].Status == PointerInfo.EStatus.ReleaseFlick)
			{
				nbReleaseFlicks++;
				isCalcDirection = true;
			}

			if (PointerInfos[i].Status == PointerInfo.EStatus.LeaveFlick)
			{
				nbLeaveFlicks++;
				isCalcDirection = true;
			}

			if( isCalcDirection)
			{
				Float3.Add(f3Direction, f3Direction, PointerInfos[i].Direction);

				Float3 v3Prev = Float3.Local().Set( 
					(info.FirstPosition.X - EnterCenter.X) / EnterRadius,
					(info.FirstPosition.Y - EnterCenter.Y) / EnterRadius,
					0.0f);//new Vector3( (PrevPoint.Position - Center)/Radius, 0.0f );

				Float3 v3Current = Float3.Local().Set(
					(info.Position.X - EnterCenter.X) / EnterRadius, 
					(info.Position.Y - EnterCenter.Y) / EnterRadius,
					0.0f);//new Float3( (Point.Position - Center)/Radius, 0.0f );

				float len = (v3Prev.X * v3Prev.X + v3Prev.Y * v3Prev.Y);
				if( len > 1.0f )
				{
					Float3.Normalize( v3Prev, v3Prev );
				}
				else
				{
					v3Prev.Z = FMath.Sqrt(1.0f - len );
					Float3.Normalize( v3Prev, v3Prev);
				}

				len = (v3Current.X * v3Current.X + v3Current.Y * v3Current.Y);
				if( len > 1.0f )
				{
					Float3.Normalize( v3Current, v3Current );
				}
				else
				{
					v3Current.Z = FMath.Sqrt(1.0f - len);
					Float3.Normalize( v3Current, v3Current);
				}


				Quaternion.LoadRotation( Rotation, v3Current, v3Prev );
			}

		}

		Down = ((nbDowns > 0) ? true : false );
		Tap = ((nbTaps > 0) ? true : false);
		Hold = ((nbHolds > 0) ? true : false);

		Slide = ((nbSlides > 0) ? true : false);
		ReleaseFlick = ((nbReleaseFlicks > 0 && nbDowns <= 0) ? true : false);
		LeaveFlick = ((nbLeaveFlicks > 0 && nbDowns <= 0 ) ? true : false);
		Slide = ((nbSlides > 0) ? true : false);
		if (Slide || ReleaseFlick || LeaveFlick )
		{
			Float3.Normalize( Direction, f3Direction);
//				System.Error.WriteLine( String.format( "%f %f", Direction.X, Directinon.Y ) );
		}
		
		Time += fElapsedTime;

		//System.Error.WriteLine(Boolean.toString(Hold));
	}

	@Override
	public void OnRender(final BasicRender br, final BitmapFont bf)
	{
		UiTrackBall.Render(br,Time,EnterCenter,EnterRadius,Float4x4.Rotation( Float4x4.Local(), Rotation ));
		/*
		if (m_nbPrevPointers < m_nbPointers)
		{
			br.SetColor(0.5f, 1.0f, 0.75f, 1);
		}
		else if (m_nbPointers > 0)
		{
			br.SetColor(0.2f, 1.0f, 0.5f, 1);			
		}
		else
		{
			br.SetColor(0, 0.5f, 0, 1);
		}

		br.Arc(EnterCenter.X, EnterCenter.Y, EnterRadius, 16);

		MatrixCache mc = br.GetMatrixCache();

		for (int i = -2 ; i <= 2 ; i++)
		{
			float z = (0.5f + (float)i) / 2.0f;
			float r = EnterRadius - 2.0f;
			float s = FMath.Sqrt(1.0f - z * z);
			Float4x4 matrix = Float4x4.Local();

			Float4x4.Mul(
				matrix,
				Float4x4.Scaling( Float4x4.Local(), s, s, 1.0f),
				Float4x4.Translation( Float4x4.Local(), 0.0f, 0.0f, z),
				Float4x4.Rotation( Float4x4.Local(), Rotation ), 
				Float4x4.Scaling( Float4x4.Local(),  r, r, 1.0f),
				Float4x4.Translation(Float4x4.Local(), EnterCenter)
			);

			mc.SetWorld(matrix);
			//mc.Update();
			br.Arc(0.0f, 0.0f, 1.0f, 8);
		}
		mc.SetWorld(Float4x4.Identity());
		//mc.Update();
		*/
	}
}
