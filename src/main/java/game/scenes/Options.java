package game.scenes;

import engine.MouseInput;
import engine.Scene;
import engine.SceneLight;
import engine.Window;
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
import game.utils.OptionsIO;
import org.joml.Vector3f;
import org.lwjgl.openal.AL11;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Jake stanger
 *         TODO Write JavaDoc
 */
public class Options implements IScene
{
	private static final Material MATERIAL = new Material(new Vector3f(0.6f, 0, 0.8f), 1);
	private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
	private static final float Z_LEVEL = -6;
	
	private static final int MENU_COOLDOWN_TIME = 7;
	
	private static final int NAME_DIGIT_ONE_ID = 0, NAME_DIGIT_TWO_ID = 1, NAME_DIGIT_THREE_ID = 2;
	private static final int MUTE_SOUND_ID = 3, MUTE_MUSIC_ID = 4;
	private static final int SAVE_ID = 5;
	
	private List<List<GameItem>> gameItems;
	
	private final Renderer renderer;
	private final SoundManager soundManager;
	private final Camera camera;
	
	private Scene scene;
	private static Hud hud;
	
	private int selectedCycle = 0, selectedOption = 0;
	private int menuCooldown = MENU_COOLDOWN_TIME;
	
	public Options()
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
		
		this.gameItems = new ArrayList<>();
		
		Mesh a = OBJLoader.loadMesh("/models/chars/a.obj");
		a.setMaterial(MATERIAL);
		
		//--Username digits--
		for(int i = 0; i < 3; i++)
		{
			List<GameItem> digitList = new ArrayList<>();
			for(char c : ALPHABET.toCharArray())
			{
				Mesh mesh = OBJLoader.loadMesh("/models/chars/" + c + ".obj");
				mesh.setMaterial(MATERIAL);
				MenuButton digit = new MenuButton();
				digit.setMesh(mesh);
				digit.setPosition(-5.5f+i, 2, Z_LEVEL);
				if(c != Values.name[i]) digit.setScale(0);
				digitList.add(digit);
			}
			this.gameItems.add(digitList);
		}
		
		//Audio controls
		this.gameItems.add(createWord("sounds", -5.5f, 1));
		this.gameItems.add(createWord("music", 1, 1));
		
		//Save button
		this.gameItems.add(createWord("save", -5.5f, -1));
		
		//Select first option
		for (GameItem gameItem : this.gameItems.get(0)) gameItem.setSelected(true);
		
		//Rotate all to be right way up
		for(List<GameItem> gameItems : this.gameItems)
			for(GameItem gameItem : gameItems) gameItem.getRotation().rotateX((float) Math.toRadians(90));
		
		this.setupLighting();
		
