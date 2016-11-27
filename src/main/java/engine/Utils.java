package engine;

import engine.items.GameItem;
import game.enums.Direction;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.lwjgl.BufferUtils.createByteBuffer;

/**
 * @author Jake stanger
 * Utility class containing generic helper methods
 */
public class Utils
{
	/**
	 * Loads a resource with the given classpath
	 * @param fileName
	 * @return The resource
	 * @throws Exception
	 */
	public static String loadResource(String fileName) throws Exception
	{
		String result;
		try (InputStream in = Utils.class.getClass().getResourceAsStream(fileName))
		{
			result = new Scanner(in, "UTF-8").useDelimiter("\\A").next();
		}
		return result;
	}
	
	/**
	 * Reads the file with the given classpath
	 * @param fileName
	 * @return A list of each line in the file
	 * @throws Exception
	 */
	public static List<String> readAllLines(String fileName) throws Exception
	{
		List<String> list = new ArrayList<>();
		
		try (BufferedReader br = new BufferedReader(new InputStreamReader(Utils.class.getClass().getResourceAsStream(fileName))))
		{
			String line;
			while ((line = br.readLine()) != null) list.add(line);
		}
		return list;
	}
	
	public static int[] intListToArray(List<Integer> list)
	{
		return list.stream().mapToInt((Integer v) -> v).toArray();
	}
	
	public static float[] floatListToArray(List<Float> list)
	{
		int size = list != null ? list.size() : 0;
		float[] floatArr = new float[size];
		
		for(int i = 0; i < size; i++) floatArr[i] = list.get(i);
		
		return floatArr;
	}
	
	public static GameItem[] gameItemListToArray(List<GameItem> list)
	{
		int size = list != null ? list.size() : 0;
		GameItem[] arr = new GameItem[size];
		
		for(int i = 0; i < size; i++) arr[i] = list.get(i);
		
		return arr;
	}
	
	public static boolean resourceFileExists(String fileName)
	{
		boolean result;
		
		try (InputStream stream = Utils.class.getResourceAsStream(fileName))
		{
			result = stream != null;
		}
		catch (Exception e)
		{
			result = false;
		}
		return result;
	}
	
	public static float[] createEmptyFloatArray(int length, float defaultValue)
	{
		float[] result = new float[length];
		Arrays.fill(result, defaultValue);
		return result;
	}
	
	public static int[] createEmptyIntArray(int length, int defaultValue)
	{
		int[] result = new int[length];
		Arrays.fill(result, defaultValue);
		return result;
	}
	
	public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException
	{
		ByteBuffer buffer;
		
		Path path = Paths.get(resource);
		if(Files.isReadable(path))
		{
			try(SeekableByteChannel fc = Files.newByteChannel(path))
			{
				buffer = createByteBuffer((int) fc.size() + 1);
				while (fc.read(buffer) != -1);
			}
			
		}
		else
		{
			try (InputStream source = Utils.class.getResourceAsStream(resource);
			     ReadableByteChannel rbc = Channels.newChannel(source))
			{
				buffer = createByteBuffer(bufferSize);
				
				while(true)
				{
					int bytes = rbc.read(buffer);
					if(bytes == -1) break;
					
					if(buffer.remaining() == 0) buffer = resizeBuffer(buffer, buffer.capacity() * 2);
				}
			}
		}
		
		buffer.flip();
		return buffer;
	}
	
	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity)
	{
		ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}
	
	/**
	 * Checks if two vectors are equal, allowing for a difference of 0.001.
	 * @param vec1 The first vector
	 * @param vec2 The second vector
	 * @return Whether or not the vectors are equal
	 */
	public static boolean areVectorsEqual(Vector3f vec1, Vector3f vec2)
	{
		return areVectorsInRange(vec1, vec2, 0.001f);
	}
	
	/**
	 * Checks if two positions are close enough together to be considered touching
	 * @param vec1 The first vector
	 * @param vec2 The second vector
	 * @param range The range to check
	 * @return Whether or not the positions are in range
	 */
	public static boolean areVectorsInRange(Vector3f vec1, Vector3f vec2, float range)
	{
		boolean xInRange = (vec1.x - vec2.x < range && vec1.x - vec2.x > -range);
		boolean yInRange = (vec1.y - vec2.y < range && vec1.y - vec2.y > -range);
		boolean zInRange = (vec1.z - vec2.z < range && vec1.z - vec2.z > -range);
		
		return xInRange && yInRange && zInRange;
	}
	
	/**
	 * Gets the next vector to be reached in the given direction
	 * which is a grid point.
	 * @param vec The vector
	 * @param direction The direction of travel
	 * @param gridSize The size of the grid
	 * @return The next vector on the grid
	 */
	public static Vector3f getNextRoundedVector(Vector3f vec, Direction direction, float gridSize)
	{
		switch(direction)
		{
			case UP:
				return new Vector3f(vec.x, (float)Math.ceil(vec.y/gridSize)*gridSize, vec.z);
			case DOWN:
				return new Vector3f(vec.x, (float)Math.floor(vec.y/gridSize)*gridSize, vec.z);
			case LEFT:
				return new Vector3f((float)Math.floor(vec.x/gridSize)*gridSize, vec.y, vec.z);
			case RIGHT:
				return new Vector3f((float)Math.ceil(vec.x/gridSize)*gridSize, vec.y, vec.z);
			case FORWARDS:
				return new Vector3f(vec.x, vec.y, (float)Math.floor(vec.z/gridSize)*gridSize);
			case BACKWARDS:
				return new Vector3f(vec.x, vec.y, (float)Math.ceil(vec.z/gridSize)*gridSize);
			default:
				return vec;
		}
	}
	
	/**
	 * @param min The minimum value
 	 * @param max The maximum value
	 * @return A random integer between the minimum and maximum value (inclusive).
	 */
	public static int getRandomIntBetween(int min, int max)
	{
		Random random = new Random();
		return random.nextInt(max-min+1)+min;
	}
	
	/**
	 * @param min The minimum value
	 * @param max The maximum value
	 * @param mult The number the return value will be a multiple of
	 * @return A random integer which is a multiple of <i>mult</i>
	 * between the minimum and maximum value (inclusive).
	 */
	public static int getRandomMultipleBetween(int min, int max, float mult)
	{
		float rand = getRandomIntBetween(min, max);
		return (int)(Math.round(rand/mult)*mult);
	}
}
