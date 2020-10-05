package main.api;

import java.util.List;

import main.api.exceptions.FireproofBuildingException;

/**
 * Utility class to set fires in a city
 */
public class Pyromaniac {

  /**
   * Sets a number of fires {@param numFires} at {@param victimLocations} in the given {@param victimCity}
   *
   * @param victimCity City to be set on fire
   * @param victimLocations Locations to be set on fire
   * @throws FireproofBuildingException if one of the buildings in question is fireproof
   */
  public static void setFires(City victimCity, CityNode[] victimLocations)
      throws FireproofBuildingException {
    for (CityNode location : victimLocations) {
      setFire(victimCity, location);
    }
  }

  /**
   * Sets a fire at the {@param location} in the given {@param victimCity}
   * @param victimCity City to be set on fire
   * @param location Location to be set on fire
   * @throws FireproofBuildingException if the building in question is fireproof
   */
  public static void setFire(City victimCity, CityNode location) throws FireproofBuildingException {
    victimCity.getBuilding(location).setFire();
  }
}
