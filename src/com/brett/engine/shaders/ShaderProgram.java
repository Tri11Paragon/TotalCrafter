package com.brett.engine.shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

/**
* @author Brett
* @date Jun. 20, 2020
*/

public abstract class ShaderProgram {
	
	// this is the shader id;
	private int programID;

	// individual shader ids.
	private int vertexShaderID;
	private int geometryShaderID = -1;
	private int fragmentShaderID;

	private static float[] matrixBuffer = new float[16];

	public ShaderProgram(String vertexFile, String fragmentFile) {
		// load the shaders
		vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		// create the shader program
		programID = GL20.glCreateProgram();
		// attach the loaded shaders to the shader program
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		bindAttributes();
		// link and make sure that our program is valid.
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		getAllUniformLocations();
	}

	public ShaderProgram(String vertexFile, String fragmentFile, String geometryShader) {
		// load the shaders
		vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		geometryShaderID = loadShader(geometryShader, GL32.GL_GEOMETRY_SHADER);
		fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		// create the shader program
		programID = GL20.glCreateProgram();
		// attach the loaded shaders to the shader program
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, geometryShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		bindAttributes();
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		getAllUniformLocations();
	}

	/**
	 * The function allows you to grab the uniform locations inside the shader, and
	 * reduce them to an integer pointer. This pointer can then be used at a later
	 * time to load information into the shader, be it a matrix or a color or any
	 * OpenGL type.
	 */
	protected abstract void getAllUniformLocations();

	/**
	 * finds the uniform location by name and returns a integer pointer to its
	 * location
	 */
	protected int getUniformLocation(String uniformName) {
		return GL20.glGetUniformLocation(programID, uniformName);
	}

	public void start() {
		GL20.glUseProgram(programID);
	}

	public void stop() {
		GL20.glUseProgram(0);
	}

