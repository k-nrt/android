package com.nrt.model;

import com.nrt.render.*;

public class ModelShader
{
	public Program Program = null;
	public Uniform u_worldViewProjection = null;
	public Uniform u_world = null;
	public Uniform u_viewPosition = null;

	public Uniform u_ambient = null;
	public Uniform u_diffuse = null;
	public Uniform u_specular = null;
	public Uniform u_emission = null;

	public Uniform u_power = null;

	public Uniform u_lightAmbient = null;
	public Uniform u_lightColor = null;
	public Uniform u_lightDirection = null;

	public Uniform u_skyColor = null;
	public Uniform u_horizonColor = null;
	public Uniform u_earthColor = null;
	public Uniform u_skyDirection = null;

	public Uniform u_fogNear = null;
	public Uniform u_fogFar = null;
	public Uniform u_fogColor = null;
	
	public Uniform u_transformsX = null;
	public Uniform u_transformsY = null;
	public Uniform u_transformsZ = null;
	public Uniform u_indexOffset = null;
	
	public Uniform u_shadowProjection = null;	
	public Uniform u_shadowDirection = null;
	public Sampler u_samplerShadow = null;		
	
	public ModelShaderPluginUniformList Uniforms = null;

	public ModelShader()
	{}

	public ModelShader
	(
		DelayResourceQueue drq, 
		AttributeBinding[] ab, 
		VertexShader vs, FragmentShader fs,
		ModelShaderPluginUniformListFactory uniformListFactoty 
	)
	//throws ThreadForceDestroyException
	{
		Program = new Program( drq, ab, vs, fs);

		u_worldViewProjection = new Uniform( drq, Program, "u_worldViewProjection");
		u_world = new Uniform( drq, Program, "u_world");
		u_viewPosition = new Uniform( drq, Program, "u_viewPosition");
		u_ambient = new Uniform( drq, Program, "u_ambient");
		u_diffuse = new Uniform( drq, Program, "u_diffuse");
		u_specular = new Uniform( drq, Program, "u_specular");
		u_emission = new Uniform( drq, Program, "u_emission");

		u_power = new Uniform( drq, Program, "u_power");

		u_lightAmbient = new Uniform( drq, Program, "u_lightAmbient");
		u_lightColor = new Uniform( drq, Program, "u_lightColor");
		u_lightDirection = new Uniform( drq, Program, "u_lightDirection");

		u_skyColor = new Uniform( drq, Program, "u_skyColor");
		u_horizonColor = new Uniform( drq, Program, "u_horizonColor");
		u_earthColor = new Uniform( drq, Program, "u_earthColor");
		u_skyDirection = new Uniform( drq, Program, "u_skyDirection");

		u_fogNear = new Uniform( drq, Program, "u_fogNear");
		u_fogFar = new Uniform( drq, Program, "u_fogFar");
		u_fogColor = new Uniform( drq, Program, "u_fogColor");
		
		u_transformsX = new Uniform( drq, Program, "TransformsX" );
		u_transformsY = new Uniform( drq, Program, "TransformsY" );
		u_transformsZ = new Uniform( drq, Program, "TransformsZ" );

		u_indexOffset = new Uniform( drq, Program, "IndexOffset" );
		
		u_shadowProjection = new Uniform( drq, Program, "u_shadowProjection" );
		u_shadowDirection = new Uniform( drq, Program, "u_shadowDirection" );
		u_samplerShadow = new Sampler( drq, Program, 0, "u_samplerShadow" );
		
		if( uniformListFactoty != null )
		{
			Uniforms = uniformListFactoty.Create();
			Uniforms.OnCreateUniforms( drq, Program );
		}
	}
}
