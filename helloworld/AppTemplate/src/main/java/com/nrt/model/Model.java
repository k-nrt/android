package com.nrt.model;

import java.io.InputStream;
import com.nrt.math.Float3;
import com.nrt.basic.LoaderBase;
import com.nrt.render.*;


public class Model
{

	/*
	 public static class ModelJoint
	 {
	 public String Name = "";
	 public int Parent = -1;
	 public Float4x4 Local = Float4x4.Identity();
	 public Float4x4 BindPoseInverse = Float4x4.Identity();
	 }
	 */


//	public static class ModelMaterial
//	{
//		public String Name = "";
//		public String Shader = "";
//		public String Technique = "";
//		public Float4 Diffuse = new Float4( 0.0f );
//		public Float4 Ambient = new Float4( 0.0f );
//		public Float4 Specular = new Float4( 0.0f );
//		public Float4 Emission = new Float4( 0.0f );
//		public float Power = 0.0f;
//		public float FresnelIndex = 0.0f;
//		public int MeshStart = 0;
//		public int MeshCount = 0;
//		public String DiffuseAlpha = "";
//		public String AmbientOcclusion = "";
//		public String Normal = "";
//
//	}
//
	/*
	 public static class ModelLocator
	 {
	 public String Name = "";
	 public int Joint = 0;
	 public Float4x4 Local = Float4x4.Identity();
	 public Float3 Scale = new Float3(1.0f); 
	 }
	 */
	/*
	 public static class ModelMesh
	 {
	 public String Name = "";
	 public int Stride = 0;
	 public AttributeFormat[] Formats = null;
	 public byte[] Vertices = null;
	 public short[] Indices = null;

	 public VertexStream Stream = null;
	 public StaticVertexBuffer VertexBuffer = null;
	 public StaticIndexBuffer IndexBuffer = null;
	 }
	 public static class AttributeFormat
	 {
	 public int Format = 0;
	 public int ComponentCount = 0;
	 public boolean Normalize = false;
	 public int Offset = 0;
	 }
	 */	
	public ModelJoint[] Joints = null;
	public ModelMaterial[] Materials = null;
	public ModelLocator[] Locators = null;
	public ModelMesh[] Meshes = null;

	public final Float3 Min = new Float3(0.0f);
	public final Float3 Max = new Float3(0.0f);
	
	public final Float3 Center = new Float3(0.0f);
	public float Radius = 0.0f;

	public ModelShaderSet.EDeformation Deformation = ModelShaderSet.EDeformation.Rigid;

