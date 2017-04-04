precision mediump float;
uniform vec3 u_LightPos;
uniform float u_distance_coef;
uniform float u_light_coef;
varying vec3 v_Position;
varying vec4 v_Color;
varying vec3 v_Normal;
void main(){
    float distance = length(u_LightPos - v_Position);
    vec3 lightVector = normalize(u_LightPos - v_Position);
    float diffuse = max(dot(v_Normal, lightVector), 0.1) * u_light_coef;
    diffuse = diffuse * (1.0 / (1.0 + (u_distance_coef * distance * distance)));
    gl_FragColor = v_Color * diffuse;
}