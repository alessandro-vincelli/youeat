package it.av.eatt.ocm.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.annotations.DocumentId;

@MappedSuperclass
@Node(isAbstract = true)
public class BasicEntity implements Serializable {

    @Id
    @Field
    @GenericGenerator(name = "generator", strategy = "uuid", parameters = {})
    @GeneratedValue(generator = "generator")
    @Column(updatable = false, length = 36)
    @DocumentId
    private String id;

    /**
     * @return the id
     */
    public String getId() {
        return id;
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
