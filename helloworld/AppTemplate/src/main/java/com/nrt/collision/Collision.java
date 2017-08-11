package com.nrt.collision;

import com.nrt.basic.DebugLog;
import com.nrt.math.FMath;
import com.nrt.math.Float3;

public class Collision extends FMath
{
	public static float Distance(Float3 f3Start, Float3 f3End, Float3 f3Point, Intersection intersection)
	{
		Float3 f3Direction = Float3.Local();
		Float3.Sub(f3Direction, f3End, f3Start);
		float fLength = Float3.Length(f3Direction);

		if (fLength <= 0.0f)
		{
			return Float3.Distance(f3Start, f3Point);
		}
		else
		{
			Float3.Normalize(f3Direction, f3Direction);
			Float3 f3StartToPoint = Float3.Sub(Float3.Local(), f3Point, f3Start);
			float fDot = Float3.Dot(f3StartToPoint, f3Direction);
			if (fDot < 0.0f)
			{
				intersection.Position.Set(f3Start);
				Float3.SubNormalize(intersection.Normal, f3Point, f3Start);
				intersection.Distance = Float3.Distance(f3Start, f3Point);
				return intersection.Distance;
			}
			else if (fDot > fLength)
			{
				intersection.Position.Set(f3End);
				Float3.SubNormalize(intersection.Normal, f3Point, f3End);
				intersection.Distance = Float3.Distance(f3End, f3Point);
				return intersection.Distance;
			}
			else
			{
				float a = Float3.Distance(f3Point, f3Start);
				float fDistance = FMath.Sqrt(a * a - fDot * fDot);
				Float3.Mad(intersection.Position, f3Direction, fDot, f3Start);
				Float3.SubNormalize(intersection.Normal, f3Point, intersection.Position);
				intersection.Distance = fDistance;

				DebugLog.Error.WriteLine("d");

				return fDistance;
			}
		}

	}

	public static boolean Intersect(Edge edge, Sphere sphere, Intersection intersection)
	{
		return Intersect(edge, sphere.Position, sphere.Radius, intersection);
	}

	public static boolean Intersect(Edge edge, Float3 f3Position, float fRadius, Intersection intersection)
	{
		Float3 v3StartToSphere = Float3.Sub(Float3.Local(), f3Position, edge.Start);
		float fDot = Float3.Dot(edge.Direction, v3StartToSphere);

		if (fDot <= 0.0f)
		{
			//. 始点より外側.
			float fDistance = Float3.Length(v3StartToSphere);
			if (fDistance  < fRadius)
			{
				//. 始点と接触.
				//intersection = new Ssg.Collision.Intersection();
				//intersection.Position.Set( edge.Start );
				//intersection.Normal.Set(  = Float3.Normalize( intersection.Normal,  v3StartToSphere );
				//intersection.Distance = fDistance; //. 始点と中心の距離.
				intersection.Set(edge.Start, Float3.Normalize(v3StartToSphere, v3StartToSphere), fDistance);
				return true;
			}
		}
		else if (edge.Length <= fDot)
		{
			//. 終点より外側.
			Float3 v3EndToSphere = Float3.Sub(Float3.Local(), f3Position, edge.End);
			float fDistance = Float3.Length(v3EndToSphere);
			if (fDistance  < fRadius)
			{
				//. 始点と接触.
				//intersection = new Ssg.Collision.Intersection();
				//intersection.Position = Float3.Mov( intersection.Position, edge.End );
				//intersection.Normal = Float3.Normalize( intersection.Normal, v3EndToSphere );
				//intersection.Distance = fDistance; //. 終点と中心の距離.
				intersection.Set(edge.End, Float3.Normalize(v3EndToSphere, v3EndToSphere), fDistance);
				return true;
			}
		}
		else
		{
			//. 始点と終点の間.
			float fDistance = FMath.Sqrt(Float3.LengthSquared(v3StartToSphere) - fDot * fDot);

			if (fDistance < fRadius)
			{
				//intersection = new Ssg.Collision.Intersection();
				//intersection.Position = Float3.Mad( intersection.Position, edge.Direction, fDot, edge.Start );
				//intersection.Normal = Float3.Normalize( Float3.Sub( sphere.Position, intersection.Position ) );
				//intersection.Distance = fDistance; //. 線分と中心の距離.
				Float3.Mad(intersection.Position, edge.Direction, fDot, edge.Start);
				Float3.SubNormalize(intersection.Normal, f3Position, intersection.Position);
				intersection.Distance = fDistance; //. 線分と中心の距離.
				return true;
			}
		}

		float fDistanceStart = Float3.Distance(f3Position, edge.Start);
		float fDistanceEnd = Float3.Distance(f3Position, edge.Start);

		if (fDistanceStart < fRadius && fDistanceEnd < fRadius)
		{
			if (fDistanceStart < fDistanceEnd)
			{
				intersection.Distance = fDistanceStart;
				intersection.Position.Set(edge.Start);
				Float3.SubNormalize(intersection.Normal, intersection.Position, f3Position);
			}
			else
			{
				intersection.Distance = fDistanceEnd;
				intersection.Position.Set(edge.End);
				Float3.SubNormalize(intersection.Normal, intersection.Position, f3Position);
			}

			return true;
		}

		//intersection = new Ssg.Collision.Intersection();
		return false;
	}

