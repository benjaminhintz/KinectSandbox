/*
 * TODO: Licence
 */
package kinect.sandbox;

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
import com.jme3.math.Quaternion;
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
	private static float					KINECT_X_MIN_VALUE		= 100.0f;
	private static float					KINECT_X_MAX_VALUE		= 100.0f;
	private static float					KINECT_Y_MAX_VALUE		= 600.0f;
	private static float					KINECT_Z_MIN_VALUE		= 100.0f;
	private static float					KINECT_Z_MAX_VALUE		= 100.0f;
	
	private static float					KINECT_Y_OFFSET			= -20.0f;	// How much offset the hand should have to the ground to be able to make meaningful geastures
	private static boolean					KINECT_INVERT_HANDS		= true;		// Swich hands depending on how you positioned your kinect
	private static boolean					MOUSE_INPUT				= false;
	
	/*
	Max Position: (288.72, 581.303, 257.574)
	Min Position: (-489.182, -1.70818, -450.201)
	*/
			
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
	private SixDofJoint				mConnection;
		
	// kinect finger tracker
	private FingerTracker			mFingerTracker = null;
	private Vector3f				mNewPosition = null;
	private Vector3f				mCurrentPosition = null;
	//private Quaternion				mNewRotation = null;
	//private Quaternion				mCurrentRotation = null;
	
	//private Vector3f				mMinUpdatePosition = null;
	//private Vector3f				mMaxUpdatePosition = null;
	private float					mMovementLerpFactor = 0;
			
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
		
		mNewPosition = _controller.getPosition().clone();
		mCurrentPosition = _controller.getPosition().clone();
		//mNewRotation =  _controller.getRotation().clone();
		//mCurrentRotation =  _controller.getRotation().clone();
						
		//mMinUpdatePosition = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
		//mMaxUpdatePosition = new Vector3f(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
		
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
		inputManager.addMapping("pickup", new KeyTrigger(KeyInput.KEY_Q));
		inputManager.addListener(this, "pickup");
		inputManager.addMapping("drop", new KeyTrigger(KeyInput.KEY_E));
		inputManager.addListener(this, "drop");
		
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
		
		if (!MOUSE_INPUT) {
			synchronized(mNewPosition) {
				mMovementLerpFactor = Math.min(mMovementLerpFactor + (_delta * _delta * 0.6f * 0.05f), 1);
				//System.out.println("lerp: " + mMovementLerpFactor);
				FastMath.interpolateLinear(mMovementLerpFactor, mCurrentPosition, mNewPosition, mCurrentPosition);

				mController.setPosition(mCurrentPosition);
			}

			/* Does not work properly...
			synchronized(mNewRotation) {
				mController.setRotation(mNewRotation);
				//mCurrentRotation = mNewRotation.clone();
			}
			*/
		}
	}
	
	public void updatePosition(FingerBaseEvent _event) {
		synchronized(mNewPosition) {
			javax.vecmath.Vector3f pos = _event.getPosition();
			
			//System.out.println("Position: " + pos);
			
			mNewPosition.x = KINECT_INVERT_HANDS ? (-1.0f) * pos.x : pos.x;
			mNewPosition.y = pos.y;
			mNewPosition.z = pos.z;
			
			// cap position to playfield
			/*
			mMinUpdatePosition.x = mUpdatePosition.x < mMinUpdatePosition.x ? mUpdatePosition.x : mMinUpdatePosition.x;
			mMinUpdatePosition.y = mUpdatePosition.y < mMinUpdatePosition.y ? mUpdatePosition.y : mMinUpdatePosition.y;
			mMinUpdatePosition.z = mUpdatePosition.z < mMinUpdatePosition.z ? mUpdatePosition.z : mMinUpdatePosition.z;
			
			mMaxUpdatePosition.x = mUpdatePosition.x > mMaxUpdatePosition.x ? mUpdatePosition.x : mMaxUpdatePosition.x;
			mMaxUpdatePosition.y = mUpdatePosition.y > mMaxUpdatePosition.y ? mUpdatePosition.y : mMaxUpdatePosition.y;
			mMaxUpdatePosition.z = mUpdatePosition.z > mMaxUpdatePosition.z ? mUpdatePosition.z : mMaxUpdatePosition.z;
			*/
					
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
			
			//System.out.println("Position: " + mUpdatePosition);
			
			/*
			System.out.println("Min Position: " + mMinUpdatePosition);
			System.out.println("Max Position: " + mMaxUpdatePosition);
			*/
		}
		
		/* Does not work properly...
		synchronized(mNewRotation) {
			javax.vecmath.Quat4f rot = _event.getRotation();
			mNewRotation.set(rot.x, rot.y, rot.z, rot.w);
		}
		*/
	}
	
	public void collision(PhysicsCollisionEvent _event) {
		
		if (mConnection != null && mCurrentControlledObject != null)
		{
			//System.out.println("Object picked up: " + mCurrentControlledObject.getName());
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
				// we dont wanna pick the ground
				/*if (_event.getNodeB().getName().equals("Ground"))
				{
					return;
				}*/
				
				mLastTouchedObject = (BoxObject) _event.getNodeB();
				mLocalControllerCollisionPoint = _event.getLocalPointA().clone();
				mLocalTouchedObjectCollisionPoint = _event.getLocalPointB().clone();
			}
		} else if (mController.getName().equals(_event.getNodeB().getName())) {
			if (_event.getNodeA() instanceof BoxObject) {
				// we dont wanna pick the ground
				/*if (_event.getNodeA().getName().equals("Ground"))
				{
					return;
				}*/
				
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

	
	// Kinect finger tracker input
	public void onLeftHandMove(FingerBaseEvent _evt) {
		//System.out.println("Left hand Move: " + _evt.getPosition());
		if (MOUSE_INPUT) {
			return;
		}
			
		if (KINECT_INVERT_HANDS) {
			updatePosition(_evt);
		}
	}

	public void onRightHandMove(FingerBaseEvent _evt) {
		//System.out.println("Right hand Move: " + _evt.getPosition());
		if (MOUSE_INPUT) {
			return;
		}
		
		if (!KINECT_INVERT_HANDS) {
			updatePosition(_evt);
		}
	}

	public void onLeftHandDragStart(FingerDragEvent evt) {
		if (MOUSE_INPUT) {
			return;
		}
		
		if (KINECT_INVERT_HANDS) {
			pickupObject();
		}
		
		//System.out.println("Lef hand drag start: ");
	}

	public void onRightHandDragStart(FingerDragEvent evt) {
		if (MOUSE_INPUT) {
			return;
		}
		
		if (!KINECT_INVERT_HANDS) {
			pickupObject();
		}
		
		//System.out.println("Right hand drag start: ");
	}

	public void onLeftHandDragEnd(FingerDragEvent evt) {
		if (MOUSE_INPUT) {
			return;
		}
		
		if (KINECT_INVERT_HANDS) {
			dropObject();
		}
		
		//System.out.println("Left hand drag end: ");
	}

	public void onRightHandDragEnd(FingerDragEvent evt) {
		if (MOUSE_INPUT) {
			return;
		}
		
		if (!KINECT_INVERT_HANDS) {
			dropObject();
		}
		
		//System.out.println("Right hand drag end: ");
	}

	public void onLeftHandDrag(FingerDragEvent evt) {
		//System.out.println("Left hand drag: ");
	}

	public void onRightHandDrag(FingerDragEvent evt) {
		//System.out.println("Right hand drag: ");
	}
	
}
