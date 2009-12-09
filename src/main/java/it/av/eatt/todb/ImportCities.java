package it.av.eatt.todb;

import it.av.eatt.JackWicketException;
import it.av.eatt.ocm.model.data.City;
import it.av.eatt.ocm.model.data.Country;
import it.av.eatt.service.CityService;
import it.av.eatt.service.CountryService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ImportCities {

    private void runImportCountryRegion() throws JackWicketException, IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");
        CityService cityService = (CityService) context.getBean("cityService");
        CountryService countryService = (CountryService) context.getBean("countryService");
        List<Country> countries = countryService.getAll();
        HashMap<String, Country> mapCountry = new HashMap<String, Country>();
        for (Country country : countries) {
            mapCountry.put(country.getIso2(), country);
        }

        BufferedReader in = new BufferedReader(
                new FileReader("/Users/alessandro/Documents/eattogether/Data/cities.txt"));
        String str;
        ArrayList<City> cities = new ArrayList<City>();
        in.readLine();
        while ((str = in.readLine()) != null) {
            City cr = new City();
            String[] splittedLine = str.split(",");
            cr.setCountry(mapCountry.get(splittedLine[0].toUpperCase()));
            
            cr.setNameSimplified(splittedLine[1]);
            cr.setName(splittedLine[2]);
            cr.setRegion(splittedLine[3]);
            cr.setLatitude(splittedLine[4]);
            cr.setLongitude(splittedLine[5]);
            if(cr.getCountry() != null){
                cities.add(cr);
            }
        }
        in.close();
        int numberofcity = cities.size();
        for (City city : cities) {
            cityService.save(city);
            numberofcity--;
            System.out.println(numberofcity);
        }

    }

    public static void main(String[] arg0) throws JackWicketException, IOException {
        ImportCities importCities = new ImportCities();
        importCities.runImportCountryRegion();
    }
}
