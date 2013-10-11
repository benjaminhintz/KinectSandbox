/*
 * TODO: Licence
 */
package kinect.sandbox;

import com.jme3.app.SimpleApplication;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;

/**
 *
 * @author impmja
 */
public class StereoCamera {
	
	// global objects
	private SimpleApplication		mApplication;
	
	// private objects
	private ChaseCamera				mCamera;
	private SceneObject				mTarget;
	
	
	// properties
	public Vector3f getDirection() {
		return mApplication.getCamera().getDirection();
	}
	
	public Vector3f getLeft() {
		return mApplication.getCamera().getLeft();
	}
	
	/**
	 * Constructor
	 */		
	public StereoCamera(SimpleApplication _application, SceneObject _target) {
		mApplication = _application;
		mTarget = _target;
		
		create();
	}
	
	private void create() {
		// get global objects
		InputManager inputManager = mApplication.getInputManager();
		
		// disable fly camera
		mApplication.getFlyByCamera().setEnabled(false);
				
		ChaseCamera chaseCam = new ChaseCamera(mApplication.getCamera(), mTarget, inputManager);
		chaseCam.setSmoothMotion(false);
		chaseCam.setTrailingEnabled(false);
		chaseCam.setInvertVerticalAxis(true);
	}
	
	
	
}
