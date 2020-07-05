package com.brett.engine;

import java.io.BufferedInputStream;
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
import org.lwjgl.opengl.GL42;

import com.brett.engine.data.datatypes.ModelData;
import com.brett.engine.data.datatypes.TextureData;
import com.brett.engine.data.datatypes.VAO;
import com.brett.world.GameRegistry;

import de.matthiasmann.twl.utils.PNGDecoder;

public class Loader {
	
	/**
	 * list of the VBOs, textures and VAOs.
	 */
	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	private List<Integer> textures = new ArrayList<Integer>();
	// map of loaded textures.
	public Map<String, Integer> textureMap = new HashMap<String, Integer>();
	
	/**
	 * prints the sizes of all the maps
	 */
	public void printSizes() {
		System.out.println("VAOs Size: " + vaos.size());
		System.out.println("VBOs Size: " + vbos.size());
		System.out.println("Textures Size: " + textures.size());
		System.out.println("Texture Map Size: " + textureMap.size());
	}
	
	/**
	 * load to a VAO using indices
	 */
	public VAO loadToVAO(float[] positions,float[] textureCoords,int[] indices){
		// create a VAO
		int vaoID = createVAO();
		int[] vbos = new int[2];
		// bind the index buffer
		bindIndicesBuffer(indices);
		// store the data into vbos
		vbos[0] = storeDataInAttributeList(0,3,positions);
		vbos[1] = storeDataInAttributeList(1,2,textureCoords);
		// unbind the vbos
		unbindVAO();
		// return the model
		return new VAO(vaoID,vbos,indices.length);
	}
	
	/**
	 * load to VAO containing a position and texture while specifying the size
	 */
	public VAO loadToVAO(float[] positions,float[] textureCoords, int dimensions){
		// standard stuff that this point
		// create VAO
		int vaoID = createVAO();
		// we want to keep reference of vbos for runtime deletion
		int[] vbos = new int[2];
		// store the data into the vbos
		vbos[0] = this.storeDataInAttributeList(0, dimensions, positions);
		vbos[1] = this.storeDataInAttributeList(1, dimensions, textureCoords);
		// unbind the VAO
		unbindVAO();
		return new VAO(vaoID, vbos, positions.length);
	}
	
	public VAO loadToVAOChunk(float[] positions,float[] textureCoords){
		// standard stuff that this point
		// create VAO
		int vaoID = createVAO();
		// we want to keep reference of vbos for runtime deletion
		int[] vbos = new int[2];
		// store the data into the vbos
		vbos[0] = this.storeDataInAttributeList(0, 3, positions);
		vbos[1] = this.storeDataInAttributeList(1, 1, textureCoords);
		// unbind the VAO
		unbindVAO();
		return new VAO(vaoID, vbos, positions.length);
	}
	
	/**
	 * loads to VAO using ModelData
	 */
	public VAO loadToVAO(ModelData data) {
		// create the VAO
		int vaoID = createVAO();
		int[] vbos = new int[3];
		// get the indices
		int[] indices = data.getIndices();
		// bind the indices buffer
		bindIndicesBuffer(indices);
		/**
		 * I should note its not actually storing the data itself into the VAO, but a pointer to the VBO
		 * you still need to enable the VBO when rendering.
		 */
		// store data into the vao 
		vbos[0] = storeDataInAttributeList(0,3,data.getVertices());
		vbos[1] = storeDataInAttributeList(1,2,data.getTextureCoords());
		vbos[2] = storeDataInAttributeList(2,3,data.getNormals());
		// unbind the VAO
		unbindVAO();
		// return the model
		return new VAO(vaoID, vbos, indices.length);
	}
	
	/**
	 * creates a VAO with only position data.
	 */
	public VAO loadToVAO(float[] positions, int dimensions) {
		// create the VAO
		int vaoID = createVAO();
		int[] vbos = new int[1];
		// store data in its first position
		vbos[0] = this.storeDataInAttributeList(0, 2, positions);
		// unbind the vao
		unbindVAO();
		// return this as a ModelVAO object.
		return new VAO(vaoID, vbos, positions.length/2);
		
	}
	
	/**
	 * deletes an actual model from the graphics card
	 */
	public VAO deleteVAO(VAO model) {
		try {
			try {
				// if this is a block then we will delete the VBOs
				// list of all the VBOs for this model
				int[] vbos = model.getVbos();
				// remove them from the graphics card
				for (int i = 0; i < vbos.length; i++) {
					GL15.glDeleteBuffers(vbos[i]);
					// make sure we remove them from the list.
					for (int j = 0; j < this.vbos.size(); j++) {
						if (this.vbos.get(i) == vbos[i])
							this.vbos.remove(j);
					}
				}
			} catch (Exception e) {}
			try {
				// make sure we remove it from the VAO list
				for (int i = 0; i < vaos.size(); i++) {
					if (vaos.get(i) == model.getVaoID()) {
						vaos.remove(i);
					}
				}
			} catch (Exception e) {}
			// delete the VAO
			GL30.glDeleteVertexArrays(model.getVaoID());
		} catch (Exception e) {}
		return null;
	}
	
