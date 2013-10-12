package de.hsbremen.powerwall.kinect;

import java.io.IOException;
import java.util.ArrayList;

import javax.vecmath.Vector3f;

import com.threegear.gloveless.network.HandTrackingAdapter;
import com.threegear.gloveless.network.HandTrackingClient;
import com.threegear.gloveless.network.HandTrackingMessage;
import com.threegear.gloveless.network.HandTrackingMessage.Hand;
import com.threegear.gloveless.network.HandTrackingMessage.HandState;
import com.threegear.gloveless.network.HandTrackingMessage.MessageType;
import com.threegear.gloveless.network.PinchMessage;

import de.hsbremen.powerwall.kinect.events.FingerBaseEvent;
import de.hsbremen.powerwall.kinect.events.FingerDragEvent;
import de.hsbremen.powerwall.kinect.listener.ClickListener;
import de.hsbremen.powerwall.kinect.listener.DblClickListener;
import de.hsbremen.powerwall.kinect.listener.DragListener;
import de.hsbremen.powerwall.kinect.listener.MoveListener;

public class FingerTracker {

	public final static String EVENT_LEFT_DRAGGED = "LEFT_DRAG";	
	public final static String EVENT_LEFT_RELEASED = "LEFT_RELEASE";
	public final static String EVENT_LEFT_MOVE = "LEFT_MOVE";
	
	public final static String EVENT_RIGHT_DRAGGED = "RIGHT_DRAG";	
	public final static String EVENT_RIGHT_RELEASED = "RIGHT_RELEASE";
	public final static String EVENT_RIGHT_MOVE = "RIGHT_MOVE";

	private boolean leftHandPress = false;
	private boolean leftHandDrag = false;
	private boolean rightHandPress = false;
	private boolean rightHandDrag = false;

	private Vector3f leftDragStart;
	private Vector3f rightDragStart;

	private long leftPressedWhen;
	private long leftFirstClickWhen;
	private long rightPressedWhen;
	private long rightFirstClickWhen;
	
	private HandTrackingClient client = new HandTrackingClient();

	private ArrayList<ClickListener> clickListenerList = new ArrayList<ClickListener>();
	private ArrayList<DblClickListener> dblClickListenerList = new ArrayList<DblClickListener>();
	private ArrayList<DragListener> dragListenerList = new ArrayList<DragListener>();
	private ArrayList<MoveListener> moveListenerList = new ArrayList<MoveListener>();
	
	public void addClickListener(ClickListener listener) {
		clickListenerList.add(listener);
	}
	
	public void addDblClickListener(DblClickListener listener) {
		dblClickListenerList.add(listener);
	}
	
	public void addDragListener(DragListener listener) {
		dragListenerList.add(listener);
	}
	
	public void addMoveListener(MoveListener listener) {
		moveListenerList.add(listener);
	}
	
