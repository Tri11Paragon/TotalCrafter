#version 330

in vec2 textureCoords;
in float lightLevel;
in float layerF;

out vec4 out_Color;

uniform sampler2DArray te;


void main(void){
	vec4 color = texture(te, vec3(textureCoords.x,textureCoords.y,layerF));
	
	if (color.a < 0.1){
		discard;
	}
	
    out_Color = color * max(vec4(lightLevel/15.0, lightLevel/15.0, lightLevel/15.0, 1.0), vec4(0.2));
}