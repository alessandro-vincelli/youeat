/**
 * Copyright 2009 the original author or authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.av.youeat.web.page;

import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.data.City;
import it.av.youeat.ocm.model.data.Country;
import it.av.youeat.service.CityService;
import it.av.youeat.service.CountryService;
import it.av.youeat.service.RistoranteService;
import it.av.youeat.web.components.ImageRisto;
import it.av.youeat.web.util.RistoranteUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Check friends list, Confirm and remove friends, send a message to a friend
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class IndexRistoPage extends BasePage {

    @SpringBean
    private RistoranteService ristoranteService;
    @SpringBean
    private CountryService countryService;
    @SpringBean
    private CityService cityService;

    private Country countrySelected = null;
    private City citySelected = null;

    public IndexRistoPage(PageParameters pageParameters) {
        super();
        if (pageParameters.containsKey(YoueatHttpParams.COUNTRY_ID)) {
            countrySelected = countryService.getByID(pageParameters.getString(YoueatHttpParams.COUNTRY_ID));
        }
        if (pageParameters.containsKey(YoueatHttpParams.CITY_ID)) {
            citySelected = cityService.getByID(pageParameters.getString(YoueatHttpParams.CITY_ID));
            countrySelected = citySelected.getCountry();
        }

        add(new Label("instructions", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                if (countrySelected == null) {
                    return getString("indexPage.chooseCountry");
                } else if (citySelected == null) {
                    return getString("indexPage.chooseCity");
                } else {
                    return "";
                }
            }
        }).setOutputMarkupId(true));

        add(getFeedbackPanel());
        WebMarkupContainer countriesContainer = new WebMarkupContainer("countryListContainer");
        countriesContainer.setOutputMarkupId(true);
        add(countriesContainer);

        PropertyListView<Country> countryListView = new PropertyListView<Country>("countryList", new CountriesModel()) {

            @Override
            protected void populateItem(ListItem<Country> item) {

                BookmarkablePageLink<Country> link = new BookmarkablePageLink<Country>("country", IndexRistoPage.class,
                        new PageParameters(YoueatHttpParams.COUNTRY_ID + "=" + item.getModelObject().getId()));

                link.add(new Label("countryTitle", item.getModelObject().getName()));
                if (item.getModelObject().equals(countrySelected)) {
                    link.add(new SimpleAttributeModifier("class", "cityOrCountrySelected"));
                }
                item.add(link);
            }
        };

        countriesContainer.add(countryListView);

        WebMarkupContainer cityContainer = new WebMarkupContainer("cityListContainer");
        cityContainer.setOutputMarkupId(true);
        add(cityContainer);

        PropertyListView<City> cityListView = new PropertyListView<City>("cityList", new CitiesModel()) {

            @Override
            protected void populateItem(ListItem<City> item) {
                // IF not logged user, use params, better for search engigne
                BookmarkablePageLink link = new BookmarkablePageLink<Country>("city", IndexRistoPage.class,
                        new PageParameters(YoueatHttpParams.CITY_ID + "=" + item.getModelObject().getId()));

                if (item.getModelObject().equals(citySelected)) {
                    link.add(new SimpleAttributeModifier("class", "cityOrCountrySelected"));
                }
                link.add(new Label("cityTitle", item.getModelObject().getName()));
                item.add(link);
            }
        };

        cityContainer.add(cityListView);

        WebMarkupContainer ristoContainer = new WebMarkupContainer("ristoListContainer");
        ristoContainer.setOutputMarkupId(true);
        add(ristoContainer);

        PropertyListView<Ristorante> ristoListView = new PropertyListView<Ristorante>("ristoList", new RistoModel()) {

            @Override
            protected void populateItem(ListItem<Ristorante> item) {
                BookmarkablePageLink link = new BookmarkablePageLink("risto", RistoranteViewPage.class, RistoranteUtil
                        .createParamsForRisto(item.getModelObject()));
                link.add(new Label("ristoTitle", item.getModelObject().getName()));
                item.add(link);
                item.add(new Label("ristoAddress", item.getModelObject().getAddress()));
                item.add(new Label("ristoPhoneNumber", item.getModelObject().getPhoneNumber() != null ? item
                        .getModelObject().getPhoneNumber() : ""));
                item.add(new Label("ristoMobilePhoneNumber",
                        item.getModelObject().getMobilePhoneNumber() != null ? item.getModelObject()
                                .getMobilePhoneNumber() : ""));
                item.add(new SmartLinkLabel("ristoWWW", item.getModelObject().getWww() != null ? item.getModelObject()
                        .getWww() : ""));
                item.add(new Label("ristoCity", item.getModelObject().getCity().getName()));
                item.add(ImageRisto.getRandomThumbnailImage("picture", item.getModelObject(), this.getPage(), true));
            }
        };

        ristoContainer.add(ristoListView);
    }

    private class CountriesModel extends LoadableDetachableModel<List<Country>> {
        public CountriesModel() {
            super();
        }

        @Override
        protected List<Country> load() {
            return ristoranteService.getCountryWithRisto();
        }
    }

    private class CitiesModel extends LoadableDetachableModel<List<City>> {
        public CitiesModel() {
            super();
        }

        @Override
        protected List<City> load() {
            if (countrySelected != null) {
                return ristoranteService.getCityWithRistoByCountry(countrySelected);
            } else{
                return new ArrayList<City>(0);
            }
        }
    }

    private class RistoModel extends LoadableDetachableModel<List<Ristorante>> {
        public RistoModel() {
            super();
        }

        @Override
        protected List<Ristorante> load() {
            if (citySelected != null) {
                return ristoranteService.getByCity(citySelected, 0, 0);
            } else{
                return new ArrayList<Ristorante>(0);
            }
        }
    }
}
