package com.nrt.render;

import android.opengl.*;

import com.nrt.basic.DebugLog;
import android.app.*;


public class Uniform extends RenderResource
{
	public Program Program = null;
	public String Name = null;

	public int Index = -1;
	
	public void Initialize( DelayResourceQueue queue, Program program, String strName )
		//throws ThreadForceDestroyException
	{
		Program = program;
		Name = strName;
		Index = -1;
		
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
		if (Program.Name == 0)
		{
			return;
		}

		Index = GLES20.glGetUniformLocation(Program.Name, Name);
		if( Index < 0 )
		{
			DebugLog.Error.WriteLine( String.format( "uniform not found %d %s", Index, Name ));
		}
		else
		{
			DebugLog.Error.WriteLine( String.format( "apply uniform %d %s", Index, Name ));
		}
	}
	
	public Uniform()
	{}

	/*
	public Uniform(Program program, String name)
	throws ThreadForceDestroyException
	{
		Initialize( null, program, name );
		//Create( this, program, name );
		//Name = name;
		//BindLocation( program );
	}
	*/
	public Uniform( DelayResourceQueue queue, Program program, String name)
		//throws ThreadForceDestroyException
	{
		Initialize( queue, program, name );
		//Create( this, program, name );
		//Name = name;
		//BindLocation( program );
	}
	
	
	/*
	public Uniform( String name )
	{
		Name = name;
	}
	*/
	
	/*
	public void BindLocation( Program program )
	{
		if (program.Name == 0)
		{
			return;
		}

		Index = GLES20.glGetUniformLocation(program.Name, Name);
		if( Index < 0 )
		{
			DebugLog.Error.WriteLine( String.format( "uniform not found %d %s", Index, Name ));
		}
	}
	
	public static void Create(Uniform uniform, Program program, String name)
	{
		uniform.Name = name;
		uniform.BindLocation( program );
	}
	*/
}
