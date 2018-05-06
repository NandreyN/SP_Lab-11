package model;

import com.sun.javaws.exceptions.InvalidArgumentException;

public class Email {
    private enum ValidateTypes {
        BODY, ENTIRE
    }

    private String address;
    private String domain;

    public Email(String address, String domain) {
        if (!validate(address, ValidateTypes.BODY) || !validate(domain, ValidateTypes.BODY))
            throw new IllegalArgumentException("");
        this.address = address;
        this.domain = domain;
    }

    public Email(String address) {
        if (!validate(address, ValidateTypes.ENTIRE))
            throw new IllegalArgumentException("address");
        this.address = address.split("@")[0];
        this.domain = address.split("@")[1];
    }

    private boolean validate(String candidate, ValidateTypes type) {
        switch (type) {
            case BODY:
                return validateBody(candidate);
            case ENTIRE:
                return validateEntire(candidate);
        }
        return true;
    }

    private boolean validateEntire(String candidate) {
        return true;
    }

    private boolean validateBody(String candidate) {
        return true;
    }

    @Override
    public String toString() {
        return address + "@" + domain;
    }
}
