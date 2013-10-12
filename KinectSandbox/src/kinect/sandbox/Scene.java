/*
 * TODO: Licence
 */
package kinect.sandbox;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.SkyFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Scene class which creates the actual Sandbox with all its objects in it
 * @author Jan Schulte
 */
public class Scene  implements ActionListener {
	
	// constants
	public static final float		GROUND_SIZE = 40.0f; 

	public static final float		BOX_SIZE_MIN = 0.4f; 
	public static final float		BOX_SIZE_MAX = 1.0f; 
	public static final float		BOX_MASS = 4.0f; 
	public static final int			BOX_COUNT = 20; 
	public static final float		BOX_SPAWN_HEIGHT = 8.0f; 
	
	public static final float		CONTROLLER_SIZE = 1.4f; 
	public static final float		CONTROLLER_MASS = 0.0f; 
	public static final float		CONTROLLER_MIN_HEIGHT = 1.6f; 
	public static final float		CONTROLLER_MAX_HEIGHT = 100.0f; 
	
	// global objects
	private SimpleApplication		mApplication;
    private BulletAppState			mBulletAppState;
	private KinectController		mKinectController;
	private StereoCamera			mCamera;
	
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
		mBulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
		mApplication.getStateManager().attach(mBulletAppState);
		//mBulletAppState.getPhysicsSpace().enableDebug(mApplication.getAssetManager());
				
