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
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Shows risto per city
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
    private String cityFirstLetterSelected = null;
    private int pageSelected = 0;
    
    private int citiesPerPage = 50;

    public IndexRistoPage(PageParameters pageParameters) {
        super();
        appendToPageTile(" Index :: ");
        if (pageParameters.getNamedKeys().contains(YoueatHttpParams.COUNTRY_ID)) {
            countrySelected = countryService.getByID(pageParameters.get(YoueatHttpParams.COUNTRY_ID).toString());
            appendToPageTile(" " + countrySelected.getName());
        }
        if (pageParameters.getNamedKeys().contains(YoueatHttpParams.CITY_FL) && pageParameters.getNamedKeys().contains(YoueatHttpParams.COUNTRY_ID)) {
            cityFirstLetterSelected = pageParameters.get(YoueatHttpParams.CITY_FL).toString("");
            countrySelected = countryService.getByID(pageParameters.get(YoueatHttpParams.COUNTRY_ID).toString());
            appendToPageTile(" " + countrySelected.getName());
        }
        if (pageParameters.getNamedKeys().contains(YoueatHttpParams.CITY_ID) && StringUtils.isNotBlank(pageParameters.get(YoueatHttpParams.CITY_ID).toString())) {
            citySelected = cityService.getByID(pageParameters.get(YoueatHttpParams.CITY_ID).toString());
            countrySelected = citySelected.getCountry();
            appendToPageTile(" " + countrySelected.getName() + " -> " + citySelected.getName());
        }
        if (pageParameters.getNamedKeys().contains(YoueatHttpParams.PAGE)) {
            pageSelected = pageParameters.get(YoueatHttpParams.PAGE).toInt();
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
        
        PropertyListView<String> cityListAZView = new PropertyListView<String>("cityListAZ", new CitiesAZModel()) {

            @Override
            protected void populateItem(ListItem<String> item) {
                // IF not logged user, use params, better for search engigne
                BookmarkablePageLink<Country> link = new BookmarkablePageLink<Country>("city", IndexRistoPage.class,
                        new PageParameters(YoueatHttpParams.CITY_FL + "=" + item.getModelObject() + "," + YoueatHttpParams.COUNTRY_ID + "=" + ((countrySelected != null)?countrySelected.getId():"")));

                if (item.getModelObject().equals(cityFirstLetterSelected)) {
                    link.add(new SimpleAttributeModifier("class", "cityOrCountrySelected"));
                }
                link.add(new Label("cityFirstLetter", item.getModelObject()));
                item.add(link);
            }
        };

        cityContainer.add(cityListAZView);
        
        
        PageableListView<City> listview = new PageableListView<City>("cityList", new CitiesModel(), citiesPerPage)
        {
            @Override
            protected void populateItem(ListItem<City> item)
            {
                // IF not logged user, use params, better for search engigne
                BookmarkablePageLink<Country> link = new BookmarkablePageLink<Country>("city", IndexRistoPage.class,
                        new PageParameters(YoueatHttpParams.CITY_ID + "=" + item.getModelObject().getId() + ","+ YoueatHttpParams.CITY_FL + "=" + cityFirstLetterSelected));

                if (item.getModelObject().equals(citySelected)) {
                    link.add(new SimpleAttributeModifier("class", "cityOrCountrySelected"));
                }
                link.add(new Label("cityTitle", item.getModelObject().getName()));
                item.add(link);
            }
        };
        cityContainer.add(listview);
        cityContainer.add(new PagingNavigator("navigator", listview){

            @Override
            protected AbstractLink newPagingNavigationIncrementLink(String id, IPageable pageable, int increment) {
                // TODO Auto-generated method stub
                return super.newPagingNavigationIncrementLink(id, pageable, increment);
            }

            @Override
            protected AbstractLink newPagingNavigationLink(String id, IPageable pageable, int pageNumber) {
                super.newPagingNavigationLink(id, pageable, pageNumber);
                return new BookmarkablePageLink<Country>(id, IndexRistoPage.class,
                        new PageParameters(YoueatHttpParams.CITY_FL + "=" + cityFirstLetterSelected + "," + YoueatHttpParams.COUNTRY_ID + "=" + ((countrySelected != null)?countrySelected.getId():"")));

            }
            
        });
        cityContainer.setVersioned(false);

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
            
            if (countrySelected != null && StringUtils.isBlank(cityFirstLetterSelected)) {
                //return ristoranteService.getCityWithRistoByCountry(countrySelected);
                return ristoranteService.getCityWithRistoByCountry(countrySelected, 0, 999999);
            }
            else if (countrySelected != null && StringUtils.isNotBlank(cityFirstLetterSelected)) {
                //return ristoranteService.getCityWithRistoByCountry(countrySelected);
                return ristoranteService.getCityWithRistoByCountryAZ(countrySelected, cityFirstLetterSelected, 0, 99999999);
                //return ristoranteService.getCityWithRistoByCountryAZ(countrySelected, cityFirstLetterSelected, pageSelected * citiesPerPage, citiesPerPage);
            }
            else{
                return new ArrayList<City>(0);
            }
        }
    }
    
    private class CitiesAZModel extends LoadableDetachableModel<List<String>> {
        public CitiesAZModel() {
            super();
        }

        @Override
        protected List<String> load() {
            if (countrySelected != null) {
                List<City> cityWithRistoByCountry = ristoranteService.getCityWithRistoByCountry(countrySelected);
                SortedSet<String> az = new TreeSet<String>(new AZComparator());

                for (City city : cityWithRistoByCountry) {
                    az.add(StringUtils.substring(city.getName(), 0, 1).toUpperCase());
                }
                ArrayList<String> azL = new ArrayList<String>();
                for (String string : az) {
                    azL.add(string);
                }                
                return azL;
            } else{
                return new ArrayList<String>();
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
    
    private class AZComparator implements Comparator<String>{

        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
        
    }
}