	public static class Loader extends LoaderBase
	{
		//InputStream Stream = null;
		public Loader( InputStream stream )
		{
			super( stream );
			//Stream = stream;			
		}
		/*
		 public void Close()
		 {
		 try
		 {
		 Stream.close();
		 }
		 catch( IOException e )
		 {
		 return;
		 }
		 }

		 int ReadByte()
		 {
		 try
		 {
		 return Stream.read();
		 }
		 catch( IOException e )
		 {
		 return 0;
		 }
		 }

		 byte[] ReadBytes( int size )
		 {
		 byte[] result = new byte[size];
		 try
		 {
		 Stream.read( result );
		 }
		 catch( IOException e )
		 {
		 result = null;
		 }
		 return result;
		 }

		 int Read7BitEncodedInt()
		 {
		 int result = 0;
		 for( int i = 0 ; i < 32 ; i+=7 )
		 {
		 int v = ReadByte();
		 result |= (v & 0x7f) << i;
		 if( (v & 0x80) == 0 )
		 {
		 break;
		 }
		 }

		 return result;
		 }

		 String ReadString()
		 {
		 int length = Read7BitEncodedInt();
		 String result = "";

		 for( int i = 0 ; i < length ; i++ )
		 {
		 result += (char) ReadByte();
		 }

		 return result;
		 }

		 int ReadInt32()
		 {
		 int result = 0;
		 for( int i = 0 ; i < 32 ; i += 8 )
		 {
		 result |= ReadByte() << i;
		 }

		 return result;
		 }

		 short ReadUInt16()
		 {
		 int result = 0;
		 for( int i = 0 ; i < 16 ; i += 8 )
		 {
		 result |= ReadByte() << i;
		 }

		 return (short) result;
		 }

		 boolean ReadBoolean()
		 {
		 return ( ReadByte() == 0 ? true : false );
		 }

		 float ReadSingle()
		 {
		 return Float.intBitsToFloat( ReadInt32() );
		 }

		 Float3 ReadFloat3()
		 {
		 float x = ReadSingle();
		 float y = ReadSingle();
		 float z = ReadSingle();
		 return new Float3( x, y, z );
		 }

		 Float4x4 ReadFloat4x4()
		 {
		 float[] values = new float[16];
		 for( int i = 0; i < 16 ; i++ )
		 {
		 values[i] = ReadSingle();
		 }
		 return new Float4x4( values );
		 }

		 Float4 ReadFloat4()
		 {
		 float x = ReadSingle();
		 float y = ReadSingle();
		 float z = ReadSingle();
		 float w = ReadSingle();
		 return new Float4( x, y, z, w );			
		 }
		 */
		public Model ReadModel(DelayResourceQueue drq)
		//throws ThreadForceDestroyException
		{
			Model model = new Model();

			String strVersion = ReadString(); // Write("SSG Model for PSS 1.0");

			int nbJoints = ReadInt32(); //Write(model.Joints.Count);
			model.Joints = new ModelJoint[nbJoints];

			int nbMaterials = ReadInt32(); //Write(model.Materials.Count);
			model.Materials = new ModelMaterial[nbMaterials];

			int nbLocators = ReadInt32();
			model.Locators = new ModelLocator[nbLocators];

			int nbMeshs     = ReadInt32(); //Write(model.Meshes.Count);
			model.Meshes = new ModelMesh[nbMeshs];

			model.Min.Set( ReadFloat3() ); // Write(model.Min);
			model.Max.Set( ReadFloat3() ); // Write(model.Max);
			
			Float3.Mul( model.Center,
				Float3.Add( Float3.Local(), model.Min, model.Max ),
				0.5f );
				
			model.Radius = Float3.Distance( model.Max, model.Min )*0.5f;

			for (int i = 0 ; i < nbJoints ; i++)
			{
				model.Joints[i] = ReadJoint();
			}

			for (int i = 0 ; i < nbMaterials ; i++)
			{
				model.Materials[i] = ReadMaterial();
			}

			for (int i = 0 ; i < nbLocators ; i++)
			{
				model.Locators[i] = ReadLocator();
			}

			for (int i = 0 ; i < nbMeshs ; i++)
			{
				model.Meshes[i] = ReadMesh(drq);
			}

			model.Deformation = (
					(model.Joints.length) <= 0 ?
					ModelShaderSet.EDeformation.Rigid :
					ModelShaderSet.EDeformation.RigidSkin );
			
			for( int i = 0 ; i < model.Materials.length ; i++ )
			{
				ModelMaterial material = model.Materials[i];
				
				if( material.DiffuseAlpha.length() <= 0 )
				{
					if( material.Normal.length() <= 0 )
					{
						material.Technique = ModelShaderSet.EType.NoTexture;
					}

					else
					{
						material.Technique = ModelShaderSet.EType.Normal;
					}
				}
				else
				{
					if( material.Normal.length() <= 0 )
					{
						material.Technique = ModelShaderSet.EType.Diffuse;
					}
					else
					{
						material.Technique = ModelShaderSet.EType.DiffuseNormal;
					}
				}			
			}
			

			return model;		
		}


		public ModelJoint ReadJoint()
		{
			String strVersion = ReadString(); // Write( "SSG Joint for PSS 1.0" );

			ModelJoint joint = new ModelJoint();

			joint.Name = ReadString(); // Write(joint.Name);
			joint.Parent = ReadInt32(); // Write(joint.Parent);
			joint.Local = ReadFloat4x4(); // Write(joint.Local);
			joint.BindPoseInverse = ReadFloat4x4(); // Write(joint.BindPoseInverse);

			return joint;
		}


