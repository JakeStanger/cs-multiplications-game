package engine.sound;

import engine.Utils;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbisInfo;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author Jake stanger
 *         TODO Write JavaDoc
 */
public class SoundBuffer
{
	private final int bufferID;
	
	public SoundBuffer(String file) throws Exception
	{
		this.bufferID = alGenBuffers();
		
		try (STBVorbisInfo info = STBVorbisInfo.malloc())
		{
			ShortBuffer pcm = readVorbis(file, 32 * 1024, info);
			
			//Copy to buffer
			alBufferData(bufferID, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm, info.sample_rate());
		}
	}
	
	public int getBufferID()
	{
		return this.bufferID;
	}
	
	public void cleanup()
	{
		alDeleteBuffers(this.bufferID);
	}
	
	/**
	 * Reads an Ogg Vorbis file.
	 * Taken from the OpenAL test code.
	 * @param resource
	 * @param bufferSize
	 * @param info
	 * @return
	 * @throws Exception
	 */
	private ShortBuffer readVorbis(String resource, int bufferSize, STBVorbisInfo info) throws Exception
	{
		ByteBuffer vorbis;
		vorbis = Utils.ioResourceToByteBuffer(resource, bufferSize);
		
		IntBuffer error = BufferUtils.createIntBuffer(1);
		
		long decoder = stb_vorbis_open_memory(vorbis, error, null);
		if(decoder == NULL)  throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
		
		stb_vorbis_get_info(decoder, info);
		
		int channels = info.channels();
		int lengthSamples = stb_vorbis_stream_length_in_samples(decoder) * channels;
		ShortBuffer pcm = BufferUtils.createShortBuffer(lengthSamples);
		
		pcm.limit(stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels);
		stb_vorbis_close(decoder);
		
		return pcm;
	}
}
