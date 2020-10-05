package main.impls;

import main.api.Building;
import main.api.City;
import main.api.CityNode;
import main.api.FireDispatch;
import main.api.exceptions.InvalidDimensionException;
import main.api.exceptions.OutOfCityBoundsException;
import main.firefighters.FireDispatchImpl;

public class CityImpl implements City {
  private final FireStation fireStation;
  private final Building[][] buildingGrid;
  private FireDispatch fireDispatch;

  public CityImpl(int xDimension, int yDimension, CityNode fireStationLocation) {
    validateCityDimensions(xDimension, yDimension);
    this.fireStation = new FireStation(fireStationLocation);
    this.buildingGrid = initBuildingGrid(xDimension, yDimension);
  }

  @Override
  public Building getFireStation() {
    return fireStation;
  }

  @Override
  public FireDispatch getFireDispatch() {
    if (fireDispatch == null) {
      fireDispatch = new FireDispatchImpl(this);
    }

    return fireDispatch;
  }

  @Override
  public int getXDimension() {
    return buildingGrid.length;
  }

  @Override
  public int getYDimension() {
    return buildingGrid[0].length;
  }

  @Override
  public Building getBuilding(int xCoordinate, int yCoordinate) throws OutOfCityBoundsException {
    validateCoordinate(xCoordinate, yCoordinate);
    return buildingGrid[xCoordinate][yCoordinate];
  }

  @Override
  public Building getBuilding(CityNode cityNode) throws OutOfCityBoundsException {
    return getBuilding(cityNode.getX(), cityNode.getY());
  }

  private Building[][] initBuildingGrid(int xDimension, int yDimension) {
    Building[][] initGrid = new Building[xDimension][yDimension];

    for (int x = 0; x < xDimension; x++) {
      for (int y = 0; y < yDimension; y++) {
        initGrid[x][y] = initBuilding(x, y);
      }
    }

    return initGrid;
  }

  private void validateCityDimensions(int xDimension, int yDimension) {
    if (xDimension < 2) {
      throw new InvalidDimensionException(xDimension);
    } else if (yDimension < 2) {
      throw new InvalidDimensionException(yDimension);
    }
  }

  private Building initBuilding(int x, int y) {
    if (x == fireStation.getLocation().getX() && y == fireStation.getLocation().getY()) {
      return fireStation;
    } else {
      return new BuildingImpl(new CityNode(x, y));
    }
  }

  private void validateCoordinate(int xCoordinate, int yCoordinate) {
    if (xCoordinate < 0 || yCoordinate < 0 || xCoordinate >= getXDimension() ||
        yCoordinate >= getYDimension()) {
      throw new OutOfCityBoundsException();
    }
  }
}
