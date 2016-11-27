package engine.graph;

import engine.*;
import engine.graph.anim.AnimGameItem;
import engine.graph.anim.AnimatedFrame;
import engine.graph.lights.DirectionalLight;
import engine.graph.lights.PointLight;
import engine.graph.lights.SpotLight;
import engine.graph.particles.IParticleEmitter;
import engine.items.GameItem;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengles.GLES20.GL_FRAMEBUFFER;

/**
 * @author Jake stanger
 * Engine rendering class
 */
public class Renderer
{
	/**
	 * Field of View in Radians
	 */
	private static final float FOV = (float) Math.toRadians(60.0f);
	
	private static final float Z_NEAR = 0.01f, Z_FAR = 1000.f;
	private static final int MAX_POINT_LIGHTS = 5, MAX_SPOT_LIGHTS = 5; //These must be updated in the scene shader too
	
	private final Transformation transformation;
	private ShadowMap shadowMap;
	private ShaderProgram depthShaderProgram, skyBoxShaderProgram, sceneShaderProgram, particlesShaderProgram, hudShaderProgram;
	
	private final float specularPower;
	
	public Renderer()
	{
		transformation = new Transformation();
		specularPower = 10f;
	}
	
	public void init(Window window) throws Exception
	{
		shadowMap = new ShadowMap();
		
		setupDepthShader();
		setupSkyBoxShader();
		setupSceneShader();
		setupParticlesShader();
		setupHudShader();
	}
	
	public void render(Window window, Camera camera, Scene scene, IHud hud)
	{
		clear();
		
		// Render depth map before view ports has been set up
		renderDepthMap(window, camera, scene);
		
		glViewport(0, 0, window.getWidth(), window.getHeight());
		
		//Update projection and view matrices once per render cycle
		transformation.updateProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
		camera.updateViewMatrix();
		
		if(scene != null) renderScene(window, camera, scene);
		if(scene != null && scene.hasSkybox()) renderSkyBox(window, camera, scene);
		if(scene != null) this.renderParticles(window, camera, scene);
		if(hud != null) renderHud(window, hud);
		
		//renderAxes(camera);
		renderCrossHair(window);
	}
	
	private void setupDepthShader() throws Exception
	{
		depthShaderProgram = new ShaderProgram();
		depthShaderProgram.createVertexShader(Utils.loadResource("/shaders/depth_vertex.vs"));
		depthShaderProgram.createFragmentShader(Utils.loadResource("/shaders/depth_fragment.fs"));
		depthShaderProgram.link();
		
		depthShaderProgram.createUniform("isInstanced");
		depthShaderProgram.createUniform("jointsMatrix");
		depthShaderProgram.createUniform("modelLightViewNonInstancedMatrix");
		depthShaderProgram.createUniform("orthoProjectionMatrix");
	}
	
	private void setupSkyBoxShader() throws Exception
	{
		skyBoxShaderProgram = new ShaderProgram();
		skyBoxShaderProgram.createVertexShader(Utils.loadResource("/shaders/skybox_vertex.vs"));
		skyBoxShaderProgram.createFragmentShader(Utils.loadResource("/shaders/skybox_fragment.fs"));
		skyBoxShaderProgram.link();
		
		//Create uniforms for projection matrix
		skyBoxShaderProgram.createUniform("projectionMatrix");
		skyBoxShaderProgram.createUniform("modelViewMatrix");
		skyBoxShaderProgram.createUniform("texture_sampler");
		skyBoxShaderProgram.createUniform("ambientLight");
	}
	
