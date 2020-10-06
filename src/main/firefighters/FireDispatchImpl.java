package main.firefighters;

import main.api.*;
import main.api.exceptions.NoFireFoundException;

import java.util.ArrayList;
import java.util.List;

public class FireDispatchImpl implements FireDispatch {
  private City city;
  private List<Firefighter> firefighters;
  private int[][] distMatrix;

  public FireDispatchImpl(City city) {
    this.city = city;
    this.firefighters = new ArrayList<>();
  }

  /**
   * Generates all permutations of a list of integers in n!
   * Works by iteratively growing the list of permutations
   * Given a permutation with n digits there are n+1 places to put another digit i.e. _1_2_3_ (3 digits 3+1 dashes)
   *
   * @param nums list of integers, representing indices of buildings, to permute
   * @return a list of all permutations
   * @throws IllegalArgumentException
   */
  private List<List<Integer>> permute(int[] nums) throws IllegalArgumentException {
    if (nums.length > 10)
      throw new IllegalArgumentException("Should not list permutations with more than 10 elements");

    List<List<Integer>> permutations = new ArrayList<>();

    // l0 is base list which consists of the first integer
    List<Integer> l0 = new ArrayList<>();
    l0.add(nums[0]);
    permutations.add(l0);

    // iterate through each number adding it to each position in each permutation generated in previous step
    for (int i = 1; i < nums.length; i++) {
      List<List<Integer>> newAns = new ArrayList<>();
      for (int j = 0; j <= i; j++){
        for (List<Integer> perm : permutations) {
          List<Integer> newPerm = new ArrayList<>(perm);
          newPerm.add(j, nums[i]);
          newAns.add(newPerm);
        }
      }
      permutations = newAns;
    }
    return permutations;
  }

  /**
   * Updates distance matrix row for a given firefighter index
   *
   * @param firefighterIdx index of the firefighter corresponding to the row of distMatrix to be updated
   * @param buildings array of buildings to calculate distances to
   */
  private void updateDistances(int firefighterIdx, CityNode[] buildings) {
    CityNode firefighterLoc = this.firefighters.get(firefighterIdx).getLocation();

    for (int i = 0; i < buildings.length; i++) {
      CityNode building = buildings[i];
      if (this.city.getBuilding(building).isBurning())
        distMatrix[firefighterIdx][i] = CityNode.absDistance(firefighterLoc, building);
      else
        distMatrix[firefighterIdx][i] = Integer.MAX_VALUE;
    }
  }

  /**
   * Converts distMatrix to a list of possible moves
   *
   * @return List of {@link Move}
   */
  private List<Move> getPossibleMoves() {
    List<Move> moves = new ArrayList<>();

    for (int i = 0; i < distMatrix.length; i++) {
      for (int j = 0; j < distMatrix[0].length; j++) {
        if (distMatrix[i][j] < Integer.MAX_VALUE) {
          Move move = new Move(i, j);
          moves.add(move);
        }
      }
    }
    return moves;
  }

  /**
   * General function for returning moves that minimize an associated value
   *
   * @param moves List of moves
   * @param values List of associated values with moves
   * @return a List of {@link Move} that have the min value
   */
  private List<Move> getMins(List<Move> moves, List<Integer> values) {
    int minVal = Integer.MAX_VALUE;
    List<Move> mins = new ArrayList<>();

    for (int i = 0; i < values.size(); i++) {
      int v = values.get(i);

      if (v < minVal) {
        minVal = v;
        mins.clear();
        mins.add(moves.get(i));
      } else if (v == minVal) {
        mins.add(moves.get(i));
      }
    }

    return mins;
  }

  /**
   * For a list of Moves finds the associated distance
   *
   * @param moves
   * @return List of distances
   */
  private List<Integer> moveToDist(List<Move> moves) {
    List<Integer> distances = new ArrayList<>();
    for (Move move : moves){
      distances.add(distMatrix[move.fireFighterIdx][move.buildingIdx]);
    }
    return distances;
  }

  /**
   * For a list of Moves finds the associated column sum in distMatrix which corresponds to the
   * distance of all firefighters from a node
   *
   * @param moves
   * @return
   */
  private List<Integer> moveToColumnSum(List<Move> moves) {
    List<Integer> columnSum = new ArrayList<>();
    for (Move move : moves) {
      int sum = 0;
      for (int[] matrix : distMatrix) {
        sum += matrix[move.buildingIdx];
      }
      columnSum.add(sum * -1);
    }
    return columnSum;
  }

  /**
   * For a list of Moves gives the distance traveled by that firefighter so far
   *
   * @param moves
   * @return
   */
  private List<Integer> moveToDistTraveled(List<Move> moves) {
    List<Integer> distTraveled = new ArrayList<>();
    for (Move move : moves) {
      distTraveled.add(firefighters.get(move.fireFighterIdx).distanceTraveled());
    }
    return distTraveled;
  }

