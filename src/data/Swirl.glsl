
uniform float time;
//(uniform vec2 mouse;
//uniform vec2 resolution;
float pi = 3.1415926535897932384626433832795028841;

float sin1(float val) {
	return ((sin((val*2.)-1.5)/2.)+0.5);
}

float strobes(float val) {
	//return step(0.5,fract(val*8.));
	return pow(sin1(val*pi*8.),10.)*6000.;
}

void main( void ) {
	float ourtime = (time);
	vec2 position = ( gl_FragCoord.xy / vec2(1024.0,768.0)/*resolution.xy*/ );
	vec2 uv = vec2(position.x,((position.y-0.5)*(1024.0/768.0))+0.5);
	float length1 = length(uv-0.5);
	float atan2 = (atan(uv.x-0.5,uv.y-0.5)+pi)/(pi*2.);
	float circle1 = clamp((length1-0.15)*900.,0.,1.);
	float swirl = clamp(strobes(atan2+(length1*7.)+(ourtime/4.)),0.,1.);
	float swirla2 = (1.-length(abs(uv-0.5)-50.))*5.;
	float swirl2 = clamp(strobes(swirla2-ourtime),0.,1.);
	float outputs = mix(swirl,swirl2,circle1);
	gl_FragColor = vec4(vec3(outputs), 1.0 );
}
