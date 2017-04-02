package com.samuelberrien.spectrix.obj;

import android.content.Context;
import android.opengl.GLES20;

import com.samuelberrien.spectrix.R;
import com.samuelberrien.spectrix.utils.ShaderLoader;
import com.samuelberrien.spectrix.utils.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by samuel on 01/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class HeightMap {

    private final int NBSLICES = 300;
    private final int NBSTRIPS = 300;
    private int nbFaces;
    private float[] points;

    private FloatBuffer mPositions;

    private int mMVPMatrixHandle;
    private int mMVMatrixHandle;
    private int mLightPosHandle;
    private int mPositionHandle;
    private int mTextureHeightMapDataHandle;
    private int mTextureHeightMapUniformHandle;
    private int mTextureDataHandle;
    private int mTextureUniformHandle;
    private int mNbSlicesHandles;
    private int mNbStripsHandles;
    private int mCoeffHandle;
    private int mLightCoefHandle;
    private int mDistanceCoefHandle;
    private int mProgram;

    private final int mBytesPerFloat = 4;
    private final int mPositionDataSize = 3;

    private float coeff;
    private float lightCoeff;
    private float distanceCoeff;

    public HeightMap(Context context, int texHMResId, int texResId, float coeff, float lightCoeff, float distanceCoeff){
        int vertexShader = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShaderLoader.openShader(context, R.raw.vertex_shader_height_map));
        int fragmentShader = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderLoader.openShader(context, R.raw.fragment_shader_height_map));

        this.mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(this.mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(this.mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(this.mProgram);

        mTextureHeightMapDataHandle = TextureHelper.loadTexture(context, texHMResId);
        mTextureDataHandle = TextureHelper.loadTexture(context, texResId);

        this.coeff = coeff;
        this.lightCoeff = lightCoeff;
        this.distanceCoeff = distanceCoeff;

        this.initPlan();
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
        ShaderLoader.checkGlError("glUniformLocation");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVMatrix");
        ShaderLoader.checkGlError("glUniformLocation");
        mLightPosHandle = GLES20.glGetUniformLocation(mProgram, "u_LightPos");
        ShaderLoader.checkGlError("glUniformLocation");
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vp");
        ShaderLoader.checkGlError("glUniformLocation");
        mTextureHeightMapUniformHandle = GLES20.glGetUniformLocation(mProgram, "textureHeight");
        ShaderLoader.checkGlError("glUniformLocation");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "textureMap");
        ShaderLoader.checkGlError("glUniformLocation");
        mNbStripsHandles = GLES20.glGetUniformLocation(mProgram, "nbStrips");
        ShaderLoader.checkGlError("glUniformLocation");
        mNbSlicesHandles = GLES20.glGetUniformLocation(mProgram, "nbSlices");
        ShaderLoader.checkGlError("glUniformLocation");
        mCoeffHandle = GLES20.glGetUniformLocation(mProgram, "coefficient");
        ShaderLoader.checkGlError("glUniformLocation");
        mLightCoefHandle = GLES20.glGetUniformLocation(mProgram, "u_light_coef");
        ShaderLoader.checkGlError("glUniformLocation");
        mDistanceCoefHandle = GLES20.glGetUniformLocation(mProgram, "u_distance_coef");
        ShaderLoader.checkGlError("glUniformLocation");
    }

    private void initPlan(){
        nbFaces = NBSTRIPS * (NBSLICES + 1) * 2;
        points = new float[ nbFaces * 3 ];
        for( int indStrip = 0 ; indStrip < NBSTRIPS ; indStrip++ ) {
            for( int indFace = 0 ; indFace <= NBSLICES ; indFace++ ) {
                int indPoint = indStrip * (NBSLICES + 1) * 2 + indFace * 2;
                points[ indPoint * 3 ] = (float)indFace / (float)NBSLICES;
                points[ indPoint * 3 + 1 ] = 0.0f;
                points[ indPoint * 3 + 2 ] = (float)indStrip / (float)NBSTRIPS;

                indPoint++;
                points[ indPoint * 3 ] = (float)indFace / (float)NBSLICES;
                points[ indPoint * 3 + 1 ] = 0.0f;
                points[ indPoint * 3 + 2 ] = ((float)indStrip + 1) / (float)NBSTRIPS;
            }
        }
        mPositions = ByteBuffer.allocateDirect(points.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mPositions.put(points).position(0);
    }

    public void draw(float[] mvpMatrix, float[] mvMatrix, float[] mLightPosInEyeSpace) {
        GLES20.glUseProgram(this.mProgram);
        ShaderLoader.checkGlError("glUseProgram");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureHeightMapDataHandle);
        ShaderLoader.checkGlError("glBindTexture");
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureHeightMapUniformHandle, 0);
        ShaderLoader.checkGlError("glUniform1i");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
        ShaderLoader.checkGlError("glBindTexture");
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 1);
        ShaderLoader.checkGlError("glUniform1i");

        mPositions.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mPositions);
        ShaderLoader.checkGlError("glVertexAttribPointer");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        ShaderLoader.checkGlError("glEnableVertexAttribArray");

        // get handle to shape's transformation matrix
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvMatrix, 0);
        ShaderLoader.checkGlError("glUniformMatrix4fv");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        ShaderLoader.checkGlError("glUniformMatrix4fv");

        GLES20.glUniform3fv(mLightPosHandle, 1, mLightPosInEyeSpace, 0);
        ShaderLoader.checkGlError("glUniform3fv");

        GLES20.glUniform1i(mNbSlicesHandles, NBSLICES);
        ShaderLoader.checkGlError("glUniform1i");
        GLES20.glUniform1i(mNbStripsHandles, NBSTRIPS);
        ShaderLoader.checkGlError("glUniform1i");

        GLES20.glUniform1f(mCoeffHandle, this.coeff);
        ShaderLoader.checkGlError("glUniform1i");

        GLES20.glUniform1f(mLightCoefHandle, this.lightCoeff);

        GLES20.glUniform1f(mDistanceCoefHandle, this.distanceCoeff);

        // Draw the cube.
        int nbStackTriangles = (NBSLICES + 1) * 2;
        for(int i = 0 ; i < NBSTRIPS; i++) {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, i * nbStackTriangles, nbStackTriangles);
            ShaderLoader.checkGlError("glDrawArrays");
        }
    }
}
