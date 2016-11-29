package game.items;

import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.Texture;
import engine.items.GameItem;
import engine.loaders.obj.OBJLoader;

import static game.scenes.Game.MAP_SIZE;

/**
 * @author Jake stanger
 * A wall around the edge of the map
 */
public class Wall extends GameItem
{
	public Wall() throws Exception
	{
		Mesh mesh = OBJLoader.loadMesh("/models/skybox.obj");
		
		Material material = new Material(new Texture("/textures/grid.png"), 1);
		mesh.setMaterial(material);
		
		this.setMesh(mesh);
		this.setScale(MAP_SIZE/2);
		this.setPosition(MAP_SIZE/2, MAP_SIZE/2, -MAP_SIZE/2);
	}
}
