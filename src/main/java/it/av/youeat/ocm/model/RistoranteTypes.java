package it.av.youeat.ocm.model;

import javax.persistence.Entity;

/**
 * @author Alessandro Vincelli
 */
@Entity
public class RistoranteTypes extends BasicEntity{

    private boolean ristorante;
    private boolean pizzeria;
    private boolean bar;

    public boolean isRistorante() {
        return ristorante;
    }

    public void setRistorante(boolean ristorante) {
        this.ristorante = ristorante;
    }

    public boolean isPizzeria() {
        return pizzeria;
    }

    public void setPizzeria(boolean pizzeria) {
        this.pizzeria = pizzeria;
    }

    public boolean isBar() {
        return bar;
    }

    public void setBar(boolean bar) {
        this.bar = bar;
    }

}
