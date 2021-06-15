#version 330 core

in vec2 textureCoords;
in float lightLevel;
in float layerF;
in vec3 normalo;
in vec3 fragpos;

layout (location = 0) out vec4 gPosition;
layout (location = 1) out vec4 gNormal;
layout (location = 2) out vec4 gAlbedoSpec;

uniform sampler2DArray te;

void main(){
    gPosition = vec4(fragpos, 1.0f);
    //gPosition = vec3(1.0f, 0.0f, 0.0f);
    gNormal = vec4(normalize(normalo), 1.0f);
    gAlbedoSpec = texture(te, vec3(textureCoords.x,textureCoords.y,layerF));
    //gPosition = gAlbedoSpec;
    //gNormal = gAlbedoSpec;
    //gAlbedoSpec = vec4(1.0f, 0.0f, 0.0f, 1.0f);
}