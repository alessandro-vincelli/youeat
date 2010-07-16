package it.av.youeat.web.rest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Simple response
 * 
 * TODO to be verified
 * 
 * @author Alessandro Vincelli
 * 
 */
@XmlRootElement
public class YouEatResponse {

    private String responseCode;

    /**
     * Constructor
     * 
     * @param responseCode
     */
    public YouEatResponse(String responseCode) {
        super();
        this.responseCode = responseCode;
    }

    /**
     * @return the responseCode
     */
    public String getResponseCode() {
        return responseCode;
    }

    /**
     * @param responseCode the responseCode to set
     */
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }
}