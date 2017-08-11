package com.nrt.basic;

import java.nio.ByteOrder;
import com.nrt.math.Float4;

public class RgbaColor
{
	
	public final static int FromRgba( Float4 rgba )
	{
		return FromRgba( rgba.X, rgba.Y, rgba.Z, rgba.W );
	}

	public final static int FromRgba( float r, float g, float b, float a)
	{
		return FromRgba(
			(int) (r*255.0f) & 0xff, 
			(int) (g*255.0f) & 0xff, 
			(int) (b*255.0f) & 0xff, 
			(int) (a*255.0f) & 0xff );
	}
	
	public final static int FromRgba(int r, int g, int b, int a)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN)
		{
			return a | b << 8 | g << 16 | r << 24;
		}
		else
		{
			return r | g << 8 | b << 16 | a << 24;
		}
	}

	public final static int FromRgba(int rgba)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN)
		{
			return rgba;
		}
		else
		{
			return ((rgba & 0xff000000) >> 24)
				| ((rgba & 0xff0000) >> 8)
				| ((rgba & 0xff00) << 8)
				| ((rgba & 0xff) << 24);
		}
	}
	
	public final static Float4 ToFloat4( Float4 dst, int rgba )
	{
		if (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN)
		{
			return dst.Set
			(
				((float)((rgba >> 24) & 0xff))/255.0f,
				((float)((rgba >> 16) & 0xff))/255.0f,
				((float)((rgba >> 8) & 0xff))/255.0f,
				((float)(rgba & 0xff))/255.0f
			);		}
		else
		{
			return dst.Set
			(
				((float)(rgba & 0xff))/255.0f,
				((float)((rgba >> 8) & 0xff))/255.0f,
				((float)((rgba >> 16) & 0xff))/255.0f,
				((float)((rgba >> 24) & 0xff))/255.0f
			);
		}
	}
	public final static int Lerp( float fLerp, int rgba0, int rgba1 )
	{
		Float4 f4Rgba = Float4.Lerp( Float4.Local(), fLerp, ToFloat4( Float4.Local(), rgba0 ), ToFloat4( Float4.Local(), rgba1 ) );
		return FromRgba( f4Rgba );
	}
	

	public final static int Black = 0xff000000;
	public final static int Red = 0xff0000ff;
	public final static int Green = 0xff00ff00;
	public final static int Blue = 0xffff0000;
	public final static int Yellow = 0xff00ffff;
	public final static int Cyan = 0xffffff00;
	public final static int Magenta = 0xffff00ff;
	public final static int White = 0xffffffff;




}
