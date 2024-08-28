package main.java.com.illumio.flowlogparser;

public interface Parser<T> {
    T parse() throws Exception;
}