/*
 * TODO: Licence
 */
package kinect.sandbox;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.joints.PhysicsJoint;
import com.jme3.bullet.joints.Point2PointJoint;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;

/**
 *
 * @author Jan Schulte
 */
public class KinectController implements ActionListener, PhysicsCollisionListener {
	
	// constants
	private Vector3f				FORWARD = new Vector3f(1.0f, 1.0f, 1.0f);
	
	
	// global objects
	private SimpleApplication		mApplication;
	private BulletAppState			mBulletAppState;
	
	// controller related objects
	private StereoCamera			mCamera;
	private SceneObject				mController;
	private BoxObject				mLastTouchedObject = null;
	private BoxObject				mCurrentControlledObject = null;
	private Vector3f				mLocalControllerCollisionPoint = null;
	private Vector3f				mLocalTouchedObjectCollisionPoint = null;
	private Point2PointJoint		mConnection;
	
			
	// movement flags
	private boolean					mForward = false;
	private boolean					mBackward = false;
	private boolean					mLeft = false;
	private boolean					mRight = false;
	
	/**
	 * Constructor
	 */
	public KinectController(SimpleApplication _application, BulletAppState _bulletAppState, SceneObject _controller, StereoCamera _camera) {
		mApplication = _application;
		mBulletAppState = _bulletAppState;
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
		inputManager.addMapping("pickup", new KeyTrigger(KeyInput.KEY_RETURN));
		inputManager.addListener(this, "pickup");
		inputManager.addMapping("drop", new KeyTrigger(KeyInput.KEY_SPACE));
		inputManager.addListener(this, "drop");
	}

	public void onAction(String _name, boolean _isPressed, float _tpf) {
		if (_name.equals("forward")) {
			mForward = _isPressed;
        }
        if (_name.equals("backward")) {
			mBackward = _isPressed;
		}
		if (_name.equals("left")) {
			mLeft = _isPressed;
        }
		if (_name.equals("right")) {
			mRight = _isPressed;
        }
		if (_name.equals("pickup")) {
			pickupObject();
        }
		if (_name.equals("drop")) {
			dropObject();
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

	public void collision(PhysicsCollisionEvent _event) {
		
		if (mConnection != null && mCurrentControlledObject != null)
		{
			System.out.println("Object picked up: " + mCurrentControlledObject.getName());
			return;
		}
		
		// ignore ground
		if (_event.getNodeA().getName().equals("Ground") || _event.getNodeB().getName().equals("Ground"))
		{
			return;
		}
				
		System.out.println("Object A: " + _event.getNodeA().getName());
		System.out.println("Object B: " + _event.getNodeB().getName());
		
		// check if we hit a BoxObject
		if (mController.getName().equals(_event.getNodeA().getName())) {
			if (_event.getNodeB() instanceof BoxObject) {
				// we dont wanna pick the ground
				if (_event.getNodeB().getName().equals("Ground"))
				{
					return;
				}
				
				mLastTouchedObject = (BoxObject) _event.getNodeB();
				mLocalControllerCollisionPoint = _event.getLocalPointA().clone();
				mLocalTouchedObjectCollisionPoint = _event.getLocalPointB().clone();
			}
		} else if (mController.getName().equals(_event.getNodeB().getName())) {
			if (_event.getNodeA() instanceof BoxObject) {
				// we dont wanna pick the ground
				if (_event.getNodeB().getName().equals("Ground"))
				{
					return;
				}
				
				mLastTouchedObject = (BoxObject) _event.getNodeA();
				mLocalControllerCollisionPoint = _event.getLocalPointB().clone();
				mLocalTouchedObjectCollisionPoint = _event.getLocalPointA().clone();
			}
		}
	}
	
	
	private void pickupObject() {
		if (mLastTouchedObject == null || mLocalControllerCollisionPoint == null || mLocalTouchedObjectCollisionPoint == null) {
			return;
		}
		
		dropObject();
		
		// create connection joint
		//Vector3f pivotA = mController.worldToLocal(mLocalControllerCollisionPoint, new Vector3f());
        //Vector3f pivotB = mLastTouchedObject.worldToLocal(mLocalTouchedObjectCollisionPoint, new Vector3f());
		
		mConnection = new Point2PointJoint(mController.getController(), mLastTouchedObject.getController(), mLocalControllerCollisionPoint, mLocalTouchedObjectCollisionPoint);
		mBulletAppState.getPhysicsSpace().add(mConnection);
		
		mCurrentControlledObject = mLastTouchedObject;
		mLastTouchedObject = null;
		
		System.out.println("Pickup Object: " + mCurrentControlledObject.getName());
	}
	
	private void dropObject() {
		if (mCurrentControlledObject == null || mConnection == null) {
			return;
		}
			
		System.out.println("Drop Object: " + mCurrentControlledObject.getName());
		
		if (mConnection != null) {
			mBulletAppState.getPhysicsSpace().remove(mConnection);
			mConnection.destroy();
			mConnection = null;
		}
		
		mCurrentControlledObject = null;
	}
	
}
