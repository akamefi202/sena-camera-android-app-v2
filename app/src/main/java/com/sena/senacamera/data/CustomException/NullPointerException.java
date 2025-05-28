package com.sena.senacamera.data.CustomException;


public class NullPointerException extends Exception{
    private String exceptionType = "NullPointerException!";
    public NullPointerException() {
        super();
    }

    public NullPointerException(String tag,String describleInfo,String detailInfo) {
        super(describleInfo);
    }
}
