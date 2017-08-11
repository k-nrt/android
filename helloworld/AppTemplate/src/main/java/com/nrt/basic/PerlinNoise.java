package com.nrt.basic;
import java.util.*;
//import android.renderscript.*;
import com.nrt.clipper.*;
import com.nrt.math.*;
import java.nio.*;

public class PerlinNoise
{
	public static class FloatSurface
	{
		public int Width = 0;
		public int Height = 0;
		public int Depth = 0;
		public float[] Pixels = null;
		
		private int SMask = 0;
		private int TMask = 0;
		private int RMask = 0;
		
		public FloatSurface(){}
		public FloatSurface( int w, int h, int d )
		{
			Width = w;
			Height = h;			
			Depth = d;
			Pixels = new float[w*h*d];
			
			for(SMask=1;SMask<Width;SMask <<= 1);
			for(TMask=1;TMask<Height;TMask <<= 1);
			for(RMask=1;RMask<Depth;RMask <<= 1);
			
			SMask -= 1;
			TMask -= 1;
			RMask -= 1;
		}
		
		float GetPixel( int x, int y, int z )
		{
			x = x&SMask;
			y = y&TMask;
			z = z&RMask;
			return Pixels[x+y*Width+z*Width*Height];
		}
		
		void SetPixel( int x, int y, int z, float pixel )
		{
			x = x&SMask;
			y = y&TMask;
			z = z&RMask;
			Pixels[x+y*Width+z*Width*Height] = pixel;			
		}
		
		float FetchLinear( float s, float t, float r )
		{
			float x = s*((float)Width);
			float y = t*((float)Height);
			float z = r*((float)Depth);
			
			int xfloor = (int)FMath.Floor(x);
			int yfloor = (int)FMath.Floor(y);
			int zfloor = (int)FMath.Floor(z);
			
			float xfrac = x - (float)xfloor;
			float yfrac = y - (float)yfloor;
			float zfrac = z - (float)zfloor;
			
			float p000 = GetPixel( xfloor, yfloor, zfloor );
			float p100 = GetPixel( xfloor+1, yfloor, zfloor );
			float p010 = GetPixel( xfloor, yfloor+1, zfloor );
			float p110 = GetPixel( xfloor+1, yfloor+1, zfloor );
			
			float p001 = GetPixel( xfloor, yfloor, zfloor + 1 );
			float p101 = GetPixel( xfloor+1, yfloor, zfloor + 1);
			float p011 = GetPixel( xfloor, yfloor+1, zfloor + 1);
			float p111 = GetPixel( xfloor+1, yfloor+1, zfloor +1);
			
			float p00 = FMath.Lerp( p000, p001, zfrac );
			float p10 = FMath.Lerp( p100, p101, zfrac );
			float p01 = FMath.Lerp( p010, p011, zfrac );
			float p11 = FMath.Lerp( p110, p111, zfrac );
			
			float p0 = FMath.Lerp( p00, p10, xfrac );
			float p1 = FMath.Lerp( p01, p11, xfrac );
			return FMath.Lerp(p0,p1,yfrac);
			
		}
	}
	
	public int Width = 0;
	public int Height = 0;
	public int Depth = 0;
	public int SurfaceWidth = 0;
	public int SurfaceHeight = 0;
	
	public ByteBuffer Pixels = null;
	
	public PerlinNoise( int w, int h, int d )
	{
		Width = w;
		Height = h;
		Depth = d;
		
		
		List<FloatSurface> listSurfaces = new ArrayList<FloatSurface>();
	
		for( int ww =w, hh=h, dd=d; 2 < ww || 2 < hh || 2 < dd ; ww >>= 1, hh >>= 1, dd  >>= 1)
		{
			ww = ( (ww < 1) ? 1 : ww );
			hh = ( (hh < 1) ? 1 : hh );
			dd = ( (dd < 1) ? 1 : dd );
			
			FloatSurface surface = new FloatSurface( ww, hh, dd );
			
			Rand rand = new Rand();
			for( int i = 0 ; i < ww*hh*dd ; i++ )
			{
				surface.Pixels[i] = rand.Float();
			}
			
			listSurfaces.add( surface );
		}
		
		int dw = (int) FMath.Sqrt( (float) d );
		int dh = d/dw;
		
		int sw = SurfaceWidth = w*dw;
		int sh = SurfaceHeight = h*dh;
		
		FloatSurface surfaceDst = new FloatSurface( sw, sh, 1 );
		
		for( int i = 0 ; i < listSurfaces.size() ; i++ )
		{
			FloatSurface surface = listSurfaces.get(i);
			
			float fPersistence = FMath.Pow( 0.5f, (float)(listSurfaces.size()-i));
			
			for( int z = 0 ; z < d ; z++ )
			{
				float r = ((float)z)/((float)d);
				int ox = z%dw;
				int oy = z/dw;
				
				for( int y = 0 ; y < h ; y++ )
				{
					float t = ((float)y)/((float)h);
					for( int x = 0 ; x < w ; x++ )
					{					
						float s = ((float)x)/((float)w);
						float fPixel = surface.FetchLinear( s, t, r );
						surfaceDst.Pixels[ox*w+x+(oy*h+y)*sw] += fPixel*fPersistence;
					}
				}
			}
		}
		
		float fMin = surfaceDst.Pixels[0];
		float fMax = surfaceDst.Pixels[0];
		for( int i = 0 ; i < sw*sh ; i++ )
		{
			fMin = FMath.Min( fMin, surfaceDst.Pixels[i] );
			fMax = FMath.Max( fMax, surfaceDst.Pixels[i] );
		}
		
		Pixels = ByteBuffer.allocateDirect( sw*sh );
		
		for( int i = 0 ; i < sw*sh ; i++ )
		{
			float p = surfaceDst.Pixels[i];
			p = (p-fMin)/(fMax-fMin);
			byte pixel = (byte) FMath.Clamp( p*255.f, 0.0f, 255.0f );
			Pixels.put( pixel );
		}
		
		
		Pixels.position(0);
		
	}
}
