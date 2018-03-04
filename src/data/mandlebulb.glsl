#ifdef GL_ES
precision highp float;
#endif

uniform float time;
//uniform vec2 resolution;
uniform vec3 cameraPos;
uniform vec3 cameraLookat;
uniform vec3 lightDir;
uniform vec3 lightColour;
uniform float specular;
uniform float specularHardness;
uniform vec3 diffuse;
uniform float ambientFactor;
uniform bool ao;
uniform bool shadows;
uniform bool rotateWorld;
uniform bool antialias;

#define resolution vec2(640.0,480.0)

#define PI 3.14159265
#define AO_SAMPLES 5
#define RAY_DEPTH 256
#define MAX_DEPTH 20.0
#define DISTANCE_MIN 0.003
#define PI 3.14159265

#define FLOOR_YPOS -2.0

const vec2 delta = vec2(DISTANCE_MIN, 0.);


vec3 RotateY(vec3 p, float a)
{
   float c,s;
   vec3 q=p;
   c = cos(a);
   s = sin(a);
   p.x = c * q.x + s * q.z;
   p.z = -s * q.x + c * q.z;
   return p;
}

float Plane(vec3 p, vec3 n)
{
   return dot(p, n);
}

// Formula for original MandelBulb from http://blog.hvidtfeldts.net/index.php/2011/09/distance-estimated-3d-fractals-v-the-mandelbulb-different-de-approximations/
float MandelBulb(vec3 pos)
{
   const int Iterations = 12;
   const float Bailout = 8.0;
   float Power = 5.0 + sin(time*0.5)*4.0;

	vec3 z = pos;
	float dr = 1.0;
	float r = 0.0;
	for (int i = 0; i < Iterations; i++)
	{
		r = length(z);
		if (r > Bailout) break;

		// convert to polar coordinates
		float theta = acos(z.z/r);
		float phi = atan(z.y,z.x);
		dr = pow(r, Power-1.0)*Power*dr + 1.0;

		// scale and rotate the point
		float zr = pow(r,Power);
		theta = theta*Power;
		phi = phi*Power;

		// convert back to cartesian coordinates
		z = zr*vec3(sin(theta)*cos(phi), sin(phi)*sin(theta), cos(theta));
		z += pos;
	}
	return 0.5*log(r)*r/dr;
}

// This should return continuous positive values when outside and negative values inside,
// which roughly indicate the distance of the nearest surface.
float Dist(vec3 pos)
{
   if (rotateWorld) pos = RotateY(pos, sin(time*0.25)*PI);

   return min(
      // Floor is at y=-2.0
      Plane(pos-vec3(0.,FLOOR_YPOS,0.), vec3(0.,1.,0.)),
      MandelBulb(pos)
   );
}

// Based on original by IQ - optimized to remove a divide
float CalcAO(vec3 p, vec3 n)
{
   float r = 0.0;
   float w = 1.0;
   for (int i=1; i<=AO_SAMPLES; i++)
   {
      float d0 = float(i) * 0.3;
      r += w * (d0 - Dist(p + n * d0));
      w *= 0.5;
   }
   return 1.0 - r;
}

// Based on original code by IQ
float SelfShadow(vec3 ro, vec3 rd)
{
   float k = 32.0;
   float res = 1.0;
   float t = 0.1;          // min-t see http://www.iquilezles.org/www/articles/rmshadows/rmshadows.htm
   for (int i=0; i<16; i++)
   {
      float h = Dist(ro + rd * t);
      res = min(res, k*h/t);
      t += h;
      if (t > 4.0) break; // max-t
   }
   return clamp(res, 0.0, 1.0);
}
float SoftShadow(vec3 ro, vec3 rd)
{
   float k = 16.0;
   float res = 1.0;
   float t = 0.1;          // min-t see http://www.iquilezles.org/www/articles/rmshadows/rmshadows.htm
   for (int i=0; i<48; i++)
   {
      float h = Dist(ro + rd * t);
      res = min(res, k*h/t);
      t += h;
      if (t > 8.0) break; // max-t
   }
   return clamp(res, 0.0, 1.0);
}

vec3 GetNormal(vec3 pos, float s)
{
   if (pos.y < FLOOR_YPOS + DISTANCE_MIN)
   {
      return vec3(0.0,1.0,0.0);
   }
   else
   {
      vec3 n;
      n.x = s - Dist(pos - delta.xyy);
      n.y = s - Dist(pos - delta.yxy);
      n.z = s - Dist(pos - delta.yyx);

      return normalize(n);
   }
}

