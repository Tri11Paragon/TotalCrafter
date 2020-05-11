#version 150

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D colourTexture;

void main(void){
	vec4 color = texture(colourTexture, textureCoords);
	float brightness = (color.r * 0.2126) + (color.g * 0.7152) + (color.b * 0.0722);
	// add a * brightness for more steep brightness control
	if (brightness > 0.6){
		out_Color = color * brightness * brightness;
	} else {
		out_Color = vec4(0.0);
	}
}