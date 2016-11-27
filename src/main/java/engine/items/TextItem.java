package engine.items;

import engine.Utils;
import engine.graph.FontTexture;
import engine.graph.Material;
import engine.graph.Mesh;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jake stanger
 * A string to be drawn onto the hud
 */
public class TextItem extends GameItem
{
	public static final float Z_POS = 0f;
	public static final int VERTICES_PER_QUAD = 4;
	
	private String text;
	
	private FontTexture fontTexture;
	
	public TextItem(String text, FontTexture fontTexture) throws Exception
	{
		super();
		this.text = text;
		this.fontTexture = fontTexture;
		this.setMesh(this.buildMesh());
	}
	
	private Mesh buildMesh()
	{
		List<Float> positions = new ArrayList<>();
		List<Float> textCoords = new ArrayList<>();
		
		float[] normals   = new float[0];
		List<Integer> indices   = new ArrayList<>();
		
		char[] characters = text.toCharArray();
		int numChars = characters.length;
		
		float startX = 0;
		for(int i=0; i<numChars; i++)
		{
			FontTexture.CharInfo charInfo = fontTexture.getCharInfo(characters[i]);
			
			//--Build each character tile from 2 triangles--
			//Top-left
			positions.add(startX); //x
			positions.add(0.0f); //y
			positions.add(Z_POS); //z
			textCoords.add( (float)charInfo.getStartX() / (float)fontTexture.getWidth());
			textCoords.add(0.0f);
			indices.add(i*VERTICES_PER_QUAD);
			
			//Bottom-left
			positions.add(startX); //x
			positions.add((float)fontTexture.getHeight()); //y
			positions.add(Z_POS); //z
			textCoords.add((float)charInfo.getStartX() / (float)fontTexture.getWidth());
			textCoords.add(1.0f);
			indices.add(i*VERTICES_PER_QUAD + 1);
			
			//Bottom-right
			positions.add(startX + charInfo.getWidth()); //x
			positions.add((float)fontTexture.getHeight()); //y
			positions.add(Z_POS); //z
			textCoords.add((float)(charInfo.getStartX() + charInfo.getWidth()) / (float)fontTexture.getWidth());
			textCoords.add(1.0f);
			indices.add(i*VERTICES_PER_QUAD + 2);
			
			//Top-right
			positions.add(startX + charInfo.getWidth()); //x
			positions.add(0.0f); //y
			positions.add(Z_POS); //z
			textCoords.add((float)(charInfo.getStartX() + charInfo.getWidth()) / (float)fontTexture.getWidth());
			textCoords.add(0.0f);
			indices.add(i*VERTICES_PER_QUAD + 3);
			
			//Add indices por left top and bottom right vertices
			indices.add(i*VERTICES_PER_QUAD);
			indices.add(i*VERTICES_PER_QUAD + 2);
			
			startX += charInfo.getWidth();
		}
		
		float[] posArr = Utils.floatListToArray(positions);
		float[] textCoordsArr = Utils.floatListToArray(textCoords);
		int[] indicesArr = indices.stream().mapToInt(i->i).toArray();
		
		Mesh mesh = new Mesh(posArr, textCoordsArr, normals, indicesArr);
		mesh.setMaterial(new Material(fontTexture.getTexture()));
		
		return mesh;
	}
	
	public String getText()
	{
		return this.text;
	}
	
	public void setText(String text)
	{
		this.text = text;
		//this.getMesh().deleteBuffers();
		this.setMesh(buildMesh());
	}
	
	public void setColour(Vector3f colour)
	{
		this.getMesh().getMaterial().setColour(colour);
	}
	
	public FontTexture getFontTexture()
	{
		return fontTexture;
	}
}
