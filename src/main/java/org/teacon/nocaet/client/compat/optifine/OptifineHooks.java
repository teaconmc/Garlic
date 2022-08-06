package org.teacon.nocaet.client.compat.optifine;

import org.teacon.nocaet.client.GarlicShaders;

import java.lang.reflect.Constructor;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class OptifineHooks {

    private static final Pattern PATTERN_VERSION = Pattern.compile("^\\s*#version\\s+(\\d+).*$", Pattern.MULTILINE | Pattern.UNIX_LINES);
    private static final Pattern PATTERN_FRAME_TIME = Pattern.compile("uniform\\s+float\\s+frameTimeCounter\\s*;", Pattern.MULTILINE | Pattern.UNIX_LINES);
    private static final Pattern PATTERN_CAM_POS = Pattern.compile("uniform\\s+vec3\\s+cameraPosition\\s*;", Pattern.MULTILINE | Pattern.UNIX_LINES);

    public static float getProgress() {
        return GarlicShaders.getProgress();
    }

    @SuppressWarnings("unchecked")
    public static Object injectShader(Object program, Object shaderType, Object buffer) {
        if (programGetName(program).startsWith("gbuffers_terrain")) {
            var isVert = shaderType.toString().equals("VERTEX");
            var isFrag = shaderType.toString().equals("FRAGMENT");
            var insertion = new StringBuilder();
            var text = StreamSupport.stream(((Iterable<String>) buffer).spliterator(), false).collect(Collectors.joining("\n"));
            var matcher = PATTERN_VERSION.matcher(text);
            if (matcher.find()) {
                var end = matcher.end();
                if (isVert && !text.contains("mc_Entity")) {
                    insertion.append("\nin vec3 mc_Entity;");
                }
                if (isVert && !PATTERN_CAM_POS.matcher(text).find()) {
                    insertion.append("\nuniform vec3 cameraPosition;");
                }
                if (isFrag && !PATTERN_FRAME_TIME.matcher(text).find()) {
                    insertion.append("\nuniform float frameTimeCounter;");
                }
                text = text.substring(0, end) + insertion + text.substring(end);
            }
            text = text.replaceAll("void(\\s+)main(\\s*)\\((\\s*)\\)", "void nocaet_main()");
            if (isVert) {
                text += "\n" + TERRAIN_VSH;
            }
            if (isFrag) {
                text += "\n" + TERRAIN_FSH;
            }
            return toBuffer(text);
        } else {
            return buffer;
        }
    }

    private static Object toBuffer(String text) {
        try {
            Class<?> cl = Class.forName("net.optifine.util.LineBuffer");
            Constructor<?> constructor = cl.getConstructor(String[].class);
            return constructor.newInstance((Object) text.lines().toArray(String[]::new));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static String programGetName(Object program) {
        try {
            var method = program.getClass().getMethod("getName");
            return (String) method.invoke(program);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // language=glsl
    private static final String TERRAIN_VSH = """
        //
        // Description : Array and textureless GLSL 2D/3D/4D simplex
        //               noise functions.
        //      Author : Ian McEwan, Ashima Arts.
        //  Maintainer : stegu
        //     Lastmod : 20201014 (stegu)
        //     License : Copyright (C) 2011 Ashima Arts. All rights reserved.
        //               Distributed under the MIT License. See LICENSE file.
        //               https://github.com/ashima/webgl-noise
        //               https://github.com/stegu/webgl-noise
        //
                
        vec3 nocaet_mod289(vec3 x) {
            return x - floor(x * (1.0 / 289.0)) * 289.0;
        }
                
        vec4 nocaet_mod289(vec4 x) {
            return x - floor(x * (1.0 / 289.0)) * 289.0;
        }
                
        vec4 nocaet_permute(vec4 x) {
            return nocaet_mod289(((x*34.0)+10.0)*x);
        }
                
        vec4 nocaet_taylorInvSqrt(vec4 r)
        {
            return 1.79284291400159 - 0.85373472095314 * r;
        }
                
        float nocaet_snoise(vec3 v)
        {
            const vec2  C = vec2(1.0/6.0, 1.0/3.0) ;
            const vec4  D = vec4(0.0, 0.5, 1.0, 2.0);
                
            // First corner
            vec3 i  = floor(v + dot(v, C.yyy) );
            vec3 x0 =   v - i + dot(i, C.xxx) ;
                
            // Other corners
            vec3 g = step(x0.yzx, x0.xyz);
            vec3 l = 1.0 - g;
            vec3 i1 = min( g.xyz, l.zxy );
            vec3 i2 = max( g.xyz, l.zxy );
                
            //   x0 = x0 - 0.0 + 0.0 * C.xxx;
            //   x1 = x0 - i1  + 1.0 * C.xxx;
            //   x2 = x0 - i2  + 2.0 * C.xxx;
            //   x3 = x0 - 1.0 + 3.0 * C.xxx;
            vec3 x1 = x0 - i1 + C.xxx;
            vec3 x2 = x0 - i2 + C.yyy; // 2.0*C.x = 1/3 = C.y
            vec3 x3 = x0 - D.yyy;      // -1.0+3.0*C.x = -0.5 = -D.y
                
            // Permutations
            i = nocaet_mod289(i);
            vec4 p = nocaet_permute( nocaet_permute( nocaet_permute(
            i.z + vec4(0.0, i1.z, i2.z, 1.0 ))
            + i.y + vec4(0.0, i1.y, i2.y, 1.0 ))
            + i.x + vec4(0.0, i1.x, i2.x, 1.0 ));
                
            // Gradients: 7x7 points over a square, mapped onto an octahedron.
            // The ring size 17*17 = 289 is close to a multiple of 49 (49*6 = 294)
            float n_ = 0.142857142857; // 1.0/7.0
            vec3  ns = n_ * D.wyz - D.xzx;
                
            vec4 j = p - 49.0 * floor(p * ns.z * ns.z);  //  mod(p,7*7)
                
            vec4 x_ = floor(j * ns.z);
            vec4 y_ = floor(j - 7.0 * x_ );    // mod(j,N)
                
            vec4 x = x_ *ns.x + ns.yyyy;
            vec4 y = y_ *ns.x + ns.yyyy;
            vec4 h = 1.0 - abs(x) - abs(y);
                
            vec4 b0 = vec4( x.xy, y.xy );
            vec4 b1 = vec4( x.zw, y.zw );
                
            //vec4 s0 = vec4(lessThan(b0,0.0))*2.0 - 1.0;
            //vec4 s1 = vec4(lessThan(b1,0.0))*2.0 - 1.0;
            vec4 s0 = floor(b0)*2.0 + 1.0;
            vec4 s1 = floor(b1)*2.0 + 1.0;
            vec4 sh = -step(h, vec4(0.0));
                
            vec4 a0 = b0.xzyw + s0.xzyw*sh.xxyy ;
            vec4 a1 = b1.xzyw + s1.xzyw*sh.zzww ;
                
            vec3 p0 = vec3(a0.xy,h.x);
            vec3 p1 = vec3(a0.zw,h.y);
            vec3 p2 = vec3(a1.xy,h.z);
            vec3 p3 = vec3(a1.zw,h.w);
                
            //Normalise gradients
            vec4 norm = nocaet_taylorInvSqrt(vec4(dot(p0,p0), dot(p1,p1), dot(p2, p2), dot(p3,p3)));
            p0 *= norm.x;
            p1 *= norm.y;
            p2 *= norm.z;
            p3 *= norm.w;
                
            // Mix final noise value
            vec4 m = max(0.5 - vec4(dot(x0,x0), dot(x1,x1), dot(x2,x2), dot(x3,x3)), 0.0);
            m = m * m;
            return 105.0 * dot( m*m, vec4( dot(p0,x0), dot(p1,x1),
            dot(p2,x2), dot(p3,x3) ) );
        }
                
        float nocaet_frac_noise(vec3 v) {
            float n = 0.0;
            n += nocaet_snoise(v);
            n += nocaet_snoise(v*2.0)*0.5;
            n += nocaet_snoise(v*4.0)*0.25;
            n += nocaet_snoise(v*8.0)*0.125;
            return n;
        }
                
        out float nocaetNoise;
                
        void main() {
            nocaet_main(); // original main
            if (mc_Entity.x == 10990) { // nocaet blocks magic number
            	nocaetNoise = nocaet_frac_noise((gl_Vertex.xyz + cameraPosition) / 50.0);
            } else {
            	nocaetNoise = -200.0;
            }
        }
        """;

    // language=glsl
    private static final String TERRAIN_FSH = """
        /* https://stackoverflow.com/a/17897228 Licensed under WTFPL */
        vec3 nocaet_hsv2rgb(vec3 c)
        {
            vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
            vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
            return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
        }
        
        float nocaet_logistic(float x) {
            return 1.0 / (1.0 + pow(2.71828, -x * 5));
        }
        
        float nocaet_conch(float x) {
            if (x < 0.4) {
                return x - 0.125;
            } else {
                return nocaet_logistic(x);
            }
        }
        
        float nocaet_arclight(float x) {
            if (x < 0.5) {
                return 1;
            } else {
                return 1 - x / 2.0;
            }
        }
                
        uniform float nocaetProgress;
        in float nocaetNoise;
                
        void main() {
            nocaet_main(); // original main
            if (nocaetNoise > -100.0) {
                float timeVary = abs(1.0 - mod(nocaetNoise + frameTimeCounter / 4.0, 2.0)) + 0.000001;
                vec3 hsvColor = vec3(0.16667 - nocaet_logistic(nocaet_conch(nocaetProgress) * timeVary) / 6.0 , 0.875, nocaet_arclight(nocaetProgress * timeVary));
                vec3 rgbColor = nocaet_hsv2rgb(hsvColor);
                gl_FragData[0] = gl_FragData[0] * vec4(rgbColor, 1.0);
            }
        }
        """;
}
