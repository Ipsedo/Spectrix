uniform mat4 u_MVPMatrix;
uniform mat4 u_MVMatrix;
uniform vec4 u_material_ambient_Color;
uniform vec4 u_material_diffuse_Color;
uniform vec4 u_material_specular_Color;
uniform float u_material_shininess;
attribute vec4 a_Position;
attribute vec3 a_Normal;
varying vec3 v_Position;
varying vec3 v_Normal;
void main(){
    v_Position = vec3(u_MVMatrix * a_Position);
    v_Normal = normalize(vec3(u_MVMatrix * vec4(a_Normal, 0.0)));
    gl_Position = u_MVPMatrix * a_Position;
}
