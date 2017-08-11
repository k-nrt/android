#ifdef FRAGMENT_SHADER
precision mediump float;
#endif //FRAGMENT_SHADER

#ifdef VERTEX_SHADER
	attribute vec3 a_position;
	attribute vec3 a_normal;
	attribute vec2 a_texcoord;
	
	#ifdef RIGIDSKIN
		attribute float a_fIndex;
	#endif //RIGIDSKIN
#endif //VERTEX_SHADER

varying vec3 v_world;
varying vec3 v_normal;
varying vec3 v_viewDirection;
varying float v_fog;

#ifdef CASTSHADOW
	varying vec4 v_hpos;
#endif //CASTSHADOW

#ifdef RECIEVESHADOW
	varying vec4 v_hposShadow;
	varying vec3 v_shadowNormal;
#endif //RECIEVESHADOW

#ifdef VERTEX_SHADER

uniform mat4 u_worldViewProjection;
uniform mat4 u_world;
uniform mat4 u_viewPosition;

uniform float u_fogNear;
uniform float u_fogFar;

#ifdef RIGIDSKIN
	uniform vec4 TransformsX[16];
	uniform vec4 TransformsY[16];
	uniform vec4 TransformsZ[16];
	uniform float IndexOffset;
#endif //RIGIDSKIN

#ifdef RECIEVESHADOW
	uniform mat4 u_shadowProjection;
#endif //RECIEVESHADOW

#ifdef OUTPUTPLUGIN
	void VsMain();
#endif //OUTPUTPLUGIN

void main()
{
#ifdef RIGID
	vec3 v3LocalPos = a_position;
	vec3 v3World = (u_world*vec4(a_position,1.0)).xyz;
	vec3 v3Normal = (u_world*vec4(a_normal,0.0)).xyz;
	vec4 v4HPos;
	gl_Position = v4HPos = u_worldViewProjection * vec4(a_position,1.0);
#endif //RIGID

#ifdef RIGIDSKIN
	int i0 = int( a_fIndex - IndexOffset );
	mat4 transform = mat4( TransformsX[i0],TransformsY[i0],TransformsZ[i0], vec4( 0.0, 0.0, 0.0, 1.0  ) );
	//vec3 v3World = mul( transform, vec4( a_position, 1.0) ).xyz;
	
	vec3 v3LocalPos = (vec4( a_position, 1.0)*transform).xyz;
	vec3 v3LocalNormal = (vec4(a_normal,0.0)*transform).xyz;
	vec3 v3World = (u_world*vec4(v3LocalPos,1.0)).xyz;
	vec3 v3Normal = (u_world*vec4(v3LocalNormal,0.0)).xyz;
	vec4 v4HPos;
	gl_Position = v4HPos = u_worldViewProjection * vec4(v3LocalPos,1.0);
#endif //RIGIDSKIN

	v_world = v3World;
	v_normal = v3Normal;
	v_viewDirection = v3World.xyz - u_viewPosition[3].xyz;
	v_fog = clamp( (v4HPos.w - u_fogNear)/(u_fogFar-u_fogNear), 0.0, 1.0 );
	
#ifdef CASTSHADOW
	v_hpos = v4HPos;
#endif //CASTSHADOW

#ifdef RECIEVESHADOW
	v_hposShadow = u_shadowProjection * vec4( v3LocalPos, 1.0 );
#endif //RECIEVESHADOW
	
#ifdef OUTPUTPLUGIN
	VsMain();
#endif //OUTPUTPLUGIN
}
#endif //VERTEX_SHADER

#ifdef FRAGMENT_SHADER

uniform vec3 u_ambient;
uniform vec3 u_diffuse;
uniform vec3 u_specular;
uniform vec3 u_emission;

uniform float u_power;

uniform vec3 u_lightAmbient;
uniform vec3 u_lightDirection;
uniform vec3 u_lightColor; 
	
uniform	vec3 u_skyColor;
uniform	vec3 u_horizonColor;
uniform	vec3 u_earthColor;
uniform vec3 u_skyDirection;

uniform vec4 u_fogColor;

#ifdef RECIEVESHADOW
	uniform vec3 u_shadowDirection;
	uniform sampler2D u_samplerShadow;
#endif //RECIEVESHADOW
	

vec3 lerp( float l, vec3 v0, vec3 v1 )
{
	return v1*l + v0*(1.0-l);
}

vec4 lerp( float l, vec4 v0, vec4 v1 )
{
	return v1*l + v0*(1.0-l);
}

float lerp( float l, float v0, float v1 )
{
	return v1*l + v0*(1.0-l);
}

const float kResolusion = 256.0;

vec4 packFloatToVec4i(const float value)
{
  const vec4 bitSh = vec4(kResolusion*kResolusion*kResolusion, kResolusion*kResolusion, kResolusion, 1.0);
  const vec4 bitMsk = vec4(0.0, 1.0/kResolusion, 1.0/kResolusion, 1.0/kResolusion);
  vec4 res = fract(value * bitSh);
  res -= res.xxyz * bitMsk;
  return res;
}

