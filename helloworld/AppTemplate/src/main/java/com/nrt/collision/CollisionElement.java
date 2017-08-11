package com.nrt.collision;

import com.nrt.math.FMath;
import com.nrt.render.BasicRender;
import com.nrt.render.EPrimitive;


public class CollisionElement
{
	public final BoundingBox Box = new BoundingBox();

	public CollisionElement Front = null;
	public CollisionElement Back = null;

	public Polygon[] Polygons = null;

	public CollisionElement()
	{
	}

	/// <summary>
	/// Intersect the specified sphere and intersection.
	/// </summary>
	/// <param name='sphere'>
	/// If set to <c>true</c> sphere.
	/// </param>
	/// <param name='intersection'>
	/// If set to <c>true</c> intersection.
	/// </param>
	public boolean IntersectToSphere(Sphere sphere, Intersection intersectionNearest )
	{
		if( Box.IntersectToSphere( sphere ) == false )
		{
			return false;
		}

		if (Polygons != null)
		{
			boolean isHit = false;
			//Intersection intersectionNearest = new Intersection();
			for (Polygon polygon : Polygons)
			{
				//Intersection intersectionTemp = new Intersection();
				//if (Collision.Intersect(polygon.Triangle, sphere, intersectionTemp))
				if( polygon.Triangle.IntersectToSphere( sphere, intersectionNearest ) )
				{
					isHit = true;
				}
			}

			if (isHit)
			{
				return true;
			}
		}
		else
		{
			boolean isHitFront = false;
			if (Front != null)
			{
				isHitFront = Front.IntersectToSphere( sphere, intersectionNearest );
			}

			boolean isHitBack = false;
			if (Back != null)
			{
				isHitBack = Back.IntersectToSphere(sphere, intersectionNearest );
			}

			if ( isHitFront || isHitBack )
			{
				return true;
			}
		}
		
		return false;
	}

	/// <summary>
	/// Intersect the specified edge and intersection.
	/// </summary>
	/// <param name='edge'>
	/// If set to <c>true</c> edge.
	/// </param>
	/// <param name='intersection'>
	/// If set to <c>true</c> intersection.
	/// </param>
	Intersection m_intersectionNearest = new Intersection();
	Intersection m_intersectionTemp = new Intersection();
	Intersection m_intersectionFront = new Intersection();
	Intersection m_intersectionBack = new Intersection();
	public boolean Intersect(final Edge edge, Intersection intersection )
	{
		//System.Error.WriteLine( "element" );

		if (Collision.Intersect(Box, edge) == false)
		{
			//intersection = new Ssg.Collision.Intersection();
			return false;
		}

		//System.Error.WriteLine( "box" );

		if (Polygons == null && Front == null && Back == null)
		{
			//System.Error.WriteLine( "nop" );
		}


		if (Front == null && Back == null)
		{
			//Intersection intersectionNearest = Intersection.Local();
			//intersectionNearest.Clean();
			//Intersection intersectionTemp = Intersection.Local();//FMath.Intersect(polygon.Triangle, edge);

			//System.Error.WriteLine( "polygon" );
			m_intersectionNearest.Clean();

			for (Polygon polygon : Polygons)
			{
				//Intersection intersectionTemp = new Intersection();
				//if ( FMath.Intersect( polygon.Triangle, edge, intersectionTemp ) )
				//m_intersectionTemp.Clean();
				//m_intersectionTemp = new Intersection();

				if ( Collision.Intersect( polygon.Triangle, edge, m_intersectionTemp ) )
				{
					//System.Error.WriteLine( "h" );
					if (m_intersectionNearest.Distance < 0.0f || m_intersectionTemp.Distance < m_intersectionNearest.Distance)
					{
						m_intersectionNearest.Set( m_intersectionTemp );//.Clone();
					}
				}
			}

			if (m_intersectionNearest.Distance >= 0.0f)
			{
				//intersection.CopyFrom( intersectionNearest );
				//System.Error.WriteLine( "tri" );

				intersection.Set( m_intersectionNearest );
				return true;
			}
			else
			{
				//intersection = new Ssg.Collision.Intersection();
				return false;
			}
		}

		else
		{
			//Intersection intersectionFront = Intersection.Local();//null;//new Intersection();
			boolean isFront = false; 
			if (Front != null)
			{
				//System.Error.WriteLine( "f" );

				isFront = Front.Intersect(edge, m_intersectionFront );
			}

			//Intersection intersectionBack = Intersection.Local();//null;//new Intersection();
			boolean isBack = false;
			if (Back != null)
			{
				//System.Error.WriteLine( "b" );

				isBack = Back.Intersect(edge, m_intersectionBack);
			}

			if ( isFront == false && isBack == false)
			{
				//intersection = new Ssg.Collision.Intersection();
				return false;
			}
			else if (isFront == false && isBack == true)
			{
				//System.Error.WriteLine( "b" );
				//intersection.CopyFrom( intersectionBack );
				intersection.Set( m_intersectionBack );
				return true;
			}
			else if (isBack == false && isFront == true)
			{
				//System.Error.WriteLine( "f" );

				//intersection.CopyFrom( intersectionFront );
				intersection.Set( m_intersectionFront );
				return true;
			}
			else
			{

				if (m_intersectionFront.Distance < m_intersectionBack.Distance)
				{
					//System.Error.WriteLine( "f" );

					intersection.Set( m_intersectionFront );
				}
				else
				{
					//System.Error.WriteLine( "b" );

					intersection.Set( m_intersectionBack );
				}

				return true;					
			}
		}
	}

