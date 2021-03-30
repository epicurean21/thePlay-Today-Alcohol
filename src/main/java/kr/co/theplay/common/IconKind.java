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
    drink7("07", "Drinks/soju/5"),
    drink8("08", "Drinks/soju/6"),
    drink9("09", "Drinks/soju/7"),
    drink10("10", "Drinks/soju/8"),
    drink11("11", "Drinks/soju/B"),

    drink12("12", "Drinks/wine/main"),
    drink13("13", "Drinks/wine/0"),
    drink14("14", "Drinks/wine/1"),
    drink15("15", "Drinks/wine/2"),
    drink16("16", "Drinks/wine/3"),
    drink17("17", "Drinks/wine/4"),
    drink18("18", "Drinks/wine/5"),
    drink19("19", "Drinks/wine/6"),
    drink20("20", "Drinks/wine/7"),
    drink21("21", "Drinks/wine/8"),
    drink22("22", "Drinks/wine/B"),

    drink23("23", "Drinks/cock/main"),
    drink24("24", "Drinks/cock/0"),
    drink25("25", "Drinks/cock/1"),
    drink26("26", "Drinks/cock/2"),
    drink27("27", "Drinks/cock/3"),
    drink28("28", "Drinks/cock/4"),
    drink29("29", "Drinks/cock/5"),
    drink30("30", "Drinks/cock/6"),
    drink31("31", "Drinks/cock/7"),
    drink32("32", "Drinks/cock/8"),
    drink33("33", "Drinks/cock/B"),

    drink34("34", "Drinks/wine2/main"),
    drink35("35", "Drinks/wine2/0"),
    drink36("36", "Drinks/wine2/1"),
    drink37("37", "Drinks/wine2/2"),
    drink38("38", "Drinks/wine2/3"),
    drink39("39", "Drinks/wine2/4"),
    drink40("40", "Drinks/wine2/5"),
    drink41("41", "Drinks/wine2/6"),
    drink42("42", "Drinks/wine2/7"),
    drink43("43", "Drinks/wine2/8"),
    drink44("44", "Drinks/wine2/B"),

    drink45("45", "Drinks/vod/main"),
    drink46("46", "Drinks/vod/0"),
    drink47("47", "Drinks/vod/1"),
    drink48("48", "Drinks/vod/2"),
    drink49("49", "Drinks/vod/3"),
    drink50("50", "Drinks/vod/4"),
    drink51("51", "Drinks/vod/5"),
    drink52("52", "Drinks/vod/6"),
    drink53("53", "Drinks/vod/7"),
    drink54("54", "Drinks/vod/8"),
    drink55("55", "Drinks/vod/B"),

    drink56("56", "Drinks/sake/main"),
    drink57("57", "Drinks/sake/0"),
    drink58("58", "Drinks/sake/1"),
    drink59("59", "Drinks/sake/2"),
    drink60("60", "Drinks/sake/3"),
    drink61("61", "Drinks/sake/4"),
    drink62("62", "Drinks/sake/5"),
    drink63("63", "Drinks/sake/6"),
    drink64("64", "Drinks/sake/7"),
    drink65("65", "Drinks/sake/8"),
    drink66("66", "Drinks/sake/B"),

    drink67("67", "Drinks/shot/main"),
    drink68("68", "Drinks/shot/0"),
    drink69("69", "Drinks/shot/1"),
    drink70("70", "Drinks/shot/2"),
    drink71("71", "Drinks/shot/3"),
    drink72("72", "Drinks/shot/4"),
    drink73("73", "Drinks/shot/5"),
    drink74("74", "Drinks/shot/6"),
    drink75("75", "Drinks/shot/7"),
    drink76("76", "Drinks/shot/8"),
    drink77("77", "Drinks/shot/B"),

    drink78("78", "Drinks/can/main"),
    drink79("79", "Drinks/can/0"),
    drink80("80", "Drinks/can/1"),
    drink81("81", "Drinks/can/2"),
    drink82("82", "Drinks/can/3"),
    drink83("83", "Drinks/can/4"),
    drink84("84", "Drinks/can/5"),
    drink85("85", "Drinks/can/6"),
    drink86("86", "Drinks/can/7"),
    drink87("87", "Drinks/can/8"),
    drink88("88", "Drinks/can/B"),

    drink89("89", "Drinks/beer/main"),
    drink90("90", "Drinks/beer/0"),
    drink91("91", "Drinks/beer/1"),
    drink92("92", "Drinks/beer/2"),
    drink93("93", "Drinks/beer/3"),
    drink94("94", "Drinks/beer/4"),
    drink95("95", "Drinks/beer/5"),
    drink96("96", "Drinks/beer/6"),
    drink97("97", "Drinks/beer/7"),
    drink98("98", "Drinks/beer/8"),
    drink99("99", "Drinks/beer/B"),

    drink100("100", "Drinks/cherry"),
    drink101("101", "Drinks/cherry_M"),
    drink102("102", "Drinks/leaf"),
    drink103("103", "Drinks/leaf_M"),
    drink104("104", "Drinks/lemon"),
    drink105("105", "Drinks/lemon_M");


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