	private void setupSceneShader() throws Exception
	{
		//Create shader
		sceneShaderProgram = new ShaderProgram();
		sceneShaderProgram.createVertexShader(Utils.loadResource("/shaders/scene_vertex.vs"));
		sceneShaderProgram.createFragmentShader(Utils.loadResource("/shaders/scene_fragment.fs"));
		sceneShaderProgram.link();
		
		//Create uniforms for modelView and projection matrices
		sceneShaderProgram.createUniform("projectionMatrix");
		sceneShaderProgram.createUniform("modelViewNonInstancedMatrix");
		sceneShaderProgram.createUniform("texture_sampler");
		sceneShaderProgram.createUniform("normalMap");
		
		sceneShaderProgram.createMaterialUniform("material"); //Create uniform for material
		
		//Create lighting related uniforms
		sceneShaderProgram.createUniform("specularPower");
		sceneShaderProgram.createUniform("ambientLight");
		sceneShaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
		sceneShaderProgram.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
		sceneShaderProgram.createDirectionalLightUniform("directionalLight");
		sceneShaderProgram.createFogUniform("fog");
		
		//Create uniforms for shadow mapping
		sceneShaderProgram.createUniform("shadowMap");
		sceneShaderProgram.createUniform("orthoProjectionMatrix");
		sceneShaderProgram.createUniform("modelLightViewNonInstancedMatrix");
		//sceneShaderProgram.createUniform("renderShadow");
		
		sceneShaderProgram.createUniform("jointsMatrix"); //Create uniform for joint matrices
		
		sceneShaderProgram.createUniform("isInstanced");
		sceneShaderProgram.createUniform("numCols");
		sceneShaderProgram.createUniform("numRows");
		
		sceneShaderProgram.createUniform("selectedNonInstanced");
	}
	
	private void setupParticlesShader() throws Exception
	{
		particlesShaderProgram = new ShaderProgram();
		particlesShaderProgram.createVertexShader(Utils.loadResource("/shaders/particles_vertex.vs"));
		particlesShaderProgram.createFragmentShader(Utils.loadResource("/shaders/particles_fragment.fs"));
		particlesShaderProgram.link();
		
		particlesShaderProgram.createUniform("projectionMatrix");
		particlesShaderProgram.createUniform("texture_sampler");
		
		particlesShaderProgram.createUniform("numCols");
		particlesShaderProgram.createUniform("numRows");
	}
	
	private void setupHudShader() throws Exception
	{
		hudShaderProgram = new ShaderProgram();
		hudShaderProgram.createVertexShader(Utils.loadResource("/shaders/hud_vertex.vs"));
		hudShaderProgram.createFragmentShader(Utils.loadResource("/shaders/hud_fragment.fs"));
		hudShaderProgram.link();
		
		//Create uniforms for Orthographic-model projection matrix and base colour
		hudShaderProgram.createUniform("projModelMatrix");
		hudShaderProgram.createUniform("colour");
		hudShaderProgram.createUniform("hasTexture");
	}
	
	private void renderDepthMap(Window window, Camera camera, Scene scene)
	{
		//Setup view port to match the texture size
		glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
		glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);
		glClear(GL_DEPTH_BUFFER_BIT);
		
		depthShaderProgram.bind();
		
		DirectionalLight light = scene.getSceneLight().getDirectionalLight();
		Vector3f lightDirection = light.getDirection();
		
		float lightAngleX = (float)Math.toDegrees(Math.acos(lightDirection.z));
		float lightAngleY = (float)Math.toDegrees(Math.asin(lightDirection.x));
		float lightAngleZ = 0;
		
		Matrix4f lightViewMatrix = transformation.updateLightViewMatrix(new Vector3f(lightDirection).mul(light.getShadowPosMult()),
				new Vector3f(lightAngleX, lightAngleY, lightAngleZ));
		DirectionalLight.OrthoCoords orthCoords = light.getOrthoCoords();
		Matrix4f orthoProjMatrix = transformation.updateOrthoProjectionMatrix(orthCoords.left, orthCoords.right,
				orthCoords.bottom, orthCoords.top, orthCoords.near, orthCoords.far);
		
		depthShaderProgram.setUniform("orthoProjectionMatrix", orthoProjMatrix);
		
		renderNonInstancedMeshes(scene, depthShaderProgram, null, lightViewMatrix);
		renderInstancedMeshes(scene, true, depthShaderProgram, null, lightViewMatrix);
		
