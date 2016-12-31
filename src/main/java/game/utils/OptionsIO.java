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
