package com.nrt.model;

import com.nrt.render.*;
import com.nrt.math.Float3;
import com.nrt.math.Float4;
import com.nrt.math.Float4x4;
import com.nrt.basic.Loader;

public class ModelRender
{
	//public BasicRender BasicRender = null;

	
	public static class AmbientLight
	{
		public final Float3 Color = new Float3( 0.05f, 0.05f, 0.05f );
	}
	
	public static class DirectionalLight
	{
		public final Float3 Color = new Float3( 2.0f );
		public final Float3 Direction = Float3.Normalize( new Float3(), Float3.Local( 1.0f, 1.0f, -1.0f ) );
	}
	
	public static class ThreeSphereLight
	{
		public final Float3 SkyColor = new Float3( 0.0f, 0.0f, 0.05f );
		public final Float3 HorizonColor = new Float3( 0.3f, 0.3f, 0.4f );
		public final Float3 EarthColor = new Float3( 0.0f, 0.0f, 0.05f );
		public final Float3 SkyDirection = new Float3( 0.0f, 1.0f, 0.0f );
	}
	
	public static class Fog
	{
		public float Near = 0.0f;
		public float Far = 10000.0f;
		public final Float4 Color = new Float4( 0.0f );
	}
	
	public final AmbientLight AmbientLight = new AmbientLight();
	public final DirectionalLight DirectionalLight = new DirectionalLight();
	public final ThreeSphereLight ThreeSphereLight = new ThreeSphereLight();
	public final Fog Fog = new Fog();
		
	public ModelShadow Shadow = new ModelShadow();
	
	public SamplerState SamplerStateNearest = null;
		
	public final int kMaxTransforms = 64;
	public final float[] m_arrayTransformsX = new float[kMaxTransforms*4];
	public final float[] m_arrayTransformsY = new float[kMaxTransforms*4];
	public final float[] m_arrayTransformsZ = new float[kMaxTransforms*4];
	
	public ModelShaderSet ShaderSet = null;
	public ModelShaderPluginParameterList ShaderParameter = null;
	
	public ModelShaderSet DefaultShaderSet = null;
	
	public ModelRender( final DelayResourceQueue drq, final Loader loader )
	{
		DefaultShaderSet = new ModelShaderSet( drq, loader, "nrt_model_render.glsl", null, null );
		
		ShaderSet = DefaultShaderSet;
		
		SamplerStateNearest = new SamplerState( EMagFilter.Nearest, EMinFilter.Nearest, EWrap.ClampToEdge, EWrap.ClampToEdge );
	}
	
	public void SetShaderSet( ModelShaderSet shaderSet )
	{
		if( shaderSet != null )
		{
			ShaderSet = shaderSet;
		}
		else
		{
			ShaderSet = DefaultShaderSet;
		}
	}
	
	public void SetShaderParameter( ModelShaderPluginParameterList shaderParameter )
	{
		ShaderParameter = shaderParameter;
	}
	
	public void SetShadowParameter( ModelShadow shadow )
	{
		Shadow = shadow;
	}
	
	public void Draw( GfxCommandContext gfxc, MatrixCache mc,  Model model, Float4x4[] modelMatrices, ModelShaderSet.EShadow eShadow )
	{
		for( int i = 0 ; i < model.Joints.length ; i++ )
		{
			Float4x4 m = modelMatrices[i];
			
			m_arrayTransformsX[i*4+0] = m.Values[0];
			m_arrayTransformsX[i*4+1] = m.Values[4];
			m_arrayTransformsX[i*4+2] = m.Values[8];
			m_arrayTransformsX[i*4+3] = m.Values[12];
		
			m_arrayTransformsY[i*4+0] = m.Values[1];
			m_arrayTransformsY[i*4+1] = m.Values[5];
			m_arrayTransformsY[i*4+2] = m.Values[9];
			m_arrayTransformsY[i*4+3] = m.Values[13];
			
			m_arrayTransformsZ[i*4+0] = m.Values[2];
			m_arrayTransformsZ[i*4+1] = m.Values[6];
			m_arrayTransformsZ[i*4+2] = m.Values[10];
			m_arrayTransformsZ[i*4+3] = m.Values[14];
		}
		
		for( int i = 0 ; i < model.Materials.length ; i++ )
		{
			DrawRigidSkin( gfxc, mc,i, model.Materials[i], model.Meshes, model.Joints.length, eShadow );
		}
	}
	
