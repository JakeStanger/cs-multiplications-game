package engine;

/**
 * @author Jake stanger
 * Contains base methods to be used by any game
 * implementing the engine
 */
public interface IGameLogic
{
	void init(Window window) throws Exception;
	void input(Window window, MouseInput mouseInput);
	void update(float interval, MouseInput mouseInput);
	void render(Window window);
	void cleanup();
}
