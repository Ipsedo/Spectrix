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
 * Created by samuel on 21/02/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class ObjModelMtlTestSpecular {
    private HashMap<String,float[]> mtlAmbColor = new HashMap<>();
    private HashMap<String,float[]> mtlDiffColor = new HashMap<>();
    private HashMap<String,float[]> mtlSpecColor = new HashMap<>();
    private HashMap<String,Float> mtlSpecShininess = new HashMap<>();

    private ArrayList<FloatBuffer> allVertexBuffer = new ArrayList<>();
    private ArrayList<FloatBuffer> allNormalsBuffer = new ArrayList<>();
    private ArrayList<FloatBuffer> allAmbColorBuffer = new ArrayList<>();
    private ArrayList<FloatBuffer> allDiffColorBuffer = new ArrayList<>();
    private ArrayList<FloatBuffer> allSpecColorBuffer = new ArrayList<>();
    private ArrayList<Float> allSpecShininess = new ArrayList<>();
    private final int mProgram;
    private int mPositionHandle;
    private int mNormalHandle;
    private int mAmbColorHandle;
    private int mDiffColorHandle;
    private int mSpecColorHandle;
    private int mSpecShininessHandle;
    private int mCameraPosHandle;
    private int mMVPMatrixHandle;
    private int mLightPosHandle;
    private int mMVMatrixHandle;
    private int mDistanceCoefHandle;
    private float distanceCoef;
    private int mLightCoefHandle;
    private float lightCoef;

    // number of coordinates per vertex in this array
    private final int COORDS_PER_VERTEX = 3;
    private ArrayList<float[]> allCoords = new ArrayList<>();
    private ArrayList<float[]> allNormals = new ArrayList<>();
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    /**
     *
     * @param context The application context
     * @param objResId The res id of the obj 3D model file
     * @param mtlResId The res id of the mtl model file
     * @param lightAugmentation The light augmentation
     */
    public ObjModelMtlTestSpecular(Context context, int objResId, int mtlResId, float lightAugmentation, float distanceCoef){

        this.parseMtl(context, mtlResId);
        this.parseObj(context, objResId);

        this.lightCoef = lightAugmentation;
        this.distanceCoef = distanceCoef;

        int vertexShader = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShaderLoader.openShader(context, R.raw.vertex_shader_specular));
        int fragmentShader = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderLoader.openShader(context, R.raw.fragment_shader_specular));

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
                }else if(line.startsWith("Ka")){
                    String[] tmp = line.split(" ");
                    this.mtlAmbColor.put(currentMtl, new float[]{Float.parseFloat(tmp[1]), Float.parseFloat(tmp[2]), Float.parseFloat(tmp[3])});
                }else if(line.startsWith("Kd")){
                    String[] tmp = line.split(" ");
                    this.mtlDiffColor.put(currentMtl, new float[]{Float.parseFloat(tmp[1]), Float.parseFloat(tmp[2]), Float.parseFloat(tmp[3])});
                }else if(line.startsWith("Ks")){
                    String[] tmp = line.split(" ");
                    this.mtlSpecColor.put(currentMtl, new float[]{Float.parseFloat(tmp[1]), Float.parseFloat(tmp[2]), Float.parseFloat(tmp[3])});
                }else if(line.startsWith("Ns")){
                    this.mtlSpecShininess.put(currentMtl, Float.parseFloat(line.split(" ")[1]));
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

            float[] normal = new float[3 * allVertexDrawOrderList.get(i).size()];
            for (int j = 0; j < allNormalDrawOrderList.get(i).size(); j++) {
                normal[j * 3] = currNormalsList.get((allNormalDrawOrderList.get(i).get(j) - 1) * 3);
                normal[j * 3 + 1] = currNormalsList.get((allNormalDrawOrderList.get(i).get(j) - 1) * 3 + 1);
                normal[j * 3 + 2] = currNormalsList.get((allNormalDrawOrderList.get(i).get(j) - 1) * 3 + 2);
            }
            this.allNormals.add(normal);

            float ambRed = this.mtlAmbColor.get(mtlToUse.get(i))[0];
            float ambGreen = this.mtlAmbColor.get(mtlToUse.get(i))[1];
            float ambBlue = this.mtlAmbColor.get(mtlToUse.get(i))[2];

            float diffRed = this.mtlDiffColor.get(mtlToUse.get(i))[0];
            float diffGreen = this.mtlDiffColor.get(mtlToUse.get(i))[1];
            float diffBlue = this.mtlDiffColor.get(mtlToUse.get(i))[2];

            float specRed = this.mtlSpecColor.get(mtlToUse.get(i))[0];
            float specGreen = this.mtlSpecColor.get(mtlToUse.get(i))[1];
            float specBlue = this.mtlSpecColor.get(mtlToUse.get(i))[2];

            float[] ambColor = new float[coords.length * 4 / 3];
            float[] diffColor = new float[coords.length * 4 / 3];
            float[] specColor = new float[coords.length * 4 / 3];
            for (int j = 0; j < diffColor.length; j += 4) {
                ambColor[j] = ambRed;
                ambColor[j + 1] = ambGreen;
                ambColor[j + 2] = ambBlue;
                ambColor[j + 3] = 1f;

                diffColor[j] = diffRed;
                diffColor[j + 1] = diffGreen;
                diffColor[j + 2] = diffBlue;
                diffColor[j + 3] = 1f;

                specColor[j] = specRed;
                specColor[j + 1] = specGreen;
                specColor[j + 2] = specBlue;
                specColor[j + 3] = 1f;
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

            FloatBuffer tmpAC = ByteBuffer.allocateDirect(ambColor.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            tmpAC.put(ambColor)
                    .position(0);
            this.allAmbColorBuffer.add(tmpAC);

            FloatBuffer tmpDC = ByteBuffer.allocateDirect(diffColor.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            tmpDC.put(diffColor)
                    .position(0);
            this.allDiffColorBuffer.add(tmpDC);

            FloatBuffer tmpSC = ByteBuffer.allocateDirect(specColor.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            tmpSC.put(specColor)
                    .position(0);
            this.allSpecColorBuffer.add(tmpSC);

            this.allSpecShininess.add(this.mtlSpecShininess.get(mtlToUse.get(i)));
        }
    }

    /**
     *
     * @param rand A random object used for random colors generating
     * @return A FloatBuffer ArrayList containing all the colors per material
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

    /**
     *
     * @param red Red float value [0;1]
     * @param green Green float value [0;1]
     * @param blue Blue float value [0;1]
     * @return A FloatBuffer ArrayList containing all the colors per material
     */
    public ArrayList<FloatBuffer> makeColor(float red, float green, float blue){
        ArrayList<FloatBuffer> result = new ArrayList<>();
        for(int i=0; i < this.allVertexBuffer.size(); i++){
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

    /**
     *
     * @param mAmbColors The FloatBuffer ArrayList of all material ambient color
     * @param mDiffColors The diffuse FloatBuffer ArrayList of all material color
     * @param mSpecColors The FloatBuffer ArrayList of all material specular color
     */
    public void setColors(ArrayList<FloatBuffer> mAmbColors, ArrayList<FloatBuffer> mDiffColors, ArrayList<FloatBuffer> mSpecColors){
        this.allAmbColorBuffer = mAmbColors;
        this.allDiffColorBuffer = mDiffColors;
        this.allSpecColorBuffer = mSpecColors;
    }

    /**
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw this shape.
     * @param mvMatrix - The Model View matrix
     * @param mLightPosInEyeSpace - The position of light in eye space
     */
    public void draw(float[] mvpMatrix, float[] mvMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition){
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
            mAmbColorHandle = GLES20.glGetAttribLocation(mProgram, "a_material_ambient_Color");
            ShaderLoader.checkGlError("glGetAttribLocation");

            mDiffColorHandle = GLES20.glGetAttribLocation(mProgram, "a_material_diffuse_Color");
            ShaderLoader.checkGlError("glGetAttribLocation");

            mSpecColorHandle = GLES20.glGetAttribLocation(mProgram, "a_material_specular_Color");
            ShaderLoader.checkGlError("glGetAttribLocation");

            mLightPosHandle = GLES20.glGetUniformLocation(mProgram, "u_LightPos");
            ShaderLoader.checkGlError("glGetUniformLocation");

            //this.allNormalsBuffer.get(i).position(0);
            mNormalHandle = GLES20.glGetAttribLocation(mProgram, "a_Normal");
            ShaderLoader.checkGlError("glGetAttribLocation");

            mDistanceCoefHandle = GLES20.glGetUniformLocation(mProgram, "u_distance_coef");
            ShaderLoader.checkGlError("glGetUniformLocation");

            mLightCoefHandle = GLES20.glGetUniformLocation(mProgram, "u_light_coef");
            ShaderLoader.checkGlError("glGetUniformLocation");

            mCameraPosHandle = GLES20.glGetUniformLocation(mProgram, "u_CameraPosition");
            ShaderLoader.checkGlError("glGetUniformLocation");

            mSpecShininessHandle = GLES20.glGetUniformLocation(mProgram, "u_materialShininess");
            ShaderLoader.checkGlError("glGetUniformLocation");


            // Enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(mPositionHandle);
            // Prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, this.allVertexBuffer.get(i));
            ShaderLoader.checkGlError("glVertexAttribPointer");

            GLES20.glEnableVertexAttribArray(mAmbColorHandle);
            ShaderLoader.checkGlError("glEnableVertexAttribArray");
            GLES20.glVertexAttribPointer(mAmbColorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, this.allAmbColorBuffer.get(i));
            ShaderLoader.checkGlError("glVertexAttribPointer");

            GLES20.glEnableVertexAttribArray(mDiffColorHandle);
            ShaderLoader.checkGlError("glEnableVertexAttribArray");
            GLES20.glVertexAttribPointer(mDiffColorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, this.allDiffColorBuffer.get(i));
            ShaderLoader.checkGlError("glVertexAttribPointer");

            GLES20.glEnableVertexAttribArray(mSpecColorHandle);
            GLES20.glVertexAttribPointer(mSpecColorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, this.allSpecColorBuffer.get(i));

            GLES20.glEnableVertexAttribArray(mNormalHandle);
            GLES20.glVertexAttribPointer(mNormalHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, this.allNormalsBuffer.get(i));

            // get handle to shape's transformation matrix
            GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvMatrix, 0);

            // Apply the projection and view transformation
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
            ShaderLoader.checkGlError("glUniformMatrix4fv");

            GLES20.glUniform3fv(mLightPosHandle, 1, mLightPosInEyeSpace, 0);

            GLES20.glUniform3fv(mCameraPosHandle, 1, mCameraPosition, 0);

            GLES20.glUniform1f(mDistanceCoefHandle, this.distanceCoef);
            ShaderLoader.checkGlError("glUniform1f");

            GLES20.glUniform1f(mLightCoefHandle, this.lightCoef);
            ShaderLoader.checkGlError("glUniform1f");

            GLES20.glUniform1f(mSpecShininessHandle, this.allSpecShininess.get(i));
            ShaderLoader.checkGlError("glUniform1f");

            // Draw the polygon
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, this.allCoords.get(i).length / 3);

            // Disable vertex array
            GLES20.glDisableVertexAttribArray(mPositionHandle);
        }
    }
}
