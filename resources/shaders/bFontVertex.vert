#version 140

in vec2 position;

out vec2 textureCoords;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform int numberOfRows;

void main(void){

	gl_Position = projectionMatrix * transformationMatrix * vec4(position, 0.0, 1.0);
	textureCoords = vec2((position.x+1.0)/2.0, 1 - (position.y+1.0)/2.0);
	
}