/*
 * TODO: Licence
 */
package kinect.sandbox;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.SpotLightShadowFilter;
import com.jme3.shadow.SpotLightShadowRenderer;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Scene class which creates the actual Sandbox with all its objects in it
 * @author Jan Schulte
 */
public class Scene {
	
	// constants
	public static final float		GROUND_SIZE = 10.0f; 
	
	public static final float		BOX_SIZE = 0.2f; 
	public static final float		BOX_MASS = 1.0f; 
	public static final int			BOX_COUNT = 30; 
	
	// global objects
	private SimpleApplication		mApplication;
    private BulletAppState			mBulletAppState;
			
	// Scene objects
	private DirectionalLight		mSunLight;			
	private List<SceneObject>		mObjects = new ArrayList<SceneObject>();
	
    // helper objects
	private Random					mRandom = new Random();
	
	/**
	 * Scene Contructor
	 * @param _application 
	 */
    public Scene(SimpleApplication _application) {
		mApplication = _application;
		
		// create jBullet Physics
		mBulletAppState = new BulletAppState();
		mApplication.getStateManager().attach(mBulletAppState);
		//mBulletAppState.getPhysicsSpace().enableDebug(mApplication.getAssetManager());
				
		// create Scene
		create();
	}
       
    private void create() {
		setupScene();
		createGround();
		createBoxes();
	}
	
	public void reset() {
		
	}
	 
	private void setupScene() {
		// get global objects
		AssetManager assetManager = mApplication.getAssetManager();
		Node rootNode = mApplication.getRootNode();
		ViewPort viewPort = mApplication.getViewPort();

		// create ambient light
		AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.3f));
        rootNode.addLight(al);
		
		// setup shadow renderer
		FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
		
		// Directional Light
		mSunLight = new DirectionalLight();
		mSunLight.setColor(ColorRGBA.White);
		mSunLight.setDirection(new Vector3f(0.0f, -1.0f, -1.0f).normalizeLocal());
		mApplication.getRootNode().addLight(mSunLight);
		
		/*
		final int SHADOWMAP_SIZE = 1024;
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 3);
        dlsr.setLight(mSunLight);
		dlsr.setLambda(0.55f);
        dlsr.setShadowIntensity(0.6f);
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.Bilinear);
        //dlsr.displayDebug();
        viewPort.addProcessor(dlsr);
 
        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, SHADOWMAP_SIZE, 3);
        dlsf.setLight(mSunLight);
        dlsf.setEnabled(true);
		dlsf.setLambda(0.55f);
        dlsf.setShadowIntensity(0.6f);
        dlsf.setEdgeFilteringMode(EdgeFilteringMode.Bilinear);
        fpp.addFilter(dlsf);
        */
	
		/* Spot Light
		SpotLight spot = new SpotLight();
		spot.setSpotRange(100f);                           // distance
		spot.setSpotInnerAngle(15f * FastMath.DEG_TO_RAD); // inner light cone (central beam)
		spot.setSpotOuterAngle(35f * FastMath.DEG_TO_RAD); // outer light cone (edge of the light)
		spot.setColor(ColorRGBA.White.mult(1.3f));         // light color
		spot.setPosition(mApplication.getCamera().getLocation());               // shine from camera loc
		spot.setDirection(mApplication.getCamera().getDirection());             // shine forward from camera loc
		rootNode.addLight(spot);

		SpotLightShadowRenderer slsr = new SpotLightShadowRenderer(assetManager, 512);
        slsr.setLight(spot);      
        slsr.setShadowIntensity(0.5f);
        slsr.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);  
        viewPort.addProcessor(slsr);

        SpotLightShadowFilter slsf = new SpotLightShadowFilter(assetManager, 512);
        slsf.setLight(spot);    
        slsf.setShadowIntensity(0.5f);
        slsf.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);  
        slsf.setEnabled(false);
		fpp.addFilter(slsf);
		*/
		
        //viewPort.addProcessor(fpp);

	}
	
	private void createGround() {
		// get global objects
		AssetManager assetManager = mApplication.getAssetManager();
		Node rootNode = mApplication.getRootNode();
		
		// create ground & add to scene
		Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		Texture diffuseTexture = assetManager.loadTexture("Textures/ground_02.png");
		Texture normalTexture = assetManager.loadTexture("Textures/ground_02_n.png");
		//diffuseTexture.setMinFilter(Texture.MinFilter.Trilinear);
		//diffuseTexture.setMagFilter(Texture.MagFilter.Bilinear);
		//diffuseTexture.setAnisotropicFilter(16);
		diffuseTexture.setWrap(WrapMode.Repeat);
		normalTexture.setWrap(WrapMode.Repeat);

		material.setTexture("DiffuseMap", diffuseTexture);
		material.setTexture("NormalMap", normalTexture);
		material.setBoolean("UseMaterialColors",true);    
		material.setColor("Diffuse",ColorRGBA.White);
		material.setColor("Specular",ColorRGBA.White);
		material.setFloat("Shininess", 128f);  // [0,128]
		
		BoxObject ground = new BoxObject("Ground", new Vector3f(0.0f, -4.0f, 0.0f), new Vector3f(GROUND_SIZE, 1.0f, GROUND_SIZE), material, 0.0f, new Vector2f(3.0f, 3.0f));
		addToScene(ground);		
	}
	
	
	private void createBoxes() {
		// get global objects
		AssetManager assetManager = mApplication.getAssetManager();
		Node rootNode = mApplication.getRootNode();
		
		// create ground & add to scene
		/*
		Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Texture diffuseTexture = assetManager.loadTexture("Textures/crate_02.png");
		material.setTexture("ColorMap", diffuseTexture);
		*/
				
		
		Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		Texture diffuseTexture = assetManager.loadTexture("Textures/crate_02.png");
		Texture normalTexture = assetManager.loadTexture("Textures/crate_02_n.png");
		//diffuseTexture.setMinFilter(Texture.MinFilter.Trilinear);
		//diffuseTexture.setMagFilter(Texture.MagFilter.Bilinear);
		//diffuseTexture.setAnisotropicFilter(16);
		diffuseTexture.setWrap(WrapMode.Repeat);
		normalTexture.setWrap(WrapMode.Repeat);
		
		material.setTexture("DiffuseMap", diffuseTexture);
		material.setTexture("NormalMap", normalTexture);
		material.setBoolean("UseMaterialColors",true);    
		material.setColor("Diffuse",ColorRGBA.White);
		material.setColor("Specular",ColorRGBA.White);
		material.setFloat("Shininess", 128f);  // [0,128]
		
		int minGroundSize = -(int)(GROUND_SIZE / 2.0f);
		int maxGroundSize = (-1) * minGroundSize;
		
		for (int i = 0; i < BOX_COUNT; ++i) {
			int x = randInt(minGroundSize, maxGroundSize);
			int z = randInt(minGroundSize, maxGroundSize);
			
			BoxObject ground = new BoxObject("box_" + i, new Vector3f(x, 1.0f, z), new Vector3f(BOX_SIZE, BOX_SIZE, BOX_SIZE), material, BOX_MASS, null);
			addToScene(ground);		
		}
	}
	
	
	private void addToScene(SceneObject _object) {
		mApplication.getRootNode().attachChild(_object);
		mBulletAppState.getPhysicsSpace().add(_object.getPhyscsController());
		mObjects.add(_object);
	}

	
	public int randInt(int min, int max) {
		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		return mRandom.nextInt((max - min) + 1) + min;
	}
	
}
