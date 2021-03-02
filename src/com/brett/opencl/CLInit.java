package com.brett.opencl;

import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.CL_DEVICE_TYPE_GPU;
import static org.jocl.CL.clCreateCommandQueueWithProperties;
import static org.jocl.CL.clCreateContext;
import static org.jocl.CL.clGetDeviceIDs;
import static org.jocl.CL.clGetPlatformIDs;
import static org.jocl.CL.clReleaseCommandQueue;
import static org.jocl.CL.clReleaseContext;

import org.jocl.CL;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_platform_id;
import org.jocl.cl_queue_properties;

/**
* @author Brett
* @date 28-Feb-2021
*/

public class CLInit {
	
	public static boolean inited = false;
	public static int numPlatforms;
	public static cl_platform_id platform;
	public static cl_context_properties contextProperties;
	public static int numDevices;
	public static cl_device_id device;
	public static cl_context context;
	public static cl_queue_properties properties;
	public static cl_command_queue commandQueue;

	public static void cleanup() {
		clReleaseCommandQueue(CLInit.commandQueue);
        clReleaseContext(CLInit.context);
	}
	
	public static void init() {
		final int platformIndex = 0;
        final long deviceType = CL_DEVICE_TYPE_GPU;
        final int deviceIndex = 0;
        
        CL.setExceptionsEnabled(true);
        
        int numPlatformsArray[] = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        numPlatforms = numPlatformsArray[0];
        
        System.out.println("Number of OpenCL Platforms: " + numPlatforms);
		
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        platform = platforms[platformIndex];
        
        contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);
        
        int numDevicesArray[] = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        numDevices = numDevicesArray[0];
        
        // Obtain a device ID 
        cl_device_id devices[] = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        device = devices[deviceIndex];
        
        // Create a context for the selected device
        context = clCreateContext(
            contextProperties, 1, new cl_device_id[]{device}, 
            null, null, null);
        
        // Create a command-queue for the selected device
        properties = new cl_queue_properties();
        commandQueue = clCreateCommandQueueWithProperties(
            context, device, properties, null);
        
        inited = true;
	}
	
}
