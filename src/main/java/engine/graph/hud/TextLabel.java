package engine.graph.hud;

import engine.graph.FontTexture;
import engine.items.TextItem;
import org.joml.Vector3f;

import java.awt.*;

/**
 * @author Jake stanger
 * A text string which is rendered on the screen
 */
public class TextLabel
{
	private static final Font DEFAULT_FONT = new Font("Impact", Font.PLAIN, 20);
	private static final Vector3f DEFAULT_COLOUR = new Vector3f(1, 1, 1);
	private static final String CHARSET = "ISO-8859-1";
	
	private TextItem textItem;
	
	public TextLabel(float x, float y) throws Exception
	{
		this(x, y, "");
	}
	
	public TextLabel(float x, float y, String defaultText) throws Exception
	{
		this(x, y, defaultText, DEFAULT_FONT, DEFAULT_COLOUR);
	}
	
	public TextLabel(float x, float y, String defaultText, Font font, Vector3f colour) throws Exception
	{
		FontTexture fontTexture = new FontTexture(font, CHARSET);
		this.textItem = new TextItem(defaultText, fontTexture);
		this.textItem.setColour(colour);
		this.textItem.setPosition(x, y, 0);
	}
	
	public void setText(String text) throws Exception
	{
		this.textItem.setText(text); //TODO Fix text rendering black
	}
	
	public String getText()
	{
		return this.textItem.getText();
	}
	
	public void setPosition(float x, float y)
	{
		this.textItem.setPosition(x, y, 0);
	}
	
	public TextItem getTextItem()
	{
		return textItem;
	}
	
}
