precision mediump float;
varying vec4 v_color;
//varying float v_fog;

void main()
{
	gl_FragColor = v_color;
}
			
/*
void main
(
	float4 out Color : COLOR,
	uniform float4 MaterialColor
)
{
	Color = MaterialColor;
}
*/
