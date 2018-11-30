/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.bradford.spacegame;

/**
 * The Blaster class extends the Entity class and adds a single attribute to store a Asteroid.Direction value.
 * Objects of this class are used to
 * store the position and movement direction of blasters in the game.
 * @author klaudiabzdyk
 */
public class Blaster extends Entity {
    
    /**
     * Stores the movement direction for a single blaster object.
     */
    private Asteroid.Direction blasterDirection;
    
    /**
     * Gets the direction value for this blaster.
     * @return UP, DOWN, LEFT, RIGHT or NONE depending on the movement direction
     * for this blaster.
     */
    public Asteroid.Direction getBlasterDirection() {
        return blasterDirection;
    }
    
    /**
     * Creates a blaster object with a position specified by the two integer
     * arguments passed to the constructor, and a given movement direction.
     * @param x The x co-ordinate for this blaster
     * @param y The x co-ordinate for this blaster
     * @param d The movement direction for this blaster, which must be one of
     * the vales permitted in the Direction enumeration type
     */
    public Blaster(int x, int y, Asteroid.Direction d) {
        setPosition(x, y);
        blasterDirection = d;
    }
}