	public static boolean Intersect(Triangle triangle, Sphere sphere, Intersection intersection)
	{
		float fDistance = Float3.Dot(triangle.Normal, Float3.Sub(Float3.Local(), sphere.Position, triangle.Edges[0].Start));

		if (fDistance < 0.0f || sphere.Radius < fDistance)
		{
			//. 面に接していない.
			//. 中心点が裏のときも経験上取らない方が良い.
			//. ※鋭角なエッジで裏側のポリゴンに当たってしまう.

			//intersection = new Ssg.Collision.Intersection();
			return false;
		}

		//. 球の中心との距離が最短になる平面上の点.
		Float3 v3Intersection = Float3.Mad(Float3.Local(), triangle.Normal, -fDistance, sphere.Position);

		//. ポリゴン内に v33Intersectioin があるかどうか.
		Float3 v3FirstCross = Float3.Local();
		Float3.Cross(v3FirstCross, triangle.Edges[0].Direction, Float3.Sub(v3FirstCross, v3Intersection, triangle.Edges[0].Start));
		boolean isHit = true;
		for (int i = 1 ; i < 3 ; i++)
		{
			Float3 v3Cross = Float3.Local();
			Float3.Cross(v3Cross, triangle.Edges[i].Direction, Float3.Sub(v3Cross, v3Intersection, triangle.Edges[i].Start));

			if (Float3.Dot(v3FirstCross, v3Cross) < 0.0f)
			{
				isHit = false;
				break;
			}
		}

		if (isHit == true)
		{
			//. ポリゴン内で接触.
			//intersection = new Ssg.Collision.Intersection();
			//intersection.Position = v3Intersection;
			//intersection.Normal = triangle.Normal;
			//intersection.Distance = fDistance; //. 平面と中心の距離.
			intersection.Set(v3Intersection, triangle.Normal, fDistance);
			return true;
		}

		//. エッジとの当たり.
		for (int i = 0 ; i < 3 ; i++)
		{
			if (Collision.Intersect(triangle.Edges[i], sphere, intersection))
			{
				return true;
			}
		}

		//intersection = new Ssg.Collision.Intersection();
		return false;
	}

	public static boolean Intersect(Triangle triangle, Edge edge, Intersection intersection)
	//public static Intersection Intersect( Triangle triangle, Edge edge )
	{
		float fDotStart = Float3.Dot(triangle.Normal, Float3.Sub(Float3.Local(), edge.Start, triangle.Edges[0].Start));
		float fDotEnd   = Float3.Dot(triangle.Normal, Float3.Sub(Float3.Local(), edge.End,   triangle.Edges[0].Start));

		if (fDotEnd * fDotStart > 0.0f)
		{
			//intersection = new Ssg.Collision.Intersection();
			//return false;
			return false;
		}
		else if (fDotEnd == 0.0f && fDotStart == 0.0f)
		{
			//intersection = new Ssg.Collision.Intersection();
			//intersection.Position = Float3.Mov( edge.Start );
			//intersection.Normal   = Float3.Mov( triangle.Normal );
			//intersection.Distance = 0.0f;
			//return true;
			return false;
			//return new Intersection(
			//	Float3.Mov( edge.Start ),
			//	Float3.Mov( triangle.Normal ),
			//	0.0f );			
		}
		else
		{
			fDotStart = FMath.Abs(fDotStart);
			fDotEnd   = FMath.Abs(fDotEnd);

			Float3 v3Intersection = Float3.Lerp(Float3.Local(), fDotStart / (fDotStart + fDotEnd), edge.Start, edge.End);


			Float3 v3FirstCross = Float3.Cross(Float3.Local(), triangle.Edges[0].Direction, Float3.Sub(Float3.Local(), v3Intersection, triangle.Edges[0].Start));
			boolean isHit = true;
			for (int i = 1 ; i < 3 ; i++)
			{
				Float3 v3Cross = Float3.Cross(Float3.Local(), triangle.Edges[i].Direction, Float3.Sub(Float3.Local(), v3Intersection, triangle.Edges[i].Start));

				if (Float3.Dot(v3FirstCross, v3Cross) < 0.0f)
				{
					isHit = false;
					break;
				}
			}

			if (isHit)
			{
				//intersection = new Ssg.Collision.Intersection();
				//intersection.Position = v3Intersection;
				//intersection.Normal   = Float3.Mov( triangle.Normal );
				//intersection.Distance = Float3.Distance( edge.End, edge.Start )*fDotStart/(fDotStart+fDotEnd);
				//return true;
				/*
				 return new Intersection(
				 v3Intersection,
				 triangle.Normal,//Float3.Mov( triangle.Normal ),
				 //Float3.Distance( edge.End, edge.Start )*fDotStart/(fDotStart+fDotEnd) );
				 Float3.Distance( edge.Start, v3Intersection ) );
				 */
				intersection.Set( 
					v3Intersection,
					triangle.Normal,
					Float3.Distance(edge.Start, v3Intersection));
				return true;
			}
			else
			{
				//return false;
				return false;
			}
		}
	}

