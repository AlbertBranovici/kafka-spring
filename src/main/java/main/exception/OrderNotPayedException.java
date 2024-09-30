package main.exception;

public class OrderNotPayedException extends RuntimeException{
    public OrderNotPayedException(String errorMessage) {super(errorMessage);}
}
