package engine.sound;

import org.joml.Vector3f;

import static org.lwjgl.openal.AL10.*;

/**
 * @author Jake stanger
 *         TODO Write JavaDoc
 */
public class SoundListener
{
	public SoundListener()
	{
		this(new Vector3f(0, 0, 0));
	}
	
	public SoundListener(Vector3f pos)
	{
		alListener3f(AL_POSITION, pos.x, pos.y, pos.z);
		alListener3f(AL_VELOCITY, 0, 0, 0);
	}
	
	public void setSpeed(Vector3f speed)
	{
		alListener3f(AL_VELOCITY, speed.x, speed.y, speed.z);
	}
	
	public void setPosition(Vector3f pos)
	{
		alListener3f(AL_POSITION, pos.x, pos.y, pos.z);
	}
	
	/**
	 *
	 * @param at The position where the listener is facing
	 * @param up The up direction for the listener
	 */
	public void setOrientation(Vector3f at, Vector3f up)
	{
		final int NUM_DIRECTIONS = 6;
		float[] data = new float[NUM_DIRECTIONS]; //6 directions
		
		int i = 0;
		
		data[i] = at.x;
		data[i++] = at.y;
		data[i++] = at.z;
		
		data[i++] = up.x;
		data[i++] = up.y;
		data[i] = up.z;
		
		alListenerfv(AL_ORIENTATION, data);
	}
}
