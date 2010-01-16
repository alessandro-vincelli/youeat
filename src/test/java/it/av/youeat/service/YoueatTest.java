package it.av.youeat.service;

import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.EaterProfile;
import it.av.youeat.ocm.model.data.Country;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class YoueatTest {

    @Autowired
    private EaterProfileService userProfileService;
    @Autowired
    private CountryService countryService;
    private EaterProfile profile;
    private Country nocountry;

    public void setUp() throws YoueatException {
        profile = new EaterProfile();
        profile.setName("testProfile");
        profile = userProfileService.save(profile);
        nocountry = new Country("xx", "xxx", "test country");
        nocountry = countryService.save(nocountry);
        EaterProfile userProfile = new EaterProfile("USER");
        userProfile = userProfileService.save(userProfile);
        EaterProfile adminProfile = new EaterProfile("ADMIN");
        adminProfile = userProfileService.save(adminProfile);
    }

    public EaterProfile getProfile() {
        return profile;
    }

    public Country getNocountry() {
        return nocountry;
    }
}