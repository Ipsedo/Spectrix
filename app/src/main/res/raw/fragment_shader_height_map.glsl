precision mediump float;

varying vec3 normal;
varying float z;

uniform vec3 u_LightPos;
varying vec3 v_Position;

void main () {
    float distance = length(u_LightPos - v_Position);
    vec3 lightVector = normalize(u_LightPos - v_Position);

  float diffuse = max(0.2, dot(normal, lightVector));
  //frag_colour.rgb = vec3(diffuse);
  vec4 frag_colour = vec4(vec3(diffuse), 1.0);

  if(z <= 0.3){
    float alpha = z / 0.3;
    frag_colour = (1.0 - alpha) * vec4(0.0, 0.0, 1.0, 1.0) + alpha * vec4(0.0, 1.0, 0.0, 1.0);
  } else {
    float alpha = min((z - 0.3) / 0.7, 1.0);
    frag_colour = (1.0 - alpha) * vec4(0.0, 1.0, 0.0, 1.0) + alpha * vec4(1.0, 1.0, 1.0, 1.0);
  }

  gl_FragColor = vec4(frag_colour.rgb * diffuse, 1.0);
}