package com.nrt.anim;

import java.io.InputStream;

import com.nrt.math.Quaternion;
import com.nrt.math.Float4x4;

import com.nrt.math.FMath;

public class AnimPoseStack
{
	public static class Node
	{
		public float Time = 0.0f;
		public float TimeOut = 0.0f;
		public AnimPose Pose = null;

		public Node() {}

		public void Update( float fElapsedTime )
		{
			if( Time < TimeOut )
			{
				Time += fElapsedTime;
			}

			if( Time > TimeOut )
			{
				Time = TimeOut;
			}
		}
	}

	public AnimRig Rig = null;
	public Node[] Nodes = null;

	public Float4x4[] ModelMatrices = null;
	public Float4x4[] WorldMatrices = null;
	
	public AnimPose RootPose = null;

	//public AnimPoseStack () {}

	public AnimPoseStack( int nDepth, AnimRig rig )
	{
		Nodes = new Node[nDepth];
		for( int i = 0 ; i < nDepth ; i++ )
		{
			Nodes[i] = new Node();
		}
		RootPose = new AnimPose(rig.Joints.length);

		Rig = rig;

		ModelMatrices = new Float4x4[Rig.Joints.length];
		WorldMatrices = new Float4x4[Rig.Joints.length];

		for( int i = 0 ; i < Rig.Joints.length; i++ )
		{
			ModelMatrices[i] = new Float4x4();
			WorldMatrices[i] = new Float4x4();

			AnimJoint joint = Rig.Joints[i];
			if( joint.Parent < 0 )
			{
				ModelMatrices[i].Set( joint.Local );
				WorldMatrices[i].Set( joint.Local );
			}
			else
			{
				Float4x4.Mul( ModelMatrices[i], joint.Local, ModelMatrices[joint.Parent] );
				Float4x4.Mul( WorldMatrices[i], joint.Local, WorldMatrices[joint.Parent] );
			}
		}
	}

	public void Push( AnimPose pose, float fTime )
	{
		Node node = Nodes[Nodes.length-1];
		if( node.Pose != null )
		{
			if( node.Time < node.TimeOut )
			{
				AnimPose.Lerp( RootPose, RootPose, node.Pose, Interpolate( node ) );
			}
			else
			{
				RootPose.Load( node.Pose );
			}
		}
		
		for( int i = Nodes.length - 1 ; i >= 1 ; i-- )
		{
			Nodes[i].Time = Nodes[i-1].Time;
			Nodes[i].TimeOut = Nodes[i-1].TimeOut;
			Nodes[i].Pose = Nodes[i-1].Pose;
		}

		Nodes[0].Time = 0.0f;
		Nodes[0].TimeOut = fTime;
		Nodes[0].Pose = pose;
	}

	private float Interpolate( Node node )
	{
		return -FMath.Cos( FMath.PI*(node.Time/node.TimeOut) )*0.5f + 0.5f;		
	}
	
	public void Update( float fElapsedTime, Float4x4 matrixWorld )
	{
		
		for( int i = 0 ; i < Rig.Joints.length; i++ )
		{
			Quaternion quat = Quaternion.Local().Load( RootPose.Rotations[i] );
			for( int j = Nodes.length - 1 ; j >= 0 ; j-- )
			{
				Node node = Nodes[j];
				if( node.Pose != null && node.TimeOut > 0.0f )
				{
					Quaternion.Lerp( quat, quat, node.Pose.Rotations[i], Interpolate( node ) );
					//quat = node.Pose.Rotations[i];
				}
			}

			AnimJoint joint = Rig.Joints[i];
			if( joint.Parent < 0 )
			{
				//WorldMatrices[i] = matrixWorld*joint.Local*quat.ToMatrix4();
				Float4x4.Mul( WorldMatrices[i], Float4x4.Rotation( Float4x4.Local(), quat ), joint.Local, matrixWorld );
				//WorldMatrices[i].Set( Float4x4.Mul( Float4x4.Local(), Float4x4.Rotation( quat ), joint.Local, matrixWorld ) );
				//Float4x4 r = Float4x4.Local().Set( Float4x4.Rotation( quat ) );
				//Float4x4.Mul( WorldMatrices[i],r, joint.Local );
				//Float4x4.Mul( WorldMatrices[i], WorldMatrices[i], matrixWorld );


				//WorldMatrices[i] = Float4x4.Mul( r, joint.Local );
				//WorldMatrices[i] = Float4x4.Mul(WorldMatrices[i], matrixWorld );

			}
			else
			{
				//WorldMatrices[i] = WorldMatrices[joint.Parent]*joint.Local*quat.ToMatrix4();
				Float4x4.Mul( WorldMatrices[i], Float4x4.Rotation( Float4x4.Local(), quat ), joint.Local, WorldMatrices[joint.Parent] );
				//WorldMatrices[i].Set( Float4x4.Mul( Float4x4.Local(), Float4x4.Rotation( quat ), joint.Local, WorldMatrices[joint.Parent] ) );
				//Float4x4.Mul( WorldMatrices[i], Float4x4.Rotation( quat ), joint.Local );
				//Float4x4.Mul( WorldMatrices[i], WorldMatrices[i], WorldMatrices[joint.Parent] );
			}

			//ModelMatrices[i].Set( Float4x4.Mul( Float4x4.Local(),

			//Float4x4 m = new Float4x4();
			Float4x4.Mul( ModelMatrices[i],
						 joint.BindPoseInverse,
						 WorldMatrices[i] );

			//ModelMatrices[i].Set( m );

			//ModelMatrices[i] = Float4x4.Mul( joint.BindPoseInverse, WorldMatrices[i] );
		}

		for( int i = 0 ; i < Nodes.length ; i++ )
		{
			Nodes[i].Update( fElapsedTime );
		}
		
	}
}
