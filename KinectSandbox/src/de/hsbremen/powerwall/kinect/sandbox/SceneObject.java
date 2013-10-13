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

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

/**
 * Abstract Object Class
 * @author Jan Schulte
 */
abstract class SceneObject extends Geometry {
	
	// Members
	protected RigidBodyControl		mPhysicsController = null;
	
	// Properties
	public RigidBodyControl getController() {
		return mPhysicsController;
	}
	
	
	/**
	 * Constructor
	 */
	public SceneObject(String _name) {
		super(_name);
	}
	
	protected void activatePhysics() {
		if (mPhysicsController != null && mPhysicsController.getMass() != 0.0f)
		{
			mPhysicsController.setDamping(0.0f, 0.3f);
			mPhysicsController.setFriction(30.0f);
			mPhysicsController.setRestitution(0.0f);
			mPhysicsController.setSleepingThresholds(1f, 1f);
			mPhysicsController.activate();
		}
	}
	
	public void setPosition(Vector3f _position) {
		setLocalTranslation(_position);
		mPhysicsController.setPhysicsLocation(_position);
	}
	
	public void setRotation(Quaternion _rotation) {
		setLocalRotation(_rotation);
		mPhysicsController.setPhysicsRotation(_rotation);
	}
	
	public Vector3f getPosition() {
		return mPhysicsController.getPhysicsLocation();
	}
	
	public Quaternion getRotation() {
		return mPhysicsController.getPhysicsRotation();
	}
	
	public void addPosition(Vector3f _position) {
		Vector3f curentPosition = getPosition();
		//System.out.println("Current Pos: " + curentPosition);
		
		Vector3f newPosition = curentPosition.add(_position);
		//System.out.println("New Pos: " + newPosition);
		
		setPosition(newPosition);		
	}
	
	
}
