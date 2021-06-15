#version 330 core

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gAlbedoSpec;

struct Light {
    vec3 Position;
    vec3 Color;
    
    float Linear;
    float Quadratic;
    float Radius;
};
const int NR_LIGHTS = 32;
uniform Light lights[NR_LIGHTS];
uniform float lightAmount;
uniform vec3 viewPos;
uniform vec3 directlight;

uniform mat4 translationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

vec3 calcBlinnPhong(vec3 normal, vec3 lightPos, vec3 fragPos, vec3 viewPos, vec3 diffuse, vec3 lightColor, float shininess){
    vec3 ret = diffuse * 0.1f;

    vec3 lightDir   = normalize(lightPos - fragPos);
    vec3 viewDir    = normalize(viewPos - fragPos);
    vec3 halfwayDir = normalize(lightDir + viewDir);

    float spec = pow(max(dot(normal, halfwayDir), 0.0), shininess);
    vec3 specular = lightColor * spec;

    return specular + ret;
}

void main(){

    vec3 directlightDir = normalize(-directlight);
    vec3 FragPos = texture(gPosition, textureCoords).rgb;
    vec3 Normal = texture(gNormal, textureCoords).rgb;
    vec3 Diffuse = texture(gAlbedoSpec, textureCoords).rgb;
    float Specular = 0.05f;
    
    // then calculate lighting as usual
    vec3 lighting  = Diffuse * 0.1; // hard-coded ambient component

    lighting += Diffuse * max(dot(Normal, directlightDir), 0.0);

    vec3 viewDir  = normalize(viewPos - FragPos);
    for(int i = 0; i < NR_LIGHTS; ++i)
    {
        // calculate distance between light source and current fragment
        float distance = length(lights[i].Position - FragPos);
        if(distance < lights[i].Radius)
        {
            // diffuse
            vec3 lightDir = normalize(lights[i].Position - FragPos);
            vec3 diffuse = max(dot(Normal, lightDir), 0.0) * Diffuse * lights[i].Color;
            // specular
            vec3 halfwayDir = normalize(lightDir + viewDir);  
            float spec = pow(max(dot(Normal, halfwayDir), 0.0), 16.0);
            vec3 specular = lights[i].Color * spec * Specular;
            // attenuation
            float attenuation = 1.0 / (1.0 + lights[i].Linear * distance + lights[i].Quadratic * distance * distance);
            diffuse *= attenuation;
            specular *= attenuation;

            lighting += (diffuse + specular);
        }
    }    
    
    out_Color = vec4(lighting, 1.0);

}

/*vec3 CalcDirLight(DirLight light, vec3 normal, vec3 viewDir)
{
    vec3 lightDir = normalize(-light.direction);
    // diffuse shading
    float diff = max(dot(normal, lightDir), 0.0);
    // specular shading
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    // combine results
    vec3 ambient  = light.ambient  * vec3(texture(material.diffuse, TexCoords));
    vec3 diffuse  = light.diffuse  * diff * vec3(texture(material.diffuse, TexCoords));
    vec3 specular = light.specular * spec * vec3(texture(material.specular, TexCoords));
    return (ambient + diffuse + specular);
}  
struct DirLight {
    vec3 direction;
  
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};  */