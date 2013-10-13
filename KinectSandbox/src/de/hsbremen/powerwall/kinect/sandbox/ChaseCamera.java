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
import com.jme3.input.InputManager;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.ViewPort;

/**
 *
 * @author Jan Schulte
 */
public class ChaseCamera extends Camera {
	// private objects
	private com.jme3.input.ChaseCamera				mCamera;
	
	// properties
	@Override
	public Vector3f getDirection() {
		return mApplication.getCamera().getDirection();
	}
	
	@Override
	public Vector3f getLeft() {
		return mApplication.getCamera().getLeft();
	}
	
	@Override
	public float getVerticalRotation() {
		return 0.0f;
	}
	
	@Override
	public boolean isSingleView() {
		return true;
	}
	
	
	/**
	 * Constructor
	 */		
	public ChaseCamera(SimpleApplication _application, SceneObject _target) {
		super(_application, _target);
		
		create();
	}
	
	private void create() {
		// get global objects
		InputManager inputManager = mApplication.getInputManager();
		
		// disable fly camera
		mApplication.getFlyByCamera().setEnabled(false);
				
		com.jme3.input.ChaseCamera chaseCam = new com.jme3.input.ChaseCamera(mApplication.getCamera(), mTarget, inputManager);
		chaseCam.setSmoothMotion(false);
		chaseCam.setTrailingEnabled(false);
		chaseCam.setInvertVerticalAxis(true);
		chaseCam.setDefaultHorizontalRotation(FastMath.DEG_TO_RAD * 90.0f);
		chaseCam.setMaxDistance(50.0f);
		chaseCam.setDefaultDistance(50.0f);
	}
	
	public void update(float _delta) {
		// Note: do nothing
	}
	
	public void rotateVertival(float _value) {
		// Note: do nothing
	}
	
	public void addPostProcessor(SceneProcessor _processor) {
		ViewPort viewPort = mApplication.getViewPort();
		viewPort.addProcessor(_processor);
	}
	
	@Override
	public void addProcessor(SceneProcessor _processor) {
		ViewPort viewPort = mApplication.getViewPort();
		viewPort.addProcessor(_processor);
	}
	
	@Override
	public void addProcessorLeft(SceneProcessor _processor) {
	}
	
	@Override
	public void addProcessorRight(SceneProcessor _processor) {
	}
}