		public ModelMaterial ReadMaterial()
		{
			String strVersion = ReadString(); //Write("SSG Material for PSS 1.0");

			ModelMaterial material = new ModelMaterial();

			material.Name = ReadString(); // Write(material.Name);
			material.Shader = ReadString(); // Write(material.Shader);
			material.TechniqueName = ReadString(); // Write(material.Technique);
			material.Diffuse.Set( ReadFloat4() ); // Write(material.Diffuse);
			material.Ambient.Set( ReadFloat4() ); // Write(material.Ambient);
			material.Specular.Set( ReadFloat4() ); // Write(material.Specular);
			material.Emission.Set( ReadFloat4() ); // Write(material.Emission);
			material.Power = ReadSingle(); // Write(material.Power);
			material.FresnelIndex = ReadSingle(); // Write(material.FresnelIndex);
			material.MeshStart = ReadInt32(); 
			material.MeshCount = ReadInt32();
			material.DiffuseAlpha = ReadString(); // Write((material.DiffuseAlpha == null ? "" : material.DiffuseAlpha));
			material.AmbientOcclusion = ReadString(); // Write((material.AmbientOcclusion == null ? "" : material.AmbientOcclusion));
			material.Normal = ReadString(); // Write((material.Normal == null ? "" : material.Normal));

			return material;
		}

		public ModelLocator ReadLocator()
		{
			String strVersion = ReadString();
			ModelLocator locator = new ModelLocator();
			locator.Name = ReadString();
			locator.Joint = ReadInt32();
			locator.Local = ReadFloat4x4();
			locator.Scale = ReadFloat3();

			return locator;
		}

		public ModelMesh ReadMesh( DelayResourceQueue drq)
		//throws ThreadForceDestroyException
		{
			String strVersion = ReadString(); //Write("SSG Mesh for PSS 1.0");

			ModelMesh mesh = new ModelMesh();

			mesh.Name = ReadString(); // Write(mesh.Name);

			int nbFormats = ReadInt32(); //	Write(mesh.Formats.Length);
			int nbVertices = ReadInt32(); //Write(mesh.Vertices.Length);
			int nbIndices = ReadInt32(); //	Write(mesh.Indices.Count);
			mesh.Stride = ReadInt32(); //Write(mesh.Stride);

			mesh.Formats = new ModelMesh.AttributeFormat[nbFormats];

			for (int i = 0 ; i < nbFormats ; i++)
			{
				mesh.Formats[i] = ReadAttributeFormat();
			}

			mesh.Vertices = ReadBytes(nbVertices * mesh.Stride);

			mesh.Indices = new short[nbIndices];
			for (int i = 0 ; i < nbIndices ; i++)
			{
				mesh.Indices[i] = ReadUInt16();
			}

			VertexAttribute[] attributes = new VertexAttribute[nbFormats];
			for( int i = 0 ; i < nbFormats ; i++ )
			{
				ModelMesh.AttributeFormat format = mesh.Formats[i];
				attributes[i] = new VertexAttribute
				(
					i,
					format.ComponentCount,
					EVertexAttributeFormat.Find(format.Format),
					format.Normalize,
					format.Offset
				);
			}

			mesh.Stream = new VertexStream( attributes, mesh.Stride );

			mesh.VertexBuffer = new StaticVertexBuffer( drq, mesh.Vertices );
			mesh.IndexBuffer = new StaticIndexBuffer( drq, mesh.Indices );

			/*
			 Sce.Pss.Core.Graphics.VertexFormat[] vertexFormats = new Sce.Pss.Core.Graphics.VertexFormat[nbFormats];

			 for (int i = 0 ; i < nbFormats ; i++)
			 {
			 vertexFormats[i] = AttributeFormatToVertexFormat[mesh.Formats[i]];
			 }

			 Sce.Pss.Core.Graphics.VertexBuffer vertexBuffer = new Sce.Pss.Core.Graphics.VertexBuffer(nbVertices, nbIndices, vertexFormats);

			 vertexBuffer.SetVertices(mesh.Vertices);
			 vertexBuffer.SetIndices(mesh.Indices);

			 mesh.VertexBuffer = vertexBuffer;
			 */

			return mesh;
		}

		public ModelMesh.AttributeFormat ReadAttributeFormat()
		{
			ModelMesh.AttributeFormat format = new ModelMesh.AttributeFormat();

			format.Format = ReadInt32(); //Write((int)format.Format);
			format.ComponentCount = ReadInt32(); //Write(format.ComponentCount);
			format.Normalize = ReadBoolean(); //Write(format.Normalize);
			format.Offset = ReadInt32(); //Write(format.Offset);

			return format;
		}
	}
	

}
