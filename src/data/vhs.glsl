//////////////////////////////////
// VHS effect
//////////////////////////////////

#ifdef GL_ES
	precision highp float;
#endif

uniform sampler2D samplerFront;
varying vec2 vTex;

uniform float intensity;
uniform float p;
uniform float amount;
uniform float seed;
uniform float lines;

float rand(vec2 co){
	return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

void main( void ) {
	vec2 position = vTex; //unnecessary in this case, but lets stick to the C2 params
	
	float y = floor(lines*position.y)/lines;
	float disf = 0.01*(cos(position.y*130.0+p*10.0)+sin(position.y*183.0+p*80.0));
	
	float parity = 0.0;
	if(mod(y*lines, 2.0)>0.5) 
		parity=1.0;
	else parity=-1.0;
	
	float a = smoothstep(0.0, 1.0, p);
	
	position.x = 	amount*a*(y*0.3+disf) + position.x + amount*0.5*parity*smoothstep(0.6, 0.65, p)*(sin(position.y*(12.+40.*rand(vec2(seed, -seed)))+smoothstep(0.64, 0.65, p)));
	
	vec4 colorInput = texture2D( samplerFront, position);
	lowp vec4 front = texture2D(samplerFront, vTex);
	
	gl_FragColor = mix(front, colorInput, intensity);
}