	public void Render(final BasicRender br)
	{

		br.Begin(EPrimitive.Lines, BasicRender.EShader.Color);
		br.SetColor(1.0f, 0.0f, 0.0f, 1.0f);

		br.SetVertex(Box.Min.X, Box.Min.Y, Box.Min.Z);
		br.SetVertex(Box.Min.X, Box.Max.Y, Box.Min.Z);

		br.SetVertex(Box.Max.X, Box.Min.Y, Box.Min.Z);
		br.SetVertex(Box.Max.X, Box.Max.Y, Box.Min.Z);

		br.SetVertex(Box.Min.X, Box.Min.Y, Box.Min.Z);
		br.SetVertex(Box.Max.X, Box.Min.Y, Box.Min.Z);

		br.SetVertex(Box.Min.X, Box.Max.Y, Box.Min.Z);
		br.SetVertex(Box.Max.X, Box.Max.Y, Box.Min.Z);


		br.SetVertex(Box.Min.X, Box.Min.Y, Box.Max.Z);
		br.SetVertex(Box.Min.X, Box.Max.Y, Box.Max.Z);

		br.SetVertex(Box.Max.X, Box.Min.Y, Box.Max.Z);
		br.SetVertex(Box.Max.X, Box.Max.Y, Box.Max.Z);

		br.SetVertex(Box.Min.X, Box.Min.Y, Box.Max.Z);
		br.SetVertex(Box.Max.X, Box.Min.Y, Box.Max.Z);

		br.SetVertex(Box.Min.X, Box.Max.Y, Box.Max.Z);
		br.SetVertex(Box.Max.X, Box.Max.Y, Box.Max.Z);

		br.SetVertex(Box.Min.X, Box.Min.Y, Box.Min.Z);
		br.SetVertex(Box.Min.X, Box.Min.Y, Box.Max.Z);

		br.SetVertex(Box.Min.X, Box.Max.Y, Box.Min.Z);
		br.SetVertex(Box.Min.X, Box.Max.Y, Box.Max.Z);

		br.SetVertex(Box.Max.X, Box.Min.Y, Box.Min.Z);
		br.SetVertex(Box.Max.X, Box.Min.Y, Box.Max.Z);

		br.SetVertex(Box.Max.X, Box.Max.Y, Box.Min.Z);
		br.SetVertex(Box.Max.X, Box.Max.Y, Box.Max.Z);


		br.End();

		if (Front != null)
		{
			Front.Render(br);
		}

		if (Back != null)
		{
			Back.Render(br);
		}
	}
}
