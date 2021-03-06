#version 400

in vec3 psd;
out vec4 out_Color;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 translationMatrix;
uniform float time;


#define PI 3.141592
#define iTime time
#define scale 0.7
#define iResolution vec2(scale, scale)

float circleshape(vec3 position, float radius){
	return step(radius, length(position));
}

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

float superformula(float phi, float a, float b, float m, float n1, float n2, float n3) {
  return pow( pow( abs( cos(m * phi / 4.0) / a ), n2 ) + pow( abs( sin(m * phi / 4.0) / b ), n3 ), -1.0 / n1 );
}

float renderFormula(float now, float t1, float t2, float t3, float i) {
  	float s1 = now / t1+time;
  	float s2 = now / t2+time*0.25;
  	float s3 = now / t3+time*0.5;
  
  	float a = abs(sin(s1));
  	float b = abs(sin(s1));
  
  	float m = abs(sin(s2) * 5.0);
  	float n1 = abs(sin(s3) * 5.0);
  	float n2 = abs(sin(s2) * 5.0);
  	float n3 = abs(sin(s1) * 5.0);
	float radius = superformula(i / 360.0 * 3.141592 * 2.0,a,b,m,n1,n2,n3);	
	return radius;
}


float checkIfBelongsToMandelbrotSet(vec2 p) {
    float realComponentOfResult = p.x;
    float imaginaryComponentOfResult = p.y;
    const float maxIterations = 50.0;
    for(float i = 0.0; i < maxIterations; i+= 1.0) {
         float tempRealComponent = realComponentOfResult * realComponentOfResult
                                 - imaginaryComponentOfResult * imaginaryComponentOfResult
                                 + p.x;
         float tempImaginaryComponent = 2.0 * realComponentOfResult * imaginaryComponentOfResult
                                 + p.y;
         realComponentOfResult = tempRealComponent;
         imaginaryComponentOfResult = tempImaginaryComponent;
         
         if(realComponentOfResult * imaginaryComponentOfResult > 5.0) 
            return (i / maxIterations);
    }
    return 0.0;   // Return zero if in set        
}

struct Kali {
	vec3 col;
    float d;
    float fold;
};

Kali kali_set(vec3 pos, vec3 param) 
{
    float d = 10000.;
    vec3 col = vec3(0.);
    vec4 p = vec4(pos, 1.);
    for (int i=0; i<17; ++i)
    {
        p = abs(p) / dot(p.xyz, p.xyz);
        //p = (1.+.4*sin(iTime/53.))*abs(p.zyxw) / dot(p.xyz, p.xyz);
        
        d = min(d, (length(p.xy-vec2(0,.01))-.03) / p.w);
        col = max(col, p.xyz);
        p.xyz -= param;
    }
    mat3 colmat = mat3(
        1,0.4,0.,
        0.3,1,0.0,
        -.1,0.03*col.y,1);
    return Kali(colmat*col, d, p.w);
}


vec3 kali_param;
vec3 render_scene(in vec3 ro, in vec3 rd)
{  
    vec3 col = vec3(0.);
    float sum_samples = 0.;
    
    float t = 0.;
    const float max_t = 0.03;
    for (int i=0; i<100; ++i)
    {
        if (t > max_t)
            break;
        float nt = t / max_t;
    	vec3 pos = ro + t * rd;
        Kali kali = kali_set(pos, kali_param);
        //Kali kali2 = kali_set(kali.col, param);
        
        float sampling = max(0., nt*(1.-nt)-dot(col,col)*.001);
        float surf = smoothstep(.0001, .0, kali.d);
        
        col += sampling * surf * (kali.col+vec3(2.))/3.;
        sum_samples += sampling;
        
        float fwd = pow(kali.d, 1.1);
        fwd = min(fwd, 0.0003);
        fwd = min(fwd, .9/kali.fold);
        fwd = max(fwd, 0.00001);
        t += fwd;
    }
    return col / sum_samples;
}

struct PathParam {
	vec3 freq, amp, offs, param;
};

