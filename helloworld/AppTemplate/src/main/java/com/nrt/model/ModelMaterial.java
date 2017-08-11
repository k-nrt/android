package com.nrt.model;

import com.nrt.math.Float4;

public class ModelMaterial
{
	public String Name = "";
	public String Shader = "";
	public String TechniqueName = "";
	public final Float4 Diffuse = new Float4( 0.0f );
	public final Float4 Ambient = new Float4( 0.0f );
	public final Float4 Specular = new Float4( 0.0f );
	public final Float4 Emission = new Float4( 0.0f );
	public float Power = 0.0f;
	public float FresnelIndex = 0.0f;
	public int MeshStart = 0;
	public int MeshCount = 0;
	public String DiffuseAlpha = "";
	public String AmbientOcclusion = "";
	public String Normal = "";
	
	public ModelShaderSet.EType Technique = ModelShaderSet.EType.NoTexture;
}

