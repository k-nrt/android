package com.nrt.render;

public class VertexShader extends Shader
{
	public VertexShader()
	{}
	
	public VertexShader( DelayResourceQueue queue, String[] arrayLines)
		//throws ThreadForceDestroyException
	{
		//Create( this, ConbineSourceLines( arrayLines ) );
		super( queue, Shader.EType.Vertex, arrayLines);
	}
	/*
	public static void Create( VertexShader vertexShader, String strSource )
	{
		Shader.Create( vertexShader, Shader.EType.Vertex, strSource );
	}
	*/
}

