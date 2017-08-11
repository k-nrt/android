uniform mat4 u_modelView;
uniform mat4 u_proj;

attribute vec3 a_pos;
attribute vec4 a_color;

varying vec4 v_color;

void main()
{
	gl_Position = u_proj * u_modelView * vec4(a_pos,1.0);
	v_color = a_color;
	v_color.x = 1.0;
}

