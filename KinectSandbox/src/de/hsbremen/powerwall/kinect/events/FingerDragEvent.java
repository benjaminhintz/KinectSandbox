package de.hsbremen.powerwall.kinect.events;

import javax.vecmath.Vector3f;

public class FingerDragEvent {

		private Vector3f dragStart;
		private Vector3f dragPosition;
		
		public FingerDragEvent(Vector3f dragStart, Vector3f dragPosition) {
			this.dragStart = dragStart;
			this.dragPosition = dragPosition;
		}
		
		public Vector3f getDragPosition() {
			return dragPosition;
		}
		
		public Vector3f getDragStart() {
			return dragStart;
		}
}
