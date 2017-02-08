package com.samuelberrien.spectrix.obj;

import android.content.Context;
import android.opengl.GLES20;

import com.samuelberrien.spectrix.R;
import com.samuelberrien.spectrix.ShaderLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by samuel on 17/01/17.
 */

public class ObjModelMtl {

    private HashMap<String,float[]> mtlDiffColor = new HashMap<>();

    private ArrayList<FloatBuffer> allVertexBuffer = new ArrayList<>();
    private ArrayList<FloatBuffer> allNormalsBuffer = new ArrayList<>();
    private ArrayList<FloatBuffer> allDiffColorBuffer = new ArrayList<>();
    private final int mProgram;
    private int mPositionHandle;
    private int mNormalHandle;
    private int mDiffColorHandle;
    private int mMVPMatrixHandle;
    private int mLightPosHandle;
    private int mMVMatrixHandle;
    private int mDistanceCoefHandle;
    private float distanceCoef;
    private int mLightCoefHandle;
    private float lightCoef;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    private ArrayList<float[]> allCoords = new ArrayList<>();
    private ArrayList<float[]> allNormals = new ArrayList<>();
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    /**
     *
     * @param context
     * @param objResId
     * @param mtlResId
     * @param lightAugmentation
     */
    public ObjModelMtl(Context context, int objResId, int mtlResId, float lightAugmentation, float distanceCoef){

        this.parseMtl(context, mtlResId);
        this.parseObj(context, objResId);

        this.lightCoef = lightAugmentation;
        this.distanceCoef = distanceCoef;

        int vertexShader = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShaderLoader.openShader(context, R.raw.vertex_shader_diffuse));
        int fragmentShader = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderLoader.openShader(context, R.raw.fragment_shader_diffuse));

        this.mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(this.mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(this.mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(this.mProgram);
    }

    private void parseMtl(Context context, int resId){
        InputStream inputStream = context.getResources().openRawResource(resId);
        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader1 = new BufferedReader(inputreader);
        String line;
        try {
            String currentMtl = "";
            while ((line = buffreader1.readLine()) != null) {
                if(line.startsWith("newmtl")){
                    currentMtl = line.split(" ")[1];
                }else if(line.startsWith("Kd")){
                    String[] tmp = line.split(" ");
                    this.mtlDiffColor.put(currentMtl, new float[]{Float.parseFloat(tmp[1]), Float.parseFloat(tmp[2]), Float.parseFloat(tmp[3])});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseObj(Context context, int resId){
        InputStream inputStream = context.getResources().openRawResource(resId);
        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;

        ArrayList<Float> currVertixsList = new ArrayList<>();
        ArrayList<Float> currNormalsList = new ArrayList<>();
        ArrayList<Integer> currVertexDrawOrderList = new ArrayList<>();
        ArrayList<Integer> normalDrawOrderList = new ArrayList<>();
        ArrayList<ArrayList<Integer>> allVertexDrawOrderList = new ArrayList<>();
        ArrayList<ArrayList<Integer>> allNormalDrawOrderList = new ArrayList<>();
        ArrayList<String> mtlToUse = new ArrayList<>();

        int idMtl = 0;

        try {
            while (( line = buffreader.readLine()) != null) {
                if(line.startsWith("usemtl")) {
                    mtlToUse.add(line.split(" ")[1]);
                    if(idMtl != 0){
                        allVertexDrawOrderList.add(currVertexDrawOrderList);
                        allNormalDrawOrderList.add(normalDrawOrderList);
                    }
                    currVertexDrawOrderList = new ArrayList<>();
                    normalDrawOrderList = new ArrayList<>();
                    idMtl++;
                }else if(line.startsWith("vn")){
                    String[] tmp = line.split(" ");
                    currNormalsList.add(Float.parseFloat(tmp[1]));
                    currNormalsList.add(Float.parseFloat(tmp[2]));
                    currNormalsList.add(Float.parseFloat(tmp[3]));
                }else if(line.startsWith("v ")){
                    String[] tmp = line.split(" ");
                    currVertixsList.add(Float.parseFloat(tmp[1]));
                    currVertixsList.add(Float.parseFloat(tmp[2]));
                    currVertixsList.add(Float.parseFloat(tmp[3]));
                }else if(line.startsWith("f")){
                    String[] tmp = line.split(" ");
                    currVertexDrawOrderList.add(Integer.parseInt(tmp[1].split("/")[0]));
                    currVertexDrawOrderList.add(Integer.parseInt(tmp[2].split("/")[0]));
                    currVertexDrawOrderList.add(Integer.parseInt(tmp[3].split("/")[0]));

                    normalDrawOrderList.add(Integer.parseInt(tmp[1].split("/")[2]));
                    normalDrawOrderList.add(Integer.parseInt(tmp[2].split("/")[2]));
                    normalDrawOrderList.add(Integer.parseInt(tmp[3].split("/")[2]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        allVertexDrawOrderList.add(currVertexDrawOrderList);
        allNormalDrawOrderList.add(normalDrawOrderList);

        for(int i=0; i<allVertexDrawOrderList.size(); i++) {
            float[] coords = new float[3 * allVertexDrawOrderList.get(i).size()];
            for (int j = 0; j < allVertexDrawOrderList.get(i).size(); j++) {
                coords[j * 3] = currVertixsList.get((allVertexDrawOrderList.get(i).get(j) - 1) * 3);
                coords[j * 3 + 1] = currVertixsList.get((allVertexDrawOrderList.get(i).get(j) - 1) * 3 + 1);
                coords[j * 3 + 2] = currVertixsList.get((allVertexDrawOrderList.get(i).get(j) - 1) * 3 + 2);
            }
            this.allCoords.add(coords);

            float[] normal = new float[coords.length];
            for (int j = 0; j < allNormalDrawOrderList.get(i).size(); j++) {
                normal[j * 3] = currNormalsList.get((allNormalDrawOrderList.get(i).get(j) - 1) * 3);
                normal[j * 3 + 1] = currNormalsList.get((allNormalDrawOrderList.get(i).get(j) - 1) * 3 + 1);
                normal[j * 3 + 2] = currNormalsList.get((allNormalDrawOrderList.get(i).get(j) - 1) * 3 + 2);
            }
            this.allNormals.add(normal);

            float red = this.mtlDiffColor.get(mtlToUse.get(i))[0];
            float green = this.mtlDiffColor.get(mtlToUse.get(i))[1];
            float blue = this.mtlDiffColor.get(mtlToUse.get(i))[2];

            float[] color = new float[coords.length * 4 / 3];
            for (int j = 0; j < color.length; j += 4) {
                color[j] = red;
                color[j + 1] = green;
                color[j + 2] = blue;
                color[j + 3] = 1f;
            }

            FloatBuffer tmpV = ByteBuffer.allocateDirect(coords.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            tmpV.put(coords)
                    .position(0);
            this.allVertexBuffer.add(tmpV);

            FloatBuffer tmpN = ByteBuffer.allocateDirect(normal.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            tmpN.put(normal)
                    .position(0);
            this.allNormalsBuffer.add(tmpN);

            FloatBuffer tmpC = ByteBuffer.allocateDirect(color.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            tmpC.put(color)
                    .position(0);
            this.allDiffColorBuffer.add(tmpC);
        }
    }

    /**
     *
     * @param rand
     * @return
     */
    public ArrayList<FloatBuffer> makeColor(Random rand){
        ArrayList<FloatBuffer> result = new ArrayList<>();
        for(int i=0; i < this.allVertexBuffer.size(); i++){
            float red = rand.nextFloat();
            float green = rand.nextFloat();
            float blue = rand.nextFloat();
            float[] color = new float[this.allCoords.get(i).length * 4 / 3];
            for (int j = 0; j < color.length; j += 4) {
                color[j] = red;
                color[j + 1] = green;
                color[j + 2] = blue;
                color[j + 3] = 1f;
            }
            FloatBuffer tmpC = ByteBuffer.allocateDirect(color.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            tmpC.put(color)
                    .position(0);
            result.add(tmpC);
        }
        return result;
    }

    public void setColors(ArrayList<FloatBuffer> mColors){
        this.allDiffColorBuffer = mColors;
    }

    /**
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw this shape.
     * @param mvMatrix
     * @param mLightPosInEyeSpace
     */
    public void draw(float[] mvpMatrix, float[] mvMatrix, float[] mLightPosInEyeSpace) {
        for(int i=0 ; i<this.allVertexBuffer.size(); i++) {
            GLES20.glUseProgram(mProgram);

            this.allVertexBuffer.get(i).position(0);
            mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
            ShaderLoader.checkGlError("glGetUniformLocation");

            mMVMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVMatrix");

            // get handle to vertex shader's vPosition member
            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
            ShaderLoader.checkGlError("glGetAttribLocation");

            // get handle to fragment shader's vColor member
            this.allDiffColorBuffer.get(i).position(0);
            mDiffColorHandle = GLES20.glGetAttribLocation(mProgram, "a_Color");
            ShaderLoader.checkGlError("glGetAttribLocation");

            mLightPosHandle = GLES20.glGetUniformLocation(mProgram, "u_LightPos");
            ShaderLoader.checkGlError("glGetUniformLocation");

            this.allNormalsBuffer.get(i).position(0);
            mNormalHandle = GLES20.glGetAttribLocation(mProgram, "a_Normal");
            ShaderLoader.checkGlError("glGetAttribLocation");

            mDistanceCoefHandle = GLES20.glGetUniformLocation(mProgram, "u_distance_coef");
            ShaderLoader.checkGlError("glGetUniformLocation");

            mLightCoefHandle = GLES20.glGetUniformLocation(mProgram, "u_light_coef");
            ShaderLoader.checkGlError("glGetUniformLocation");

            // Enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(mPositionHandle);
            // Prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, this.allVertexBuffer.get(i));

            GLES20.glEnableVertexAttribArray(mDiffColorHandle);
            GLES20.glVertexAttribPointer(mDiffColorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, this.allDiffColorBuffer.get(i));

            GLES20.glEnableVertexAttribArray(mNormalHandle);
            GLES20.glVertexAttribPointer(mNormalHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, this.allNormalsBuffer.get(i));

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
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, this.allCoords.get(i).length / 3);

            // Disable vertex array
            GLES20.glDisableVertexAttribArray(mPositionHandle);
        }
    }
}
