package com.brett.opencl;

import static org.jocl.CL.*;

import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_kernel;

/**
* @author Brett
* @date 28-Feb-2021
*/

public class StaticCLKernel {
	
	private StaticCLProgram program;
	private cl_kernel kernel;
	// all arrays must be the same size.
	private int size;
	private CLBuffer[] buffers;
	
	public StaticCLKernel(StaticCLProgram program, String name, int size, CLBuffer... buffers) {
		this.program = program;
		kernel = clCreateKernel(program.getProgram(), name, null);
		this.size = size;
		this.buffers = buffers;
		
		for (int i = 0; i < buffers.length; i++) {
			clSetKernelArg(kernel, i, Sizeof.cl_mem, Pointer.to(buffers[i].getBuffer()));
		}
	}
	
	public void writeFloatBuffers(CLBuffer... buffers) {
		if (buffers.length != buffers.length)
			throw new RuntimeException("Buffer size doesn't match.");
		for (int i = 0; i < buffers.length; i++)
			clEnqueueWriteBuffer(CLInit.commandQueue, buffers[i].getBuffer(), CL_TRUE, 0, size * Sizeof.cl_float, buffers[i].getPointer(), 0, null, null);
		this.buffers = buffers;
	}
	
	public void writeIntBuffers(CLBuffer... buffers) {
		if (buffers.length != buffers.length)
			throw new RuntimeException("Buffer size doesn't match.");
		for (int i = 0; i < buffers.length; i++)
			clEnqueueWriteBuffer(CLInit.commandQueue, buffers[i].getBuffer(), CL_TRUE, 0, size * Sizeof.cl_int, buffers[i].getPointer(), 0, null, null);
		this.buffers = buffers;
	}
	
	public void writeFloatBuffer(CLBuffer buffer) {
		clEnqueueWriteBuffer(CLInit.commandQueue, buffer.getBuffer(), CL_TRUE, 0, size * Sizeof.cl_float, buffer.getPointer(), 0, null, null);
	}
	
	public void writeFloatBuffer(CLBuffer buffer, int size) {
		clEnqueueWriteBuffer(CLInit.commandQueue, buffer.getBuffer(), CL_TRUE, 0, size * Sizeof.cl_float, buffer.getPointer(), 0, null, null);
	}
	
	public void writeIntBuffer(CLBuffer buffer) {
		clEnqueueWriteBuffer(CLInit.commandQueue, buffer.getBuffer(), CL_TRUE, 0, size * Sizeof.cl_int, buffer.getPointer(), 0, null, null);
	}
	
	public void writeIntBuffer(CLBuffer buffer, int size) {
		clEnqueueWriteBuffer(CLInit.commandQueue, buffer.getBuffer(), CL_TRUE, 0, size * Sizeof.cl_int, buffer.getPointer(), 0, null, null);
	}
	
	public void execute(CLBuffer out) {
		long global_work_size[] = new long[]{size};
		clEnqueueNDRangeKernel(CLInit.commandQueue, kernel, 1, null,
	            global_work_size, null, 0, null, null);
		clEnqueueReadBuffer(CLInit.commandQueue, out.getBuffer(), CL_TRUE, 0,
	            size * Sizeof.cl_float, out.getPointer(), 0, null, null);
	}
	
	public void execute(CLBuffer out, Pointer arrOut) {
		long global_work_size[] = new long[]{size};
		clEnqueueNDRangeKernel(CLInit.commandQueue, kernel, 1, null,
	            global_work_size, null, 0, null, null);
		clEnqueueReadBuffer(CLInit.commandQueue, out.getBuffer(), CL_TRUE, 0,
	            size * Sizeof.cl_float, arrOut, 0, null, null);
	}
	
	public void executeInt(CLBuffer out) {
		long global_work_size[] = new long[]{size};
		clEnqueueNDRangeKernel(CLInit.commandQueue, kernel, 1, null,
	            global_work_size, null, 0, null, null);
		clEnqueueReadBuffer(CLInit.commandQueue, out.getBuffer(), CL_TRUE, 0,
	            size * Sizeof.cl_int, out.getPointer(), 0, null, null);
	}
	
	public void executeInt(CLBuffer out, Pointer arrOut) {
		long global_work_size[] = new long[]{size};
		clEnqueueNDRangeKernel(CLInit.commandQueue, kernel, 1, null,
	            global_work_size, null, 0, null, null);
		clEnqueueReadBuffer(CLInit.commandQueue, out.getBuffer(), CL_TRUE, 0,
	            size * Sizeof.cl_int, arrOut, 0, null, null);
	}
	
	public void cleanup() {
		for (int i = 0; i < buffers.length; i++)
			clReleaseMemObject(buffers[i].getBuffer());
		clReleaseKernel(kernel);
	}

	public StaticCLProgram getProgram() {
		return program;
	}

	public cl_kernel getKernel() {
		return kernel;
	}

	public int getSize() {
		return size;
	}
	
	public static CLBuffer createBuffer(float[] arr) {
		Pointer srcA = Pointer.to(arr);
		return new CLBuffer(clCreateBuffer(CLInit.context, 
	            CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
	            Sizeof.cl_float * arr.length, srcA, null), srcA);
	}
	
	public static CLBuffer createBufferInt(int[] arr) {
		Pointer srcA = Pointer.to(arr);
		return new CLBuffer(clCreateBuffer(CLInit.context, 
	            CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
	            Sizeof.cl_int * arr.length, srcA, null), srcA);
	}
	
}
