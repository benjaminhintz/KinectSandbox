package de.hsbremen.powerwall.kinect.listener;

import de.hsbremen.powerwall.kinect.events.FingerBaseEvent;

public interface MoveListener {
	public void onLeftHandMove(FingerBaseEvent evt);
	public void onRightHandMove(FingerBaseEvent evt);
}
