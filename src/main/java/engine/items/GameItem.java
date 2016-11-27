package engine.items;

import engine.graph.Mesh;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * @author Jake stanger
 * Any items to be rendered in-game
 */
public class GameItem
{
	private Mesh[] meshes;
	private final Vector3f position;
	private final Quaternionf rotation;
	private float scale;
	private int textPos;
	
	private boolean selected;
	
	public GameItem()
	{
		position = new Vector3f(0, 0, 0);
		rotation = new Quaternionf();
		scale = 1;
		textPos = 0;
	}
	
	public GameItem(Mesh mesh)
	{
		this();
		this.meshes = new Mesh[]{mesh};
	}
	
	public GameItem(Mesh[] meshes)
	{
		this();
		this.meshes = meshes;
	}
	
	public Vector3f getPosition()
	{
		return position;
	}
	
	public void setPosition(float x, float y, float z)
	{
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
	}
	
	public void setPosition(Vector3f position)
	{
		this.position.x = position.x;
		this.position.y = position.y;
		this.position.z = position.z;
	}
	
	public float getScale()
	{
		return scale;
	}
	
	public void setScale(float scale)
	{
		this.scale = scale;
	}
	
	public Quaternionf getRotation()
	{
		return this.rotation;
	}
	
	public void setRotation(Quaternionf q)
	{
		this.rotation.set(q);
	}
	
	public Mesh getMesh()
	{
		return this.meshes[0];
	}
	
	public Mesh[] getMeshes()
	{
		return this.meshes;
	}
	
	public void setMeshes(Mesh[] meshes)
	{
		this.meshes = meshes;
	}
	
	public void setMesh(Mesh mesh)
	{
		//if(this.meshes != null) for(Mesh currMesh : meshes) currMesh.cleanup();
		this.meshes = new Mesh[]{mesh};
	}
	
	public int getTextPos()
	{
		return textPos;
	}
	
	public void setTextPos(int textPos)
	{
		this.textPos = textPos;
	}
	
	public boolean isSelected()
	{
		return selected;
	}
	
	public void setSelected(boolean selected)
	{
		this.selected = selected;
	}
	
	public void cleanup()
	{
		int numMeshes = this.meshes != null ? this.meshes.length : 0;
		
		for(int i = 0; i < numMeshes; i++) this.meshes[i].cleanup();
	}
}
