precision mediump float;

uniform vec3 u_LightPos;
uniform sampler2D u_Texture;

varying vec3 v_Position;
varying vec4 v_Color;

varying vec3 v_Normal;
varying vec2 v_TexCoordinate;

void main()
{
    float distance = length(u_LightPos - v_Position);
    vec3 lightVector = normalize(u_LightPos - v_Position);
    float diffuse = max(dot(v_Normal, lightVector), 0.0);
    diffuse = diffuse * (1.0 / (1.0 + (0.10 * distance)));
    diffuse = diffuse + 0.3;
    gl_FragColor = (v_Color * diffuse * texture2D(u_Texture, v_TexCoordinate));
  }
