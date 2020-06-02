#version 330

in vec2 textureCoords;
in float lightLevel;
in float layerF;
//in float visibility;

out vec4 out_Color;

uniform sampler2DArray te;

const vec3 skycolor = vec3(0.5444f, 0.62f, 0.69f);

void main(void){
	vec4 color = texture(te, vec3(textureCoords.x,textureCoords.y,layerF));
	
	if (color.a < 0.1){
		discard;
	}
	
    out_Color = color * max(vec4(lightLevel/15.0, lightLevel/15.0, lightLevel/15.0, 1.0), vec4(0.2));
   // out_Color = mix(vec4(skycolor, 1.0), out_Color, visibility);
    
}