package com.nrt.input;

import android.view.MotionEvent;


public class DevicePointer
{
	public static class Pointer
	{
		public float X = 0.0f;
		public float Y = 0.0f;
		public boolean Down = false;
		public int ID = 0;
		public Pointer()
		{}
		public Pointer(int id)
		{
			ID = id;
		}
	}

	public static Pointer[] Pointers = 
	{
		new Pointer(0),
		new Pointer(1),
		new Pointer(2),
		new Pointer(3),
		new Pointer(4),
	};

	public static void OnTouchEvent(MotionEvent me)
	{
		int nbPointers = me.getPointerCount();
		int action = me.getActionMasked();

		int i = me.getActionIndex();
		int id = me.getPointerId(i);

		if (id < Pointers.length)
		{
			switch (action)
			{
				case MotionEvent.ACTION_DOWN:
					Pointers[id].Down = true;
					Pointers[id].X = me.getX();
					Pointers[id].Y = me.getY();
					break;

				case MotionEvent.ACTION_UP:
					Pointers[id].Down = false;
					Pointers[id].X = me.getX();
					Pointers[id].Y = me.getY();
					break;

				case MotionEvent.ACTION_POINTER_DOWN:
					Pointers[id].Down = true;
					Pointers[id].X = me.getX(i);
					Pointers[id].Y = me.getY(i);
					break;

				case MotionEvent.ACTION_POINTER_UP:
					Pointers[id].Down = false;
					Pointers[id].X = me.getX(i);
					Pointers[id].Y = me.getY(i);
					break;

				case MotionEvent.ACTION_MOVE:
					for (int j = 0 ; j < nbPointers ; j++)
					{
						id = me.getPointerId(j);
						if (id < Pointers.length)
						{
							Pointers[id].X = me.getX(j);
							Pointers[id].Y = me.getY(j);
						}

					}
					break;
			}
		}
	}
}
	

