#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float GameTime;
uniform float nocaetProgress;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec4 normal;

out vec4 fragColor;

/* https://stackoverflow.com/a/17897228 Licensed under WTFPL */
vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    // Hue == 0 -> red, hue == 1/6 -> yellow, / 60.0 to make sure the hue falls in [0, 1 / 60)
    // Saturation is progress divided by Euler's number (base of natural logarithm).
    vec3 red = vec3(nocaetProgress / 60.0, nocaetProgress / 2.71828182846, 1.0);
    vec4 redAsRgb = vec4(hsv2rgb(red), 1.0);
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * redAsRgb * ColorModulator;
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
