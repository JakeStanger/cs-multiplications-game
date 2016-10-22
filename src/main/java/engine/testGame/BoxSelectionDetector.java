package engine.testGame;

import engine.graph.Camera;
import engine.items.GameItem;
import org.joml.Intersectionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * @author Jake stanger
 *         TODO Write JavaDoc
 */
public class BoxSelectionDetector
{
	private final Vector3f max, min;
	private Vector3f dir;
	private Vector2f nearFar;
	
	public BoxSelectionDetector()
	{
		this.dir = new Vector3f();
		this.min = new Vector3f();
		this.max = new Vector3f();
		this.nearFar = new Vector2f();
	}
	
	/**
	 * Finds whichever GameItem the camera is looking at, if any, and sets it as selected.
	 * @param gameItems A list of all GameItems
	 * @param camera The camera
	 */
	public void selectGameItem(GameItem[] gameItems, Camera camera)
	{
		GameItem selectedGameItem = null;
		float closestDistance = Float.POSITIVE_INFINITY;
		
		dir = camera.getViewMatrix().positiveZ(dir).negate();
		
		for(GameItem gameItem : gameItems)
		{
			gameItem.setSelected(false);
			
			min.set(gameItem.getPosition());
			max.set(gameItem.getPosition());
			
			min.add(-gameItem.getScale(), -gameItem.getScale(), -gameItem.getScale());
			max.add(gameItem.getScale(), gameItem.getScale(), gameItem.getScale());
			
			if(Intersectionf.intersectRayAab(camera.getPosition(), dir, min, max, nearFar))
			{
				closestDistance = nearFar.x;
				selectedGameItem = gameItem;
			}
		}
		
		if(selectedGameItem != null) selectedGameItem.setSelected(true);
	}
}
