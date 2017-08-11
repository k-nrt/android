package com.nrt.anim;

import com.nrt.math.Float4x4;

public class AnimJoint
{
	public String Name = "";
	public Float4x4 Local = Float4x4.Identity();
	public Float4x4 BindPoseInverse = Float4x4.Identity();
	public int Parent = -1;

	//public AnimJoint(){}

	public AnimJoint( String name, Float4x4 local, Float4x4 bindPoseInverse, int parent )
	{
		Name = name;
		Local.Set( local );
		BindPoseInverse.Set( bindPoseInverse );
		Parent = parent;
	}
}

