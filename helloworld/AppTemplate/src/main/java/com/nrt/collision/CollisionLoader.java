package com.nrt.collision;

import java.io.InputStream;
import com.nrt.basic.LoaderBase;
import com.nrt.basic.DebugLog;
import com.nrt.math.Float3;

public class CollisionLoader extends LoaderBase
{
	//public Reader ( System.IO.Stream stream ) : base( stream ) {}
	public CollisionLoader(InputStream stream)
	{
		super(stream);
	}

	public BoundingBox ReadBoundingBox()
	{
		BoundingBox box = new BoundingBox();
		box.Min.Set( ReadFloat3() );
		box.Max.Set( ReadFloat3() );
		return box;
		// Write(box.Min);
		// Write(box.Max);
	}

	public Triangle ReadTriangle()
	{
		Float3 v3Normal = ReadFloat3();
		Float3 v3Vertex0 = ReadFloat3();
		Float3 v3Vertex1 = ReadFloat3();
		Float3 v3Vertex2 = ReadFloat3();
		return new Triangle(v3Normal, v3Vertex0, v3Vertex1, v3Vertex2);

		// Write(triangle.Normal);
		//foreach (Nrt.Float3 f3Vertex in triangle.Vertices)
		//{
		//	Write(f3Vertex);
		//}
	}

	public Polygon ReadPolygon()
	{
		Polygon polygon = new Polygon();
		polygon.Triangle = ReadTriangle();
		int nbVertices = ReadInt32();

		polygon.Vertices = new Float3[nbVertices];

		for (int i = 0 ; i < nbVertices ; i++)
		{
			polygon.Vertices[i] = ReadFloat3();
			/*
			 System.Error.WriteLine(Integer.toString(i)+":"+
			 Float.toString( polygon.Vertices[i].X ) +","+
			 Float.toString( polygon.Vertices[i].Y ) +","+
			 Float.toString( polygon.Vertices[i].Z ) );*/
		}

		return polygon;

		//Write(polygon.Triangle);
		//Write(polygon.Vertices.Length);
		//foreach (Nrt.Float3 f3Vertex in polygon.Vertices)
		//{
		//	Write(f3Vertex);
		//}
	}

	public CollisionElement ReadElement()
	{
		CollisionElement element = new CollisionElement();
		element.Box.Set(  ReadBoundingBox() );
		boolean isExsistFront = ReadBoolean();
		boolean isExsistBack  = ReadBoolean();
		int nbPolygons = ReadInt32();

		//System.Error.WriteLine(
		//	"F " + Boolean.toString(isExsistFront) +
		//	" B " + Boolean.toString(isExsistBack) +
		//	" P " + Integer.toString(nbPolygons));

		//Write(element.Box);
		//Write((bool)(element.Front == null ? false : true ) );
		//Write((bool)(element.Back  == null ? false : true ) );

		//int nbPolygons = element.Polygons.Length;
		//if (element.Front != null || element.Back != null)
		//{
		//	nbPolygons = 0;
		//}

		//Write(nbPolygons);


		//if (nbPolygons > 0)
		//{
		//	foreach (Nrt.Collision.Polygon polygon in element.Polygons)
		//	{
		//		Write(polygon);
		//	}
		//}

		if (nbPolygons > 0)
		{
			element.Polygons = new Polygon[nbPolygons];
			for (int i = 0 ; i < nbPolygons ; i++)
			{
				element.Polygons[i] = ReadPolygon();
			}
		}

		//if (element.Front != null)
		//{
		//	Write(element.Front);
		//}

		if (isExsistFront)
		{
			element.Front = ReadElement();
		}

		//if (element.Back != null)
		//{
		//	Write(element.Back);
		//}

		if (isExsistBack)
		{
			element.Back = ReadElement();
		}

		return element;
	}

	public CollisionMesh ReadMesh()
	{
		String strVersion = ReadString();
		DebugLog.Error.WriteLine(strVersion);
		CollisionMesh mesh = new CollisionMesh();
		mesh.Root = ReadElement();

		return mesh;
		//Write("SSG Collision Mesh for PSS 1.0");
		//Write(mesh.Root);
	}
}
