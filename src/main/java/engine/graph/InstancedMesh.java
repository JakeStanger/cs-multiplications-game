package engine.graph;

import engine.items.GameItem;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

/**
 * @author Jake stanger
 *         TODO Write JavaDoc
 */
public class InstancedMesh extends Mesh
{
	private static final int FLOAT_SIZE_BYTES = 4, VECTOR4F_SIZE_BYTES = 4 * 4, MATRIX_SIZE_BYTES = 4 * VECTOR4F_SIZE_BYTES, MATRIX_SIZE_FLOATS = 4 * 4;
	private static final int INSTANCE_SIZE_BYTES = MATRIX_SIZE_BYTES * 2 + FLOAT_SIZE_BYTES * 2;
	private static final int INSTANCE_SIZE_FLOATS = MATRIX_SIZE_FLOATS * 2 + 2;
	
	
	private final int numInstances, instanceDataVBO;
	private final FloatBuffer instanceDataBuffer;
	
	
	public InstancedMesh(float[] positions, float[] textCoords, float[] normals, int[] indices, int numInstances)
	{
		super(positions, textCoords, normals, indices);
		
		this.numInstances = numInstances;
		
		glBindVertexArray(vaoID);
		
		//Model view matrix
		this.instanceDataVBO = glGenBuffers();
		this.vboIDList.add(instanceDataVBO);
		
		this.instanceDataBuffer = BufferUtils.createFloatBuffer(numInstances * MATRIX_SIZE_FLOATS);
		glBindBuffer(GL_ARRAY_BUFFER, instanceDataVBO);
		
		int start = 5;
		int strideStart = 0;
		for(int i = 0; i < 4; i++)
		{
			glVertexAttribPointer(start, 4, GL_FLOAT, false, INSTANCE_SIZE_BYTES, strideStart);
			glVertexAttribDivisor(start, 1);
			
			start++;
			strideStart += VECTOR4F_SIZE_BYTES;
		}
		
		//Light view matrix
		for (int i = 0; i < 4; i++)
		{
			glVertexAttribPointer(start, 4, GL_FLOAT, false, INSTANCE_SIZE_BYTES, strideStart);
			glVertexAttribDivisor(start, 1);
			start++;
			strideStart += VECTOR4F_SIZE_BYTES;
		}
		
		//Texture offsets
		glVertexAttribPointer(start, 2, GL_FLOAT, false, INSTANCE_SIZE_BYTES, strideStart);
		glVertexAttribDivisor(start, 1);
		
		//Selected
		glVertexAttribPointer(start, 1, GL_FLOAT, false, INSTANCE_SIZE_BYTES, strideStart);
		glVertexAttribDivisor(start, 1);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
	
	@Override
	protected void initRender()
	{
		super.initRender();
		
		int start = 5;
		int numElements = 4 * 2 + 1;
		
		for(int i = 0; i < numElements; i++) glEnableVertexAttribArray(start + i);
	}
	
	@Override
	protected void endRender()
	{
		int start = 5;
		int numElements = 4 * 2 + 1;
		
		for(int i = 0; i < numElements; i++) glDisableVertexAttribArray(start + i);
		
		super.endRender();
	}
	
	public void renderListInstanced(List<GameItem> gameItems, Transformation transformation, Matrix4f viewMatrix, Matrix4f lightViewMatrix)
	{
		renderListInstanced(gameItems, false, transformation, viewMatrix, lightViewMatrix);
	}
	
	public void renderListInstanced(List<GameItem> gameItems, boolean billboard, Transformation transformation, Matrix4f viewMatrix, Matrix4f lightViewMatrix)
	{
		initRender();
		
		int length = gameItems.size();
		
		for(int i = 0; i < length; i += numInstances)
		{
			int end = Math.min(length, i + numInstances);
			List<GameItem> subList = gameItems.subList(i, end);
			renderChunkInstanced(subList, billboard, transformation, viewMatrix, lightViewMatrix);
		}
		
		endRender();
	}
	
	private void renderChunkInstanced(List<GameItem> gameItems, boolean billboard, Transformation transformation, Matrix4f viewMatrix, Matrix4f lightViewMatrix)
	{
		this.instanceDataBuffer.clear();
		
		Texture texture = this.getMaterial().getTexture();
		
		int i = 0;
		for(GameItem gameItem : gameItems)
		{
			Matrix4f modelMatrix = transformation.buildModelMatrix(gameItem);
			
			if(viewMatrix != null)
			{
				if(billboard) viewMatrix.transpose3x3(modelMatrix);
				
				Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(modelMatrix, viewMatrix);
				modelViewMatrix.get(MATRIX_SIZE_FLOATS * i, instanceDataBuffer);
			}
			
			if(lightViewMatrix != null)
			{
				Matrix4f modelLightViewMatrix = transformation.buildModelLightViewMatrix(modelMatrix, lightViewMatrix);
				modelLightViewMatrix.get(MATRIX_SIZE_FLOATS * i, this.instanceDataBuffer);
			}
			
			if(texture != null)
			{
				int col = gameItem.getTextPos() % texture.getNumCols();
				int row = gameItem.getTextPos() / texture.getNumCols();
				
				float textXOffset = (float) col / texture.getNumCols();
				float textYOffset = (float) row / texture.getNumRows();
				
				int buffPos = INSTANCE_SIZE_FLOATS * i + MATRIX_SIZE_FLOATS * 2;
				
				this.instanceDataBuffer.put(buffPos, textXOffset);
				this.instanceDataBuffer.put(buffPos + 1, textYOffset);
			}
			
			//Selected data
			int buffPos = INSTANCE_SIZE_FLOATS * i + MATRIX_SIZE_FLOATS * 2 + 2;
			this.instanceDataBuffer.put(buffPos, gameItem.isSelected() ? 1 : 0);
			
			i++;
		}
		
		glBindBuffer(GL_ARRAY_BUFFER, instanceDataVBO);
		glBufferData(GL_ARRAY_BUFFER, instanceDataBuffer, GL_DYNAMIC_DRAW);
		
		glDrawElementsInstanced(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0, gameItems.size());
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
}
