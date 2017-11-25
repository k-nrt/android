package com.nrt.render;

import android.opengl.*;

import com.nrt.basic.DebugLog;
import android.app.*;


public class Uniform extends RenderResource
{
	public Program Program = null;
	public String Name = null;

	public int Index = -1;

	public Uniform(DelayResourceQueue drq, Program program, String name)
	{
		Program = program;
		Name = name;
		Index = -1;

		if (drq != null)
		{
			drq.Add(this);
		}
		else
		{
			Generate();
		}
	}

	@Override public void Generate()
	{
		if (Program.Name == 0)
		{
			return;
		}

		Index = GLES20.glGetUniformLocation(Program.Name, Name);
		if (Index < 0)
		{
			DebugLog.Error.WriteLine(String.format("uniform not found %d %s", Index, Name));
		}
		else
		{
			DebugLog.Error.WriteLine(String.format("apply uniform %d %s", Index, Name));
		}
	}

	@Override public void Delete()
	{
		Index = -1;
	}
}
