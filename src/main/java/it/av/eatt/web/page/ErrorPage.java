package it.av.eatt.web.page;

import it.av.eatt.JackWicketException;

import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.http.WebResponse;

public class ErrorPage extends BasePage {

	/**
	 * Constructor.
	 */
	public ErrorPage(Throwable e) {
	    super();
        // Random delay to prevent attackers to get information from timing
	    //TODO import  common library
        //ThreadUtils.randomSleep(200, 1000);

		JackWicketException exception;
		if (e == null) {
            // Note the side effect: logging.
            exception = new JackWicketException("unknown");
		} else if (!(e instanceof JackWicketException)) {
			// If it is not a BBoxException, make it one with most general error code.
            // Note the side effect: logging.
			exception = new JackWicketException(e);
		} else {
			exception = (JackWicketException) e;
		}

		add(new Label("errorMessage", "internal error"));
		
		((WebResponse)RequestCycle.get().getResponse()).getHttpServletResponse().setStatus(400);

	}

	@Override
	public boolean isErrorPage() {
		return true;
	}

}
