package game;

import game.wrappers.LeaderboardEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jake stanger
 * Handles leaderboard database querying
 */
public class Database
{
	private static final String url = "jdbc:mysql://localhost:3306/3d_snake";
	private static final String username = "java";
	private static final String password = "JS2tgLzXIbFmZxMv";
	
	public static List<LeaderboardEntry> readAllEntries()
	{
		try
		{
			Connection connection = Database.connectToDatabase();
			Statement statement = connection.createStatement();
			
			ResultSet resultSet = statement.executeQuery("SELECT * FROM default_scores ORDER BY Score DESC");
			
			List<LeaderboardEntry> leaderboardEntries = new ArrayList<>();
			while(resultSet.next())
			{
				LeaderboardEntry entry = new LeaderboardEntry(resultSet.getString("Name"), Integer.parseInt(resultSet.getString("Score")));
				leaderboardEntries.add(entry);
			}
			
			connection.close();
			
			return leaderboardEntries;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void addEntry(LeaderboardEntry entry)
	{
		try
		{
			Connection connection = Database.connectToDatabase();
			Statement statement = connection.createStatement();
			statement.executeUpdate("INSERT INTO `default_scores` (Name, Score) VALUES('" +
					entry.getName() + "'," + entry.getScore() + ")");
			connection.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static Connection connectToDatabase() throws SQLException
	{
		return DriverManager.getConnection(url, username, password);
	}
}
