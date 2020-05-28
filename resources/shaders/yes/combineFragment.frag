#version 150

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D colourTexture;
uniform sampler2D highlightTexture;

const float bloom_amount = 0.5f;

void main(void){

	vec4 sceneColor = texture(colourTexture, textureCoords);
	vec4 highlightColor = texture(highlightTexture,textureCoords);
	out_Color = sceneColor + highlightColor * bloom_amount;

}