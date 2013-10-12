package de.hsbremen.powerwall.kinect.listener;

import de.hsbremen.powerwall.kinect.events.FingerBaseEvent;

public interface ClickListener {
	public void onLeftClick(FingerBaseEvent evt);
	public void onRightClick(FingerBaseEvent evt);
	
	public void onLeftPress(FingerBaseEvent evt);
	public void onRightPress(FingerBaseEvent evt);
	
	public void onLeftRelease(FingerBaseEvent evt);
	public void onRightRelease(FingerBaseEvent evt);
}
