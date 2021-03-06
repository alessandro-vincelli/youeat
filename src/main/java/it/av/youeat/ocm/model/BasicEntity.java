package it.av.youeat.ocm.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Entity;
import org.hibernate.annotations.GenericGenerator;

@MappedSuperclass
@Entity
public class BasicEntity implements Serializable {

    @Id
    @GenericGenerator(name = "generator", strategy = "uuid", parameters = {})
    @GeneratedValue(generator = "generator")
    @Column(updatable = false, length = 36)
    // @DocumentId
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BasicEntity() {
    }

    @Override
    public boolean equals(Object arg0) {
        return arg0 != null && getClass() == arg0.getClass() && getId() != null && !getId().isEmpty()
                && getId().equals(((BasicEntity) arg0).getId());
    }

    @Override
    public int hashCode() {
        return getId() == null ? super.hashCode() : getId().hashCode();
    }
}
