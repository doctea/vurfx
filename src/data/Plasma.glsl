#version 120
// http://www.bidouille.org/prog/plasma
//precision mediump float;
#define PI 3.1415926535897932384626433832795
#extension GL_EXT_gpu_shader4 : enable
 
uniform int u_time_2;
uniform int width;
uniform int height;
//varying vec2 v_coords;


varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform int colourMode;
 
void main() {
	//gl_FragColor = vec4(1,0.25,0.75,1); //vec4(sin(u_time),sin(u_time),sin(u_time),sin(u_time));

    vec2 v_coords = vertTexCoord.st;

    float u_time = float(u_time_2)/50.0;///10.0;

    float v = 0.0;
    vec2 u_k = vec2(float(width),float(height));
    vec2 c = v_coords * u_k - u_k/2.0;
    v += sin((c.x+u_time));
    v += sin((c.y+u_time)/2.0);
    v += sin((c.x+c.y+u_time)/2.0);
    c += u_k/2.0 * vec2(sin(u_time/3.0), cos(u_time/2.0));
    v += sin(sqrt(c.x*c.x+c.y*c.y+1.0)+u_time);
    v = v/2.0;

    vec3 col;
    if (colourMode==0)
    	col = vec3(1,sin(PI*v),cos(PI*v));
    else if (colourMode==1)
	col = vec3(sin(PI*v), cos(PI*v), 0);
    else if (colourMode==2)
	col = vec3(sin(PI*v), sin(v*PI+2.0*PI/3.0), sin(v*PI+4.0*PI/3.0));
    else if (colourMode==3) {
	float t = sin(v*5.0*PI);
	col = vec3(1,t,t);
    } else if (colourMode==4) {
	float t = sin(v*5.0*PI);
	col = vec3(t,1,t);
    } else if (colourMode==5) {
	float t = sin(v*5.0*PI);
	col = vec3(t,t,1);
    } else if (colourMode==6) {
	float t = sin(v*5.0*PI);
	col = vec3(t,t,t);
    } else if (colourMode==7) {
	col = vec3(1,sin(v*PI),sin(v*PI));
    } else {
	col = vec3(sin(PI*v),1,cos(PI*v));
    }

    gl_FragColor = vec4(col*.5 + .5, 1);
}