	/**
	 * deletes all the VAOs, VBOs and Textures from the graphics card.
	 */
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
	
	/**
	 * deletes a vao
	 */
	public void deleteVAO(int vaoID) {
		// remove it since we already deleted it
		this.vaos.remove((Integer) vaoID);
		// deletes the vertex array from the graphics card
		GL30.glDeleteVertexArrays(vaoID);
	}
	
	/**
	 * deletes a vbo
	 */
	public void deleteVBO(int vboID) {
		// remove it since we already deleted it
		this.vbos.remove((Integer) vboID); // needed to make this work
		// delete it from the graphics card
		GL15.glDeleteBuffers(vboID);
	}
	
	/**
	 * creates a VAO
	 */
	private int createVAO(){
		// create the VAO
		int vaoID = GL30.glGenVertexArrays();
		// store for later deletion
		vaos.add(vaoID);
		// bind it
		GL30.glBindVertexArray(vaoID);
		// return
		return vaoID;
	}
	
	/**
	 * stores a float[] into a vbo for drawing. This stores on the graphics card.
	 * @param attributeNumber is the pointer for this float[] to be used inside the shader as the layout position
	 * the layout position needs to be then defined inside the shader or bound when creating the shader class.
	 * @param coordinateSize how many floats are to be passed each vertex. 1 is just one float, 2 would be a 2d vector, 3 would be a 3d vector and so on
	 * @param data float[] the data
	 */
	private int storeDataInAttributeList(int attributeNumber, int coordinateSize,float[] data){
		// creates a VBO
		int vboID = GL15.glGenBuffers();
		// add for later deletion
		vbos.add(vboID);
		// binds the buffer as just a standard array buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		// creates a float buffer from the data
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		// puts the data into the VBO
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		// assign this VBO to the pointer and coordinate size. We are using floats, they are not normalized and start at 0
		GL20.glVertexAttribPointer(attributeNumber,coordinateSize,GL11.GL_FLOAT,false,0,0);
		// unbind the VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		// return the VBO
		return vboID;
	}
	
	/**
	 * unbinds any active VAO
	 */
	private void unbindVAO(){
		GL30.glBindVertexArray(0);
	}
	
