#version 150

in vec3 position;
in vec2 textureCoord;
out vec2 textureCoords;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 translationMatrix;

void main(void){
	
	vec4 worldPosition = translationMatrix * vec4(position,1.0);
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCam;
	textureCoords = textureCoord;

}
