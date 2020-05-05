#version 330

const int MAX_JOINTS = 50;
const int MAX_WEIGHTS = 3;

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;
in ivec3 jointIndices;
in vec3 weights;

out vec2 textureCoords;

uniform mat4 jointTransforms[MAX_JOINTS];

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(void){

	vec4 totalLocalPos = vec4(0.0);
	vec4 totalNormal = vec4(0.0);

	for(int i=0;i<MAX_WEIGHTS;i++){
		mat4 jointTransform = jointTransforms[jointIndices[i]];
		vec4 posePosition = jointTransform * vec4(position, 1.0);
		totalLocalPos += posePosition * weights[i];
		
		vec4 worldNormal = jointTransform * vec4(normal, 0.0);
		totalNormal += worldNormal * weights[i];
	}

	vec4 worldPosition = totalLocalPos;
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCam;
	textureCoords = (textureCoordinates);
}