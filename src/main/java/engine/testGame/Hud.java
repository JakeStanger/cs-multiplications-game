package engine.testGame;

import engine.items.GameItem;
import engine.IHud;
import engine.items.TextItem;
import engine.Window;
import engine.graph.FontTexture;
import engine.graph.Material;
import engine.graph.Mesh;
import engine.loaders.obj.OBJLoader;
import org.joml.Vector3f;

import java.awt.*;

/**
 * @author Jake stanger
 * Game HUD implementation
 */
public class Hud implements IHud
{
	private static final Font FONT = new Font("Helvetica", Font.PLAIN, 20);
	private static final String CHARSET = "ISO-8859-1";
	
	private final GameItem[] gameItems;
	
	private final TextItem statusTextItem;
	
	private final GameItem compassItem;
	
	public Hud(String statusText) throws Exception
	{
		FontTexture fontTexture = new FontTexture(FONT, CHARSET);
		this.statusTextItem = new TextItem(statusText, fontTexture);
		this.statusTextItem.getMesh().getMaterial().setColour(new Vector3f(1, 1, 1));
		
		//Create compass
		Mesh mesh = OBJLoader.loadMesh("/models/compass.obj");
		
		Material material = new Material();
		material.setColour(new Vector3f(1, 0, 0));
		mesh.setMaterial(material);
		
		compassItem = new GameItem(mesh);
		compassItem.setScale(40f);
		//compassItem.setRotation(0, 0, 180); //Flip to work with OpenGL space coordinates
		
		gameItems = new GameItem[]{statusTextItem, compassItem};
	}
	
	public void setStatusText(String statusText)
	{
		this.statusTextItem.setText(statusText);
	}
	
	@Override
	public GameItem[] getGameItems()
	{
		return gameItems;
	}
	
	public void updateSize(Window window)
	{
		this.statusTextItem.setPosition(10f, window.getHeight() - 50f, 0);
		this.compassItem.setPosition(window.getWidth() - 40f, 50f, 0);
	}
}
