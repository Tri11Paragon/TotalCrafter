#version 330 core

in vec3 position;
in vec2 texCoords;

out vec2 textureCoords;

uniform mat4 translationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(){
	textureCoords = texCoords;
	gl_Position = vec4(position, 1.0f);
}
