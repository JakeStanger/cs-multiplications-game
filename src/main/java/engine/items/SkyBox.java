package engine.items;

import engine.graph.Material;
import engine.graph.Mesh;
import engine.loaders.obj.OBJLoader;
import engine.graph.Texture;
import org.joml.Vector3f;

/**
 * @author Jake stanger
 * The world Skybox.
 * A big obj textured on the inside.
 */
public class SkyBox extends GameItem
{
	public SkyBox(String objModel, String textureFile) throws Exception
	{
		super();
		Mesh skyBoxMesh = OBJLoader.loadMesh(objModel);
		Texture skyboxTexture = new Texture(textureFile);
		skyBoxMesh.setMaterial(new Material(skyboxTexture, 0.0f));
		
		this.setMesh(skyBoxMesh);
		this.setPosition(0, 0, 0);
	}
	
	public SkyBox(String objModel, Vector3f colour) throws Exception
	{
		super();
		Mesh skyBoxMesh = OBJLoader.loadMesh(objModel);
		
		Material material = new Material(colour, 0);
		skyBoxMesh.setMaterial(material);
		
		this.setMesh(skyBoxMesh);
		this.setPosition(0, 0, 0);
	}
}
