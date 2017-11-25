package com.nrt.render;
import com.nrt.framework.*;

public class DelayResourceQueueMarker extends RenderResource
{
	public boolean Done = false;
	public String Name = null;
	public DelayResourceQueueMarker(String strName)
	{
		Name = strName;
	}
	
	public void Reset()
	{
		Done = false;
	}

	@Override public void Generate()
	{
		Done = true;
		SubSystem.Log.WriteLine( Name + " marker is done." );
	}
}
