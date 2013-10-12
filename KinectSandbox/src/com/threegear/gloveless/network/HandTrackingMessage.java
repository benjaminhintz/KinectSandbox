package com.threegear.gloveless.network;


import javax.vecmath.Quat4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

/**
 * Base class for all hand-tracking messages / events. 
 */
public abstract class HandTrackingMessage {
  
  /** Number of joints in the skeleton model; currently 17 (1 for the global transform, 
   *  one for the wrist, and 3 each for the 5 fingers). */
  public static final int N_JOINTS = 17;
  
  /** Number of fingers in the skeleton model; currently assumed to be five. */ 
  public static final int N_FINGERS = 5;
  
  /** Number of hands we can track. */
  public static final int N_HANDS = 2;
  
  /** Number of recognized poses. */
  public static final int N_POSES = 7;
  
  /** The different poses that are currently recognized by the tracking software. */
  public enum HandPose {
    /** The hand curled up, ready to pinch. */
	HAND_CURLED(0),
	/** The sign language "L" shape. */
    HAND_ELL(1),            
    /** The "okay" symbol; thumb and index touching and other three fingers spread. */
    HAND_OKAY(2),           
    /** Thumb and forefinger touching. */
    HAND_PINCH(3),          
    /** Index finger point. */
    HAND_POINTING(4),       
    /** What we consider a "neutral" pose, with fingers slightly curled. */
    HAND_RELAXEDOPEN(5),    
    /** All five fingers spread. */
    HAND_SPREAD(6);         

    private int id;

    HandPose(int id) {
      this.id = id;
    }

    public int id() {
      return id;
    }
  }
  
  /** Most events (like click events) only apply to
    * one hand at a time, but simultaneous click and
    * release events will have the BOTH_HANDS identifier set.
    */
  public enum Hand {
    LEFT(0),
    RIGHT(1),
    BOTH(2);
    
    private int id;
    
    Hand(int id) {
      this.id = id;
    }
    
    public int id() {
      return id;
    }

    public static Hand fromId(int id) {
      switch (id) {
      case 0: return LEFT;
      case 1: return RIGHT;
      case 2: return BOTH;
      }
      return null;
    }
    
    public Hand opposite() {
      if (id == 0) return RIGHT;
      if (id == 1) return LEFT;
      if (id == 2) return BOTH;
      return null; // can't get here
    }
  }
  
  /** An enum describing the message type.  Useful for distinguishing between
   *  PRESSED and RELEASED, which are both PinchMessages.  
   */
  public enum MessageType {
    /** The WelcomeMessage provides server and protocol versions */
    WELCOME, 

    /** The UserMessage provides the user profile name and skinning information of each hand */
    USER, 
    
    /** The PoseMessage provides full skeleton and finger-tip information of each hand */
    POSE, 
    
    /** User pinched thumb and index finger together */
    PRESSED, 
    /** User moved hand while holding a pinch */
    DRAGGED, 
    /** User released the pinch */
    RELEASED, 
    /** User moved without pinching */
    MOVED, 

    /** User pinched thumb and index finger of both hands at (approximately) the same time. */
    SIMULTANEOUSLY_PRESSED,
    /** User pinched thumb and index finger of one hand (compare to SIMULTANEOUSLY_PRESSED). */
    INDIVIDUALLY_PRESSED,
    /** User released thumb and index finger of both hands at the same time. */
    SIMULTANEOUSLY_RELEASED,
    /** User released thumb and index finger of one hand (compare to SIMULTANEOUSLY_RELEASED). */
    INDIVIDUALLY_RELEASED, 
    /** User moved hands while both were pinching. */
    DRAGGED_BIMANUAL,

    /** User pointed at something with the index finger. */
    POINT,
    
    /** Obsolete; will be removed in the next version. */
    CALIBRATING
  }
  
  /**
   * Factory method that parses a HandTrackingMessage from a string
   * 
   * @param data
   * @return the parsed hand tacking message or null if parsing fails
   */
  public static HandTrackingMessage deserialize(String data) {
    // Read up until the first space
    if (data.indexOf(" ") == -1) return null;
    String firstToken = data.substring(0, data.indexOf(" "));
    
    try {
      switch (MessageType.valueOf(firstToken)) {
      case WELCOME: return WelcomeMessage.deserialize(data);
      case USER: return UserMessage.deserialize(data);
      case POSE: return PoseMessage.deserialize(data);
      case PRESSED: 
      case DRAGGED: 
      case RELEASED:  
      case MOVED: return PinchMessage.deserialize(data);
      case SIMULTANEOUSLY_PRESSED:
      case INDIVIDUALLY_PRESSED:
      case SIMULTANEOUSLY_RELEASED:
      case INDIVIDUALLY_RELEASED:
      case DRAGGED_BIMANUAL: return BimanualPinchMessage.deserialize(data);
      case POINT: return PointMessage.deserialize(data);
      case CALIBRATING: return CalibrationMessage.deserialize(data);
      }
    } catch (IllegalArgumentException e) {
      System.err.println("Couldn't parse message type: " + data);
    }
    return null;
  }

  /**
   * Structure holding the position, rotational frame and other information
   * relevant to the state of the hand.
   */
  public static class HandState {
    private Vector3f position;
    
    private Quat4f rotation;
    
    private int clickCount;
    
    public HandState(Tuple3f position, Quat4f rotation, int clickCount) {
      this.position = new Vector3f(position);
      this.rotation = new Quat4f(rotation);
      this.clickCount = clickCount;
    }
    
    /** Position estimate for the hand.
     *   <p>
     *   This is defined by the rigid frame given by the back of
     *   the hand, so it will be considerably more stable than
     *   the individual finger position estimates given by the PoseMessage.
     *   
     * @return Current estimate of the global hand position.
     */
    public Vector3f getPosition() { return position; }
    
    /** Rotation estimate for the hand.
     *   <p>
     *   This is defined by the rigid frame given by the back of
     *   the hand, so it will be considerably more stable than
     *   the individual finger position estimates given by the PoseMessage.
     *   
     * @return Current estimate of the global hand rotation.
     */
    public Quat4f getRotation() { return rotation; }
    
    /** Number of consecutive pinches. 
     * 
     * @return Returns 1 if the user single-clicked, 2 if the user double-
     *  clicked, and 0 if neither.
     * 
     */
    public int getClickCount() { return clickCount; }
  }
  
  /**
   * @return the type of the message
   */
  public abstract MessageType getType();
  
  /** Writes the message to a string, suitable for sending over the network.
   *  <p>
   *  Developers shouldn't need to worry about this unless they plan on writing
   *  their own networking code.
   */
  public abstract String serialize();
}
