package engine.graph.particles;

import engine.items.GameItem;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Jake stanger
 * Example implementation of IParticleEmitter
 */
public class FlowParticleEmitter implements IParticleEmitter
{
	private int maxParticles;
	private boolean active;
	private final List<GameItem> particles;
	private final Particle baseParticle;
	private long creationPeriodMillis, lastCreationTime;
	private float speedRndRange, positionRndRange, scaleRndRange;
	private long animRange;
	
	public FlowParticleEmitter(Particle baseParticle, int maxParticles, long creationPeriodMillis)
	{
		particles = new ArrayList<>();
		this.baseParticle = baseParticle;
		this.maxParticles = maxParticles;
		this.active = false;
		this.lastCreationTime = 0;
		this.creationPeriodMillis = creationPeriodMillis;
	}
	
	public void update(long elapsedTime)
	{
		long now = System.currentTimeMillis();
		
		if(lastCreationTime == 0) lastCreationTime = now;
		
		Iterator<? extends GameItem> iterator = particles.iterator();
		while(iterator.hasNext())
		{
			Particle particle = (Particle) iterator.next();
			
			if(particle.updateTimeToLive(elapsedTime) < 0) iterator.remove();
			else this.updatePosition(particle, elapsedTime);
		}
		
		int length = this.getParticles().size();
		if(now - lastCreationTime >= this.creationPeriodMillis && length < maxParticles)
		{
			this.createParticle();
			this.lastCreationTime = now;
		}
	}
	
	private void createParticle()
	{
		Particle particle = new Particle(this.getBaseParticle());
		
		//Randomize particle properties
		float sign = Math.random() > 0.5d ? -1.0f : 1.0f;
		
		float speedInc = sign * (float) Math.random() * this.speedRndRange;
		float posInc = sign * (float) Math.random() * this.positionRndRange;
		float scaleInc = sign * (float) Math.random() * this.scaleRndRange;
		
		particle.getPosition().add(posInc, posInc, posInc);
		particle.getSpeed().add(speedInc, speedInc, speedInc);
		particle.setScale(particle.getScale() + scaleInc);
		
		long updateAnimInc = (long)sign *(long)(Math.random() * (float)this.animRange);
		particle.setUpdateTextureMillis(particle.getUpdateTextureMillis() + updateAnimInc);
		
		particles.add(particle);
	}
	
	/**
	 * Updates a given particle's position
	 * @param particle The particle to update
	 * @param elapsedTime The elapsed time in milliseconds
	 */
	public void updatePosition(Particle particle, long elapsedTime)
	{
		Vector3f speed = particle.getSpeed();
		
		float delta = elapsedTime / 1000f;
		
		float dx = speed.x * delta;
		float dy = speed.y * delta;
		float dz = speed.z * delta;
		
		Vector3f pos = particle.getPosition();
		particle.setPosition(pos.x + dx, pos.y + dy, pos.z + dz);
	}
	
	@Override
	public void cleanup()
	{
		this.getParticles().forEach(GameItem::cleanup);
	}
	
	@Override
	public Particle getBaseParticle()
	{
		return baseParticle;
	}
	
	public long getCreationPeriodMillis()
	{
		return creationPeriodMillis;
	}
	
	public int getMaxParticles()
	{
		return maxParticles;
	}
	
	@Override
	public List<GameItem> getParticles()
	{
		return particles;
	}
	
	public float getPositionRndRange()
	{
		return positionRndRange;
	}
	
	public float getScaleRndRange()
	{
		return scaleRndRange;
	}
	
	public float getSpeedRndRange()
	{
		return speedRndRange;
	}
	
	public void setCreationPeriodMillis(long creationPeriodMillis)
	{
		this.creationPeriodMillis = creationPeriodMillis;
	}
	
	public void setMaxParticles(int maxParticles)
	{
		this.maxParticles = maxParticles;
	}
	
	public void setPositionRndRange(float positionRndRange)
	{
		this.positionRndRange = positionRndRange;
	}
	
	public void setScaleRndRange(float scaleRndRange)
	{
		this.scaleRndRange = scaleRndRange;
	}
	
	public boolean isActive()
	{
		return active;
	}
	
	public void setActive(boolean active)
	{
		this.active = active;
	}
	
	public void setSpeedRndRange(float speedRndRange)
	{
		this.speedRndRange = speedRndRange;
	}
	
	public long getAnimRange()
	{
		return animRange;
	}
	
	public void setAnimRange(long animRange)
	{
		this.animRange = animRange;
	}
}
