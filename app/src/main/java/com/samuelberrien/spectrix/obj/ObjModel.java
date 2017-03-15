package com.samuelberrien.spectrix.obj;

import android.content.Context;
import android.opengl.GLES20;

import com.samuelberrien.spectrix.R;
import com.samuelberrien.spectrix.utils.ShaderLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Created by samuel on 05/01/17.
 */

public class ObjModel {

    private final FloatBuffer vertexBuffer;
    private final FloatBuffer normalsBuffer;
    private FloatBuffer colorBuffer;

    private final int mProgram;
    private int mPositionHandle;
    private int mNormalHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private int mLightPosHandle;
    private int mMVMatrixHandle;
    private int mDistanceCoefHandle;
    private int mLightCoefHandle;

    private float lightCoef;
    private float distanceCoef;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    private float[] coords;
    private float[] normal;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private float[] color;

    /**
     * @param context           the application context
     * @param resId             the res id of the obj file
     * @param red               the red color of the object
     * @param green             the green color of the object
     * @param blue              the blue color of the object
     * @param lightAugmentation the light augmentation of the object
     */
    public ObjModel(Context context, int resId, float red, float green, float blue, float lightAugmentation, float distanceCoef) {

        this.lightCoef = lightAugmentation;
        this.distanceCoef = distanceCoef;

        InputStream inputStream = context.getResources().openRawResource(resId);
        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader1 = new BufferedReader(inputreader);
        String line;

        ArrayList<Float> vertixsList = new ArrayList<>();
        ArrayList<Float> normalsList = new ArrayList<>();
        ArrayList<Integer> vertexDrawOrderList = new ArrayList<>();
        ArrayList<Integer> normalDrawOrderList = new ArrayList<>();

        try {
            while ((line = buffreader1.readLine()) != null) {
                if (line.startsWith("vn")) {
                    String[] tmp = line.split(" ");
                    normalsList.add(Float.parseFloat(tmp[1]));
                    normalsList.add(Float.parseFloat(tmp[2]));
                    normalsList.add(Float.parseFloat(tmp[3]));
                } else if (line.startsWith("v ")) {
                    String[] tmp = line.split(" ");
                    vertixsList.add(Float.parseFloat(tmp[1]));
                    vertixsList.add(Float.parseFloat(tmp[2]));
                    vertixsList.add(Float.parseFloat(tmp[3]));
                } else if (line.startsWith("f")) {
                    String[] tmp = line.split(" ");
                    vertexDrawOrderList.add(Integer.parseInt(tmp[1].split("/")[0]));
                    vertexDrawOrderList.add(Integer.parseInt(tmp[2].split("/")[0]));
                    vertexDrawOrderList.add(Integer.parseInt(tmp[3].split("/")[0]));

                    normalDrawOrderList.add(Integer.parseInt(tmp[1].split("/")[2]));
                    normalDrawOrderList.add(Integer.parseInt(tmp[2].split("/")[2]));
                    normalDrawOrderList.add(Integer.parseInt(tmp[3].split("/")[2]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.coords = new float[3 * vertexDrawOrderList.size()];
        for (int i = 0; i < vertexDrawOrderList.size(); i++) {
            this.coords[i * 3] = vertixsList.get((vertexDrawOrderList.get(i) - 1) * 3);
            this.coords[i * 3 + 1] = vertixsList.get((vertexDrawOrderList.get(i) - 1) * 3 + 1);
            this.coords[i * 3 + 2] = vertixsList.get((vertexDrawOrderList.get(i) - 1) * 3 + 2);
        }

        this.normal = new float[this.coords.length];
        for (int i = 0; i < normalDrawOrderList.size(); i++) {
            this.normal[i * 3] = normalsList.get((normalDrawOrderList.get(i) - 1) * 3);
            this.normal[i * 3 + 1] = normalsList.get((normalDrawOrderList.get(i) - 1) * 3 + 1);
            this.normal[i * 3 + 2] = normalsList.get((normalDrawOrderList.get(i) - 1) * 3 + 2);
        }

        this.color = new float[this.coords.length * 4 / 3];
        for (int i = 0; i < this.color.length; i += 4) {
            this.color[i] = red;
            this.color[i + 1] = green;
            this.color[i + 2] = blue;
            this.color[i + 3] = 1f;
        }

        this.vertexBuffer = ByteBuffer.allocateDirect(this.coords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        this.vertexBuffer.put(this.coords)
                .position(0);

        this.normalsBuffer = ByteBuffer.allocateDirect(this.normal.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        this.normalsBuffer.put(this.normal)
                .position(0);

        this.colorBuffer = ByteBuffer.allocateDirect(this.color.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        this.colorBuffer.put(this.color)
                .position(0);

        int vertexShader = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShaderLoader.openShader(context, R.raw.vertex_shader_diffuse));
        int fragmentShader = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderLoader.openShader(context, R.raw.fragment_shader_diffuse));

        this.mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(this.mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(this.mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(this.mProgram);
    }

    /**
     * @param colorBuffer a color buffer that will be used to draw the obj 3D model
     */
    public void setColor(FloatBuffer colorBuffer) {
        this.colorBuffer = colorBuffer;
    }

    /**
     * @return the vertex draw list length of the obj 3D model
     */
    public int getVertexDrawListLength() {
        return this.coords.length;
    }

    /**
     * @param mvpMatrix           The Model View Project matrix in which to draw this shape.
     * @param mvMatrix            The Model View matrix
     * @param mLightPosInEyeSpace The light position in the eye space
     */
    public void draw(float[] mvpMatrix, float[] mvMatrix, float[] mLightPosInEyeSpace) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        this.vertexBuffer.position(0);
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
        ShaderLoader.checkGlError("glGetUniformLocation");

        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVMatrix");

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        ShaderLoader.checkGlError("glGetAttribLocation");

        // get handle to fragment shader's vColor member
        this.colorBuffer.position(0);
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "a_Color");
        ShaderLoader.checkGlError("glGetAttribLocation");

        mLightPosHandle = GLES20.glGetUniformLocation(mProgram, "u_LightPos");
        ShaderLoader.checkGlError("glGetUniformLocation");

        mDistanceCoefHandle = GLES20.glGetUniformLocation(mProgram, "u_distance_coef");
        ShaderLoader.checkGlError("glGetUniformLocation");

        mLightCoefHandle = GLES20.glGetUniformLocation(mProgram, "u_light_coef");
        ShaderLoader.checkGlError("glGetUniformLocation");

        this.normalsBuffer.position(0);
        mNormalHandle = GLES20.glGetAttribLocation(mProgram, "a_Normal");
        ShaderLoader.checkGlError("glGetAttribLocation");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, colorBuffer);

        GLES20.glEnableVertexAttribArray(mNormalHandle);
        GLES20.glVertexAttribPointer(mNormalHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, normalsBuffer);


        // get handle to shape's transformation matrix
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvMatrix, 0);

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        ShaderLoader.checkGlError("glUniformMatrix4fv");

        GLES20.glUniform3fv(mLightPosHandle, 1, mLightPosInEyeSpace, 0);

        GLES20.glUniform1f(mDistanceCoefHandle, this.distanceCoef);
        ShaderLoader.checkGlError("glUniform1f");

        GLES20.glUniform1f(mLightCoefHandle, this.lightCoef);
        ShaderLoader.checkGlError("glUniform1f");

        // Draw the polygon
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, this.coords.length / 3);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
