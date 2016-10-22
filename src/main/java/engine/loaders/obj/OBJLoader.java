package engine.loaders.obj;

import engine.Utils;
import engine.graph.InstancedMesh;
import engine.graph.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jake stanger
 * Loads an OBJ file so it can be rendered in-game,
 * creating a set of vertices and texture coordinates.
 */
public class OBJLoader
{
	public static Mesh loadMesh(String fileName) throws Exception
	{
		return loadMesh(fileName, 1);
	}
	
	public static Mesh loadMesh(String fileName, int instances) throws Exception
	{
		List<String> lines = Utils.readAllLines(fileName);
		
		List<Vector3f> vertices = new ArrayList<>();
		List<Vector2f> textures = new ArrayList<>();
		List<Vector3f> normals = new ArrayList<>();
		List<Face> faces = new ArrayList<>();
		
		//Parse OBJ contents
		for(String line : lines)
		{
			String[] tokens = line.split("\\s+");
			switch(tokens[0])
			{
				//TODO Create constants for strings
				case "v":
					//Geometric vertex
					Vector3f vec3f = new Vector3f(
							Float.parseFloat(tokens[1]),
							Float.parseFloat(tokens[2]),
							Float.parseFloat(tokens[3]));
					vertices.add(vec3f);
					break;
				case "vt":
					//Texture coordinate
					Vector2f vec2f = new Vector2f(
							Float.parseFloat(tokens[1]),
							Float.parseFloat(tokens[2]));
					textures.add(vec2f);
					break;
				case "vn":
					//Vertex normal
					Vector3f vec3fNorm = new Vector3f(
							Float.parseFloat(tokens[1]),
							Float.parseFloat(tokens[2]),
							Float.parseFloat(tokens[3]));
					normals.add(vec3fNorm);
					break;
				case "f":
					Face face = new Face(tokens[1], tokens[2], tokens[3]);
					faces.add(face);
					break;
				default:
					break; //We can safely ignore any other lines
			}
		}
		
		//Some complicated re-ordering of the lists has to be done to please OpenGL
		return reorderLists(vertices, textures, normals, faces, instances);
	}
	
	/**
	 * Does some complicated re-ordering of the lists so OpenGL renders them properly.
	 * @param posList
	 * @param textCoordList
	 * @param normList
	 * @param facesList
	 * @return
	 */
	private static Mesh reorderLists(List<Vector3f> posList, List<Vector2f> textCoordList,
	                                 List<Vector3f> normList, List<Face> facesList, int instances)
	{
		List<Integer> indices = new ArrayList<>();
		
		//Create position array in same order as declaration
		float[] posArr = new float[posList.size() * 3];
		
		int i = 0;
		for(Vector3f pos : posList)
		{
			posArr[i * 3] = pos.x;
			posArr[i * 3 + 1] = pos.y;
			posArr[i * 3 + 2] = pos.z;
			i++;
		}
		
		float[] textCoordArr = new float[posList.size() * 2];
		float[] normArr = new float[posList.size() * 3];
		
		for(Face face : facesList)
		{
			IdxGroup[] faceVertexIndices = face.getFaceVetexIndices();
			for(IdxGroup indValue : faceVertexIndices) processFaceVertex(indValue, textCoordList, normList, indices, textCoordArr, normArr);
		}
		
		int[] indicesArr;
		indicesArr = indices.stream().mapToInt((Integer v) -> v).toArray();
		
		Mesh mesh;
		if(instances > 1) mesh = new InstancedMesh(posArr, textCoordArr, normArr, indicesArr, instances);
		else mesh = new Mesh(posArr, textCoordArr, normArr, indicesArr);
		
		return mesh;
	}
	
	/**
	 * Does some fancy re-ordering stuff
	 * @param indices
	 * @param textCoordList
	 * @param normList
	 * @param indicesList
	 * @param texCoordArr
	 * @param normArr
	 */
	private static void processFaceVertex(IdxGroup indices, List<Vector2f> textCoordList,
        List<Vector3f> normList, List<Integer> indicesList, float[] texCoordArr, float[] normArr)
	{
		//Set index for vertex coords
		int posIndex = indices.idxPos;
		indicesList.add(posIndex);
		
		//Re-order texture co-ordinates
		if(indices.idxTextCoord >= 0)
		{
			Vector2f textCoord = textCoordList.get(indices.idxTextCoord);
			texCoordArr[posIndex * 2] = textCoord.x;
			texCoordArr[posIndex * 2 + 1] = 1 - textCoord.y; //UV co-ordinates are 'upside-down', so we take from 1
		}
		if(indices.idxVecNormal >= 0)
		{
			//Reorder vector normals
			Vector3f vecNorm = normList.get(indices.idxVecNormal);
			normArr[posIndex * 3] = vecNorm.x;
			normArr[posIndex * 3 + 1] = vecNorm.y;
			normArr[posIndex * 3 + 2] = vecNorm.z;
		}
	}
	
	/**
	 * Class for holding information for a group.
	 * Each face consists of several indices groups to make up the triangles.
	 */
	protected static class IdxGroup
	{
		public static final int NO_VALUE = -1;
		
		public int idxPos, idxTextCoord, idxVecNormal;
		
		public IdxGroup()
		{
			this.idxPos = NO_VALUE;
			this.idxTextCoord = NO_VALUE;
			this.idxVecNormal = NO_VALUE;
		}
	}
	
	/**
	 * Holds information for each face of an object
	 */
	protected static class Face
	{
		private IdxGroup[] idxGroups = new IdxGroup[3]; //3 vertices per face
		
		public Face(String v1, String v2, String v3)
		{
			idxGroups = new IdxGroup[3];
			
			//Parse given lines
			idxGroups[0] = parseLine(v1);
			idxGroups[1] = parseLine(v2);
			idxGroups[2] = parseLine(v3);
		}
		
		private IdxGroup parseLine(String line)
		{
			IdxGroup idxGroup = new IdxGroup();
			
			String[] lineTokens = line.split("/");
			int length = lineTokens.length;
			
			idxGroup.idxPos = Integer.parseInt(lineTokens[0]) - 1;
			if(length > 1)
			{
				String textCoord = lineTokens[1];
				idxGroup.idxTextCoord = textCoord.length() > 0 ? Integer.parseInt(textCoord) - 1 : IdxGroup.NO_VALUE;
				
				if(length > 2) idxGroup.idxVecNormal = Integer.parseInt(lineTokens[2]) - 1;
			}
			
			return idxGroup;
		}
		
		public IdxGroup[] getFaceVetexIndices()
		{
			return idxGroups;
		}
	}
}
