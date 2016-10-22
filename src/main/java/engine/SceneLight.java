package engine;

import engine.graph.lights.DirectionalLight;
import engine.graph.lights.PointLight;
import engine.graph.lights.SpotLight;
import org.joml.Vector3f;

/**
 * @author Jake stanger
 *  Wrapper class for lights
 */
public class SceneLight
{
	private Vector3f ambientLight;
	
	private Vector3f skyBoxLight;
	
	private PointLight[] pointLights;
	private SpotLight[] spotLights;
	private DirectionalLight directionalLight;
	
	public Vector3f getAmbientLight()
	{
		return ambientLight;
	}
	
	public void setAmbientLight(Vector3f ambientLight)
	{
		this.ambientLight = ambientLight;
	}
	
	public PointLight[] getPointLights()
	{
		return pointLights;
	}
	
	public void setPointLights(PointLight[] pointLights)
	{
		this.pointLights = pointLights;
	}
	
	public SpotLight[] getSpotLights()
	{
		return spotLights;
	}
	
	public void setSpotLights(SpotLight[] spotLights)
	{
		this.spotLights = spotLights;
	}
	
	public DirectionalLight getDirectionalLight()
	{
		return directionalLight;
	}
	
	public void setDirectionalLight(DirectionalLight directionalLight)
	{
		this.directionalLight = directionalLight;
	}
	
	public Vector3f getSkyBoxLight()
	{
		return skyBoxLight;
	}
	
	public void setSkyBoxLight(Vector3f skyBoxLight)
	{
		this.skyBoxLight = skyBoxLight;
	}
}
