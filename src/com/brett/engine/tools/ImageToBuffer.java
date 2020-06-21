package com.brett.engine.tools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;

/**
* @author Brett
* @date Jun. 20, 2020
*/

public class ImageToBuffer {
	
    private static final ColorModel glAlphaColorModel = 
    		new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
	            new int[] {8,8,8,8},
	            true,
	            false,
	            ComponentColorModel.TRANSLUCENT,
	            DataBuffer.TYPE_BYTE);
    
    /** The colour model for the GL image */
    private static final  ColorModel glColorModel =
    		new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[] {8,8,8,0},
                false,
                false,
                ComponentColorModel.OPAQUE,
                DataBuffer.TYPE_BYTE);
	
	@SuppressWarnings("rawtypes")
	public static ByteBuffer convet(BufferedImage image) {
		ByteBuffer imageBuffer = null; 
        WritableRaster raster;
        BufferedImage texImage;
        
        int texWidth = 2;
        int texHeight = 2;
        
        // find the closest power of 2 for the width and height
        // of the produced texture

        while (texWidth < image.getWidth()) {
            texWidth *= 2;
        }
        while (texHeight < image.getHeight()) {
            texHeight *= 2;
        }
        
        int width = image.getWidth();
        int height = image.getHeight();
        
        // create a raster that can be used by OpenGL as a source
        // for a texture
        boolean useAlpha = image.getColorModel().hasAlpha(); 
        
        @SuppressWarnings("unused")
		int depth = 0;
        
        if (useAlpha) {
        	depth = 32;
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,texWidth,texHeight,4,null);
            texImage = new BufferedImage(glAlphaColorModel,raster,false,new Hashtable());
        } else {
        	depth = 24;
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,texWidth,texHeight,3,null);
            texImage = new BufferedImage(glColorModel,raster,false,new Hashtable());
        }
            
        // copy the source image into the produced image
        Graphics2D g = (Graphics2D) texImage.getGraphics();
        
        // only need to blank the image for mac compatibility if we're using alpha
        if (useAlpha) {
	        g.setColor(new Color(0f,0f,0f,0f));
	        g.fillRect(0,0,texWidth,texHeight);
        }
        
        g.drawImage(image,0,0,null);
        
        if (true) {
	        if (height < texHeight - 1) {
	        	copyArea(texImage, 0, 0, width, 1, 0, texHeight-1);
	        	copyArea(texImage, 0, height-1, width, 1, 0, 1);
	        }
	        if (width < texWidth - 1) {
	        	copyArea(texImage, 0,0,1,height,texWidth-1,0);
	        	copyArea(texImage, width-1,0,1,height,1,0);
	        }
        }
        
        // build a byte buffer from the temporary image 
        // that be used by OpenGL to produce a texture.
        byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData(); 
        
        imageBuffer = ByteBuffer.allocateDirect(data.length); 
        imageBuffer.order(ByteOrder.nativeOrder()); 
        imageBuffer.put(data, 0, data.length); 
        imageBuffer.flip();
        g.dispose();
        
        return imageBuffer; 
	}
	
	private static void copyArea(BufferedImage image, int x, int y, int width, int height, int dx, int dy) {
		Graphics2D g = (Graphics2D) image.getGraphics();
		
		g.drawImage(image.getSubimage(x, y, width, height),x+dx,y+dy,null);
	}
	
}
