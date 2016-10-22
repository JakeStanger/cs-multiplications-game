package game.items;

import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.Texture;
import engine.loaders.obj.OBJLoader;
import game.Game;

import static game.Game.SNAKE_TAIL_SCALE;
import static game.items.SnakeHead.REFLECTANCE;

/**
 * @author Jake stanger
 * A tail piece of the snake, which follows the head.
 */
public class SnakeTail extends SnakePiece
{
	private static final float ANIM_MULTIPLIER = 0.05f;
	
	private boolean visible;
	
	/**
	 * When popIn is true, an
	 * animation plays causing the
	 * tail piece to increase in size.
	 */
	private boolean popIn;
	
	public SnakeTail(int ID) throws Exception
	{
		this.ID = ID;
		
		Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
		
		Material material = new Material(new Texture("/textures/blue.png"), REFLECTANCE);
		mesh.setMaterial(material);
		
		this.setMesh(mesh);
		
		this.direction = Game.snakeHead.getDirection();
	}
	
	@Override
	public void update()
	{
		super.update();
		if(this.popIn) this.popIn();
	}
	
	private void popIn()
	{
		final float delta = 0.01f;
		
		float scale = this.getScale();
		if(scale < SNAKE_TAIL_SCALE) this.setScale(scale + (float) (Math.sin(SNAKE_TAIL_SCALE - scale)) * ANIM_MULTIPLIER);
		if(this.getScale() > SNAKE_TAIL_SCALE - delta)
		{
			this.setScale(SNAKE_TAIL_SCALE); //Round off
			this.setPopIn(false);
		}
	}
	
	boolean isVisible()
	{
		return visible;
	}
	
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
	
	void setPopIn(boolean shouldPopIn)
	{
		this.popIn = shouldPopIn;
	}
}

