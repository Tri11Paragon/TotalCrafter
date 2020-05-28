#version 150

in vec2 textureCoord;
out vec4 out_Color;

uniform sampler2D te;

void main(void){
	vec4 color = texture(te, textureCoord);
	if (color.a < 0.1){
		discard;
	}
    out_Color = color;
}