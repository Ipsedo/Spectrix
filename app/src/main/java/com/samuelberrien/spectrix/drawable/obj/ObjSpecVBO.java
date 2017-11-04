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

/**
 * Created by samuel on 26/10/17.
 */

public class ObjSpecVBO {

	private static final int POSITION_SIZE = 3;

	private static final int NORMAL_SIZE = 3;

	private static final int BYTES_PER_FLOAT = 4;

	private static final int STRIDE = (POSITION_SIZE + NORMAL_SIZE) * BYTES_PER_FLOAT;

	private FloatBuffer packedDataBuffer;

	private float[] diffColor = new float[]{0.5f, 0.5f, 0.5f, 1f};

	private int packedDataBufferId;

	private int nbVertex;

	private int mProgram;
	private int mPositionHandle;
	private int mNormalHandle;
	private int mMVPMatrixHandle;
	private int mLightPosHandle;
	private int mMVMatrixHandle;
	private int mDistanceCoefHandle;
	private int mCameraPosHandle;
	private int mDiffColorHandle;

	private float distanceCoef;

	public ObjSpecVBO(Context context,
					  int resId, float distanceCoef) {

		this.distanceCoef = distanceCoef;


		InputStream inputStream = context.getResources().openRawResource(resId);
		InputStreamReader inputreader = new InputStreamReader(inputStream);
		parseObj(inputreader);
		try {
			inputreader.close();
			inputStream.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		init(context);
	}

	public ObjSpecVBO(Context context,
					  String fileName, float distanceCoef) {

		this.distanceCoef = distanceCoef;


		try {
			InputStream inputStream = context.getAssets().open(fileName);
			InputStreamReader inputreader = new InputStreamReader(inputStream);
			parseObj(inputreader);
			inputreader.close();
			inputStream.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		init(context);
	}

	private void init(Context context) {
		int vertexShader = ShaderLoader.loadShader(
				GLES20.GL_VERTEX_SHADER,
				ShaderLoader.openShader(context, R.raw.specular_simple_vs));
		int fragmentShader = ShaderLoader.loadShader(
				GLES20.GL_FRAGMENT_SHADER,
				ShaderLoader.openShader(context, R.raw.specular_simple_fs));

		mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
		GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
		GLES20.glLinkProgram(mProgram);

		bind();
		bindBuffer();
	}

	private void bind() {
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
		mMVMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVMatrix");
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
		mDiffColorHandle = GLES20.glGetUniformLocation(mProgram, "u_material_diffuse_Color");
		mLightPosHandle = GLES20.glGetUniformLocation(mProgram, "u_LightPos");
		mDistanceCoefHandle = GLES20.glGetUniformLocation(mProgram, "u_distance_coef");
		mNormalHandle = GLES20.glGetAttribLocation(mProgram, "a_Normal");
		mCameraPosHandle = GLES20.glGetUniformLocation(mProgram, "u_CameraPosition");
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

	private void parseObj(InputStreamReader inputreader) {
		nbVertex = 0;
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

			buffreader1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ArrayList<Float> packedData = new ArrayList<>();
		for (int i = 0; i < vertexDrawOrderList.size(); i++) {
			packedData.add(vertixsList.get((vertexDrawOrderList.get(i) - 1) * 3));
			packedData.add(vertixsList.get((vertexDrawOrderList.get(i) - 1) * 3 + 1));
			packedData.add(vertixsList.get((vertexDrawOrderList.get(i) - 1) * 3 + 2));

			packedData.add(normalsList.get((normalDrawOrderList.get(i) - 1) * 3));
			packedData.add(normalsList.get((normalDrawOrderList.get(i) - 1) * 3 + 1));
			packedData.add(normalsList.get((normalDrawOrderList.get(i) - 1) * 3 + 2));
			nbVertex++;
		}

		float[] tmp = new float[packedData.size()];
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = packedData.get(i);
		}

		packedDataBuffer = ByteBuffer.allocateDirect(tmp.length * BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		packedDataBuffer.put(tmp)
				.position(0);
	}

	public void setDiffColor(float[] color) {
		diffColor = color;
	}

	public void draw(float[] mvpMatrix,
					 float[] mvMatrix,
					 float[] mLightPosInEyeSpace,
					 float[] mCameraPosition) {
		GLES20.glUseProgram(mProgram);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, packedDataBufferId);
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, POSITION_SIZE, GLES20.GL_FLOAT, false,
				STRIDE, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, packedDataBufferId);
		GLES20.glEnableVertexAttribArray(mNormalHandle);
		GLES20.glVertexAttribPointer(mNormalHandle, NORMAL_SIZE, GLES20.GL_FLOAT, false,
				STRIDE, POSITION_SIZE * BYTES_PER_FLOAT);

		GLES20.glUniform4fv(mDiffColorHandle, 1, diffColor, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvMatrix, 0);

		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

		GLES20.glUniform3fv(mLightPosHandle, 1, mLightPosInEyeSpace, 0);

		GLES20.glUniform3fv(mCameraPosHandle, 1, mCameraPosition, 0);

		GLES20.glUniform1f(mDistanceCoefHandle, distanceCoef);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, nbVertex);

		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}
}
