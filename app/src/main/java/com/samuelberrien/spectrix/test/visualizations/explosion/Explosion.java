package com.samuelberrien.spectrix.test.visualizations.explosion;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.spectrix.obj.drawable.ObjModel;
import com.samuelberrien.spectrix.test.utils.Visualization;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Explosion implements Visualization {

	private Context context;

	private final float LIGHTAUGMENTATION = 1f;
	private final float DISTANCECOEFF = 0f;

	private Random rand;

	private float minDist;
	private float rangeDist;

	private int nbCenter;
	private int nbSameCenter;
	private float[][] mCenterPoint;
	private FloatBuffer[] mCenterColorBuffer;

	private float maxOctagonSpeed;

	private int nbMaxOctagonePerExplosion;
	private float mFreqAugmentation = 0.3f;

	private float[] freqArray = new float[1024];

	private ObjModel octagone;
	private List<Octagone> mOctagone;

	private boolean isInit = false;

	@Override
	public void init(Context context) {
		this.context = context;
		this.rand = new Random(System.currentTimeMillis());
		this.minDist = 5f;
		this.rangeDist = 2.5f;
		this.nbCenter = 30;
		this.nbSameCenter = 5;
		this.mCenterColorBuffer = new FloatBuffer[this.nbSameCenter * this.nbCenter];
		this.nbMaxOctagonePerExplosion = 5;
		this.mCenterPoint = new float[this.nbCenter * this.nbSameCenter][3];
		this.octagone = new ObjModel(this.context, "obj/octagone.obj", 1f, 1f, 1f, LIGHTAUGMENTATION, DISTANCECOEFF);
		this.maxOctagonSpeed = 1f;
		this.mOctagone = Collections.synchronizedList(new ArrayList<Octagone>());

		this.setupCenter();

		isInit = true;
	}

	@Override
	public boolean isInit() {
		return isInit;
	}

	@Override
	public boolean is3D() {
		return true;
	}

	@Override
	public void update(float[] freqArray) {
		this.freqArray = freqArray;
		this.deleteOldOctagone();
		this.createNewOctagones();
		this.moveOctagone();
	}

	@Override
	public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition) {
		float[] tmpModelViewMatrix = new float[16];
		float[] tmpModelViewProjectionMatrix = new float[16];
		ArrayList<Octagone> octagones = new ArrayList<>();
		octagones.addAll(mOctagone);
		for (Octagone o : octagones) {
			Matrix.multiplyMM(tmpModelViewMatrix, 0, mViewMatrix, 0, o.getmOctagoneModelMatrix(), 0);
			Matrix.multiplyMM(tmpModelViewProjectionMatrix, 0, mProjectionMatrix, 0, tmpModelViewMatrix, 0);

			this.octagone.setColor(o.getmOctagoneColorBuffer());
			this.octagone.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);
		}
	}

	@Override
	public String getString() {
		return "Explosion";
	}

	private void setupCenter() {
		for (int i = 0; i < this.nbCenter * this.nbSameCenter; i++) {
			float maxRange = rand.nextFloat() * rangeDist + minDist;
			double phi = rand.nextDouble() * Math.PI * 2;
			double theta = rand.nextDouble() * Math.PI * 2;
			this.mCenterPoint[i][0] = maxRange * (float) (Math.cos(phi) * Math.sin(theta));
			this.mCenterPoint[i][1] = maxRange * (float) Math.sin(phi);
			this.mCenterPoint[i][2] = maxRange * (float) (Math.cos(phi) * Math.cos(theta));

			float[] color = new float[this.octagone.getVertexDrawListLength() * 4 / 3];
			float red = rand.nextFloat();
			float green = rand.nextFloat();
			float blue = rand.nextFloat();
			for (int j = 0; j < color.length; j += 4) {
				color[j] = red;
				color[j + 1] = green;
				color[j + 2] = blue;
				color[j + 3] = 1f;
			}
			FloatBuffer tmp = ByteBuffer.allocateDirect(color.length * 4)
					.order(ByteOrder.nativeOrder())
					.asFloatBuffer();
			tmp.put(color)
					.position(0);
			this.mCenterColorBuffer[i] = tmp;
		}
	}

	/**
	 * @param center
	 * @param magn
	 * @param indiceFreq
	 * @param indCenter
	 */
	private void addNewOctagone(float[] center, float magn, int indiceFreq, int indCenter) {
		double phi = rand.nextDouble() * Math.PI * 2;
		double theta = rand.nextDouble() * Math.PI * 2;
		float xSpeed = magn * this.maxOctagonSpeed * (float) (Math.cos(phi) * Math.sin(theta));
		float ySpeed = magn * this.maxOctagonSpeed * (float) Math.sin(phi);
		float zSpeed = magn * this.maxOctagonSpeed * (float) (Math.cos(phi) * Math.cos(theta));
		this.mOctagone.add(new Octagone((this.nbCenter - indiceFreq) * 0.001f + 0.1f, rand.nextFloat() * 360f, new float[]{rand.nextFloat() * 2 - 1f, rand.nextFloat() * 2 - 1f, rand.nextFloat() * 2 - 1f}, center, new float[]{xSpeed, ySpeed, zSpeed}, this.mCenterColorBuffer[indCenter]));
	}

	private void createNewOctagones() {
		for (int i = 0; i < this.nbSameCenter * this.nbCenter; i++) {
			int tmpFreqIndex = i / this.nbSameCenter;
			float tmpMagn = freqArray[tmpFreqIndex] + freqArray[tmpFreqIndex] * tmpFreqIndex * this.mFreqAugmentation;
			int nbNewOct = (int) (tmpMagn * (float) this.nbMaxOctagonePerExplosion);
			for (int j = 0; j < nbNewOct; j++) {
				this.addNewOctagone(this.mCenterPoint[i], tmpMagn, tmpFreqIndex, i);
			}
		}
	}

	/**
	 *
	 */
	private void deleteOldOctagone() {
		for (int i = this.mOctagone.size() - 1; i >= 0; i--) {
			if (this.mOctagone.get(i).getSpeedVectorNorm() < 0.2f) {
				this.mOctagone.remove(i);
			}
		}
	}

	/**
	 *
	 */
	private void moveOctagone() {
		ArrayList<Octagone> octagones = new ArrayList<>(mOctagone);
		for (Octagone o : octagones)
			o.move();
	}
}