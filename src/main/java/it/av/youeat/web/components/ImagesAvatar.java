package it.av.youeat.web.components;

import it.av.youeat.ocm.model.Eater;

import org.apache.wicket.Page;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.image.resource.DynamicImageResource;

/**
 * Common components for Image
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class ImagesAvatar {

    /**
     * Return the avatar for the given user, the default image if no avatar presents
     * 
     * @param id the id of the wicket component
     * @param eater the owner of the avatar
     * @param page as reference to load the static avatar resource
     * @param isCachingImage if the Image is cachable
     * @return an avatar image
     */
    public static Image getAvatar(String id, final Eater eater, Page page, boolean isCachingImage) {
        ResourceReference img = new ResourceReference(page.getClass(), "resources/images/avatar.gif");//
        Image avatar;
        if (isCachingImage) {
            avatar = new Image(id);
        } else {
            avatar = new NonCachingImage(id);
        }
        if (eater.getAvatar() == null) {
            avatar.setImageResourceReference(img);
        } else {

            avatar.setImageResource(new DynamicImageResource() {
                @Override
                protected byte[] getImageData() {
                    return eater.getAvatar();
                }
            });
        }
        return avatar;
    }
}
