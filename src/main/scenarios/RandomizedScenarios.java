package main.scenarios;

import main.api.*;
import main.api.exceptions.FireproofBuildingException;
import main.impls.CityImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.fail;

public class RandomizedScenarios {
    /**
     * Compares path length and runtime of greedyDispatch vs bruteForce method.
     * Randomizes locations of the buildings on fire
     * @throws FireproofBuildingException
     */
    @Test
    public void randomizedFireNodesAlgoComparison() throws FireproofBuildingException {
        final int CITY_BOUND = 10;
        final int NUM_BURNING_BUILDINGS = 8;
        final int NUM_TRIALS = 10;

        Random rand = new Random(1);
        for (int i = 0; i < NUM_TRIALS; i++) {
            CityNode fireStation = new CityNode(4, 4);
            City basicCity = new CityImpl(CITY_BOUND, CITY_BOUND, fireStation);
            FireDispatch fireDispatch = basicCity.getFireDispatch();

            // Get random buildings to be set on fire that are not the firestation
            Set<CityNode> fireNodeSet = new HashSet<>();
            while (fireNodeSet.size() < NUM_BURNING_BUILDINGS) {
                CityNode fireNode = new CityNode(rand.nextInt(CITY_BOUND), rand.nextInt(CITY_BOUND));
                if (!fireStation.equals(fireNode)) {
                    fireNodeSet.add(fireNode);
                }
            }

            // Set buildings on fire and hire firefighters
            CityNode[] fireNodes = new CityNode[fireNodeSet.size()];
            fireNodes = fireNodeSet.toArray(fireNodes);
            Pyromaniac.setFires(basicCity, fireNodes);
            fireDispatch.setFirefighters(1);

            // Dispatch firefighters and time it
            long greedyStartTime = System.nanoTime();
            fireDispatch.greedyDispatch(fireNodes);
            long greedyEndTime = System.nanoTime();

            // Sum total distance travelled for Greedy
            List<Firefighter> firefightersGreedy = fireDispatch.getFirefighters();
            int totalDistanceTraveledGreedy = firefightersGreedy.get(0).distanceTraveled();

            // Set buildings on fire and hire firefighters
            Pyromaniac.setFires(basicCity, fireNodes);
            fireDispatch.setFirefighters(1);

            // Get brute force minimum path length and time it
            long bruteForceStartTime = System.nanoTime();
            fireDispatch.bruteForce(fireNodes);
            long bruteForceEndTime = System.nanoTime();

            // Sum total distance travelled for brute force
            List<Firefighter> firefightersBruteForce = fireDispatch.getFirefighters();
            int totDistTraveledBruteForce = firefightersBruteForce.get(0).distanceTraveled();

            for (CityNode fireNode : fireNodes) {
                Assert.assertFalse(basicCity.getBuilding(fireNode).isBurning());
            }
            System.out.println("bruteForce(pathLength: " + totDistTraveledBruteForce + " time: " + (bruteForceEndTime-bruteForceStartTime));
            System.out.println("greedy(pathLength: " + totalDistanceTraveledGreedy + " time: " + (greedyEndTime-greedyStartTime));
        }
    }

