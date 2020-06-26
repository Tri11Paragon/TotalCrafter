#version 330

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D guiTexture;
uniform sampler2D guiTexture2;
uniform sampler2D guiTexture3;

uniform int using_textures;
uniform vec3 color;

uniform vec2 minpos;
uniform vec2 maxpos;
uniform float screenHeight;

void main(void){
	
	if (maxpos.x > 0 && maxpos.y > 0){
		if (gl_FragCoord.x > minpos.x && (screenHeight - gl_FragCoord.y) > minpos.y){
			if (gl_FragCoord.x < maxpos.x && (screenHeight - gl_FragCoord.y) < maxpos.y){
				if (color.x >= 0){
					out_Color = vec4(color, 1.0);
				} else {
					vec4 t1 = texture(guiTexture,textureCoords);
					vec4 t2 = texture(guiTexture2,textureCoords);
					vec4 t3 = texture(guiTexture3,textureCoords);
					
					if (using_textures == 0){
						out_Color = t1;
					} else if(using_textures == 1) {
						out_Color = (t2);
					} else {
						out_Color = (t3);
					}
				}
			}
		}
	} else {
		if (color.x >= 0){
			out_Color = vec4(color, 1.0);
		} else {
			vec4 t1 = texture(guiTexture,textureCoords);
			vec4 t2 = texture(guiTexture2,textureCoords);
			vec4 t3 = texture(guiTexture3,textureCoords);
			
			if (using_textures == 0){
				out_Color = t1;
			} else if(using_textures == 1) {
				out_Color = (t2);
			} else {
				out_Color = (t3);
			}
		}
	}
}