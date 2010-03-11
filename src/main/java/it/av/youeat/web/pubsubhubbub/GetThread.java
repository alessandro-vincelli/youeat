package it.av.youeat.web.pubsubhubbub;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

/**
 * From Apache Http Client doc
 * 
 * @see http://hc.apache.org/httpcomponents-client/tutorial/html/connmgmt.html#d4e627
 *
 */
public final class GetThread extends Thread {
    
    private final HttpClient httpClient;
    private final HttpContext context;
    private final HttpPost httppost;
	private HttpResponse response;
   
	/**
	 * Constructor
	 * 
	 * @param httpClient
	 * @param httppost
	 */
    public GetThread(HttpClient httpClient, HttpPost httppost) {
        this.httpClient = httpClient;
        this.context = new BasicHttpContext();
        this.httppost = httppost;
    }
    
    @Override
    public void run() {
        try {
        	// Fixme don't expose the response globally!!! 
            response = this.httpClient.execute(this.httppost, this.context);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // do something useful with the entity
                // ...
                // ensure the connection gets released to the manager
            	InputStream stream = entity.getContent();
            	System.out.println(stream);
                entity.consumeContent();
            }
        } catch (Exception ex) {
            this.httppost.abort();
        }
    }

    /**
     * 
     * @return http response 
     */
	public HttpResponse getResponse() {
    	return response;
    }
}
