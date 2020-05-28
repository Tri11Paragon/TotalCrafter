#version 330

in vec2 textureCoords;

out vec4 out_color;

uniform vec3 color;
uniform sampler2D fontAtlas;

const float width = 0.5f; // change these for changing sizes
const float edge = 0.1f;

const float borderWidth = 0.7f; // must be greater then width
const float borderEdge = 0.1f;

const vec3 outlineColor = vec3(1.0, 0.0, 0.0);

const vec2 offset = vec2(0.0, 0.0); // 0.003, 0.006

void main(void){
	
	float distance = 1 - texture(fontAtlas, textureCoords).a;
	float alpha = 1 - smoothstep(width, width + edge, distance);
	
	float distance2 = 1 - texture(fontAtlas, pass_textureCoords + offset).a;
	float outlineAlpha = 1 - smoothstep(borderWidth, borderWidth + borderEdge, distance2);
	
	float overallAlpha = alpha + (1.0 - alpha) * outlineAlpha;
	vec3 overallColor = mix(outlineColor, color, alpha / overallAlpha);
	
	out_Color = texture(guiTexture,textureCoords);
	out_color = vec4(overallColor, overallAlpha);

}