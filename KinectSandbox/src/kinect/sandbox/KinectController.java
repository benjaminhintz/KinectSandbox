/*
 * TODO: Licence
 */
package kinect.sandbox;

import com.jme3.app.SimpleApplication;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;

/**
 *
 * @author Jan Schulte
 */
public class KinectController implements ActionListener {
	
	// constants
	private Vector3f				FORWARD = new Vector3f(1.0f, 1.0f, 1.0f);
	
	
	// global objects
	private SimpleApplication		mApplication;
	
	// controller related objects
	private StereoCamera			mCamera;
	private SphereObject			mController;
	
	// movement flags
	private boolean					mForward = false;
	private boolean					mBackward = false;
	private boolean					mLeft = false;
	private boolean					mRight = false;
	
	
	
	/**
	 * Constructor
	 */
	public KinectController(SimpleApplication _application, SphereObject _controller, StereoCamera _camera) {
		mApplication = _application;
		mController = _controller;
		mCamera = _camera;
		
		create();
	}
	
	
	private void create() {
		// get global objects
		InputManager inputManager = mApplication.getInputManager();
		
		//init input
		inputManager.addMapping("forward", new KeyTrigger(KeyInput.KEY_W));
		inputManager.addListener(this, "forward");
		inputManager.addMapping("backward", new KeyTrigger(KeyInput.KEY_S));
		inputManager.addListener(this, "backward");
		inputManager.addMapping("left", new KeyTrigger(KeyInput.KEY_A));
		inputManager.addListener(this, "left");
		inputManager.addMapping("right", new KeyTrigger(KeyInput.KEY_D));
		inputManager.addListener(this, "right");
	}

	public void onAction(String name, boolean isPressed, float tpf) {
		if (name.equals("forward")) {
			mForward = isPressed;
        }
        if (name.equals("backward")) {
			mBackward = isPressed;
		}
		if (name.equals("left")) {
			mLeft = isPressed;
        }
		if (name.equals("right")) {
			mRight = isPressed;
        }
	}
	
	public void update(float _delta) {
		
		Vector3f camDir = mCamera.getDirection().clone().multLocal(10.0f * _delta);
		Vector3f camLeft = mCamera.getLeft().clone().multLocal(10.0f * _delta);
		
		if (mForward) {
			mController.addPosition(camDir);
        }
		
        if (mBackward) {
            mController.addPosition(camDir.negate());
        }

		if (mLeft) {
            mController.addPosition(camLeft);
        }
		
		if (mRight) {
            mController.addPosition(camLeft.negate());
        }
	}
	
}