// Based on a shading method by Ben Weston. Added AO and SoftShadows to original.
vec4 Shading(vec3 pos, vec3 rd, vec3 norm)
{
   vec3 light;

   // simple pos test on pos.y for floor (see Dist() above) - different colour and no spec for floor
   if (pos.y > FLOOR_YPOS+DISTANCE_MIN)
   {
      light = lightColour * max(0.0, dot(norm, lightDir));
      vec3 heading = normalize(-rd + lightDir);
      float spec = pow(max(0.0, dot(heading, norm)), specularHardness);
      light = (diffuse * light) + (spec * specular);
      if (shadows) light *= SelfShadow(pos, lightDir);   // harder edged shadows on object
      if (ao) light += CalcAO(pos, norm) * ambientFactor;
   }
   else
   {
      light = vec3(0.1,0.66,0.2) * max(0.0, dot(norm, lightDir));
      if (shadows) light *= SoftShadow(pos, lightDir);   // softer edged shadows on floor
      if (ao) light += CalcAO(pos, norm) * max(ambientFactor-0.25, 0.0);
   }

   return vec4(light, 1.0);
}

// Original method by David Hoskins
vec3 Sky(in vec3 rd)
{
   float sunAmount = max(dot(rd, lightDir), 0.0);
   float v = pow(1.0 - max(rd.y,0.0),6.);
   vec3 sky = mix(vec3(.1, .2, .3), vec3(.32, .32, .32), v);
   sky += lightColour * sunAmount * sunAmount * .25 + lightColour * min(pow(sunAmount, 800.0)*1.5, .3);
   return sky;
}

// Camera function by TekF
// Compute ray from camera parameters
vec3 GetRay(vec3 dir, vec2 pos)
{
   pos = pos - 0.5;
   pos.x *= resolution.x/resolution.y;

   dir = normalize(dir);
   vec3 right = normalize(cross(vec3(0.,1.,0.),dir));
   vec3 up = normalize(cross(dir,right));

   return dir + right*pos.x + up*pos.y;
}

vec4 March(vec3 ro, vec3 rd)
{
   float t = 0.0;
   for (int i=0; i<RAY_DEPTH; i++)
   {
      vec3 p = ro + rd * t;
      float d = Dist(p);
      if (abs(d) < DISTANCE_MIN)
      {
         return vec4(p, d);
      }
      t += d;
      if (t >= MAX_DEPTH) break;
   }
   return vec4(0.0);
}

void main()
{
   const int ANTIALIAS_SAMPLES = 4;
   const int DOF_SAMPLES = 16;

   const bool dof = false;

   vec4 res = vec4(0.0);

   if (antialias)
   {
      vec2 p;
      float d_ang = 2.*PI / float(ANTIALIAS_SAMPLES);
      float ang = d_ang * 0.33333;
      float r = 0.3;
      for (int i = 0; i < ANTIALIAS_SAMPLES; i++)
      {
         p = vec2((gl_FragCoord.x + cos(ang)*r) / resolution.x, (gl_FragCoord.y + sin(ang)*r) / resolution.y);
         vec3 ro = cameraPos;
         vec3 rd = normalize(GetRay(cameraLookat-cameraPos, p));
         vec4 _res = March(ro, rd);
         if (_res.a != 0.0) res.xyz += Shading(_res.xyz, rd, GetNormal(_res.xyz, _res.a)).xyz;
         else res.xyz += Sky(rd);
         ang += d_ang;
      }
      res.xyz /= float(ANTIALIAS_SAMPLES);
   }
   else if (dof)
   {
      vec2 p = gl_FragCoord.xy / resolution.xy;
      vec3 ro = cameraPos;
      vec3 rd = normalize(GetRay(cameraLookat-cameraPos, p));
      vec4 _res = March(ro, rd);

      float d_ang = 2.*PI / float(DOF_SAMPLES);
      float ang = d_ang * 0.33333;
      // cheap DOF! - offset by camera zdiff (as cam/lookat are quite far apart)
      float r = max(0.3, abs(cameraLookat.z - _res.z + 0.0) * .2);
      for (int i = 0; i < DOF_SAMPLES; i++)
      {
         p = vec2((gl_FragCoord.x + cos(ang)*r) / resolution.x, (gl_FragCoord.y + sin(ang)*r) / resolution.y);
         ro = cameraPos;
         rd = normalize(GetRay(cameraLookat-cameraPos, p));
         _res = March(ro, rd);
         if (_res.a != 0.0) res.xyz += Shading(_res.xyz, rd, GetNormal(_res.xyz, _res.a)).xyz;
         else res.xyz += Sky(rd);
         ang += d_ang;
      }
      res.xyz /= float(DOF_SAMPLES);
   }
   else
   {
      vec2 p = gl_FragCoord.xy / resolution.xy;
      vec3 ro = cameraPos;
      vec3 rd = normalize(GetRay(cameraLookat-cameraPos, p));
      res = March(ro, rd);
      if (res.a != 0.0) res.xyz = Shading(res.xyz, rd, GetNormal(res.xyz, res.a)).xyz;
      else res.xyz = Sky(rd);
   }

   gl_FragColor = vec4(res.rgb, 1.0);
}
