package de.hsbremen.powerwall.kinect.listener;

import de.hsbremen.powerwall.kinect.events.FingerDragEvent;

public interface DragListener {	
	public void onLeftHandDragStart(FingerDragEvent evt);
	public void onRightHandDragStart(FingerDragEvent evt);
	
	public void onLeftHandDragEnd(FingerDragEvent evt);
	public void onRightHandDragEnd(FingerDragEvent evt);
	
	public void onLeftHandDrag(FingerDragEvent evt);
	public void onRightHandDrag(FingerDragEvent evt);
}
