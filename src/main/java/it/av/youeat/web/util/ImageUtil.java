package it.av.youeat.web.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.wicket.markup.html.image.resource.BufferedDynamicImageResource;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
public final class ImageUtil {

    /**
     * Scale and crop an image
     * 
     * @param img image to be scaled
     * @param width the width to which to scale the image.
     * @param height the height to which to scale the image.
     * @return the scaled image
     */
    public final static BufferedDynamicImageResource getScaledImage(java.awt.Image img, int width, int height) {
        final BufferedDynamicImageResource resource = new BufferedDynamicImageResource();
        int widthImg = img.getWidth(null);
        int heightImg = img.getHeight(null);

        final BufferedImage imgScaled = new BufferedImage(widthImg, heightImg, BufferedImage.TYPE_INT_RGB);
        imgScaled.getGraphics().drawImage(img, 0, 0, null);
        // if the image is smaller than the request, don't do anything
        if (widthImg <= width || heightImg <= height) {
            imgScaled.getGraphics().drawImage(img, 0, 0, null);
            resource.setImage(imgScaled);
            return resource;
        }
        // if width == height, crop not necessary, scale only
        if (widthImg == heightImg) {
            img = imgScaled.getScaledInstance(width, width, java.awt.Image.SCALE_DEFAULT);
            imgScaled.getGraphics().drawImage(img, 0, 0, null);
            resource.setImage(imgScaled);
            return resource;
        }
        if (widthImg > heightImg) {
            img = imgScaled.getScaledInstance(-width, height, java.awt.Image.SCALE_SMOOTH);
        } else if (heightImg > widthImg) {
            img = imgScaled.getScaledInstance(width, -height, java.awt.Image.SCALE_SMOOTH);
        }
        widthImg = img.getWidth(null);
        heightImg = img.getHeight(null);
        final BufferedImage imageCrop = new BufferedImage(widthImg, heightImg, BufferedImage.TYPE_INT_RGB);
        imageCrop.getGraphics().drawImage(img, 0, 0, null);
        int xCrop = 0;
        int yCrop = 0;
        if (widthImg > heightImg) {
            xCrop = (widthImg - width) / 2;
        } else if (heightImg > widthImg) {
            yCrop = (heightImg - height) / 2;
        }
        int x = 0 + xCrop;
        int y = 0 + yCrop;
        int w = width;
        int h = height;
        imageCrop.flush();
        imgScaled.flush();
        resource.setImage(imageCrop.getSubimage(x, y, w, h));
        return resource;
    }
    
    public final static byte[] getScaledImageByteArray(java.awt.Image img, int width, int height){
        BufferedDynamicImageResource res = getScaledImage(img, width, height);
        try {
            InputStream is = res.getResourceStream().getInputStream();
            return IOUtils.toByteArray(is);
        } catch (ResourceStreamNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
}
