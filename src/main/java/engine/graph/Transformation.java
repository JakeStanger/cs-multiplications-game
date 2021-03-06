package engine.graph;

import engine.items.GameItem;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * @author Jake stanger
 * Handles object view transformation depending on world position.
 * Also depends on the FOV.
 * Holds several matricies depending on view model being used.
 */
public class Transformation
{
	private final Matrix4f projectionMatrix, modelMatrix, modelViewMatrix, modelLightViewMatrix,
			lightViewMatrix, orthoProjMatrix, ortho2DMatrix, orthoModelMatrix;
	
	public Transformation()
	{
		projectionMatrix = new Matrix4f();
		modelMatrix = new Matrix4f();
		modelViewMatrix = new Matrix4f();
		modelLightViewMatrix = new Matrix4f();
		orthoProjMatrix = new Matrix4f();
		ortho2DMatrix = new Matrix4f();
		orthoModelMatrix = new Matrix4f();
		lightViewMatrix = new Matrix4f();
	}
	
	public Matrix4f getProjectionMatrix()
	{
		return projectionMatrix;
	}
	
	public Matrix4f updateProjectionMatrix(float fov, float width, float height, float zNear, float zFar)
	{
		float aspectRatio = width / height;
		return projectionMatrix.setPerspective(fov, aspectRatio, zNear, zFar);
	}
	
	public final Matrix4f getOrthoProjectionMatrix()
	{
		return orthoProjMatrix;
	}
	
	public Matrix4f updateOrthoProjectionMatrix(float left, float right, float bottom, float top, float zNear, float zFar)
	{
		return orthoProjMatrix.setOrtho(left, right, bottom, top, zNear, zFar);
	}
	
	public Matrix4f getLightViewMatrix()
	{
		return lightViewMatrix;
	}
	
	public Matrix4f updateLightViewMatrix(Vector3f position, Vector3f rotation)
	{
		return updateGenericViewMatrix(position, rotation, lightViewMatrix);
	}
	
	public static Matrix4f updateGenericViewMatrix(Vector3f position, Vector3f rotation, Matrix4f matrix)
	{
		return matrix.rotationX((float)Math.toRadians(rotation.x))
				.rotateY((float)Math.toRadians(rotation.y))
				.translate(-position.x, -position.y, -position.z);
	}
	
	public final Matrix4f getOrtho2DProjectionMatrix(float left, float right, float bottom, float top)
	{
		ortho2DMatrix.identity();
		ortho2DMatrix.setOrtho2D(left, right, bottom, top);
		return ortho2DMatrix;
	}
	
	public Matrix4f buildModelMatrix(GameItem gameItem)
	{
		Quaternionf rotation = gameItem.getRotation();
		return modelMatrix.translationRotateScale(
				gameItem.getPosition().x, gameItem.getPosition().y, gameItem.getPosition().z,
				rotation.x, rotation.y, rotation.z, rotation.w,
				gameItem.getScale(), gameItem.getScale(), gameItem.getScale());
	}
	
	public Matrix4f buildModelViewMatrix(GameItem gameItem, Matrix4f matrix)
	{
		return buildModelViewMatrix(buildModelMatrix(gameItem), matrix);
	}
	
	public Matrix4f buildModelViewMatrix(Matrix4f modelMatrix, Matrix4f viewMatrix)
	{
		return viewMatrix.mulAffine(modelMatrix, modelViewMatrix);
	}
	
	public Matrix4f buildModelLightViewMatrix(Matrix4f modelMatrix, Matrix4f lightViewMatrix)
	{
		return lightViewMatrix.mulAffine(modelMatrix, modelLightViewMatrix);
	}
	
	public Matrix4f buildOrthoProjectionModelMatrix(GameItem gameItem, Matrix4f orthoMatrix)
	{
		return orthoMatrix.mulOrthoAffine(buildModelMatrix(gameItem), orthoModelMatrix);
	}
}
