package game.items;

import engine.graph.Material;
import engine.graph.Mesh;
import engine.items.GameItem;
import engine.loaders.obj.OBJLoader;
import org.joml.Vector3f;

/**
 * @author Jake stanger
 *         TODO Write JavaDoc
 */
public class MenuButton extends GameItem
{
	private static final float BUTTON_REFLECTANCE = 1f;
	
	private static final Material MAT_DESELECTED = new Material(new Vector3f(0, 0.8f, 0.2f), BUTTON_REFLECTANCE);
	private static final Material MAT_SELECTED = new Material(new Vector3f(0.2f, 0.8f, 0.8f), BUTTON_REFLECTANCE);
	
	public MenuButton(String meshName) throws Exception
	{
		Mesh mesh = OBJLoader.loadMesh("/models/menu/" + meshName + ".obj");
		mesh.setMaterial(MAT_DESELECTED);
		this.setMesh(mesh);
	}
	
	@Override
	public void setSelected(boolean selected)
	{
		if(selected) this.getMesh().setMaterial(MAT_SELECTED);
		else this.getMesh().setMaterial(MAT_DESELECTED);
	}
}
