#version 330

in vec2 textureCoords;
in float layerF;
in float lightLevel;

out vec4 out_Color;

uniform sampler2DArray te;

void main(void){
	vec4 color = texture(te, vec3(textureCoords.x,textureCoords.y,layerF));
	
	if (color.a < 0.5){
		discard;
	}
	
	float lid = pow(lightLevel, 2);
	float lif = lid/225;
	lif = 1;
    out_Color = color * max(vec4(lif, lif, lif, 1.0), vec4(1.0));
}