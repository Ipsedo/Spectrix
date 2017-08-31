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

	protected Context context;

	/**
	 * Size of the position data in elements.
	 */
	private static final int POSITION_DATA_SIZE = 3;

	/**
	 * Size of the normal data in elements.
	 */
	private static final int NORMAL_DATA_SIZE = 3;

	private static final int COLOR_DATA_SIZE = 4;

	private static final int SHININESS_DATA_SIZE = 1;

	/**
	 * How many bytes per float.
	 */
	private static final int BYTES_PER_FLOAT = 4;

	private HashMap<String, float[]> mtlAmbColor = new HashMap<>();
	private HashMap<String, float[]> mtlDiffColor = new HashMap<>();
	private HashMap<String, float[]> mtlSpecColor = new HashMap<>();
	private HashMap<String, Float> mtlSpecShininess = new HashMap<>();

	private FloatBuffer vertexBuffer;
	private FloatBuffer normalsBuffer;
	private FloatBuffer ambColorBuffer;
	protected FloatBuffer diffColorBuffer;
	private FloatBuffer specColorBuffer;
	private FloatBuffer specShininess;

	private int vertexBufferId;
	private int normalsBufferId;
	private int ambBufferId;
	private int diffBufferId;
	private int specBufferId;
	private int shinBufferId;

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

	// number of coordinates per vertex in this array
	private final int COORDS_PER_VERTEX = 3;
	protected float[] allCoords;
	private float[] allNormals;
	private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

	/**
	 * @param context           The application context
	 * @param objResId          The res id of the obj 3D model file
	 * @param mtlResId          The res id of the mtl model file
	 * @param lightAugmentation The light augmentation
	 * @param distanceCoef      The distance attenuation coefficient
	 */
	public ObjModelMtlVBO(Context context, int objResId, int mtlResId, float lightAugmentation, float distanceCoef, boolean randomColor) {

		this.context = context;

		InputStream inputStream;
		inputStream = context.getResources().openRawResource(mtlResId);
		this.parseMtl(inputStream);
		inputStream = context.getResources().openRawResource(objResId);
		this.parseObj(inputStream, randomColor);

		this.lightCoef = lightAugmentation;
		this.distanceCoef = distanceCoef;

		this.makeProgram(context, R.raw.specular_vs_2, R.raw.specular_fs_2);
		this.bindBuffer();
	}

	/**
	 * @param context           The application context
	 * @param objFileName       The obj file name in assets folder
	 * @param mtlFileName       The mtl file name in assets folder
	 * @param lightAugmentation The light augmentation
	 * @param distanceCoef      The distance attenuation coefficient
	 */
	public ObjModelMtlVBO(Context context, String objFileName, String mtlFileName, float lightAugmentation, float distanceCoef, boolean randomColor) {

		this.context = context;

		InputStream inputStream;
		try {
			inputStream = context.getAssets().open(mtlFileName);
			this.parseMtl(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			inputStream = context.getAssets().open(objFileName);
			this.parseObj(inputStream, randomColor);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.lightCoef = lightAugmentation;
		this.distanceCoef = distanceCoef;

		this.makeProgram(context, R.raw.specular_vs_2, R.raw.specular_fs_2);
		this.bindBuffer();
	}

	public ObjModelMtlVBO(ObjModelMtlVBO objModelMtl) {
		this.context = objModelMtl.context;

		this.mtlAmbColor = objModelMtl.mtlAmbColor;
		this.mtlDiffColor = objModelMtl.mtlDiffColor;
		this.mtlSpecColor = objModelMtl.mtlSpecColor;

		this.vertexBuffer = objModelMtl.vertexBuffer;
		this.normalsBuffer = objModelMtl.normalsBuffer;
		this.ambColorBuffer = objModelMtl.ambColorBuffer;
		this.diffColorBuffer = objModelMtl.diffColorBuffer;
		this.specColorBuffer = objModelMtl.specColorBuffer;
		this.specShininess = objModelMtl.specShininess;

		this.vertexBufferId = objModelMtl.vertexBufferId;
		this.normalsBufferId = objModelMtl.normalsBufferId;
		this.ambBufferId = objModelMtl.ambBufferId;
		this.diffBufferId = objModelMtl.diffBufferId;
		this.specBufferId = objModelMtl.specBufferId;
		this.shinBufferId = objModelMtl.shinBufferId;

		this.mProgram = objModelMtl.mProgram;
		this.mPositionHandle = objModelMtl.mPositionHandle;
		this.mNormalHandle = objModelMtl.mNormalHandle;
		this.mAmbColorHandle = objModelMtl.mAmbColorHandle;
		this.mDiffColorHandle = objModelMtl.mDiffColorHandle;
		this.mSpecColorHandle = objModelMtl.mSpecColorHandle;
		this.mSpecShininessHandle = objModelMtl.mSpecShininessHandle;
		this.mCameraPosHandle = objModelMtl.mCameraPosHandle;
		this.mMVPMatrixHandle = objModelMtl.mMVPMatrixHandle;
		this.mLightPosHandle = objModelMtl.mLightPosHandle;
		this.mMVMatrixHandle = objModelMtl.mMVMatrixHandle;
		this.mDistanceCoefHandle = objModelMtl.mDistanceCoefHandle;
		this.mLightCoefHandle = objModelMtl.mLightCoefHandle;

		this.lightCoef = objModelMtl.lightCoef;
		this.distanceCoef = objModelMtl.distanceCoef;

		this.allCoords = objModelMtl.allCoords;
		this.allNormals = objModelMtl.allNormals;
	}

	private void makeProgram(Context context, int vertexShaderResId, int fragmentShaderResId) {
		int vertexShader = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShaderLoader.openShader(context, vertexShaderResId));
		int fragmentShader = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderLoader.openShader(context, fragmentShaderResId));

		this.mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
		GLES20.glAttachShader(this.mProgram, vertexShader);   // add the vertex shader to program
		GLES20.glAttachShader(this.mProgram, fragmentShader); // add the fragment shader to program
		GLES20.glLinkProgram(this.mProgram);

		this.bind();
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
		final int buffers[] = new int[6];
		GLES20.glGenBuffers(6, buffers, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, this.vertexBuffer.capacity() * BYTES_PER_FLOAT, this.vertexBuffer, GLES20.GL_STATIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, this.normalsBuffer.capacity() * BYTES_PER_FLOAT, this.normalsBuffer, GLES20.GL_STATIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[2]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, this.ambColorBuffer.capacity() * BYTES_PER_FLOAT, this.ambColorBuffer, GLES20.GL_STATIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[3]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, this.diffColorBuffer.capacity() * BYTES_PER_FLOAT, this.diffColorBuffer, GLES20.GL_STATIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[4]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, this.specColorBuffer.capacity() * BYTES_PER_FLOAT, this.specColorBuffer, GLES20.GL_STATIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[5]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, this.specShininess.capacity() * BYTES_PER_FLOAT, this.specShininess, GLES20.GL_STATIC_DRAW);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		this.vertexBufferId = buffers[0];
		this.normalsBufferId = buffers[1];
		this.ambBufferId = buffers[2];
		this.diffBufferId = buffers[3];
		this.specBufferId = buffers[4];
		this.shinBufferId = buffers[5];

		this.vertexBuffer.limit(0);
		this.vertexBuffer = null;
		this.normalsBuffer.limit(0);
		this.normalsBuffer = null;
		this.ambColorBuffer.limit(0);
		this.ambColorBuffer = null;
		// Need to keeps diffColorBuffer for Explosion

		this.specColorBuffer.limit(0);
		this.specColorBuffer = null;
		this.specShininess.limit(0);
		this.specShininess = null;
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
					this.mtlAmbColor.put(currentMtl, new float[]{Float.parseFloat(tmp[1]), Float.parseFloat(tmp[2]), Float.parseFloat(tmp[3])});
				} else if (line.startsWith("Kd")) {
					String[] tmp = line.split(" ");
					this.mtlDiffColor.put(currentMtl, new float[]{Float.parseFloat(tmp[1]), Float.parseFloat(tmp[2]), Float.parseFloat(tmp[3])});
				} else if (line.startsWith("Ks")) {
					String[] tmp = line.split(" ");
					this.mtlSpecColor.put(currentMtl, new float[]{Float.parseFloat(tmp[1]), Float.parseFloat(tmp[2]), Float.parseFloat(tmp[3])});
				} else if (line.startsWith("Ns")) {
					this.mtlSpecShininess.put(currentMtl, Float.parseFloat(line.split(" ")[1]));
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

		ArrayList<Float> coords = new ArrayList<>();
		ArrayList<Float> normals = new ArrayList<>();
		ArrayList<Float> ambColor = new ArrayList<>();
		ArrayList<Float> diffColor = new ArrayList<>();
		ArrayList<Float> specColor = new ArrayList<>();
		ArrayList<Float> specShin = new ArrayList<>();
		Random random = new Random(System.currentTimeMillis());
		for (int i = 0; i < allVertexDrawOrderList.size(); i++) {
			for (int j = 0; j < allVertexDrawOrderList.get(i).size(); j++) {
				coords.add(currVertixsList.get((allVertexDrawOrderList.get(i).get(j) - 1) * 3));
				coords.add(currVertixsList.get((allVertexDrawOrderList.get(i).get(j) - 1) * 3 + 1));
				coords.add(currVertixsList.get((allVertexDrawOrderList.get(i).get(j) - 1) * 3 + 2));
			}

			for (int j = 0; j < allNormalDrawOrderList.get(i).size(); j++) {
				normals.add(currNormalsList.get((allNormalDrawOrderList.get(i).get(j) - 1) * 3));
				normals.add(currNormalsList.get((allNormalDrawOrderList.get(i).get(j) - 1) * 3 + 1));
				normals.add(currNormalsList.get((allNormalDrawOrderList.get(i).get(j) - 1) * 3 + 2));
			}
			float ambRed, ambGreen, ambBlue, diffRed, diffGreen, diffBlue, specRed, specGreen, specBlue;
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
				ambRed = this.mtlAmbColor.get(mtlToUse.get(i))[0];
				ambGreen = this.mtlAmbColor.get(mtlToUse.get(i))[1];
				ambBlue = this.mtlAmbColor.get(mtlToUse.get(i))[2];

				diffRed = this.mtlDiffColor.get(mtlToUse.get(i))[0];
				diffGreen = this.mtlDiffColor.get(mtlToUse.get(i))[1];
				diffBlue = this.mtlDiffColor.get(mtlToUse.get(i))[2];

				specRed = this.mtlSpecColor.get(mtlToUse.get(i))[0];
				specGreen = this.mtlSpecColor.get(mtlToUse.get(i))[1];
				specBlue = this.mtlSpecColor.get(mtlToUse.get(i))[2];
			}

			for (int j = 0; j < allVertexDrawOrderList.get(i).size() * 4; j += 4) {
				ambColor.add(ambRed);
				ambColor.add(ambGreen);
				ambColor.add(ambBlue);
				ambColor.add(1f);

				diffColor.add(diffRed);
				diffColor.add(diffGreen);
				diffColor.add(diffBlue);
				diffColor.add(1f);

				specColor.add(specRed);
				specColor.add(specGreen);
				specColor.add(specBlue);
				specColor.add(1f);

				specShin.add(this.mtlSpecShininess.get(mtlToUse.get(i)));
			}
		}

		this.allCoords = new float[coords.size()];
		for (int i = 0; i < allCoords.length; i++) {
			this.allCoords[i] = coords.get(i);
		}
		this.vertexBuffer = ByteBuffer.allocateDirect(this.allCoords.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		this.vertexBuffer.put(this.allCoords)
				.position(0);

		this.allNormals = new float[normals.size()];
		for (int i = 0; i < this.allNormals.length; i++) {
			this.allNormals[i] = normals.get(i);
		}
		this.normalsBuffer = ByteBuffer.allocateDirect(this.allNormals.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		this.normalsBuffer.put(this.allNormals)
				.position(0);

		float[] allAmbColor = new float[ambColor.size()];
		for (int i = 0; i < allAmbColor.length; i++) {
			allAmbColor[i] = ambColor.get(i);
		}
		this.ambColorBuffer = ByteBuffer.allocateDirect(allAmbColor.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		this.ambColorBuffer.put(allAmbColor)
				.position(0);

		float[] allDiffColor = new float[diffColor.size()];
		for (int i = 0; i < allDiffColor.length; i++) {
			allDiffColor[i] = diffColor.get(i);
		}
		this.diffColorBuffer = ByteBuffer.allocateDirect(allDiffColor.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		this.diffColorBuffer.put(allDiffColor)
				.position(0);

		float[] allSpecColor = new float[specColor.size()];
		for (int i = 0; i < allSpecColor.length; i++) {
			allSpecColor[i] = specColor.get(i);
		}
		this.specColorBuffer = ByteBuffer.allocateDirect(allSpecColor.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		this.specColorBuffer.put(allSpecColor)
				.position(0);

		float[] allSpecShin = new float[specShin.size()];
		for (int i = 0; i < allSpecShin.length; i++) {
			allSpecShin[i] = specShin.get(i);
		}
		this.specShininess = ByteBuffer.allocateDirect(allSpecShin.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		this.specShininess.put(allSpecShin)
				.position(0);
	}

	public void draw(float[] mvpMatrix, float[] mvMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
		GLES20.glUseProgram(mProgram);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.vertexBufferId);
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, 0, 0);

		// Pass in the normal information
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.normalsBufferId);
		GLES20.glEnableVertexAttribArray(mNormalHandle);
		GLES20.glVertexAttribPointer(mNormalHandle, NORMAL_DATA_SIZE, GLES20.GL_FLOAT, false, 0, 0);

		// Pass in the texture information
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.ambBufferId);
		GLES20.glEnableVertexAttribArray(this.mAmbColorHandle);
		GLES20.glVertexAttribPointer(this.mAmbColorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, 0, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.diffBufferId);
		GLES20.glEnableVertexAttribArray(this.mDiffColorHandle);
		GLES20.glVertexAttribPointer(this.mDiffColorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, 0, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.specBufferId);
		GLES20.glEnableVertexAttribArray(this.mSpecColorHandle);
		GLES20.glVertexAttribPointer(this.mSpecColorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false, 0, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.shinBufferId);
		GLES20.glEnableVertexAttribArray(this.mSpecShininessHandle);
		GLES20.glVertexAttribPointer(this.mSpecShininessHandle, SHININESS_DATA_SIZE, GLES20.GL_FLOAT, false, 0, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvMatrix, 0);

		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

		GLES20.glUniform3fv(mLightPosHandle, 1, mLightPosInEyeSpace, 0);

		GLES20.glUniform3fv(mCameraPosHandle, 1, mCameraPosition, 0);

		GLES20.glUniform1f(mDistanceCoefHandle, this.distanceCoef);

		GLES20.glUniform1f(mLightCoefHandle, this.lightCoef);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, this.allCoords.length / 3);

		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}

}
