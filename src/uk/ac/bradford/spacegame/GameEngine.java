/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.bradford.spacegame;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;

/**
 * The GameEngine class is responsible for managing information about the game,
 * creating levels, the player, aliens, blasters and asteroids, as well as updating
 * information when a key is pressed while the game is running.
 *
 * @author prtrundl & klaudiabzdyk
 */
public class GameEngine {

    /**
     * An enumeration type to represent different types of tiles that make up
     * the level. Each type has a corresponding image file that is used to draw
     * the right tile to the screen for each tile in a level. Space is open for
     * the player and asteroids to move into, black holes will kill the player
     * if they move into the tile and destroy asteroids that move into them,
     * pulsars will damage the player if they are in or adjacent to a pulsar
     * tile while it is active.
     */
    public enum TileType {
        SPACE, BLACK_HOLE, PULSAR_ACTIVE, PULSAR_INACTIVE
    }

    /**
     * The width of the level, measured in tiles. Changing this may cause the
     * display to draw incorrectly, and as a minimum the size of the GUI would
     * need to be adjusted.
     */
    public static final int GRID_WIDTH = 25;

    /**
     * The height of the level, measured in tiles. Changing this may cause the
     * display to draw incorrectly, and as a minimum the size of the GUI would
     * need to be adjusted.
     */
    public static final int GRID_HEIGHT = 18;

    /**
     * The chance of a black hole being generated instead of open space when
     * generating the level. 1.0 is 100% chance, 0.0 is 0% chance. This can be
     * changed to affect the difficulty.
     */
    private static double BLACK_HOLE_CHANCE = 0.07;

    /**
     * The chance of a pulsar being created instead of open space when
     * generating the level. 1.0 is 100% chance, 0.0 is 0% chance. This can be
     * changed to affect the difficulty.
     */
    private static double PULSAR_CHANCE = 0.03;

    /**
     * A random number generator that can be used to include randomised choices
     * in the creation of levels, in choosing places to spawn the player, aliens
     * and asteroids, and to randomise movement or other factors.
     */
    private Random rng = new Random();

    /**
     * The number of levels cleared by the player in this game. Can be used to
     * generate harder games as the player clears levels.
     */
    private int cleared = 0;

    /**
     * The number of points the player has gained this level. Used to track when
     * the current level is won and a new one should be generated.
     */
    private int points = 0;

    private int blasterCheck = 0;
    
    /**
     * Tracks the current turn number. Used to control pulsar activation and
     * asteroid movement.
     */
    private int turnNumber = 1;

    /**
     * The GUI associated with a GameEngine object. THis link allows the engine
     * to pass level (tiles) and entity information to the GUI to be drawn.
     */
    private GameGUI gui;

    /**
     * The 2 dimensional array of tiles the represent the current level. The
     * size of this array should use the GRID_HEIGHT and GRID_WIDTH attributes
     * when it is created.
     */
    private TileType[][] tiles;

    /**
     * An ArrayList of Point objects used to create and track possible locations
     * to spawn the player, aliens and asteroids.
     */
    private ArrayList<Point> spawns;

    /**
     * A Player object that is the current player. This object stores the state
     * information for the player, including hull strength and the current
     * position (which is a pair of co-ordinates that corresponds to a tile in
     * the current level)
     */
    private Player player;

    /**
     * An array of Alien objects that represents the aliens in the current
     * level. Elements in this array should be of the type Alien, meaning that
     * an alien is alive and needs to be drawn or moved, or should be null which
     * means nothing is drawn or processed for movement. Null values in this
     * array are skipped during drawing and movement processing.
     */
    private Alien[] aliens;

    /**
     * An array of Asteroid objects that represents the asteroids in the current
     * level. Elements in this array should be of the type Asteroid, meaning
     * that an asteroid exists and needs to be drawn or moved, or should be null
     * which means nothing is drawn or processed for movement. Null values in
     * this array are skipped during drawing and movement processing.
     */
    private Asteroid[] asteroids;

    private Blaster[] blasters;

    /**
     * Constructor that creates a GameEngine object and connects it with a
     * GameGUI object.
     *
     * @param gui The GameGUI object that this engine will pass information to
     * in order to draw levels and entities to the screen.
     */
    public GameEngine(GameGUI gui) {
        this.gui = gui;
        startGame();
    }

