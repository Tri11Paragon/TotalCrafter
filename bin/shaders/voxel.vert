#version 330

in vec3 position;
in float data;

out vec2 textureCoords;
out float lightLevel;
out float layerF;

uniform mat4 translationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(void){

	vec4 worldPosition = translationMatrix * vec4(position - 0.5f,1.0);
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCam;
	
	int idata = int(data);
	textureCoords = vec2((idata >> 1) & 0x1, idata & 0x1);
	
	lightLevel = (idata >> 2 & 0xF) + (idata >> 6 & 0xF);
	
	if (lightLevel > 15){
		lightLevel = 15;
	}
	layerF = (idata >> 10) & 0x2FF;
	
}
