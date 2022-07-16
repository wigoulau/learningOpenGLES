package com.example.shaderdemo;

import androidx.appcompat.app.AppCompatActivity;

import javax.microedition.khronos.egl.EGLConfig;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "MainActivity";
    GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        initView();
    }

    void initView() {
        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setEGLContextClientVersion(3);
        setContentView(mGLSurfaceView);
        ShaderRenderer render = new ShaderRenderer();
        mGLSurfaceView.setRenderer(render);
    }


    private class ShaderRenderer implements GLSurfaceView.Renderer {
        private String vertexShaderCode = "#version 300 es\n" +
                "layout (location = 0) in vec4 vPosition;\n" +
                "out vec4 vColor;\n" +
                "void main() { \n" +
                "     gl_Position  = vPosition;\n" +
                "     gl_PointSize = 10.0;\n" +
                "     vColor = vec4(1.0,0.0,0.0,1.0);\n" +
                "}";
        private String fragmentShaderCode = "#version 300 es\n" +
                "precision mediump float;\n" +
                "in vec4 vColor;\n" +
                "out vec4 fragColor;\n" +
                "void main() { \n" +
                "     fragColor = vColor;\n" +
                "}";

        /**
         * 点的坐标
         */
        private float[] vertexPoints = new float[] {
                0.25f, 0.25f, 0.0f,  //V0
                -0.75f, 0.25f, 0.0f, //V1
                -0.75f, -0.75f, 0.0f, //V2
                0.25f, -0.75f, 0.0f, //V3

                0.75f, -0.25f, 0.0f, //V4
                0.75f, 0.75f, 0.0f, //V5
                -0.25f, 0.75f, 0.0f, //V6
                -0.25f, -0.25f, 0.0f, //V7

                -0.25f, 0.75f, 0.0f, //V6
                -0.75f, 0.25f, 0.0f, //V1

                0.75f, 0.75f, 0.0f, //V5
                0.25f, 0.25f, 0.0f, //V0

                -0.25f, -0.25f, 0.0f, //V7
                -0.75f, -0.75f, 0.0f, //V2

                0.75f, -0.25f, 0.0f, //V4
                0.25f, -0.75f, 0.0f //V3
        };

        // 着色器程序ID
        private int mProgramId = 0;
        private FloatBuffer vertexBuffer;

        public ShaderRenderer() {
            // 分配内存
            vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.length * Float.SIZE)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            // 将点坐标数据传入内存
            vertexBuffer.put(vertexPoints);
            vertexBuffer.position(0);
        }
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig var2) {
            // 设置红色背景
            GLES30.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);

            // 编译着色器
            int vertexShaderId = ShaderUtil.compileVertexShader(vertexShaderCode);
            int fragmentShaderId = ShaderUtil.compileFragmentShader(fragmentShaderCode);
            // 链接程序片段
            mProgramId = ShaderUtil.linkProgram(vertexShaderId, fragmentShaderId);
            if (mProgramId != 0) {
                GLES30.glUseProgram(mProgramId);
                final int POSITION_COMPONENT_COUNT = 3;
                GLES30.glVertexAttribPointer(0, POSITION_COMPONENT_COUNT, GLES30.GL_FLOAT, false, 0, vertexBuffer);
                GLES30.glEnableVertexAttribArray(0);
            }
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int w, int h) {
            // 设置视图窗口大小
            GLES30.glViewport(0, 0, w, h);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            // 清除缓存
            GLES30.glClear(GLES30.GL_COLOR_CLEAR_VALUE);

            GLES30.glLineWidth(5);
            GLES30.glDrawArrays(GLES30.GL_LINE_LOOP, 0, 4);

        }
    }
}
