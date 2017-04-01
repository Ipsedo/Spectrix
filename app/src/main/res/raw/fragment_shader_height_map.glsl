precision mediump float;

varying vec3 normal;
varying vec3 newVp;

uniform vec3 u_LightPos;
varying vec3 v_Position;

uniform sampler2D textureMap;

void main () {
    float distance = length(u_LightPos - v_Position);
    vec3 lightVector = normalize(u_LightPos - v_Position);

  float diffuse = max(0.2, dot(normal, lightVector)) * 0.05;
  /*vec4 frag_colour = (0.05 - newVp.z) * vec4(1.0, 1.0, 0.0, 1.0) + newVp.z * vec4(1.0, 0.25, 0.0, 1.0);*/
  vec4 frag_colour = texture2D(textureMap, newVp.xz);

  /*if(z <= 0.3){
    float alpha = z / 0.3;
    frag_colour = (1.0 - alpha) * vec4(1.0, 1.0, 0.0, 1.0) + alpha * vec4(1.0, 0.5, 0.25, 1.0);
  } else {
    float alpha = min((z - 0.3) / 0.7, 1.0);
    frag_colour = (1.0 - alpha) * vec4(1.0, 0.5, 0.25, 1.0) + alpha * vec4(1.0, 0.25, 0.0, 1.0);
  }*/

  gl_FragColor = vec4(frag_colour.rgb * diffuse, 1.0);
}