package it.av.youeat.ocm.model.geo;

import it.av.youeat.ocm.model.BasicEntity;
import it.av.youeat.ocm.model.Ristorante;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Index;

@Entity
public class RistorantePosition extends BasicEntity {

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
        this.latitude = location.latitude.value;
        this.longitude = location.longitude.value;
        location = location.toRadiant();
        this.cosLatitude = location.latitude.cos();
        this.sinLatitude = location.latitude.sin();
        this.cosLongitude = location.longitude.cos();
        this.sinLongitude = location.longitude.sin();
    }

    public Ristorante getRistorante() {
        return ristorante;
    }
}