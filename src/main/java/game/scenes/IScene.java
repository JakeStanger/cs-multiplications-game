package game.scenes;

import engine.MouseInput;
import engine.Window;

/**
 * @author Jake stanger
 * Interface for a scene
 */
public interface IScene
{
	void init(Window window) throws Exception;
	void update(float interval, MouseInput mouseInput);
	void input(Window window, MouseInput mouseInput);
	void render(Window window);
	void cleanup();
}