const PathParam path_f1 = PathParam(vec3(3.,  3.,  1.9),vec3(0.11,  0.04,  0.03), vec3(.0,  0.0,  .213), vec3(.706));
const PathParam path_f2 = PathParam(vec3(3.,  5.,  3.),	vec3(0.13,  0.05, 0.18), vec3(.0,  0.198,  .204), vec3(.7,.7,.69));
const PathParam path_f3 = PathParam(vec3(3.,  5.,  3.),	vec3(-0.13,  0.03, 0.11), vec3(.012,  0.204,  .245), vec3(.8,.6,.69));
const PathParam path_f4 = PathParam(vec3(3.,  4.,  5.),	vec3(0.01,  0.06, -0.04), vec3(0.02,  0.23,  .34), vec3(.5,.7,.5));
const PathParam path_f5 = PathParam(vec3(4.,  5.,  4.),	vec3(0.09,  0.019, 0.08), vec3(.021,  0.305,  .5095), vec3(.7));
//#define FIX_SCENE 4

vec3 path_f(in float t, in PathParam p) {
    t *= 2.;
	return sin(t/p.freq+vec3(0., 1.56, 1.56)) * p.amp / 10. + p.offs;
}

vec3 path(in float t, in float offs) {
    t *= .83;
    t += 42.; // thumbnail image
    float 
#ifdef FIX_SCENE
        scene_t = float(FIX_SCENE) - 1.,
#else        
        scene_t = t / 14.1 + offs/30.,
#endif        
        scene = mod(scene_t, 5.),
        blend = mod(scene_t, 1.);
    
    PathParam p1, p2;
    if (scene < 1.)
        p1 = path_f1, p2 = path_f2;
    else if (scene < 2.)
        p1 = path_f2, p2 = path_f3;
    else if (scene < 3.)
        p1 = path_f3, p2 = path_f4;
    else if (scene < 4.)
        p1 = path_f4, p2 = path_f5;
    else if (scene < 5.)
        p1 = path_f5, p2 = path_f1;
    
    t += offs;
    blend = smoothstep(0.4, 0.6, blend);
    vec3 p = mix(path_f(t, p1), path_f(t, p2), blend);
    kali_param = mix(p1.param, p2.param, blend);
	return p;
}


void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 uv = (fragCoord - .5*scale) / scale * 2.;
    
    vec3 look = path(iTime, 5.);
    vec3 pos = path(iTime, 0.);
    //vec3 dir = normalize(vec3(uv, -1.+.4*length(uv)));
    
    vec3 fwd = normalize(look-pos);
    vec3 rgt = normalize(vec3(fwd.z, (look.y-look.z)*.5, -fwd.x));
    vec3 up = cross(fwd, rgt);
    
    vec3 dir = normalize(fwd * (1.-.5*length(uv)) + (uv.x*rgt + uv.y*up));
    
    vec3 col = render_scene(pos, dir);
	vec2 suv = fragCoord / scale * 2. - 1.;
	
    col *= 1.-pow(length(suv)*.66, 1.9);
    
    col = pow(col, vec3(1./1.6));
	fragColor = vec4(col,1.0);
}

void mainVR( out vec4 fragColor, in vec2 fragCoord, in vec3 ro, in vec3 rd)
{
	vec3 pos = path(iTime, 0.);
	vec3 col = render_scene(pos, rd);
	vec2 suv = fragCoord / scale * 2. - 1.;
	col *= 1.-pow(length(suv)*.66, 1.9);
	col = pow(col, vec3(1./1.6));
	fragColor = vec4(col,1.0);
}

const vec4 iMouse = vec4(0.0);

const float MAX_TRACE_DISTANCE = 10.0;
const float INTERSECTION_PRECISION = 0.001;
const int NUM_OF_TRACE_STEPS = 100;

float distSphere(vec3 p, float radius) 
{
    return length(p) - radius;
}

mat3 calcLookAtMatrix( in vec3 ro, in vec3 ta, in float roll )
{
    vec3 ww = normalize( ta - ro );
    vec3 uu = normalize( cross(ww,vec3(sin(roll),cos(roll),0.0) ) );
    vec3 vv = normalize( cross(uu,ww));
    return mat3( uu, vv, ww );
}

