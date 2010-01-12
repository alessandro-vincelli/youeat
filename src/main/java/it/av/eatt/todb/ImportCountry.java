package it.av.eatt.todb;

import it.av.eatt.YoueatException;
import it.av.eatt.ocm.model.data.Country;
import it.av.eatt.ocm.model.data.CountryRegion;
import it.av.eatt.service.CountryService;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ImportCountry {

    public void runImportCountryRegion() {
        try {
            BufferedReader in = new BufferedReader(new FileReader("/home/alessandro/Prod/Data/country.txt"));
            String str;
            ArrayList<CountryRegion> countries = new ArrayList<CountryRegion>();
            in.readLine();
            while ((str = in.readLine()) != null) {
                System.out.println(str);
                CountryRegion cr = new CountryRegion();
                String[] splittedLine = str.split("\t");
                cr.setIso2(splittedLine[0]);
                cr.setIso3(splittedLine[1]);
                cr.setName(splittedLine[2]);
                cr.setRegion(splittedLine[3]);
                // countryRegionService.save(cr);
            }
            in.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void runImportCountry() {
        ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");
        CountryService countryService = (CountryService) context.getBean("countryService");
        try {
            BufferedReader in = new BufferedReader(new FileReader(
                    "/Users/alessandro/Documents/eattogether/Data/country.txt"));
            String str;
            HashMap<String, Country> countries = new HashMap<String, Country>();

            in.readLine();
            while ((str = in.readLine()) != null) {
                Country cr = new Country();
                String[] splittedLine = str.split("\t");
                cr.setIso2(splittedLine[0]);
                cr.setIso3(splittedLine[1]);
                cr.setName(splittedLine[2]);
                countries.put(cr.getIso3(), cr);
            }
            in.close();

            for (Country c : countries.values()) {
                countryService.save(c);
            }
            System.out.println(countries.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (YoueatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] arg0) {
        ImportCountry importCountry = new ImportCountry();
        importCountry.runImportCountry();
    }

}
