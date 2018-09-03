uniform mat4 uMVPMatrix;
uniform mat4 uMVMatrix;
uniform vec3 uLightPos;

attribute vec4 aPosition;
attribute vec4 aColor;
attribute vec3 aNormal;

varying vec4 vColor;
varying vec3 vNormal;
varying vec3 vPosition;

void main() {

    vNormal = vec3(uMVMatrix * vec4(aNormal,0.0));
    vPosition = vec3(uMVMatrix * aPosition);
    vColor = aColor;

    gl_Position = uMVPMatrix * aPosition;
}