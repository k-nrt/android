package com.nrt.render;

import android.opengl.GLES20;

public class BlendState
{
	public boolean EnableBlend = false;
	public BlendFunc SrcColor = BlendFunc.One;
	public BlendFunc DstColor = BlendFunc.Zero;
	public BlendEquation ColorEquation = BlendEquation.Add;
	
	public BlendFunc SrcAlpha = BlendFunc.One;
	public BlendFunc DstAlpha = BlendFunc.Zero;
	public BlendEquation AlphaEquation = BlendEquation.Add;
	
	public float BlendColorRed = 0.0f;
	public float BlendColorGreen = 0.0f;
	public float BlendColorBlue = 0.0f;
	public float BlendColorAlpha = 0.0f;
	
	public boolean EnableAlphaToCoverage = false;
	
	public boolean ColorMaskRed = true;
	public boolean ColorMaskGreen = true;
	public boolean ColorMaskBlue = true;
	public boolean ColorMaskAlpha = true;
	
	public BlendState()
	{}
	
	public BlendState(boolean enableBlend, BlendFunc srcColor, BlendFunc dstColor)
	{
		EnableBlend = enableBlend;
		SrcColor = srcColor;
		DstColor = dstColor;
	}
	
	public BlendState
	(
		boolean enableBlend, BlendFunc srcColor, BlendFunc dstColor,
		boolean colorMaskRed, 
		boolean colorMaskGreen, 
		boolean colorMaskBlue, 
		boolean colorMaskAlpha
	)
	{
		EnableBlend = enableBlend;
		SrcColor = srcColor;
		DstColor = dstColor;
		ColorMaskRed = colorMaskRed;
		ColorMaskGreen = colorMaskGreen;
		ColorMaskBlue = colorMaskBlue;
		ColorMaskAlpha = colorMaskAlpha;
	}
	
	public BlendState
	(
		boolean enableBlend,
		BlendFunc srcColor, BlendFunc dstColor, BlendFunc srcAlpha, BlendFunc dstAlpha )
	{
		EnableBlend = enableBlend;
		SrcColor = srcColor;
		DstColor = dstColor;
		SrcAlpha = srcAlpha;
		DstAlpha = dstAlpha;
	}
	
	public BlendState
	(
		boolean enableBlend, 
		BlendFunc srcColor, BlendFunc dstColor, BlendEquation colorEquation,
		BlendFunc srcAlpha, BlendFunc dstAlpha, BlendEquation alphaEquation,
		float blendColorRed, float blendColorGreen, float blendColorBlue, float blendColorAlpha,
		boolean enableAlphaToCoverage,
		boolean colorMaskRed, boolean colorMaskGreen, boolean colorMaskBlue, boolean colorMaskAlpha
	)
	{
		EnableBlend = enableBlend;
		SrcColor = srcColor;
		DstColor = dstColor;
		ColorEquation = colorEquation;
		
		SrcAlpha = srcAlpha;
		DstAlpha = dstAlpha;
		AlphaEquation = alphaEquation;
		
		BlendColorRed = blendColorRed;
		BlendColorGreen = blendColorGreen;
		BlendColorBlue = blendColorBlue;
		BlendColorAlpha = blendColorAlpha;
		
		EnableAlphaToCoverage = enableAlphaToCoverage;
		
		ColorMaskRed = colorMaskRed;
		ColorMaskGreen = colorMaskGreen;
		ColorMaskBlue = colorMaskBlue;
		ColorMaskAlpha = colorMaskAlpha;
	}
}


