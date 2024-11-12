// magnus_effect.fs
#version 330 core

out vec4 fragColor;
in vec2 fragTexCoord;

uniform sampler2D texture0; // The texture of the ball
uniform float spin;
uniform float velocity;

// Function to create a Magnus effect around the ball
void main() {
    vec2 center = vec2(0.5, 0.5); // Center of the texture
    float dist = distance(fragTexCoord, center);

    // Adjust intensity based on distance from the center
    float intensity = max(0.0, (1.0 - dist * 2.0)) * (spin * velocity * 0.1);

    // Color modification based on intensity
    vec4 texColor = texture(texture0, fragTexCoord);
    fragColor = vec4(texColor.rgb * (1.0 + intensity), texColor.a);
}
