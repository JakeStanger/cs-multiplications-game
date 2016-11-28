package engine.graph.particles;

import engine.graph.Mesh;
import engine.graph.Texture;
import engine.items.GameItem;
import org.joml.Vector3f;

/**
 * @author Jake stanger
 *         TODO Write JavaDoc
 */
public class Particle extends GameItem
{
	private Vector3f speed;
	
	/**
	 * Number of milliseconds the particle
	 * has left to live.
	 */
	private long timeToLive;
	
	private long updateTextureMillis;
	
	private long currentAnimTimeMillis;
	
	private int animFrames;
	
	public Particle(Mesh mesh, Vector3f speed, long timeToLive, long updateTextureMillis)
	{
		super(mesh);
		this.speed = new Vector3f(speed);
		this.timeToLive = timeToLive;
		
		this.updateTextureMillis = updateTextureMillis;
		this.currentAnimTimeMillis = 0;
		
		Texture texture = this.getMesh().getMaterial().getTexture();
		this.animFrames = texture.getNumCols() * texture.getNumRows();
	}
	
	public Particle(Particle baseParticle)
	{
		super(baseParticle.getMesh());
		
		this.speed = new Vector3f(baseParticle.speed);
		this.timeToLive = baseParticle.getTimeToLive();
		
		Vector3f aux = baseParticle.getPosition();
		this.setPosition(aux.x, aux.y, aux.z);
		
		this.setRotation(baseParticle.getRotation());
		
		this.setScale(baseParticle.getScale());
	}
	
	public Vector3f getSpeed()
{
	return speed;
}
	
	public void setSpeed(Vector3f speed)
	{
		this.speed = speed;
	}
	
	public long getTimeToLive()
	{
		return timeToLive;
	}
	
	public void setTimeToLive(long timeToLive)
	{
		this.timeToLive = timeToLive;
	}
	
	/**
	 * Updates the Particle's TTL
	 * @param elapsedTime Elapsed Time in milliseconds
	 * @return The Particle's TTL
	 */
	public long updateTimeToLive(long elapsedTime)
	{
		this.timeToLive -= elapsedTime;
		
		this.currentAnimTimeMillis += elapsedTime;
		if(this.currentAnimTimeMillis >= this.updateTextureMillis && this.animFrames > 0)
		{
			this.currentAnimTimeMillis = 0;
			int pos = this.getTexturePos() + 1;
			
			if(pos < this.animFrames) this.setTexturePos(pos);
			else this.setTexturePos(0);
		}
		
		return this.timeToLive;
	}
	
	public long getUpdateTextureMillis()
	{
		return updateTextureMillis;
	}
	
	public void setUpdateTextureMillis(long updateTextureMillis)
	{
		this.updateTextureMillis = updateTextureMillis;
	}
}