		this.soundManager.init();
		this.soundManager.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);
		this.setupSounds();
		
		List<GameItem> sceneGameItems = new ArrayList<>();
		this.gameItems.forEach(sceneGameItems::addAll);
		this.scene.setGameItems(sceneGameItems);
	}
	
	private List<GameItem> createWord(String text, float startX, float y) throws Exception
	{
		List<GameItem> charList = new ArrayList<>();
		for(int i = 0; i < text.length(); i++)
		{
			Mesh mesh = OBJLoader.loadMesh("/models/chars/" + text.toCharArray()[i] + ".obj");
			mesh.setMaterial(MATERIAL);
			MenuButton digit = new MenuButton();
			digit.setMesh(mesh);
			digit.setPosition(startX+i, y, Z_LEVEL);
			charList.add(digit);
		}
		
		return charList;
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
		
		//Music
		SoundBuffer bufferMusic = new SoundBuffer("/sounds/overworld.ogg");
		this.soundManager.addSoundBuffer(bufferMusic);
		SoundSource sourceMusic = new SoundSource(true, false);
		sourceMusic.setBuffer(bufferMusic.getBufferID());
		this.soundManager.addSoundSource(Sound.MENU_MUSIC.toString(), sourceMusic);
		this.soundManager.playSoundSource(Sound.MENU_MUSIC.toString());
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
	
	@Override
	public void update(float interval, MouseInput mouseInput)
	{
		//Toggle music
		SoundSource music = this.soundManager.getSoundSource(Sound.MENU_MUSIC.toString());
		if(music.isPlaying() && Values.muteMusic) music.stop();
		else if(!music.isPlaying() && !Values.muteMusic) music.play();
		
		//Toggle sound effects
		SoundSource boop = this.soundManager.getSoundSource(Sound.BOOP.toString());
		SoundSource boopHigh = this.soundManager.getSoundSource(Sound.BOOP_HIGH.toString());
		if(Values.muteSound)
		{
			if(boop.isPlaying()) boop.stop();
			if(boopHigh.isPlaying()) boopHigh.stop();
		}
	}
	
	private void triggerOption(Window window)
	{
		//Play boop
		this.soundManager.playSoundSource(Sound.BOOP_HIGH.toString());
		
		switch(this.selectedOption)
		{
			case MUTE_SOUND_ID:
				Values.muteSound = !Values.muteSound;
				break;
			case MUTE_MUSIC_ID:
				Values.muteMusic = !Values.muteMusic;
				break;
			case SAVE_ID:
				OptionsIO.writeToFile();
				break;
		}
	}
	
	@Override
	public void input(Window window, MouseInput mouseInput)
	{
		if(this.menuCooldown > 0) this.menuCooldown--;
		
		if(this.menuCooldown == 0)
		{
			int prevCycle = this.selectedCycle;
			int prevSelected = this.selectedOption;
			
			//Option selection
			if (window.isKeyPressed(GLFW_KEY_RIGHT)) this.selectedOption++;
			else if (window.isKeyPressed(GLFW_KEY_LEFT)) this.selectedOption--;
			
			//Wrap around
			if (selectedOption == gameItems.size()) selectedOption = 0;
			if (selectedOption == -1) selectedOption = gameItems.size() - 1;
			
			//Update selected option
			if (this.selectedOption != prevSelected)
				for (int i = 0; i < gameItems.size(); i++)
					if (i == this.selectedOption) for (GameItem gameItem : gameItems.get(i)) gameItem.setSelected(true);
					else for (GameItem gameItem : gameItems.get(i)) gameItem.setSelected(false);
			
			//Option cycle
			if (window.isKeyPressed(GLFW_KEY_UP)) this.selectedCycle--;
			else if (window.isKeyPressed(GLFW_KEY_DOWN)) this.selectedCycle++;
			
			//Wrap around
			if (selectedCycle == this.gameItems.get(this.selectedOption).size()) selectedCycle = 0;
			if (selectedCycle == -1) selectedCycle = this.gameItems.get(this.selectedOption).size() - 1;
			
			//Update cycled item
			if (this.selectedCycle != prevCycle)
			{
				for (int i = 0; i < this.gameItems.get(selectedOption).size(); i++)
				{
					if (i == this.selectedCycle)
					{
						this.gameItems.get(selectedOption).get(i).setScale(1);
						
						if(this.selectedOption <= NAME_DIGIT_THREE_ID)
							Values.name[selectedOption] = ALPHABET.toCharArray()[i];
					}
					else if(this.selectedOption <= NAME_DIGIT_THREE_ID)
						this.gameItems.get(selectedOption).get(i).setScale(0);
				}
			}
			
			if (window.isKeyPressed(GLFW_KEY_SPACE) || window.isKeyPressed(GLFW_KEY_ENTER)) this.triggerOption(window);
			
			
			//Quit to menu
			if (window.isKeyPressed(GLFW_KEY_Q) || window.isKeyPressed(GLFW_KEY_ESCAPE)) try
			{
				((GameLogic) Main.getGameLogic()).setScene(new Menu(), window);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			//Play sounds
			if(this.selectedOption != prevSelected) this.soundManager.playSoundSource(Sound.BOOP.toString());
			if(this.selectedCycle != prevCycle) this.soundManager.playSoundSource(Sound.BOOP_HIGH.toString());
			
			this.menuCooldown = MENU_COOLDOWN_TIME;
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
	
	public static class Values
	{
		public static char[] name = new char[3];
		public static boolean muteSound = false, muteMusic = false;
		
		public static class SQL
		{
			public static String url = "jdbc:mysql://localhost:3306/3d_snake";
			public static String username = "java";
			public static String password = "JS2tgLzXIbFmZxMv";
		}
	}
}
