package com.brett.engine.obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.brett.engine.data.datatypes.ModelData;
import com.brett.engine.data.datatypes.SmallModelData;
import com.brett.engine.data.datatypes.Vertex;

/**
* @author Brett
* @date Jun. 20, 2020
*/

public class OBJLoader {
	
	private static final String RES_LOC = "resources/models/";

	// karl's function
	public static ModelData loadOBJ(String objFileName) {
		FileReader isr = null;
		File objFile = new File(RES_LOC + objFileName + ".obj");
		try {
			isr = new FileReader(objFile);
		} catch (FileNotFoundException e) {
			System.err.println("File not found in res; don't use any extention");
		}
		BufferedReader reader = new BufferedReader(isr);
		String line;
		List<Vertex> vertices = new ArrayList<Vertex>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		try {
			while (true) {
				line = reader.readLine();
				if (line.startsWith("v ")) {
					String[] currentLine = line.split(" ");
					Vector3f vertex = new Vector3f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]),
							(float) Float.valueOf(currentLine[3]));
					Vertex newVertex = new Vertex(vertices.size(), vertex);
					vertices.add(newVertex);

				} else if (line.startsWith("vt ")) {
					String[] currentLine = line.split(" ");
					Vector2f texture = new Vector2f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]));
					textures.add(texture);
				} else if (line.startsWith("vn ")) {
					String[] currentLine = line.split(" ");
					Vector3f normal = new Vector3f((float) Float.valueOf(currentLine[1]),
							(float) Float.valueOf(currentLine[2]),
							(float) Float.valueOf(currentLine[3]));
					normals.add(normal);
				} else if (line.startsWith("f ")) {
					break;
				}
			}
			while (line != null && line.startsWith("f ")) {
				String[] currentLine = line.split(" ");
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");
				processVertex(vertex1, vertices, indices);
				processVertex(vertex2, vertices, indices);
				processVertex(vertex3, vertices, indices);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Error reading the file");
		}
		removeUnusedVertices(vertices);
		float[] verticesArray = new float[vertices.size() * 3];
		float[] texturesArray = new float[vertices.size() * 2];
		float[] normalsArray = new float[vertices.size() * 3];
		float furthest = convertDataToArrays(vertices, textures, normals, verticesArray,
				texturesArray, normalsArray);
		int[] indicesArray = convertIndicesListToArray(indices);
		ModelData data = new ModelData(verticesArray, texturesArray, normalsArray, indicesArray,
				furthest, objFileName);
		return data;
	}
	// this is something that i added.
	/**
	 * loads a OBJ as arrays instead of as indices
	 */
	public static SmallModelData loadAsArrays(String file) {
		// final list of verts
		float[] verts = {};
		float[] uvs = {};
		// list of all the verts
		List<float[]> v = new ArrayList<float[]>();
		List<float[]> u = new ArrayList<float[]>();
		// reader for the file
		FileReader fr = null;
		try {
			fr = new FileReader(RES_LOC + file + ".obj");
		} catch (FileNotFoundException e) {System.err.println("File not found in res; don't use any extention");}
		BufferedReader r = new BufferedReader(fr);
		// current line being read.
		String line;
		
		// TODO: work in support for object groups.
		
		try {
			while (true) {
				line = r.readLine();
				String[] currentLine = line.split(" ");
				// parses the OBJ file format. Basiclly just karls code copied.
				// this stuff is not hard.
				// verts
				if (line.startsWith("v ")) {
					v.add(new float[] {Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3])});
					// uvs
				} else if (line.startsWith("vt ")) {
					u.add(new float[] {Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2])});
					// break if we find a face
				} else if (line.startsWith("f ")) {
					break;
				}
			}
			while (line != null && line.startsWith("f ")) {
				String[] currentLine = line.split(" ");
				// split the face line
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");
				// this just converts from indicies into pure arrays.
				// since the indices are just pointers to positions in the list of verts and uvs
				// its pretty easy to convert them to pure arrays.
				verts = addArrays(verts, v.get((int) (Float.parseFloat(vertex1[0])-1)));
				uvs = addArrays(uvs, u.get((int)(Float.parseFloat(vertex1[1])-1)));
				
				verts = addArrays(verts, v.get((int) (Float.parseFloat(vertex2[0])-1)));
				uvs = addArrays(uvs, u.get((int)(Float.parseFloat(vertex2[1])-1)));
				
				verts = addArrays(verts, v.get((int) (Float.parseFloat(vertex3[0])-1)));
				uvs = addArrays(uvs, u.get((int)(Float.parseFloat(vertex3[1])-1)));
				// read the next line
				line = r.readLine();
			}
		} catch (Exception e) {}
		return new SmallModelData(verts, uvs);
	}
	
	// this is something that i added.
	/**
	 * prints a OBJ file as arrays instead of indices
	 */
	public static void printArrays(String file) {
		// get the model
		SmallModelData model = OBJLoader.loadAsArrays(file);
		
		// get the verts from the model
		float[] verts = model.getVerts();
		float[] uvs = model.getUvs();
		// string to print
		String v = "VERTS: \n";
		String u = "UVS: \n";
		
		// add in all the verts to the string
		for (int i = 0; i < verts.length; i+=3) {
			v += verts[i] + "f,";
			v += verts[i+1] + "f,";
			v += verts[i+2] + "f, \n";
		}
		// add all the uvs
		for (int i = 0; i < uvs.length; i+=2) {
			u += uvs[i] + "f,";
			u += uvs[i+1] + "f, \n";
		}
		
		// print
		System.out.println("---------");
		System.out.println(file);
		System.out.println("---------");
		System.out.println(v);
		System.out.println(u);
	}
	
	/**
	 * adds arrays
	 */
	public static float[] addArrays(float[] array1, float[] array2) {
		// I made this before i knew about system.arraycopy.
		// this is only to be used like once at startup
		// and not during play time
		// so im fine with it.
		float[] rtv = new float[array1.length + array2.length];
		
		// take old array and add it to new array
		for (int i = 0; i<array1.length;i++) {
			rtv[i] = array1[i];
		}
		
		// add in the new array starting at the end of the old array
		for (int i = 0; i<array2.length; i++) {
			rtv[i + array1.length] = array2[i];
		}
		
		return rtv;
	}
	
	/**
	 * karl's functions below
	 */
	private static void processVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices) {
		int index = Integer.parseInt(vertex[0]) - 1;
		Vertex currentVertex = vertices.get(index);
		int textureIndex = Integer.parseInt(vertex[1]) - 1;
		int normalIndex = Integer.parseInt(vertex[2]) - 1;
		if (!currentVertex.isSet()) {
			currentVertex.setTextureIndex(textureIndex);
			currentVertex.setNormalIndex(normalIndex);
			indices.add(index);
		} else {
			dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices,
					vertices);
		}
	}

	private static int[] convertIndicesListToArray(List<Integer> indices) {
		int[] indicesArray = new int[indices.size()];
		for (int i = 0; i < indicesArray.length; i++) {
			indicesArray[i] = indices.get(i);
		}
		return indicesArray;
	}

	private static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures,
			List<Vector3f> normals, float[] verticesArray, float[] texturesArray,
			float[] normalsArray) {
		float furthestPoint = 0;
		for (int i = 0; i < vertices.size(); i++) {
			Vertex currentVertex = vertices.get(i);
			if (currentVertex.getLength() > furthestPoint) {
				furthestPoint = currentVertex.getLength();
			}
			Vector3f position = currentVertex.getPosition();
			Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
			Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
			verticesArray[i * 3] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;
			texturesArray[i * 2] = textureCoord.x;
			texturesArray[i * 2 + 1] = 1 - textureCoord.y;
			normalsArray[i * 3] = normalVector.x;
			normalsArray[i * 3 + 1] = normalVector.y;
			normalsArray[i * 3 + 2] = normalVector.z;
		}
		return furthestPoint;
	}

	private static void dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex,
			int newNormalIndex, List<Integer> indices, List<Vertex> vertices) {
		if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
			indices.add(previousVertex.getIndex());
		} else {
			Vertex anotherVertex = previousVertex.getDuplicateVertex();
			if (anotherVertex != null) {
				dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex,
						indices, vertices);
			} else {
				Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
				duplicateVertex.setTextureIndex(newTextureIndex);
				duplicateVertex.setNormalIndex(newNormalIndex);
				previousVertex.setDuplicateVertex(duplicateVertex);
				vertices.add(duplicateVertex);
				indices.add(duplicateVertex.getIndex());
			}

		}
	}
	
	private static void removeUnusedVertices(List<Vertex> vertices){
		for(Vertex vertex:vertices){
			if(!vertex.isSet()){
				vertex.setTextureIndex(0);
				vertex.setNormalIndex(0);
			}
		}
	}
	
}
