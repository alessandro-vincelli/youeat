package it.av.youeat.web.components;

import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.web.util.ImageUtil;

import javax.swing.ImageIcon;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.wicket.Page;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.request.resource.DynamicImageResource;

/**
 * Utils for risto Image
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public final class ImageRisto {
    
    private ImageRisto(){
    }
    
    /**
     * Return wicket image scaled to 130x130
     * 
     * @param id the id of the wicket component
     * @param image image to be rendered
     * @param isCachingImage true to cache
     * @return the wicket image
     */
    public static Image getThumbnailImage(String id, final byte[] image, boolean isCachingImage) {
        return getImage(id, image, 130, 130, isCachingImage);
    }
    
    /**
     * Return a random risto image scaled to 130x130
     * 
     * @param id the id of the wicket component
     * @param risto the risto images
     * @param page as reference to load the static avatar resource
     * @param isCachingImage true to cache
     * @return the wicket image
     */
	public static Image getRandomThumbnailImage(String id, Ristorante risto, Page page, boolean isCachingImage) {
		int size = risto.getPictures().size();
		if (size > 0) {
			return getImage(id, risto.getPictures().get(RandomUtils.nextInt(size)).getPicture(), 130, 130, isCachingImage);
		}
		return new Image(id) {
			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				// necessary to override the default loading resource
				tag.addBehavior(new SimpleAttributeModifier("src", "/images/logo-mela-small.png"));
			}
		};
	}

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
            imgRisto = new Image(id, id);
        } else {
            imgRisto = new NonCachingImage(id);
        }
        ImageIcon icon = new ImageIcon(image);
        if (width <= 0 || heigth <= 0) {
            imgRisto.setImageResource(new DynamicImageResource() {
                @Override
                protected byte[] getImageData(Attributes attributes) {
                    return image;
                }
            });
        } else {
            imgRisto.setImageResource(ImageUtil.getScaledImage(icon.getImage(), width, heigth));
        }
        return imgRisto;
    }
}
