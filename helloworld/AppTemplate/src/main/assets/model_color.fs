precision mediump float;

varying vec3 v_world;
varying vec3 v_normal;
varying vec3 v_viewDirection;

varying float v_fog;


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

vec3 lerp( float l, vec3 v0, vec3 v1 )
{
	return v1*l + v0*(1.0-l);
}

vec4 lerp( float l, vec4 v0, vec4 v1 )
{
	return v1*l + v0*(1.0-l);
}


void main()
{
/*
	vec3 u_lightAmbient = vec3( 0.5, 0.5, 0.5 );
	vec3 u_lightDirection = vec3( 0, 0, 1 );
	vec3 u_lightColor = vec3( 1, 1, 1 ); 
	
	vec3 u_skyColor = vec3( 0, 0, 0.25 );
	vec3 u_horizonColor = vec3( 0.25, 0.25, 0.25 );
	vec3 u_earthColor = vec3( 0.25, 0, 0 );
    vec3 u_skyDirection = vec3( 0, 1, 0 );
*/	
	vec3 v3Normal = normalize( v_normal );
	float fDiffuse = clamp( dot( v3Normal, u_lightDirection ), 0.0, 1.0 );
	
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
		
	gl_FragColor = lerp( v_fog, v4Shading, u_fogColor );
	
	


	/*
	vec3 c0 = vec3( 0.3, 0.3, 0.3 );
	vec3 c1 = vec3( 0.7, 0.5, 0.2 );
	float f = length( fract( v_local*(1.0/50.0) + c0) - 0.5) *
		length( fract( v_local*(1.0/500.0) + c1 ) - 0.5);
	
	if( f < 0.1 )
	{
		discard;
		//gl_FragColor = v4Shading;
	}
	else if( f < 0.15 )
	{
		gl_FragColor = vec4( 1.0 );
	}
	else 
	{
		gl_FragColor = v4Shading;
		//discard;
	}
	*/
}



