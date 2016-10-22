package engine.graph;

import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

/**
 * @author Jake stanger
 * Handles texture files to be used for meshes.
 * Only supports PNGs.
 */
public class Texture
{
	private final int id;
	private final int width, height;
	private int numRows = 1;
	
	private int numCols = 1;
	
	public Texture(String fileName) throws Exception
	{
		this(Texture.class.getResourceAsStream(fileName));
	}
	
	public Texture(InputStream inputStream) throws Exception
	{
		//Load Texture file
		PNGDecoder decoder = new PNGDecoder(inputStream);
		
		this.width = decoder.getWidth();
		this.height = decoder.getHeight();
		
		//Load texture contents into a byte buffer
		ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
		decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
		buf.flip();
		
		//Create a new OpenGL texture
		int textureId = glGenTextures();
		
		//Bind the texture
		glBindTexture(GL_TEXTURE_2D, textureId);
		
		//Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size.
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		//Improve text readability
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		//Upload the texture data
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0,GL_RGBA, GL_UNSIGNED_BYTE, buf);
		
		glGenerateMipmap(GL_TEXTURE_2D); //Generate Mip Map
		
		this.id = textureId;
	}
	
	public Texture(int width, int height, int pixelFormat) throws Exception
	{
		this.id = glGenTextures();
		this.width = width;
		this.height = height;
		
		glBindTexture(GL_TEXTURE_2D, this.id);
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, this.width, this.height, 0, pixelFormat, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		//Stop textures from repeating
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	}
	
	public Texture(String fileName, int numCols, int numRows) throws Exception
	{
		this(fileName);
		this.numCols = numCols;
		this.numRows = numRows;
	}
	
	public void bind()
	{
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	public int getID()
	{
		return this.id;
	}
	
	public int getWidth()
	{
		return this.width;
	}
	
	public int getHeight()
	{
		return this.height;
	}
	
	public int getNumRows()
	{
		return numRows;
	}
	
	public int getNumCols()
	{
		return numCols;
	}
	
	public void cleanup()
	{
		glDeleteTextures(id);
	}
}
