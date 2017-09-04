package com.samuelberrien.spectrix.visualizations.explosion;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.spectrix.drawable.obj.ObjModel;
import com.samuelberrien.spectrix.utils.core.Visualization;

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

	private ObjModel octagone;
	private List<Octagone> mOctagone;

	private boolean isInit = false;

	@Override
	public void init(Context context, boolean isVR) {
		this.context = context;
		rand = new Random(System.currentTimeMillis());
		minDist = 5f;
		rangeDist = 2.5f;
		nbCenter = 30;
		nbSameCenter = 5;
		mCenterColorBuffer = new FloatBuffer[nbSameCenter * nbCenter];
		nbMaxOctagonePerExplosion = !isVR ? 5 : 2;
		mCenterPoint = new float[nbCenter * nbSameCenter][3];
		octagone = new ObjModel(this.context, "obj/octagone.obj", 1f, 1f, 1f, LIGHTAUGMENTATION, DISTANCECOEFF);
		maxOctagonSpeed = 1f;
		mOctagone = Collections.synchronizedList(new ArrayList<Octagone>());

		setupCenter();

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
		deleteOldOctagone();
		createNewOctagones(freqArray);
		moveOctagone();
	}

	@Override
	public float[] getCameraPosition() {
		return new float[]{0f, 0f, 0f};
	}

	@Override
	public float[] getInitCamLookDirVec() {
		return new float[]{0f, 0f, 1f};
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

			octagone.setColor(o.getmOctagoneColorBuffer());
			octagone.draw(tmpModelViewProjectionMatrix, tmpModelViewMatrix, mLightPosInEyeSpace);
		}
	}

	@Override
	public String getName() {
		return "Explosion";
	}

	private void setupCenter() {
		for (int i = 0; i < nbCenter * nbSameCenter; i++) {
			float maxRange = rand.nextFloat() * rangeDist + minDist;
			double phi = rand.nextDouble() * Math.PI * 2;
			double theta = rand.nextDouble() * Math.PI * 2;
			mCenterPoint[i][0] = maxRange * (float) (Math.cos(phi) * Math.sin(theta));
			mCenterPoint[i][1] = maxRange * (float) Math.sin(phi);
			mCenterPoint[i][2] = maxRange * (float) (Math.cos(phi) * Math.cos(theta));

			float[] color = new float[octagone.getVertexDrawListLength() * 4 / 3];
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
			mCenterColorBuffer[i] = tmp;
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
		float xSpeed = magn * maxOctagonSpeed * (float) (Math.cos(phi) * Math.sin(theta));
		float ySpeed = magn * maxOctagonSpeed * (float) Math.sin(phi);
		float zSpeed = magn * maxOctagonSpeed * (float) (Math.cos(phi) * Math.cos(theta));
		mOctagone.add(new Octagone((nbCenter - indiceFreq) * 0.001f + 0.1f, rand.nextFloat() * 360f, new float[]{rand.nextFloat() * 2 - 1f, rand.nextFloat() * 2 - 1f, rand.nextFloat() * 2 - 1f}, center, new float[]{xSpeed, ySpeed, zSpeed}, mCenterColorBuffer[indCenter]));
	}

	private void createNewOctagones(float[] freqArray) {
		for (int i = 0; i < nbSameCenter * nbCenter; i++) {
			int tmpFreqIndex = i / nbSameCenter;
			float tmpMagn = freqArray[tmpFreqIndex];// + freqArray[tmpFreqIndex] * tmpFreqIndex * mFreqAugmentation;
			int nbNewOct = Math.round(tmpMagn * (float) nbMaxOctagonePerExplosion);
			for (int j = 0; j < nbNewOct; j++) {
				addNewOctagone(mCenterPoint[i], tmpMagn, tmpFreqIndex, i);
			}
		}
	}

	/**
	 *
	 */
	private void deleteOldOctagone() {
		for (int i = mOctagone.size() - 1; i >= 0; i--) {
			if (mOctagone.get(i).getSpeedVectorNorm() < 0.2f) {
				mOctagone.remove(i);
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