/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.bradford.spacegame;

/**
 *
 * @author klaudiabzdyk
 */
public class Blaster extends Entity {
    
    private Asteroid.Direction blasterDirection;
    
    public Asteroid.Direction getBlasterDirection() {
        return blasterDirection;
    }
    
    public Blaster(int x, int y, Asteroid.Direction d) {
        setPosition(x, y);
        blasterDirection = d;
    }
}
