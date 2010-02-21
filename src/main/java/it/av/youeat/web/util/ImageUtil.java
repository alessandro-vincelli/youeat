package it.av.youeat.web.util;

import java.awt.image.BufferedImage;

import org.apache.wicket.markup.html.image.resource.BufferedDynamicImageResource;

/**
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public final class ImageUtil {

    private ImageUtil() {
    }

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
        java.awt.Image imgNew = new BufferedImage(widthImg, heightImg, BufferedImage.TYPE_INT_RGB);

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
            imgNew = imgScaled.getScaledInstance(width, width, java.awt.Image.SCALE_DEFAULT);
            imgScaled.getGraphics().drawImage(imgNew, 0, 0, null);
            resource.setImage(imgScaled);
            return resource;
        }
        if (widthImg > heightImg) {
            imgNew = imgScaled.getScaledInstance(-width, height, java.awt.Image.SCALE_SMOOTH);
        } else if (heightImg > widthImg) {
            imgNew = imgScaled.getScaledInstance(width, -height, java.awt.Image.SCALE_SMOOTH);
        }
        widthImg = imgNew.getWidth(null);
        heightImg = imgNew.getHeight(null);
        final BufferedImage imageCrop = new BufferedImage(widthImg, heightImg, BufferedImage.TYPE_INT_RGB);
        imageCrop.getGraphics().drawImage(imgNew, 0, 0, null);
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
        imgNew.flush();
        resource.setImage(imageCrop.getSubimage(x, y, w, h));
        return resource;
    }
}
