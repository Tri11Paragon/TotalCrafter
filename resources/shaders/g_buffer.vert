#version 330 core

in vec3 position;
in vec3 normals;
in float data;

out vec2 textureCoords;
out vec3 normal;
out vec3 fragPos;
out float layerF;

uniform mat4 translationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 cameraPos;

void main(void){

	vec4 worldPosition = translationMatrix * vec4(position - 0.5f,1.0);
	fragPos = worldPosition.xyz;
	
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCam;
	
	int idata = int(data);
	textureCoords = vec2((idata >> 1) & 0x1, idata & 0x1);
	
	layerF = (idata >> 10) & 0x2FF;
	
	mat3 normalMatrix = transpose(inverse(mat3(translationMatrix)));
	normal = normalMatrix * normals;
	
}