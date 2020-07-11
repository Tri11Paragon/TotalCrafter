#version 330 core

out vec4 out_Color;

in vec2 textureCoords;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gAlbedoSpec;

void main(){
	out_Color = vec4(texture(gAlbedoSpec, textureCoords).rgb, 1.0);
}