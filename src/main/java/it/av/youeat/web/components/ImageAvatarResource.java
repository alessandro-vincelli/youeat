package it.av.youeat.web.components;

import it.av.youeat.ocm.model.Eater;

import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.util.time.Time;


public class ImageAvatarResource extends DynamicImageResource {

    private transient Eater eater;

    /**
     * @param eater
     */
    public ImageAvatarResource(Eater eater) {
        super();
        this.eater = eater;
        setLastModifiedTime(Time.now());
    }

    @Override
    protected byte[] getImageData(Attributes attributes) {
        return eater.getAvatar();
    }
}