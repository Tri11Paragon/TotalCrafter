#version 330

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;

out vec2 textureCoords;

uniform mat4 translationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(void){

	vec4 totalLocalPos = vec4(0.0);
	vec4 totalNormal = vec4(0.0);

	vec4 worldPosition = translationMatrix * vec4(position,1.0);
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCam;
	textureCoords = textureCoordinates;
}
