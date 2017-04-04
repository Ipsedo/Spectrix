precision mediump float;

varying vec3 normal;
varying vec3 newVp;

uniform vec3 u_LightPos;
varying vec3 v_Position;

uniform float u_light_coef;
uniform float u_distance_coef;

uniform sampler2D textureMap;

void main () {
    float distance = length(u_LightPos - v_Position);
    vec3 lightVector = normalize(u_LightPos - v_Position);
    float diffuse = max(0.2, dot(normal, lightVector)) * u_light_coef;
    diffuse = diffuse * (1.0 / (1.0 + (u_distance_coef * distance * distance)));
    vec4 frag_colour = texture2D(textureMap, newVp.xz);
    gl_FragColor = vec4(frag_colour.rgb * diffuse, 1.0);
}