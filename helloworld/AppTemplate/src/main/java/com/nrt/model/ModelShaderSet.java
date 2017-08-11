package com.nrt.model;

import com.nrt.basic.*;
import com.nrt.render.*;
import java.util.*;

public class ModelShaderSet
{
	public enum EDeformation
	{
		Rigid(0,"RIGID"),
		RigidSkin(1,"RIGIDSKIN");
		
		public int Index = 0;
		public String Macro = null;
		
		private EDeformation( int i, String strMacro )
		{
			Index = i;
			Macro = strMacro;
		}
	};
	
	public enum EShadow
	{
		Disable(0,"NOSHADOW"),
		Caster(1,"CASTSHADOW"),
		Reciever(2,"RECIEVESHADOW");
		
		public int Index = 0;
		public String Macro = null;
		
		private EShadow(int i, String strMacro )
		{
			Index = i;
			Macro = strMacro;
		}
	}
	
	public enum EType
	{
		NoTexture(0, new String[]{"NOTEXTURE"}),
		Diffuse(1,new String[]{"DIFFUSE"}),
		Normal(2, new String[]{"NORMAL"}),
		DiffuseNormal(3,new String[]{"DIFFUSE","NORMAL"});
		
		public int Index = 0;
		public String[] Macros = null;
		
		private EType(int i, String[] macros )
		{
			Index = i;
			Macros = macros;
		}
	};
	
	
	
	public ModelShader[] RigidShaders = new ModelShader[EType.values().length];
	public ModelShader[] RigidSkinShaders = new ModelShader[EType.values().length];
	
	public ModelShader[] RigidCastShadowShaders = new ModelShader[EType.values().length];
	public ModelShader[] RigidSkinCastShadowShaders = new ModelShader[EType.values().length];
	
	public ModelShader[] RigidRecieveShadowShaders = new ModelShader[EType.values().length];
	public ModelShader[] RigidSkinRecieveShadowShaders = new ModelShader[EType.values().length];
	
	
	public ModelShaderSet( DelayResourceQueue drq, Loader loader, String strShaderPath, String strPluginPath, ModelShaderPluginUniformListFactory pluginUniformListFactory )
		//throws ThreadForceDestroyException
	{
		String rigid = EDeformation.Rigid.Macro;
		String rigidSkin = EDeformation.RigidSkin.Macro;
		String noShadow = EShadow.Disable.Macro;
		String castShadow = EShadow.Caster.Macro;
		String recieveShadow = EShadow.Reciever.Macro;
		
		String[] strSource = loader.LoadTextFile( strShaderPath );
		String[] strPluginSource = null;
		String[] strPluginMacros = null;
		
		drq.Add( "shader set " + strShaderPath );
		if( strPluginPath != null )
		{
			strPluginSource = loader.LoadTextFile( strPluginPath );
			strPluginMacros = new String[] { "OUTPUTPLUGIN" };
			
			drq.Add( "shader set plugin " + strShaderPath );
		}
			
		drq.Add( "rigid" );
		RigidShaders = CreateShader( drq,
			strSource, new String[]{ rigid, noShadow }, 
			strPluginSource, strPluginMacros, pluginUniformListFactory,
			s_abRigid );
		drq.Add( "rigid skin" );
		RigidSkinShaders = CreateShader( drq,
			strSource, new String[]{ rigidSkin, noShadow }, 
			strPluginSource, strPluginMacros, pluginUniformListFactory,
			s_abRigidSkin );
		
		drq.Add( "rigid cast shadow" );
		RigidCastShadowShaders = CreateShader( drq,
			strSource, new String[]{ rigid, castShadow }, 
			strPluginSource, strPluginMacros, pluginUniformListFactory,						  
			s_abRigid );
		drq.Add( "rigid skin cast shadow" );
		RigidSkinCastShadowShaders = CreateShader( drq,
			strSource, new String[]{ rigidSkin, castShadow }, 
			strPluginSource, strPluginMacros, pluginUniformListFactory,
			s_abRigidSkin );

		drq.Add( "rigid recieve shadow" );
		RigidRecieveShadowShaders = CreateShader( drq,
			strSource, new String[]{ rigid, recieveShadow },
			strPluginSource, strPluginMacros, pluginUniformListFactory,
			s_abRigid );
		drq.Add( "rigid skin recieve shadow" );
		RigidSkinRecieveShadowShaders = CreateShader( drq,
			strSource, new String[]{ rigidSkin, recieveShadow },
			strPluginSource, strPluginMacros, pluginUniformListFactory,
			s_abRigidSkin );
	}
	
	private static AttributeBinding[][] s_abRigid =
	{
		new AttributeBinding[] {
			new AttributeBinding( 0, "a_position" ),
			new AttributeBinding( 1, "a_normal" ),
			new AttributeBinding( 2, "a_texcoord" ), },	
		new AttributeBinding[] {
			new AttributeBinding( 0, "a_position" ),
			new AttributeBinding( 1, "a_normal" ),
			new AttributeBinding( 2, "a_texcoord" ), },
		new AttributeBinding[] {
			new AttributeBinding( 0, "a_position" ),
			new AttributeBinding( 1, "a_normal" ),
			new AttributeBinding( 2, "a_texcoord" ),},
		new AttributeBinding[] {
			new AttributeBinding( 0, "a_position" ),
			new AttributeBinding( 1, "a_normal" ),
			new AttributeBinding( 2, "a_texcoord" ),
		},
	};
	
