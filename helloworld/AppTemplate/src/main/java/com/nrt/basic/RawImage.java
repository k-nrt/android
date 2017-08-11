package com.nrt.basic;

import android.opengl.*;
import java.nio.ByteBuffer;
import java.nio.*;

public class RawImage
{
	public int Width = 0;
	public int Height = 0;
	public int Format = 0;
	public int Type = 0;

	public ByteBuffer Pixels = null;
	
	
	public static RawImage CreateHalfSizeImage( RawImage src )
	{
		RawImage dst = new RawImage();
		dst.Width = src.Width/2;
		dst.Height = src.Height/2;
		dst.Format = src.Format;
		dst.Type = src.Type;
		dst.Pixels = ByteBuffer.allocateDirect( dst.Width*dst.Height*dst.GetByteLengthPerPixel());
		dst.Pixels.order(ByteOrder.nativeOrder());
		int nbComponents = src.GetComponentCount();
		int[] srcPixels = src.GetPixels();
		
		int[] dstPixels = new int[dst.Width*dst.Height*nbComponents];
		int dp = 0;
		for( int y = 0 ; y < dst.Height ; y++ )
		{
			int sp00 = y*2*src.Width*nbComponents;
			int sp10 = sp00 + src.Width*nbComponents;
			int sp01 = sp00 + nbComponents;
			int sp11 = sp10 + nbComponents;
			for( int x = 0 ; x < dst.Width ; x++ )
			{
				for( int c = 0 ; c < nbComponents ; c++, dp++)
				{
					int p = (srcPixels[sp00]+srcPixels[sp10]+srcPixels[sp01]+srcPixels[sp11])/4;
					dstPixels[dp]=p;
					sp00 += 1;//2*nbComponents;
					sp10 += 1;//*nbComponents;
					sp01 += 1;//*nbComponents;
					sp11 += 1;//*nbComponents;
				}
				
				sp00 += nbComponents;
				sp10 += nbComponents;
				sp01 += nbComponents;
				sp11 += nbComponents;
			}
		}
		
		dst.SetPixels( dstPixels );
		
		return dst;
	}
	
	public int GetComponentCount() 
	{
		switch (Format)
		{
			default: return 0;
			case GLES20.GL_LUMINANCE: return 1;					
			case GLES20.GL_RGB: return 3;					
			case GLES20.GL_LUMINANCE_ALPHA: return 2;
			case GLES20.GL_RGBA: return 4;
		}
	}
	
	public int GetByteLengthPerComponent()
	{
		switch( Type )
		{
			default: return 0;
			case GLES20.GL_UNSIGNED_BYTE:
			case GLES20.GL_BYTE:
				return 1;

			case GLES20.GL_SHORT:
			case GLES20.GL_UNSIGNED_SHORT:
				return 2;

			case GLES20.GL_INT:
			case GLES20.GL_UNSIGNED_INT:
				return 4;
		}
	}
	
	public int GetByteLengthPerPixel()
	{
		return GetByteLengthPerComponent()*GetComponentCount();
	}
	
	public int[] GetPixels()
	{
		int nbComponents = GetComponentCount();
		int[] pixels = new int[Width*Height*nbComponents];
		int pos = 0;
		Pixels.position(0);
		switch(Type)
		{
			case GLES20.GL_BYTE:
			case GLES20.GL_UNSIGNED_BYTE:
				for(int i = 0 ; i < (Width*Height) ; i++ )
				{
					for( int j = 0 ; j < nbComponents ; j++, pos++ )
					{
						pixels[pos] = ((int) Pixels.get())&0xff;
					}
				}
				break;
				
				
			case GLES20.GL_SHORT:
			case GLES20.GL_UNSIGNED_SHORT:
				for(int i = 0 ; i < (Width*Height) ; i++ )
				{
					for( int j = 0 ; j < nbComponents ; j++, pos++ )
					{
						pixels[pos] = ((int) Pixels.getShort())&0xffff;
					}
				}
				break;
				
			case GLES20.GL_INT:
			case GLES20.GL_UNSIGNED_INT:
				for(int i = 0 ; i < (Width*Height) ; i++ )
				{
					for( int j = 0 ; j < nbComponents ; j++, pos++ )
					{
						pixels[pos] = Pixels.getInt();
					}
				}
				break;
		}
		Pixels.position(0);
		return pixels;
	}
	
	public void SetPixels( int[] pixels )
	{
		int nbComponents = GetComponentCount();
		int pos = 0;
		Pixels.position(0);
		switch(Type)
		{
			case GLES20.GL_BYTE:
			case GLES20.GL_UNSIGNED_BYTE:
				for(int i = 0 ; i < (Width*Height) ; i++ )
				{
					for( int j = 0 ; j < nbComponents ; j++, pos++ )
					{
						Pixels.put((byte)0xff);//(pixels[pos]&0xff));
					}
				}
				break;


			case GLES20.GL_SHORT:
			case GLES20.GL_UNSIGNED_SHORT:
				for(int i = 0 ; i < (Width*Height) ; i++ )
				{
					for( int j = 0 ; j < nbComponents ; j++, pos++ )
					{
						Pixels.putShort((short)(pixels[pos]&0xffff));
					}
				}
				break;

			case GLES20.GL_INT:
			case GLES20.GL_UNSIGNED_INT:
				for(int i = 0 ; i < (Width*Height) ; i++ )
				{
					for( int j = 0 ; j < nbComponents ; j++, pos++ )
					{
						Pixels.putInt(pixels[pos]);
					}
				}
				break;
		}

		Pixels.position(0);
	}
}