		//Unbind
		depthShaderProgram.unbind();
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	private void renderSkyBox(Window window, Camera camera, Scene scene)
	{
		skyBoxShaderProgram.bind();
			
		skyBoxShaderProgram.setUniform("texture_sampler", 0);
		
		Matrix4f projectionMatrix = transformation.getProjectionMatrix();
		skyBoxShaderProgram.setUniform("projectionMatrix", projectionMatrix);
		Matrix4f viewMatrix = camera.getViewMatrix();
		viewMatrix.m30(0);
		viewMatrix.m31(0);
		viewMatrix.m32(0);
		Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(scene.getSkyBox(), viewMatrix);
		
		skyBoxShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
		skyBoxShaderProgram.setUniform("ambientLight", scene.getSceneLight().getSkyBoxLight());
		
		scene.getSkyBox().getMesh().render();
		
		skyBoxShaderProgram.unbind();
	}
	
	public void renderScene(Window window, Camera camera, Scene scene)
	{
		sceneShaderProgram.bind();
		
		Matrix4f projectionMatrix = transformation.getProjectionMatrix();
		sceneShaderProgram.setUniform("projectionMatrix", projectionMatrix);
		Matrix4f orthoProjMatrix = transformation.getOrthoProjectionMatrix();
		sceneShaderProgram.setUniform("orthoProjectionMatrix", orthoProjMatrix);
		Matrix4f lightViewMatrix = transformation.getLightViewMatrix();
		
		Matrix4f viewMatrix = camera.getViewMatrix();
		
		SceneLight sceneLight = scene.getSceneLight();
		renderLights(viewMatrix, sceneLight);
		
		sceneShaderProgram.setUniform("fog", scene.getFog());
		sceneShaderProgram.setUniform("texture_sampler", 0);
		sceneShaderProgram.setUniform("normalMap", 1);
		sceneShaderProgram.setUniform("shadowMap", 2);
		//sceneShaderProgram.setUniform("renderShadow", scene.isRenderShadows() ? 1 : 0);
		
		renderNonInstancedMeshes(scene, sceneShaderProgram, viewMatrix, lightViewMatrix);
		renderInstancedMeshes(scene, false, sceneShaderProgram, viewMatrix, lightViewMatrix);
		
		sceneShaderProgram.unbind();
	}
	
	private void renderLights(Matrix4f viewMatrix, SceneLight sceneLight)
	{
		sceneShaderProgram.setUniform("ambientLight", sceneLight.getAmbientLight());
		sceneShaderProgram.setUniform("specularPower", specularPower);
		
		//Process Point Lights
		PointLight[] pointLightList = sceneLight.getPointLights();
		int numLights = pointLightList != null ? pointLightList.length : 0;
		
		for (int i = 0; i < numLights; i++)
		{
			// Get a copy of the point light object and transform its position to view coordinates
			PointLight currPointLight = new PointLight(pointLightList[i]);
			Vector3f lightPos = currPointLight.getPosition();
			Vector4f aux = new Vector4f(lightPos, 1);
			aux.mul(viewMatrix);
			
			lightPos.x = aux.x;
			lightPos.y = aux.y;
			lightPos.z = aux.z;
			
			sceneShaderProgram.setUniform("pointLights", currPointLight, i);
		}
		
		//Process Spotlights
		SpotLight[] spotLightList = sceneLight.getSpotLights();
		numLights = spotLightList != null ? spotLightList.length : 0;
		
		for (int i = 0; i < numLights; i++)
		{
			//Get a copy of the spotlight object and transform its position and cone direction to view coordinates
			SpotLight currSpotLight = new SpotLight(spotLightList[i]);
			Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
			dir.mul(viewMatrix);
			currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));
			
			Vector3f lightPos = currSpotLight.getPointLight().getPosition();
			Vector4f aux = new Vector4f(lightPos, 1);
			aux.mul(viewMatrix);
			
			lightPos.x = aux.x;
			lightPos.y = aux.y;
			lightPos.z = aux.z;
			
