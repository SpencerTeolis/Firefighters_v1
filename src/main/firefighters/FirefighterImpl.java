package main.firefighters;

import main.api.CityNode;
import main.api.Firefighter;

public class FirefighterImpl implements Firefighter {
  private CityNode location;
  private int distanceTraveled;

  public FirefighterImpl(CityNode location) {
    this.location = location;
    this.distanceTraveled = 0;
  }

  @Override
  public CityNode getLocation() {
    return location;
  }

  @Override
  public int distanceTraveled() { return distanceTraveled; }

  @Override
  public void updateLocation(CityNode location) {
    this.distanceTraveled += CityNode.absDistance(this.location, location);
    this.location = location;
  }

  @Override
  public String toString() {
    return "Firefighter{" + "xCoordinate=" + this.location.getX() + ", yCoordinate=" + this.location.getY() +
            ", distanceTraveled=" + this.distanceTraveled + '}';
  }
}
