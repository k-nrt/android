package com.nrt.render;

import android.util.Log;
import android.opengl.*;
import java.util.*;
import com.nrt.framework.*;

public class Shader extends RenderResource
{
	public static List<String> Error = new ArrayList<String>();

	public enum EType
	{
		Unknown(0),
		Vertex(GLES20.GL_VERTEX_SHADER),
		Fragment(GLES20.GL_FRAGMENT_SHADER);
		
		public int Value = 0;
		EType( int value )
		{
			Value = value;
		}
	}
	
	EType Type = EType.Unknown;
	String Source = null;
	
	public Shader()
	{}
	
	public void Initialize( DelayResourceQueue queue, EType type, String strSource ) // throws ThreadForceDestroyException
	{
		Type = type;
		Source = strSource;
		
		if( queue == null )
		{
			//Apply();
			SubSystem.Log.WriteLine(strSource);
		}
		else
		{
			queue.Add( this );
		}
	}

	@Override
	public void Apply()
	{
		//SubSystem.Log.WriteLine( "apply shader" );
		Name = GLES20.glCreateShader(Type.Value);
		if (Name == 0)
		{
			SubSystem.Log.WriteLine( "can not create shader" );
			// シェーダーの領域確保に失敗した
			//Log.d("compileShader", "領域確保に失敗"); 
			return; 
		}
		// シェーダーをコンパイル 

		GLES20.glShaderSource(Name, Source);
		GLES20.glCompileShader(Name);

		// コンパイルが成功したか調べる
		int[] res = new int[1];
		GLES20.glGetShaderiv(Name, GLES20.GL_COMPILE_STATUS, res, 0);
		if (res[0] == 0)
		{
			// 失敗してる
			//	Log.d("compileShader", GLES20.glGetShaderInfoLog(Name));
			SubSystem.Log.WriteLine( Type.name() );
			String[] logs = GLES20.glGetShaderInfoLog( Name ).split( "\n" );

			for( String log : logs )
			{
				SubSystem.Log.WriteLine( log );
			}

			Name = 0;
			return;
		}
	}
	
	public Shader( DelayResourceQueue queue, EType eType, String[] arrayLines ) // throws ThreadForceDestroyException
	{
		//Create( this, eType, ConbineSourceLines( arrayLines ) );
		Initialize( queue, eType, ConbineSourceLines( arrayLines ) );
	}
	
	public static String ConbineSourceLines( String[] arrayLines )
	{
		String strSrc = new String();
		for (int i = 0 ; i < arrayLines.length ; i++)
		{
			strSrc += arrayLines[i];
			strSrc += "\n";
		}
		return strSrc;
	}
	
//	public static void Create( Shader shader, EType eType, String strSource )
//	{
//		/*
//		String strSrc = new String();
//		for (int i = 0 ; i < arraySrc.length ; i++)
//		{
//			strSrc += arraySrc[i];
//			strSrc += "\n";
//		}
//		*/
//		/*
//		int type = GLES20.GL_VERTEX_SHADER;
//		if (eType == EType.Fragment)
//		{
//			type = GLES20.GL_FRAGMENT_SHADER;
//		}
//		*/
//		shader.Name = GLES20.glCreateShader(eType.Value);
//		if (shader.Name == 0)
//		{
//			// シェーダーの領域確保に失敗した
//			Log.d("compileShader", "領域確保に失敗"); 
//			return; 
//		}
//		// シェーダーをコンパイル 
//
//		GLES20.glShaderSource(shader.Name, strSource);
//		GLES20.glCompileShader(shader.Name);
//
//		// コンパイルが成功したか調べる
//		int[] res = new int[1];
//		GLES20.glGetShaderiv(shader.Name, GLES20.GL_COMPILE_STATUS, res, 0);
//		if (res[0] == 0)
//		{
//			// 失敗してる
//			//	Log.d("compileShader", GLES20.glGetShaderInfoLog(Name));
//			Error.add( eType.name() );
//			String[] logs = GLES20.glGetShaderInfoLog( shader.Name ).split( "\n" );
//
//			for( String log : logs )
//			{
//				Error.add( log );
//			}
//
//			shader.Name = 0;
//			return;
//		}
//	}
}
