package com.halenteck.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import javax.swing.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL.setCapabilities;
import static org.lwjgl.opengl.GL20.*;

public class OpenGLComponent extends AWTGLCanvas {
    private int width;
    private int height;
    private float aspect;

    private int fps = 0;

    private Vector3f cameraPosition = new Vector3f(0, 0, 0);
    private Vector3f directionVector = new Vector3f(0, 0, -1);

    private float yaw = -180;
    private float pitch = 0;

    private int programHandle;
    private int vPMatrixHandle;
    private int positionHandle;
    private int colorHandle;
    private int textureCoordinateHandle;

    private final Matrix4f projectionMatrix = new Matrix4f();

    public OpenGLComponent() {
        super(new GLData());
    }

    @Override
    public void initGL() {
        createCapabilities();
        glClearColor(0.3f, 0.4f, 0.5f, 1.0f);
        width = getWidth();
        height = getHeight();
        aspect = (float) width / height;
        glViewport(0, 0, width, height);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        String vertexShaderCode = "uniform mat4 u_MVPMatrix;" +
                "attribute vec4 a_Position;" +
                "attribute vec4 a_Color;" +
                "attribute vec2 a_TexCoordinate;" +
                "varying vec4 v_Color;" +
                "varying vec2 v_TexCoordinate;" +
                "void main()" +
                "{" +
                "v_Color = a_Color;" +
                "v_TexCoordinate = a_TexCoordinate;" +
                "gl_Position = u_MVPMatrix * a_Position;" +
                "}";
        int vertexShader = OpenGLUtils.loadShader(GL_VERTEX_SHADER, vertexShaderCode);
        String fragmentShaderCode = "precision mediump float;" +
                "uniform sampler2D u_Texture;" +
                "varying vec4 v_Color;" +
                "varying vec2 v_TexCoordinate;" +
                "void main() {" +
                "vec4 val = v_Color * texture2D(u_Texture, v_TexCoordinate);" +
                "if(val.a < 0.25){ discard; }" +
                "gl_FragColor = val;" +
                "}";
        int fragShader = OpenGLUtils.loadShader(GL_FRAGMENT_SHADER, fragmentShaderCode);
        programHandle = OpenGLUtils.createAndLinkProgram(vertexShader, fragShader, new String[]{"a_Position", "a_Color", "a_TexCoordinate"});

        glUseProgram(programHandle);

        vPMatrixHandle = glGetUniformLocation(programHandle, "u_MVPMatrix");
        positionHandle = glGetAttribLocation(programHandle, "a_Position");
        colorHandle = glGetAttribLocation(programHandle, "a_Color");
        textureCoordinateHandle = glGetAttribLocation(programHandle, "a_TexCoordinate");

        projectionMatrix.setPerspective((float) Math.toRadians(45), aspect, 0.1f, 10.0f);
    }

    @Override
    public void paintGL() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        Matrix4f viewMatrix = new Matrix4f().setLookAt(cameraPosition,
                new Vector3f(directionVector).add(cameraPosition), new Vector3f(0, 1, 0));
        Matrix4f viewProjectionMatrix = new Matrix4f(projectionMatrix).mul(viewMatrix);
        FloatBuffer vp = BufferUtils.createFloatBuffer(16);
        viewProjectionMatrix.get(vp);
        glUniformMatrix4fv(vPMatrixHandle, false, vp);

        float[] triangleCoords = {
                0.0f, 0.622008459f, -2.0f,
                -0.5f, -0.311004243f, -2.0f,
                0.5f, -0.311004243f, -2.0f
        };

        float[] color = {
                1.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f
        };

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(triangleCoords.length);
        vertexBuffer.put(triangleCoords).position(0);
        glEnableVertexAttribArray(positionHandle);
        glVertexAttribPointer(positionHandle, 3, GL_FLOAT, false, 0, vertexBuffer);

        FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(color.length);
        colorBuffer.put(color).position(0);
        glEnableVertexAttribArray(colorHandle);
        glVertexAttribPointer(colorHandle, 4, GL_FLOAT, false, 0, colorBuffer);

        glBindTexture(GL_TEXTURE_2D, OpenGLUtils.loadTexture("/whiteSquare.png"));

        glDrawArrays(GL_TRIANGLES, 0, 3);

        float[] squareCoords = {
                -1, -0.5f, -1,
                1, -0.5f, -1,
                1, -0.5f, 1,
                -1, -0.5f, 1
        };

        float[] squareColor = {
                1, 1, 1, 1,
                1, 1, 1, 1,
                1, 1, 1, 1,
                1, 1, 1, 1
        };

        float[] textureCoords = {
                0, 0,
                0, 1,
                1, 1,
                1, 0
        };

        int[] indices = {
                0, 1, 2,
                0, 2, 3
        };

