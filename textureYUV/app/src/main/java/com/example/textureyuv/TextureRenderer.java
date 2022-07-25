package com.example.textureyuv;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TextureRenderer implements GLSurfaceView.Renderer {

    private final String TAG = "TextureRenderer";
    private final int VERTEX_LOCATION_POSITION = 0;
    private final int TEXTURE_LOCATION_POSITION = 1;
    private Context mContext;
    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;
    private int textureUniformY, textureUniformU, textureUniformV;
    private int[] texId;
    int yuvWidth = 0, yuvHeight = 0;
    private ByteBuffer BufY, BufU, BufV;

    // 顶点坐标
    private float[] vertexPoints = new float[]{
            -1f, -1f, 0.0f, // bottom left
            1f, -1f, 0.0f, // bottom right
            -1f, 1f, 0.0f, // top left
            1f, 1f, 0.0f,  // top right
    };
    // 纹理坐标
    private float[] texturePoints = new float[] {
            0f, 1f, 0.0f, // bottom left
            1f, 1f, 0.0f, // bottom right
            0f, 0f, 0.0f, // top left
            1f, 0f, 0.0f,  // top right
    };

    public TextureRenderer(Context context){
        mContext = context;
        vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.length * Float.SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(vertexPoints);
        vertexBuffer.position(0);

        textureBuffer = ByteBuffer.allocateDirect(texturePoints.length * Float.SIZE)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        textureBuffer.put(texturePoints);
        textureBuffer.position(0);
    }

    public void loadYuvImage() {
        InputStream is = null;
        try {
            is = mContext.getAssets().open("YUV_Image_840x1074.NV21");
        } catch (IOException e) {
            e.printStackTrace();
        }
        int fileLen = 0;
        try {
            fileLen = is.available();
            Log.i(TAG, "fileLen is " + fileLen);
            int w = 840, h = 1074;
            byte[] bufferY = new byte[w * h];
            byte[] bufferU = new byte[w * h / 4];
            byte[] bufferV = new byte[w * h / 4];
            is.read(bufferY);
            is.read(bufferU);
            is.read(bufferV);
            setYuvData(w, h, bufferY, bufferU, bufferV);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setYuvData(int w, int h, byte[] y, byte[] u, byte[] v) {
        yuvWidth = w;
        yuvHeight = h;
        BufY = ByteBuffer.wrap(y);
        BufU = ByteBuffer.wrap(u);
        BufV = ByteBuffer.wrap(v);
    }

    public void initTexture() {
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        int vertexShaderId = ShaderUtil.compileVertexShader(ResReadUtil.readResource(mContext, R.raw.vertex_shader));
        int fragmentShaderId = ShaderUtil.compileFragmentShader(ResReadUtil.readResource(mContext, R.raw.fragment_shader));
        int programId = ShaderUtil.linkProgram(vertexShaderId, fragmentShaderId);
        Log.d(TAG, "programId is " + programId);
        GLES30.glUseProgram(programId);

        // 获取顶点坐标字段
        // 获取纹理坐标字段
        GLES30.glBindAttribLocation(programId, VERTEX_LOCATION_POSITION, "vPosition");
        GLES30.glBindAttribLocation(programId, TEXTURE_LOCATION_POSITION, "vTexcoord");

        GLES30.glVertexAttribPointer(VERTEX_LOCATION_POSITION, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);
        GLES30.glEnableVertexAttribArray(VERTEX_LOCATION_POSITION);

        GLES30.glVertexAttribPointer(TEXTURE_LOCATION_POSITION, 3, GLES30.GL_FLOAT, false, 0, textureBuffer);
        GLES30.glEnableVertexAttribArray(TEXTURE_LOCATION_POSITION);

        GLES30.glDeleteShader(vertexShaderId);
        GLES30.glDeleteShader(fragmentShaderId);

        // 获取YUV字段
        textureUniformY = GLES30.glGetUniformLocation(programId, "tex_y");
        textureUniformU = GLES30.glGetUniformLocation(programId, "tex_u");
        textureUniformV = GLES30.glGetUniformLocation(programId, "tex_v");

        // 创建3个纹理
        texId = new int[3];
        GLES30.glGenTextures(3, texId, 0);
        // 绑定纹理
        for (int id : texId) {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, id);
            // 超出纹理范围设置，环绕，s=x, t=y,重复
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
            // 纹理像素映射到坐标点，过滤，放大/缩小：线性
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        }
    }

    public void drawTexture() {
        // 激活纹理0，绑定数据y
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId[0]);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_LUMINANCE,
                yuvWidth, yuvHeight, 0, GLES30.GL_LUMINANCE, GLES30.GL_UNSIGNED_BYTE, BufY);

        // 激活纹理1，绑定数据u
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId[1]);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_LUMINANCE,
                yuvWidth / 2, yuvHeight / 2, 0, GLES30.GL_LUMINANCE, GLES30.GL_UNSIGNED_BYTE, BufU);

        // 激活纹理2，绑定数据u
        GLES30.glActiveTexture(GLES30.GL_TEXTURE2);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId[2]);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_LUMINANCE,
                yuvWidth / 2, yuvHeight / 2, 0, GLES30.GL_LUMINANCE, GLES30.GL_UNSIGNED_BYTE, BufV);

        // 给fragment_shader里面yuv变量设置值   0 1 2 标识纹理x
        GLES30.glUniform1i(textureUniformY, 0);
        GLES30.glUniform1i(textureUniformU, 1);
        GLES30.glUniform1i(textureUniformV, 2);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);




    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig egl) {
        loadYuvImage();
        initTexture();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glLineWidth(5);
        //GLES30.glDrawArrays(GLES30.GL_LINE_LOOP, 0, 3);
        drawTexture();
    }
}
