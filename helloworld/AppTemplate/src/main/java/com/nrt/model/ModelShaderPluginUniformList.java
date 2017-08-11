package com.nrt.model;
import com.nrt.render.*;

public interface ModelShaderPluginUniformList
{
	public void OnCreateUniforms( DelayResourceQueue drq, Program program ); //throws ThreadForceDestroyException;
}
