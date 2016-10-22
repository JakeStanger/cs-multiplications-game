#version 330

in vec2 outTexCoord;
in vec3 mvPos;

out vec4 fragColour;

uniform sampler2D texture_sampler;
uniform vec3 colour;
uniform int hasTexture;

void main()
{
    if(hasTexture == 1) fragColour = vec4(colour, 1) * texture(texture_sampler, outTexCoord);
    else fragColour = vec4(colour, 1);
}