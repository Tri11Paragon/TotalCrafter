package com.brett.opencl;

import org.jocl.Pointer;
import org.jocl.cl_mem;

/**
* @author Brett
* @date 1-Mar-2021
*/

public class CLBuffer {
	
	private cl_mem mem;
	private Pointer pointer;
	
	public CLBuffer(cl_mem mem, Pointer pointer) {
		this.mem = mem;
		this.pointer = pointer;
	}

	public cl_mem getBuffer() {
		return mem;
	}

	public Pointer getPointer() {
		return pointer;
	}
	
	public CLBuffer update(float[] arr) {
		this.pointer = Pointer.to(arr);
		return this;
	}
	
	public CLBuffer update(int[] arr) {
		this.pointer = Pointer.to(arr);
		return this;
	}
	
}
