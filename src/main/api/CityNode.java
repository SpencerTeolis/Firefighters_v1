package main.api;

import java.util.Objects;

/**
 * Represents a location in the city
 */
public class CityNode {
  private final int xCoordinate;
  private final int yCoordinate;

  /**
   * Build a {@link CityNode} given coordinates
   *
   * @param xCoordinate
   * @param yCoordinate
   */
  public CityNode(int xCoordinate, int yCoordinate) {
    this.xCoordinate = xCoordinate;
    this.yCoordinate = yCoordinate;
  }

  /**
   * Get the X coordinate of this node
   *
   * @return the X coordinate of this node
   */
  public int getX() {
    return xCoordinate;
  }

  /**
   * Get the Y coordinate of this node
   *
   * @return the Y coordinate of this node
   */
  public int getY() {
    return yCoordinate;
  }

  public static int absDistance(CityNode c1, CityNode c2){
    return Math.abs(c1.xCoordinate-c2.xCoordinate) + Math.abs(c1.yCoordinate-c2.yCoordinate);
  }

  @Override
  public String toString() {
    return "CityNode{" + "xCoordinate=" + xCoordinate + ", yCoordinate=" + yCoordinate + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    CityNode cityNode = (CityNode)o;
    return xCoordinate == cityNode.xCoordinate && yCoordinate == cityNode.yCoordinate;
  }

  @Override
  public int hashCode() {
    return Objects.hash(xCoordinate, yCoordinate);
  }
}
