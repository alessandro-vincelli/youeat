package it.av.youeat.web.components;

import it.av.youeat.ocm.model.Eater;
import it.av.youeat.web.commons.YoueatHttpParams;
import it.av.youeat.web.page.EaterViewPage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;

public final class OpenFriendPageButton extends AjaxFallbackLink<String> {
    private final Eater friend;

    /**
     * 
     * @param id
     * @param friend
     */
    public OpenFriendPageButton(String id, Eater friend) {
        super(id);
        this.friend = friend;
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        PageParameters pp = new PageParameters(YoueatHttpParams.USER_ID + "=" + friend.getId());
        setResponsePage(EaterViewPage.class, pp);
    }
}