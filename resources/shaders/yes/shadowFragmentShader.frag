#version 330

in vec2 textureCoords;

out vec4 out_color;

uniform sampler2D modelTexture;

void main(void){

	float alpha = texture(modelTexture, textureCoords).a;
	if (alpha < 0.1){
		discard;
	}

	out_color = vec4(1.0);
	
}