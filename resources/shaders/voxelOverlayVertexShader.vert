#version 150

in vec3 position;
in vec2 textureCoordinates;
out vec2 textureCoord;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

void main(void){
	vec4 worldPosition = transformationMatrix * vec4(position,1.0);
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCam;
	textureCoord = textureCoordinates;
}