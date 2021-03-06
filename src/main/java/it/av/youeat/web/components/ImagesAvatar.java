package it.av.youeat.web.components;

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.web.util.ImageUtil;

import javax.swing.ImageIcon;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.image.resource.BufferedDynamicImageResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

/**
 * Common components for Image
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public final class ImagesAvatar {
    
    private ImagesAvatar(){
    }

    /**
     * Return the avatar for the given user, the default image if no avatar presents
     * 
     * @param id the id of the wicket component
     * @param eater the owner of the avatar
     * @param page as reference to load the static avatar resource
     * @param isCachingImage if the Image is cachable
     * @return an avatar image
     */
    public static Image getAvatar(String id, Eater eater, Page page, boolean isCachingImage) {

        Image avatar;
        ImageIcon icon;
        if (isCachingImage) {
            avatar = new Image(id, eater.getFirstname());
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
        if (isCachingImage) {
            Image img = new Image(id, ImageUtil.getScaledImage(image2, 50, 50));
            img.setOutputMarkupId(true);
            return img;
        } else {
            // NonCachingImage img = new NonCachingImage(id, ImageUtil.getScaledImage(image2, 50, 50));
            // NonCachingImage img = new NonCachingImage(id, new ImageAvateRend(50, 50, image2));
            NonCachingImage img = new NonCachingImage(id, new ImageAvatarResource(eater));
            img.setOutputMarkupId(true);
            return img;
        }
    }

    public static ResourceReference getDefaultAvatar(Page page) {
        return new ResourceReference(page.getClass(), "resources/images/avatar.gif"){

            @Override
            public IResource getResource() {
                // TODO 1.5 Auto-generated method stub
                return new BufferedDynamicImageResource();
            }
            
        };//
    }

}
