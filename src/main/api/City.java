package main.api;

import main.api.exceptions.OutOfCityBoundsException;

public interface City {

  /**
   * Get the city's FireStation. The FireStation is fireproof
   *
   * @return {@link Building} representing the FireStation
   */
  Building getFireStation();

  /**
   * Get the city's FireDispatch.
   * @return the city's {@link FireDispatch}
   */
  FireDispatch getFireDispatch();

  /**
   * Get the X dimension of the city
   *
   * @return the X dimension of the city
   */
  int getXDimension();

  /**
   * Get the Y dimension of the city
   *
   * @return the Y dimension of the city
   */
  int getYDimension();

  /**
   * Get the building at the given coordinates
   *
   * @param xCoordinate
   * @param yCoordinate
   * @return the {@link Building} at these coordinates
   * @throws OutOfCityBoundsException if the coordinates are out of bounds for this city
   */
  Building getBuilding(int xCoordinate, int yCoordinate) throws OutOfCityBoundsException;

  /**
   * Get the building at the given location
   *
   * @param cityNode
   * @return the {@link Building} at this location
   * @throws OutOfCityBoundsException if the location is out of bounds for this city
   */
  Building getBuilding(CityNode cityNode) throws OutOfCityBoundsException;
}
