#version 330 core

out vec4 out_Color;

in vec2 textureCoords;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gAlbedoSpec;

uniform vec3 lightPos[128];
uniform float lightAmount;
uniform vec3 viewPos;

void main(){
	vec3 fragpos = texture(gPosition, textureCoords).rgb;
	vec3 normal = texture(gNormal, textureCoords).rgb;
	vec3 diffuse = texture(gAlbedoSpec, textureCoords).rgb;
	
	vec3 lighting = diffuse * 0.5;
	for (int i = 0; i < int(lightAmount); i++){
		vec3 lightDistVec = lightPos[i] - fragpos;
	
		vec3 lightDir = normalize(lightDistVec);
		vec3 diff = max(dot(normal, lightDir), 0.0) * diffuse;
		
		float distance = length(lightDistVec);
		float attenuation = 1.0 / (1.0 + 0.7 * distance + 1.8 * distance * distance);
		diff *= attenuation;
		lighting += diff;
	} 
	out_Color = vec4(lighting, 1.0);
}