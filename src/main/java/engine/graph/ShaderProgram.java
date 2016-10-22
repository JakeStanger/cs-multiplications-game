package engine.graph;

import engine.graph.lights.DirectionalLight;
import engine.graph.lights.PointLight;
import engine.graph.lights.SpotLight;
import engine.graph.weather.Fog;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

/**
 * @author Jake stanger
 * Handles loading and binding of shader files.
 */
public class ShaderProgram
{
	private final int programID;
	private int vertexShaderID, fragmentShaderID;
	
	private final Map<String, UniformData> uniforms;
	
	public ShaderProgram() throws Exception
	{
		programID = glCreateProgram();
		if(programID == 0) throw new Exception("Could not create shader");
		
		uniforms = new HashMap<>();
	}
	
	public void createVertexShader(String shaderCode) throws Exception
	{
		vertexShaderID = createShader(shaderCode, GL_VERTEX_SHADER);
	}
	
	public void createFragmentShader(String shaderCode) throws Exception
	{
		fragmentShaderID = createShader(shaderCode, GL_FRAGMENT_SHADER);
	}
	
	protected int createShader(String shaderCode, int shaderType) throws Exception
	{
		int shaderID = glCreateShader(shaderType);
		if(shaderID == 0) throw new Exception("Error creating shader with code " + shaderID);
		
		glShaderSource(shaderID, shaderCode);
		glCompileShader(shaderID);
		
		if(glGetShaderi(shaderID, GL_COMPILE_STATUS) == 0)
			throw new Exception("Error compiling shader with code " + glGetShaderInfoLog(shaderID, 1024));
		
		glAttachShader(programID, shaderID);
		
		return shaderID;
	}
	
