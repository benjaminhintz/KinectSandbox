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

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.joints.SixDofJoint;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import de.hsbremen.powerwall.kinect.FingerTracker;
import de.hsbremen.powerwall.kinect.events.FingerBaseEvent;
import de.hsbremen.powerwall.kinect.events.FingerDragEvent;
import de.hsbremen.powerwall.kinect.listener.DragListener;
import de.hsbremen.powerwall.kinect.listener.MoveListener;
import java.io.IOException;

/**
 *
 * @author Jan Schulte
 */
public class KinectController implements ActionListener, PhysicsCollisionListener, MoveListener, DragListener {
	
	// constants
	private static final float		KINECT_X_MIN_VALUE		= 100.0f;
	private static final float		KINECT_X_MAX_VALUE		= 100.0f;
	private static final float		KINECT_Y_MAX_VALUE		= 600.0f;
	private static final float		KINECT_Z_MIN_VALUE		= 100.0f;
	private static final float		KINECT_Z_MAX_VALUE		= 100.0f;
	
	private static final float		KINECT_Y_OFFSET			= -20.0f;	// How much offset the hand should have to the ground to be able to make meaningful geastures
	private static final boolean	KINECT_INVERT_HANDS		= true;		// Swich hands depending on how you positioned your kinect

	private static final float		MIN_CAMERA_VERTICAL_ROTATION = 10.0f;	
	private static final float		MAX_CAMERA_VERTICAL_ROTATION = 80.0f;

	// global objects
	private SimpleApplication		mApplication;
	private BulletAppState			mBulletAppState;
	
	// controller related objects
	private Camera					mCamera;
	private SceneObject				mController;
	private BoxObject				mLastTouchedObject = null;
	private BoxObject				mCurrentControlledObject = null;
	private Vector3f				mLocalControllerCollisionPoint = null;
	private Vector3f				mLocalTouchedObjectCollisionPoint = null;
	private SixDofJoint				mConnection;
		
	// kinect finger tracker
	private FingerTracker			mFingerTracker = null;
	private Vector3f				mNewPosition = null;
	private Vector3f				mCurrentPosition = null;
	private float					mMovementLerpFactor = 0;
			
	// mouse movement flags
	private boolean					mForward = false;
	private boolean					mBackward = false;
	private boolean					mLeft = false;
	private boolean					mRight = false;
	
	// rotation flags
	private javax.vecmath.Vector3f	mFristRightHandPosition = null;
	private float					mInitialCameraRotation = MIN_CAMERA_VERTICAL_ROTATION;
	private float					mCurrentCameraRotation = MIN_CAMERA_VERTICAL_ROTATION;
	private boolean					mInCameraRotationMode = false;
	private float					mRotationLerpFactor = 0;
	