    /**
     * Generates a new level. The method builds a 2D array of TileTypes that
     * will be used to draw tiles to the screen and to add a variety of elements
     * into each level. Tiles can be space, black holes, active pulsars or
     * inactive pulsars. This method should contain the implementation of an
     * algorithm to create an interesting and varied level each time it is
     * called.
     *
     * @return A 2D array of TileTypes representing the tiles in the current
     * level of the dungeon. The size of this array uses the width and height
     * attributes of the level specified by GRID_WIDTH and GRID_HEIGHT.
     */
    private TileType[][] generateLevel() {
        tiles = new TileType[GRID_WIDTH][GRID_HEIGHT];
        int numberOfTiles = GRID_WIDTH * GRID_HEIGHT;
        int numberOfBHoles = (int) (numberOfTiles * BLACK_HOLE_CHANCE);
        int numberOfPulsars = (int) (numberOfTiles * PULSAR_CHANCE);
        int counter;
        int randomIndex;
        int randomSecIndex;
        /**
         * loop for spaces which iterate tiles and puts space in every tale
         */
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                    tiles[i][j] = TileType.SPACE;
            }
        }
        /**
         * while loop which creates black holes in random places of the tiles
         * array numberOfBHoles times
         */
        counter = 0;
        while (counter < numberOfBHoles) {
            randomIndex = (int) (rng.nextDouble() * GRID_WIDTH - 1);
            randomSecIndex = (int) (rng.nextDouble() * GRID_HEIGHT - 1);
            if (tiles[randomIndex][randomSecIndex] != TileType.PULSAR_INACTIVE && tiles[randomIndex][randomSecIndex] != TileType.PULSAR_ACTIVE && tiles[randomIndex][randomSecIndex] != TileType.BLACK_HOLE) {
                tiles[randomIndex][randomSecIndex] = TileType.BLACK_HOLE;
                counter++;
            }
        }

        /**
         * loop for pulsars which take random number for width and random for
         * height for tiles and check if that tale is space, if no loop search
         * another tale, if yes loop puts pulsar here, loop is executed
         * numberOfPulsars times
         */
        counter = 0;
        while (counter < numberOfPulsars) {
            randomIndex = (int) (rng.nextDouble() * GRID_WIDTH - 1);
            randomSecIndex = (int) (rng.nextDouble() * GRID_HEIGHT - 1);
            if (tiles[randomIndex][randomSecIndex] != TileType.PULSAR_ACTIVE && tiles[randomIndex][randomSecIndex] != TileType.BLACK_HOLE && tiles[randomIndex][randomSecIndex] != TileType.PULSAR_INACTIVE) {
                if (rng.nextBoolean() == true) {
                    tiles[randomIndex][randomSecIndex] = TileType.PULSAR_ACTIVE;
                    counter++;
                } else {
                    tiles[randomIndex][randomSecIndex] = TileType.PULSAR_INACTIVE;
                    counter++;
                }
            }
        }
        return tiles;
    }

    /**
     * Generates spawn points for entities. The method processes the tiles array
     * and finds tiles that are suitable for spawning, i.e. space tiles.
     * Suitable tiles should be added to the ArrayList that will be returned as
     * Point objects - Points are a simple kind of object that contain an X and
     * a Y co-ordinate stored using the int primitive type.
     *
     * @return An ArrayList containing Point objects representing suitable X and
     * Y co-ordinates in the current level that entities can be spawned in.
     */
    private ArrayList<Point> getSpawns() {
        spawns = new ArrayList<Point>();
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (tiles[i][j] == TileType.SPACE) {
                    Point x = new Point();
                    x.setLocation(i, j);
                    spawns.add(x);
                }
            }
        }
        return spawns;
    }
    
    private Blaster[] createBlastersList() {
        blasters = new Blaster[8];
        for (Blaster blaster : blasters) {
            blaster = null;
        }
        return blasters;
    }

    /**
     * Spawns aliens in suitable locations in the current level. The method uses
     * the spawns ArrayList to pick suitable positions to add aliens, removing
     * these positions from the spawns ArrayList as they are used (using the
     * remove() method) to avoid multiple entities spawning in the same
     * location. The method creates aliens by instantiating the Alien class,
     * setting health and the X and Y position for the alien using the Point
     * object removed from the spawns ArrayList.
     *
     * @return An array of Alien objects representing the aliens for the current
     * level
     */
    private Alien[] spawnAliens() {
        int aIndex = 0;
        Point alienPoint = null;
        aliens = new Alien[getSpawns().size()];
        int counter = 0;
        int counterForList = 0;
        for (int i = 0; i < getSpawns().size(); i++) {
            aliens[i] = null;
        }
        if (player != null) {
            int playerX = player.getX();
            int playerY = player.getY();
            int [] aliensYs = new int[cleared + 4];
            while (counter < cleared + 4) {
                int counter1 = 0;
                while (counter1 < cleared + 4) {
                    aIndex = (int) (rng.nextDouble() * aliens.length);
                    alienPoint = getSpawns().get(aIndex);
                    for (int i = 0; i < aliensYs.length; i++) {
                        if (alienPoint.getY() != aliensYs[i]) {
                            counter1++;
                        }
                    }
                }
                if (aliens[aIndex] == null) {
                    if (alienPoint.getX() != playerX || alienPoint.getY() != playerY) {
                        getSpawns().remove(aIndex);
                        Alien newAlien = new Alien(50, (int) alienPoint.getX(), (int) alienPoint.getY());
                        aliens[aIndex] = newAlien;
                        counter++;
                    }
                }
                aliensYs[counter - 1] =  (int) alienPoint.getX();  
            }
            for (int i = 0; i < aliensYs.length; i++) {
                System.out.println(aliensYs[i]);
                
            }
    
        }
        return aliens;
    }

    /**
     * Spawns a Player entity in the game. The method uses the spawns ArrayList
     * to select a suitable location to spawn the player and removes the Point
     * from the spawns ArrayList. The method instantiates the Player class and
     * assigns values for the health and position of the player.
     *
     * @return A Player object representing the player in the game
     */
    private Player spawnPlayer() {
        int randomTale = (int) (rng.nextDouble() * getSpawns().size());
        Point xPoint = getSpawns().get(randomTale);
        double x = xPoint.getX();
        double y = xPoint.getY();
        player = new Player(100, (int) x, (int) y);
        return player;
    }

    /**
     * Handles the movement of the player when attempting to move left in the
     * game. This method is called by the InputHandler class when the user has
     * pressed the left arrow key on the keyboard. The method checks whether the
     * tile to the left of the player is empty for movement and if it is updates
     * the player object's X and Y locations with the new position. If the tile
     * to the left of the player is not empty the method will not update the
     * player position, but could make other changes to the game.
     */
    public void movePlayerLeft() {
        int playerX = player.getX();
        int playerY = player.getY();
        if ((playerX - 1) >= 0 && tiles[playerX - 1][playerY] != TileType.BLACK_HOLE) {
            playerX--;
            player.setPosition(playerX, playerY);
            for (int i = 0; i < asteroids.length; i++) {
                if ((asteroids[i] != null && asteroids[i].getX() == playerX) && (asteroids[i].getY() == playerY)) {
                    asteroids[i] = null;
                    points++;
                    blasterCheck++;
                }
            }
        } else if ((playerX - 1) == -1 && tiles[GRID_WIDTH - 1][playerY] != TileType.BLACK_HOLE) {
            player.setPosition(GRID_WIDTH - 1, playerY);
            for (int i = 0; i < asteroids.length; i++) {
                if (asteroids[i] != null && asteroids[i].getX() == GRID_WIDTH - 1 && asteroids[i].getY() == playerY) {
                    asteroids[i] = null;
                    points++;
                    blasterCheck++;
                }
            }
        } else {
            if (points > 0) {
                points--;
                blasterCheck--;
            }
        }
        for (int i = 0; i < aliens.length; i++) {
            if ((aliens[i] != null && aliens[i].getX() == playerX) && (aliens[i].getY() == playerY)) {
                if (player.hullStrength > 30) {
                    player.hullStrength -= 30;
                } 
                else {
                    player.hullStrength = 0;
                }
            }
        }
    }

    /**
     * Handles the movement of the player when attempting to move right in the
     * game. This method is called by the InputHandler class when the user has
     * pressed the right arrow key on the keyboard. The method checks whether
     * the tile to the right of the player is empty for movement and if it is
     * updates the player object's X and Y locations with the new position. If
     * the tile to the right of the player is not empty the method will not
     * update the player position, but could make other changes to the game.
     */
    public void movePlayerRight() {
        int playerX = player.getX();
        int playerY = player.getY();
        if ((playerX + 1) < GRID_WIDTH && tiles[playerX + 1][playerY] != TileType.BLACK_HOLE) {
            playerX++;
            player.setPosition(playerX, playerY);
            for (int i = 0; i < asteroids.length; i++) {
                if (asteroids[i] != null && asteroids[i].getX() == playerX && asteroids[i].getY() == playerY) {
                    asteroids[i] = null;
                    points++;
                    blasterCheck++;
                }

            }
        } else if ((playerX + 1) == GRID_WIDTH && tiles[0][playerY] != TileType.BLACK_HOLE) {
            player.setPosition(0, playerY);
            for (int i = 0; i < asteroids.length; i++) {
                if (asteroids[i] != null && asteroids[i].getX() == 0 && asteroids[i].getY() == playerY) {
                    asteroids[i] = null;
                    points++;
                    blasterCheck++;
                }

            }
        } else {
            if (points > 0) {
                points--;
                blasterCheck--;
            }
        }
        for (int i = 0; i < aliens.length; i++) {
            if ((aliens[i] != null && aliens[i].getX() == playerX) && (aliens[i].getY() == playerY)) {
                if (player.hullStrength > 30) {
                    player.hullStrength -= 30;
                } 
                else {
                    player.hullStrength = 0;
                }
            }
        }

    }

    /**
     * Handles the movement of the player when attempting to move up in the
     * game. This method is called by the InputHandler class when the user has
     * pressed the up arrow key on the keyboard. The method checks whether the
     * tile above the player is empty for movement and if it is updates the
     * player object's X and Y locations with the new position. If the tile
     * above the player is not empty the method will not update the player
     * position, but could make other changes to the game.
     */
    public void movePlayerUp() {
        int playerX = player.getX();
        int playerY = player.getY();
        if ((playerY - 1) >= 0 && tiles[playerX][playerY - 1] != TileType.BLACK_HOLE) {
            playerY--;
            player.setPosition(playerX, playerY);
            for (int i = 0; i < asteroids.length; i++) {
                if (asteroids[i] != null && asteroids[i].getX() == playerX && asteroids[i].getY() == playerY) {
                    asteroids[i] = null;
                    points++;
                    blasterCheck++;
                }

            }
        } else if ((playerY - 1) == -1 && tiles[playerX][GRID_HEIGHT - 1] != TileType.BLACK_HOLE) {
            player.setPosition(playerX, GRID_HEIGHT - 1);
            for (int i = 0; i < asteroids.length; i++) {
                if (asteroids[i] != null && asteroids[i].getX() == playerX && asteroids[i].getY() == GRID_HEIGHT - 1) {
                    asteroids[i] = null;
                    points++;
                    blasterCheck++;
                }

            }
        } else {
            if (points > 0) {
                points--;
                blasterCheck--;
            }
        }
        for (int i = 0; i < aliens.length; i++) {
            if ((aliens[i] != null && aliens[i].getX() == playerX) && (aliens[i].getY() == playerY)) {
                if (player.hullStrength > 30) {
                    player.hullStrength -= 30;
                } 
                else {
                    player.hullStrength = 0;
                }
            }
        }

    }

    /**
     * Handles the movement of the player when attempting to move right in the
     * game. This method is called by the InputHandler class when the user has
     * pressed the down arrow key on the keyboard. The method checks whether the
     * tile below the player is empty for movement and if it is updates the
     * player object's X and Y locations with the new position. If the tile
     * below the player is not empty the method will not update the player
     * position, but could make other changes to the game.
     */
    public void movePlayerDown() {
        int playerX = player.getX();
        int playerY = player.getY();
        if ((playerY + 1) < GRID_HEIGHT && tiles[playerX][playerY + 1] != TileType.BLACK_HOLE) {
            playerY++;
            player.setPosition(playerX, playerY);
            for (int i = 0; i < asteroids.length; i++) {
                if (asteroids[i] != null && asteroids[i].getX() == playerX && asteroids[i].getY() == playerY) {
                    asteroids[i] = null;
                    points++;
                    blasterCheck++;
                }

            }
        } else if ((playerY + 1) == GRID_HEIGHT && tiles[playerX][0] != TileType.BLACK_HOLE) {
            player.setPosition(playerX, 0);
            for (int i = 0; i < asteroids.length; i++) {
                if (asteroids[i] != null && asteroids[i].getX() == playerX && asteroids[i].getY() == 0) {
                    asteroids[i] = null;
                    points++;
                    blasterCheck++;
                }

            }
        } else {
            if (points > 0) {
                points--;
                blasterCheck--;
            }
        }
        for (int i = 0; i < aliens.length; i++) {
            if ((aliens[i] != null && aliens[i].getX() == playerX) && (aliens[i].getY() == playerY)) {
                if (player.hullStrength > 30) {
                    player.hullStrength -= 30;
                } 
                else {
                    player.hullStrength = 0;
                }
            }
        }

    }

    /**
     * Updates the position of Asteroid objects by altering their X and Y
     * co-ordinates according to their moveDirection attribute value. This
     * iterates over the asteroids array one element at a time, checks if the
     * current element is null (skipping it if it is null) and finding the
     * moveDirection value for the current asteroid object. Asteroids with a
     * moveDirection value other than NONE should have their position updated
     * accordingly, and if their new position puts them outside the map or
     * inside a black hole they are "destroyed". Destroyed asteroids should be
     * replaced by creating a new, randomly positioned asteroid in the same
     * index of the asteroids array that the destroyed asteroid used to occupy.
     */
    private void moveAsteroids() {
        for (int i = 0; i < asteroids.length; i++) {
            if (asteroids[i] != null) {
                int asteroidX = asteroids[i].getX();
                int asteroidY = asteroids[i].getY();
                Asteroid.Direction direction = asteroids[i].getMovementDirection();
                int pIndex = (int) (rng.nextDouble() * getSpawns().size());
                Point point = getSpawns().get(pIndex);
                switch (direction) {
                    case DOWN:
                        if (asteroidY == GRID_HEIGHT - 1) {
                            asteroids[i].setPosition((int) point.getX(), (int) point.getY());
                            getSpawns().remove(pIndex);
                        } else {
                            asteroids[i].setPosition(asteroidX, asteroidY + 1);
                        }
                        break;
                    case UP:
                        if (asteroidY == 0) {
                            asteroids[i].setPosition((int) point.getX(), (int) point.getY());
                        } else {
                            asteroids[i].setPosition(asteroidX, asteroidY - 1);
                        }
                        break;
                    case RIGHT:
                        if (asteroidX == GRID_WIDTH - 1) {
                            asteroids[i].setPosition((int) point.getX(), (int) point.getY());
                        } else {
                            asteroids[i].setPosition(asteroidX + 1, asteroidY);
                        }
                        break;
                    case LEFT:
                        if (asteroidX == 0) {
                            asteroids[i].setPosition((int) point.getX(), (int) point.getY());
                        } else {
                            asteroids[i].setPosition(asteroidX - 1, asteroidY);
                        }
                        break;
                    case NONE:
                        break;
                }
                asteroidX = asteroids[i].getX();
                asteroidY = asteroids[i].getY();
                if (tiles[asteroidX][asteroidY] == TileType.BLACK_HOLE) {
                    asteroids[i].setPosition((int) point.getX(), (int) point.getY());
                }
            }

        }
    }

    /**
     * Moves all aliens on the current level. The method checks for non-null
     * elements in the aliens array and calls the moveAlien method for each one
     * that is not null.
     */
    private void moveAliens() {
        for (Alien newAlien : aliens) {
            if (newAlien != null) {
                moveAlien(newAlien);
            }
        }
    }

    /**
     * Moves a specific alien in the game. The method updates the X and Y
     * attributes of the alien to reflect its new position.
     *
     * @param a The Alien that needs to be moved
     */
    private void moveAlien(Alien a) {
        int playerX = player.getX();
        int playerY = player.getY();
        int alienX = a.getX();
        int alienY = a.getY();
        int counter = 0;
        
        
        
        
        
              


        
//        int dX = Math.abs(playerX - alienX);
//        int dY = Math.abs(playerY - alienY);
//        
//        
//        if (dX >= dY) {
//            if (alienX < playerX - 1) {
//                alienX++;
//            } else if (alienX > playerX + 1) {
//                alienX--;
//            }
//        }
//        else {
//            if (alienY < playerY - 1) {
//                alienY++;
//            } else if (alienY > playerY + 1) {
//                alienY--;
//            }
//        }
        
        if (alienX < playerX - 1) {
            alienX++;
        } else if (alienX > playerX + 1) {
            alienX--;
        }
        if (alienY < playerY - 1) {
            alienY++;
        } else if (alienY > playerY + 1) {
            alienY--;
        }
        
//        a.setPosition(alienX, alienY);
        for (int i = 0; i < aliens.length; i++) {
            if (aliens[i] != null) {
                if (aliens[i].getX() != alienX || aliens[i].getY() != alienY ) {
                    a.setPosition(alienX, alienY);
                }

            }
        }
        for (Asteroid asteroid : asteroids) {
            if (asteroid != null && asteroid.getX() == alienX && asteroid.getY() == alienY) {
                counter = 0;
                while (counter < 1) {
                    int randomIndex = (int) (rng.nextDouble() * getSpawns().size() - 1);
                    Point point = getSpawns().get(randomIndex);
                    int pointX = (int) point.getX();
                    int pointY = (int) point.getY();
                    if (pointX != playerX || pointY != playerY) {
                        asteroid.setPosition(pointX, pointY);
                        if (a.hullStrength < a.maxHull - 10) {
                            a.hullStrength += 10;
                        } else {
                            a.hullStrength = a.maxHull;
                        }
                        counter++;
                    }
                }
            }
        }
    }

    /**
     * Spawns asteroids in suitable locations in the current level. The method
     * uses the spawns ArrayList to pick suitable positions to add asteroids,
     * removing these positions from the spawns ArrayList as they are used
     * (using the remove() method) to avoid multiple entities spawning in the
     * same location. The method creates asteroids by repeatedly instantiating
     * the Asteroid class and setting the X and Y position for the asteroid
     * using the Point object removed from the spawns ArrayList.
     *
     * @return An array of Asteroid objects representing the asteroids for the
     * current level
     */
    private Asteroid[] spawnAsteroids() {
        asteroids = new Asteroid[getSpawns().size()];
        int counter = 0;
        for (int i = 0; i < asteroids.length; i++) {
            asteroids[i] = null;
        }
        while (counter < asteroids.length / 5) {
            int aIndex = (int) (rng.nextDouble() * asteroids.length);
            Point asteroidPoint = getSpawns().get(aIndex);
            if (asteroids[aIndex] == null) {
                getSpawns().remove(aIndex);
                Asteroid newAsteroid = new Asteroid((int) asteroidPoint.getX(), (int) asteroidPoint.getY());
                asteroids[aIndex] = newAsteroid;
                counter++;
            }
        }
        return asteroids;
    }

    /**
     * Processes the tiles array to find inactive pulsars and change them to
     * active pulsars. When a tile is found of the correct type, that tile is
     * set to PULSAR_ACTIVE. When the map is drawn to the screen next the
     * inactive pulsar will now be an active pulsar.
     */
    private void activatePulsars() {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (tiles[i][j] == TileType.PULSAR_INACTIVE) {
                    tiles[i][j] = TileType.PULSAR_ACTIVE;
                }
            }
        }

    }

    /**
     * Processes the tiles array to find active pulsars and change them to
     * inactive pulsars. When a tile is found of the correct type, that tile is
     * set to PULSAR_INACTIVE. When the map is drawn to the screen next the
     * active pulsar will now be an inactive pulsar.
     */
    private void deactivatePulsars() {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (tiles[i][j] == TileType.PULSAR_ACTIVE) {
                    tiles[i][j] = TileType.PULSAR_INACTIVE;
                }
            }
        }
    }

    /**
     * Damages the player if the player is in an active pulsar tile, or any of
     * the eight tiles adjacent to the active pulsar, when this method is
     * called. The method uses the player's current x and y position and
     * searches around the player looking for pulsar tiles. Any pulsar tiles
     * found this way result in a call to the changeHullStrength method for the
     * player object to damage the player.
     */
    private void pulsarDamage() {
        int playerX = player.getX();
        int playerY = player.getY();
//        00 10 20
//        01 11 21
//        02 12 22

        for (int i = playerX - 1; i < playerX + 1; i++) {
            for (int j = playerY - 1; j < playerY + 1; j++) {
                if (i >= 0 && i < GRID_WIDTH && j >= 0 && j < GRID_HEIGHT) {
                    if (tiles[i][j] == TileType.PULSAR_ACTIVE) {
                        player.hullStrength -= 5;
                    }
                }

            }
        }
    }

    public Blaster[] fireBlaster() {
        int playerX = player.getX();
        int playerY = player.getY();
        if (playerX > 0 && tiles[playerX - 1][playerY] != TileType.BLACK_HOLE) {
            blasters[0] = new Blaster(playerX - 1, playerY, Asteroid.Direction.LEFT);   
        } 
        if (playerX < GRID_WIDTH - 1 && tiles[playerX + 1][playerY] != TileType.BLACK_HOLE) {
            blasters[1] = new Blaster(playerX + 1, playerY, Asteroid.Direction.RIGHT);
        } 
        if (playerY < GRID_HEIGHT - 1 && tiles[playerX][playerY + 1] != TileType.BLACK_HOLE) {
            blasters[2] = new Blaster(playerX, playerY + 1, Asteroid.Direction.DOWN);
        } 
        if (playerY > 0 && tiles[playerX][playerY - 1] != TileType.BLACK_HOLE) {
            blasters[3] = new Blaster(playerX, playerY - 1, Asteroid.Direction.UP);
        } 
        if (playerX < GRID_WIDTH - 1 && playerY > 0 && tiles[playerX + 1][playerY - 1] != TileType.BLACK_HOLE) {
            blasters[4] = new Blaster(playerX + 1, playerY - 1, Asteroid.Direction.UPRIGHT);
        }
        if (playerX > 0 && playerY > 0 && tiles[playerX - 1][playerY - 1] != TileType.BLACK_HOLE) {
            blasters[5] = new Blaster(playerX - 1, playerY - 1, Asteroid.Direction.UPLEFT);
        }
        if (playerX < GRID_WIDTH - 1 && playerY < GRID_HEIGHT - 1 && tiles[playerX + 1][playerY + 1] != TileType.BLACK_HOLE) {
            blasters[6] = new Blaster(playerX + 1, playerY + 1, Asteroid.Direction.DOWNRIGHT);
        }
        if (playerX > 0 && playerY > 0 && playerY < GRID_HEIGHT - 1 && tiles[playerX - 1][playerY + 1] != TileType.BLACK_HOLE) {
            blasters[7] = new Blaster(playerX - 1, playerY + 1, Asteroid.Direction.DOWNLEFT);
        }
        return blasters;
    }

    private void moveBlasters() {
        int blasterX;
        int blasterY;
        for (int i = 0; i < blasters.length; i++) {
            if (blasters[i] != null) {
                blasterX = blasters[i].getX();
                blasterY = blasters[i].getY();
                if (blasters[i].getBlasterDirection() == Asteroid.Direction.LEFT) {
                    if (blasterX > 0 && tiles[blasterX - 1][blasterY] != TileType.BLACK_HOLE) {
                        blasters[i].setPosition(blasterX - 1, blasterY);
                    } else {
                        blasters[i] = null;
                    }
                } else if (blasters[i].getBlasterDirection() == Asteroid.Direction.RIGHT) {
                    if (blasterX < GRID_WIDTH - 1 && tiles[blasterX + 1][blasterY] != TileType.BLACK_HOLE) {
                        blasters[i].setPosition(blasterX + 1, blasterY);
                    } else {
                        blasters[i] = null;
                    }
                } 
                else if (blasters[i].getBlasterDirection() == Asteroid.Direction.UP) {
                    if (blasterY > 0 && tiles[blasterX][blasterY - 1] != TileType.BLACK_HOLE) {
                        blasters[i].setPosition(blasterX, blasterY - 1);
                    } else {
                        blasters[i] = null;
                    }
                } 
                else if (blasters[i].getBlasterDirection() == Asteroid.Direction.DOWN) {
                    if (blasterY < GRID_HEIGHT - 1 && tiles[blasterX][blasterY + 1] != TileType.BLACK_HOLE) {
                        blasters[i].setPosition(blasterX, blasterY + 1);
                    } 
                    else {
                        blasters[i] = null;
                    }
                }
                else if (blasters[i].getBlasterDirection() == Asteroid.Direction.UPRIGHT) {
                    if (blasterX < GRID_WIDTH - 1 && blasterY > 0 && tiles[blasterX + 1][blasterY - 1] != TileType.BLACK_HOLE) {
                        blasters[i].setPosition(blasterX + 1, blasterY - 1);
                    }
                    else {
                        blasters[i] = null;
                    }
                }
                else if (blasters[i].getBlasterDirection() == Asteroid.Direction.UPLEFT) {
                    if (blasterX > 0 && blasterY > 0 && tiles[blasterX - 1][blasterY - 1] != TileType.BLACK_HOLE) {
                        blasters[i].setPosition(blasterX - 1, blasterY - 1);
                    }
                    else {
                        blasters[i] = null;
                    }
                }
                else if (blasters[i].getBlasterDirection() == Asteroid.Direction.DOWNRIGHT) {
                    if (blasterX < GRID_WIDTH - 1 && blasterY < GRID_HEIGHT - 1 && tiles[blasterX + 1][blasterY + 1] != TileType.BLACK_HOLE) {
                        blasters[i].setPosition(blasterX + 1, blasterY + 1);
                    }
                    else {
                        blasters[i] = null;
                    }
                }
                else if (blasters[i].getBlasterDirection() == Asteroid.Direction.DOWNLEFT) {
                    if (blasterX > 0 && blasterY > 0 && blasterY < GRID_HEIGHT - 1 && tiles[blasterX - 1][blasterY + 1] != TileType.BLACK_HOLE) {
                        blasters[i].setPosition(blasterX - 1, blasterY + 1);
                    }
                    else {
                        blasters[i] = null;
                    }
                }

                if (blasters[i] != null) {
                    blasterX = blasters[i].getX();
                    blasterY = blasters[i].getY();
                    for (Asteroid asteroid : asteroids) {
                        if (asteroid != null) {
                            if (asteroid.getX() == blasterX && asteroid.getY() == blasterY) {
                                asteroid = null;
                                points++;
                                blasterCheck++;
                            }
                        }
                    }
                    for (Alien alien : aliens) {
                        if (alien != null) {
                            if (alien.getX() == blasterX && alien.getY() == blasterY) {
                                if (alien.hullStrength > 29) {
                                    alien.hullStrength -= 30;
                                } else {
                                    alien.hullStrength = 0;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
        /**
         * Called in response to the player collecting enough points win the
         * current level. The method increases the valued of cleared by one,
         * resets the value of points to zero, generates a new level by calling
         * the generateLevel method, fills the spawns ArrayList with suitable
         * spawn locations, then spawns aliens and asteroids. Finally it places
         * the player in the new level by calling the placePlayer() method. Note
         * that a new player object should not be created here as this will
         * reset the player's health to maximum.
         */
    private void newLevel() {
        cleared++;
        BLACK_HOLE_CHANCE += 0.01;
        PULSAR_CHANCE += 0.01;
        points = 0;
        blasterCheck = 0;
        generateLevel();
        getSpawns();
        spawnAliens();
        spawnAsteroids();
        spawnPlayer(); //uwaga tu było placePlayer
        createBlastersList();

    }

    /**
     * Places the player in a level by choosing a spawn location from the spawns
     * ArrayList, removing the spawn position as it is used. The method sets the
     * players position in the level by calling its setPosition method with the
     * x and y values of the Point taken from the spawns ArrayList.
     */
    private void placePlayer() {

    }

    /**
     * Performs a single turn of the game when the user presses a key on the
     * keyboard. This method activates or deactivates pulsars periodically by
     * using the turn attribute, moves any aliens and asteroids and then checks
     * if the player is dead, exiting the game or resetting it. It checks if the
     * player has collected enough asteroids to win the level and calls the
     * method if it does. Finally it requests the GUI to redraw the game level
     * by passing it the tiles, player, aliens and asteroids for the current
     * level.
     */
    public void doTurn() {
        if (turnNumber % 20 == 0) {
            activatePulsars();
        }
        if (turnNumber % 20 == 5) {
            deactivatePulsars();
        }
        if (turnNumber % 10 == 5) {
            moveAsteroids();
        }
        moveBlasters();
        if (points % 5 == 0 && blasterCheck == 5) {
            fireBlaster();
            blasterCheck++;
        }                   
        moveAliens();
        if (player.getHullStrength() < 1) {
            System.exit(0);
        }
        pulsarDamage();
        if (cleared < 10 && points >= 5) {
            newLevel();
        }
        gui.updateDisplay(tiles, player, aliens, asteroids, blasters);
        turnNumber++;
        System.out.println(points);
        System.out.println(cleared);
    }

    /**
     * Starts a game. This method generates a level, finds spawn positions in
     * the level, spawns aliens, asteroids and the player and then requests the
     * GUI to update the level on screen using the information on tiles, player,
     * asteroids and aliens.
     */
    public void startGame() {
        tiles = generateLevel();
        spawns = getSpawns();
        asteroids = spawnAsteroids();
        aliens = spawnAliens();
        player = spawnPlayer();
        blasters = createBlastersList();
        gui.updateDisplay(tiles, player, aliens, asteroids, blasters);
    }
}

