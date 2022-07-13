package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.opengl.GLES10;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        initView();
    }

    public void initView() {
        mGLSurfaceView = new GLSurfaceView(this);
        // 设置版本为OpenGLES 3.0
        mGLSurfaceView.setEGLContextClientVersion(3);
        // 设置窗体视图
        setContentView(mGLSurfaceView);
        // 创建渲染器
        GLSurfaceView.Renderer renderer = new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
                Log.d(TAG, "onSurfaceCteated call");
                // 设置背景色为蓝色，分别对应rgba，参数为0.0 ~ 1.0的浮点数，对应0 ~ 255
                GLES30.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
            }

            @Override
            public void onSurfaceChanged(GL10 gl10, int w, int h) {
                Log.d(TAG, "onSurfaceChanged call:w="+w+", h="+h);
                // 设置视图窗口
                GLES30.glViewport(0, 0, w, h);
            }

            @Override
            public void onDrawFrame(GL10 gl10) {
                //Log.d(TAG, "onDrawFrame call");
                // 清除OpenGL缓冲区
                GLES30.glClear(GLES10.GL_COLOR_BUFFER_BIT);
            }
        };
        // 设置渲染器
        mGLSurfaceView.setRenderer(renderer);
    }
}