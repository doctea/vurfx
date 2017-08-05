#define numLegs 12.0	//non integers look terrible
#define wibblewobble 6.5

uniform float iGlobalTime;
vec2 iResolution = vec2(1024,768);

void main( void) {

	fragCoord.xy = gl_TexCoord[0].st;

	vec2 p = -1.0+2.0*fragCoord.xy/iResolution.xy;
	
	float w = sin(iGlobalTime+wibblewobble*sqrt(dot(p,p))*cos(p.x)); 	//part 2
	float x = cos(numLegs*atan(p.y,p.x) + 1.8*w);	//part 1
	
	vec3 col = vec3(0.1,0.2,0.82)*15.0;

	gl_fragColor = vec4(col*x,1.0);
	
}
