package com.nrt.render;

public class Sampler extends Uniform
{
	public int TextureUnit = -1;

	public void Initialize(DelayResourceQueue queue, Program program, int iTextureUnit, String strName)
	{
		TextureUnit = iTextureUnit;
		super.Initialize(queue, program, strName);
	}

	public Sampler()
	{}

	public Sampler( DelayResourceQueue queue, Program program, int iTextureUnit, String name)
	{
		Initialize( queue, program, iTextureUnit, name );
	}
}

