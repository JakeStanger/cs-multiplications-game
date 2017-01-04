package game.utils;

import com.google.gson.*;
import game.scenes.Options;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Jake stanger
 * Handles reading and writing of options
 * to file.
 */
public class OptionsIO
{
	/**
	 * Writes the  values of Options.Values
	 * to the options file
	 */
	public static void writeToFile()
	{
		JsonObject object = new JsonObject();
		object.addProperty("name", new String(Options.Values.name));
		
		object.addProperty("muteSound", Options.Values.muteSound);
		object.addProperty("muteMusic", Options.Values.muteMusic);
		
		//SQL - Not really required writing these,
		//but protects against config edited while running
		JsonObject sql = new JsonObject();
		sql.addProperty("url", Options.Values.SQL.url);
		sql.addProperty("username", Options.Values.SQL.username);
		sql.addProperty("password", Options.Values.SQL.password);
		object.add("sql", sql);
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(object);
		
		try {
			
			FileWriter writer = new FileWriter("options.json");
			writer.write(json);
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the values of the options file
	 * to Options.Values' values.
	 */
	public static void readFromFile()
	{
		JsonParser parser = new JsonParser();
		try
		{
			JsonObject object = parser.parse(new FileReader("options.json")).getAsJsonObject();
			
			Options.Values.name = object.get("name").getAsString().toCharArray();
			
			Options.Values.muteSound = object.get("muteSound").getAsBoolean();
			Options.Values.muteMusic = object.get("muteMusic").getAsBoolean();
			
			//SQL
			JsonObject sql = object.getAsJsonObject("sql");
			Options.Values.SQL.url = sql.get("url").getAsString();
			Options.Values.SQL.username = sql.get("username").getAsString();
			Options.Values.SQL.password = sql.get("password").getAsString();
		}
		catch (NullPointerException e)
		{
			System.out.println("Missing key - using default values");
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}
