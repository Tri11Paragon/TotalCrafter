#version 330

in vec2 pass_textureCoords;

out vec4 out_color;

uniform vec3 color;
uniform vec3 outlineColor;
uniform sampler2D fontAtlas;

const float width = 0.5f;
const float edge = 0.1f;

const float borderWidth = 0.7f; // must be greater then width
const float borderEdge = 0.1f;

const vec2 offset = vec2(0.0, 0.0); // 0.003, 0.006

uniform vec2 minpos;
uniform vec2 maxpos;
uniform float screenHeight;

void main(void){
	
	if (maxpos.x > 0 && maxpos.y > 0){
		if (gl_FragCoord.x > minpos.x && (screenHeight - gl_FragCoord.y) > minpos.y){
			if (gl_FragCoord.x < maxpos.x && (screenHeight - gl_FragCoord.y) < maxpos.y){
				float distance = 1 - texture(fontAtlas, pass_textureCoords).a;
				float alpha = 1 - smoothstep(width, width + edge, distance);
				
				float distance2 = 1 - texture(fontAtlas, pass_textureCoords + offset).a;
				float outlineAlpha = 1 - smoothstep(borderWidth, borderWidth + borderEdge, distance2);
				
				float overallAlpha = alpha + (1.0 - alpha) * outlineAlpha;
				vec3 overallColor = mix(outlineColor, color, alpha / overallAlpha);
				
				out_color = vec4(overallColor, overallAlpha);
			}
		}
	} else {
	
		float distance = 1 - texture(fontAtlas, pass_textureCoords).a;
		float alpha = 1 - smoothstep(width, width + edge, distance);
		
		float distance2 = 1 - texture(fontAtlas, pass_textureCoords + offset).a;
		float outlineAlpha = 1 - smoothstep(borderWidth, borderWidth + borderEdge, distance2);
		
		float overallAlpha = alpha + (1.0 - alpha) * outlineAlpha;
		vec3 overallColor = mix(outlineColor, color, alpha / overallAlpha);
		
		out_color = vec4(overallColor, overallAlpha);
		
	}

}
