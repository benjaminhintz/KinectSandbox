package de.hsbremen.powerwall.kinect.events;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class FingerBaseEvent {
	private Vector3f position;
	private Quat4f rotation;
	
	public FingerBaseEvent(Vector3f position, Quat4f rotation) {
		this.position = position;
		this.rotation = rotation;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public Quat4f getRotation() {
		return rotation;
	}
}
