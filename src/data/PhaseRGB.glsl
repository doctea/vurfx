#version 120

uniform float rshift;
uniform float gshift;
uniform float bshift;

uniform sampler2D src_tex_unit0;
varying vec4 vertTexCoord;

void main(void)
{
    //gl_FragColor = vec4(texture2DRect(src_tex_unit0, src_tex_offset0).abrg);
    vec4 color = texture2D(src_tex_unit0, vertTexCoord.st).rgba;
/*    gl_FragColor = vec4 (
	rshift, //1 + color.r * rshift,
	gshift, //color.g * gshift,
	bshift, //color.b * bshift,
	0
    );
*/
gl_FragColor = vec4(
	0.05 /*+ (rshift/2)*/ + color.r * rshift, 
	0.05 /*+ (gshift/2)*/ + color.g * gshift, 
	0.05 /*+ (bshift/2)*/ + color.b * bshift, 
	color.a
).rgba;

    //gl_FragColor = vec4(0.5,1,1,1);

    /*if(swap_mode==0)
    	gl_FragColor = vec4(color.brga);
    else if (swap_mode==1)
	gl_FragColor = vec4(color.rbga);
    else if (swap_mode==2)
        gl_FragColor = vec4(color.gbra);
    else if (swap_mode==3)
        gl_FragColor = vec4(color.bgra);
    else if (swap_mode==4)
        gl_FragColor = vec4(color.rbga);
    else if (swap_mode==5)
        gl_FragColor = vec4(color.grba);*/

    /*if(gl_FragColor.g == gl_FragColor.r && gl_FragColor.r == gl_FragColor.b) {
        if((1.0*float(swap_mode)/3.0)>1.0) gl_FragColor.r = 0.0;
	if((1.0*float(swap_mode)/4.0)>1.0) gl_FragColor.g = 0.0;
	if((1.0*float(swap_mode)/5.0)>1.0) gl_FragColor.b = 0.0;
    }*/

       
}
