package it.av.youeat.web.rest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Simple boolean response
 * 
 * @author Alessandro Vincelli
 * 
 */
@XmlRootElement
public class YouEatBooleanResponse {

    private boolean response;

    /**
     * Constructor
     * 
     * @param response
     */
    public YouEatBooleanResponse(boolean response) {
        super();
        this.response = response;
    }

    /**
     * @return the response
     */
    public boolean isResponse() {
        return response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(boolean response) {
        this.response = response;
    }

}
