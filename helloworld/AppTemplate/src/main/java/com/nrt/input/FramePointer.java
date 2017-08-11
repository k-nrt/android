package com.nrt.input;

import com.nrt.math.Float3;

public class FramePointer
{
	public static class Pointer
	{
		public Float3 Position = new Float3();
		public Float3 PrevPosition = new Float3();

		public boolean Down = false;
		public boolean Up = true;
		public boolean Push = false;
		public boolean Release = false;

		public boolean PrevDown = false;
		public boolean PrevUp = true;

		public float Time = 0.0f;
		
		public PositionHistory History = new PositionHistory( 4 );
	}

	public Pointer[] Pointers = null;

	public FramePointer()
	{
		Pointers = new Pointer[DevicePointer.Pointers.length];
		for (int i = 0 ; i < Pointers.length ; i++)
		{
			Pointers[i] = new Pointer();
		}
	}

	public void Update( float fElapsedTime )
	{
		for (int i = 0 ; i < Pointers.length ; i++)
		{
			Pointer pointer = Pointers[i];
			pointer.PrevPosition.Set( pointer.Position );
			pointer.PrevDown = pointer.Down;
			pointer.PrevUp = pointer.Up;
			pointer.Position.X = DevicePointer.Pointers[i].X;
			pointer.Position.Y = DevicePointer.Pointers[i].Y;
			pointer.Down = DevicePointer.Pointers[i].Down;

			pointer.Up  = !pointer.Down;

			pointer.Push = (pointer.PrevUp && pointer.Down ? true : false);
			pointer.Release = (pointer.PrevDown && pointer.Up ? true : false);

			if( pointer.Push )
			{
				pointer.PrevPosition.Set( pointer.Position );
				pointer.History.Reset();
				pointer.Time = 0.0f;
			}

			if( pointer.Down )
			{
				pointer.History.UpdatePosition( pointer.Position, fElapsedTime );
				pointer.Time += fElapsedTime;
			}
		}
	}
}
	
