package com.example.textureyuv;

import android.opengl.GLES30;
import android.util.Log;

import javax.microedition.khronos.opengles.GL;

public class ShaderUtil {
    private final static String TAG = "ShaderUtil";

    /**
     * compile shader
     * @param type: Shader type:GLES30.GL_VERTEX_SHADER or GLES30.GL_FRAGMENT_SHADER
     * @param shaderCode: shader code
     * @return: shader id
     */
    public static int compileShader(int type, String shaderCode) {
        final int shaderId = GLES30.glCreateShader(type);
        if (shaderId != 0) {
            GLES30.glShaderSource(shaderId, shaderCode);
            GLES30.glCompileShader(shaderId);
            // check shader code whether it compile error
            final int[] compileStatus = new int[1];
            GLES30.glGetShaderiv(shaderId, GLES30.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == 0) {
                // compile shader code error
                String errInfo = GLES30.glGetShaderInfoLog(shaderId);
                Log.e(TAG, "compileShader error:" + errInfo);
                GLES30.glDeleteShader(shaderId);
                return 0;
            } else {
                return shaderId;
            }
        }

        return 0;
    }

    public static int compileVertexShader(String vertexShaderCode) {
        return compileShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode);
    }

    public static int compileFragmentShader(String fragmentShaderCode) {
        return compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode);
    }

    /**
     * link program
     * @param vertexShaderId: vertex shader
     * @param fragmentShaderId: fragment shader
     * @return
     */
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        final int programId = GLES30.glCreateProgram();
        if (programId != 0) {
            GLES30.glAttachShader(programId, vertexShaderId);
            GLES30.glAttachShader(programId, fragmentShaderId);
            GLES30.glLinkProgram(programId);
            // check link program wether it link error
            final int[] linkStatus = new int[1];
            GLES30.glGetProgramiv(programId, GLES30.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] == 0) {
                String errInfo;
                errInfo = GLES30.glGetProgramInfoLog(programId);
                Log.e(TAG, "linkProgram error:" + errInfo);
                GLES30.glDeleteProgram(programId);
                return 0;
            } else {
                return programId;
            }
        }

        return 0;
    }
}

