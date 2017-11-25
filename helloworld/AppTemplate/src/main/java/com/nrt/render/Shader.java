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
	
	public Shader( DelayResourceQueue drq, EType eType, String[] arrayLines )
	{
		Type = eType;
		Source = CombineSourceLines( arrayLines );
		
		if( drq != null )
		{
			drq.Add( this );
		}
		else
		{
			Generate();
		}
	}

	@Override public void Generate()
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

	@Override public void Delete()
	{
		if( 0 < Name )
		{
			GLES20.glDeleteShader(Name);
		}
		Name = 0;
	}

	public static String CombineSourceLines( String[] arrayLines )
	{
		String strSrc = new String();
		for (int i = 0 ; i < arrayLines.length ; i++)
		{
			strSrc += arrayLines[i];
			strSrc += "\n";
		}
		return strSrc;
	}
}
