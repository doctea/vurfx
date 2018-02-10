#define TWO_PI 6.28318531

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D src_tex_unit0;
uniform sampler2D ppixels;

varying vec4 vertColor;
varying vec4 vertTexCoord;
uniform vec2 texOffset;

uniform vec2 dest_tex_size;
uniform int dirs;

uniform float radio;
uniform float amp;
//#define radio 3.//0.5
//#define amp 2.

uniform int distance_r,distance_g,distance_b,distance_a;
 
void main(void)
{
 	vec2 tex_coords = vertTexCoord.st;
 	vec4 orig ;
	vec4 sum = vec4(0.0)*0.01;
	
	//different distance for each color
	//vec4 d=vec4(2,4,6,8);
	vec4 d=vec4(distance_r,distance_g,distance_b,distance_a);
	
   //iterate 9 times to get samples around the current point for median calculation
   for(int j=0; j<dirs; j++){
   	
   	//calculate the points for each distance
   float dir = float(j)*TWO_PI/float(dirs) + radio;
   	 
   	 //iterate each one of the colors
   for(int g=0; g<4;g++){
   	 		
		 float dx = (d[g] * cos(dir) * amp);
	     float dy = (d[g] * sin(dir) * amp);
	         
	  		vec2 point = (vec2(dx, dy)*texOffset.xy)+tex_coords;
	  		//calculate the median for 9
	  		float col = texture2D(src_tex_unit0, point)[g];
	  		sum[g] += col/float(dirs); //8.;
	  		
	  		
	  		//feedback if it is bigger than one
	     	if(col>=1.){

	     		float left = texture2D(src_tex_unit0, tex_coords-4.)[g];
	     		float right = texture2D(src_tex_unit0, tex_coords+4.)[g];

	     		//sum[g] = (texture2D(src_tex_unit0, tex_coords-4.)[g]+texture2D(src_tex_unit0, tex_coords+4.)[g])/(float(dirs)*2.);
	     		sum[g] = left + right / (float(dirs)*2.);
	     		   				
	     		}
			
      	 	}
    
   
   }
   
   orig = sum;

   //orig = vec4(0.,1.,0.5,0.5);
	
   vec3 newColor  = /*vec3(1./radio,0.,0.) +*/ orig.rgb;

   //vec3 newColor = vec3(1./radio,1./amp,1.f);
   
   gl_FragColor = vec4(newColor, 1.);
   
   //gl_FragColor = vec4(tex_coords.x,tex_coords.y,tex_coords.x,1.);  // fuckin handy deubg line !
   //gl_FragColor = vec4(tex_coords.x,tex_coords.y,tex_coords.x,1.) * texture2D(src_tex_unit0, tex_coords);


}
