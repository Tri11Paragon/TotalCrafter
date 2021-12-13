#version 330 core

in vec3 position;
in float data;
in vec3 normal;

out vec2 textureCoords;
out float lightLevel;
out float layerF;
out vec3 normalo;
out vec3 fragpos;

uniform mat4 translationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(){

	vec4 worldPosition = translationMatrix * vec4(position - 0.5f,1.0);
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	fragpos = worldPosition.xyz;
	normalo = normal * transpose(inverse(mat3(translationMatrix)));
    gl_Position = projectionMatrix * positionRelativeToCam;

	int idata = int(data);
	textureCoords = vec2((idata >> 5) & 0x1F, idata & 0x1F);

	//lightLevel = (idata >> 2 & 0xF) + (idata >> 6 & 0xF);
	
	//if (lightLevel > 15){
		lightLevel = 15;
	//}
	layerF = (idata >> 10) & 0x2FF;
	

}
