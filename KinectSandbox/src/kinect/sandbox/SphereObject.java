/*
 * TODO: Licence
 */
package kinect.sandbox;

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
	public PhysicsControl getController() {
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
		setShadowMode(RenderQueue.ShadowMode.Cast);
		
		// create physics controller
		mGhostControl = new GhostControl(new SphereCollisionShape(_radius));
		addControl(mGhostControl);
		
		// set position
		setPosition(_position);
	}
	
	public void setPosition(Vector3f _position) {
		setLocalTranslation(_position);
		mGhostControl.setPhysicsLocation(_position);
	}
	
	public Vector3f getPosition() {
		return mGhostControl.getPhysicsLocation();
	}

	
}
