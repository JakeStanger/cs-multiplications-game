package engine;

import engine.graph.InstancedMesh;
import engine.graph.Mesh;
import engine.graph.particles.IParticleEmitter;
import engine.graph.weather.Fog;
import engine.items.GameItem;
import engine.items.SkyBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jake stanger
 * Wrapper class for objects and lighting in the world.
 */
public class Scene
{
	private SkyBox skyBox;
	private SceneLight sceneLight;
	private Fog fog;
	
	private Map<Mesh, List<GameItem>> meshMap;
	
	private Map<InstancedMesh, List<GameItem>> instancedMeshMap;
	
	private boolean renderShadows;
	
	private IParticleEmitter[] particleEmitters;
	
	public Scene()
	{
		this.meshMap = new HashMap<>();
		this.instancedMeshMap = new HashMap<>();
		this.fog = Fog.NO_FOG;
		this.renderShadows = true;
	}
	
	public void setGameItems(GameItem[] gameItems)
	{
		int numGameItems = gameItems != null ? gameItems.length : 0;
		for(int i = 0; i < numGameItems; i++)
		{
			GameItem gameItem = gameItems[i];
			Mesh mesh = gameItem.getMesh();
			
			List<GameItem> list = meshMap.get(mesh);
			if(list == null)
			{
				list = new ArrayList<>();
				this.meshMap.put(mesh, list);
			}
			
			list.add(gameItem);
		}
	}
	
	public void setGameItems(List<GameItem> gameItems)
	{
		for(GameItem gameItem : gameItems)
		{
			Mesh mesh = gameItem.getMesh();
			
			List<GameItem> list = this.meshMap.get(mesh);
			if(list == null)
			{
				list = new ArrayList<>();
				this.meshMap.put(mesh, list);
			}
			
			list.add(gameItem);
		}
	}
	
	public SkyBox getSkyBox()
	{
		return skyBox;
	}
	
	public void setSkyBox(SkyBox skyBox)
	{
		this.skyBox = skyBox;
	}
	
	public boolean hasSkybox()
	{
		return this.skyBox != null;
	}
	
	public SceneLight getSceneLight()
	{
		return sceneLight;
	}
	
	public void setSceneLight(SceneLight sceneLight)
	{
		this.sceneLight = sceneLight;
	}
	
	public Map<Mesh, List<GameItem>> getGameMeshes()
	{
		return meshMap;
	}
	
	public Fog getFog()
	{
		return this.fog;
	}
	
	public void setFog(Fog fog)
	{
		this.fog = fog;
	}
	
	public IParticleEmitter[] getParticleEmitters()
	{
		return particleEmitters;
	}
	
	public void setParticleEmitters(IParticleEmitter[] particleEmitters)
	{
		this.particleEmitters = particleEmitters;
	}
	
	public void setRenderShadows(boolean renderShadows)
	{
		this.renderShadows = renderShadows;
	}
	
	public boolean isRenderShadows()
	{
		return this.renderShadows;
	}
	
	public Map<InstancedMesh, List<GameItem>> getInstancedMeshMap()
	{
		return instancedMeshMap;
	}
	
	public void cleanup()
	{
		meshMap.keySet().forEach(Mesh::cleanup);
	}
}
