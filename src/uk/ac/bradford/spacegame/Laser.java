/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.bradford.spacegame;

/**
 * The Laser class extends the Entity class.
 * Objects of this class are used to
 * store the position of lasers in the game.
 * @author klaudiabzdyk
 */
public class Laser extends Entity{
    /**
     * Creates an Laser object with specified
     * position on the game board.
     * @param x Starting X position for the laser.
     * @param y Starting Y position for the laser.
     */
    public Laser(int x, int y) {
        setPosition(x, y);
    }
}