	/**
	 * puts the indices into the element buffer for index based drawing.
	 */
	private void bindIndicesBuffer(int[] indices){
		// generate the vbo
		int vboID = GL15.glGenBuffers();
		// add it for later deletion
		vbos.add(vboID);
		// bind the buffer as the element buffer (used to defined indexes inside the arrays for drawing)
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		// create the int buffer
		IntBuffer buffer = storeDataInIntBuffer(indices);
		// but the int buffer into the VBO
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	
	/**
	 * stores the int[] into a int buffer
	 */
	private IntBuffer storeDataInIntBuffer(int[] data){
		// create the int buffer
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		// put the data 
		buffer.put(data);
		// flip for reading
		buffer.flip();
		return buffer;
	}
	
	/**
	 * stores the float[] into a float buffer
	 */
	private FloatBuffer storeDataInFloatBuffer(float[] data){
		// create a float buffer
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		// put the data
		buffer.put(data);
		// flip it for reading.
		buffer.flip();
		return buffer;
	}
	
	/**
	 * loads textures from the specified files and then
	 * put them into a cubemap and returns.
	 */
	public int loadCubeMap(String[] textureFiles) {
		// generate a texture buffer
		int texID = GL11.glGenTextures();
		// enable texture
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		// bind buffer this time as a cube map
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);
		
		// loop through all files
		for(int i=0;i<textureFiles.length;i++) {
			// load the texture from the skybox location
			TextureData data = decodeTextureFile("resources/textures/terrain/skyboxes/" + textureFiles[i] + ".png");
			// load the texture into its position in the cube map
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		}
		/**
		 * Apply texture filters
		 */
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE); 
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		// add texture for later deletion.
		textures.add(texID);
		return texID;
	}
	
	/**
	 * loads a texture with default settings.
	 */
	public int loadTexture(String filename) {
		try {
			// load texture with default settings.
			return loadTexture(filename, -0.2f);
		} catch (RuntimeException e) {}
		return 0;
	}
	
	/**
	 * loads textures with a specified LOD bias.
	 */
	public int loadTexture(String filename, float bias) {
		// this used to be a different function but I have changed it to use the special texture loader
		// as it appears to be able to handle non 2^x sized images and its just more clean.
		return loadSpecialTexture(filename, bias, GL11.GL_NEAREST, GL11.GL_LINEAR_MIPMAP_LINEAR);
	}
	
	/**
	 * this is the same thing as <b>{@code loadTexture(String)}</b>
	 * loads a texture with default settings.
	 */
	public int loadSpecialTexture(String texture) {
		// default settings.
		return loadSpecialTexture(texture, -0.2f, GL11.GL_NEAREST, GL11.GL_LINEAR_MIPMAP_LINEAR);
	}
	
	/**
	 * Loads a texture with specified LOD bias, min/mag filtering and mipmap filtering.
	 */
	public int loadSpecialTexture(String texture, float bias, int minmag_filter, int minmag_mipmap) {
		// return the texture if its already been loaded.
		if (textureMap.containsKey(texture))
			return textureMap.get(texture);
		try {
			// don't load if we don't have a window with OpenGL (we are the server)
			//if (GLContext.getCapabilities() == null)
			//	return 0;
			// decode some texture data.
			TextureData d = decodeTextureFile("resources/textures/" + texture + ".png");
			// generate a new texture buffer
			int id = GL11.glGenTextures();
			
			// enable texture
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			// bind the texture buffer
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
			
			// Min and Mag filter is for when a texture is upscaled or downscaled.
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minmag_filter); 
	        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, minmag_filter); 
			
	        // put the texture data into the texture buffer.
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, d.getWidth(), d.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, d.getBuffer());
	        
	        // generates the mipmaps
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			// Min and Mag filter is for when a texture is upscaled or downscaled.
			// im pretty sure i only need to call this ^ but i'd like to make sure that
			// the mipmaps use the same filters.
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minmag_mipmap); 
	        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, minmag_mipmap); 
	        // this bias is how fast a texture loses detail (LOD = level of detail)
			// > 0 = less detail
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, bias);
			// applies anisotropic filtering and makes sure that the graphics card supports this level of AF
			float amount = Math.min(4, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
			
			// add this texture to the map
			textureMap.put(texture, id);
			// add to the textures buffer list (for deletion when the game closes)
			textures.add(id);
			// return the buffer.
			return id;
		} catch (Exception e) {return 0;}
	}
	
	/**
	 * loads all the textures defined inside <b>GameRegistry.registerTextures) 
	 * at as specified width and height
	 */
	public int loadSpecialTextureATLAS(int width, int height) {
		try {
			//for more detail on array textures
			//https://www.khronos.org/opengl/wiki/Array_Texture
			float anisf = Math.min(4, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
			// map of textures that need to be put into the texture array.
			// they should be in order from 0 to #
			HashMap<Integer, String> texs = GameRegistry.registerTextures();
			// generate a texture id like normal
			int id = GL11.glGenTextures();
			
			// enable texture
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			// bind the texture buffer, this time to texture array.
			GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, id); 
			
	        // i really don't like this
	        // openGL4.2. i was trying to use < 3.2
	        // if you are having issues its likely because of this.
	        // "OpenGL 4.2 (2011)"
	        // i feel like this should be in gl30
			// but at the same time im able to use contect of 3.3 without any issues
			// this is very weird and I think this is in the wrong class.
	        GL42.glTexStorage3D(GL30.GL_TEXTURE_2D_ARRAY, 4, GL11.GL_RGBA8, width, height, texs.size());
	        
	        // loop through all textures.
	        for (Entry<Integer, String> s : texs.entrySet()) {
	        	// i don't understand why this is in gl12 but to allocate this is in gl42
	        	GL12.glTexSubImage3D(GL30.GL_TEXTURE_2D_ARRAY,
	        			// level
	        			0, 
	        			// x,y,z offsets using the texture # as the position in the array
	        			0, 0, s.getKey(),
	        			// width, height depth
	        			width, height, 1, 
	        			// format, format
	        			GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 
	        			// decode the image texture
	        			decodeTextureFile("resources/textures/" + s.getValue() + ".png").getBuffer());
	        	// AF
	        	GL11.glTexParameterf(GL30.GL_TEXTURE_2D_ARRAY, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, anisf);
	        }

	        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST); 
	        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
	        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
	        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
	        
	        
	        GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D_ARRAY);
			GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
			GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
			// > 0 = less detail
			GL11.glTexParameterf(GL30.GL_TEXTURE_2D_ARRAY, GL14.GL_TEXTURE_LOD_BIAS, 0.2f);
	        
			// add texture for later deletion.
			textures.add(id);
			return id;
		} catch (Exception e) {}
		return 0;
	}
	
	/**
	 * Decodes texture data from a file
	 */
	private TextureData decodeTextureFile(String fileName) {
		// image data storage.
		int width = 0;
		int height = 0;
		ByteBuffer buffer = null;
		try {
			// input stream for the texture
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileName));
			// decoder for the PNG file
			PNGDecoder decoder = new PNGDecoder(in);
			// assigns the width and height of the texture data
			width = decoder.getWidth();
			height = decoder.getHeight();
			// assign a bytebuffer for this image.
			buffer = ByteBuffer.allocateDirect(4 * width * height);
			// decode the data and put it into the buffer
			decoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
			// flip the buffer for reading
			buffer.flip();
			// close the input stream.
			in.close();
		} catch (Exception e) {
			// we had issue loading texture. exit the game.
			e.printStackTrace();
			System.err.println("Tried to load texture " + fileName + ", didn't work");
			System.exit(-1);
		}
		// return the texture data.
		return new TextureData(buffer, width, height);
	}
	
}
