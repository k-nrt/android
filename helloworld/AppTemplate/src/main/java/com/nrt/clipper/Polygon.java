package com.nrt.clipper;

import com.nrt.math.Float3;
import com.nrt.math.*;
import com.nrt.render.*;

public class Polygon
{
	public final Float3[] Vertices = new Float3[]
	{
		new Float3(), new Float3(), new Float3(), new Float3(), new Float3(),
		new Float3(), new Float3(), new Float3(), new Float3(), new Float3(),
	};
	
	private final float[] dots = new float[10];
	
	public int VertexCount = 0;
	
	public Polygon(){}
	
	public final void Update( 
		final Float3 f3Pos0, 
		final Float3 f3Pos1, 
		final Float3 f3Pos2,
		final Float3 f3Pos3 )
	{
		Vertices[0].Set( f3Pos0 );
		Vertices[1].Set( f3Pos1 );
		Vertices[2].Set( f3Pos2 );
		Vertices[3].Set( f3Pos3 );
		VertexCount = 4;
	}
	
	public final void Clear()
	{
		VertexCount = 0;
	}
	
	public final void AddVertex( final Float3 f3Vertex )
	{
		Vertices[VertexCount].Set( f3Vertex );
		VertexCount++;
	}
	
	public final Polygon SccisorBackFace( Polygon result, final Surface surface )
	{
		result.Clear();
		
		for( int i = 0 ; i < VertexCount ; i++ )
		{
			dots[i] = Float3.Dot( 
				Float3.Sub( Float3.Local(),
					Vertices[i], 
					surface.Position ),
				surface.Normal );
		}
		
		for( int i = 0 ; i < VertexCount ; i++ )
		{
			int i0 = i;
			int i1 = (i+1)%VertexCount;
			
			if( 0.0f <= dots[i] )
			{
				result.AddVertex( Vertices[i] );
			}
			
			if( dots[i0]*dots[i1] < 0.0f )
			{

				float d0 = FMath.Abs( dots[i0] );
				float d1 = FMath.Abs( dots[i1] );
				float length = d0 + d1;
				
				result.AddVertex( 
					Float3.Div( Float3.Local(),
						Float3.Mad( Float3.Local(), 
							Vertices[i0],
							d1, 
							Float3.Mul( Float3.Local(), 
								Vertices[i1], 
								d0
							)
						),
						length
					)
				);
			}
		}
		
		return result;		
	}
	
	public final void Draw( final BasicRender br )
	{
		br.Begin( EPrimitive.Lines, BasicRender.EShader.Color );
		for( int i = 0 ; i < VertexCount ; i++ )
		{
			int i1 = (i+1)%VertexCount;
			br.SetVertex( Vertices[i] );
			br.SetVertex( Vertices[i1] );
		}
		br.End();
		
	}
}
