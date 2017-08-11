attribute vec3 a_f3Position;
attribute vec3 a_f3Normal;
attribute vec2 a_f2Indices;
attribute vec2 a_f2Weights;
	
//varying	float4 v_f4HPos;
	
varying	vec3 v_f3SegmentDirection;
varying	vec3 v_f3SegmentPosition;
varying	vec3 v_f3World;
varying	float  v_fRadius;
	
uniform mat4 u_m4WorldViewProjection;
uniform float u_fRadius;
uniform vec4 u_v4TransformsX[16];
uniform vec4 u_v4TransformsY[16];
uniform vec4 u_v4TransformsZ[16];

void main()
{
	int i0 = int(a_f2Indices.x);
	int i1 = int(a_f2Indices.y);
	
	mat4 transform0 = mat4( u_v4TransformsX[i0],u_v4TransformsY[i0],u_v4TransformsZ[i0], vec4( 0,0,0,1 ) );
	mat4 transform1 = mat4( u_v4TransformsX[i1],u_v4TransformsY[i1],u_v4TransformsZ[i1], vec4( 0,0,0,1 ) );
	
	//float3 f3World0 = mul( transform0, float4( a_f3Position*Radius, 1.0f) ).xyz*a_f2Weights.x;
	//float3 f3World1 = mul( transform1, float4( a_f3Position*Radius, 1.0f) ).xyz*a_f2Weights.y;
//	vec3 f3World0 = (transform0*vec4( a_f3Position*u_fRadius, 1.0) ).xyz*a_f2Weights.x;
//	vec3 f3World1 = (transform1*vec4( a_f3Position*u_fRadius, 1.0) ).xyz*a_f2Weights.y;
	vec3 f3World0 = (vec4( a_f3Position*u_fRadius, 1.0)*transform0 ).xyz*a_f2Weights.x;
	vec3 f3World1 = (vec4( a_f3Position*u_fRadius, 1.0)*transform1 ).xyz*a_f2Weights.y;

	vec3 f3World = f3World0 + f3World1;
	
	gl_Position = u_m4WorldViewProjection*vec4( f3World, 1.0 );
//	gl_Position = u_m4WorldViewProjection*vec4( a_f3Position*u_fRadius, 1.0 );

	vec3 f3Pos0 = vec3( u_v4TransformsX[i0].w, u_v4TransformsY[i0].w, u_v4TransformsZ[i0].w )*a_f2Weights.x;
	vec3 f3Pos1 = vec3( u_v4TransformsX[i1].w, u_v4TransformsY[i1].w, u_v4TransformsZ[i1].w )*a_f2Weights.y;
	v_f3SegmentPosition  = f3Pos0 + f3Pos1;

	vec3 f3Dir0 = vec3( u_v4TransformsX[i0].z, u_v4TransformsY[i0].z, u_v4TransformsZ[i0].z )*a_f2Weights.x;
	vec3 f3Dir1 = vec3( u_v4TransformsX[i1].z, u_v4TransformsY[i1].z, u_v4TransformsZ[i1].z )*a_f2Weights.y;
	v_f3SegmentDirection = f3Dir0 + f3Dir1;
	v_f3World = f3World;
//	v_f4HPos = mul( float4( a_f3Position, 1.0f), WorldViewProjection );

	v_fRadius = u_fRadius*(a_f2Weights.x*length( vec3( u_v4TransformsX[i0].x,  u_v4TransformsY[i0].x,  u_v4TransformsZ[i0].x ) ) +
				a_f2Weights.y*length( vec3( u_v4TransformsX[i1].x,  u_v4TransformsY[i1].x,  u_v4TransformsZ[i1].x ) ) );
}

/*
void main
(
	float3 in a_f3Position  : POSITION,
	float3 in a_f3Normal    : NORMAL,
	float2 in a_f2Indices   : TEXCOORD0,
	float2 in a_f2Weights   : TEXCOORD1,
	
	float4 out v_f4HPos     : POSITION,
	
	float3 out v_f3SegmentDirection : TEXCOORD0,
	float3 out v_f3SegmentPosition  : TEXCOORD1,
	float3 out v_f3World            : TEXCOORD2,
	float  out v_fRadius           : TEXCOORD3,
	
	uniform float4x4 WorldViewProjection,
	uniform float4 TransformsX[16],
	uniform float4 TransformsY[16],
	uniform float4 TransformsZ[16],
	uniform float Radius
)
{
	int i0 = int(a_f2Indices.x);
	int i1 = int(a_f2Indices.y);
	
	float4x4 transform0 = float4x4( TransformsX[i0],TransformsY[i0],TransformsZ[i0], float4( 0,0,0,1 ) );
	float4x4 transform1 = float4x4( TransformsX[i1],TransformsY[i1],TransformsZ[i1], float4( 0,0,0,1 ) );
	
	float3 f3World0 = mul( transform0, float4( a_f3Position*Radius, 1.0f) ).xyz*a_f2Weights.x;
	float3 f3World1 = mul( transform1, float4( a_f3Position*Radius, 1.0f) ).xyz*a_f2Weights.y;
	float3 f3World = f3World0 + f3World1;
	
	v_f4HPos = mul( float4( f3World, 1.0f), WorldViewProjection );

	float3 f3Pos0 = float3( TransformsX[i0].w, TransformsY[i0].w, TransformsZ[i0].w )*a_f2Weights.x;
	float3 f3Pos1 = float3( TransformsX[i1].w, TransformsY[i1].w, TransformsZ[i1].w )*a_f2Weights.y;
	v_f3SegmentPosition  = f3Pos0 + f3Pos1;

	float3 f3Dir0 = float3( TransformsX[i0].z, TransformsY[i0].z, TransformsZ[i0].z )*a_f2Weights.x;
	float3 f3Dir1 = float3( TransformsX[i1].z, TransformsY[i1].z, TransformsZ[i1].z )*a_f2Weights.y;
	v_f3SegmentDirection = f3Dir0 + f3Dir1;
	v_f3World = f3World;
//	v_f4HPos = mul( float4( a_f3Position, 1.0f), WorldViewProjection );

	v_fRadius = Radius*(a_f2Weights.x*length( float3( TransformsX[i0].x,  TransformsY[i0].x,  TransformsZ[i0].x ) ) +
				a_f2Weights.y*length( float3( TransformsX[i1].x,  TransformsY[i1].x,  TransformsZ[i1].x ) ) );
}
*/
