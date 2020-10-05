package main.api;

/**
 * Represents a move where the firefighter is moving to the building
 */
public class Move {
    public final int fireFighterIdx;
    public final int buildingIdx;

    public Move(int fireFighterIdx, int buildingIdx) {
        this.fireFighterIdx = fireFighterIdx;
        this.buildingIdx = buildingIdx;
    }
}