void doCamera( out vec3 camPos, out vec3 camTar, in float time, in vec2 mouse )
{
    float radius = 4.0;
    float theta = 0.3 + 5.0*mouse.x - iTime*0.5;
    float phi = 3.14159*0.4;//5.0*mouse.y;
    
    float pos_x = radius * cos(theta) * sin(phi);
    float pos_z = radius * sin(theta) * sin(phi);
    float pos_y = radius * cos(phi);
    
    camPos = vec3(pos_x, pos_y, pos_z);
    camTar = vec3(0.0,0.0,0.0);
}

float smin( float a, float b, float k )
{
    float res = exp( -k*a ) + exp( -k*b );
    return -log( res )/k;
}

float opS( float d1, float d2 )
{
    return max(-d1,d2);
}

float opU( float d1, float d2 )
{
    return min(d1,d2);
}

// noise func
float hash( float n ) { return fract(sin(n)*753.5453123); }
float noise( in vec3 x )
{
    vec3 p = floor(x);
    vec3 f = fract(x);
    f = f*f*(3.0-2.0*f);
	
    float n = p.x + p.y*157.0 + 113.0*p.z;
    return mix(mix(mix( hash(n+  0.0), hash(n+  1.0),f.x),
                   mix( hash(n+157.0), hash(n+158.0),f.x),f.y),
               mix(mix( hash(n+113.0), hash(n+114.0),f.x),
                   mix( hash(n+270.0), hash(n+271.0),f.x),f.y),f.z);
}

// checks to see which intersection is closer
// and makes the y of the vec2 be the proper id
vec2 opU( vec2 d1, vec2 d2 ){
	return (d1.x<d2.x) ? d1 : d2; 
}

float opI( float d1, float d2 )
{
    return max(d1,d2);
}

//--------------------------------
// Modelling 
//--------------------------------
vec2 map( vec3 pos ){  
   
    float sphere = distSphere(pos, 1.75) + noise(pos * 1.0 + iTime*0.75);   
    float t1 = sphere;
    
    t1 = smin( t1, distSphere( pos + vec3(1.8,0.0,0.0), 0.2 ), 2.0 );
    t1 = smin( t1, distSphere( pos + vec3(-1.8,0.0,-1.0), 0.2 ), 2.0 );
   
   	return vec2( t1, 1.0 );
    
}

vec2 map2( vec3 pos ){  
   
    //float sphere = distSphere(pos, 1.0) + noise(pos * 1.2 + vec3(-0.3) + iTime*0.2);
    float sphere = distSphere(pos, 0.45);
    
    sphere = smin( sphere, distSphere( pos + vec3(-0.4,0.0,-1.0), 0.04 ), 5.0 );
    sphere = smin( sphere, distSphere( pos + vec3(-0.5,-0.75,0.0), 0.05 ), 50.0 );
    sphere = smin( sphere, distSphere( pos + vec3(0.5,0.7,0.5), 0.1 ), 5.0 );

   	return vec2( sphere, 1.0 );
}

float shadow( in vec3 ro, in vec3 rd )
{
    const float k = 2.0;
    
    const int maxSteps = 10;
    float t = 0.0;
    float res = 1.0;
    
    for(int i = 0; i < maxSteps; ++i) {
        
        float d = map(ro + rd*t).x;
            
        if(d < INTERSECTION_PRECISION) {
            
            return 0.0;
        }
        
        res = min( res, k*d/t );
        t += d;
    }
    
    return res;
}


float ambientOcclusion( in vec3 ro, in vec3 rd )
{
    const int maxSteps = 7;
    const float stepSize = 0.05;
    
    float t = 0.0;
    float res = 0.0;
    
    // starting d
    float d0 = map(ro).x;
    
    for(int i = 0; i < maxSteps; ++i) {
        
        float d = map(ro + rd*t).x;
		float diff = max(d-d0, 0.0);
        
        res += diff;
        
        t += stepSize;
    }
    
    return res;
}

vec3 calcNormal( in vec3 pos ){
    
	vec3 eps = vec3( 0.001, 0.0, 0.0 );
	vec3 nor = vec3(
	    map(pos+eps.xyy).x - map(pos-eps.xyy).x,
	    map(pos+eps.yxy).x - map(pos-eps.yxy).x,
	    map(pos+eps.yyx).x - map(pos-eps.yyx).x );
	return normalize(nor);
}


