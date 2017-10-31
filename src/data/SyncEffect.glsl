#define PI 3.141592653589793

uniform sampler2D src_tex_unit0;

//uniform float sin_mode;

uniform float step_x;
uniform float step_y;

uniform float offset_x;
uniform float offset_y;

varying vec4 vertTexCoord;


void main(void)
{
    vec2 tex_coord = vertTexCoord.st;

    //tex_coord.xy = vec2(0.5-tex_coord.x * step_x, 0.5-tex_coord.y * step_y);
   	// ^^ this one for straightforward 'grid'

    //float ang = atan2(tex_coord.xy);
    //float d = distance(vec2(0.0,0.0),tex_coord.xy)
    //tex_coord.x = sin(ang) * (d + step_x);
    //tex_coord.y = cos(ang) * (d + step_x);

	/*
    vec2 new_coord = vec2(0.0,0.0);
    new_coord.x = sin(cos(tex_coord.x) * step_x);
    new_coord.y = cos(tan(tex_coord.y) * step_y);
    tex_coord.xy = new_coord.xy;
    */
    // ^^^ this one for sin-wavey wibblewobbly-ness

	//wrapped grid!
	/*
	vec2 new_coord = vec2(0.0,0.0);
    new_coord.x = abs(sin(tex_coord.x * (step_x)));
    new_coord.y = abs(cos(tex_coord.y * (step_y)));
    tex_coord.xy = new_coord.xy;
    */
    // ^^^ this one for wrapped grid with base at zero, apparently?!?!?


	//wrapped grid!
	vec2 new_coord = vec2(0.0,0.0);
	//tex_coord.x += 1.0; //0.5;
	//tex_coord.y += 1.0; //0.5;

	if (tex_coord.x >= 1.0/offset_x) {
		new_coord.x = (1.0/offset_x) - (tex_coord.x/offset_x);
		new_coord.y = (tex_coord.y/offset_y);

		new_coord.x = new_coord.x * step_x;
    	new_coord.y = new_coord.y * step_y;

		new_coord.x = (new_coord.x*offset_x); //+ 0.5;
		new_coord.y = (new_coord.y*offset_y); //+ 0.5;
    } else {
		new_coord.x = (tex_coord.x/offset_x);
		new_coord.y = (tex_coord.y/offset_y);

		new_coord.x = new_coord.x * step_x;
    	new_coord.y = new_coord.y * step_y;

		new_coord.x = (new_coord.x*offset_x); //+ 0.5;
		new_coord.y = (new_coord.y*offset_y); //+ 0.5;
	}

	/*if (sin_mode==0.0) {
		tex_coord.x = asin(tex_coord.x)+0.5;
		tex_coord.y = acos(tex_coord.y)+0.5;
	}*/

    /*new_coord.x = sin(new_coord.x * (step_x/8.0));
    new_coord.y = cos(new_coord.y * (step_y/8.0));*/
    //new_coord = new_coord * vec2(sin(step_x),cos(step_x)); // weird warping

    tex_coord.xy = new_coord.xy; //, vec2(offset_x,offset_y);

    // ^^^ this one for wrapped grid with base at zero, apparently?!?!?


    gl_FragColor = vec4( texture2D(src_tex_unit0, fract(tex_coord)).rgba );

}
