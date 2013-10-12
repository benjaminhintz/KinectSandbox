/**
 * 
 * Uses a simple network protocol to communicate with
 * your applications over a socket.  This API communicates with the server
 * and wraps the results out using Java classes.
 * <p>
 * The library operates using callbacks.  To get started, you'll need to 
 * derive from HandTrackingListener.  You can then create a 
 * HandTrackingClient which will connect to the server start up its own thread
 * which waits and passes messages (PinchMessage, PointMessage, etc.) back to
 * your HandTrackingListener via the callback interface.  
 * <p>
 * Each HandTrackingMessage contains the position and orientation of both hands. 
 * The coordinate space of the hand position is specified by the camera setup
 * stage.  For a standard camera setup, the x-axis points right; the y-axis points
 * up; and the z-axis points away from the screen. Units are in millimeters and
 * the origin is at the center of the checkerboard you used during calibration.
 * 
 */
package com.threegear.gloveless.network;