	public static boolean Intersect(BoundingBox box, Edge edge)
	{
		Float3 f3Min = Float3.Min(Float3.Local(), edge.Start, edge.End);
		Float3 f3Max = Float3.Max(Float3.Local(), edge.Start, edge.End);

		if (f3Max.X < box.Min.X || box.Max.X < f3Min.X ||
			f3Max.Y < box.Min.Y || box.Max.Y < f3Min.Y ||
			f3Max.Z < box.Min.Z || box.Max.Z < f3Min.Z)
		{
			return false;
		}
		return true;

		/*
		 if(	box.Min.X <= edge.Start.X && edge.Start.X <= box.Max.X  &&
		 box.Min.Y <= edge.Start.Y && edge.Start.Y <= box.Max.Y &&
		 box.Min.Z <= edge.Start.Z && edge.Start.Z <= box.Max.Z  &&
		 box.Min.X <= edge.End.X && edge.End.X <= box.Max.X  &&
		 box.Min.Y <= edge.End.Y && edge.End.Y <= box.Max.Y &&
		 box.Min.Z <= edge.End.Z && edge.End.Z <= box.Max.Z )
		 {
		 return true;
		 }

		 return false;

		 if( Intersect( edge.Start, edge.End, box.Min, box.Max ) )
		 {
		 return true;
		 }

		 if( Intersect( edge.Start.Yzx(), edge.End.Yzx(), box.Min.Yzx(), box.Max.Yzx() ) )
		 {
		 return true;
		 }

		 if( Intersect( edge.Start.Zxy(), edge.End.Zxy(), box.Min.Zxy(), box.Max.Zxy() ) )
		 {
		 return true;
		 }

		 return true;
		 */
	}

	private static boolean Intersect(Float3 v3Start, Float3 v3End, Float3 v3Min, Float3 v3Max)
	{
		float fDotStart = v3Start.X - v3Min.X;
		float fDotEnd   = v3End.X - v3Min.X;

		if (fDotStart < 0.0f && fDotEnd < 0.0f)
		{
			return false;
		}
		else if (fDotStart < 0.0f && 0.0f < fDotEnd)
		{
			fDotStart = -fDotStart;
			float fY = (fDotStart * v3End.Y + fDotEnd * v3Start.Y) / (fDotStart + fDotEnd);
			float fZ = (fDotStart * v3End.Z + fDotEnd * v3Start.Z) / (fDotStart + fDotEnd);

			if (v3Min.Y < fY && fY < v3Max.Y && v3Min.Z < fZ && fZ < v3Max.Z)
			{
				return true;
			}
		}
		else if (fDotEnd < 0.0f && 0.0f < fDotStart)
		{
			fDotEnd = -fDotEnd;
			float fY = (fDotStart * v3End.Y + fDotEnd * v3Start.Y) / (fDotStart + fDotEnd);
			float fZ = (fDotStart * v3End.Z + fDotEnd * v3Start.Z) / (fDotStart + fDotEnd);

			if (v3Min.Y < fY && fY < v3Max.Y && v3Min.Z < fZ && fZ < v3Max.Z)
			{
				return true;
			}
		}

		fDotStart = v3Max.X - v3Start.X;
		fDotEnd   = v3Max.X - v3End.X;

		if (0.0f < fDotStart && 0.0f < fDotEnd)
		{
			return false;
		}
		else if (fDotStart < 0.0f && 0.0f < fDotEnd)
		{
			fDotStart = -fDotStart;
			float fY = (fDotStart * v3End.Y + fDotEnd * v3Start.Y) / (fDotStart + fDotEnd);
			float fZ = (fDotStart * v3End.Z + fDotEnd * v3Start.Z) / (fDotStart + fDotEnd);

			if (v3Min.Y < fY && fY < v3Max.Y && v3Min.Z < fZ && fZ < v3Max.Z)
			{
				return true;
			}
		}
		else if (fDotEnd < 0.0f && 0.0f < fDotStart)
		{
			fDotEnd = -fDotEnd;
			float fY = (fDotStart * v3End.Y + fDotEnd * v3Start.Y) / (fDotStart + fDotEnd);
			float fZ = (fDotStart * v3End.Z + fDotEnd * v3Start.Z) / (fDotStart + fDotEnd);

			if (v3Min.Y < fY && fY < v3Max.Y && v3Min.Z < fZ && fZ < v3Max.Z)
			{
				return true;
			}
		}
		return false;
	}
}


