package game.scenes;

import engine.*;
import engine.graph.Camera;
import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.Renderer;
import engine.graph.lights.DirectionalLight;
import engine.items.GameItem;
import engine.loaders.obj.OBJLoader;
import engine.sound.SoundBuffer;
import engine.sound.SoundManager;
import engine.sound.SoundSource;
import game.GameLogic;
import game.Hud;
import game.Main;
import game.enums.Sound;
import game.items.MenuButton;
import org.joml.Vector3f;
import org.lwjgl.openal.AL11;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Jake stanger
 * Scene for maths questions.
 */
public class Maths implements IScene
{
	private static final int MENU_COOLDOWN_TIME = 10;
	
	private static final int MULTIPLY_ID = 0;
	
	private static final int DIGIT_ONE_ID = 1;
	private static final int DIGIT_TWO_ID = 2;
	
	private static final int OPT_ONE_ID = 3;
	private static final int OPT_THREE_ID = 5;
	private static final int OPT_FOUR_ID = 6;
	
	private static final int TOTAL_MAJOR_COMPONENTS = 7;
	private static final int DIGIT_COMPONENTS = 2;
	private static final int OPTION_COMPONENTS = 3;
	
	private static final Material MATERIAL = new Material(new Vector3f(0.6f, 0, 0.8f), 1);
	
	private static final float Z_POS = -5;
	
	private List<List<GameItem>> gameItems;
	
	private final Renderer renderer;
	private final SoundManager soundManager;
	private final Camera camera;
	
	private Scene scene;
	private static Hud hud;
	
	private int selectedOption = 0;
	private int menuCooldown = MENU_COOLDOWN_TIME;
	
	private int answerPos;
	
	public Maths()
	{
		this.renderer = new Renderer();
		this.soundManager = new SoundManager();
		this.camera = new Camera();
	}
	
	@Override
	public void init(Window window) throws Exception
	{
		this.renderer.init(window);
		this.soundManager.init();
		
		this.scene = new Scene();
		
		//--Add game items--
		gameItems = new ArrayList<>();
		for(int i = 0; i < TOTAL_MAJOR_COMPONENTS; i++) gameItems.add(new ArrayList<>());
		
		GameItem multiplySign = new GameItem();
		Mesh multiplyMesh = OBJLoader.loadMesh("/models/chars/x.obj");
		multiplyMesh.setMaterial(MATERIAL);
		multiplySign.setMesh(multiplyMesh);
		multiplySign.setPosition(-0.35f, 1.5f, Z_POS);
		gameItems.get(MULTIPLY_ID).add(multiplySign);
		
		//Create and position digits
		for(int i = 0; i < DIGIT_COMPONENTS; i++)
		{
			GameItem digitOne = new GameItem();
			GameItem digitTwo = new GameItem();
			
			digitOne.setPosition(-2.35f+i, 1.5f, Z_POS);
			digitTwo.setPosition(0.7f+i, 1.5f, Z_POS);
			
			gameItems.get(DIGIT_ONE_ID).add(digitOne);
			gameItems.get(DIGIT_TWO_ID).add(digitTwo);
		}
		
		//Create and position options
		for(int i = OPT_ONE_ID; i < OPT_FOUR_ID+1; i++)
		{
			for(int j = 0; j < OPTION_COMPONENTS; j++)
			{
				GameItem gameItem = new MenuButton();
				gameItem.setPosition(i % 2 != 0 ? -4.5f+j: 1.8f+j,
						i < OPT_THREE_ID ? 0 : -2,
						Z_POS);
				gameItems.get(i).add(gameItem);
			}
		}
		
		//Rotate all to be right way up
		for(List<GameItem> gameItems : this.gameItems)
			for(GameItem gameItem : gameItems) gameItem.getRotation().rotateX((float) Math.toRadians(90));
		
		this.setupLighting();
		
		this.soundManager.init();
		this.soundManager.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);
		this.setupSounds();
		
		this.generateQuestion();
		
