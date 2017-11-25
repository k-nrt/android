package com.nrt.render;
import android.opengl.GLES20;
import com.nrt.framework.SubSystem;

public class RenderTexture extends RenderResource implements Texture
{
	public RenderTextureFormat Format = RenderTextureFormat.RGBA;
	public int PotWidth = 0;
	public int PotHeight = 0;
	
	public int NpotWidth = 0;
	public int NpotHeight = 0;
	
	public RenderTexture( DelayResourceQueue drq, RenderTextureFormat eFormat, int width, int height )
	{
		Format = eFormat;
		NpotWidth = width;
		NpotHeight = height;
		PotWidth =StaticTexture.GetPowerOfTwo( width );
		PotHeight = StaticTexture.GetPowerOfTwo( height );
		Name = 0;//CreateTexture( eFormat, PotWidth, PotHeight );

		if( drq != null )
		{
			drq.Add(this);
		}
		else
		{
			Generate();
		}
	}

	@Override public void Generate()
	{
		DeleteTexture( Name );
		Name = CreateTexture( Format, PotWidth, PotHeight );
		SubSystem.Log.WriteLine( String.format( "RenderTexture.Apply() %d %s %dx%d", Name, Format.toString(), PotWidth, PotHeight ) );
	}

	@Override public void Delete()
	{
		DeleteTexture( Name );
	}
	
	@Override
	public int GetTextureName()
	{
		return Name;
	}

	@Override
	public int GetPotWidth()
	{
		return PotWidth;
	}

	@Override
	public int GetPotHeight()
	{
		return PotHeight;
	}

	@Override
	public int GetNpotWidth()
	{
		return NpotWidth;
	}

	@Override
	public int GetNpotHeight()
	{
		return NpotHeight;
	}	
	
	public void OnSurfaceChanged( int w, int h )
	{
		DeleteTexture( Name );
		PotWidth = StaticTexture.GetPowerOfTwo(w);
		PotHeight = StaticTexture.GetPowerOfTwo(h);
		NpotWidth = w;
		NpotHeight = h;
		
		Name = CreateTexture( Format, PotWidth, PotHeight );
	}
		
	protected static int CreateTexture( RenderTextureFormat eFormat, int width, int height )
	{
		int[] names={0};
		java.nio.Buffer buffer = java.nio.ByteBuffer.allocateDirect( width*height*4 );
	
		GLES20.glGenTextures( 1, names, 0 );
		
		GLES20.glBindTexture( GLES20.GL_TEXTURE_2D, names[0] );
		GLES20.glTexImage2D( GLES20.GL_TEXTURE_2D, 0, eFormat.Value,
							width, height, 0, 
							GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer );
							
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		
		GLES20.glBindTexture( GLES20.GL_TEXTURE_2D, 0 );	
		
		return names[0];
	}
	
	protected static void DeleteTexture( int name )
	{
		if( name <= 0 )
		{
			return;
		}

		GLES20.glBindTexture( GLES20.GL_TEXTURE_2D, 0 );
		
		int[] names = { name };		
		GLES20.glDeleteTextures( 1, names, 0 );
	}
	
}
