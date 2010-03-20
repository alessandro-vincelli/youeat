package it.av.youeat.web;

import com.ttdev.wicketpagetest.MockableSpringBeanInjector;

public class YouetApplicationMock extends YoueatApplication {

    @Override
    protected void init() {
        super.init();

        MockableSpringBeanInjector.installInjector(this);
    }

}
