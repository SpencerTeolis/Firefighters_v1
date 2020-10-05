package main.api.exceptions;

public class InvalidDimensionException extends RuntimeException {
  public InvalidDimensionException(int invalidDimension) {
    super("Invalid dimension for a city: " + invalidDimension);
  }
}
