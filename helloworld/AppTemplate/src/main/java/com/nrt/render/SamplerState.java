package com.nrt.render;

import android.opengl.*;


public class SamplerState
{
	public EMagFilter MagFilter = EMagFilter.Nearest;
	public EMinFilter MinFilter = EMinFilter.Nearest;

	public EWrap WrapS = EWrap.Repeat;
	public EWrap WrapT = EWrap.Repeat;

	public SamplerState() {}

	public SamplerState( EMagFilter eMagFilter, EMinFilter eMinFilter, EWrap eWrapS, EWrap eWrapT )
	{
		MagFilter = eMagFilter;
		MinFilter = eMinFilter;
		WrapS = eWrapS;
		WrapT = eWrapT;
	}

	public int GetMagFilter()
	{
		return MagFilter.Value;
	}

	public int GetMinFilter()
	{
		return MinFilter.Value;
	}
}
