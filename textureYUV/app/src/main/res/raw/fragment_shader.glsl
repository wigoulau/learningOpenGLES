#version 300 es
precision mediump float;
in vec2 Texcoord;
uniform sampler2D tex_y;
uniform sampler2D tex_u;
uniform sampler2D tex_v;
out vec4 vFragColor;
void main() {
    vec3 yuv;
    vec3 rgb;
    yuv.x = texture(tex_y, Texcoord).r;
    yuv.y = texture(tex_u, Texcoord).r - 0.5;
    yuv.z = texture(tex_v, Texcoord).r - 0.5;
    rgb = mat3(1,       1,          1,
               0,       -0.39465,   2.03211,
               1.13983, -0.58060,   0) * yuv;
    vFragColor = vec4(rgb, 1);
}