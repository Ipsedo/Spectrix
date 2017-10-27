precision mediump float;
uniform vec3 u_CameraPosition;
uniform vec3 u_LightPos;
uniform float u_distance_coef;
uniform float u_light_coef;
uniform vec4 u_material_ambient_Color;
uniform vec4 u_material_diffuse_Color;
uniform vec4 u_material_specular_Color;
uniform float u_material_shininess;
varying vec3 v_Position;
varying vec3 v_Normal;
void main(){
    float distance = length(u_LightPos - v_Position);
    vec3 lightVector = normalize(u_LightPos - v_Position);

    float diffuse_coeff = max(dot(v_Normal, lightVector), 0.1) * u_light_coef;
    diffuse_coeff = diffuse_coeff * (1.0 / (1.0 + (u_distance_coef * distance * distance)));
    vec4 diffuse = diffuse_coeff * u_material_diffuse_Color;

    float specularCoefficient = 0.0;
    if(diffuse_coeff > 0.0){
        vec3 incidenceVector = -lightVector;
        vec3 reflectionVector = reflect(incidenceVector, v_Normal);
        vec3 surfaceToCamera = normalize(u_CameraPosition - v_Position);
        float cosAngle = max(0.0, dot(surfaceToCamera, reflectionVector));
        specularCoefficient = pow(cosAngle, u_material_shininess) * u_light_coef;
    }
    vec4 specular = specularCoefficient * u_material_specular_Color;

    vec4 ambient = 0.1 * u_material_ambient_Color;
    gl_FragColor = ambient + diffuse + specular;
}