package com.nrt.clipper;

import com.nrt.math.*;

public class Frustum
{
	public final Surface[] Surfaces =
	{
		new Surface(), new Surface(), new Surface(),
		new Surface(), new Surface(), new Surface(),
	};

	public Frustum()
	{}

	public final void Update(final Float4x4 matrixProjection, final Float4x4 matrixView)
	{
		Float4[] f4Positions =
		{
			new Float4(-1.0f, -1.0f, -1.0f, 1.0f),
			new Float4(1.0f, -1.0f, -1.0f, 1.0f),
			new Float4(1.0f,  1.0f, -1.0f, 1.0f),
			new Float4(-1.0f,  1.0f, -1.0f, 1.0f),
			new Float4(-1.0f, -1.0f,  1.0f, 1.0f),
			new Float4(1.0f, -1.0f,  1.0f, 1.0f),
			new Float4(1.0f,  1.0f,  1.0f, 1.0f),
			new Float4(-1.0f,  1.0f,  1.0f, 1.0f),
		};

		Float4x4 matrixProjectionInverse = Float4x4.Invert(Float4x4.Local(), matrixProjection);
		Float4x4 matrixViewInverse  = Float4x4.Invert(Float4x4.Local(), matrixView);

		for (int i = 0 ; i < 8 ; i++)
		{
			f4Positions[i].Set( 
				Float4x4.Mul(Float4.Local(),
							 f4Positions[i], 
							 matrixProjectionInverse 
							 )
			);
			float w = f4Positions[i].W;
			f4Positions[i].X /= w;
			f4Positions[i].Y /= w;
			f4Positions[i].Z /= w;
			f4Positions[i].W = 1.0f;

			f4Positions[i].Set(
				Float4x4.Mul(Float4.Local(),
							 f4Positions[i],
							 matrixViewInverse
							 )
			);
		}
		//32 76
		//01 45
		
		int[] indices =
		{
			0,4,7,3,
			1,2,6,5,
			2,3,7,6,
			0,1,5,4,				
			0,3,2,1,
			4,5,6,7,
		};

		for (int i = 0 ; i < 6 ; i++)
		{
			int i0 = indices[i * 4 + 0];
			int i1 = indices[i * 4 + 1];
			int i2 = indices[i * 4 + 2];
			int i3 = indices[i * 4 + 3];

			Surfaces[i].Update(
				Float3.Local(f4Positions[i0]),
				Float3.Local(f4Positions[i1]),
				Float3.Local(f4Positions[i2]),
				Float3.Local(f4Positions[i3])
			);
		}
	}
	
	public final void Draw( com.nrt.render.BasicRender br )
	{
		for( int i = 0 ; i < Surfaces.length ; i++ )
		{
			Surfaces[i].Draw(br);
		}
	}
}
