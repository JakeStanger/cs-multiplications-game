package engine.graph;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;


/**
 * @author Jake stanger
 *         TODO Write JavaDoc
 */
public class ShadowMap
{
	public static final int SHADOW_MAP_WIDTH = 1024, SHADOW_MAP_HEIGHT = 1024;
	
	private final int depthMapFBO;
	private final Texture depthMap;
	
	public ShadowMap() throws Exception
	{
		depthMapFBO = glGenFramebuffers(); //Create Frame Buffer Object to render depth map
		
		//Create depth map texture
		depthMap = new Texture(SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, GL_DEPTH_COMPONENT);
		
		//Attach depth map texture to FBO
		glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
		
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMap.getID(), 0);
		
		//Set only depth, do not render colour
		glDrawBuffer(GL_NONE);
		glReadBuffer(GL_NONE);
		
		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) throw new Exception("Could not create FrameBuffer");
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0); //Unbind
	}
	
	public Texture getDepthMapTexture()
	{
		return depthMap;
	}
	
	public int getDepthMapFBO()
	{
		return depthMapFBO;
	}
	
	public void cleanup()
	{
		glDeleteFramebuffers(depthMapFBO);
		depthMap.cleanup();
	}
}
