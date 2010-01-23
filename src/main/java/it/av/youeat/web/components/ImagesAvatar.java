package it.av.youeat.web.components;

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.web.util.ImageUtil;

import javax.swing.ImageIcon;

import org.apache.wicket.Page;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;

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

        Image avatar;
        ImageIcon icon;
        if (isCachingImage) {
            avatar = new Image(id);
        } else {
            avatar = new NonCachingImage(id);
        }
        if (eater.getAvatar() == null) {
            avatar.setImageResourceReference(getDefaultAvatar(page));
            // icon = new ImageIcon("resources/images/avatar.gif");
            return avatar;
        } else {
            icon = new ImageIcon(eater.getAvatar());
            // avatar.setImageResource(new DynamicImageResource() {
            // @Override
            // protected byte[] getImageData() {
            // return eater.getAvatar();
            // }
            // });
        }
        java.awt.Image image2 = icon.getImage();
        return new Image(id, ImageUtil.getScaledImage(image2, 50, 50));
    }

    public static ResourceReference getDefaultAvatar(Page page) {
        return new ResourceReference(page.getClass(), "resources/images/avatar.gif");//
    }

}
