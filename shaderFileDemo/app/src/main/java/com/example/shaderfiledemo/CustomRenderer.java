package com.example.shaderfiledemo;

import android.content.Context;
import android.graphics.Shader;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CustomRenderer implements GLSurfaceView.Renderer {

    private final String TAG = "CustomRenderer";
    private final int VERTEX_LOCATION_POSITION = 0;
    private Context mContext;
    private FloatBuffer vertexBuffer;

    private float[] vertexPoints = new float[]{
            -0.5f, 0.0f, 0.0f,  // V0
            0.5f,  0.0f, 0.0f,  // V1
            0.0f,  0.5f, 0.0f   // V2
    };

    public CustomRenderer(Context context){
        mContext = context;
        vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.length * Float.SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(vertexPoints);
        vertexBuffer.position(0);
    }

    public void initShader() {
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        int vertexShaderId = ShaderUtil.compileVertexShader(ResReadUtil.readResource(mContext, R.raw.vertex_shader));
        int fragmentShaderId = ShaderUtil.compileFragmentShader(ResReadUtil.readResource(mContext, R.raw.fragment_shader));
        int programId = ShaderUtil.linkProgram(vertexShaderId, fragmentShaderId);
        Log.d(TAG, "programId is " + programId);
        GLES30.glUseProgram(programId);

        GLES30.glVertexAttribPointer(VERTEX_LOCATION_POSITION, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);
        GLES30.glEnableVertexAttribArray(VERTEX_LOCATION_POSITION);

        GLES30.glDeleteShader(vertexShaderId);
        GLES30.glDeleteShader(fragmentShaderId);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig egl) {
        initShader();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glLineWidth(5);
        GLES30.glDrawArrays(GLES30.GL_LINE_LOOP, 0, 3);
    }
}
