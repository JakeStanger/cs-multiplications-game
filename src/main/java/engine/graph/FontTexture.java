package engine.graph;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jake stanger
 * Loads a charset and puts each character into a png
 * to be read for text rendering.
 */
public class FontTexture
{
	private static final String IMAGE_FORMAT = "png";
	
	private Font font;
	private String charSetName;
	private Texture texture;
	
	private final Map<Character, CharInfo> charMap;
	
	private int width, height;
	
	public FontTexture(Font font, String charSetName) throws Exception
	{
		this.font = font;
		this.charSetName = charSetName;
		this.charMap = new HashMap<>();
		
		this.buildTexture();
	}
	
	/**
	 * Returns a list of all the available characters in a character set
	 * @param charsetName
	 * @return
	 */
	private String getAllAvailableChars(String charsetName)
	{
		CharsetEncoder encoder = Charset.forName(charsetName).newEncoder();
		StringBuilder result = new StringBuilder();
		
		for(char c = 0; c < Character.MAX_VALUE; c++)
		{
			if(encoder.canEncode(c)) result.append(c);
		}
		
		return result.toString();
	}
	
	private void buildTexture() throws Exception
	{
		//Get font metrics for each charcter for selected font by using image
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = img.createGraphics(); //Create temporary image
		
		graphics2D.setFont(font);
		
		FontMetrics fontMetrics = graphics2D.getFontMetrics();
		
		String allChars = getAllAvailableChars(charSetName);
		
		this.width = 0;
		this.height = 0;
		
		for(char c : allChars.toCharArray())
		{
			//Get size of character and update global image size
			CharInfo charInfo = new CharInfo(width, fontMetrics.charWidth(c));
			charMap.put(c, charInfo);
			
			width += charInfo.getWidth();
			height = Math.max(height, fontMetrics.getHeight());
		}
		
		//Round image size up to nearest power of 2 for older GPU support
		int power = width == 0 ? 0 : 32 - Integer.numberOfLeadingZeros(width - 1);
		width = (int)Math.pow(2, power);
		
		power = height == 0 ? 0 : 32 - Integer.numberOfLeadingZeros(height - 1);
		height = (int)Math.pow(2, power);
		
		graphics2D.dispose();
		
		//Create image associated with charset
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		graphics2D = img.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2D.setFont(font);
		fontMetrics = graphics2D.getFontMetrics();
		graphics2D.setColor(Color.WHITE);
		graphics2D.drawString(allChars, 0, fontMetrics.getAscent());
		graphics2D.dispose();
		
		//Debug output
		//ImageIO.write(img, IMAGE_FORMAT, new java.io.File("FontOutput-" + UUID.randomUUID() + ".png"));
		
		//Dump image to byte buffer
		InputStream stream;
		try (ByteArrayOutputStream out = new ByteArrayOutputStream())
		{
			ImageIO.write(img, IMAGE_FORMAT, out);
			out.flush();
			stream = new ByteArrayInputStream(out.toByteArray());
		}
		
		texture = new Texture(stream);
	}
	
	public Texture getTexture()
	{
		return texture;
	}
	
	public void setTexture(Texture texture)
	{
		this.texture = texture;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public CharInfo getCharInfo(char c)
	{
		return this.charMap.get(c);
	}
	
	/**
	 * Wrapper for character information
	 */
	public static class CharInfo
	{
		private final int startX, width;
		
		public CharInfo(int startX, int width)
		{
			this.startX = startX;
			this.width = width;
		}
		
		public int getStartX()
		{
			return startX;
		}
		
		public int getWidth()
		{
			return width;
		}
	}
}
