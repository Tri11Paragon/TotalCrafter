package com.brett;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.brett.engine.managers.DisplayManager;
import com.brett.engine.managers.ScreenManager;
import com.brett.engine.tools.RunLengthEncoding;
import com.brett.engine.ui.screen.SinglePlayer;
import com.brett.world.chunks.BlockStorage;
import com.brett.world.chunks.Noise;

public class Main {

	public static final float RED = 0.5444f;
	public static final float GREEN = 0.62f;
	public static final float BLUE = 0.69f;
	
	public static boolean isOpen = true;
	
	/**
	 * Josiah doesn't get credit
	 * 
	 * ThinMatrix gets credit? (loader)
	 */
	
	public static void main(String[] args) {
		ScreenManager.pre_init();
		ScreenManager.init();
		ScreenManager.post_init();
		
		BlockStorage storage = new BlockStorage();
		
		
		for (int i = 0; i < 128; i++) {
			for (int j = 0; j < 128; j++) {
				int reference = (int) (Noise.noise((double)i / 174.4d, (double)j/174.3d)*64)+64;
				for (int k = 0; k < reference; k++) {
					if (k == reference-1)
						storage.set(i, k, j, 3);
					else if (k < reference-1 && k > reference-4)
						storage.set(i, k, j, 2);
					else if (k < reference-4)
						storage.set(i, k, j, 1);
				}
				
			}
		}
		
		short[] loler = storage.blocks;
		
		System.out.println("Uncompressed array: " + loler.length + " : OLD 2097152");
		
		long gzipStart = System.currentTimeMillis();
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			DataOutputStream dao = new DataOutputStream(new GZIPOutputStream(bos));
			for (int i = 0; i < loler.length; i++) {
				dao.write(loler[i]);
			}
			dao.close();
			System.out.println("Compressed Byte Array Size: " + bos.toByteArray().length);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("GZIP COMPRESS TIME: " + (System.currentTimeMillis() - gzipStart)); 
		
		long encodeStart = System.currentTimeMillis();
		short[] rle = RunLengthEncoding.encodeDouble(loler);
		System.out.println("RLE COMPRESS TIME: " + (System.currentTimeMillis() - encodeStart));
		
		System.out.println("RLE: " + rle.length);
		
		long zipedStart = System.currentTimeMillis();
		ByteArrayOutputStream bosd = new ByteArrayOutputStream();
		try {
			DataOutputStream dao = new DataOutputStream(new GZIPOutputStream(bosd));
			for (int i = 0; i < rle.length; i++) {
				dao.write(rle[i]);
			}
			dao.close();
			System.out.println("Compressed Byte Array Size (With RLE): " + bosd.toByteArray().length);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("GZIP COMPRESS RLE TIME: " + (System.currentTimeMillis() - zipedStart));
		System.out.println("GZIP COMPRESS + RLE ENCODE TIME: " + (System.currentTimeMillis() - encodeStart));
		
		System.out.println("RLE Data: ");
		try {
			BufferedWriter fos = new BufferedWriter(new FileWriter("superfile.txt"));
			for (int i = 0; i < rle.length; i+=2) {
				fos.write("[" + rle[i] + "|" + rle[i+1] + "], ");
			}
			fos.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		ScreenManager.switchScreen(new SinglePlayer());
		
		while (isOpen) {
			isOpen = !GLFW.glfwWindowShouldClose(DisplayManager.window);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glClearColor(RED, GREEN, BLUE, 1);
			
			try {
				ScreenManager.update();
			} catch (Exception e) {
				GLFW.glfwSetInputMode(DisplayManager.window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
				ScreenManager.close();
				isOpen = false;
				e.printStackTrace();
				break;
			}
			
			DisplayManager.updateDisplay();
		}
		
		ScreenManager.close();
	}

}
