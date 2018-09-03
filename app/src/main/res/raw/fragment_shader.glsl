precision mediump float;

uniform vec3 uLightPos;

varying vec3 vPosition;
varying vec4 vColor;
varying vec3 vNormal;

void main() {
    float distance = length(uLightPos - vPosition);
    vec3 lightVector = normalize(uLightPos - vPosition);
    float diffuse = max(dot(vNormal,lightVector),0.5);
    diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));

    gl_FragColor = vColor * diffuse;
}
