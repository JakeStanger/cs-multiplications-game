package engine.graph;

import engine.Utils;
import engine.items.GameItem;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author Jake stanger
 * GameObject render mesh.
 * Defines the shape of the object,
 * it's position in the world,
 * and it's texture
 */
public class Mesh
{
	public static final int MAX_WEIGHTS = 4; //Must be updated in depth and scene vertex shader too
	
	protected final int vaoID, vertexCount;
	protected final List<Integer> vboIDList;
	
	private Material material;
	
	public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices)
	{
		this(positions, textCoords, normals, indices,
				Utils.createEmptyIntArray(MAX_WEIGHTS * positions.length / 3, 0), Utils.createEmptyFloatArray(MAX_WEIGHTS * positions.length / 3, 0));
	}
	
	public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices, int[] jointIndices, float[] weights)
	{
		vertexCount = indices.length;
		
		vboIDList = new ArrayList<>();
		
		//Bind VAO
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);
		
		//Create and bind position VBO
		int vboID = glGenBuffers();
		vboIDList.add(vboID);
		
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(positions.length);
		verticesBuffer.put(positions).flip();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		//Create and bind texture coordinates VBO
		vboID = glGenBuffers();
		vboIDList.add(vboID);
		
		FloatBuffer textCoordsBuffer = BufferUtils.createFloatBuffer(textCoords.length);
		textCoordsBuffer.put(textCoords).flip();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		
		//Create and bind vertex normals VBO
		vboID = glGenBuffers();
		vboIDList.add(vboID);
		
		FloatBuffer vecNormalsBuffer = BufferUtils.createFloatBuffer(normals.length);
		vecNormalsBuffer.put(normals).flip();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		
		//Create and bind weights
		vboID = glGenBuffers();
		vboIDList.add(vboID);
		
		FloatBuffer weightsBuffer = BufferUtils.createFloatBuffer(weights.length);
		weightsBuffer.put(weights).flip();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, weightsBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(3, 4, GL_FLOAT, false, 0, 0);
		
		//Create and bind joint indices
		vboID = glGenBuffers();
		vboIDList.add(vboID);
		
		IntBuffer jointIndicesBuffer = BufferUtils.createIntBuffer(jointIndices.length);
		jointIndicesBuffer.put(jointIndices).flip();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, jointIndicesBuffer, GL_STATIC_DRAW);
		glVertexAttribPointer(4, 4, GL_FLOAT, false, 0, 0);
		
		
		//Create and bind index VBO
		vboID = glGenBuffers();
		vboIDList.add(vboID);
		
		IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
		indicesBuffer.put(indices).flip();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
		
		glBindVertexArray(0);
	}
	
	public Material getMaterial()
	{
		return material;
	}
	
	public void setMaterial(Material material)
	{
		this.material = material;
	}
	
	public int getVaoID()
	{
		return this.vaoID;
	}
	
	public int getVertexCount()
	{
		return vertexCount;
	}
	
	/**
	 * Render setup
	 */
	protected void initRender()
	{
		Texture texture = this.material.getTexture();
		if(texture != null)
		{
			glActiveTexture(GL_TEXTURE0); //Activate first texture unit
			glBindTexture(GL_TEXTURE_2D, texture.getID());
		}
		
		Texture normalMap = material.getNormalMap();
		if(normalMap != null)
		{
			glActiveTexture(GL_TEXTURE1);
			glBindTexture(GL_TEXTURE_2D, normalMap.getID());
		}
		
		//Draw the mesh
		glBindVertexArray(this.getVaoID());
		glEnableVertexAttribArray(0); //TODO Create constants for magic number IDs
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
		glEnableVertexAttribArray(4);
	}
	
	/**
	 * Render finish
	 */
	protected void endRender()
	{
		//Restore state
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);
		glDisableVertexAttribArray(4);
		
		glBindVertexArray(0);
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	/**
	 * Render the mesh
	 */
	public void render()
	{
		initRender();
		glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
		endRender();
	}
	
	public void renderList(List<GameItem> gameItems, Consumer<GameItem> consumer)
	{
		initRender();
		
		for(GameItem gameItem : gameItems)
		{
			consumer.accept(gameItem);
			glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
		}
		
		endRender();
	}
	
	/**
	 * Clear the mesh from the GPU
	 */
	public void cleanup()
	{
		this.deleteVBOs();
		if(material.isTextured()) material.getTexture().cleanup();
		this.deleteVAO();
	}
	
	public void deleteBuffers()
	{
		this.deleteVBOs();
		this.deleteVAO();
		
	}
	
	private void deleteVBOs()
	{
		glDisableVertexAttribArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		vboIDList.forEach(GL15::glDeleteBuffers);
	}
	
	private void deleteVAO()
	{
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoID);
	}
}