		// create Scene
		create();
	}
       
    private void create() {
		setupScene();
		createGround();
		createBoxes();
		createController();
	}
	
	public void reset() {
		int minGroundSize = -(int)(GROUND_SIZE / 2.0f);
		int maxGroundSize = (-1) * minGroundSize;
		
		for (SceneObject object : mObjects) {
			// spawn box at random location
			int x = randInt(minGroundSize, maxGroundSize);
			int z = randInt(minGroundSize, maxGroundSize);
			
			object.setPosition(new Vector3f(x, BOX_SPAWN_HEIGHT, z));
			object.getController().activate();
		}
	}
	
	public void update(float _delta) {
		
		if (mKinectController != null) {
			mKinectController.update(_delta);
		}
	}
	
	private void setupScene() {
		// get global objects
		AssetManager assetManager = mApplication.getAssetManager();
		Node rootNode = mApplication.getRootNode();
		ViewPort viewPort = mApplication.getViewPort();
		InputManager inputManager = mApplication.getInputManager();
		
		//init input
		inputManager.addMapping("reset", new KeyTrigger(KeyInput.KEY_R));
		inputManager.addListener(this, "reset");
		
		// add skysphere
		Texture west = assetManager.loadTexture("Textures/skybox/lefttron.jpg");
		Texture east =  assetManager.loadTexture("Textures/skybox/righttron.jpg");
		Texture north = assetManager.loadTexture("Textures/skybox/fronttron.jpg");
		Texture south =  assetManager.loadTexture("Textures/skybox/backtron.jpg");
		Texture up =  assetManager.loadTexture("Textures/skybox/uptron.jpg");
		Texture down =  assetManager.loadTexture("Textures/skybox/downtron.jpg");
		rootNode.attachChild(SkyFactory.createSky(assetManager, west, east, north, south, up, down));
		
		// create ambient light
		AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.3f));
        rootNode.addLight(al);
		
		FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
		
		// Directional Light
		mSunLight = new DirectionalLight();
		mSunLight.setColor(ColorRGBA.White);
		mSunLight.setDirection(new Vector3f(0.0f, -1.0f, -1.0f).normalizeLocal());
		mApplication.getRootNode().addLight(mSunLight);
		
		// setup shadow renderer
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
		
		// glow filter
		BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
		bloom.setBloomIntensity(4.0f);
		bloom.setBlurScale(1.0f);
		fpp.addFilter(bloom);
	
        viewPort.addProcessor(fpp);
	}
	
	private void createGround() {
		// get global objects
		AssetManager assetManager = mApplication.getAssetManager();
		Node rootNode = mApplication.getRootNode();
		
		// create ground & add to scene
		Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		
		Texture diffuseTexture = assetManager.loadTexture("Textures/skybox/downtron.jpg");
		//Texture diffuseTexture = assetManager.loadTexture("Textures/ground_02.png");
		//Texture normalTexture = assetManager.loadTexture("Textures/ground_02_n.png");
		//diffuseTexture.setMinFilter(Texture.MinFilter.Trilinear);
		//diffuseTexture.setMagFilter(Texture.MagFilter.Bilinear);
		//diffuseTexture.setAnisotropicFilter(16);
		diffuseTexture.setWrap(WrapMode.Repeat);
		//normalTexture.setWrap(WrapMode.Repeat);

		material.setTexture("DiffuseMap", diffuseTexture);
		//material.setTexture("NormalMap", normalTexture);
		material.setBoolean("UseMaterialColors",true);    
		material.setColor("Diffuse",ColorRGBA.White);
		material.setColor("Specular",ColorRGBA.White);
		material.setFloat("Shininess", 64f);  // [0,128]
		
		BoxObject ground = new BoxObject("Ground", new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(GROUND_SIZE, 0.1f, GROUND_SIZE), material, 0.0f, new Vector2f(4.0f, 4.0f));
		addToScene(ground);
	}
	
	
	private void createBoxes() {
		// get global objects
		AssetManager assetManager = mApplication.getAssetManager();
		
		// create ground & add to scene
		/*
		Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Texture diffuseTexture = assetManager.loadTexture("Textures/crate_02.png");
		material.setTexture("ColorMap", diffuseTexture);
		*/

		Texture diffuseTexture_01 = assetManager.loadTexture("Textures/crate_02.png");
		Texture normalTexture_01 = assetManager.loadTexture("Textures/crate_02_n.png");
		Texture diffuseTexture_02 = assetManager.loadTexture("Textures/crate_03.png");
		Texture normalTexture_02 = assetManager.loadTexture("Textures/crate_03_n.png");
		
		//diffuseTexture.setMinFilter(Texture.MinFilter.Trilinear);
		//diffuseTexture.setMagFilter(Texture.MagFilter.Bilinear);
		//diffuseTexture.setAnisotropicFilter(16);
		diffuseTexture_01.setWrap(WrapMode.Repeat);
		normalTexture_01.setWrap(WrapMode.Repeat);
		diffuseTexture_02.setWrap(WrapMode.Repeat);
		normalTexture_02.setWrap(WrapMode.Repeat);
		
		int minGroundSize = -(int)(GROUND_SIZE / 2.0f);
		int maxGroundSize = (-1) * minGroundSize;
		
		for (int i = 0; i < BOX_COUNT; ++i) {
			int textureId = randInt(0, 1);
			
			Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
			if (textureId == 0)
			{
				material.setTexture("DiffuseMap", diffuseTexture_01);
				material.setTexture("NormalMap", normalTexture_01);
			}
			else
			{
				material.setTexture("DiffuseMap", diffuseTexture_02);
				material.setTexture("NormalMap", normalTexture_02);
			}
			material.setBoolean("UseMaterialColors",true);    
			material.setColor("Diffuse",ColorRGBA.White);
			material.setColor("Specular",ColorRGBA.White);
			material.setFloat("Shininess", 128f);  // [0,128]
			
			// spawn box at random location
			int x = randInt(minGroundSize, maxGroundSize);
			int z = randInt(minGroundSize, maxGroundSize);
			float size = randFloat(BOX_SIZE_MIN, BOX_SIZE_MAX);
			
			BoxObject ground = new BoxObject("box_" + i, new Vector3f(x, BOX_SPAWN_HEIGHT, z), new Vector3f(size, size, size), material, BOX_MASS * size, null);
			addToScene(ground);		
		}
	}
	
	private void createController() {
		// get global objects
		AssetManager assetManager = mApplication.getAssetManager();
		
		// create sphere based controller
		Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		material.setColor("Color", new ColorRGBA(0.2f, 0.2f, 1.0f, 0.7f));
		material.setColor("GlowColor", ColorRGBA.Blue);
		material.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
				
		SphereObject controller = new SphereObject("Controller", new Vector3f(0.0f, 1.0f, 6.0f), CONTROLLER_SIZE, material, CONTROLLER_MASS);
		controller.setQueueBucket(Bucket.Transparent); 
		addToScene(controller);
		mBulletAppState.getPhysicsSpace().add(controller.getGhostController());
				
		// create camera
		mCamera = new StereoCamera(mApplication, controller);
		
		// create kinect controller
		mKinectController = new KinectController(mApplication, mBulletAppState, controller, mCamera);
		mBulletAppState.getPhysicsSpace().addCollisionListener(mKinectController);
	}
	
	
	private void addToScene(SceneObject _object) {
		mApplication.getRootNode().attachChild(_object);
		mBulletAppState.getPhysicsSpace().add(_object.getController());
		
		if (!_object.getName().equals("Controller") && !_object.getName().equals("Ground")) {
			mObjects.add(_object);
		}
	}

	public int randInt(int _min, int _max) {
		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		return mRandom.nextInt((_max - _min) + 1) + _min;
	}
	
	public float randFloat(float _min, float _max) {
		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		return (float) (_min + (Math.random() * ((_max - _min) + 1.0f)));
	}
	
	public void onAction(String _name, boolean _isPressed, float _tpf) {
		if (_name.equals("reset") && _isPressed) {
			reset();
        }
	}
	
}
