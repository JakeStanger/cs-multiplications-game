package engine.sound;

import org.joml.Vector3f;

import static org.lwjgl.openal.AL10.*;

/**
 * @author Jake stanger
 *         TODO Write JavaDoc
 */
public class SoundSource
{
	private final int sourceID;
	
	public SoundSource(boolean loop, boolean relative)
	{
		this.sourceID = alGenSources();
		
		if(loop) alSourcei(sourceID, AL_LOOPING, AL_TRUE);
		if(relative) alSourcei(sourceID, AL_SOURCE_RELATIVE, AL_TRUE);
	}
	
	public void setBuffer(int bufferID)
	{
		stop();
		alSourcei(sourceID, AL_BUFFER, bufferID);
	}
	
	public void setPosition(Vector3f pos)
	{
		alSource3f(sourceID, AL_POSITION, pos.x, pos.y, pos.z);
	}
	
	public void setSpeed(Vector3f speed)
	{
		alSource3f(sourceID, AL_VELOCITY, speed.x, speed.y, speed.z);
	}
	
	public void setGain(float gain)
	{
		alSourcef(sourceID, AL_GAIN, gain);
	}
	
	public void setProperty(int param, float value)
	{
		alSourcef(sourceID, param, value);
	}
	
	public void play()
	{
		alSourcePlay(sourceID);
	}
	
	public boolean isPlaying()
	{
		return alGetSourcei(sourceID, AL_SOURCE_STATE) == AL_PLAYING;
	}
	
	public void pause()
	{
		alSourcePlay(sourceID);
	}
	
	public void stop()
	{
		alSourceStop(sourceID);
	}
	
	public void cleanup()
	{
		stop();
		alDeleteSources(sourceID);
	}
}