			sceneShaderProgram.setUniform("spotLights", currSpotLight, i);
		}
		
		//Get a copy of the directional light object and transform its position to view coordinates
		DirectionalLight currDirLight = new DirectionalLight(sceneLight.getDirectionalLight());
		Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
		dir.mul(viewMatrix);
		
		currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
		sceneShaderProgram.setUniform("directionalLight", currDirLight);
	}
	
	private void renderParticles(Window window, Camera camera, Scene scene)
	{
		glDepthMask(false);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE);
		
		particlesShaderProgram.bind();
		
		particlesShaderProgram.setUniform("texture_sampler", 0);
		
		Matrix4f projectionMatrix = transformation.getProjectionMatrix();
		particlesShaderProgram.setUniform("projectionMatrix", projectionMatrix);
		
		Matrix4f viewMatrix = camera.getViewMatrix();
		
		IParticleEmitter[] emitters = scene.getParticleEmitters();
		int numEmitters = emitters != null ? emitters.length : 0;
		
		Matrix3f aux = new Matrix3f();
		for(int i = 0; i < numEmitters; i++)
		{
			IParticleEmitter emitter = emitters[i];
			InstancedMesh mesh = (InstancedMesh) emitter.getBaseParticle().getMesh();
			
			Texture texture = mesh.getMaterial().getTexture();
			particlesShaderProgram.setUniform("numCols", texture.getNumCols());
			particlesShaderProgram.setUniform("numRows", texture.getNumRows());
			
			mesh.renderListInstanced(emitter.getParticles(), true, transformation, viewMatrix, null);
		}
		
		particlesShaderProgram.unbind();
		
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDepthMask(true);
		
	}
	
	private void renderHud(Window window, IHud hud)
	{
		hudShaderProgram.bind();
		
		Matrix4f ortho = transformation.getOrtho2DProjectionMatrix(0, window.getWidth(), window.getHeight(), 0);
		for (GameItem gameItem : hud.getGameItems())
		{
			Mesh mesh = gameItem.getMesh();
			// Set orthographic and model matrix for this hud item
			Matrix4f projModelMatrix = transformation.buildOrthoProjectionModelMatrix(gameItem, ortho);
			
			hudShaderProgram.setUniform("projModelMatrix", projModelMatrix);
			hudShaderProgram.setUniform("colour", gameItem.getMesh().getMaterial().getColour());
			hudShaderProgram.setUniform("hasTexture", gameItem.getMesh().getMaterial().isTextured() ? 1 : 0);
			
			//Render the mesh for this hud item
			mesh.render();
		}
		
		hudShaderProgram.unbind();
	}
	
	/**
	 * Renders the three axis in space (For debugging purposes only
	 * @param camera
	 */
	private void renderAxes(Window window, Camera camera)
	{
		Window.WindowOptions opts = window.getWindowOptions();
		if (opts.compatibleProfile)
		{
			glPushMatrix();
			glLoadIdentity();
			
			float rotX = camera.getRotation().x;
			float rotY = camera.getRotation().y;
			float rotZ = 0;
			
			glRotatef(rotX, 1.0f, 0.0f, 0.0f);
			glRotatef(rotY, 0.0f, 1.0f, 0.0f);
			glRotatef(rotZ, 0.0f, 0.0f, 1.0f);
			
			glLineWidth(2.0f);
			
			glBegin(GL_LINES);
			
			//X Axis
			glColor3f(1.0f, 0.0f, 0.0f);
			glVertex3f(0.0f, 0.0f, 0.0f);
			glVertex3f(1.0f, 0.0f, 0.0f);
			
			//Y Axis
			glColor3f(0.0f, 1.0f, 0.0f);
			glVertex3f(0.0f, 0.0f, 0.0f);
			glVertex3f(0.0f, 1.0f, 0.0f);
			
			//Z Axis
			glColor3f(1.0f, 1.0f, 1.0f);
			glVertex3f(0.0f, 0.0f, 0.0f);
			glVertex3f(0.0f, 0.0f, 1.0f);
			glEnd();
			
			glPopMatrix();
		}
	}
	
	/**
	 * Renders a crosshair in the centre of the screen
	 */
	private void renderCrossHair(Window window)
	{
		if (window.getWindowOptions().compatibleProfile)
		{
			glPushMatrix();
			glLoadIdentity();
			
			float inc = 0.05f;
			glLineWidth(2.0f);
			
			glBegin(GL_LINES);
			
			glColor3f(1.0f, 1.0f, 1.0f);
			
			//Horizontal line
			glVertex3f(-inc, 0.0f, 0.0f);
			glVertex3f(+inc, 0.0f, 0.0f);
			glEnd();
			
			//Vertical line
			glBegin(GL_LINES);
			glVertex3f(0.0f, -inc, 0.0f);
			glVertex3f(0.0f, +inc, 0.0f);
			glEnd();
			
			glPopMatrix();
		}
	}
	
	private void renderInstancedMeshes(Scene scene, boolean depthMap, ShaderProgram shader, Matrix4f viewMatrix, Matrix4f lightViewMatrix)
	{
		shader.setUniform("isInstanced", 1);
		
		//Render each mesh with the associated game Items
		Map<InstancedMesh, List<GameItem>> mapMeshes = scene.getInstancedMeshMap();
		for (InstancedMesh mesh : mapMeshes.keySet())
		{
			Texture texture = mesh.getMaterial().getTexture();
			if(texture != null)
			{
				sceneShaderProgram.setUniform("numCols", texture.getNumCols());
				sceneShaderProgram.setUniform("numRows", texture.getNumRows());
			}
			
			if (viewMatrix != null)
			{
				shader.setUniform("material", mesh.getMaterial());
				glActiveTexture(GL_TEXTURE2);
				glBindTexture(GL_TEXTURE_2D, shadowMap.getDepthMapTexture().getID());
			}
			
			mesh.renderListInstanced(mapMeshes.get(mesh), transformation, viewMatrix, lightViewMatrix);
		}
	}
	
	private void renderNonInstancedMeshes(Scene scene, ShaderProgram shader, Matrix4f viewMatrix, Matrix4f lightViewMatrix)
	{
		sceneShaderProgram.setUniform("isInstanced", 0);
		
		//Render each mesh with the associated game Items
		Map<Mesh, List<GameItem>> mapMeshes = scene.getGameMeshes();
		for (Mesh mesh : mapMeshes.keySet())
		{
			if (viewMatrix != null)
			{
				shader.setUniform("material", mesh.getMaterial());
				glActiveTexture(GL_TEXTURE2);
				glBindTexture(GL_TEXTURE_2D, shadowMap.getDepthMapTexture().getID());
			}
			
			if(mesh.getMaterial() != null)
			{
				Texture texture = mesh.getMaterial().getTexture();
				if (texture != null)
				{
					sceneShaderProgram.setUniform("numCols", texture.getNumCols());
					sceneShaderProgram.setUniform("numRows", texture.getNumRows());
				}
			}
			
			mesh.renderList(mapMeshes.get(mesh), (GameItem gameItem) ->
			{
				sceneShaderProgram.setUniform("selectedNonInstanced", gameItem.isSelected() ? 1.0f : 0.0f);
				
				Matrix4f modelMatrix = transformation.buildModelMatrix(gameItem);
				if (viewMatrix != null)
				{
					Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(modelMatrix, viewMatrix);
					sceneShaderProgram.setUniform("modelViewNonInstancedMatrix", modelViewMatrix);
				}
				
				Matrix4f modelLightViewMatrix = transformation.buildModelLightViewMatrix(modelMatrix, lightViewMatrix);
				sceneShaderProgram.setUniform("modelLightViewNonInstancedMatrix", modelLightViewMatrix);
				
				if (gameItem instanceof AnimGameItem)
				{
					AnimGameItem animGameItem = (AnimGameItem) gameItem;
					AnimatedFrame frame = animGameItem.getCurrentFrame();
					
					shader.setUniform("jointsMatrix", frame.getJointMatrices());
				}
			});
		}
	}
	
	public void clear()
	{
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	public void cleanup()
	{
		if (shadowMap != null) shadowMap.cleanup();
		if (depthShaderProgram != null) depthShaderProgram.cleanup();
		if (skyBoxShaderProgram != null) skyBoxShaderProgram.cleanup();
		if (sceneShaderProgram != null) sceneShaderProgram.cleanup();
		if(particlesShaderProgram != null) particlesShaderProgram.cleanup();
		if (hudShaderProgram != null) hudShaderProgram.cleanup();
	}
}