        FloatBuffer squareVertexBuffer = BufferUtils.createFloatBuffer(squareCoords.length);
        squareVertexBuffer.put(squareCoords).position(0);
        glEnableVertexAttribArray(positionHandle);
        glVertexAttribPointer(positionHandle, 3, GL_FLOAT, false, 0, squareVertexBuffer);

        FloatBuffer squareColorBuffer = BufferUtils.createFloatBuffer(squareColor.length);
        squareColorBuffer.put(squareColor).position(0);
        glEnableVertexAttribArray(colorHandle);
        glVertexAttribPointer(colorHandle, 4, GL_FLOAT, false, 0, squareColorBuffer);

        glBindTexture(GL_TEXTURE_2D, OpenGLUtils.loadTexture("/test/clan.jpeg"));

        FloatBuffer textureCoordinateBuffer = BufferUtils.createFloatBuffer(textureCoords.length);
        textureCoordinateBuffer.put(textureCoords).position(0);
        glEnableVertexAttribArray(textureCoordinateHandle);
        glVertexAttribPointer(textureCoordinateHandle, 2, GL_FLOAT, false, 0, textureCoordinateBuffer);

        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices).position(0);

        glDrawElements(GL_TRIANGLES, indexBuffer);

        float[] squareCoords2 = {
                -1, -1, 1,
                1, -1, 1,
                1, 1, 1,
                -1, 1, 1
        };

        float[] squareColor2 = {
                1, 1, 1, 1,
                1, 1, 1, 1,
                1, 1, 1, 1,
                1, 1, 1, 1
        };

        float[] textureCoords2 = {
                0, 0,
                0, 1,
                1, 1,
                1, 0
        };

        int[] indices2 = {
                0, 1, 2,
                0, 2, 3
        };

        FloatBuffer squareVertexBuffer2 = BufferUtils.createFloatBuffer(squareCoords2.length);
        squareVertexBuffer2.put(squareCoords2).position(0);
        glEnableVertexAttribArray(positionHandle);
        glVertexAttribPointer(positionHandle, 3, GL_FLOAT, false, 0, squareVertexBuffer2);

        FloatBuffer squareColorBuffer2 = BufferUtils.createFloatBuffer(squareColor2.length);
        squareColorBuffer2.put(squareColor2).position(0);
        glEnableVertexAttribArray(colorHandle);
        glVertexAttribPointer(colorHandle, 4, GL_FLOAT, false, 0, squareColorBuffer2);

        glBindTexture(GL_TEXTURE_2D, OpenGLUtils.loadTexture("/test/adsiz.png"));

        FloatBuffer textureCoordinateBuffer2 = BufferUtils.createFloatBuffer(textureCoords2.length);
        textureCoordinateBuffer2.put(textureCoords2).position(0);
        glEnableVertexAttribArray(textureCoordinateHandle);
        glVertexAttribPointer(textureCoordinateHandle, 2, GL_FLOAT, false, 0, textureCoordinateBuffer2);

        IntBuffer indexBuffer2 = BufferUtils.createIntBuffer(indices2.length);
        indexBuffer2.put(indices2).position(0);

        glDrawElements(GL_TRIANGLES, indexBuffer2);

        swapBuffers();

        fps++;
    }


    public void startRender() {
        while (true) {
            if (!isValid()) {
                setCapabilities(null);
                return;
            }
            render();
        }
    }

    public void moveForward(float distance) {
        cameraPosition.add(new Vector3f(directionVector).mul(distance));
    }

    public void moveBackward(float distance) {
        cameraPosition.sub(new Vector3f(directionVector).mul(distance));
    }

    public void moveRight(float distance) {
        Vector3f right = new Vector3f(directionVector).cross(new Vector3f(0, 1, 0));
        cameraPosition.add(right.mul(distance));
    }

    public void moveLeft(float distance) {
        Vector3f right = new Vector3f(directionVector).cross(new Vector3f(0, 1, 0));
        cameraPosition.sub(right.mul(distance));
    }

    public void moveUp(float distance) {
        cameraPosition.add(new Vector3f(0, 1, 0).mul(distance));
    }

    public void moveDown(float distance) {
        cameraPosition.sub(new Vector3f(0, 1, 0).mul(distance));
    }


    public void rotate(float dYaw, float dPitch) {
        yaw += dYaw;
        pitch += dPitch;
        if (pitch > 90) pitch = 90;
        if (pitch < -90) pitch = -90;
        float directionX = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        float directionY = (float) Math.sin(Math.toRadians(pitch));
        float directionZ = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        directionVector = new Vector3f(directionX, directionY, directionZ);
    }

    public void setFpsText(JLabel fpsText) {
        new Timer(1000, e -> {
            fpsText.setText("FPS: " + fps);
            fps = 0;
        }).start();
    }

}
