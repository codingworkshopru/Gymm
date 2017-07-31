package ru.codingworkshop.gymm.util;

/**
 * Created by Радик on 31.07.2017 as part of the Gymm project.
 */
public final class SimpleWrapper {
    private Long number;
    private String string;
    private Boolean bool;

    public SimpleWrapper() {
    }

    public void setLong(Long myNumber) {
        number = myNumber;
    }

    public Long getLong() {
        return number;
    }

    public void setString(String value) {
        string = value;
    }

    public String getString() {
        return string;
    }

    public void setBool(Boolean bool) {
        this.bool = bool;
    }

    public Boolean getBool() {
        return bool;
    }
}
