#define PROCESSING_TEXTURE_SHADER


/*!
 * <info>
 * <author>ehj1 [ https://www.shadertoy.com/user/ehj1 ]</author>
 * <name>Distorted TV</name>
 * 
 * <description>
 *   Horizontal wiggle distortion effect combined with, static, vertical movement,
 *   scanlines and an rgb offset. Updated from the last version to add static and
 *   vertical movement.
 * </description>
 * 
 * <url>https://www.shadertoy.com/view/ldXGW4</url>
 * 
 * <date>2013-06-08</date>
 * 
 * <tags>noise, distortion, perlin, scanlines, tv, crt, monitor, static, analog</tags>
 * 
 * <synthclipse-importer-legal-note>
 *   As noted in: [ https://www.shadertoy.com/terms ]:
 *   If the author did not stated otherwise, this shader is licensed under
 *   Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License 
 *   [ http://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_US ].
 * </synthclipse-importer-legal-note>
 * </info>
 */
#extension GL_EXT_gpu_shader4 : enable
uniform sampler2D src_tex_unit0;
varying vec4 vertTexCoord;
varying vec4 vertColor;

//#define gl_FragCoord vertTexCoord

//#define iChannel0 src_tex_unit0

//#define iResolutionX dest_tex_size_x
//#define iResolutionY dest_tex_size_y

//#define iResolution vec2(1024.0,768.0)
uniform float iResolutionX;
uniform float iResolutionY;

//uniform vec3 iResolution;           // viewport resolution (in pixels)
uniform float iTime;          // shader playback time (in seconds)
//uniform float iTimeDelta;           // render time (in seconds)
//uniform int iFrame;                 // shader playback frame
//uniform float iChannelTime[4];      // channel playback time (in seconds)
//uniform vec3 iChannelResolution[4]; // channel resolution (in pixels)
//uniform vec4 iMouse;                // mouse pixel coords. xy: current (if MLB down), zw: click
//uniform vec4 iDate;                 // (year, month, day, time in seconds)
//uniform float iSampleRate;          // sound sample rate (i.e., 44100)
//uniform float iFrameRate;           // frames per second (effectively "1.0 / iTimeDelta")

//uniform sampler2D iChannel0; //! WARNING: Unsupported input type: "video". Source: "3405e48f74815c7baa49133bdc835142948381fbe003ad2f12f5087715731153.ogv"

// change these values to 0.0 to turn off individual effects
/*float vertJerkOpt = 1.0;
float vertMovementOpt = 1.0;
float bottomStaticOpt = 1.0;
float scalinesOpt = 1.0;
float rgbOffsetOpt = 1.0;
float horzFuzzOpt = 1.0;*/

#define vertJerkOpt 0.5
#define vertMovementOpt 0.5
#define bottomStaticOpt 1.0
#define scalinesOpt 1.0
#define rgbOffsetOpt 0.5
#define horzFuzzOpt 0.5

// Noise generation functions borrowed from: 
// https://github.com/ashima/webgl-noise/blob/master/src/noise2D.glsl

vec3 mod289(vec3 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec2 mod289(vec2 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec3 permute(vec3 x) {
  return mod289(((x*34.0)+1.0)*x);
}

float snoise(vec2 v)
  {
  const vec4 C = vec4(0.211324865405187,  // (3.0-sqrt(3.0))/6.0
                      0.366025403784439,  // 0.5*(sqrt(3.0)-1.0)
                     -0.577350269189626,  // -1.0 + 2.0 * C.x
                      0.024390243902439); // 1.0 / 41.0
// First corner
  vec2 i  = floor(v + dot(v, C.yy) );
  vec2 x0 = v -   i + dot(i, C.xx);

// Other corners
  vec2 i1;
  //i1.x = step( x0.y, x0.x ); // x0.x > x0.y ? 1.0 : 0.0
  //i1.y = 1.0 - i1.x;
  i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
  // x0 = x0 - 0.0 + 0.0 * C.xx ;
  // x1 = x0 - i1 + 1.0 * C.xx ;
  // x2 = x0 - 1.0 + 2.0 * C.xx ;
  vec4 x12 = x0.xyxy + C.xxzz;
  x12.xy -= i1;

// Permutations
  i = mod289(i); // Avoid truncation effects in permutation
  vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 ))
		+ i.x + vec3(0.0, i1.x, 1.0 ));

  vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw)), 0.0);
  m = m*m ;
  m = m*m ;

