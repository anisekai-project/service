package me.anisekai.toshiko.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Setting {

    @Id
    private String key;

    @Column
    private String value;

    public Setting() {

    }

    public Setting(String key, String value) {

        this.key   = key;
        this.value = value;
    }

    public String getKey() {

        return this.key;
    }

    public String getValue() {

        return this.value;
    }

    public void setValue(String value) {

        this.value = value;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {return true;}
        if (o == null || this.getClass() != o.getClass()) {return false;}
        Setting setting = (Setting) o;
        return Objects.equals(this.getKey(), setting.getKey());
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getKey());
    }

}
