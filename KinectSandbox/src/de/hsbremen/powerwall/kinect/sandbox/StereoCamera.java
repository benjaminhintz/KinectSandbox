/*
The MIT License (MIT)

Copyright (c) 2013 Jan Schulte

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package de.hsbremen.powerwall.kinect.sandbox;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

/**
 *
 * @author Jan Schulte
 */
public class StereoCamera extends Camera {
	
	// constants
	private static final float					CAMERA_OFFSET = 0.1f;
	private static final float					CAMERA_FOV = 45.0f;
	private static final float					DISTANCE_TO_TARGET = 20.0f;
	
	// private objects
	private com.jme3.renderer.Camera			mLeftCam = null;
	private com.jme3.renderer.Camera			mRightCam = null;
	private ViewPort							mLeftView = null;
	private ViewPort							mRightView = null;
	private Vector3f							mDirection = null;
	private Vector3f							mLeft = null;					
	private float								mRotationVertical = -30.0f;
	
	// temporary data
	Vector3f									mTempCameraPos = new Vector3f(0, 0, 1);
	Quaternion									mTempRotationQuaternion = new Quaternion();
		
	// properties
	@Override
	public Vector3f getDirection() {
		return mDirection;
	}
	
	@Override
	public Vector3f getLeft() {
		return mLeft;
	}
	
	@Override
	public float getVerticalRotation() {
		return mRotationVertical < 0.0f ? -mRotationVertical : mRotationVertical;
	}
	
	@Override
	public boolean isSingleView() {
		return false;
	}
	
	
	/**
	 * Constructor
	 */		
	public StereoCamera(SimpleApplication _application, SceneObject _target) {
		super(_application, _target);
		
		create();
	}
	
	private void create() {
		// disable fly camera
		mApplication.getFlyByCamera().setEnabled(false);
		
		AppSettings settings = mApplication.getContext().getSettings();
		Node rootNode = mApplication.getRootNode();
		
		mDirection = new Vector3f(0, 0, -1);
		mLeft = new Vector3f(-1, 0, 0);
		
		// create left & right camera
		mLeftCam = new com.jme3.renderer.Camera(settings.getWidth(), settings.getHeight());
		mRightCam = new com.jme3.renderer.Camera(settings.getWidth(), settings.getHeight());

		float aspect = (float)mRightCam.getWidth() / mRightCam.getHeight();
		
		// setup left camera
		mLeftCam.setFrustumPerspective(CAMERA_FOV, aspect, 0.1f, 1000f);
		mLeftCam.setLocation(new Vector3f(CAMERA_OFFSET, 0f, 10f));
		mLeftCam.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);
		mLeftCam.setViewPort(0f, 0.5f, 0f, 1f);

		// setup right camera
		mRightCam.setFrustumPerspective(CAMERA_FOV, aspect, 0.1f, 1000f);
		mRightCam.setLocation(new Vector3f(-CAMERA_OFFSET, 0f, 10f));
		mRightCam.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);
		mRightCam.setViewPort(.5f, 1f, 0f, 1f);

		RenderManager renderManager = mApplication.getRenderManager();

		mLeftView = renderManager.createMainView("LeftView", mLeftCam);
		mLeftView.setClearFlags(true, true, true);
		mLeftView.setBackgroundColor(ColorRGBA.Black);
		mLeftView.attachScene(rootNode);

		mRightView = renderManager.createMainView("RightView", mRightCam);
		mRightView.setClearFlags(true, true, true);
		mRightView.setBackgroundColor(ColorRGBA.Black);
		mRightView.attachScene(rootNode);		
	}
	
	public void update(float _delta) {
		Vector3f targetPos = mTarget.getPosition();

		// compute camera position
		mTempCameraPos.set(0, 0, 1);
		mTempRotationQuaternion.set(0, 0, 0, 1);
		mTempRotationQuaternion.fromAngleAxis(FastMath.DEG_TO_RAD * mRotationVertical, new Vector3f(1, 0, 0));
		mTempRotationQuaternion.multLocal(mTempCameraPos);
		mTempCameraPos.multLocal(DISTANCE_TO_TARGET);
		mTempCameraPos.addLocal(targetPos);
		
		// compute new direction
		targetPos.subtract(mTempCameraPos, mDirection);
		mDirection.normalizeLocal();
		//System.out.println("Direction: " + mDirection);
		
		// position one camera to get the left vector
		mRightCam.setLocation(mTempCameraPos);
		mRightCam.lookAt(targetPos, Vector3f.UNIT_Y);
		mLeft.set(mRightCam.getLeft());
		
		// offset cameras
		Vector3f leftDir = mLeft.normalize();
		
		// reposition with new offset
		//mRightCam.setLocation(mTempCameraPos);
		mRightCam.setLocation(mTempCameraPos.add(leftDir.mult(CAMERA_OFFSET)));
		mRightCam.lookAt(targetPos, Vector3f.UNIT_Y);
		
		//mLeftCam.setLocation(mTempCameraPos);
		mLeftCam.setLocation(mTempCameraPos.add(leftDir.mult(-CAMERA_OFFSET)));
		mLeftCam.lookAt(targetPos, Vector3f.UNIT_Y);
	}
	
	public void rotateVertival(float _value) {
		if (_value > 0) {
			_value = -_value;
		}
		
		mRotationVertical = _value;
	}
	
	@Override
	public void addProcessor(SceneProcessor _processor) {
		mRightView.addProcessor(_processor);
		mLeftView.addProcessor(_processor);
	}
	
	@Override
	public void addProcessorLeft(SceneProcessor _processor) {
		mLeftView.addProcessor(_processor);
	}
	
	@Override
	public void addProcessorRight(SceneProcessor _processor) {
		mRightView.addProcessor(_processor);
	}

}
