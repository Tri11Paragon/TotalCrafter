#version 330

in vec2 position;
in vec2 textureCoords;

out vec2 pass_textureCoords;

uniform vec2 translation;
uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;

void main(void){

	gl_Position = projectionMatrix * (transformationMatrix * vec4(position, 0.0, 1.0));
	pass_textureCoords = textureCoords;

}