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

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.shape.Sphere;


/**
 *
 * @author impmja
 */
public class SphereObject extends SceneObject {
	
	private GhostControl	mGhostControl = null;
	
	// Properties
	public PhysicsControl getGhostController() {
		return mGhostControl;
	}
	
	/**
	 * Constructor
	 */
	public SphereObject(String _name, Vector3f _position, float _radius, Material _material, float _mass) {
		super(_name);
		
		// create sphere object
		Sphere sphere = new Sphere(32, 32, _radius, false, true);
		
		setMesh(sphere);
		setMaterial(_material);
		setShadowMode(RenderQueue.ShadowMode.Off);
		
		// create ghost physics controller
		mGhostControl = new GhostControl(new SphereCollisionShape(_radius));
		addControl(mGhostControl);
		
		// create physics controller
		mPhysicsController = new RigidBodyControl(10.0f);
		addControl(mPhysicsController);
		//mPhysicsController.setGravity(new Vector3f(0,0,0));
		mPhysicsController.setKinematic(true);
		mPhysicsController.setCollisionShape(new SphereCollisionShape(1.0f));
		mPhysicsController.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_04);
		mPhysicsController.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_04);
				
		// set position
		setPosition(_position);
	}
	
	/*
	public void setPosition(Vector3f _position) {
		setLocalTranslation(_position);
		//mGhostControl.setPhysicsLocation(_position);
		mPhysicsController.setPhysicsLocation(_position);
	}
	
	public Vector3f getPosition() {
		return getLocalTranslation();
		//return mGhostControl.getPhysicsLocation();
	}
	*/
	
}
