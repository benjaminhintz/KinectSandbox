package de.hsbremen.powerwall.kinect.listener;

import de.hsbremen.powerwall.kinect.events.FingerBaseEvent;

public interface DblClickListener {
	public void onLeftDblClick(FingerBaseEvent evt);
	public void onRightDblClick(FingerBaseEvent evt);
}
