package com.nrt.collision;

public class CollisionMesh
{
	public CollisionElement Root = null;

	public CollisionMesh()
	{
	}

	public boolean IntersectToSphere(Sphere sphere, Intersection intersectionNearest)
	{
		return Root.IntersectToSphere( sphere, intersectionNearest );
	}

	public boolean Intersect(Edge edge, Intersection intersection )
	{
		return Root.Intersect(edge, intersection );
	}
}
