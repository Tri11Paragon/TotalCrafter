#version 330

in float position;
in float textureCoordinates;

out vec2 textureCoords;
out float lightLevel;
out float layerF;
//out float visibility;

uniform mat4 translationMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

int texi;
int posi;

const float density = 0.007f;
const float gradient = 1.5f;

void main(void){
	
	posi = int(position);
	
	vec3 posadj = vec3(posi & 0xFF, (posi >> 16), (posi >> 8) & 0xFF) - 0.5f;
	//gl_Position = translationMatrix * vec4(posadj,1.0);
	
	vec4 worldPosition = translationMatrix * vec4(posadj - 0.5f,1.0);
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCam;
	
	texi = int(textureCoordinates);
	
	textureCoords = vec2(((texi >> 1) & 0x1), ((texi & 0x1)));
	
	lightLevel = ((texi >> 2) & 0xF) + ((texi >> 6) & 0xF);
	lightLevel = 15;
	
	if (lightLevel > 15){
		lightLevel = 15;
	}
	layerF = (texi >> 10) & 0x2FF;
	
	
	//float distance = length(posToCam.xyz);
	//visibility = exp(-pow((distance*density), gradient));
	//visibility = clamp(visibility, 0.0, 1.0);
	
	//vec4 worldPosition = translationMatrix * vec4(position,1.0);
	//gl_Position = projectionMatrix * viewMatrix * worldPosition;
	
}