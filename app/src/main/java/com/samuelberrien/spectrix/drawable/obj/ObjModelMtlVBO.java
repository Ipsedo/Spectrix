package com.samuelberrien.spectrix.drawable.obj;

import android.content.Context;
import android.opengl.GLES20;

import com.samuelberrien.spectrix.R;
import com.samuelberrien.spectrix.utils.graphics.ShaderLoader;

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
 * Created by samuel on 14/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class ObjModelMtlVBO {

	private static final int POSITION_DATA_SIZE = 3;

	private static final int NORMAL_DATA_SIZE = 3;

	private static final int COLOR_DATA_SIZE = 4;

	private static final int SHININESS_DATA_SIZE = 1;

	private static final int BYTES_PER_FLOAT = 4;

	private static final int STRIDE = (POSITION_DATA_SIZE
			+ NORMAL_DATA_SIZE
			+ COLOR_DATA_SIZE * 3
			+ SHININESS_DATA_SIZE)
			* BYTES_PER_FLOAT;

	private HashMap<String, float[]> mtlAmbColor = new HashMap<>();
	private HashMap<String, float[]> mtlDiffColor = new HashMap<>();
	private HashMap<String, float[]> mtlSpecColor = new HashMap<>();
	private HashMap<String, Float> mtlSpecShininess = new HashMap<>();

	private FloatBuffer packedDataBuffer;

	private float[] randomDiffRGB = new float[3];

	private int packedDataBufferId;

	private int mProgram;
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
	private int mLightCoefHandle;

	private float lightCoef;
	private float distanceCoef;

	private int nbVertex;

	/**
	 * @param context           The application context
	 * @param objResId          The res id of the obj 3D model file
	 * @param mtlResId          The res id of the mtl model file
	 * @param lightAugmentation The light augmentation
	 * @param distanceCoef      The distance attenuation coefficient
	 */
	public ObjModelMtlVBO(Context context,
						  int objResId, int mtlResId,
						  float lightAugmentation, float distanceCoef, boolean randomColor) {

		InputStream inputStream;
		inputStream = context.getResources().openRawResource(mtlResId);
		parseMtl(inputStream);
		inputStream = context.getResources().openRawResource(objResId);
		parseObj(inputStream, randomColor);

		lightCoef = lightAugmentation;
		this.distanceCoef = distanceCoef;

		makeProgram(context, R.raw.specular_vs_2, R.raw.specular_fs_2);
		bindBuffer();
	}

	/**
	 * @param context           The application context
	 * @param objFileName       The obj file name in assets folder
	 * @param mtlFileName       The mtl file name in assets folder
	 * @param lightAugmentation The light augmentation
	 * @param distanceCoef      The distance attenuation coefficient
	 */
	public ObjModelMtlVBO(Context context,
						  String objFileName, String mtlFileName,
						  float lightAugmentation, float distanceCoef, boolean randomColor) {

		InputStream inputStream;
		try {
			inputStream = context.getAssets().open(mtlFileName);
			parseMtl(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			inputStream = context.getAssets().open(objFileName);
			parseObj(inputStream, randomColor);
		} catch (IOException e) {
			e.printStackTrace();
		}

		lightCoef = lightAugmentation;
		this.distanceCoef = distanceCoef;

		makeProgram(context, R.raw.specular_vs_2, R.raw.specular_fs_2);
		bindBuffer();
	}

	private void makeProgram(Context context, int vertexShaderResId, int fragmentShaderResId) {
		int vertexShader = ShaderLoader.loadShader(
				GLES20.GL_VERTEX_SHADER,
				ShaderLoader.openShader(context, vertexShaderResId));
		int fragmentShader = ShaderLoader.loadShader(
				GLES20.GL_FRAGMENT_SHADER,
				ShaderLoader.openShader(context, fragmentShaderResId));

		mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
		GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
		GLES20.glLinkProgram(mProgram);

		bind();
	}

	/**
	 * Get shaders var location
	 */
	private void bind() {
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
		mMVMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVMatrix");
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
		mAmbColorHandle = GLES20.glGetAttribLocation(mProgram, "a_material_ambient_Color");
		mDiffColorHandle = GLES20.glGetAttribLocation(mProgram, "a_material_diffuse_Color");
		mSpecColorHandle = GLES20.glGetAttribLocation(mProgram, "a_material_specular_Color");
		mLightPosHandle = GLES20.glGetUniformLocation(mProgram, "u_LightPos");
		mNormalHandle = GLES20.glGetAttribLocation(mProgram, "a_Normal");
		mDistanceCoefHandle = GLES20.glGetUniformLocation(mProgram, "u_distance_coef");
		mLightCoefHandle = GLES20.glGetUniformLocation(mProgram, "u_light_coef");
		mCameraPosHandle = GLES20.glGetUniformLocation(mProgram, "u_CameraPosition");
		mSpecShininessHandle = GLES20.glGetAttribLocation(mProgram, "a_material_shininess");
	}

	private void bindBuffer() {
		final int buffers[] = new int[1];
		GLES20.glGenBuffers(1, buffers, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
				packedDataBuffer.capacity() * BYTES_PER_FLOAT,
				packedDataBuffer, GLES20.GL_STATIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		packedDataBufferId = buffers[0];
		packedDataBuffer.limit(0);
		packedDataBuffer = null;
	}

	/**
	 * @param inputStream The input stream of the file
	 */
	private void parseMtl(InputStream inputStream) {
		InputStreamReader inputreader = new InputStreamReader(inputStream);
		BufferedReader buffreader = new BufferedReader(inputreader);
		String line;
		try {
			String currentMtl = "";
			while ((line = buffreader.readLine()) != null) {
				if (line.startsWith("newmtl")) {
					currentMtl = line.split(" ")[1];
				} else if (line.startsWith("Ka")) {
					String[] tmp = line.split(" ");
					mtlAmbColor.put(currentMtl, new float[]{
							Float.parseFloat(tmp[1]),
							Float.parseFloat(tmp[2]),
							Float.parseFloat(tmp[3])});
				} else if (line.startsWith("Kd")) {
					String[] tmp = line.split(" ");
					mtlDiffColor.put(currentMtl, new float[]{
							Float.parseFloat(tmp[1]),
							Float.parseFloat(tmp[2]),
							Float.parseFloat(tmp[3])});
				} else if (line.startsWith("Ks")) {
					String[] tmp = line.split(" ");
					mtlSpecColor.put(currentMtl, new float[]{
							Float.parseFloat(tmp[1]),
							Float.parseFloat(tmp[2]),
							Float.parseFloat(tmp[3])});
				} else if (line.startsWith("Ns")) {
					mtlSpecShininess.put(currentMtl, Float.parseFloat(line.split(" ")[1]));
				}
			}
			buffreader.close();
			inputreader.close();
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param inputStream The input stream of the file
	 */
	private void parseObj(InputStream inputStream, boolean randomColor) {
		InputStreamReader inputreader = new InputStreamReader(inputStream);
		BufferedReader buffreader = new BufferedReader(inputreader);
		String line;

		ArrayList<Float> currVertixsList = new ArrayList<>();
		ArrayList<Float> currNormalsList = new ArrayList<>();
		ArrayList<Integer> currVertexDrawOrderList = new ArrayList<>();
		ArrayList<Integer> currNormalDrawOrderList = new ArrayList<>();
		ArrayList<ArrayList<Integer>> allVertexDrawOrderList = new ArrayList<>();
		ArrayList<ArrayList<Integer>> allNormalDrawOrderList = new ArrayList<>();
		ArrayList<String> mtlToUse = new ArrayList<>();

		int idMtl = 0;

		try {
			while ((line = buffreader.readLine()) != null) {
				if (line.startsWith("usemtl")) {
					mtlToUse.add(line.split(" ")[1]);
					if (idMtl != 0) {
						allVertexDrawOrderList.add(currVertexDrawOrderList);
						allNormalDrawOrderList.add(currNormalDrawOrderList);
					}
					currVertexDrawOrderList = new ArrayList<>();
					currNormalDrawOrderList = new ArrayList<>();
					idMtl++;
				} else if (line.startsWith("vn")) {
					String[] tmp = line.split(" ");
					currNormalsList.add(Float.parseFloat(tmp[1]));
					currNormalsList.add(Float.parseFloat(tmp[2]));
					currNormalsList.add(Float.parseFloat(tmp[3]));
				} else if (line.startsWith("v ")) {
					String[] tmp = line.split(" ");
					currVertixsList.add(Float.parseFloat(tmp[1]));
					currVertixsList.add(Float.parseFloat(tmp[2]));
					currVertixsList.add(Float.parseFloat(tmp[3]));
				} else if (line.startsWith("f")) {
					String[] tmp = line.split(" ");
					currVertexDrawOrderList.add(Integer.parseInt(tmp[1].split("/")[0]));
					currVertexDrawOrderList.add(Integer.parseInt(tmp[2].split("/")[0]));
					currVertexDrawOrderList.add(Integer.parseInt(tmp[3].split("/")[0]));

					currNormalDrawOrderList.add(Integer.parseInt(tmp[1].split("/")[2]));
					currNormalDrawOrderList.add(Integer.parseInt(tmp[2].split("/")[2]));
					currNormalDrawOrderList.add(Integer.parseInt(tmp[3].split("/")[2]));
				}
			}
			buffreader.close();
			inputreader.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		allVertexDrawOrderList.add(currVertexDrawOrderList);
		allNormalDrawOrderList.add(currNormalDrawOrderList);

		ArrayList<Float> objPackedData = new ArrayList<>();
		Random random = new Random(System.currentTimeMillis());
		nbVertex = 0;
		for (int i = 0; i < allVertexDrawOrderList.size(); i++) {
			float ambRed, ambGreen, ambBlue,
					diffRed, diffGreen, diffBlue,
					specRed, specGreen, specBlue;
			if (randomColor) {
				ambRed = random.nextFloat();
				ambGreen = random.nextFloat();
				ambBlue = random.nextFloat();

				diffRed = random.nextFloat();
				diffGreen = random.nextFloat();
				diffBlue = random.nextFloat();

				specRed = random.nextFloat();
				specGreen = random.nextFloat();
				specBlue = random.nextFloat();
			} else {
				ambRed = mtlAmbColor.get(mtlToUse.get(i))[0];
				ambGreen = mtlAmbColor.get(mtlToUse.get(i))[1];
				ambBlue = mtlAmbColor.get(mtlToUse.get(i))[2];

				diffRed = mtlDiffColor.get(mtlToUse.get(i))[0];
				diffGreen = mtlDiffColor.get(mtlToUse.get(i))[1];
				diffBlue = mtlDiffColor.get(mtlToUse.get(i))[2];

				specRed = mtlSpecColor.get(mtlToUse.get(i))[0];
				specGreen = mtlSpecColor.get(mtlToUse.get(i))[1];
				specBlue = mtlSpecColor.get(mtlToUse.get(i))[2];
			}

			randomDiffRGB[0] = diffRed;
			randomDiffRGB[1] = diffGreen;
			randomDiffRGB[2] = diffBlue;

			for (int j = 0; j < allVertexDrawOrderList.get(i).size(); j++) {
				objPackedData.add(
						currVertixsList.get(
								(allVertexDrawOrderList.get(i).get(j) - 1) * 3));
				objPackedData.add(
						currVertixsList.get(
								(allVertexDrawOrderList.get(i).get(j) - 1) * 3 + 1));
				objPackedData.add(
						currVertixsList.get(
								(allVertexDrawOrderList.get(i).get(j) - 1) * 3 + 2));

				objPackedData.add(
						currNormalsList.get(
								(allNormalDrawOrderList.get(i).get(j) - 1) * 3));
				objPackedData.add(
						currNormalsList.get(
								(allNormalDrawOrderList.get(i).get(j) - 1) * 3 + 1));
				objPackedData.add(
						currNormalsList.get(
								(allNormalDrawOrderList.get(i).get(j) - 1) * 3 + 2));

				objPackedData.add(ambRed);
				objPackedData.add(ambGreen);
				objPackedData.add(ambBlue);
				objPackedData.add(1f);

				objPackedData.add(diffRed);
				objPackedData.add(diffGreen);
				objPackedData.add(diffBlue);
				objPackedData.add(1f);

				objPackedData.add(specRed);
				objPackedData.add(specGreen);
				objPackedData.add(specBlue);
				objPackedData.add(1f);

				objPackedData.add(mtlSpecShininess.get(mtlToUse.get(i)));

				nbVertex++;
			}
		}

		float[] allDataPacked = new float[objPackedData.size()];
		for (int i = 0; i < allDataPacked.length; i++) {
			allDataPacked[i] = objPackedData.get(i);
		}

		packedDataBuffer = ByteBuffer.allocateDirect(allDataPacked.length * BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		packedDataBuffer.put(allDataPacked)
				.position(0);

	}

	public void changeColor() {
		int tmp = mSpecColorHandle;
		mSpecColorHandle = mDiffColorHandle;
		mDiffColorHandle = tmp;
	}

	public void draw(float[] mvpMatrix,
					 float[] mvMatrix,
					 float[] mLightPosInEyeSpace,
					 float[] mCameraPosition) {
		GLES20.glUseProgram(mProgram);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, packedDataBufferId);
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false,
				STRIDE, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, packedDataBufferId);
		GLES20.glEnableVertexAttribArray(mNormalHandle);
		GLES20.glVertexAttribPointer(mNormalHandle, NORMAL_DATA_SIZE, GLES20.GL_FLOAT, false,
				STRIDE, POSITION_DATA_SIZE * BYTES_PER_FLOAT);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, packedDataBufferId);
		GLES20.glEnableVertexAttribArray(mAmbColorHandle);
		GLES20.glVertexAttribPointer(mAmbColorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false,
				STRIDE, (POSITION_DATA_SIZE + NORMAL_DATA_SIZE) * BYTES_PER_FLOAT);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, packedDataBufferId);
		GLES20.glEnableVertexAttribArray(mDiffColorHandle);
		GLES20.glVertexAttribPointer(mDiffColorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false,
				STRIDE, (POSITION_DATA_SIZE + NORMAL_DATA_SIZE + COLOR_DATA_SIZE) * BYTES_PER_FLOAT);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, packedDataBufferId);
		GLES20.glEnableVertexAttribArray(mSpecColorHandle);
		GLES20.glVertexAttribPointer(mSpecColorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false,
				STRIDE, (POSITION_DATA_SIZE + NORMAL_DATA_SIZE + COLOR_DATA_SIZE * 2) * BYTES_PER_FLOAT);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, packedDataBufferId);
		GLES20.glEnableVertexAttribArray(mSpecShininessHandle);
		GLES20.glVertexAttribPointer(mSpecShininessHandle, SHININESS_DATA_SIZE, GLES20.GL_FLOAT, false,
				STRIDE, (POSITION_DATA_SIZE + NORMAL_DATA_SIZE + COLOR_DATA_SIZE * 3) * BYTES_PER_FLOAT);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvMatrix, 0);

		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

		GLES20.glUniform3fv(mLightPosHandle, 1, mLightPosInEyeSpace, 0);

		GLES20.glUniform3fv(mCameraPosHandle, 1, mCameraPosition, 0);

		GLES20.glUniform1f(mDistanceCoefHandle, distanceCoef);

		GLES20.glUniform1f(mLightCoefHandle, lightCoef);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, nbVertex);

		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}

	public float[] getRandomMtlDiffRGB() {
		return randomDiffRGB;
	}
}