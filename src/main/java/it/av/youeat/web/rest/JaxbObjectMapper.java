package it.av.youeat.web.rest;

import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

/**
 * Set JAXB as a default serialization/deserialization method
 * 
 * @author Alessandro Vincelli
 */
public class JaxbObjectMapper extends ObjectMapper {

    /**
     * Constructor
     */
    public JaxbObjectMapper() {
        super();
        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
        // make deserializer use JAXB annotations (only)
        this.getDeserializationConfig().setAnnotationIntrospector(introspector);
        // make serializer use JAXB annotations (only)
        this.getSerializationConfig().setAnnotationIntrospector(introspector);
    }

}
