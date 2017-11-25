package com.nrt.render;

import android.opengl.GLES20;

public class Program extends RenderResource
{
	public VertexShader VertexShader = null;
	public FragmentShader FragmentShader = null;
	public AttributeBinding[] AttributeBindings = null;

	public Program( DelayResourceQueue drq, AttributeBinding[] attributeBindings, VertexShader vs, FragmentShader fs)
	{
		VertexShader = vs;
		FragmentShader = fs;

		AttributeBindings = attributeBindings;

		if( drq != null )
		{
			drq.Add( this );
		}
		else
		{
			Generate();
		}
	}

	@Override public void Generate()
	{		
		if (VertexShader.Name == 0 || FragmentShader.Name == 0)
		{
			com.nrt.basic.DebugLog.Error.WriteLine( "can not create program" );
			return;
		}
		Name = GLES20.glCreateProgram();
		com.nrt.basic.DebugLog.Error.WriteLine( String.format( "apply program %d vs=%d fs=%d", Name, VertexShader.Name, FragmentShader.Name) );
		
		GLES20.glAttachShader(Name, VertexShader.Name); 
		GLES20.glAttachShader(Name, FragmentShader.Name);

		for (int i = 0 ; i < AttributeBindings.length ; i++)
		{
			GLES20.glBindAttribLocation(Name,
										AttributeBindings[i].Index,
										AttributeBindings[i].Name);
		}

		GLES20.glLinkProgram(Name);
	}

	@Override public void Delete()
	{
		if( 0 < Name )
		{
			GLES20.glDeleteProgram(Name);
		}
		Name = 0;
	}
}

