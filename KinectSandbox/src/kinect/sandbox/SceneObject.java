/*
 * TODO: Licence
 */
package kinect.sandbox;

import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
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
			//mPhysicsController.setDamping(0.75f, 0f);
			mPhysicsController.setFriction(1f);
			mPhysicsController.setRestitution(1.0f);
			mPhysicsController.setSleepingThresholds(1f, 1f);
			
			mPhysicsController.activate();
		}
	}
	
	public void setPosition(Vector3f _position) {
		//setLocalTranslation(_position);
		mPhysicsController.setPhysicsLocation(_position);
	}
	
	public Vector3f getPosition() {
		return mPhysicsController.getPhysicsLocation();
	}
	
	public void addPosition(Vector3f _position) {
		Vector3f curentPosition = getPosition();
		//System.out.println("Current Pos: " + curentPosition);
		
		Vector3f newPosition = curentPosition.add(_position);
		//System.out.println("New Pos: " + newPosition);
		
		setPosition(newPosition);		
	}
}
