/*
 * TODO: Licence
 */
package kinect.sandbox;

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
