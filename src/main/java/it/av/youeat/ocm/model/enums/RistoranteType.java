package it.av.youeat.ocm.model.enums;

/**
 * @author Alessandro Vincelli
 *
 */
public enum RistoranteType {


    /**
	 *
	 */
	MUNICIPALITIES("municipalities", "MunicipalitiesCategory.1.i18n"),

	/**
	 *
	 */
	WATERBOARDS("waterboards", "WaterboardsCategory.1.i18n"),

    /**
     *
     */
	PROVINCES("provinces", "ProvincesCategory.1.i18n"),

    /**
     *
     */
	OTHER("other", "OtherCategory.1.i18n");

    /**
	 * @param dbValue - the given dbValue.
	 * @return - the SocialType with the given dbValue or null when it is unknown
	 */
	static RistoranteType getAuthorityCategoryByDbValue(String dbValue) {
		for (RistoranteType oc : RistoranteType.values()) {
			if (oc.dbValue.equals(dbValue)) {
				return oc;
			}
		}
		return null;
	}

	/**
	 * The value to store in the Database.
	 */
	private String dbValue;

	/**
	 * The i18n value, this value should be available in the language property.
	 */
	private String i18n;

	RistoranteType(String dbValue, String i18n) {
		this.dbValue = dbValue;
		this.i18n = i18n;
	}

	/**
	 * @return the value to store in DB.
	 */
	public String getDbValue() {
		return this.dbValue;
	}

	/**
	 * @return the value to retrieve the i18n value of the SocialType.
	 */
	public String getI18N() {
		return this.i18n;
	}

}
