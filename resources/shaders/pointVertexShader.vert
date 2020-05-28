#version 150

in vec3 position;
out vec3 psd;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 translationMatrix;
uniform float scale;

void main(void){
	psd = position;
	mat4 ModelView = viewMatrix * translationMatrix;
	// Column 0:
	ModelView[0][0] = scale;
	ModelView[0][1] = 0;
	ModelView[0][2] = 0;
	
	// Column 1:
	ModelView[1][0] = 0;
	ModelView[1][1] = scale;
	ModelView[1][2] = 0;
	
	// Column 2:
	ModelView[2][0] = 0;
	ModelView[2][1] = 0;
	ModelView[2][2] = scale;
	vec4 positionRelativeToCam = ModelView * vec4(position,1.0);
	gl_Position = projectionMatrix * positionRelativeToCam;
} 