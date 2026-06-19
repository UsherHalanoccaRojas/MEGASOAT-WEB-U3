package com.example.demo.domain.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class RoleNameConverter implements AttributeConverter<RoleName, String> {

    @Override
    public String convertToDatabaseColumn(RoleName attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public RoleName convertToEntityAttribute(String dbData) {
        return RoleName.fromValue(dbData);
    }
}