		//Add items to scene
		List<GameItem> sceneGameItems = new ArrayList<>();
		this.gameItems.forEach(sceneGameItems::addAll);
		this.scene.setGameItems(sceneGameItems);
	}
	
	private void generateQuestion()
	{
		//Generate random values
		int num1 = Utils.getRandomIntBetween(1, 12);
		int num2 = Utils.getRandomIntBetween(1, 12);
		int answer = num1*num2;
		
		//A, B, C or D
		answerPos = Utils.getRandomIntBetween(0, 3);
		int[] answers = new int[4];
		
		for(int i = 0; i < answers.length; i++)
		{
			if(i == answerPos) answers[i] = answer;
			else answers[i] = Utils.getRandomIntBetween(1, 12)*Utils.getRandomIntBetween(1, 12);
		}
		
		//Convert to string and prepend 0 if necessary
		String num1S = Integer.toString(num1);
		if(num1S.length() < 2) num1S = "0" + num1S;
		
		String num2S = Integer.toString(num2);
		if(num2S.length() < 2) num2S = "0" + num2S;
		
		String[] answersS = new String[4];
		for(int i = 0; i < answers.length; i++)
		{
			answersS[i] = Integer.toString(answers[i]);
			while(answersS[i].length() < 3) answersS[i] = "0" + answersS[i];
		}
		
		try
		{
			//Set digit meshes
			for(int i = DIGIT_ONE_ID; i < OPT_FOUR_ID+1; i++)
			{
				for(int j = 0; j < gameItems.get(i).size(); j++)
				{
					//Get path for any digit/option in one line
					String path = "/models/chars/" + (i == DIGIT_ONE_ID ? num1S.toCharArray()[j] :
							(i <= DIGIT_TWO_ID ? num2S.toCharArray()[j] :
									answersS[i-OPT_ONE_ID].toCharArray()[j])) + ".obj";
					Mesh mesh = OBJLoader.loadMesh(path);
					mesh.setMaterial(MATERIAL);
					gameItems.get(i).get(j).setMesh(mesh);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void setupSounds() throws Exception
	{
		//Boop
		SoundBuffer bufferBoop = new SoundBuffer("/sounds/boop.ogg");
		this.soundManager.addSoundBuffer(bufferBoop);
		SoundSource sourceBoop = new SoundSource(false, false);
		sourceBoop.setBuffer(bufferBoop.getBufferID());
		this.soundManager.addSoundSource(Sound.BOOP.toString(), sourceBoop);
		
		//Boop high
		SoundBuffer bufferBoopHigh = new SoundBuffer("/sounds/boop_high.ogg");
		this.soundManager.addSoundBuffer(bufferBoopHigh);
		SoundSource sourceBoopHigh = new SoundSource(false, false);
		sourceBoopHigh.setBuffer(bufferBoopHigh.getBufferID());
		this.soundManager.addSoundSource(Sound.BOOP_HIGH.toString(), sourceBoopHigh);
	}
	
	private void setupLighting()
	{
		final Vector3f ambientIntensity = new Vector3f(0.3f, 0.3f, 0.3f);
		
		final Vector3f directionalColour = new Vector3f(1, 1, 1);
		final Vector3f directionalDirection = new Vector3f(0, 1, 1);
		final float directionalIntensity = 0.4f;
		
		this.scene.setSceneLight(new SceneLight());
		this.scene.getSceneLight().setAmbientLight(ambientIntensity);
		this.scene.getSceneLight().setSkyBoxLight(ambientIntensity);
		this.scene.getSceneLight().setDirectionalLight(new DirectionalLight(directionalColour, directionalDirection, directionalIntensity));
	}
	
	private void selectNextOption()
	{
		this.selectedOption++;
		if(this.selectedOption == OPT_FOUR_ID+1) this.selectedOption = OPT_ONE_ID;
		
		this.updateSelected();
	}
	
	private void selectPrevOption()
	{
		this.selectedOption--;
		if(this.selectedOption == OPT_ONE_ID-1) this.selectedOption = OPT_FOUR_ID;
		
		this.updateSelected();
	}
	
	private void updateSelected()
	{
		//Play boop
		this.soundManager.playSoundSource(Sound.BOOP.toString());
		
		for(int i = OPT_ONE_ID; i < OPT_FOUR_ID+1; i++)
		{
			if(i == this.selectedOption)
				for(GameItem gameItem : this.gameItems.get(i)) gameItem.setSelected(true);
			else
				for(GameItem gameItem : this.gameItems.get(i)) gameItem.setSelected(false);
		}
	}
	
	private void triggerOption(Window window)
	{
		//Play high boop sound
		this.soundManager.playSoundSource(Sound.BOOP_HIGH.toString());
		
		if(selectedOption-OPT_ONE_ID == answerPos)
		{
			try
			{
				((GameLogic) Main.getGameLogic()).setScene(new Game(), window);
				Game.incrementScore();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			Game.endGame();
		}
	}
	
	@Override
	public void update(float interval, MouseInput mouseInput)
	{
		
	}
	
	@Override
	public void input(Window window, MouseInput mouseInput)
	{
		if(this.menuCooldown > 0) this.menuCooldown--;
		
		if(this.menuCooldown == 0)
		{
			if (window.isKeyPressed(GLFW_KEY_DOWN)) this.selectNextOption();
			else if (window.isKeyPressed(GLFW_KEY_UP)) this.selectPrevOption();
			
			if (window.isKeyPressed(GLFW_KEY_SPACE) || window.isKeyPressed(GLFW_KEY_ENTER)) this.triggerOption(window);
			
			this.menuCooldown = Maths.MENU_COOLDOWN_TIME;
		}
	}
	
	@Override
	public void render(Window window)
	{
		if (hud != null) hud.updateSize(window);
		this.renderer.render(window, camera, scene, hud);
	}
	
	@Override
	public void cleanup()
	{
		this.renderer.cleanup();
		this.soundManager.cleanup();
		
		if (hud != null) hud.cleanup();
	}
}
