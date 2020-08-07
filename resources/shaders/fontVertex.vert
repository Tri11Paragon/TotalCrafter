#version 330

in vec2 position;
in vec2 textureCoords;

out vec2 pass_textureCoords;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;

const vec2 negs = vec2(2,-2);
const vec2 remove = vec2(-2,0);

void main(void){

	gl_Position = projectionMatrix * (transformationMatrix * (vec4((position * negs) - remove, 0.0, 1.0)));
	pass_textureCoords = textureCoords;

}