float unpackFloatFromVec4i(const vec4 value)
{
  const vec4 bitSh = vec4(1.0/(kResolusion*kResolusion*kResolusion), 1.0/(kResolusion*kResolusion), 1.0/kResolusion, 1.0);
  return(dot(value, bitSh));
}

#ifdef OUTPUTPLUGIN
	vec4 FsMain( vec4 v4Color );
#endif //OUTPUTPLUGIN

void main()
{
	vec3 v3Normal = normalize( v_normal );
	float fDiffuse = clamp( dot( v3Normal, u_lightDirection ), 0.0, 1.0 );
	
			
#ifdef RECIEVESHADOW
	vec3 v3ShadowPos = 0.5 + (v_hposShadow.xyz/v_hposShadow.w)*0.5;
	vec4 v4Depth = texture2D( u_samplerShadow, v3ShadowPos.xy );
	//float fDepth = -(1.0/4096.0) + dot(v4Depth,vec4(1.0,1.0/256.0,1.0/65536.0,1.0/(256.0*256.0*256.0)));
	//float fDepth = (0.0/4096.0) + dot(v4Depth,vec4(1.0,1.0/16.0,1.0/256.0,1.0/4096.0));
	
	float fDepth = 0.0/4096.0 + unpackFloatFromVec4i( v4Depth );
	//v4Shading.xyz = vec3( fDepth, fDepth, fDepth);//v4Depth;
	
	if( fDepth < v3ShadowPos.z )
	{
		//v4Shading.xyz *= 0.25;
		//v4Shading.xyz = vec3(1.9,0.0,0.0);
		//v4Shading.xyz = u_lightAmbient*u_ambient + v3Hemisphere*u_diffuse + u_emission;
		fDiffuse = lerp( fDiffuse, fDiffuse, 0.0 );
	}
	

#endif //RECIEVESHADOW
	
	
	
	vec3 v3Eye  = normalize( -v_viewDirection );
	vec3 v3Half = normalize( u_lightDirection + v3Eye );
	float fSpecDot = dot( v3Half, v3Normal );
	vec3 v3SpecCol = u_specular + (1.0-u_specular)*pow(1.0-fSpecDot,5.0);//*fDiffuse;
	float fSpecular = pow( fSpecDot, u_power );//*fDiffuse;
	//float fSpecular = pow( fSpecDot, 1.0 )*fDiffuse;
	
	vec3 v3LightColor = u_lightColor;
	vec3 v3Specular = v3LightColor*fSpecular*v3SpecCol;
	v3LightColor -= v3Specular;
	
	//vec3 v3Diffuse = clamp( dot( v3Normal, u_lightDirection ), 0.0, 1.0 )*u_lightColor;
	vec3 v3Diffuse = v3LightColor*fDiffuse;

	//vec3 v3Eye  = normalize( -v_viewDirection );
	//vec3 v3Half = normalize( u_lightDirection + v3Eye );
	//vec3 v3Specular = pow( dot( v3Half, v3Normal ), u_power )*v3Diffuse;

	
	
	float fSkyDot = dot( v3Normal, u_skyDirection );
	vec3 v3Hemisphere
		= ( fSkyDot > 0.0
			? lerp( clamp( fSkyDot, 0.0, 1.0 ), u_horizonColor, u_skyColor )
			: lerp( clamp( -fSkyDot, 0.0, 1.0 ), u_horizonColor, u_earthColor )
		);

	vec4 v4Shading = //vec4( 1,0,0,1);//vec4( dot( v_normal, u_lightDirection )*u_lightColor, 1.0 );
		vec4(
		//v3Hemisphere
		u_lightAmbient*u_ambient
		+ (v3Diffuse + v3Hemisphere)*u_diffuse
		+ v3Specular //*u_specular
		+ u_emission
		, 1.0 );



#ifdef OUTPUTPLUGIN
	vec4 v4OutputColor = FsMain(lerp( v_fog, v4Shading, u_fogColor ));
#else
	vec4 v4OutputColor = lerp( v_fog, v4Shading, u_fogColor );
#endif //OUTPUTPLUGIN

#ifdef CASTSHADOW
	float z = 0.5 + (v_hpos.z/v_hpos.w)*0.5;
	//z *= 0.9999;
	//gl_FragColor = fract( vec4( z, z*256.0, z*65536.0, z*65536.0*256.0 ) );
	//gl_FragColor = fract( vec4( z, z*16.0, z*256.0, z*4096.0 ) );
	
	gl_FragColor = packFloatToVec4i( z );
#else
	gl_FragColor = v4OutputColor;
#endif //HADOWCASTS
}

#endif //FRAGMENT_SHADER