	public void DrawRigidSkin( GfxCommandContext r, MatrixCache mc, int iMaterial, ModelMaterial material, ModelMesh[] meshes, int nbJoints, ModelShaderSet.EShadow eShadow )
	{
		//GfxCommandContext r = gfxc;//BasicRender.GetRender();
		//MatrixCache mc = BasicRender.GetMatrixCache();

		//ShaderRigidSkin shader = m_shaderRigidSkin;//s[kShader_RigidSkin_Color];
		ModelShader shader = ShaderSet.GetShader(
			ModelShaderSet.EDeformation.RigidSkin,
			eShadow,
			material.Technique );
		
		r.SetProgram( shader.Program );
		SetBaseUniforms( r, mc, shader, iMaterial, material );
		
		for( int i = 0 ; i < material.MeshCount ; i++ )
		{
			int nJointOffset = i*16;
			int nbMeshJoints = nbJoints - nJointOffset;
			DrawRigidSkinMesh( r, meshes[material.MeshStart + i],
				(nbMeshJoints > 16 ? 16 : nbMeshJoints), nJointOffset,
				shader );
		}
	}
	
	public void DrawRigidSkinMesh( GfxCommandContext r, ModelMesh mesh, int nbJoints, int nJointOffset, ModelShader shader )
	{
		//Render r = BasicRender.GetRender();
		
		//ShaderRigidSkin shader = m_shaderRigidSkin;
		r.SetFloat4Array( shader.u_transformsX, nbJoints, m_arrayTransformsX, nJointOffset*4);
		r.SetFloat4Array( shader.u_transformsY, nbJoints, m_arrayTransformsY, nJointOffset*4);
		r.SetFloat4Array( shader.u_transformsZ, nbJoints, m_arrayTransformsZ, nJointOffset*4);
		
		r.SetFloat( shader.u_indexOffset, (float) nJointOffset );
		
		r.SetVertexBuffer( mesh.VertexBuffer );
		r.EnableVertexStream( mesh.Stream,0 );
		r.SetIndexBuffer( mesh.IndexBuffer );
		//GLES20.glDrawElements( GLES20.GL_TRIANGLES, mesh.Indices.length, GLES20.GL_UNSIGNED_SHORT, 0 );
		r.DrawElements( EPrimitive.Triangles, mesh.Indices.length, com.nrt.render.EIndexFormat.UnsignedShort, 0);
		r.DisableVertexStream( mesh.Stream );
	}
	
	
	
