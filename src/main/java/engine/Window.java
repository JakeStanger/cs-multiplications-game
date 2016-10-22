package engine;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author Jake stanger
 * Game window class.
 */
public class Window
{
	private final String title;
	private int width, height;
	
	private long windowHandle;
	
	private GLFWErrorCallback errorCallback;
	
	private GLFWKeyCallback keyCallback;
	
	private GLFWWindowSizeCallback windowSizeCallback;
	
	private boolean resized;
	private boolean vSync;
	
	private WindowOptions opts;
	
	public Window(String title, int width, int height, boolean vSync, WindowOptions opts)
	{
		this.title = title;
		this.width = width;
		this.height = height;
		this.vSync = vSync;
		this.resized = false;
		this.opts = opts;
	}
	
	public void init()
	{
		glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
		
		if(!glfwInit())
		{
			throw new IllegalStateException("Unable to initialise GLFW");
		}
		
		//Set window properties
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		
		if(opts.compatibleProfile) glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
		else
		{
			glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
			glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		}
		
		//Create window
		windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
		if (windowHandle == NULL) throw new RuntimeException("Failed to create the GLFW window");
		
		//Window resize callback
		glfwSetWindowSizeCallback(windowHandle, windowSizeCallback = new GLFWWindowSizeCallback()
		{
			@Override
			public void invoke(long window, int width, int height)
			{
				Window.this.width = width;
				Window.this.height = height;
				Window.this.setResized(true);
			}
		});
		
		//Keypress callback
		glfwSetKeyCallback(windowHandle, keyCallback = new GLFWKeyCallback()
		{
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods)
			{
				if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) glfwSetWindowShouldClose(window, true);
			}
		});
		
		//Get primary monitor resolution
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		
		//Centre window
		glfwSetWindowPos(windowHandle, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2
		);
		
		// Make the OpenGL context current
		glfwMakeContextCurrent(windowHandle);
		
		if (isvSync()) glfwSwapInterval(1); //Enable vSync if this a thing we should do
		
		//Make window visible
		glfwShowWindow(windowHandle);
		
		GL.createCapabilities();
		
		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		glEnable(GL_DEPTH_TEST); //Enable depth rendering (so 3D shapes have a perspective)
		
		//Enable transparency
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		//Cull non-visible faces
		if(this.opts.cullFace)
		{
			glEnable(GL_CULL_FACE);
			glCullFace(GL_BACK);
		}
		
		if(this.opts.showTriangles) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE); //Wireframe rendering
		
	}
	
	public void setClearColour(float r, float g, float b, float a)
	{
		glClearColor(r, g, b, a);
	}
	
	public boolean isKeyPressed(int keyCode)
	{
		return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
	}
	
	public boolean windowShouldClose()
	{
		return glfwWindowShouldClose(windowHandle);
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public long getWindowHandle()
	{
		return this.windowHandle;
	}
	
	public boolean isResized()
	{
		return resized;
	}
	
	public void setResized(boolean resized)
	{
		this.resized = resized;
	}
	
	public boolean isvSync()
	{
		return vSync;
	}
	
	public void setvSync(boolean vSync)
	{
		this.vSync = vSync;
	}
	
	public void setWindowTitle(String title)
	{
		glfwSetWindowTitle(windowHandle, title);
	}
	
	public WindowOptions getWindowOptions()
	{
		return this.opts;
	}
	
	public void update()
	{
		glfwSwapBuffers(windowHandle);
		glfwPollEvents();
	}
	
	public static class WindowOptions
	{
		public boolean cullFace, showTriangles, showFps, compatibleProfile;
	}
}
