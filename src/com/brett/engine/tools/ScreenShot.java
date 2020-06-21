package com.brett.engine.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.brett.engine.data.IKeyState;
import com.brett.engine.managers.DisplayManager;

/**
* @author Brett
* @date Jun. 20, 2020
*/

public class ScreenShot implements IKeyState {
	
	// makes sure that the date is in a nice format for us
		// kinda a wimpy thing to use
		private static DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");  
		
		public ScreenShot() {}

		@Override
		public void onKeyPressed(int button) {
			if (button == GLFW.GLFW_KEY_F2) {
				// number of bytes per image
				// 4 fits nicely inside a int but you really only need 3 as there won't be any alpha.
				// also wikipedia says that pngs are in 4 byte chunks
				// but like the alpha is constant so there is really no reason to probe it from
				// the current frame buffer
				// howeever not probing it is more work
				int bytes = 4;
				
				// create a byte array.
				ByteBuffer pixels = BufferUtils.createByteBuffer(bytes * DisplayManager.WIDTH * DisplayManager.HEIGHT);
				
				// read the pixels off the screen
				GL11.glReadPixels(0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
				
				// get the current time
				LocalDateTime now = LocalDateTime.now();
				
				// make sure this folder exists
				File iff = new File("screenshots/" + df.format(now) + "_" + (System.currentTimeMillis() % 1000 + ".png"));
				iff.mkdirs();
				
				
				// create a buffered image
				BufferedImage ss = new BufferedImage(DisplayManager.WIDTH, DisplayManager.HEIGHT, 0x1);
				// loop through all the pixels
				for (int x = 0; x < DisplayManager.WIDTH; x++) {
					for (int y = 0; y < DisplayManager.HEIGHT; y++) {
						// index inside bytebuffer.
						int i = (x + (DisplayManager.WIDTH * y)) * bytes;
						// we only care about the first 8 bits (up to 255) as RGB standard is 0-255.
						// first is red
						int r = pixels.get(i) & 0xFF;
						// second is green
						int g = pixels.get(i + 1) & 0xFF;
						// third is blue
						int b = pixels.get(i + 2) & 0xFF;
						// set these RGB values inside the buffered image.

						// OpenGL is stupid and starts from the bottom right corner of your screen, so
						// we have to adjust for this
						// (DisplayManager.HEIGHT - (y + 1))
						// "(0xFF << 24) | (r << 16) | (g << 8) | b" is because java image buffer loves ints
						// and the PNG format calls for 4 bytes (one int)
						ss.setRGB(x, DisplayManager.HEIGHT - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
					}
				}
				
				try {
					// write the image to a file.
					ImageIO.write(ss, "PNG", iff);
				} catch (IOException e) {}
			}
		}

		@Override
		public void onKeyReleased(int button) {
		}
	
}