	public FingerTracker() throws IOException {
		client.addListener(new HandTrackingAdapter(){
			@Override
			public void handleEvent(HandTrackingMessage message) {
				super.handleEvent(message);
				
//				if(message instanceof PointMessage) {
//					PointMessage msg = (PointMessage)message;
//					System.out.println("direction: " + msg.getDirection());
//					System.out.println("end: " + msg.getEnd());
//				}
				
				if(message instanceof PinchMessage) {
					PinchMessage msg = (PinchMessage)message;
					Hand hand = msg.getHand();
					HandState state = msg.getHandState(hand.id());
					

					/*
					 * 
					 * 
					 * 
					 * 
					 * 
					 * PRESS
					 * 
					 * 
					 * 
					 * 
					 */
					if(msg.getType() == MessageType.DRAGGED) {
						
						if(msg.getHand() == Hand.LEFT) {
							if(!leftHandPress) {
								leftDragStart = msg.getHandState(msg.getHand().id()).getPosition();
								for(ClickListener evt : clickListenerList) {
									evt.onLeftPress(new FingerBaseEvent(state.getPosition(), state.getRotation()));
								}
								leftHandPress = true;
								leftPressedWhen = System.currentTimeMillis();
							}
						}
						if(msg.getHand() == Hand.RIGHT) {
							if(!rightHandPress) {
								rightDragStart = msg.getHandState(msg.getHand().id()).getPosition();
								for(ClickListener evt : clickListenerList) {
									evt.onRightPress(new FingerBaseEvent(state.getPosition(), state.getRotation()));
								}
								rightHandPress = true;
								rightPressedWhen = System.currentTimeMillis();
							}
						}
					}

					/*
					 * 
					 * 
					 * 
					 * 
					 * 
					 * RELEASE
					 * 
					 * 
					 * 
					 * 
					 */
					if(msg.getType() == MessageType.RELEASED) {
						if(msg.getHand() == Hand.LEFT) {
							if(leftHandPress) {
								if(leftHandDrag) {
									leftHandDrag = false;
									
									for(DragListener evt : dragListenerList) {
										evt.onLeftHandDragEnd(new FingerDragEvent(leftDragStart, state.getPosition()));
									}
								}
								for(ClickListener evt : clickListenerList) {
									evt.onLeftRelease(new FingerBaseEvent(state.getPosition(), state.getRotation()));
								}
								
								leftHandPress = false;
								long now = System.currentTimeMillis();
								if(now - leftPressedWhen < 200) {
									if(now - leftFirstClickWhen < 400) {
										for(DblClickListener evt : dblClickListenerList) {
											evt.onLeftDblClick(new FingerBaseEvent(state.getPosition(), state.getRotation()));
										}
									} else {
										leftFirstClickWhen = leftPressedWhen;
										for(ClickListener evt : clickListenerList) {
											evt.onLeftClick(new FingerBaseEvent(state.getPosition(), state.getRotation()));
										}
									}
								}
								
							}
						}
						if(msg.getHand() == Hand.RIGHT) {
							if(rightHandPress) {
								if(rightHandDrag) {
									rightHandDrag = false;
									
									for(DragListener evt : dragListenerList) {
										evt.onRightHandDragEnd(new FingerDragEvent(rightDragStart, state.getPosition()));
									}
								}
								for(ClickListener evt : clickListenerList) {
									evt.onRightRelease(new FingerBaseEvent(state.getPosition(), state.getRotation()));
								}
								rightHandPress = false;
								long now = System.currentTimeMillis();
								if(now - rightPressedWhen < 200) {
									if(now - rightFirstClickWhen < 400) {
										for(DblClickListener evt : dblClickListenerList) {
											evt.onRightDblClick(new FingerBaseEvent(state.getPosition(), state.getRotation()));
										}
									} else {
										rightFirstClickWhen = rightPressedWhen;
										for(ClickListener evt : clickListenerList) {
											evt.onRightClick(new FingerBaseEvent(state.getPosition(), state.getRotation()));
										}
									}
								}
							}
						}
					}
					
					
					/*
					 * 
					 * 
					 * 
					 * 
					 * 
					 * MOVE
					 * 
					 * 
					 * 
					 * 
					 */
					if(msg.getType() == MessageType.MOVED) {
						if(msg.getHand() == Hand.LEFT) {
							if(leftHandPress) {
								if(!leftHandDrag) {
									leftHandDrag = true;
									leftDragStart = state.getPosition();
									// drag start
									for(DragListener evt : dragListenerList) {
										evt.onLeftHandDragStart(new FingerDragEvent(leftDragStart, leftDragStart));
									}
								}
								for(DragListener evt : dragListenerList) {
									evt.onLeftHandDrag(new FingerDragEvent(leftDragStart, state.getPosition()));
								}
							}
							for(MoveListener evt : moveListenerList) {
								evt.onLeftHandMove(new FingerBaseEvent(state.getPosition(), state.getRotation()));
							}
						}
						
						if(msg.getHand() == Hand.RIGHT) {
							if(rightHandPress) {
								if(!rightHandDrag) {
									rightHandDrag = true;
									rightDragStart = state.getPosition();
									// drag start
									for(DragListener evt : dragListenerList) {
										evt.onRightHandDragStart(new FingerDragEvent(rightDragStart, rightDragStart));
									}
								}
								for(DragListener evt : dragListenerList) {
									evt.onRightHandDrag(new FingerDragEvent(rightDragStart, state.getPosition()));
								}
							}
							for(MoveListener evt : moveListenerList) {
								evt.onRightHandMove(new FingerBaseEvent(state.getPosition(), state.getRotation()));
							}
						}
					}
				}
			}
		});
		client.connect();

//		addDragListener(dragListener);
//		addMoveListener(moveListener);
//		addClickListener(clickListener);
//		addDblClickListener(dblClickListener);
	}
	
//	private DragListener dragListener = new DragListener() {
//		
//		@Override
//		public void onRightHandDragStart(FingerDragEvent evt) {
//			System.out.println("right drag start");
//		}
//		
//		@Override
//		public void onRightHandDragEnd(FingerDragEvent evt) {
//			System.out.println("right drag end");
//		}
//		
//		@Override
//		public void onRightHandDrag(FingerDragEvent evt) {
//			System.out.println("right drag");
//		}
//		
//		@Override
//		public void onLeftHandDragStart(FingerDragEvent evt) {
//			System.out.println("left drag start");			
//		}
//		
//		@Override
//		public void onLeftHandDragEnd(FingerDragEvent evt) {
//			System.out.println("left drag end");			
//		}
//		
//		@Override
//		public void onLeftHandDrag(FingerDragEvent evt) {
//			System.out.println("left drag");
//		}
//	};
	
//	private MoveListener moveListener = new MoveListener() {
//		
//		@Override
//		public void onRightHandMove(FingerBaseEvent evt) {
//			System.out.println("right move");
//		}
//		
//		@Override
//		public void onLeftHandMove(FingerBaseEvent evt) {
//			System.out.println("left move");
//		}
//	};
	
//	private ClickListener clickListener = new ClickListener() {
//		
//		@Override
//		public void onRightRelease(FingerBaseEvent evt) {
//			System.out.println("right release");
//		}
//		
//		@Override
//		public void onRightPress(FingerBaseEvent evt) {
//			System.out.println("right press");
//		}
//		
//		@Override
//		public void onRightClick(FingerBaseEvent evt) {
//			System.out.println("right click");
//		}
//		
//		@Override
//		public void onLeftRelease(FingerBaseEvent evt) {
//			System.out.println("left release");
//		}
//		
//		@Override
//		public void onLeftPress(FingerBaseEvent evt) {
//			System.out.println("left press");
//		}
//		
//		@Override
//		public void onLeftClick(FingerBaseEvent evt) {
//			System.out.println("left click");
//		}
//	};
	
//	private DblClickListener dblClickListener = new DblClickListener() {
//		
//		@Override
//		public void onRightDblClick(FingerBaseEvent evt) {
//			System.out.println("right dbl click");
//		}
//		
//		@Override
//		public void onLeftDblClick(FingerBaseEvent evt) {
//			System.out.println("left dbl click");
//		}
//	};
	

	public static void main(String[] args) throws IOException {
		new FingerTracker();
	}
	
}
