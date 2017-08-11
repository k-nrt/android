package com.nrt.anim;

import com.nrt.math.Quaternion;
import com.nrt.math.Float3;
import com.nrt.framework.SubSystem;

import java.io.InputStream;
import com.nrt.model.*;

public class AnimPose
{
	public Quaternion[] Rotations = new Quaternion[0];
	public Float3[] Positions = new Float3[0];

	public AnimPose (){}

	public AnimPose( ModelJoint[] joints )
	{
		if( joints == null )
		{
			return;
		}
		Rotations = new Quaternion[joints.length];
		Positions = new Float3[joints.length];

		for( int i = 0 ; i < joints.length ; i++ )
		{
			Rotations[i] = Quaternion.FromFloat4x4( new Quaternion(), joints[i].Local );
			Positions[i] = joints[i].Local.GetAxisW( new Float3() );
		}
	}
	
	public AnimPose( int nbJoints )
	{
		Rotations = new Quaternion[nbJoints];
		Positions = new Float3[nbJoints];

		for( int i = 0 ; i < nbJoints ; i++ )
		{
			Rotations[i] = Quaternion.LoadIdentity( new Quaternion() );
			Positions[i] = new Float3( 0.0f );
		}
	}
	
	public AnimPose Load( AnimPose p )
	{
		for( int i = 0 ; i < Rotations.length ; i++ )
		{
			Rotations[i].Load( p.Rotations[i] );
			Positions[i].Set( p.Positions[i] );
		}
		return this;
	}
	
	public static AnimPose Lerp( AnimPose r, AnimPose p0, AnimPose p1, float fLerp )
	{
		if( fLerp <= 0.0f )
		{
			return r.Load( p0 );
		}
		else if( 1.0f <= fLerp )
		{
			return r.Load( p1 );
		}
		else
		{
			for( int i = 0 ; i < r.Rotations.length ; i++ )
			{
				Quaternion.Lerp( r.Rotations[i], p0.Rotations[i], p1.Rotations[i], fLerp );
				Float3.Lerp( r.Positions[i], fLerp, p0.Positions[i], p1.Positions[i] );
			}
			return r;
		}
	}
}

