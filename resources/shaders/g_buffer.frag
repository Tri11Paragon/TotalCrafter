#version 330 core

in vec2 textureCoords;
in vec3 normal;
in vec3 fragpos;
in float layerF;

layout (location = 0) out vec4 gAlbedoSpec;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec3 gPosition;

uniform sampler2DArray te;

void main(void){

	gPosition = fragpos;
	
	gNormal = normalize(normal);

	vec4 color = texture(te, vec3(textureCoords.x,textureCoords.y,layerF));
	
	if (color.a < 0.5){
		discard;
	}
	
    gAlbedoSpec.rgb = color.rgb;
    gAlbedoSpec.a = 1.0;
}