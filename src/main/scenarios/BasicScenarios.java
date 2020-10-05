package main.scenarios;

import main.api.*;
import main.api.exceptions.FireproofBuildingException;
import main.firefighters.FirefighterImpl;
import main.impls.CityImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class BasicScenarios {
  @Test
  public void firefighterDistance() {
    Firefighter firefighter = new FirefighterImpl(new CityNode(0, 0));
    firefighter.updateLocation(new CityNode(2, 3));
    Assert.assertEquals(5, firefighter.distanceTraveled());
    firefighter.updateLocation(new CityNode(0, 5));
    Assert.assertEquals(9, firefighter.distanceTraveled());
  }
  @Test
  public void singleFire() throws FireproofBuildingException {
    City basicCity = new CityImpl(5, 5, new CityNode(0, 0));
    FireDispatch fireDispatch = basicCity.getFireDispatch();

    CityNode fireNode = new CityNode(0, 1);
    Pyromaniac.setFire(basicCity, fireNode);

    fireDispatch.setFirefighters(1);
    fireDispatch.dispatchFirefighters(fireNode);
    Assert.assertFalse(basicCity.getBuilding(fireNode).isBurning());
  }

  @Test
  public void singleFireDistanceTraveledDiagonal() throws FireproofBuildingException {
    City basicCity = new CityImpl(2, 2, new CityNode(0, 0));
    FireDispatch fireDispatch = basicCity.getFireDispatch();

    // Set fire on opposite corner from Fire Station
    CityNode fireNode = new CityNode(1, 1);
    Pyromaniac.setFire(basicCity, fireNode);

    fireDispatch.setFirefighters(1);
    fireDispatch.dispatchFirefighters(fireNode);

    Firefighter firefighter = fireDispatch.getFirefighters().get(0);
    Assert.assertEquals(2, firefighter.distanceTraveled());
    Assert.assertEquals(fireNode, firefighter.getLocation());
  }

  @Test
  public void singleFireDistanceTraveledAdjacent() throws FireproofBuildingException {
    City basicCity = new CityImpl(2, 2, new CityNode(0, 0));
    FireDispatch fireDispatch = basicCity.getFireDispatch();

    // Set fire on adjacent X position from Fire Station
    CityNode fireNode = new CityNode(1, 0);
    Pyromaniac.setFire(basicCity, fireNode);

    fireDispatch.setFirefighters(1);
    fireDispatch.dispatchFirefighters(fireNode);

    Firefighter firefighter = fireDispatch.getFirefighters().get(0);
    Assert.assertEquals(1, firefighter.distanceTraveled());
    Assert.assertEquals(fireNode, firefighter.getLocation());
  }

  @Test
  public void simpleDoubleFire() throws FireproofBuildingException {
    City basicCity = new CityImpl(2, 2, new CityNode(0, 0));
    FireDispatch fireDispatch = basicCity.getFireDispatch();


    CityNode[] fireNodes = {
        new CityNode(0, 1),
        new CityNode(1, 1)};
    Pyromaniac.setFires(basicCity, fireNodes);

    fireDispatch.setFirefighters(1);
    fireDispatch.dispatchFirefighters(fireNodes);

    Firefighter firefighter = fireDispatch.getFirefighters().get(0);
    Assert.assertEquals(2, firefighter.distanceTraveled());
    Assert.assertEquals(fireNodes[1], firefighter.getLocation());
    Assert.assertFalse(basicCity.getBuilding(fireNodes[0]).isBurning());
    Assert.assertFalse(basicCity.getBuilding(fireNodes[1]).isBurning());
  }

  @Test
  public void doubleFirefighterDoubleFire() throws FireproofBuildingException {
    City basicCity = new CityImpl(2, 2, new CityNode(0, 0));
    FireDispatch fireDispatch = basicCity.getFireDispatch();


    CityNode[] fireNodes = {
        new CityNode(0, 1),
        new CityNode(1, 0)};
    Pyromaniac.setFires(basicCity, fireNodes);

    fireDispatch.setFirefighters(2);
    fireDispatch.dispatchFirefighters(fireNodes);

    List<Firefighter> firefighters = fireDispatch.getFirefighters();
    int totalDistanceTraveled = 0;
    boolean firefighterPresentAtFireOne = false;
    boolean firefighterPresentAtFireTwo = false;
    for (Firefighter firefighter : firefighters) {
      totalDistanceTraveled += firefighter.distanceTraveled();

      if (firefighter.getLocation().equals(fireNodes[0])) {
        firefighterPresentAtFireOne = true;
      }
      if (firefighter.getLocation().equals(fireNodes[1])) {
        firefighterPresentAtFireTwo = true;
      }
    }

    Assert.assertEquals(2, totalDistanceTraveled);
    Assert.assertTrue(firefighterPresentAtFireOne);
    Assert.assertTrue(firefighterPresentAtFireTwo);
    Assert.assertFalse(basicCity.getBuilding(fireNodes[0]).isBurning());
    Assert.assertFalse(basicCity.getBuilding(fireNodes[1]).isBurning());
  }

  @Test
  public void TSP() {
    CityNode fireStation = new CityNode(2, 2);
    City basicCity = new CityImpl(5, 5, fireStation);
    CityNode[] fireNodes = {
            new CityNode(3, 1),
            new CityNode(2, 4),
            new CityNode(5, 2),
            new CityNode(0, 0)};
    FireDispatch fireDispatch = basicCity.getFireDispatch();
    List<CityNode> path = fireDispatch.TSPBruteForce(fireNodes);
    System.out.println(fireDispatch.calcPathCost(path));
  }

  @Test
  public void fireDispatch() throws FireproofBuildingException {
    CityNode fireStation = new CityNode(1, 2);
    City basicCity = new CityImpl(6, 7, fireStation);
    FireDispatch fireDispatch = basicCity.getFireDispatch();

    CityNode[] fireNodes = {
            new CityNode(0, 0),
            new CityNode(2, 1),
            new CityNode(4, 2),
            new CityNode(0, 4),
            new CityNode(1, 6),
            new CityNode(4, 5)};
    Pyromaniac.setFires(basicCity, fireNodes);
    fireDispatch.setFirefighters(3);
    fireDispatch.dispatchFirefighters(fireNodes);

    List<Firefighter> firefighters = fireDispatch.getFirefighters();
    int totalDistanceTraveled = 0;
    for (Firefighter firefighter : firefighters) {
      totalDistanceTraveled += firefighter.distanceTraveled();
    }
    for (CityNode fireNode : fireNodes){
      Assert.assertFalse(basicCity.getBuilding(fireNode).isBurning());
    }
    System.out.println(totalDistanceTraveled);
  }
}

