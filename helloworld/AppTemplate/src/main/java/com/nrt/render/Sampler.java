package com.nrt.render;

public class Sampler extends Uniform
{
	public int TextureUnit = -1;

	public Sampler(DelayResourceQueue drq, Program program, int iTextureUnit, String strName)
	{
		super(drq, program, strName);
		TextureUnit = iTextureUnit;
	}
}

