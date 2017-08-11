package com.nrt.clipper;
import com.nrt.math.*;
import com.nrt.render.*;

public class Box
{
	public final Polygon[] Polygons = new Polygon[]
	{
		new Polygon(), new Polygon(), new Polygon(),
		new Polygon(), new Polygon(), new Polygon(),
	};
	public int PolygonCount = 0;
	
	public final Float3 Center = new Float3();
	public float Radius = 0.0f;
	
	public final Float3 Min = new Float3();
	public final Float3 Max = new Float3();
	
	public Box() 
	{
	}
	
	public final void Update( final Float3 f3Min, final Float3 f3Max, final Float4x4 matrixWorld )
	{
		Float4[] f4Vertices =
		{
			Float4.Local( -1.0f, -1.0f, -1.0f, 1.0f ),
			Float4.Local(  1.0f, -1.0f, -1.0f, 1.0f ),
			Float4.Local(  1.0f,  1.0f, -1.0f, 1.0f ),
			Float4.Local( -1.0f,  1.0f, -1.0f, 1.0f ),
			Float4.Local( -1.0f, -1.0f,  1.0f, 1.0f ),
			Float4.Local(  1.0f, -1.0f,  1.0f, 1.0f ),
			Float4.Local(  1.0f,  1.0f,  1.0f, 1.0f ),
			Float4.Local( -1.0f,  1.0f,  1.0f, 1.0f ),
		};
		
		
		
		for( int i = 0 ; i < 8 ; i++ )
		{
			Float4 f4Vertex = Float4.Local(
				((f4Vertices[i].X < 0.0f ) ? f3Min.X : f3Max.X),
				((f4Vertices[i].Y < 0.0f ) ? f3Min.Y : f3Max.Y),
				((f4Vertices[i].Z < 0.0f ) ? f3Min.Z : f3Max.Z),
				1.0f);

			f4Vertices[i].Set(
				Float4x4.Mul( Float4.Local(),
					f4Vertex,
					matrixWorld 
				)
			);		
		}
		
		Center.Set(
			Float4x4.Mul( Float4.Local(),
				Float4.Local( 
					Float3.Mul( Float3.Local(),
						Float3.Add( Float3.Local(),f3Min, f3Max ),
						0.5f ),
					1.0f ), 
				matrixWorld ) 
			);
				
		Radius = Float3.Distance( f3Min, f3Max )*0.5f;
		/*
		32 76
		01 45
		*/
		
		int[] indices =
		{
			2,3,7,6,
			0,1,5,4,
			0,3,7,4,
			1,5,6,2,
			0,1,2,3,
			7,6,5,4,
		};
		
		for( int i = 0 ; i < 6 ; i++ )
		{
			Polygons[i].Update(
				Float3.Local( f4Vertices[indices[i*4+0]] ),
				Float3.Local( f4Vertices[indices[i*4+1]] ),
				Float3.Local( f4Vertices[indices[i*4+2]] ),
				Float3.Local( f4Vertices[indices[i*4+3]] )
			);
		}
		
		PolygonCount = 6;
	}
	private final Polygon[] s_polygons = { new Polygon(), new Polygon() };
	public final void ScissorBackFace( Box box, Surface[] surfaces )
	{
		int p = 0;
		box.PolygonCount = 0;
		for( int i = 0; i < Polygons.length ;i++ )
		{
			Polygons[i].SccisorBackFace( s_polygons[p], surfaces[0] ); 
			
			p ^= 1;
			for( int j = 1 ; j < (surfaces.length-1) ; j++, p ^= 1 )
			{
				s_polygons[p^1].SccisorBackFace( s_polygons[p], surfaces[j] );
			}
			
			s_polygons[p^1].SccisorBackFace( box.Polygons[i], surfaces[surfaces.length-1] );
			if( box.Polygons[i].VertexCount > 0 )
			{
				box.PolygonCount++;
			}
		}
	}
	
	public final void Draw( BasicRender br )
	{
		for( int i = 0 ; i < 6 ; i++ )
		{
			Polygons[i].Draw(br);
		}
	}
}