// Gradients: 41 points uniformly over a line, mapped onto a diamond.
// The ring size 17*17 = 289 is close to a multiple of 41 (41*7 = 287)

  vec3 x = 2.0 * fract(p * C.www) - 1.0;
  vec3 h = abs(x) - 0.5;
  vec3 ox = floor(x + 0.5);
  vec3 a0 = x - ox;

// Normalise gradients implicitly by scaling m
// Approximation of: m *= inversesqrt( a0*a0 + h*h );
  m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );

// Compute final noise value at P
  vec3 g;
  g.x  = a0.x  * x0.x  + h.x  * x0.y;
  g.yz = a0.yz * x12.xz + h.yz * x12.yw;
  return 130.0 * dot(m, g);
}

float staticV(vec2 uv) {
    //float staticHeight = snoise(vec2(9.0,iTime*1.2+3.0))*0.3+5.0;
    float staticAmount = snoise(vec2(1.0,iTime*1.2-6.0))*0.1+0.3;
    float staticStrength = snoise(vec2(-9.75,iTime*0.6-3.0))*2.0+2.0;
    #define staticHeight 0.8
    //#define staticAmount 0.8
    //#define staticStrength 2.5
	return (1.0-step(snoise(vec2(5.0*pow(iTime,2.0)+pow(uv.x*7.0,1.2),pow((mod(iTime,100.0)+100.0)*uv.y*0.3+3.0,staticHeight))),staticAmount))*staticStrength;
}


void mainImage(out vec4 fragColor, in vec2 fragCoord)
{
	//vec4 color = vec4(0.0, 0.0, 0.0, 1.0);
	//vec2 fragCoord = vertTexCoord.st;

	vec2 iResolution = vec2(iResolutionX,iResolutionY);

	vec2 uv =  fragCoord.xy/vec2(iResolutionX, iResolutionY); //iResolution.xy;
	
	float jerkOffset = (1.0-step(snoise(vec2(iTime*1.3,5.0)),0.8))*0.05;
	
	float fuzzOffset = snoise(vec2(iTime*15.0,uv.y*80.0))*0.003;
	float largeFuzzOffset = snoise(vec2(iTime*1.0,uv.y*25.0))*0.004;
    
    float vertMovementOn = (1.0-step(snoise(vec2(iTime*0.2,8.0)),0.4))*vertMovementOpt;
    float vertJerk = (1.0-step(snoise(vec2(iTime*1.5,5.0)),0.6))*vertJerkOpt;
    float vertJerk2 = (1.0-step(snoise(vec2(iTime*5.5,5.0)),0.2))*vertJerkOpt;
    float yOffset = abs(sin(iTime)*4.0)*vertMovementOn+vertJerk*vertJerk2*0.3;
    float y = mod(uv.y+yOffset,1.0);
    
	
	float xOffset = (fuzzOffset + largeFuzzOffset) * horzFuzzOpt;
    
    float staticVal = 0.0;

	/*  
	    for (float y = -1.0; y <= 1.0; y += 1.0) {
	        float maxDist = 5.0/200.0;
	        float dist = y/200.0;
	    	staticVal += 	staticV(vec2(uv.x,uv.y+dist)) *
	    					(maxDist-abs(dist)) *
	    					1.5;
	    }
	*/
        
    staticVal *= bottomStaticOpt;
	
	float red 	=   texture2D(	src_tex_unit0, 	vec2(uv.x + xOffset -0.01*rgbOffsetOpt,y)).r+staticVal;
	float green = 	texture2D(	src_tex_unit0, 	vec2(uv.x + xOffset,	  y)).g+staticVal;
	float blue 	=	texture2D(	src_tex_unit0, 	vec2(uv.x + xOffset +0.01*rgbOffsetOpt,y)).b+staticVal;
	
	vec3 color = vec3(red,green,blue);
	float scanline = sin(uv.y*800.0)*0.04*scalinesOpt;
	color -= scanline;
	
	fragColor = vec4(color,1.0);
}


void main() {
	vec4 color = vec4(0.0, 0.0, 0.0, 1.0);
	mainImage(color, vertTexCoord.st); //gl_FragCoord.xy);

	gl_FragColor = color; //vec4(vertTexCoord.st, 1.0, 1.0); //color;
}

