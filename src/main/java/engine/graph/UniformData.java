package engine.graph;

import java.nio.FloatBuffer;

/**
 * @author Jake stanger
 * Wrapper class for uniform data
 */
public class UniformData
{
	private final int uniformLocation;
	
	private FloatBuffer floatBuffer;
	
	public UniformData(int uniformLocation)
	{
		this.uniformLocation = uniformLocation;
	}
	
	public int getUniformLocation()
	{
		return uniformLocation;
	}
	
	public FloatBuffer getFloatBuffer()
	{
		return floatBuffer;
	}
	
	public void setFloatBuffer(FloatBuffer floatBuffer)
	{
		this.floatBuffer = floatBuffer;
	}
}