	private void SetBaseUniforms( GfxCommandContext r, MatrixCache mc,  ModelShader shader, int iMaterial, ModelMaterial material )
	{
		//Render r = BasicRender.GetRender();
		//MatrixCache mc = BasicRender.GetMatrixCache();
		
		r.SetMatrix( shader.u_worldViewProjection, mc.GetWorldViewProjection().Values );
		r.SetMatrix( shader.u_world, mc.GetWorld().Values );
		r.SetMatrix( shader.u_viewPosition, mc.GetViewInverse().Values );

		r.SetFloat3( shader.u_ambient, material.Ambient.X, material.Ambient.Y, material.Ambient.Z );
		r.SetFloat3( shader.u_diffuse, material.Diffuse.X, material.Diffuse.Y, material.Diffuse.Z );
		r.SetFloat3( shader.u_specular, material.Specular.X, material.Specular.Y, material.Specular.Z );
		r.SetFloat3( shader.u_emission, material.Emission.X, material.Emission.Y, material.Emission.Z );

		r.SetFloat( shader.u_power, material.Power );

		r.SetFloat3( shader.u_lightAmbient, AmbientLight.Color );

		r.SetFloat3( shader.u_lightColor, DirectionalLight.Color );
		r.SetFloat3( shader.u_lightDirection, DirectionalLight.Direction );

		r.SetFloat3( shader.u_skyColor, ThreeSphereLight.SkyColor );
		r.SetFloat3( shader.u_horizonColor, ThreeSphereLight.HorizonColor );
		r.SetFloat3( shader.u_earthColor, ThreeSphereLight.EarthColor );
		r.SetFloat3( shader.u_skyDirection, ThreeSphereLight.SkyDirection );
		
		r.SetFloat( shader.u_fogNear, Fog.Near );
		r.SetFloat( shader.u_fogFar, Fog.Far );
		r.SetFloat4( shader.u_fogColor, Fog.Color );
		
		if( Shadow != null )
		{
			r.SetMatrix( shader.u_shadowProjection, Shadow.GetWorldViewProjection().Values );
			r.SetFloat3( shader.u_shadowDirection, Shadow.GetShadowDirection() );
			r.SetTexture( shader.u_samplerShadow, Shadow.Texture );
			r.SetSamplerState( shader.u_samplerShadow, SamplerStateNearest );
		}
		
		if( shader.Uniforms != null && ShaderParameter != null )
		{
			ShaderParameter.OnUpdateParameter( r, mc, shader.Uniforms, iMaterial, material );
		}
	}
	
	public void Draw( GfxCommandContext r, MatrixCache mc, Model model, ModelShaderSet.EShadow eShadow )
	{
		for( int i = 0 ; i < model.Materials.length ; i++ )
		{
			Draw( r, mc, i, model.Materials[i], model.Meshes, eShadow );
		}
	}
	
	public void Draw( GfxCommandContext r, MatrixCache mc, int iMaterial, ModelMaterial material, ModelMesh[] meshes, ModelShaderSet.EShadow eShadow )
	{
		//Render r = BasicRender.GetRender();
		
		ModelShader shader = ShaderSet.GetShader( 
			ModelShaderSet.EDeformation.Rigid,
			eShadow,
			material.Technique );
		
		r.SetProgram( shader.Program );
		SetBaseUniforms( r, mc, shader, iMaterial, material );
		
		for( int i = 0 ; i < material.MeshCount ; i++ )
		{
			Draw( r, meshes[material.MeshStart + i] );
		}
	}
	/*
	public void DrawDiscard( Model model, float fDiscardRadius )
	{
		for( int i = 0 ; i < model.Materials.length ; i++ )
		{
			DrawDiscard( i, model.Materials[i], model.Meshes, fDiscardRadius );
		}
	}
	*/
	/*
	public void DrawDiscard( int iMaterial, ModelMaterial material, ModelMesh[] meshes, float fDiscardRadius  )
	{
		Render r = BasicRender.GetRender();
		MatrixCache mc = BasicRender.GetMatrixCache();

		ShaderRigidDiscard shader = m_shaderRigidDiscard;
		r.SetProgram( shader.Program );
		SetBaseUniforms( shader, iMaterial, material );
		
		r.SetFloat( shader.u_discardRadius, fDiscardRadius );
	
		for( int i = 0 ; i < material.MeshCount ; i++ )
		{
			Draw( meshes[material.MeshStart + i] );
		}
	}
	*/
	public void Draw( GfxCommandContext r,  ModelMesh mesh )
	{
		//Render r = BasicRender.GetRender();
		r.SetVertexBuffer( mesh.VertexBuffer );
		r.EnableVertexStream( mesh.Stream,0 );
		r.SetIndexBuffer( mesh.IndexBuffer );
		//r.DrawElements( GLES20.GL_TRIANGLES, mesh.Indices.length, EIndexFormat.UnsignedShort, 0 );
		r.DrawElements( EPrimitive.Triangles, mesh.Indices.length, com.nrt.render.EIndexFormat.UnsignedShort, 0 );
		r.DisableVertexStream( mesh.Stream );
	}
}
