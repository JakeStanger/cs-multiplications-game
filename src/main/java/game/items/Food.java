package game.items;

import engine.Utils;
import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.Texture;
import engine.items.GameItem;
import engine.loaders.obj.OBJLoader;
import game.enums.Direction;
import org.joml.Vector3f;

import static game.scenes.Game.GRID_SIZE;
import static game.scenes.Game.MAP_SIZE;

/**
 * @author Jake stanger
 * A food pellet. When the snake hits this,
 * the pellet moves and the snake grows in
 * length by one.
 */
public class Food extends GameItem
{
	private static final float REFLECTANCE = 1.3f;
	
	private static final float ROTATION_SPEED = 0.005f, BOB_HEIGHT = 0.3f, BOB_STEP = 0.005f;
	
	/**
	 * The position of the food ignoring
	 * its height difference due to the bobbing animation.
	 */
	private Vector3f startPos;
	
	private Direction bobDir;
	
	public Food() throws Exception
	{
		Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
		
		Material material = new Material(new Texture("/textures/yellow.png"), REFLECTANCE);
		mesh.setMaterial(material);
		
		//this.setPosition(0, 10, 0);
		this.randomlyPlaceOnMap();
		this.startPos = new Vector3f(this.getPosition());
		
		this.setMesh(mesh);
		
		this.bobDir = Direction.UP;
	}
	
	public void update()
	{
		this.roll();
		this.bob();
		
		//this.randomlyPlaceOnMap();
	}
	
	private void roll()
	{
		this.getRotation().rotateX(ROTATION_SPEED);
		this.getRotation().rotateY(ROTATION_SPEED);
		this.getRotation().rotateZ(ROTATION_SPEED);
	}
	
	private void bob()
	{
		final float delta = 0.0005f;
		
		double step = (float) (Math.asin(this.getPosition().y - this.startPos.y)*BOB_STEP);
		if(step < delta) step = delta;
		
		if(this.bobDir == Direction.DOWN) step = -step;
		
		this.getPosition().y += step;
		
		if(this.getPosition().y - this.startPos.y > BOB_HEIGHT) this.bobDir = Direction.DOWN;
		else if(this.startPos.y - this.getPosition().y > BOB_HEIGHT) this.bobDir = Direction.UP;
	}
	
	public void randomlyPlaceOnMap()
	{
		float x = Utils.getRandomMultipleBetween(GRID_SIZE, MAP_SIZE-GRID_SIZE, GRID_SIZE);
		float y = Utils.getRandomMultipleBetween(GRID_SIZE, MAP_SIZE-GRID_SIZE, GRID_SIZE);
		float z = -Utils.getRandomMultipleBetween(GRID_SIZE, MAP_SIZE-GRID_SIZE, GRID_SIZE);
		
		this.setPosition(x, y, z);
		this.startPos = new Vector3f(x, y, z);
	}
}
