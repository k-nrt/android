package com.nrt.render;

public class VertexShader extends Shader
{
	public VertexShader( DelayResourceQueue drq, String[] arrayLines)
	{
		super( drq, Shader.EType.Vertex, arrayLines);
	}
}

