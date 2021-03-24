package kr.co.theplay.common;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum IconKind {

    drink1("01", "Drinks/soju/main"),
    drink2("02", "Drinks/soju/0"),
    drink3("03", "Drinks/soju/1"),
    drink4("04", "Drinks/soju/2"),
    drink5("05", "Drinks/soju/3"),
    drink6("06", "Drinks/soju/4"),
    drink7("07", "Drinks/soju/7"),
    drink8("08", "Drinks/soju/8");

    private String codeValue;
    private String nameValue;

    IconKind(String codeValue, String nameValue){
        this.codeValue = codeValue;
        this.nameValue = nameValue;
    }

    public static IconKind enumOf(String codeValue){
        return Arrays.stream(IconKind.values())
                .filter(t -> t.getCodeValue().equals(codeValue))
                .findAny().orElse(null);
    }
}
