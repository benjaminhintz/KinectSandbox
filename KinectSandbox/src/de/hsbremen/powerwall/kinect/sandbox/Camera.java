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
import com.jme3.math.Vector3f;
import com.jme3.post.SceneProcessor;

/**
 *
 * @author Jan Schulte
 */
public abstract class Camera {
	// global objects
	protected SimpleApplication		mApplication;
	
	// private objects
	protected SceneObject			mTarget;
	
	
	// properties
	public SceneObject getTarget() {
		return mTarget;
	}
	
	public abstract Vector3f getDirection();
	public abstract Vector3f getLeft();
	public abstract float getVerticalRotation();
	public abstract boolean isSingleView();
	
	/**
	 * Constructor
	 */	
	public Camera(SimpleApplication _application, SceneObject _target) {
		mApplication = _application;
		mTarget = _target;
	}
	
	public abstract void update(float _delta);
	public abstract void rotateVertival(float _value);
	
	public abstract void addProcessor(SceneProcessor _processor);
	public abstract void addProcessorLeft(SceneProcessor _processor);
	public abstract void addProcessorRight(SceneProcessor _processor);
}
