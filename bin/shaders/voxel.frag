#version 330

in vec2 textureCoords;
in float layerF;
in float lightLevel;

out vec4 out_Color;

uniform sampler2D te;

void main(void){
	vec4 color = texture(te, vec3(textureCoords.x,textureCoords.y,layerF));
	
	if (color.a < 0.5){
		discard;
	}
	
    out_Color = color * max(vec4(lightLevel/15.0, lightLevel/15.0, lightLevel/15.0, 1.0), vec4(0.2));
}