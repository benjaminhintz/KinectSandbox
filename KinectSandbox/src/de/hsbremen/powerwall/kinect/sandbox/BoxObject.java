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
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.shape.Box;
import com.jme3.util.TangentBinormalGenerator;

/**
 *
 * @author impmja
 */
public class BoxObject extends SceneObject {

	/**
	 * Constructor
	 */
	public BoxObject(String _name, Vector3f _position, Vector3f _size, Material _material, float _mass, Vector2f _textureScale) {
		super(_name);
		
		// create box
		Box box = new Box(_size.x, _size.y, _size.z);
		if (_textureScale != null)
		{
			box.scaleTextureCoordinates(_textureScale);
		}
		
		setMesh(box);
		setMaterial(_material);
		setShadowMode(ShadowMode.CastAndReceive);
		TangentBinormalGenerator.generate(box);
		
		// create physics controller
		mPhysicsController = new RigidBodyControl(_mass);
		addControl(mPhysicsController);
		//mPhysicsController.setKinematic(false);
		
		// setup physics
		activatePhysics();
		
		// set position
		setPosition(_position);
	}
}
