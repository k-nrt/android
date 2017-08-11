package com.nrt.helloworld;
import com.nrt.model.*;
import com.nrt.render.*;
import com.nrt.framework.*;
import com.nrt.basic.*;

public class GameModelExplosionRender
{
	private static class ShaderUniformListFactory implements ModelShaderPluginUniformListFactory
	{
		@Override
		public ModelShaderPluginUniformList Create()
		{
			return new ShaderUniformList();
		}
	}
	
	private static class ShaderUniformList implements ModelShaderPluginUniformList
	{
		public Uniform u_discardStart = null;
		public Uniform u_discardEnd = null;
		public Uniform u_discardUnit = null;
		public Uniform u_discardRadius = null;
		public Uniform u_startRadius = null;
		
		public Sampler u_sampler2dNoise = null;
		
		@Override
		public void OnCreateUniforms( DelayResourceQueue drq, Program program)
			//throws ThreadForceDestroyException
		{
			u_discardStart = new Uniform( drq, program, "u_discardStart" );
			u_discardEnd = new Uniform( drq, program, "u_discardEnd" );
			u_discardUnit = new Uniform( drq, program, "u_discardUnit" );
			u_discardRadius = new Uniform( drq, program, "u_discardRadius" );
			u_startRadius = new Uniform( drq, program, "u_startRadius" );
			u_sampler2dNoise = new Sampler( drq, program, 0, "u_sampler2dNoise" );
			//Shader.Error.add( "u_discardRadius "+ u_discardRadius.Index );			
			
		}
	}
	
	public static class ShaderParameterList implements ModelShaderPluginParameterList
	{
		public float DiscardStart = 0.0f;
		public float DiscardEnd = 0.0f;
		public float Unit = 1.0f;
		public float Radius = 0.0f;
		public float Start = 0.0f;
		
		public StaticTexture Noise = null;
		
		@Override
		public void OnUpdateParameter( GfxCommandContext r, MatrixCache mc, ModelShaderPluginUniformList uniform, int iMaterial, ModelMaterial material)
		{
			ShaderUniformList u = (ShaderUniformList) uniform;
			
			r.SetFloat( u.u_discardStart, DiscardStart );
			r.SetFloat( u.u_discardEnd, DiscardEnd );
			r.SetFloat( u.u_discardUnit, Unit );
			r.SetFloat( u.u_discardRadius, Radius );
			r.SetFloat( u.u_startRadius, Start );
			
			r.SetTexture( u.u_sampler2dNoise, Noise );
			
			//DebugLog.Error.WriteLine( "radius " + u.u_discardRadius.Index + " " + Radius );
		}
	}
	
	public ModelShaderSet ShaderSet = null;
	public ShaderParameterList Parameters = null;
	
	public GameModelExplosionRender( DelayResourceQueue drq )
		//throws ThreadForceDestroyException
	{
		ShaderSet = new ModelShaderSet( drq, SubSystem.Loader,
			"nrt_model_render.glsl", "nrt_model_render_explosion.glsl",
			new ShaderUniformListFactory() );
			
		Parameters = new ShaderParameterList();
	}
}
