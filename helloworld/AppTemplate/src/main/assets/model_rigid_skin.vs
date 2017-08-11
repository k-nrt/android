uniform mat4 u_worldViewProjection;
//uniform mat4 u_world;
uniform mat4 u_viewPosition;

uniform float u_fogNear;
uniform float u_fogFar;

uniform vec4 TransformsX[16];
uniform vec4 TransformsY[16];
uniform vec4 TransformsZ[16];
uniform float IndexOffset;


attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texcoord;
attribute float a_fIndex;

varying vec3 v_world;
varying vec3 v_normal;
varying vec3 v_viewDirection;
varying float v_fog;
void main()
{
	int i0 = int( a_fIndex - IndexOffset );
	mat4 transform = mat4( TransformsX[i0],TransformsY[i0],TransformsZ[i0], vec4( 0.0, 0.0, 0.0, 1.0  ) );
	//vec3 v3World = mul( transform, vec4( a_position, 1.0) ).xyz;
	vec3 v3World = (vec4( a_position, 1.0)*transform ).xyz;
	v_world = v3World;

	vec4 v4HPos;
	gl_Position = v4HPos = u_worldViewProjection * vec4(v3World,1.0);
	
	//vec4 v3World = u_world*vec4(a_position,1.0);
	
	v_world = v3World.xyz;
	v_normal = (vec4(a_normal,0.0)*transform).xyz;
	v_viewDirection = v3World.xyz - u_viewPosition[3].xyz;
	
	v_fog = clamp( (v4HPos.w - u_fogNear)/(u_fogFar-u_fogNear), 0.0, 1.0 );	

	
}
/*
void main
(
	float3 in a_f3Position : POSITION,
	float3 in a_f3Normal   : TEXCOORD0,

	float4 out v_f4HPos          : POSITION,
	float3 out v_f3World         : TEXCOORD0,
	float3 out v_f3Normal        : TEXCOORD1,
	float3 out v_f3ViewDirection : TEXCOORD2,

	uniform float4x4 WorldViewProjection,
	uniform float4x4 World,
	uniform float4x4 ViewInverse
)
{
	v_f4HPos = mul( float4( a_f3Position, 1.0f ), WorldViewProjection );
	float3 f3World = v_f3World = mul( float4( a_f3Position, 1.0f ), World ).xyz;
    v_f3Normal = mul( a_f3Normal, float3x3( World ) );
    v_f3ViewDirection = f3World - ViewInverse[3].xyz;
}
*/


