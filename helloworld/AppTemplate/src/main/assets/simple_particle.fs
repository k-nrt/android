precision mediump float;

varying vec2 v_texCoord;
varying float v_alpha;

//uniform vec2 u_uvSize;
uniform sampler2D u_texture;

void main()
{
	gl_FragColor = //vec4( 1.0, 1.0, 1.0, 1.0 );
		texture2D( u_texture, v_texCoord )*vec4(1.0, 1.0, 1.0, v_alpha);
		//vec4( v_texCoord.x, v_texCoord.y, 0.0, v_alpha );

}

/*
void main
( 
	float2 in  v_TexCoord : TEXCOORD0,
	float  in  v_Alpha : TEXCOORD1,
//	float2 in v_PointCoord : POINTCOORD,
	float4 out Color	  	  : COLOR,
	
	uniform float2 UVSize,
	uniform sampler2D Texture0 : TEXUNIT0
)
{
//	Color = float4( 1.0f );//tex2D(Texture0, v_TexCoord);

//	float2 f2TexCoord = v_UVOffsetAlphaRadius.xy + v_PointCoord*UVSize;
//	float2 f2TexCoord = v_UVOffsetAlphaRadius.xy;// + v_PointCoord*UVSize;
	Color = tex2D(Texture0, v_TexCoord)*float4(1.0f, 1.0f, 1.0f, v_Alpha);
}
*/