	/**
	 * cleanup all the nonsense
	 */
	public void cleanUp() {
		stop();
		// remove all the shaders from the program
		GL20.glDetachShader(programID, vertexShaderID);
		if (geometryShaderID >= 0)
			GL20.glDetachShader(programID, geometryShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		// delete the shaders
		GL20.glDeleteShader(vertexShaderID);
		if (geometryShaderID >= 0)
			GL20.glDeleteShader(geometryShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		// delete the shader program
		GL20.glDeleteProgram(programID);
	}

	/**
	 * This function is to be used with the bind attribute function to assign the
	 * inputs to shaders. binding attributes need to occur in this method
	 * <b>alone</b>.
	 */
	protected abstract void bindAttributes();

	/**
	 * The function takes an attribute position and assigns it to the string name
	 * set in the shader file. <br>
	 * <b>What's an attribute position?</b> <br>
	 * Well, remember when we set: <br>
	 * <code>this.storeDataInAttributeList(attribute, dimensions, float[]);</code>
	 * <br>
	 * in the loader class? Well the attribute number is the one you assign here.
	 * <br>
	 * So if you assign say your position float[] to attribute 0, <br>
	 * and your in on the shader is "<code> in vec3 position <code>" <br>
	 * then you do "<code> bindAttribute(0, "position"); </code>"
	 */
	protected void bindAttribute(int attribute, String variableName) {
		GL20.glBindAttribLocation(programID, attribute, variableName);
	}

	/*
	 * functions below load datatypes into the shader
	 */

	protected void loadFloat(int location, float value) {
		GL20.glUniform1f(location, value);
	}

	protected void loadInt(int location, int value) {
		GL20.glUniform1i(location, value);
	}

	protected void loadVector(int location, Vector3f vector) {
		GL20.glUniform3f(location, vector.x, vector.y, vector.z);
	}
	
	protected void loadVector(int location, Vector3d vector) {
		GL20.glUniform3f(location, (float) vector.x, (float) vector.y, (float) vector.z);
	}
	
	protected void loadVector(int location, float x, float y, float z) {
		GL20.glUniform3f(location, x, y, z);
	}

	protected void load4DVector(int location, Vector4f vector) {
		GL20.glUniform4f(location, vector.x, vector.y, vector.z, vector.w);
	}

	protected void load2DVector(int location, Vector2f vector) {
		GL20.glUniform2f(location, vector.x, vector.y);
	}

	protected void load2DVector(int location, float x, float y) {
		GL20.glUniform2f(location, x, y);
	}

	protected void loadBoolean(int location, boolean value) {
		int toLoad = 0;
		if (value) {
			toLoad = 1;
		}
		GL20.glUniform1i(location, toLoad);
	}

	protected void loadMatrix(int location, Matrix4f matrix) {
		matrix.get(matrixBuffer);
		GL20.glUniformMatrix4fv(location, false, matrixBuffer);
	}

	/*
	 * following functions should not be used during main loops. please get the uniform location first then use the above functions.
	 */
	// cache
	// using a cache makes this faster, especially if a programmer isn't smart and doesn't listen to above
	// but is still slower then using the above functions
	private HashMap<String, Integer> locations = new HashMap<String, Integer>();
	
	public void loadFloat(String location, float value) {
		Integer l = locations.get(location);
		if (l != null)
			loadFloat(l, value);
		else {
			l = getUniformLocation(location);
			loadFloat(l, value);
			locations.put(location, l);
		}
	}

	public void loadInt(String location, int value) {
		Integer l = locations.get(location);
		if (l != null)
			loadInt(l, value);
		else {
			l = getUniformLocation(location);
			loadInt(l, value);
			locations.put(location, l);
		}
	}

	public void loadVector(String location, Vector3f vector) {
		Integer l = locations.get(location);
		if (l != null)
			loadVector(l, vector);
		else {
			l = getUniformLocation(location);
			loadVector(l, vector);
			locations.put(location, l);
		}
	}
	
	public void loadVector(String location, Vector3d vector) {
		Integer l = locations.get(location);
		if (l != null)
			loadVector(l, vector);
		else {
			l = getUniformLocation(location);
			loadVector(l, vector);
			locations.put(location, l);
		}
	}
	
	public void loadVector(String location, float x, float y, float z) {
		Integer l = locations.get(location);
		if (l != null)
			loadVector(l, x, y, z);
		else {
			l = getUniformLocation(location);
			loadVector(l, x, y, z);
			locations.put(location, l);
		}
	}

	public void load4DVector(String location, Vector4f vector) {
		Integer l = locations.get(location);
		if (l != null)
			load4DVector(l, vector);
		else {
			l = getUniformLocation(location);
			load4DVector(l, vector);
			locations.put(location, l);
		}
	}

	public void load2DVector(String location, Vector2f vector) {
		Integer l = locations.get(location);
		if (l != null)
			load2DVector(l, vector);
		else {
			l = getUniformLocation(location);
			load2DVector(l, vector);
			locations.put(location, l);
		}
	}

	public void load2DVector(String location, float x, float y) {
		Integer l = locations.get(location);
		if (l != null)
			load2DVector(l, x, y);
		else {
			l = getUniformLocation(location);
			load2DVector(l, x, y);
			locations.put(location, l);
		}
	}

	public void loadBoolean(String location, boolean value) {
		Integer l = locations.get(location);
		if (l != null)
			loadBoolean(l, value);
		else {
			l = getUniformLocation(location);
			loadBoolean(l, value);
			locations.put(location, l);
		}
	}

	public void loadMatrix(String location, Matrix4f matrix) {
		Integer l = locations.get(location);
		if (l != null)
			loadMatrix(l, matrix);
		else {
			l = getUniformLocation(location);
			loadMatrix(l, matrix);
			locations.put(location, l);
		}
	}
	
	/**
	 * 
	 * @param file - File location inside resources/shaders
	 * @param type - Type of shader to use
	 * @return the id of the shader
	 */
	private static int loadShader(String file, int type) {
		StringBuilder shaderSource = new StringBuilder();
		/**
		 * reads all the lines in a shader file
		 */
		try {
			BufferedReader reader = new BufferedReader(new FileReader("resources/shaders/" + file));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("//\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		// creates a shader
		int shaderID = GL20.glCreateShader(type);
		// puts the loaded shader code into the graphics card
		GL20.glShaderSource(shaderID, shaderSource);
		// Compile it
		GL20.glCompileShader(shaderID);
		// make sure there is no errors
		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.err.println("Could not compile shader! (Shader type: " + type + ")");
			System.exit(-1);
		}
		return shaderID;
	}
	
}