vec3 calcNormal2( in vec3 pos ){
    
	vec3 eps = vec3( 0.001, 0.0, 0.0 );
	vec3 nor = vec3(
	    map2(pos+eps.xyy).x - map2(pos-eps.xyy).x,
	    map2(pos+eps.yxy).x - map2(pos-eps.yxy).x,
	    map2(pos+eps.yyx).x - map2(pos-eps.yyx).x );
	return normalize(nor);
}

void renderColor2( vec3 ro , vec3 rd, inout vec3 color, vec3 currPos )
{
    //vec3 lightDir = normalize(vec3(1.0,0.4,0.0));
    vec3 normal = calcNormal2( currPos );
    vec3 normal_distorted = calcNormal2( currPos +  rd*noise(currPos*2.5 + iTime*2.0)*0.75 );

    float ndotl = abs(dot( -rd, normal ));
    float ndotl_distorted = (dot( -rd, normal_distorted ))*0.5+0.5;
    float rim = pow(1.0-ndotl, 3.0);
    float rim_distorted = pow(1.0-ndotl_distorted, 6.0);

    //color = mix( color, normal*0.5+vec3(0.5), rim_distorted+0.15 );
    //color = mix( vec3(0.0,0.1,0.6), color, rim*1.5 );
    color = mix( refract(normal, rd, 0.5)*0.5+vec3(0.5), color, rim );
    //color = mix( vec3(0.1), color, rim );
    color += rim*0.6;
}

// for inside ball
bool renderRayMarch2(vec3 ro, vec3 rd, inout vec3 color ) {
    
    float t = 0.0;
    float d = 0.0;
    
    for(int i = 0; i < NUM_OF_TRACE_STEPS; ++i) 
    {
        vec3 currPos = ro + rd*t;
        d = map2(currPos).x;
        if(d < INTERSECTION_PRECISION) 
        {
            renderColor2( ro, rd, color, currPos );
            return true;
        }
        
        t += d;
    }
    
    if(d < INTERSECTION_PRECISION) 
    {
        vec3 currPos = ro + rd*t;
        renderColor2( ro, rd, color, currPos );
        return true;
    }

    return false;
}

void renderColor( vec3 ro , vec3 rd, inout vec3 color, vec3 currPos )
{
    vec3 lightDir = normalize(vec3(1.0,0.4,0.0));
    vec3 normal = calcNormal( currPos );
    vec3 normal_distorted = calcNormal( currPos +  noise(currPos*1.5 + vec3(0.0,0.0,sin(iTime*0.75))) );
    float shadowVal = shadow( currPos - rd* 0.01, lightDir  );
    float ao = ambientOcclusion( currPos - normal*0.01, normal );

    float ndotl = abs(dot( -rd, normal ));
    float ndotl_distorted = abs(dot( -rd, normal_distorted ));
    float rim = pow(1.0-ndotl, 6.0);
    float rim_distorted = pow(1.0-ndotl_distorted, 6.0);


    color = mix( color, normal*0.5+vec3(0.5), rim_distorted+0.1 );
    color += rim;
    //color = normal;

    // refracted ray-march into the inside area
    vec3 color2 = vec3(0.5);
    renderRayMarch2( currPos, refract(rd, normal, 0.85), color );
    //renderRayMarch2( currPos, rayDirection, color2 );

    //color = color2;
    //color = normal;
    //color *= vec3(mix(0.25,1.0,shadowVal));

    color *= vec3(mix(0.8,1.0,ao));
}

vec3 rayPlaneIntersection( vec3 ro, vec3 rd, vec4 plane )
{
	float t = -( dot(ro, plane.xyz) + plane.w) / dot( rd, plane.xyz );
	return ro + t * rd;
}

