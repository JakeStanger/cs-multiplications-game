package engine.graph.anim;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * @author Jake stanger
 *         TODO Write JavaDoc
 */
public class AnimVertex
{
	public Vector3f position, normal;
	public Vector2f textCoords;
	
	public float[] weights;
	public int[] jointIndices;
	
	public AnimVertex()
	{
		super();
		normal = new Vector3f(0, 0, 0);
	}
}
