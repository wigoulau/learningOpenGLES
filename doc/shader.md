# shader绘制基本图形

## 实践
### 参考[HelloWorld](HelloWorld.md)，创建工程
### 创建ShaderUtil类，定义编译和链接着色器的接口
### 创建ShaderRenderer类，渲染着色器
在类中首先定义顶点着色器和片段着色器

* 顶点着色器
输入：顶点位置vPosition，后续gl_Position可以经过运算变换位置
输出：顶点位置gl_Position和颜色vColor(红色)
```
#version 300 es
layout (location = 0) in vec4 vPosition;
out vec4 vColor;
void main() { 
     gl_Position  = vPosition;
     gl_PointSize = 10.0;
     vColor = vec4(1.0,0.0,0.0,1.0);
}
```
* 片段着色器
输入：颜色vColor
输出：变换后的颜色fragColor，这里直接复制，没有变换
```
#version 300 es
precision mediump float;
in vec4 vColor;
out vec4 fragColor;
void main() { 
     fragColor = vColor;
}
```
具体实现代码见shaderDemo

## 着色器应用一般流程
* 定义顶点数组，一般为float数组，包含一个多组x、y、z，数据额，和一个多组r、g、b数据。或者合并到一个数组中，每组包含x、y、z、r、g、b数据
* 编译顶点和片段着色器
     * 编写顶点着色器程序，如
     ```
     #version 300 es
     layout (location = 0) in vec4 aPosition;
     layout (location = 1) in vec3 aColor;
     out vec4 vColor;
     void main() { 
          gl_Position  = aPosition;
          gl_PointSize = 10.0;
          vColor = vec4(aColor.x, aColor.y, aColor.z, 1.0);
     }
     ```
     * 编写片段着色器程序，如
     ```
     #version 300 es
     precision mediump float;
     in vec4 vColor;
     out vec4 fragColor;
     void main() { 
          fragColor = vColor;
     }
     ```
     * 编译：
     主要流程为，创建ShaderID -- 指定shader程序 -- 编译Shader -- 检查错误
     ```java
     GLES30.glCreateShader
     GLES30.glShaderSource
     GLES30.glCompileShader
     // 检查是否编译错误
     GLES30.glGetShaderiv
     // 根据前面的返回值是否为0，判断编译是否错误
     // 如果编译没有错误，就可以使用前面创建的shaderId
     // 如果编译错误，则获取错误信息
     GLES30.glGetShaderInfoLog
     // 出错后，释放Shader资源
     GLES30.glDeleteShader
     ```
     * 分别编译顶点着色器和片段着色器程序，对应的类型分别为GLES30.GL_VERTEX_SHADER和GLES30.GL_FRAGMENT_SHADER

* 链接程序片段
主要流程：创建ProgramID -- 添加前面创建顶点着色器（根据ShaderID） -- 添加前面创建的片段着色器（根据ShaderID） -- 链接program -- 检查错误
     ```java
     GLES30.glCreateProgram
     GLES30.glAttachShader    // Vertex
     GLES30.glAttachShader    // Fragment
     GLES30.glLinkProgram
     // 检查
     GLES30.glGetProgramiv
     // 根据前面的返回值是否为0，判断编译是否错误
     // 如果编译没有错误，就可以使用前面创建的programId
     // 如果编译错误，则获取错误信息
     GLES30.glGetProgramInfoLog
     // 出错后，释放Program资源
     GLES30.glDeleteProgram
     ```
* 应用程序
     ```java
     GLES30.glUseProgram
     // 指定渲染时顶点属性数组的数据格式和位置（数据指针）
     GLES30.glVertexAttribPointer
     GLES30.glEnableVertexAttribArray
     // 如果顶点着色器程序中定义了位置position和颜色color 2个输入变量，那么可以调用以上函数2次通过着色器程序中location指定的位置，设置为index对应的参数
     ```
     以上的步骤可以在初始化或onSurfaceCreated中调用

* 设置视图窗口
在渲染类的onSurfaceChanged中设置
     ```java
     GLES30.glViewport
     ```
* 画图形渲染
在渲染类的onDrawFrame中调用
     ```java
     // 清除缓存
     GLES30.glClear
     // 绘制图形
     GLES30.glDrawArray
     ```