    /**
     * Random sample of bounded test space. Everything is reset between trials.
     * Randomizes number of burning buildings,
     *            number of firefighters,
     *            location of firehouse,
     *            and location of burning buildings
     * @throws FireproofBuildingException
     */
    @Test
    public void randomizedAllParametersWithReset() throws FireproofBuildingException {
        final int CITY_BOUND = 10;
        final int MAX_NUM_BURNING_BUILDINGS = 20;
        final int NUM_TRIALS = 10;
        final int MAX_FIREFIGHTERS = 8;

        Random rand = new Random(1);
        for (int i = 0; i <= NUM_TRIALS; i++) {
            CityNode fireStation = new CityNode(rand.nextInt(CITY_BOUND), rand.nextInt(CITY_BOUND));
            City basicCity = new CityImpl(CITY_BOUND, CITY_BOUND, fireStation);
            FireDispatch fireDispatch = basicCity.getFireDispatch();

            // Get random buildings to be set on fire that are not the firestation
            Set<CityNode> fireNodeSet = new HashSet<>();
            int numBuildings = rand.nextInt(MAX_NUM_BURNING_BUILDINGS);
            while (fireNodeSet.size() < numBuildings) {
                CityNode fireNode = new CityNode(rand.nextInt(CITY_BOUND), rand.nextInt(CITY_BOUND));
                if (!fireStation.equals(fireNode)) {
                    fireNodeSet.add(fireNode);
                }
            }

            // Set buildings on fire and hire firefighters
            CityNode[] fireNodes = new CityNode[fireNodeSet.size()];
            fireNodes = fireNodeSet.toArray(fireNodes);
            Pyromaniac.setFires(basicCity, fireNodes);
            fireDispatch.setFirefighters(rand.nextInt(MAX_FIREFIGHTERS-1) + 1);

            // Dispatch firefighters print out random variables for debug if an exception is thrown
            try {
                fireDispatch.dispatchFirefighters(fireNodes);
            } catch (Exception e) {
                System.out.println("Num firefighters: " + fireDispatch.getFirefighters().size());
                System.out.println("Firestation pos: " + basicCity.getFireStation().getLocation());
                System.out.println("Firenodes: ");
                for (CityNode fireNode : fireNodes) {
                    System.out.println("  " + fireNode);
                }
                fail("Exception thrown");
            }

            // Sum total distance travelled
            List<Firefighter> firefighters = fireDispatch.getFirefighters();
            int totalDistanceTraveled = 0;
            int maxSingleDistanceTraveled = 0;
            for (Firefighter firefighter : firefighters) {
                totalDistanceTraveled += firefighter.distanceTraveled();
                if (firefighter.distanceTraveled() > maxSingleDistanceTraveled) {
                    maxSingleDistanceTraveled = firefighter.distanceTraveled();
                }
            }

            // Assert all buildings set on fire are now not on fire
            for (CityNode fireNode : fireNodes) {
                Assert.assertFalse(basicCity.getBuilding(fireNode).isBurning());
            }
            System.out.println("pathLength: " + totalDistanceTraveled + " maxSingle: " + maxSingleDistanceTraveled);
        }
    }

    /**
     * Random sample of bounded test space. City and firefighters are persistent.
     * Randomizes number of burning buildings,
     *            number of firefighters,
     *            location of firehouse,
     *            and location of burning buildings
     * @throws FireproofBuildingException
     */
    @Test
    public void randomizedAllParametersWithoutReset() throws FireproofBuildingException {
        final int CITY_BOUND = 10;
        final int MAX_NUM_BURNING_BUILDINGS = 20;
        final int NUM_TRIALS = 10;
        final int MAX_FIREFIGHTERS = 8;

        Random rand = new Random(1);
        CityNode fireStation = new CityNode(rand.nextInt(CITY_BOUND), rand.nextInt(CITY_BOUND));
        City basicCity = new CityImpl(CITY_BOUND, CITY_BOUND, fireStation);
        FireDispatch fireDispatch = basicCity.getFireDispatch();
        for (int i = 0; i <= NUM_TRIALS; i++) {

            // Get random buildings to be set on fire that are not the firestation
            Set<CityNode> fireNodeSet = new HashSet<>();
            int numBuildings = rand.nextInt(MAX_NUM_BURNING_BUILDINGS);
            while (fireNodeSet.size() < numBuildings) {
                CityNode fireNode = new CityNode(rand.nextInt(CITY_BOUND), rand.nextInt(CITY_BOUND));
                if (!fireStation.equals(fireNode)) {
                    fireNodeSet.add(fireNode);
                }
            }

            // Set buildings on fire and hire firefighters
            CityNode[] fireNodes = new CityNode[fireNodeSet.size()];
            fireNodes = fireNodeSet.toArray(fireNodes);
            Pyromaniac.setFires(basicCity, fireNodes);
            fireDispatch.setFirefighters(rand.nextInt(MAX_FIREFIGHTERS - 1) + 1);

            try {
                fireDispatch.dispatchFirefighters(fireNodes);
            } catch (Exception e) {
                System.out.println("Num firefighters: " + fireDispatch.getFirefighters().size());
                System.out.println("Firestation pos: " + basicCity.getFireStation().getLocation());
                System.out.println("Firenodes: ");
                for (CityNode fireNode : fireNodes){
                    System.out.println("  " + fireNode);
                }
                fail("Exception thrown");
            }

            List<Firefighter> firefighters = fireDispatch.getFirefighters();
            int totalDistanceTraveled = 0;
            for (Firefighter firefighter : firefighters) {
                totalDistanceTraveled += firefighter.distanceTraveled();
            }

            for (CityNode fireNode : fireNodes) {
                Assert.assertFalse(basicCity.getBuilding(fireNode).isBurning());
            }
            System.out.println("pathLength: " + totalDistanceTraveled);
        }
    }
}
