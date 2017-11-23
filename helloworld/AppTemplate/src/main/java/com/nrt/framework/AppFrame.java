package com.nrt.framework;
import com.nrt.render.*;

public interface AppFrame
{
	public void OnCreate(DelayResourceQueue drq);
	public void OnSurfaceChanged(int width, int height);
	public void OnUpdate();
	public void OnRender(RenderContext rc);
}
