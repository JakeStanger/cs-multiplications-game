package game.wrappers;

/**
 * @author Jake stanger
 * Wrapper class for name and score
 * entry for leaderboard information.
 */
public class LeaderboardEntry
{
	private String name;
	private int score;
	
	public LeaderboardEntry(String name, int score)
	{
		this.name = name;
		this.score = score;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public int getScore()
	{
		return score;
	}
	
	public void setScore(int score)
	{
		this.score = score;
	}
}
