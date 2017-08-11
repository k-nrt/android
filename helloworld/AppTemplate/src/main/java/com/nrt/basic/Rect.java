package com.nrt.basic;

public class Rect
{
	public float X = 0.0f;
	public float Y = 0.0f;
	public float Width = 0.0f;
	public float Height = 0.0f;
	
	public Rect(){}
	public Rect(float x, float y, float w, float h)
	{
		X = x;
		Y = y;
		Width = w;
		Height = h;
	}
	
	public Rect( Rect rect )
	{
		this( rect.X, rect.Y, rect.Width, rect.Height );
	}
	
	public final boolean IsIntersect(float x, float y)
	{
		if (X <= x && x < (X + Width) && Y <= y && y < (Y + Height))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public final boolean IsInterect( Rect rect )
	{		
		if( ((rect.X + rect.Width) < X) || ((X+Width) < rect.X )
		|| ((rect.Y + rect.Height) < Y) || ((Y+Height) < rect.Y ) )
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public final Rect Set( Rect s )
	{
		X = s.X;
		Y = s.Y;
		Width = s.Width;
		Height = s.Height;
		return this;
	}

	public final Rect Set(float x, float y, float w, float h)
	{
		X = x;
		Y = y;
		Width = w;
		Height = h;
		return this;
	}
	
	public final float GetLeft() { return X; }
	public final float GetRight() { return X + Width; }
	public final float GetTop() { return Y; }
	public final float GetBottom() { return Y + Height; }
}