bool renderRayMarch(vec3 ro, vec3 rd, inout vec3 color ) 
{
    const int maxSteps = NUM_OF_TRACE_STEPS;
        
    float t = 0.0;
    float d = 0.0;
    
    for(int i = 0; i < maxSteps; ++i) 
    {
        vec3 currPos = ro + rd * t;
        d = map(currPos).x;
        if(d < INTERSECTION_PRECISION) 
        {
            break;
        }
        
        t += d;
    }
    
    if(d < INTERSECTION_PRECISION) 
    {
        vec3 currPos = ro + rd * t;
        renderColor( ro, rd, color, currPos );
        return true;
    }
    
    vec3 planePoint = rayPlaneIntersection(ro, rd, vec4(0.0, 1.0, 0.0, 1.0));
	float shadowFloor = shadow( planePoint, vec3(0.0,1.0,0.0));
	color = color * mix( 0.8, 1.0, shadowFloor );
    
    return false;
}

void mainImage2( out vec4 fragColor, in vec2 fragCoord )
{
	vec2 p = (-iResolution.xy + 2.0*fragCoord.xy)/iResolution.y;
    vec2 m = iMouse.xy/iResolution.xy;
    
    // camera movement
    vec3 ro, ta;
    doCamera( ro, ta, iTime, m );

    // camera matrix
    mat3 camMat = calcLookAtMatrix( ro, ta, 0.0 );  // 0.0 is the camera roll
    
	// create view ray
	vec3 rd = normalize( camMat * vec3(p.xy,2.0) ); // 2.0 is the lens length
    
    // calc color
    vec3 col = vec3(0.9);
    renderRayMarch( ro, rd, col );
    
    // vignette, OF COURSE
    float vignette = 1.0-smoothstep(1.0,2.5, length(p));
    col.xyz *= mix( 0.7, 1.0, vignette);
        
    fragColor = vec4( col , 1. );
}

void main(void){
    /*vec2 pos = psd.xy;
	float wave = 0.0;
	float color = 0.0;
  

	for (int i = 15; i >= 1; i-=1){	
		float x = rand(vec2(i, i+1));
		float wavephase = 2.0*x*13.3*PI+float(i);
		//float wavephase = 0.0;
		float t = time*(6.-float(i)*.1);	
		float wavetime = 0.0;
		float waveSize = 0.025;
		float waveAmp = max(0.0,waveSize - float(i)*0.0016);
		
		float k = 2.0*PI/waveSize;
		float waveshort = 5.0+float(i);	
		float wavestokes = ((1.0-1.0/16.0*pow((k*waveSize),2.0))*cos(pos.x*waveshort+t+wavephase) + 0.5*k*waveSize*cos(2.0*waveshort*pos.x+t+wavephase));
	  
	  	float wave = waveAmp*pow(wavestokes,1.0)+sin(t+x*12.2)*0.01/float(i)+(float(i)*(0.06-float(i)*0.0009)-0.5);	 
		if (pos.y < wave){
		  color = 0.05*float(i);  
		}  
	}
	if (color > 0.){ 
    	//water color
    	//out_Color = vec4(color-0.5, color-0.1,color,1.0);
  	}
  	else{
    	//sky color
    	//out_Color = vec4(.8, 0.8,.9,1.0);
  	}
  	vec2 p = psd.xy;
	p -= 0.0;
	
	float c = renderFormula(p.x*p.x + p.y*p.y, 0.1*p.x, 0.1*p.y, 0.1*p.x, 0.1*p.y);	
	out_Color = vec4( vec3(c), 1.0 );
	
  	mainImage(out_Color, psd.xy + vec2(0.35, 0.35));
  	
  	vec2 pddd = psd.xy;
	pddd -= 0.1;

	float f = .31415 * 8.;
	float x = f*sqrt(p.x * pddd.x);
	float y = f*sqrt(p.y * pddd.y);
	
	float c1 = checkIfBelongsToMandelbrotSet(vec2(x, y));
	float c2 = checkIfBelongsToMandelbrotSet(vec2(pddd.x * pddd.x, pddd.y * pddd.y));
	float c3 = checkIfBelongsToMandelbrotSet(vec2(pddd.x, pddd.y));
	
	out_Color = vec4( c1, c2, c3, 1.0 );
	
	mainImage2(out_Color, psd.xy + vec2(0.35, 0.35));*/
  	
  	out_Color = vec4((cos(time) + sin(psd.y)) * cos(psd.x + psd.y), (sin(psd.x) + cos(time)) * sin(psd.x + psd.y), (cos(psd.x) + sin(time)) * cos(psd.x + psd.y), 1.0);
}