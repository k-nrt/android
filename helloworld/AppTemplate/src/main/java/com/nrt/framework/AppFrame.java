package com.nrt.framework;

public interface AppFrame
{
	public void OnSurfaceChanged(int width, int height);
	public void OnUpdate();
	public void OnRender(RenderContext rc);
}