	private static AttributeBinding[][] s_abRigidSkin =
	{
		new AttributeBinding[] {
			new AttributeBinding( 0, "a_position" ),
			new AttributeBinding( 1, "a_normal" ), 
			new AttributeBinding( 2, "a_texcoord" ),	
			new AttributeBinding( 3, "a_fIndex" ),
		},
		new AttributeBinding[] {
			new AttributeBinding( 0, "a_position" ),
			new AttributeBinding( 1, "a_normal" ),
			new AttributeBinding( 2, "a_texcoord" ),
			new AttributeBinding( 3, "a_fIndex" ),
		},
		new AttributeBinding[] {
			new AttributeBinding( 0, "a_position" ),
			new AttributeBinding( 1, "a_normal" ),
			new AttributeBinding( 2, "a_texcoord" ),
			new AttributeBinding( 3, "a_fIndex" ),
		},
		new AttributeBinding[] {
			new AttributeBinding( 0, "a_position" ),
			new AttributeBinding( 1, "a_normal" ),
			new AttributeBinding( 2, "a_texcoord" ),
			new AttributeBinding( 3, "a_fIndex" ),
		},
	};
	
	public static ModelShader[] CreateShader
	(
		DelayResourceQueue drq,
		String[] strSource, String[] arrayMacros, 
		String[] strPluginSource, String[] arrayPluginMacros,
		ModelShaderPluginUniformListFactory pluginUniformListFactory,
		AttributeBinding[][] arrayAttributes 
	)
		//throws ThreadForceDestroyException
	{
		EType[] types = EType.values();
		ModelShader[] arrayShaders = new ModelShader[types.length];
		
		for( int i = 0 ; i < types.length ; i++ )
		{			
			drq.Add( "compling " + types[i].name() );
			List<String> listVs = new ArrayList<String>();
			List<String> listFs = new ArrayList<String>();
			
			listVs.add( "#define VERTEX_SHADER\n" );
			listFs.add( "#define FRAGMENT_SHADER\n" );
			
			String strShader = "";
			for( int j = 0 ; j < arrayMacros.length  ; j++ )
			{
				listVs.add( "#define " + arrayMacros[j] + "\n" );
				listFs.add( "#define " + arrayMacros[j] + "\n" );
				
				strShader += arrayMacros[j] + " ";
			}
			
			for( int j = 0 ; j < types[i].Macros.length ; j++ )
			{
				listVs.add( "#define " + types[i].Macros[j] + "\n" );
				listFs.add( "#define " + types[i].Macros[j] + "\n" );
				
				strShader += types[i].Macros[j] + " ";
			}
			
			if( arrayPluginMacros != null )
			{
				for( int j = 0 ; j < arrayPluginMacros.length  ; j++ )
				{
					listVs.add( "#define " + arrayPluginMacros[j] + "\n" );
					listFs.add( "#define " + arrayPluginMacros[j] + "\n" );

					strShader += arrayPluginMacros[j] + " ";
				}
			}
			
			Shader.Error.add( "----------" );
			Shader.Error.add( strShader );
			Shader.Error.add( "Offset " + listVs.size() );
			
			for( int j = 0 ; j < strSource.length ; j++ )
			{
				listVs.add( strSource[j] );
				listFs.add( strSource[j] );
			}
			
			if( strPluginSource != null )
			{
				for( int j = 0 ; j < strPluginSource.length ; j++ )
				{
					listVs.add( strPluginSource[j] );
					listFs.add( strPluginSource[j] );
				}
			}
			
			String[] arrayVs = new String[listVs.size()];
			listVs.toArray( arrayVs );
			
			String[] arrayFs = new String[listFs.size()];
			listFs.toArray( arrayFs );

			arrayShaders[i] = new ModelShader(
				drq,
				arrayAttributes[i], 
				new VertexShader( drq, arrayVs ),
				new FragmentShader( drq, arrayFs ),
				pluginUniformListFactory );
				
			drq.Add( "compling " + types[i].name() + " end" );
			Thread.yield();
		}
		
		return arrayShaders;
	}
	
	ModelShader GetShader( EDeformation eDeformation, EShadow eShadow, EType eType )
	{
		if( eDeformation == EDeformation.Rigid )
		{
			switch(eShadow)
			{
				case Disable:
					return RigidShaders[eType.Index];
					
				case Caster:
					return RigidCastShadowShaders[eType.Index];
					
				case Reciever:
					return RigidRecieveShadowShaders[eType.Index];
			}
		}
		else
		{
			switch(eShadow)
			{
				case Disable:
					return RigidSkinShaders[eType.Index];

				case Caster:
					return RigidSkinCastShadowShaders[eType.Index];

				case Reciever:
					return RigidSkinRecieveShadowShaders[eType.Index];
			}
		}
		
		return null;
	}
}

