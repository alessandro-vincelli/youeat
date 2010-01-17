package it.av.youeat.service;

import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.EaterProfile;
import it.av.youeat.ocm.model.Language;
import it.av.youeat.ocm.model.data.Country;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class YoueatTest {

    @Autowired
    private EaterProfileService userProfileService;
    @Autowired
    private CountryService countryService;
    @Autowired
    private LanguageService languageService;
    private EaterProfile profile;
    private Country nocountry;

    public void setUp() throws YoueatException {
        profile = new EaterProfile();
        profile.setName("testProfile");
        profile = userProfileService.save(profile);
        nocountry = new Country("xx", "xxx", "test country");
        nocountry = countryService.save(nocountry);
        if (userProfileService.getByName("USER") == null) {
            EaterProfile userProfile = new EaterProfile("USER");
            userProfile = userProfileService.save(userProfile);
        }
        if (userProfileService.getByName("ADMIN") == null) {
            EaterProfile adminProfile = new EaterProfile("ADMIN");
            adminProfile = userProfileService.save(adminProfile);
        }
        if(languageService.getAll().size() == 0){
            languageService.save(new Language("it", "italy"));
            languageService.save(new Language("en", "usa"));
        }
    }

    public EaterProfile getProfile() {
        return profile;
    }

    public Country getNocountry() {
        return nocountry;
    }
}