	public void link() throws Exception
	{
		glLinkProgram(programID);
		
		if (glGetProgrami(programID, GL_LINK_STATUS) == 0)
			throw new Exception("Error linking shader with code " + glGetProgramInfoLog(programID, 1024));
		
		glValidateProgram(programID);
		if (glGetProgrami(programID, GL_VALIDATE_STATUS) == 0)
			System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programID, 1024));
		
	}
	
	public void bind()
	{
		glUseProgram(programID);
	}
	
	public void unbind()
	{
		glUseProgram(0);
	}
	
	public void cleanup()
	{
		unbind();
		if(programID != 0)
		{
			if(vertexShaderID != 0) glDetachShader(programID, vertexShaderID);
			if(fragmentShaderID != 0) glDetachShader(programID, fragmentShaderID);
			glDeleteProgram(programID);
		}
	}
	
	public void createUniform(String uniformName) throws Exception
	{
		int uniformLocation = glGetUniformLocation(programID, uniformName);
		if(uniformLocation < 0) throw new Exception("Could not find uniform: " + uniformName);
		
		uniforms.put(uniformName, new UniformData(uniformLocation));
	}
	
	public void createPointLightUniform(String uniformName) throws Exception
	{
		createUniform(uniformName + ".colour");
		createUniform(uniformName + ".position");
		createUniform(uniformName + ".intensity");
		createUniform(uniformName + ".att.constant");
		createUniform(uniformName + ".att.linear");
		createUniform(uniformName + ".att.exponent");
	}
	
	public void createSpotLightUniform(String uniformName) throws Exception
	{
		createPointLightUniform(uniformName  + ".pl");
		createUniform(uniformName + ".conedir");
		createUniform(uniformName + ".cutoff");
	}
	
	public void createPointLightListUniform(String uniformName, int size) throws Exception
	{
		for (int i = 0; i < size; i++) this.createPointLightUniform(uniformName + "[" + i + "]");
	}
	
	public void createSpotLightListUniform(String uniformName, int size) throws Exception
	{
		for (int i = 0; i < size; i++) this.createSpotLightUniform(uniformName + "[" + i + "]");
	}
	
	public void createMaterialUniform(String uniformName) throws Exception
	{
		createUniform(uniformName + ".colour");
		createUniform(uniformName + ".useColour");
		createUniform(uniformName + ".reflectance");
		createUniform(uniformName + ".hasNormalMap");
	}
	
	public void createDirectionalLightUniform(String uniformName) throws Exception
	{
		createUniform(uniformName + ".colour");
		createUniform(uniformName + ".direction");
		createUniform(uniformName + ".intensity");
	}
	
	public void createFogUniform(String uniformName) throws Exception
	{
		createUniform(uniformName + ".activeFog");
		createUniform(uniformName + ".colour");
		createUniform(uniformName + ".density");
	}
	
	public void setUniform(String uniformName, Matrix4f value)
	{
		UniformData uniformData = uniforms.get(uniformName);
		
		//Dump matrix into float buffer
		FloatBuffer buffer = BufferUtils.createFloatBuffer(4*4);
		value.get(buffer);
		glUniformMatrix4fv(uniformData.getUniformLocation(), false, buffer);
	}
	
	public void setUniform(String uniformName, int value)
	{
		glUniform1i(uniforms.get(uniformName).getUniformLocation(), value);
	}
	
	public void setUniform(String uniformName, float value)
	{
		glUniform1f(uniforms.get(uniformName).getUniformLocation(), value);
	}
	
	public void setUniform(String uniformName, Vector3f value)
	{
		glUniform3f(uniforms.get(uniformName).getUniformLocation(), value.x, value.y, value.z);
	}
	
	public void setUniform(String uniformName, Material material)
	{
		setUniform(uniformName + ".colour", material.getColour());
		setUniform(uniformName + ".useColour", material.isTextured() ? 0 : 1);
		setUniform(uniformName + ".reflectance", material.getReflectance());
		setUniform(uniformName + ".hasNormalMap", material.hasNormalMap() ? 0 : 1);
	}
	
	public void setUniform(String uniformName, PointLight pointLight)
	{
		setUniform(uniformName + ".colour", pointLight.getColour());
		setUniform(uniformName + ".position", pointLight.getPosition());
		setUniform(uniformName + ".intensity", pointLight.getIntensity());
		PointLight.Attenuation att = pointLight.getAttenuation();
		setUniform(uniformName + ".att.constant", att.getConstant());
		setUniform(uniformName + ".att.linear", att.getLinear());
		setUniform(uniformName + ".att.exponent", att.getExponent());
	}
	
	public void setUniform(String uniformName, SpotLight spotLight)
	{
		setUniform(uniformName + ".pl", spotLight.getPointLight() );
		setUniform(uniformName + ".conedir", spotLight.getConeDirection());
		setUniform(uniformName + ".cutoff", spotLight.getCutOff());
	}
	
	public void setUniform(String uniformName, DirectionalLight dirLight)
	{
		setUniform(uniformName + ".colour", dirLight.getColour() );
		setUniform(uniformName + ".direction", dirLight.getDirection());
		setUniform(uniformName + ".intensity", dirLight.getIntensity());
	}
	
	public void setUniform(String uniformName, PointLight[] pointLights)
	{
		int numLights = pointLights != null ? pointLights.length : 0;
		for (int i = 0; i < numLights; i++) setUniform(uniformName, pointLights[i], i);
	}
	
	public void setUniform(String uniformName, PointLight pointLight, int pos)
	{
		setUniform(uniformName + "[" + pos + "]", pointLight);
	}
	
	public void setUniform(String uniformName, SpotLight[] spotLights)
	{
		int numLights = spotLights != null ? spotLights.length : 0;
		for (int i = 0; i < numLights; i++) setUniform(uniformName, spotLights[i], i);
	}
	
	public void setUniform(String uniformName, SpotLight spotLight, int pos)
	{
		setUniform(uniformName + "[" + pos + "]", spotLight);
	}
	
	public void setUniform(String uniformName, Fog fog)
	{
		setUniform(uniformName + ".activeFog", fog.isActive() ? 1 : 0);
		setUniform(uniformName + ".colour", fog.getColour() );
		setUniform(uniformName + ".density", fog.getDensity());
	}
	
	public void setUniform(String uniformName, Matrix4f[] matrices)
	{
		int length = matrices != null ? matrices.length : 0;
		UniformData uniformData = uniforms.get(uniformName);
		
		if(uniformData == null) throw new RuntimeException("Uniform [" + uniformName + "] has not been created");
		
		//Check if float buffer has been created
		FloatBuffer buffer = uniformData.getFloatBuffer();
		if(buffer == null)
		{
			buffer = BufferUtils.createFloatBuffer(16 * length);
			uniformData.setFloatBuffer(buffer);
		}
		
		for(int i = 0; i < length; i++) matrices[i].get(16 * i, buffer);
		
		glUniformMatrix4fv(uniformData.getUniformLocation(), false, buffer);
	}
}
