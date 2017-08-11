package com.nrt.model;

import com.nrt.render.*;

public interface ModelShaderPluginParameterList
{
	public void OnUpdateParameter( GfxCommandContext r, MatrixCache mc, ModelShaderPluginUniformList uniform, int iMaterial, ModelMaterial material );
}