  /**
   * Alternate calcPathCost that takes in indexes to buildings and a list of buildings
   *
   * @param path a path as a list of indices of buildings
   * @param buildings an array of CityNodes
   * @return
   */
  private int calcPathCost(List<Integer> path, CityNode[] buildings) {
    CityNode fireStation = this.city.getFireStation().getLocation();
    int cost = CityNode.absDistance(fireStation, buildings[path.get(0)]);

    for (int i = 0; i < path.size()-1; i++)
      cost += CityNode.absDistance(buildings[path.get(i)], buildings[path.get(i + 1)]);

    return cost;
  }

  @Override
  public void setFirefighters(int numFirefighters) {
    Building fireStation = this.city.getFireStation();
    this.firefighters.clear();
    for (int i = 0; i < numFirefighters; i++) {
      this.firefighters.add(new FirefighterImpl(fireStation.getLocation()));
    }
  }

  @Override
  public List<Firefighter> getFirefighters() { return this.firefighters; }

  /**
   * If there is more than one firefighter or more than 10 burning buildings,
   * use greedy approach to optimize for time, otherwise can use brute force
   * which will guarantee optimal solution
   * @param burningBuildings list of locations with burning buildings
   */
  @Override
  public void dispatchFirefighters(CityNode... burningBuildings) {
    if (firefighters.size() > 1 || burningBuildings.length > 10) {
      greedyDispatch(burningBuildings);
    } else {
      bruteForce(burningBuildings);
    }
  }

  /**
   * Finds solution by choosing best option at each step
   * n = # of firefighters
   * m = # of burning buildings
   *
   * Runtime: O(n * m^2)
   * @param burningBuildings list of locations to be visited
   */
  @Override
  public void greedyDispatch(CityNode[] burningBuildings){
    this.distMatrix = new int[this.firefighters.size()][burningBuildings.length];

    // Initialize distMatrix with distances
    for (int i = 0; i < this.firefighters.size(); i++)
      updateDistances(i, burningBuildings);

    // Get possible moves for firefighters, weight them based on constraints then dispatch them
    for (int i = 0; i < burningBuildings.length; i++) {
      List<Move> possibleMoves = getPossibleMoves();

      // Constraint 1 - get moves with minimum distance to next building
      if (possibleMoves.size() > 1) {
        List<Integer> moveDistances = moveToDist(possibleMoves);
        possibleMoves = getMins(possibleMoves, moveDistances);
      }

      //Constraint 2 - get moves with maximum column sum (max distance building from all firefighters)
      if (possibleMoves.size() > 1) {
        List<Integer> columnSums = moveToColumnSum(possibleMoves);
        possibleMoves = getMins(possibleMoves, columnSums);
      }

      //Constraint 3 - get moves with Firefighters who have moved the least
      if (possibleMoves.size() > 1) {
        List<Integer> distTraveled = moveToDistTraveled(possibleMoves);
        possibleMoves = getMins(possibleMoves, distTraveled);
      }

      // Move firefighter and put fire out
      try {
        Move move = possibleMoves.get(0);
        CityNode building = burningBuildings[move.buildingIdx];
        city.getBuilding(building).extinguishFire();

        firefighters.get(move.fireFighterIdx).updateLocation(building);
        updateDistances(move.fireFighterIdx, burningBuildings);
        for (int j = 0; j < this.firefighters.size(); j++) {
          distMatrix[j][move.buildingIdx] = Integer.MAX_VALUE;
        }
      } catch (NoFireFoundException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Finds solution by computing every permutation and choosing
   * the one with the min path length
   *
   * Runtime: O(n!)
   * @param burningBuildings list of locations to be visited
   */
  @Override
  public void bruteForce(CityNode[] burningBuildings) {
    // Convert list of buildings to indices to pass to permute function
    int[] idxs = new int[burningBuildings.length];
    for (int i = 0; i < burningBuildings.length; i++) {
      idxs[i] = i;
    }
    List<List<Integer>> permutations = permute(idxs);

    // Calculate which permutation is the minimum cost
    List<Integer> minPath = new ArrayList<>();
    int minCost = Integer.MAX_VALUE;
    for (List<Integer> perm : permutations) {
      int cost = calcPathCost(perm, burningBuildings);
      if (cost < minCost) {
        minCost = cost;
        minPath = perm;
      }
    }

    Firefighter firefighter = this.firefighters.get(0);
    for (Integer i : minPath){
      try {
        city.getBuilding(burningBuildings[i]).extinguishFire();
        firefighter.updateLocation(burningBuildings[i]);
      } catch (NoFireFoundException e) {
        e.printStackTrace();
      }
    }
  }
}
