package com.samuelberrien.spectrix.drawable;

import android.content.Context;
import android.opengl.GLES20;

import com.samuelberrien.spectrix.R;
import com.samuelberrien.spectrix.utils.graphics.ShaderLoader;
import com.samuelberrien.spectrix.utils.graphics.TextureHelper;

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

	/**
	 * @param context
	 * @param texHMResId
	 * @param texResId
	 * @param coeff
	 * @param lightCoeff
	 * @param distanceCoeff
	 */
	public HeightMap(Context context, int texHMResId, int texResId, float coeff, float lightCoeff, float distanceCoeff) {
		int vertexShader = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShaderLoader.openShader(context, R.raw.height_map_vs));
		int fragmentShader = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderLoader.openShader(context, R.raw.height_map_fs));

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
		mMVMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVMatrix");
		mLightPosHandle = GLES20.glGetUniformLocation(mProgram, "u_LightPos");
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vp");
		mTextureHeightMapUniformHandle = GLES20.glGetUniformLocation(mProgram, "textureHeight");
		mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "textureMap");
		mNbStripsHandles = GLES20.glGetUniformLocation(mProgram, "nbStrips");
		mNbSlicesHandles = GLES20.glGetUniformLocation(mProgram, "nbSlices");
		mCoeffHandle = GLES20.glGetUniformLocation(mProgram, "coefficient");
		mLightCoefHandle = GLES20.glGetUniformLocation(mProgram, "u_light_coef");
		mDistanceCoefHandle = GLES20.glGetUniformLocation(mProgram, "u_distance_coef");
	}

	/**
	 *
	 */
	private void initPlan() {
		nbFaces = NBSTRIPS * (NBSLICES + 1) * 2;
		points = new float[nbFaces * 3];
		for (int indStrip = 0; indStrip < NBSTRIPS; indStrip++) {
			for (int indFace = 0; indFace <= NBSLICES; indFace++) {
				int indPoint = indStrip * (NBSLICES + 1) * 2 + indFace * 2;
				points[indPoint * 3] = (float) indFace / (float) NBSLICES;
				points[indPoint * 3 + 1] = 0.0f;
				points[indPoint * 3 + 2] = (float) indStrip / (float) NBSTRIPS;

				indPoint++;
				points[indPoint * 3] = (float) indFace / (float) NBSLICES;
				points[indPoint * 3 + 1] = 0.0f;
				points[indPoint * 3 + 2] = ((float) indStrip + 1) / (float) NBSTRIPS;
			}
		}
		mPositions = ByteBuffer.allocateDirect(points.length * mBytesPerFloat)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mPositions.put(points).position(0);
	}

	/**
	 * @param mvpMatrix
	 * @param mvMatrix
	 * @param mLightPosInEyeSpace
	 */
	public void draw(float[] mvpMatrix, float[] mvMatrix, float[] mLightPosInEyeSpace) {
		GLES20.glUseProgram(this.mProgram);

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureHeightMapDataHandle);
		GLES20.glUniform1i(mTextureHeightMapUniformHandle, 0);

		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
		GLES20.glUniform1i(mTextureUniformHandle, 1);

		mPositions.position(0);
		GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mPositions);
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvMatrix, 0);

		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

		GLES20.glUniform3fv(mLightPosHandle, 1, mLightPosInEyeSpace, 0);

		GLES20.glUniform1i(mNbSlicesHandles, NBSLICES);
		GLES20.glUniform1i(mNbStripsHandles, NBSTRIPS);

		GLES20.glUniform1f(mCoeffHandle, this.coeff);

		GLES20.glUniform1f(mLightCoefHandle, this.lightCoeff);

		GLES20.glUniform1f(mDistanceCoefHandle, this.distanceCoeff);

		int nbStackTriangles = (NBSLICES + 1) * 2;
		for (int i = 0; i < NBSTRIPS; i++)
			GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, i * nbStackTriangles, nbStackTriangles);
	}
}
