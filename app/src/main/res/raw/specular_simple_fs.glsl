precision mediump float;
uniform vec3 u_CameraPosition;
uniform vec3 u_LightPos;
uniform float u_distance_coef;
uniform vec4 u_material_diffuse_Color;
varying vec3 v_Position;
varying vec3 v_Normal;
void main(){
    float distance = length(u_LightPos - v_Position);
    vec3 lightVector = normalize(u_LightPos - v_Position);

    float diffuse_coeff = max(dot(v_Normal, lightVector), 0.1);
    diffuse_coeff = diffuse_coeff * (1.0 / (1.0 + (u_distance_coef * distance * distance)));
    vec4 diffuse = diffuse_coeff * u_material_diffuse_Color;

    float specularCoefficient = 0.0;
    if(diffuse_coeff > 0.0){
        vec3 incidenceVector = -lightVector;
        vec3 reflectionVector = reflect(incidenceVector, v_Normal);
        vec3 surfaceToCamera = normalize(u_CameraPosition - v_Position);
        float cosAngle = max(0.0, dot(surfaceToCamera, reflectionVector));
        specularCoefficient = pow(cosAngle, 96.0);
    }
    vec4 specular = specularCoefficient * vec4(1);

    vec4 ambient = 0.1 * u_material_diffuse_Color;
    gl_FragColor = ambient + diffuse + specular;
}