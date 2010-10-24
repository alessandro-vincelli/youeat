package it.av.youeat.web.util;

import it.av.youeat.service.TagService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.mcavallo.opencloud.Cloud;
import org.mcavallo.opencloud.Tag;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tag Cloud utility class
 * 
 * @author <a href='mailto:alessandro.vincelli@ictu.nl'>Alessandro Vincelli</a>
 * 
 */
public class TagCloud {

    // TODO move these values in a properties file
    /**
     * smallest font size in PT
     */
    private static int smallest = 5;
    /**
     * largest font size in PT
     */
    private static int largest = 22;

    @Autowired
    private TagService tagService;

    /**
     * Generate a TagCloud like <br>
     * <i><span style=\"font-size: 11.5pt;\">tag2</span><br>
     * <span style=\"font-size: 22.0pt;\">tag8</span>
     * 
     * @return a Html tagCloud
     */
    public String getTagClound() {
        List<Tag> generateTag = generateTag(tagService.getTagsAndScore());
        StringBuffer buffer = new StringBuffer();
        for (Tag tag : generateTag) {
            buffer.append("<span style=\"font-size: ");
            buffer.append(tag.getWeight());
            buffer.append("pt;\">");
            buffer.append(tag.getName());
            buffer.append("</span> ");
        }
        return buffer.toString();
    }

    /**
     * 
     * @param tags a Map containing a tag key and the relative count
     * @return a list of opencloud Tag
     */
    public List<org.mcavallo.opencloud.Tag> generateTag(Map<String, Integer> tags) {
        Cloud cloud = new Cloud();
        cloud.setMaxWeight(largest);
        cloud.setMinWeight(smallest);
        cloud.addTags(tag2TagCloud(tags));
        List<org.mcavallo.opencloud.Tag> allTags = cloud.tags();
        return allTags;
    }

    private Collection<org.mcavallo.opencloud.Tag> tag2TagCloud(Map<String, Integer> tags) {
        Collection<org.mcavallo.opencloud.Tag> tagsCloud = new ArrayList<org.mcavallo.opencloud.Tag>();
        for (String tag : tags.keySet()) {
            tagsCloud.add(new org.mcavallo.opencloud.Tag(tag, "", tags.get(tag)));
        }
        return tagsCloud;
    }

    /**
     * Only for Test
     * 
     * @param tagService
     */
    public void setTagService(TagService tagService) {
        this.tagService = tagService;
    }

}
