package it.av.youeat.ocm.model.geo;

import it.av.youeat.ocm.model.BasicEntity;
import it.av.youeat.ocm.model.Ristorante;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Index;

@Entity
public class RistorantePosition extends BasicEntity {

    public static final String SIN_LATITUDE = "sinLatitude";
    public static final String COS_LATITUDE = "cosLatitude";
    public static final String COS_LONGITUDE = "cosLongitude";
    public static final String SIN_LONGITUDE = "sinLongitude";
    public static final String RISTORANTE_FIELD = "ristorante";

    @OneToOne(optional = false)
    @Index(name = "idx_ristorante_position_ristorante")
    // TODO add a unique constraint
    private Ristorante ristorante;
    private Double latitude;
    @Column(nullable = false)
    private Double cosLatitude;
    @Column(nullable = false)
    private Double sinLatitude;
    @Column(nullable = false)
    private Double cosLongitude;
    @Column(nullable = false)
    private Double sinLongitude;
    @Column(nullable = false)
    private Double longitude;

    public RistorantePosition() {
    }

    public RistorantePosition(Ristorante ristorante, Location where) {
        this.ristorante = ristorante;
        this.setWhere(where);
    }

    public final Location getWhere() {
        return new Location(this.latitude, this.longitude);
    }

    public final void setWhere(Location location) {
        this.latitude = location.getLatitude().value;
        this.longitude = location.getLongitude().value;
        location = location.toRadiant();
        this.cosLatitude = location.getLatitude().cos();
        this.sinLatitude = location.getLatitude().sin();
        this.cosLongitude = location.getLongitude().cos();
        this.sinLongitude = location.getLongitude().sin();
    }

    public Ristorante getRistorante() {
        return ristorante;
    }
}