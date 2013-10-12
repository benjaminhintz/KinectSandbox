package com.threegear.gloveless.network;

import javax.vecmath.Quat4f;
import javax.vecmath.Tuple3f;

/**
 * Messages relating to simultaneous or individual pinching.
 * <p>
 * Bimanual pinch messages are a ``higher-level'' message than the regular
 * pinch message, and can be used to distinguish whether one hand pinched
 * or whether both pinched simultaneously.  If the user presses both hands
 * at the same time, for example, you will only get a single Bimanual
 * SIMULTANEOUSLY_PRESSED message (instead of two PRESSED messages, as you
 * would with the PinchMessage). The cost
 * of this higher-level knowledge is a bit of additional lag: to determine
 * whether two pinches happen at the same time we have to wait 100ms or so 
 * (since the pinches are never going to be _exactly_ simultaneous).  You 
 * can decide whether the added functionality is worth the lag and choose which
 * of the two interfaces you prefer.  
 * <p>
 * Note that we do not recommend responding to both the BimanualPinchMessage
 * and the {@link PinchMessage}; applications should generally
 * pick one or the other.  Responding to both can lead to very 
 * confusing interactions (since most pinches will be detected at least
 * twice).  
 * <p>
 * To help understanding the difference between the PinchMessage and the
 * BimanualPinchMessage, consider the following two scenarios:
 * <p>
 * First, suppose the user presses her left hand and then, a second later,
 * her right hand.  You will receive the following sequence of events:
 *    <ul>
 *    <li> PinchMessage(PRESSED, left hand)
 *    <li> ...
 *    <li> BimanualPinchMessage(INDIVIDUALLY_PRESSED, left hand)
 *    <li> ...
 *    <li> PinchMessage(PRESSED, right hand)
 *    <li> ...
 *    <li> BimanualPinchMessage(INDIVIDUALLY_PRESSED, right hand)
 *    </ul>
 * Note how the BimanualPinchMessage always lags slightly behind the 
 * PinchMessage (you will receive it a couple frames later).  
 * Now, suppose the user presses her left and right hands simultaneously.
 *    <ul>
 *    <li> PinchMessage(PRESSED, left hand)
 *    <li> PinchMessage(PRESSED, right hand)
 *    <li> BimanualPinchMessage(SIMULTANEOUSLY_PRESSED, both hands)
 *    </ul>
 * In the case of the double pinch, it can be reported immediately. 
 */
public class BimanualPinchMessage extends BasicMessage {

  protected Hand hand;

  /** @return which hand this message applies to (could be BOTH, in the case
   *  of a simultaneous pinch/release).  
   */
  public Hand getHand() { return hand; }
  
  protected BimanualPinchMessage(BasicMessage message, Hand hand) {
    super(message);
    this.hand = hand;
  }
  
  public BimanualPinchMessage(MessageType type, Hand hand, Tuple3f positionLeft,
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
    return new BimanualPinchMessage(parseResult.message, 
        Hand.valueOf(result[parseResult.parsed]));
  }
}
