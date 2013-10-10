/*
 * TODO: Licence
 */
package kinect.sandbox;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;

/**
 * Abstract Object Class
 * @author Jan Schulte
 */
abstract class SceneObject extends Geometry {
	
	// Members
	protected RigidBodyControl		mPhysicsController;
	
	// Properties
	public RigidBodyControl getPhyscsController() {
		return mPhysicsController;
	}
	
	
	public SceneObject(String _name) {
		super(_name);
	}
}
