package com.brett.opencl;

import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clReleaseProgram;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.jocl.cl_program;

/**
* @author Brett
* @date 28-Feb-2021
*/

public class StaticCLProgram {

	private cl_program program;
	
	public StaticCLProgram(String file) {
		program = load(file);
	}
	
	public StaticCLProgram(String[] program) {
		this.program = clCreateProgramWithSource(CLInit.context,
                1, program, null, null);
		
		clBuildProgram(this.program, 0, null, null, null, null);
	}
	
	public void cleanup() {
		clReleaseProgram(program);
	}
	
	public cl_program getProgram() {
		return program;
	}
	
	private static cl_program load(String file) {
		if (!CLInit.inited)
			throw new RuntimeException("OpenCL not created!");
		StringBuilder clSource = new StringBuilder();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader("resources/cl/" + file));
			String line;
			while ((line = reader.readLine()) != null) {
				clSource.append(line).append("//\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		cl_program program = clCreateProgramWithSource(CLInit.context,
                1, new String[]{ clSource.toString() }, null, null);
		
		clBuildProgram(program, 0, null, null, null, null);
		
		return program;
	}
	
}
