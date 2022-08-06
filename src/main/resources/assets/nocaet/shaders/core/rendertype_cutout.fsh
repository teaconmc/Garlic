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
in float nocaetNoise;

out vec4 fragColor;

/* https://stackoverflow.com/a/17897228 Licensed under WTFPL */
vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

float logistic(float x) {
    return 1.0 / (1.0 + pow(2.71828, -x * 5));
}

float conch(float x) {
    if (x < 0.4) {
        return x - 0.125;
    } else {
        return logistic(x);
    }
}

float arclight(float x) {
    if (x < 0.5) {
        return 1;
    } else {
        return 1 - x / 2.0;
    }
}

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    if (color.a < 0.1) {
        discard;
    }
    float timeVary = abs(1.0 - mod(nocaetNoise + GameTime * 300.0, 2.0)) + 0.000001;
    //vec3 hsvColor = vec3((1.0 / 6.0) - logistic(clamp((timeVary + 1.0) / 2.0, 0.0, 0.8)) / 6.0 * nocaetProgress, 0.875, clamp(timeVary, 0.625, 1));
    vec3 hsvColor = vec3(0.16667 - logistic(conch(nocaetProgress) * timeVary) / 6.0 , 0.875, arclight(nocaetProgress * timeVary));
    vec3 rgbColor = hsv2rgb(hsvColor);
    fragColor = linear_fog(color * vec4(rgbColor, 1.0), vertexDistance, FogStart, FogEnd, FogColor);
}
