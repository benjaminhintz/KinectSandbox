package com.threegear.gloveless.network;

import javax.vecmath.Quat4f;
import javax.vecmath.Tuple3f;

/**
 * Messages relating to pressing, releasing, dragging and moving of each hand.
 * <p>
 * Pinch messages are always a single hand at a time.  That is, if 
 * the user pinches left and right simultaneously, you will still get 
 * two separate PRESS messages (one for the right and one for the left).
 * As a result, there is less lag for pinch messages (which can be reported
 * as soon as they happen) than for the "higher-level" {@link BimanualPinchMessage}
 * messages.  
 * <p>
 * Note that we do not recommend responding to both the PinchMessage 
 * and the {@link BimanualPinchMessage}; applications should generally
 * pick one or the other.  Responding to both can lead to very 
 * confusing interactions (since most pinches will be detected at least
 * twice).  
 */
public class PinchMessage extends BasicMessage {

  protected Hand hand;
  
  /** @return Which hand the message refers to. */
  public Hand getHand() { return hand; }
  
  protected PinchMessage(BasicMessage message, Hand hand) {
    super(message);
    this.hand = hand;
  }

  public PinchMessage(MessageType type, Hand hand, Tuple3f positionLeft,
      Quat4f rotationLeft, int clickCountLeft, Tuple3f positionRight,
      Quat4f rotationRight, int clickCountRight) {
    super(type, positionLeft, rotationLeft, clickCountLeft,
        positionRight, rotationRight, clickCountRight);
    this.hand = hand;
  }
  
  public String serialize() {
    StringBuffer buffer = new StringBuffer();
    buffer.append(super.serialize());
    buffer.append(' ');
    buffer.append(hand.toString());
    return buffer.toString();
  }

  public static HandTrackingMessage deserialize(String data) {
    String[] result = data.split(" ");
    ParseResult parseResult = parse(result);
    return new PinchMessage(parseResult.message, 
        Hand.valueOf(result[parseResult.parsed]));
  }

}