	/**
	 * Constructor
	 */
	public KinectController(SimpleApplication _application, BulletAppState _bulletAppState, SceneObject _controller, Camera _camera) {
		mApplication = _application;
		mBulletAppState = _bulletAppState;
		mController = _controller;
		mCamera = _camera;
		
		mNewPosition = _controller.getPosition().clone();
		mCurrentPosition = _controller.getPosition().clone();
		
		mFristRightHandPosition = new javax.vecmath.Vector3f(0 , 0, 0);
		
		create();
	}
	
	
	private void create() {
	
		//init input
		if (Scene.MOUSE_INPUT) {
			InputManager inputManager = mApplication.getInputManager();
			
			inputManager.addMapping("forward", new KeyTrigger(KeyInput.KEY_W));
			inputManager.addListener(this, "forward");
			inputManager.addMapping("backward", new KeyTrigger(KeyInput.KEY_S));
			inputManager.addListener(this, "backward");
			inputManager.addMapping("left", new KeyTrigger(KeyInput.KEY_A));
			inputManager.addListener(this, "left");
			inputManager.addMapping("right", new KeyTrigger(KeyInput.KEY_D));
			inputManager.addListener(this, "right");
			inputManager.addMapping("pickup", new KeyTrigger(KeyInput.KEY_Q));
			inputManager.addListener(this, "pickup");
			inputManager.addMapping("drop", new KeyTrigger(KeyInput.KEY_E));
			inputManager.addListener(this, "drop");
		}
		else
		{
			// start Kinect input listener
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						mFingerTracker = new FingerTracker();
						mFingerTracker.addMoveListener(KinectController.this);
						mFingerTracker.addDragListener(KinectController.this);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
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
		if (_name.equals("pickup") && _isPressed) {
			pickupObject();
        }
		if (_name.equals("drop")) {
			dropObject();
        }
	}
	
	public void update(float _delta) {
		if (Scene.MOUSE_INPUT) {
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
		else
		{
			synchronized(mNewPosition) {
				// smooth the movement abit
				mMovementLerpFactor = Math.min(mMovementLerpFactor + (_delta * _delta * 0.6f * 0.05f), 1);
				FastMath.interpolateLinear(mMovementLerpFactor, mCurrentPosition, mNewPosition, mCurrentPosition);
				mController.setPosition(mCurrentPosition);
			}
			/*
			synchronized(mCamera) {
				float currentRotation = mCamera.getVerticalRotation();
				System.out.println("Current Camera rotation: " + currentRotation);
				System.out.println("New Camera rotation: " + mCurrentCameraRotation);
				
				mRotationLerpFactor = Math.min((_delta * 400.0f), 1); // Math.min(mRotationLerpFactor + (_delta * _delta * 0.05f), 1);
				System.out.println("New Camera rotation lerp: " + mRotationLerpFactor);
				mCurrentCameraRotation = FastMath.interpolateLinear(mRotationLerpFactor, currentRotation, mCurrentCameraRotation);
				mCamera.rotateVertival(mCurrentCameraRotation);	
			}
			*/
		}
	}
	
	public void updatePosition(FingerBaseEvent _event) {
		synchronized(mNewPosition) {
			javax.vecmath.Vector3f pos = _event.getPosition();
			
			mNewPosition.x = KINECT_INVERT_HANDS ? (-1.0f) * pos.x : pos.x;
			mNewPosition.y = pos.y;
			mNewPosition.z = pos.z;
			
			// scale values to [-1..0..1] & cap to playfield
			float groundSize = Scene.GROUND_SIZE / 2.0f;
			if (mNewPosition.x < 0.0f)
				mNewPosition.x = (mNewPosition.x / KINECT_X_MIN_VALUE) * groundSize;
			else if (mNewPosition.x > 0.0f)
				mNewPosition.x = (mNewPosition.x / KINECT_X_MAX_VALUE) * groundSize;
				
			if (mNewPosition.y > 0.0f)
				mNewPosition.y = ((mNewPosition.y / KINECT_Y_MAX_VALUE) * Scene.CONTROLLER_MAX_HEIGHT) + KINECT_Y_OFFSET;
			// restrict to ground height
			if (mNewPosition.y < Scene.CONTROLLER_MIN_HEIGHT)
				mNewPosition.y = Scene.CONTROLLER_MIN_HEIGHT;
			
			if (mNewPosition.z < 0.0f)
				mNewPosition.z = (mNewPosition.z / KINECT_Z_MIN_VALUE) * groundSize;
			else if (mNewPosition.z > 0.0f)
				mNewPosition.z = (mNewPosition.z / KINECT_Z_MAX_VALUE) * groundSize;
		}
	}
	
	public void collision(PhysicsCollisionEvent _event) {
		
		if (mConnection != null && mCurrentControlledObject != null)
		{
			return;
		}
		
		// ignore ground
		if (_event.getNodeA().getName().equals("Ground") || _event.getNodeB().getName().equals("Ground") ||
			(_event.getNodeA().getName().equals("Controller") && _event.getNodeB().getName().equals("Controller")))
		{
			return;
		}
				
		//System.out.println("Object A: " + _event.getNodeA().getName());
		//System.out.println("Object B: " + _event.getNodeB().getName());
		
		// check if we hit a BoxObject
		if (mController.getName().equals(_event.getNodeA().getName())) {
			if (_event.getNodeB() instanceof BoxObject) {
				mLastTouchedObject = (BoxObject) _event.getNodeB();
				mLocalControllerCollisionPoint = _event.getLocalPointA().clone();
				mLocalTouchedObjectCollisionPoint = _event.getLocalPointB().clone();
			}
		} else if (mController.getName().equals(_event.getNodeB().getName())) {
			if (_event.getNodeA() instanceof BoxObject) {
				mLastTouchedObject = (BoxObject) _event.getNodeA();
				mLocalControllerCollisionPoint = _event.getLocalPointB().clone();
				mLocalTouchedObjectCollisionPoint = _event.getLocalPointA().clone();
			}
		}
	}
	
	
	private synchronized void pickupObject() {

		if (mLastTouchedObject == null || mLocalControllerCollisionPoint == null || mLocalTouchedObjectCollisionPoint == null) {
			return;
		}
		
		if (mConnection != null) {
			mBulletAppState.getPhysicsSpace().remove(mConnection);
			mConnection.destroy();
			mConnection = null;
		}
		
		// setup joints between both objects
		mConnection =  new SixDofJoint(mController.getController(), mLastTouchedObject.getController(), mLocalControllerCollisionPoint, mLocalTouchedObjectCollisionPoint, true);	
		mConnection.setAngularUpperLimit(new Vector3f(0, 0, 0));
		mConnection.setAngularLowerLimit(new Vector3f(0, 0, 0));
		mConnection.setLinearLowerLimit(new Vector3f(0, 0, 0));
		mConnection.setLinearUpperLimit(new Vector3f(0, 0, 0));
		mBulletAppState.getPhysicsSpace().add(mConnection);
		
		mCurrentControlledObject = mLastTouchedObject;
		
		// reset temp data
		mLastTouchedObject = null;
		mLocalControllerCollisionPoint = null;
		mLocalTouchedObjectCollisionPoint = null;
		
		// add glow to the box
		mCurrentControlledObject.getMaterial().setColor("GlowColor", new ColorRGBA(1f, 0f, 0f, 0.2f));
		mCurrentControlledObject.getController().activate();
		
		System.out.println("Pickup Object: " + mCurrentControlledObject.getName());
	}
	
	private synchronized void dropObject() {
		if (mCurrentControlledObject == null || mConnection == null) {
			return;
		}
			
		System.out.println("Drop Object: " + mCurrentControlledObject.getName());
		
		if (mConnection != null) {
			mConnection.destroy();
			mBulletAppState.getPhysicsSpace().remove(mConnection);
			mConnection = null;
		}
		
		// remove glow
		mCurrentControlledObject.getMaterial().setColor("GlowColor", ColorRGBA.Black);
		mCurrentControlledObject.getController().activate();
		
		mLastTouchedObject = null;
		mLocalControllerCollisionPoint = null;
		mLocalTouchedObjectCollisionPoint = null;
		mCurrentControlledObject = null;
	}

	private synchronized void startRotateCamera(FingerDragEvent _event) {
		mInCameraRotationMode = true;
		
		synchronized(mFristRightHandPosition) {
			javax.vecmath.Vector3f pos = _event.getDragStart();
			mInCameraRotationMode = true;
			mFristRightHandPosition.set(pos.x, pos.y, pos.z);
		}
			
		synchronized(mCamera) {
			mInitialCameraRotation = mCamera.getVerticalRotation();
		}
	}
	
	private synchronized void rotateCamera(FingerBaseEvent _event) {
		if (!mInCameraRotationMode) {
			return;
		}
		
		synchronized(mFristRightHandPosition) {
			javax.vecmath.Vector3f pos = _event.getPosition();

			// just look at the z-axis to determine the rotation angle
			float zDiff = ((mFristRightHandPosition.z - pos.z) / (KINECT_Z_MIN_VALUE + KINECT_Z_MAX_VALUE)) * Scene.GROUND_SIZE;
			mCurrentCameraRotation = mInitialCameraRotation + zDiff;
			
			if (mCurrentCameraRotation < MIN_CAMERA_VERTICAL_ROTATION) {
				mCurrentCameraRotation = MIN_CAMERA_VERTICAL_ROTATION;	
			} else if (mCurrentCameraRotation > MAX_CAMERA_VERTICAL_ROTATION) {
				mCurrentCameraRotation = MAX_CAMERA_VERTICAL_ROTATION;
			}
			
			mCamera.rotateVertival(mCurrentCameraRotation);
		}
	}
	
	// Kinect finger tracker input
	public void onLeftHandMove(FingerBaseEvent _evt) {
		if (KINECT_INVERT_HANDS) {
			updatePosition(_evt);
		} else {
			rotateCamera(_evt);
		}
	}

	public void onRightHandMove(FingerBaseEvent _evt) {
		if (!KINECT_INVERT_HANDS) {
			updatePosition(_evt);
		} else {
			rotateCamera(_evt);
		}
	}

	public void onLeftHandDragStart(FingerDragEvent _evt) {
		if (KINECT_INVERT_HANDS) {
			pickupObject();
		} else {
			startRotateCamera(_evt);
		}
	}

	public void onRightHandDragStart(FingerDragEvent _evt) {
		if (!KINECT_INVERT_HANDS) {
			pickupObject();
		} else {
			startRotateCamera(_evt);
		}
	}

	public void onLeftHandDragEnd(FingerDragEvent _evt) {
		if (KINECT_INVERT_HANDS) {
			dropObject();
		} else {
			mInCameraRotationMode = false;
		}
	}

	public void onRightHandDragEnd(FingerDragEvent _evt) {
		if (!KINECT_INVERT_HANDS) {
			dropObject();
		} else {
			mInCameraRotationMode = false;
		}
	}

	public void onLeftHandDrag(FingerDragEvent evt) {
	}

	public void onRightHandDrag(FingerDragEvent evt) {
	}
}
