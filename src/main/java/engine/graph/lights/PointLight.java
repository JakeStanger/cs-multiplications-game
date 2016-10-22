package engine.graph.lights;

import org.joml.Vector3f;

/**
 * @author Jake stanger
 * A light emitting in 360 degrees from a single point
 */
public class PointLight
{
	private Vector3f colour, position;
	
	protected float intensity;
	private Attenuation attenuation;
	
	public PointLight(Vector3f colour, Vector3f position, float intensity)
	{
		attenuation = new Attenuation(1, 0, 0);
		
		this.colour = colour;
		this.position = position;
		this.intensity = intensity;
	}
	
	public PointLight(Vector3f colour, Vector3f position, float intensity, Attenuation attentuation)
	{
		this(colour, position, intensity);
		this.attenuation = attentuation;
	}
	
	public PointLight(PointLight pointLight)
	{
		this(new Vector3f(pointLight.getColour()), new Vector3f(pointLight.getPosition()), pointLight.getIntensity(), pointLight.getAttenuation());
	}
	
	public Vector3f getColour()
	{
		return colour;
	}
	
	public void setColour(Vector3f colour)
	{
		this.colour = colour;
	}
	
	public Vector3f getPosition()
	{
		return this.position;
	}
	
	public void setPosition(Vector3f position)
	{
		this.position = position;
	}
	
	public float getIntensity()
	{
		return this.intensity;
	}
	
	public void setIntensity(float intensity)
	{
		this.intensity = intensity;
	}
	
	public Attenuation getAttenuation()
	{
		return attenuation;
	}
	
	public void setAttenuation(Attenuation attenuation)
	{
		this.attenuation = attenuation;
	}
	
	public static class Attenuation
	{
		private float constant, linear, exponent;
		
		public Attenuation(float constant, float linear, float exponent)
		{
			this.constant = constant;
			this.linear = linear;
			this.exponent = exponent;
		}
		
		public float getConstant()
		{
			return constant;
		}
		
		public void setConstant(float constant)
		{
			this.constant = constant;
		}
		
		public float getLinear()
		{
			return linear;
		}
		
		public void setLinear(float linear)
		{
			this.linear = linear;
		}
		
		public float getExponent()
		{
			return exponent;
		}
		
		public void setExponent(float exponent)
		{
			this.exponent = exponent;
		}
	}
}
