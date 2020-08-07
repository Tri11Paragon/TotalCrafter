#version 330 core

in vec2 position;

out vec2 textureCoords;

void main(void){
	gl_Position = vec4((position * vec2(2,2)) - vec2(1,1), 0.0, 1.0);
	textureCoords = vec2(((position.x)) , ((position.y)));
}