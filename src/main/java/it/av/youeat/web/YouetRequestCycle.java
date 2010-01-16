package it.av.youeat.web;

import it.av.youeat.web.page.ErrorPage;

import java.lang.reflect.InvocationTargetException;

import org.apache.wicket.Page;
import org.apache.wicket.Response;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;

public class YouetRequestCycle extends WebRequestCycle {

    public YouetRequestCycle(WebApplication application, WebRequest request, Response response) {
        super(application, request, response);
    }

    @Override
    public Page onRuntimeException(Page page, RuntimeException e) {
        if (YoueatApplication.get().inDevelopment()) {
            // Let Wicket show the error.
            return null;
        }

        // exceptions are wrapped in WicketRuntimeExceptions and
        // InvocationTargetExceptions. The actual exception that occured is in
        // the cause
        Throwable cause = e;
        if (cause instanceof WicketRuntimeException) {
            cause = cause.getCause();
        }
        if (cause instanceof InvocationTargetException) {
            cause = cause.getCause();
        }

        return new ErrorPage(cause);
    }

}
