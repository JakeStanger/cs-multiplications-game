package engine.graph.particles;

import engine.items.GameItem;

import java.util.List;

/**
 * @author Jake stanger
 *         TODO Write JavaDoc
 */
public interface IParticleEmitter
{
	void cleanup();
	Particle getBaseParticle();
	List<GameItem> getParticles();
}
