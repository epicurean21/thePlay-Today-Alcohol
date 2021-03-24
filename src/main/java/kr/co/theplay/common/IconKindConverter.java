package kr.co.theplay.common;

import javax.persistence.AttributeConverter;

public class IconKindConverter implements AttributeConverter <IconKind, String> {

    @Override
    public String convertToDatabaseColumn(IconKind attribute) {
        if(attribute == null){
            return null;
        }
        return attribute.getCodeValue();
    }

    @Override
    public IconKind convertToEntityAttribute(String dbData) {
        if(dbData == null){
            return null;
        }
        return IconKind.enumOf(dbData);
    }
}
