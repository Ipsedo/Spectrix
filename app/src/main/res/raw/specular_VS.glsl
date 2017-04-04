uniform mat4 u_MVPMatrix;
uniform mat4 u_MVMatrix;
attribute vec4 a_Position;
attribute vec4 a_material_ambient_Color;
attribute vec4 a_material_diffuse_Color;
attribute vec4 a_material_specular_Color;
attribute vec3 a_Normal;
varying vec3 v_Position;
varying vec4 v_material_ambient_Color;
varying vec4 v_material_diffuse_Color;
varying vec4 v_material_specular_Color;
varying vec3 v_Normal;
void main(){
    v_Position = vec3(u_MVMatrix * a_Position);
    v_material_ambient_Color = a_material_ambient_Color;
    v_material_diffuse_Color = a_material_diffuse_Color;
    v_material_specular_Color = a_material_specular_Color;
    v_Normal = normalize(vec3(u_MVMatrix * vec4(a_Normal, 0.0)));
    gl_Position = u_MVPMatrix * a_Position;
}
