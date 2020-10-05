package main.api.exceptions;

public class NoFireFoundException extends Exception {
  public NoFireFoundException() {
    super("This building cannot be extinguished because there is no fire!");
  }
}
