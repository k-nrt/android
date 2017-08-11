package com.nrt.anim;

import java.io.InputStream;
import com.nrt.model.*;
import com.nrt.framework.SubSystem;
import com.nrt.framework.*;
import com.nrt.render.*;

public class AnimRig
{

	public AnimJoint[] Joints = new AnimJoint[0];

	//public AnimRig() {}
	public AnimRig( String strPath )
	//throws ThreadForceDestroyException
	{
		InputStream stream = SubSystem.Loader.OpenAsset( strPath );
		Model.Loader loader = new Model.Loader( stream );
		//try
		{
			Model model = loader.ReadModel(null);
			loader.Close();
			Initialize( model );			
		}
		/*
		catch( nrt.render.ThreadForceDestroyException ex )
		{
			
		}
		*/
	}

	public AnimRig( Model model )
	{
		Initialize( model );
	}

	private void Initialize( Model model )
	{
		Joints = new AnimJoint[	model.Joints.length];

		for( int i = 0 ; i < model.Joints.length ; i++ )
		{
			ModelJoint joint = model.Joints[i];
			Joints[i] = new AnimJoint( joint.Name, joint.Local, joint.BindPoseInverse, joint.Parent );
		}
	}
}	

