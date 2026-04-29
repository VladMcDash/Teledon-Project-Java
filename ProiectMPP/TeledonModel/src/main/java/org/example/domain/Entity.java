package org.example.domain;

import java.io.Serializable;

import java.util.Objects;

public class Entity<ID> implements Serializable {
    private static final long serialVersionUID = 1L;
    private ID id;

    public ID getId() { return id; }
    public void setId(ID id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity)) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}