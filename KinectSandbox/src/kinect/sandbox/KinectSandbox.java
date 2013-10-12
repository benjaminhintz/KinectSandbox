/*
 * TODO: Licence
 */
package kinect.sandbox;

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
        settings.setResolution(1280, 768);
		//settings.setResolution(1000, 800);
        settings.setBitsPerPixel(32);
        settings.setFullscreen(false);

        KinectSandbox app = new KinectSandbox();
        app.setSettings(settings);
        app.setShowSettings(false);
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
