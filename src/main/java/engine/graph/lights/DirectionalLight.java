package engine.graph.lights;

import org.joml.Vector3f;

/**
 * @author Jake stanger
 * A light which shines in one direction
 * from an infinite point.
 * Used to replicate sunlight.
 */
public class DirectionalLight
{
	private Vector3f colour, direction;
	private float intensity;
	
	private OrthoCoords orthoCoords;
	private float shadowPosMult;
	
	public DirectionalLight(Vector3f colour, Vector3f direction, float intensity)
	{
		this.colour = colour;
		this.direction = direction;
		this.intensity = intensity;
		
		this.orthoCoords = new OrthoCoords();
		this.shadowPosMult = 1;
	}
	
	public DirectionalLight(DirectionalLight light)
	{
		this(new Vector3f(light.getColour()), new Vector3f(light.getDirection()), light.getIntensity());
	}
	
	public Vector3f getColour()
	{
		return colour;
	}
	
	public void setColour(Vector3f colour)
	{
		this.colour = colour;
	}
	
	public Vector3f getDirection()
	{
		return direction;
	}
	
	public void setDirection(Vector3f direction)
	{
		this.direction = direction;
	}
	
	public float getIntensity()
	{
		return intensity;
	}
	
	public void setIntensity(float intensity)
	{
		this.intensity = intensity;
	}
	
	public float getShadowPosMult()
	{
		return shadowPosMult;
	}
	
	public void setShadowPosMult(float shadowPosMult)
	{
		this.shadowPosMult = shadowPosMult;
	}
	
	public OrthoCoords getOrthoCoords()
	{
		return orthoCoords;
	}
	
	public void setOrthoCoords(float left, float right, float bottom, float top, float near, float far)
	{
		orthoCoords.left = left;
		orthoCoords.right = right;
		orthoCoords.bottom = bottom;
		orthoCoords.top = top;
		orthoCoords.near = near;
		orthoCoords.far = far;
	}
	
	public static class OrthoCoords
	{
		public float left, right, bottom, top, near, far;
	}
}
