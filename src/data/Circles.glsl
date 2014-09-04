#version 120

uniform sampler2D src_tex_unit0; //Texture0;
//uniform vec2 gl_TexCoord[];
//uniform vec2 offset;


//uniform vec2 positions[4](vec2(0.25,0.25), vec2(0.5,0.5), vec2(0.75,0.75), vec2(0.25,0.75));

uniform int posCount = 4;

//uniform vec3 positions[4] = { vec3(0.25,0.75,0.05), vec3(0.5,0.5,0.05), vec3(0.75,0.75,0.05), vec3(0.5,0.75,0.05) };

uniform vec3 positions[4];

//uniform vec3 positions[4] = vec3[4](vec3(0.25,0.75,0.05), vec3(0.5,0.5,0.05), vec3(0.75,0.75,0.05), vec3(0.5,0.75,0.05));


void main(void)
{
    vec2 texCoord = gl_TexCoord[0].xy;
    vec4 colorOrg = texture2D( src_tex_unit0, texCoord );

		bool noUse = true;
    for (int index = 0 ; index < posCount ; index++) {
        //float x = positions[index].x;
        //float y = positions[index].y;

        //x = 0.5;
        //y = 0.5;
        vec2 p = vec2(positions[index].x, positions[index].y);
        
	    	//if (distance(texCoord, vec2(x,y)) >= 0.2) {
	    	if (distance(texCoord, p) <= positions[index].z) {
					//gl_FragColor = vec4(colorOrg.g, colorOrg.b, colorOrg.r, colorOrg.a);
					//gl_FragColor = vec4(1, colorOrg.b, colorOrg.r, colorOrg.a);
					gl_FragColor = vec4(texture2D(src_tex_unit0,p));
					//break;
					noUse = false;
	    	} else {
	    		//noUse = false;
	    		//gl_FragColor = vec4(1, colorOrg.g, colorOrg.b, colorOrg.a);
	    		//break;
	    	}
    }
    
    if (noUse) {
    	discard;
    	//gl_FragColor = vec4(colorOrg.r, colorOrg.g, colorOrg.b, colorOrg.a);
    }

}
