package com.svj.utilities;

public class Constants {

    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int THREE = 3;
    public static final int FOUR = 4;
    public static final int FIVE = 5;
    public static final int TEN = 10;

    public enum PRODUCT{INTRADAY("INTRADAY"), DELIVERY("DELIVERY");
        private String value;
        PRODUCT(String value){
            this.value = value;
        }
    };
    public enum POSITION{LONG("LONG"), SHORT("SHORT");
        private String value;
        POSITION(String value){
            this.value= value;
        }
    };
}
