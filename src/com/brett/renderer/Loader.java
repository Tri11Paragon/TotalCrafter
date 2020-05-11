package com.brett.renderer;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.brett.renderer.datatypes.RawBlockModel;
import com.brett.renderer.datatypes.RawModel;
import com.brett.renderer.datatypes.TextureData;
import com.brett.tools.obj.ModelData;
import com.brett.voxel.world.GameRegistry;
import com.brett.voxel.world.entity.animation.dataStructures.MeshData;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class Loader {
	
	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	private List<Integer> textures = new ArrayList<Integer>();
	public Map<String, Integer> textureMap = new HashMap<String, Integer>();
	
	public RawModel loadToVAO(float[] positions,float[] textureCoords,float[] normals,int[] indices){
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0,3,positions);
		storeDataInAttributeList(1,2,textureCoords);
		storeDataInAttributeList(2,3,normals);
		unbindVAO();
		return new RawModel(vaoID,indices.length);
	}
	
	public RawModel loadToVAO(float[] positions,float[] textureCoords,int[] indices){
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0,3,positions);
		storeDataInAttributeList(1,2,textureCoords);
		unbindVAO();
		return new RawModel(vaoID,indices.length);
	}
	
	public int loadToVAO(float[] positions,float[] textureCoords){
		int vaoID = createVAO();
		storeDataInAttributeList(0,2,positions);
		storeDataInAttributeList(1,2,textureCoords);
		unbindVAO();
		return vaoID;
	}
	
	public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, float[] tangents, int[] indices) {
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, 3, normals);
		storeDataInAttributeList(3, 3, tangents);
		unbindVAO();
		return new RawModel(vaoID, indices.length);
	}
	
	public RawModel loadToVAO(ModelData data) {
		int vaoID = createVAO();
		int[] indices = data.getIndices();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0,3,data.getVertices());
		storeDataInAttributeList(1,2,data.getTextureCoords());
		storeDataInAttributeList(2,3,data.getNormals());
		unbindVAO();
		return new RawModel(vaoID,indices.length);
	}
	
	public RawModel loadToVAO(MeshData data) {
		int vaoID = createVAO();
		int[] indices = data.getIndices();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0,3,data.getVertices());
		storeDataInAttributeList(1,2,data.getTextureCoords());
		storeDataInAttributeList(2,3,data.getNormals());
		storeDataInAttributeList(3,3,data.getJointIds());
		storeDataInAttributeList(4,3,data.getVertexWeights());
		unbindVAO();
		return new RawModel(vaoID,indices.length);
	}
	
	public RawModel loadToVAO(float[] positions, int dimensions) {
		int vaoID = createVAO();
		this.storeDataInAttributeList(0, dimensions, positions);
		unbindVAO();
		return new RawModel(vaoID, positions.length/dimensions);
		
	}
	
	public RawModel loadToVAO(float[] positions, int dimensions, float[] textureCoords) {
		int vaoID = createVAO();
		this.storeDataInAttributeList(0, dimensions, positions);
		this.storeDataInAttributeList(1, 2, textureCoords);
		unbindVAO();
		return new RawModel(vaoID, positions.length/dimensions);
		
	}
	
	public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] lightLevels) {
		int vaoID = createVAO();
		int[] vbos = new int[3];
		vbos[0] = this.storeDataInAttributeList(0, 3, positions);
		vbos[1] = this.storeDataInAttributeList(1, 2, textureCoords);
		vbos[2] = this.storeDataInAttributeList(2, 1, lightLevels);
		unbindVAO();
		return new RawBlockModel(vaoID, vbos, positions.length/3);
	}
	
	public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] layer, float[] lightLevels) {
		int vaoID = createVAO();
		int[] vbos = new int[4];
		vbos[0] = this.storeDataInAttributeList(0, 3, positions);
		vbos[1] = this.storeDataInAttributeList(1, 2, textureCoords);
		vbos[2] = this.storeDataInAttributeList(2, 1, lightLevels);
		vbos[3] = this.storeDataInAttributeList(3, 1, layer);
		unbindVAO();
		return new RawBlockModel(vaoID, vbos, positions.length/3);
	}
	
	public int createEmptyVBO(int floatCount) {
		int vbo = GL15.glGenBuffers();
		vbos.add(vbo);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatCount * 4, GL15.GL_STREAM_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vbo;
	}
	
	public void updateVBO(int vbo, float[] data, FloatBuffer buffer) {
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity() * 4, GL15.GL_STREAM_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public void addInstanceAttribute(int vao, int vbo, int attribute, int dataSize, int instanceDataLength, int offset) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL30.glBindVertexArray(vao);
		GL20.glVertexAttribPointer(attribute, dataSize, GL11.GL_FLOAT, false, instanceDataLength * 4, offset * 4);
		GL33.glVertexAttribDivisor(attribute, 1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}
	
	public RawModel loadToVAO(float[] positions) {
		int vaoID = createVAO();
		this.storeDataInAttributeList(0, 2, positions);
		unbindVAO();
		return new RawModel(vaoID, positions.length/2);
		
	}
	
	public int loadTexture(String filename) {
		return loadTexture(filename, -0.2f);
	}
	
	public int loadTexture(String filename, float bias) {
		Texture texture = null;
		if (textureMap.containsKey(filename))
			return textureMap.get(filename);
		try {
			texture = TextureLoader.getTexture("PNG",
					new FileInputStream("resources/textures/" + filename + ".png"), GL11.GL_NEAREST);
			//if (SettingsLoader.USEMIPMAP)
				GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
				//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
				//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
				// this is needed for mipmaping to work.
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
				// changes the level of detail
				// i don't remember how this works out with it but as the other comment says, lower value = more detail at
				// longer distances?
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, bias); // -0.4f? negative decrease amount. don't too much or lose preformacne
			if (GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
				//float amount = Math.min(SettingsLoader.ASF, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				float amount = Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				//if (SettingsLoader.ENABLEASF)
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
			} else {
				System.out.println("ERROR IN LOADER. NOT SUPPORTED ANISOTROPIC FILTERING");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Tried to load texture " + filename + ".png , didn't work");
			if (filename.toLowerCase().equals("error")) {
				System.exit(-1);
			} else {
				return loadTexture("error");
			}
		}
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		
		textures.add(texture.getTextureID());
		textureMap.put(filename, texture.getTextureID());
		return texture.getTextureID();
	}
	
	public RawModel deleteVAO(RawModel model) {
		try {
			vaos.remove((Integer) model.getVaoID());
			GL30.glDeleteVertexArrays(model.getVaoID());
			if (model instanceof RawBlockModel) {
				int[] vbos = ((RawBlockModel) model).getVbos();
				for (int i = 0; i < vbos.length; i++) {
					GL15.glDeleteBuffers(vbos[i]);
					this.vbos.remove((Integer) vbos[i]);
				}
			}
		} catch (Exception e) {}
		return null;
	}
	
	public void cleanUp(){
		for(int vao:vaos){
			GL30.glDeleteVertexArrays(vao);
		}
		for(int vbo:vbos){
			GL15.glDeleteBuffers(vbo);
		}
		for(int texture:textures){
			GL11.glDeleteTextures(texture);
		}
	}
	
	public void deleteVAO(int vaoID) {
		this.vaos.remove(vaoID);
		GL30.glDeleteVertexArrays(vaoID);
	}
	
	public void deleteVBO(int vboID) {
		this.vbos.remove((Integer) vboID); // needed to make this work
		GL15.glDeleteBuffers(vboID);
	}
	
	private int createVAO(){
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}
	
	private int storeDataInAttributeList(int attributeNumber, int coordinateSize,float[] data){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber,coordinateSize,GL11.GL_FLOAT,false,0,0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vboID;
	}
	
	private int storeDataInAttributeList(int attributeNumber, int coordinateSize, int[] data){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL30.glVertexAttribIPointer(attributeNumber,coordinateSize,GL11.GL_INT,0,0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vboID;
	}
	
	private void unbindVAO(){
		GL30.glBindVertexArray(0);
	}
	
	private void bindIndicesBuffer(int[] indices){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	private IntBuffer storeDataInIntBuffer(int[] data){
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private FloatBuffer storeDataInFloatBuffer(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	public int loadCubeMap(String[] textureFiles) {
		int texID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);
		
		for(int i=0;i<textureFiles.length;i++) {
			TextureData data = decodeTextureFile("resources/textures/terrain/skyboxes/" + textureFiles[i] + ".png");
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		}
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE); 
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		textures.add(texID);
		return texID;
	}
	
	/**
	 * This function is the same thing as the main texture loader
	 * except this can handle non 2^x textures. (This might not be true)
	 * TODO: replace old function with this.
	 * it could also be multithreaded
	 */
	public int loadSpecialTexture(String texture) {
		TextureData d = decodeTextureFile("resources/textures/" + texture + ".png");
		int id = GL11.glGenTextures();
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
		// don't ask
		// i don't think it works the way i want
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST); 
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST); 
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, d.getWidth(), d.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, d.getBuffer());
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST_MIPMAP_NEAREST);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.2f);
		float amount = Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
		
		textures.add(id);
		return id;
	}
	
	public int loadSpecialTextureATLAS(int width, int height) {
		//for more detail on array textures
		//https://www.khronos.org/opengl/wiki/Array_Texture
		float anisf = Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
		//TextureData d = decodeTextureFile("resources/textures/" + texture + ".png");
		HashMap<Integer, String> texs = GameRegistry.registerTextures();
		int id = GL11.glGenTextures();
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, id); 
		
        // i really don't like this
        // openGL4.2. i was trying to use < 3.2
        // if you are having issues its likely because of this.
        // "OpenGL 4.2 (2011)"
        // i feel like this should be in gl30
        GL42.glTexStorage3D(GL30.GL_TEXTURE_2D_ARRAY, 4, GL11.GL_RGBA8, width, height, texs.size());
        
        for (Entry<Integer, String> s : texs.entrySet()) {
        	// i don't understand why this is in gl12 but to allocate this is in gl42
        	GL12.glTexSubImage3D(GL30.GL_TEXTURE_2D_ARRAY,
        			0, 
        			0, 0, s.getKey(), 
        			width, height, 1, 
        			GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 
        			decodeTextureFile("resources/textures/" + s.getValue() + ".png").getBuffer());
        	GL11.glTexParameterf(GL30.GL_TEXTURE_2D_ARRAY, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, anisf);
        }
        
        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST); 
        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        
        GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D_ARRAY);
		GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
		GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
		GL11.glTexParameterf(GL30.GL_TEXTURE_2D_ARRAY, GL14.GL_TEXTURE_LOD_BIAS, -0.2f);
        
		textures.add(id);
		return id;
	}
	
	private TextureData decodeTextureFile(String fileName) {
		int width = 0;
		int height = 0;
		ByteBuffer buffer = null;
		try {
			FileInputStream in = new FileInputStream(fileName);
			PNGDecoder decoder = new PNGDecoder(in);
			width = decoder.getWidth();
			height = decoder.getHeight();
			buffer = ByteBuffer.allocateDirect(4 * width * height);
			decoder.decode(buffer, width * 4, Format.RGBA);
			buffer.flip();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Tried to load texture " + fileName + ", didn't work");
			System.exit(-1);
		}
		return new TextureData(buffer, width, height);
	}
	
}
