package com.sena.senacamera.data.CustomException;


public class DataFormatException extends Exception{
    public DataFormatException() {
        super();
    }

    public DataFormatException(String tag,String message) {
        super(message);
    }
}
