package engine.graph.hud;

import engine.graph.Material;
import engine.graph.Mesh;
import engine.items.GameItem;
import engine.loaders.obj.OBJLoader;
import org.joml.Vector3f;

import java.awt.*;


/**
 * @author Jake stanger
 * A clickable button
 */
public class Button extends TextLabel
{
	private static final float RECTANGLE_ASPECT_RATIO = 2.5f;
	
	private float width;
	private Vector3f backgroundColour;
	
	private GameItem background;
	
	public Button(float x, float y, float width, String defaultText, Font font, Vector3f colour, Vector3f backgroundColour) throws Exception
	{
		super(x+5, y+(width/RECTANGLE_ASPECT_RATIO)/2-(font.getSize()/2), defaultText, font, colour);
		this.width = width;
		this.backgroundColour = backgroundColour;
		
		this.background = new GameItem();
		this.background.setPosition(x, y, -1);
		
		Mesh mesh = OBJLoader.loadMesh("/models/rectangle.obj");
		Material material = new Material(this.backgroundColour, 0);
		mesh.setMaterial(material);
		
		this.background.setMesh(mesh);
		this.background.setScale(width/RECTANGLE_ASPECT_RATIO);
	}
	
	public GameItem getBackground()
	{
		return background;
	}
}
