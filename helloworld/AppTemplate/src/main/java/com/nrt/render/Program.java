package com.nrt.render;

import android.opengl.GLES20;

public class Program extends RenderResource
{
	public VertexShader VertexShader = null;
	public FragmentShader FragmentShader = null;
	public AttributeBinding[] AttributeBindings = null;
	
	public void Initialize
	(
		DelayResourceQueue queue,
		AttributeBinding[] attributeBindings, 
		VertexShader vs,
		FragmentShader fs
	)
		//throws ThreadForceDestroyException
	{
		VertexShader = vs;
		FragmentShader = fs;
		
		AttributeBindings = attributeBindings;
		
		if( queue == null )
		{
			Apply();
		}
		else
		{
			queue.Add( this );
		}
	}

	@Override
	public void Apply()
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
	
	public Program()
	{}

	public Program( DelayResourceQueue queue, AttributeBinding[] attributeBindings, VertexShader vs, FragmentShader fs)
		//throws ThreadForceDestroyException
	{
		Initialize( queue, attributeBindings, vs, fs );
		//Create( this, attributeBindings, vs, fs );
	}
	
	/*
	
	public static void Create(Program program, AttributeBinding[] attributeBindings,
		String[] arrayVsSource, String[] arrayFsSource )
	{
		Create( program, attributeBindings,
			new VertexShader( arrayVsSource ), new FragmentShader( arrayFsSource ) );
	}
	
	public static void Create(Program program, AttributeBinding[] attributeBindings, VertexShader vs, FragmentShader fs)
	{
		if (vs.Name == 0 || fs.Name == 0)
		{
			return;
		}
		program.Name = GLES20.glCreateProgram();
		GLES20.glAttachShader(program.Name, vs.Name); 
		GLES20.glAttachShader(program.Name, fs.Name);

		for (int i = 0 ; i < attributeBindings.length ; i++)
		{
			GLES20.glBindAttribLocation(program.Name,
										attributeBindings[i].Index,
										attributeBindings[i].Name);
		}

		GLES20.glLinkProgram(program.Name);
		
		program.VertexShader = vs;
		program.FragmentShader = fs;
		program.AttributeBindings = attributeBindings;
	}
	*/
	
}

