package engine.items;

import engine.graph.HeightMapMesh;
import org.joml.Vector3f;

import javax.imageio.ImageIO;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author Jake stanger
 *         TODO Write JavaDoc
 */
public class Terrain
{
	private final int terrainSize, verticesPerRow, verticesPerCol;
	
	private final GameItem[] gameItems;
	
	private final HeightMapMesh heightMapMesh;
	
	/**
	 * A 2D array of every bounding box
	 * for every terrain tile
	 */
	private final Rectangle2D.Float[][] boundingBoxes;
	
	/**
	 *
	 * @param terrainSize The number of tiles the terrain should be made from in each direction
	 * @param scale The scale of each terrain tile (1 = normal)
	 * @param minY
	 * @param maxY
	 * @param heightMapFile
	 * @param textureFile
	 * @param textInc
	 * @throws Exception
	 */
	public Terrain(int terrainSize, float scale, float minY, float maxY, String heightMapFile, String textureFile, int textInc) throws Exception
	{
		this.terrainSize = terrainSize;
		this.gameItems = new GameItem[terrainSize * terrainSize];
		
		BufferedImage heightMapImage = ImageIO.read(getClass().getResourceAsStream(heightMapFile));
		this.verticesPerCol = heightMapImage.getWidth() - 1;
		this.verticesPerRow = heightMapImage.getHeight() - 1;
		
		this.heightMapMesh = new HeightMapMesh(minY, maxY, heightMapImage, textureFile, textInc);
		this.boundingBoxes = new Rectangle2D.Float[terrainSize][terrainSize];
		
		for(int row = 0; row < terrainSize; row++)
		{
			for(int col = 0; col < terrainSize; col++)
			{
				float xDisp = (col - ((float) terrainSize - 1) / (float) 2) * scale * HeightMapMesh.getXLength();
				float zDisp = (row - ((float) terrainSize - 1) / (float) 2) * scale * HeightMapMesh.getZLength();
				
				GameItem terrainBlock = new GameItem(heightMapMesh.getMesh());
				terrainBlock.setScale(scale);
				terrainBlock.setPosition(xDisp, 0, zDisp);
				gameItems[row * terrainSize + col] = terrainBlock;
				
				boundingBoxes[row][col] = this.getBoundingBox(terrainBlock); //Add bounding box to array
			}
		}
	}
	
	/**
	 * Gets the 2D bounding box plane
	 * for the given terrain tile.
	 * @param terrainBlock
	 * @return
	 */
	private Rectangle2D.Float getBoundingBox(GameItem terrainBlock)
	{
		float scale = terrainBlock.getScale();
		Vector3f pos = terrainBlock.getPosition();
		
		//Get where the tile starts
		float topLeftX = HeightMapMesh.START_X * scale + pos.x;
		float topLeftZ = HeightMapMesh.START_Z * scale + pos.z;
		
		float width = Math.abs(HeightMapMesh.START_X * 2) * scale;
		float height = Math.abs(HeightMapMesh.START_Z * 2) * scale;
		
		return new Rectangle2D.Float(topLeftX, topLeftZ, width, height);
	}
	
	public float getHeight(Vector3f pos)
	{
		float result = Float.MIN_VALUE; //Start by assuming we're at the bottom of the world
		
		//Create new bounding box and terrain tile
		Rectangle2D.Float boundingBox = null;
		boolean found = false;
		GameItem terrainBlock = null;
		
		for(int row = 0; row < terrainSize && !found; row++)
		{
			for(int col = 0; col < terrainSize && !found; col++)
			{
				//Get terrain block and bounding box
				terrainBlock = gameItems[row * terrainSize + col];
				boundingBox = boundingBoxes[row][col];
				
				found = boundingBox.contains(pos.x, pos.z); //Check if we have found the bounding box
			}
		}
		
		if(found)
		{
			//Calculate terrain height at this position
			Vector3f[] triangle = this.getTriangle(pos, boundingBox, terrainBlock);
			result = this.interpolateHeight(triangle[0], triangle[1], triangle[2], pos.x, pos.z);
		}
		
		return result;
	}
	
	/**
	 * Runs some co-ordinate geometry to find the terrain height
	 * at the given point
	 * @param position
	 * @param boundingBox
	 * @param terrainBlock
	 * @return
	 */
	protected Vector3f[] getTriangle(Vector3f position, Rectangle2D.Float boundingBox, GameItem terrainBlock)
	{
		//Get col and row associated with current position
		float cellWidth = boundingBox.width / (float) verticesPerCol;
		float cellHeight = boundingBox.height / (float) verticesPerRow;
		
		int col = (int) ((position.x - boundingBox.x) / cellWidth);
		int row = (int) ((position.z - boundingBox.y) / cellHeight); //bounding box y = world z (bounding box is 2D at this point)
		
		Vector3f[] triangle = new Vector3f[3];
		triangle[1] = new Vector3f(
				boundingBox.x + col * cellWidth,
				this.getWorldHeight(row + 1, col, terrainBlock),
				boundingBox.y + (row + 1) * cellHeight);
		triangle[2] = new Vector3f(
				boundingBox.x + (col + 1) * cellWidth,
				this.getWorldHeight(row, col + 1, terrainBlock),
				boundingBox.y + row * cellHeight);
		
		if (position.z < this.getDiagonalZCoord(triangle[1].x, triangle[1].z, triangle[2].x, triangle[2].z, position.x))
		{
			triangle[0] = new Vector3f(
					boundingBox.x + col * cellWidth,
					this.getWorldHeight(row, col, terrainBlock),
					boundingBox.y + row * cellHeight);
		}
		else
		{
			triangle[0] = new Vector3f(
					boundingBox.x + (col + 1) * cellWidth,
					this.getWorldHeight(row + 2, col + 1, terrainBlock),
					boundingBox.y + (row + 1) * cellHeight);
		}
		
		return triangle;
	}
	
	protected float getDiagonalZCoord(float x1, float z1, float x2, float z2, float x)
	{
		return ((z1 - z2) / (x1 - x2)) * (x - x1) + z1;
	}
	
	/**
	 * Gets the height of an object in the world
	 * @param row
	 * @param col
	 * @param gameItem
	 * @return
	 */
	protected float getWorldHeight(int row, int col, GameItem gameItem)
	{
		float y = heightMapMesh.getHeight(row, col);
		return y * gameItem.getScale() + gameItem.getPosition().y;
	}
	
	/**
	 * Performs some co-ordinate geometry
	 * in order to calculate the height of a point.
	 * @param pA
	 * @param pB
	 * @param pC
	 * @param x
	 * @param z
	 * @return
	 */
	protected float interpolateHeight(Vector3f pA, Vector3f pB, Vector3f pC, float x, float z)
	{
		//Plane equation ax+by+cz+d=0
		float a = (pB.y - pA.y) * (pC.z - pA.z) - (pC.y - pA.y) * (pB.z - pA.z);
		float b = (pB.z - pA.z) * (pC.x - pA.x) - (pC.z - pA.z) * (pB.x - pA.x);
		float c = (pB.x - pA.x) * (pC.y - pA.y) - (pC.x - pA.x) * (pB.y - pA.y);
		float d = -(a * pA.x + b * pA.y + c * pA.z);
		
		// y = (-d -ax -cz) / b
		float y = (-d - a * x - c * z) / b;
		
		return y;
	}
	
	public GameItem[] getGameItems()
	{
		return gameItems;
	}
}
