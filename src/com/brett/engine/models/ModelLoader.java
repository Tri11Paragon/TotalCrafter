package com.brett.engine.models;

import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_AMBIENT;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_DIFFUSE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_SPECULAR;
import static org.lwjgl.assimp.Assimp.aiGetMaterialColor;
import static org.lwjgl.assimp.Assimp.aiImportFile;
import static org.lwjgl.assimp.Assimp.aiProcess_FixInfacingNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_JoinIdenticalVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;
import static org.lwjgl.assimp.Assimp.aiTextureType_DIFFUSE;
import static org.lwjgl.assimp.Assimp.aiTextureType_NONE;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;

import com.brett.engine.Utils;
import com.brett.engine.data.datatypes.Material;
import com.brett.engine.data.datatypes.ModelData;
import com.brett.engine.managers.ScreenManager;

/**
* @author Brett
* @date Jun. 27, 2020
*/

public class ModelLoader {
	
	public static ModelData[] load(String file, String textures) {
		return load(file, textures, aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals);
	}
	
	public static ModelData[] load(String file, String textures, int flags) {
		AIScene aiScene = aiImportFile(file, flags);
		if (aiScene == null) {
			System.err.println("Error loading " + file + "!");
			return null;
		}
		
		int numMaterials = aiScene.mNumMaterials();
		PointerBuffer aiMaterials = aiScene.mMaterials();
		List<Material> materials = new ArrayList<>();
		for (int i = 0; i < numMaterials; i++) {
		    AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
		    processMaterial(aiMaterial, materials, textures);
		}
		
		int numMeshes = aiScene.mNumMeshes();
		PointerBuffer aiMeshes = aiScene.mMeshes();
		ModelData[] meshes = new ModelData[numMeshes];
		for (int i = 0; i < numMeshes; i++) {
		    AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
		    ModelData mesh = processMesh(aiMesh, materials);
		    meshes[i] = mesh;
		}

		return meshes;
	}
	
	public static void processMaterial(AIMaterial aiMaterial, List<Material> materials, String texturesDir) {
	    AIColor4D colour = AIColor4D.create();

	    AIString path = AIString.calloc();
	    Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
	    String textPath = path.dataString();
	    int texture = 0;
	    if (textPath != null && textPath.length() > 0) {
	        texture = ScreenManager.loader.loadTexture(texturesDir + "/" + textPath);
	    }

	    Vector4f ambient = Material.DEFAULT_COLOR;
	    int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, colour);
	    if (result == 0) {
	        ambient = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
	    }

	    Vector4f diffuse = Material.DEFAULT_COLOR;
	    result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, colour);
	    if (result == 0) {
	        diffuse = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
	    }

	    Vector4f specular = Material.DEFAULT_COLOR;
	    result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, colour);
	    if (result == 0) {
	        specular = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
	    }

	    Material material = new Material(ambient, diffuse, specular, texture);
	    material.setTexture(texture);
	    materials.add(material);
	}
	
	public static ModelData processMesh(AIMesh aiMesh, List<Material> materials) {
	    List<Float> vertices = new ArrayList<Float>();
	    List<Float> textures = new ArrayList<Float>();
	    List<Float> normals = new ArrayList<Float>();
	    List<Integer> indices = new ArrayList<Integer>();

	    processVertices(aiMesh, vertices);
	    processNormals(aiMesh, normals);
	    processTextCoords(aiMesh, textures);
	    processIndices(aiMesh, indices);

	    ModelData mesh = new ModelData(Utils.listToArrayF(vertices),
	        Utils.listToArrayF(textures),
	        Utils.listToArrayF(normals),
	        Utils.listToArrayI(indices)
	    );
	    Material material;
	    int materialIdx = aiMesh.mMaterialIndex();
	    if (materialIdx >= 0 && materialIdx < materials.size()) {
	        material = materials.get(materialIdx);
	    } else {
	        material = null;
	    }
	    mesh.setMaterial(material);

	    return mesh;
	}
	
	public static void processNormals(AIMesh aiMesh, List<Float> normals) {
	    AIVector3D.Buffer aiVertices = aiMesh.mNormals();
	    while (aiVertices.remaining() > 0) {
	        AIVector3D aiVertex = aiVertices.get();
	        normals.add(aiVertex.x());
	        normals.add(aiVertex.y());
	        normals.add(aiVertex.z());
	    }
	}
	
	public static void processTextCoords(AIMesh aiMesh, List<Float> textures) {
        AIVector3D.Buffer textCoords = aiMesh.mTextureCoords(0);
        int numTextCoords = textCoords != null ? textCoords.remaining() : 0;
        for (int i = 0; i < numTextCoords; i++) {
            AIVector3D textCoord = textCoords.get();
            textures.add(textCoord.x());
            textures.add(1 - textCoord.y());
        }
    }
	
	public static void processIndices(AIMesh aiMesh, List<Integer> indices) {
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        for (int i = 0; i < numFaces; i++) {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer buffer = aiFace.mIndices();
            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }
    }
	
	public static void processVertices(AIMesh aiMesh, List<Float> vertices) {
	    AIVector3D.Buffer aiVertices = aiMesh.mVertices();
	    while (aiVertices.remaining() > 0) {
	        AIVector3D aiVertex = aiVertices.get();
	        vertices.add(aiVertex.x());
	        vertices.add(aiVertex.y());
	        vertices.add(aiVertex.z());
	    }
	}
	
}
