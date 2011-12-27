package it.av.youeat.web.page;

import it.av.youeat.YoueatException;

import org.apache.wicket.markup.html.basic.Label;
import org.springframework.util.Assert;

public class ErrorPage extends BasePage {

	/**
	 * Constructor.
	 */
	public ErrorPage(Throwable e) {
	    super();
        // Random delay to prevent attackers to get information from timing
	    //TODO import  common library
        //ThreadUtils.randomSleep(200, 1000);

		YoueatException exception;
		if (e == null) {
            // Note the side effect: logging.
            exception = new YoueatException("unknown");
		} else if (!(e instanceof YoueatException)) {
			// If it is not a BBoxException, make it one with most general error code.
            // Note the side effect: logging.
			exception = new YoueatException(e);
		} else {
			exception = (YoueatException) e;
		}
		Assert.notNull(exception);
		add(new Label("errorMessage", "internal error"));
		
		//TODO 1.5
		//((WebResponse)RequestCycle.get().getResponse()).getHttpServletResponse().setStatus(400);

	}

	@Override
	public boolean isErrorPage() {
		return true;
	}

}
