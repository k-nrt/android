package com.nrt.render;


public class FragmentShader extends Shader
{
	public FragmentShader()
	{}
	
	public FragmentShader( DelayResourceQueue queue, String[] arrayLines)
	//throws ThreadForceDestroyException
	{
		super( queue, Shader.EType.Fragment, arrayLines );
		//super(Shader.EType.Fragment, arraySource);
	}
	
	/*	
	public static void Create( FragmentShader fragmentShader, String strSource )
	{
		Shader.Create( fragmentShader, Shader.EType.Fragment, strSource );
	}
	*/
}

