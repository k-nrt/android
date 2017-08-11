package com.nrt.clipper;

import com.nrt.math.Float3;
import com.nrt.render.*;

public class Surface
{
	public final Float3 Normal = new Float3();
	public final Float3 Position = new Float3();
	public final Float3[] Vertices =
	{
		new Float3(),
		new Float3(),
		new Float3(),
		new Float3(),
	};
	
	public Surface(){}
	public final void Update( 
		final Float3 f3Pos0, 
		final Float3 f3Pos1, 
		final Float3 f3Pos2, 
		final Float3 f3Pos3)
	{
		Float3.CrossNormalize( Normal, 
			Float3.Sub( Float3.Local(), f3Pos1, f3Pos0 ), 
			Float3.Sub( Float3.Local(), f3Pos2, f3Pos0 ) );
			
		Float3.Mul( Position,
			Float3.Add( Float3.Local(),
				Float3.Add( Float3.Local(), f3Pos0, f3Pos1 ),
				Float3.Add( Float3.Local(), f3Pos2, f3Pos3 ) ),
			0.25f );
			
		Vertices[0].Set( f3Pos0 );
		Vertices[1].Set( f3Pos1 );
		Vertices[2].Set( f3Pos2 );
		Vertices[3].Set( f3Pos3 );
	}
	
	public final void Draw( final BasicRender br )
	{
		br.Begin( EPrimitive.Lines, BasicRender.EShader.Color );
		for( int i = 0 ; i < Vertices.length ; i++ )
		{
			int i1 = (i+1)%Vertices.length;
			br.SetVertex( Vertices[i] );
			br.SetVertex( Vertices[i1] );
		}
		
		br.SetVertex( Position );
		br.SetVertex( Float3.Mad( Float3.Local(), Normal, 100.0f, Position ) );
		br.End();
	}
}

