precision mediump float;

uniform vec3 uLightPos;
uniform float uLight;

varying vec3 vPosition;
varying vec4 vColor;
varying vec3 vNormal;

void main() {
    float diffuse = 1.0;

    if(uLight > 0.5){
        float distance = length(uLightPos - vPosition);
        vec3 lightVector = normalize(uLightPos - vPosition);
        diffuse = max(dot(vNormal,lightVector),0.5);
        diffuse = diffuse * (1.0 / (1.0 + (0.1 * distance * distance)+(0.1 * distance)));
    }

    gl_FragColor = vColor * diffuse;
}
