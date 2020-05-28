#version 330

in vec2 textureCoords;
out vec4 out_Color;

uniform sampler2D te;

void main(void){
	vec4 color = texture(te, textureCoords);
	
	if (color.a < 0.5){
		discard;
	}
	
    out_Color = color;
}