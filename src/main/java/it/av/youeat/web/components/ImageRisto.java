package it.av.youeat.web.components;

import it.av.youeat.web.util.ImageUtil;

import javax.swing.ImageIcon;

import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.image.resource.DynamicImageResource;

/**
 * Utils for risto Image
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class ImageRisto {

    /**
     * Return wicket image scaled if necessary
     * 
     * @param id the id of the wicket component
     * @param image image to be rendered
     * @param width the width to which to scale the image, <=0 to keep original size.
     * @param heigth the height to which to scale the image, <=0 to keep original size.
     * @param isCachingImage true to cache
     * @return the wicket image
     */
    public static Image getImage(String id, final byte[] image, int width, int heigth, boolean isCachingImage) {
        Image imgRisto;
        if (isCachingImage) {
            imgRisto = new Image(id);
        } else {
            imgRisto = new NonCachingImage(id);
        }
        ImageIcon icon = new ImageIcon(image);
        if (width <= 0 || heigth <= 0) {
            imgRisto.setImageResource(new DynamicImageResource() {
                @Override
                protected byte[] getImageData() {
                    return image;
                }
            });
        } else {
            imgRisto.setImageResource(ImageUtil.getScaledImage(icon.getImage(), width, heigth));
        }
        return imgRisto;
    }
}
