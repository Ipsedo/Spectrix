package com.samuelberrien.spectrix.obj.drawable;

import android.opengl.GLES20;

import com.samuelberrien.spectrix.utils.ShaderLoader;

/**
 * Created by samuel on 20/12/16.
 */

public class Light {

	private final int mProgram;

	final String vertexShaderCode =
			"uniform mat4 u_MVPMatrix;      \n"
					+ "attribute vec4 a_Position;     \n"
					+ "void main()                    \n"
					+ "{                              \n"
					+ "   gl_Position = u_MVPMatrix   \n"
					+ "               * a_Position;   \n"
					+ "   gl_PointSize = 5.0;         \n"
					+ "}                              \n";

	final String fragmentShaderCode =
			"precision mediump float;       \n"
					+ "void main()                    \n"
					+ "{                              \n"
					+ "   gl_FragColor = vec4(1.0,    \n"
					+ "   1.0, 1.0, 1.0);             \n"
					+ "}                              \n";

	private int pointMVPMatrixHandle;
	private int pointPositionHandle;

	/**
	 *
	 */
	public Light() {
		int vertexShader = ShaderLoader.loadShader(
				GLES20.GL_VERTEX_SHADER,
				vertexShaderCode);
		int fragmentShader = ShaderLoader.loadShader(
				GLES20.GL_FRAGMENT_SHADER,
				fragmentShaderCode);
		mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
		GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
		GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables

	}

	/**
	 * @param mMVPMatrix
	 * @param mLightPosInModelSpace
	 */
	public void draw(float[] mMVPMatrix, float[] mLightPosInModelSpace) {
		GLES20.glUseProgram(mProgram);
		pointMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
		pointPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
		GLES20.glVertexAttrib3f(pointPositionHandle, mLightPosInModelSpace[0], mLightPosInModelSpace[1], mLightPosInModelSpace[2]);
		GLES20.glDisableVertexAttribArray(pointPositionHandle);
		GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		// Draw the point.
		GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
	}
}
