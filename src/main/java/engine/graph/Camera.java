package engine.graph;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * @author Jake stanger
 * Handles world object offset depending on camera location.
 * OpenGL does not support a camera object, so we have to move the
 * entire world around us.
 */
public class Camera
{
	private final Vector3f position, rotation;
	private Matrix4f viewMatrix;
	
	/**
	 * Create a new forward-facing camera at 0, 0, 0
	 */
	public Camera()
	{
		position = new Vector3f(0, 0, 0);
		rotation = new Vector3f(0, 0, 0);
		viewMatrix = new Matrix4f();
	}
	
	/**
	 * Create a new camera with a pre-defined rotation and position
	 * @param position
	 * @param rotation
	 */
	public Camera(Vector3f position, Vector3f rotation)
	{
		this.position = position;
		this.rotation = rotation;
	}
	
	public Vector3f getPosition()
	{
		return position;
	}
	
	/**
	 * Moves the camera to the specified coordinates
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setPosition(float x, float y, float z)
	{
		position.x = x;
		position.y = y;
		position.z = z;
	}
	
	/**
	 * Moves the camera by the specified amount
	 * @param offsetX
	 * @param offsetY
	 * @param offsetZ
	 */
	public void movePosition(float offsetX, float offsetY, float offsetZ)
	{
		if(offsetZ != 0)
		{
			position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
			position.z += (float) Math.cos(Math.toRadians(rotation.y)) * offsetZ;
		}
		if(offsetX != 0)
		{
			position.x += (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
			position.z += (float)Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
		}
		
		position.y += offsetY;
	}
	
	public Vector3f getRotation()
	{
		return this.rotation;
	}
	
	/**
	 * Sets the camera rotation to the specified values
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setRotation(float x, float y, float z)
	{
		rotation.x = x;
		rotation.y = y;
		rotation.z = z;
	}
	
	/**
	 * Changes the camera rotation by the specified amount
	 * @param offsetX
	 * @param offsetY
	 * @param offsetZ
	 */
	public void moveRotation(float offsetX, float offsetY, float offsetZ)
	{
		rotation.x += offsetX;
		rotation.y += offsetY;
		rotation.z += offsetZ;
	}
	
	public Matrix4f getViewMatrix()
	{
		return viewMatrix;
	}
	
	public Matrix4f updateViewMatrix()
	{
		return Transformation.updateGenericViewMatrix(position, rotation, viewMatrix);
	}
}
