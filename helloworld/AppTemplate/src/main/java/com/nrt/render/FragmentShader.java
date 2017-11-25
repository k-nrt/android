package com.nrt.render;


public class FragmentShader extends Shader
{
	public FragmentShader( DelayResourceQueue drq, String[] arrayLines)
	{
		super( drq, Shader.EType.Fragment, arrayLines );
	}
}

