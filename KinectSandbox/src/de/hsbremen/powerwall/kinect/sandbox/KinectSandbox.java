/*
The MIT License (MIT)

Copyright (c) 2013 Jan Schulte

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package de.hsbremen.powerwall.kinect.sandbox;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;

/**
 * KinectSandbox Main Unit
 * @author Jan Schulte
 */
public class KinectSandbox extends SimpleApplication {
	// Members
	private Scene		mScene;
	
	
    /**
     * Create the Application
     */
    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
	
		// NOTE: Hier entsprechende Bildgröße einstellen!
		settings.setResolution(1000, 800);
		//settings.setResolution(1400, 800);
		
        settings.setBitsPerPixel(32);
        settings.setFullscreen(false);
		settings.setTitle("HS-Bremen Powerwall - Kinect Sandbox (c) Jan Schulte");
		//settings.setVSync(true);
		
        KinectSandbox app = new KinectSandbox();
        app.setSettings(settings);
        app.setShowSettings(false);
		app.setDisplayStatView(false);
        app.start();
    }

    /**
     * Initialize the Application
     */
    @Override
    public void simpleInitApp() {
        mScene = new Scene(this);
    }

    @Override
    public void simpleUpdate(float tpf) {
       if (mScene != null) {
		   mScene.update(tpf);
	   }
    }

    @Override
    public void simpleRender(RenderManager rm) {
      
    }
}
