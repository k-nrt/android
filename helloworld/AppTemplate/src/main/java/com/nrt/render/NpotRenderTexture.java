package com.nrt.render;
/*
import android.opengl.*;

public class NpotRenderTexture extends RenderTexture implements NpotTexture
{
	public int TextureWidth = 0;
	public int TextureHeight = 0;
	
	public NpotRenderTexture( RenderTextureFormat eFormat, int width, int height )
	{
		super( eFormat, GetPotSize(width), GetPotSize(height));
		TextureWidth = PotWidth;
		TextureHeight = PotHeight;
		PotWidth = width;
		PotHeight = height;
	}
	
	private static final int GetPotSize( int a )
	{
		int r;
		for( r=1 ; r < a ; r <<= 1);
		return r;
	}

	@Override
	public int GetTextureWidth()
	{
		// TODO: Implement this method
		return TextureWidth;
	}

	@Override
	public int GetTextureHeight()
	{
		// TODO: Implement this method
		return TextureHeight;
	}

	@Override
	public void Resize(int width, int height)
	{
		super.Resize( GetPotSize(width), GetPotSize(height));
		TextureWidth = PotWidth;
		TextureHeight = PotHeight;
		PotWidth = width;
		PotHeight = height;
	}
}
*/
