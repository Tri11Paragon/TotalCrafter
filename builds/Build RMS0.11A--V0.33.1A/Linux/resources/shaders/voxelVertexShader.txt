#version 330

in float position;
in float textureCoordinates;

out vec2 textureCoords;
out float lightLevel;
out float layerF;

uniform mat4 transformationMatrix;

int texi;
int posi;

void main(void){
	//vec4 worldPosition = transformationMatrix * vec4(position,1.0);
	//gl_Position = projectionMatrix * viewMatrix * worldPosition;
	
	posi = int(position);
	
	vec3 posadj = vec3(posi & 0xFF, (posi >> 16), (posi >> 8) & 0xFF) - 0.5f;
	gl_Position = transformationMatrix * vec4(posadj,1.0);
	
	texi = int(textureCoordinates);
	
	textureCoords = vec2(((texi >> 1) & 0x1), ((texi & 0x1)));
	
	lightLevel = ((texi >> 2) & 0xF) + ((texi >> 6) & 0xF);
	lightLevel = 15;
	
	if (lightLevel > 15){
		lightLevel = 15;
	}
	layerF = (texi >> 10) & 0x2FF;
}