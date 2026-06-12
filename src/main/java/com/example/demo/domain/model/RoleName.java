package com.example.demo.domain.model;

public enum RoleName {
    SUPERADMIN("superadmin", "ROLE_SUPERADMIN"),
    ADMIN("admin", "ROLE_ADMIN"),
    COMERCIAL("comercial", "ROLE_COMERCIAL"),
    CLIENTE("cliente", "ROLE_CLIENTE");

    private final String value;
    private final String authority;

    RoleName(String value, String authority) {
        this.value = value;
        this.authority = authority;
    }

    public String getValue() {
        return value;
    }

    public String getAuthority() {
        return authority;
    }

    public static RoleName fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (RoleName role : values()) {
            if (role.value.equalsIgnoreCase(value) || role.name().equalsIgnoreCase(value) || role.authority.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role value: " + value);
    }
}
