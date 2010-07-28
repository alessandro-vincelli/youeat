package it.av.youeat.web;

import com.ttdev.wicketpagetest.MockableSpringBeanInjector;

//going to be removed?
@Deprecated
public class YouetApplicationMock extends YoueatApplication {

    @Override
    protected void init() {
        super.init();

        MockableSpringBeanInjector.installInjector(this);
    }

}
