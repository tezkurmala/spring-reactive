package tez.reactive.spring.lernreactive.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Table
public class Item implements Persistable<String> {
    @Id
    @NonNull
    private String id;
    @NonNull
    private String description;
    @NonNull
    private Double price;

    @Transient
    private boolean newProduct;

    /**
     * Returns if the {@code Persistable} is new or was persisted already.
     *
     * @return if {@literal true} the object is new.
     */
    @Override
    public boolean isNew() {
        return this.newProduct || id == null;
    }

    public Item just() {
        this.newProduct = true;
        return this;
    }
}
