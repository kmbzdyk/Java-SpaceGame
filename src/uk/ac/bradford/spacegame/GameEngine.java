/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.bradford.spacegame;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

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
     * A variable which counts how many moves did blasters make.
     */
    private int blastersCounter = 0;
    
    /**
     * A variable thanks to which blasters do not move in the same turn
     * in which they are fired.
     */
    private int blastersControl = 0;
    /**
     * The number of points the player has gained this level. Used to track when
     * the current level is won and a new one should be generated.
     */
    private int points = 0;

    /**
     * The array of Laser type objects.
     * Null values in this array are skipped during drawing and movement processing.
     */
    private Laser[] lasers;
    
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
     * size of this array uses the GRID_HEIGHT and GRID_WIDTH attributes
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
     * level. Elements in this array are of the type Alien, meaning that
     * an alien is alive and needs to be drawn or moved, or should be null which
     * means nothing is drawn or processed for movement. Null values in this
     * array are skipped during drawing and movement processing.
     */
    private Alien[] aliens;

    /**
     * An array of Asteroid objects that represents the asteroids in the current
     * level. Elements in this array are of the type Asteroid, meaning
     * that an asteroid exists and needs to be drawn or moved, or should be null
     * which means nothing is drawn or processed for movement. Null values in
     * this array are skipped during drawing and movement processing.
     */
    private Asteroid[] asteroids;
    
    /**
     * An array of Blaster objects that represents the blusters in the current
     * level. Elements in this array are of the type Blaster, meaning
     * that an blaster exists and needs to be drawn or moved, or should be null
     * which means nothing is drawn or processed for movement. Null values in
     * this array are skipped during drawing and movement processing.
     */
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
     * inactive pulsars. 
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
         * loop for black holes which takes random number for width and random for
         * height for tiles and checks if that tale is space, if no, loop search for
         * another tale, if yes loop puts black hole here, loop is executed
         * numberOfBHoles times
         */
        counter = 0;
        while (counter < numberOfBHoles) {
            randomIndex = (int) (rng.nextDouble() * GRID_WIDTH - 1);
            randomSecIndex = (int) (rng.nextDouble() * GRID_HEIGHT - 1);
            if (tiles[randomIndex][randomSecIndex] == TileType.SPACE) {
                tiles[randomIndex][randomSecIndex] = TileType.BLACK_HOLE;
                counter++;
            }
        }

        /**
         * loop for pulsars which takes random number for width and random for
         * height for tiles and checks if that tale is space, if no loop search
         * another tale, if yes loop puts pulsar here, loop is executed
         * numberOfPulsars times
         * number of active and inactive pulsars is random
         */
        counter = 0;
        while (counter < numberOfPulsars) {
            randomIndex = (int) (rng.nextDouble() * GRID_WIDTH - 1);
            randomSecIndex = (int) (rng.nextDouble() * GRID_HEIGHT - 1);
            if (tiles[randomIndex][randomSecIndex] == TileType.SPACE) {
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
     * Suitable tiles are added to the ArrayList that will be returned as
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
    
    /**
     * Creates array of Blaster type objects (of size 8) and sets each
     * of them to null.
     * @return An array of Blaster objects
     */
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
        int removeY;
        int randomIndex;
        int counter = 0;
        int astCounter = 0;
        int playerX;
        int playerY;
        Point alienPoint;
        aliens = new Alien[getSpawns().size()];
        Point[] tilesForAliens = new Point[getSpawns().size()];
        //loop makes all aliens equal to null
        //and adds all spawns to tilesForAliens array
        for (int i = 0; i < getSpawns().size(); i++) {
            aliens[i] = null;
            tilesForAliens[i] = getSpawns().get(i);
        }
        //loop creates Alien type objects (the amount specified by using cleared variable)
        while (counter < cleared + 2) {
            astCounter = 0;
            randomIndex = rng.nextInt(tilesForAliens.length);
            alienPoint = tilesForAliens[randomIndex];
            if (player != null) {
                playerX = player.getX();
                playerY = player.getY();
                if (aliens[randomIndex] == null && alienPoint != null) {
                    if (alienPoint.getX() != playerX || alienPoint.getY() != playerY) {
                        //loop checks if there is at least one asteroid in that position
                        for (int i = 0; i < asteroids.length; i++) {
                            if (asteroids[i] != null && asteroids[i].getX() == alienPoint.getX() && asteroids[i].getY() == alienPoint.getY()) {
                                astCounter++;
                            }
                        }
                        if (astCounter < 1) {
                            removeY = (int) alienPoint.getY();
                            //delete every position with that y, because it can be only 
                            //one alien in each row
                            for (int i = 0; i < tilesForAliens.length; i++) {
                                if (tilesForAliens[i] != null) {
                                    Point spawn = tilesForAliens[i];
                                    int spawnY = (int) spawn.getY();
                                    if (spawnY == removeY) {
                                        tilesForAliens[i] = null;
                                    }
                                }
                            }
                            Alien newAlien = new Alien(50, (int) alienPoint.getX(), (int) alienPoint.getY());
                            aliens[randomIndex] = newAlien;
                            counter++;
                        }
                    }
                }
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
        int randomTale = rng.nextInt(getSpawns().size());
        Point xPoint = getSpawns().get(randomTale);
        double x = xPoint.getX();
        double y = xPoint.getY();
        getSpawns().remove(randomTale);
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
     * player position.
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
                }
            }
        } else if ((playerX - 1) == -1 && tiles[GRID_WIDTH - 1][playerY] != TileType.BLACK_HOLE) {
            player.setPosition(GRID_WIDTH - 1, playerY);
            for (int i = 0; i < asteroids.length; i++) {
                if (asteroids[i] != null && asteroids[i].getX() == GRID_WIDTH - 1 && asteroids[i].getY() == playerY) {
                    asteroids[i] = null;
                    points++;
                }
            }
        } else {
            if (points > 0) {
                points--;
            }
        }
        //loop checks if there is any alien in the new location 
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
     * updates the player object's X and Y locations with the new position. 
     * If the tile to the left of the player is not empty the method will not update the
     * player position.
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
                }

            }
        } else if ((playerX + 1) == GRID_WIDTH && tiles[0][playerY] != TileType.BLACK_HOLE) {
            player.setPosition(0, playerY);
            for (int i = 0; i < asteroids.length; i++) {
                if (asteroids[i] != null && asteroids[i].getX() == 0 && asteroids[i].getY() == playerY) {
                    asteroids[i] = null;
                    points++;
                }

            }
        } else {
            if (points > 0) {
                points--;
            }
        }
        //loop checks if there is any alien in the new location 
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
     * player object's X and Y locations with the new position. 
     * If the tile to the left of the player is not empty the method will not update the
     * player position.
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
                }

            }
        } else if ((playerY - 1) == -1 && tiles[playerX][GRID_HEIGHT - 1] != TileType.BLACK_HOLE) {
            player.setPosition(playerX, GRID_HEIGHT - 1);
            for (int i = 0; i < asteroids.length; i++) {
                if (asteroids[i] != null && asteroids[i].getX() == playerX && asteroids[i].getY() == GRID_HEIGHT - 1) {
                    asteroids[i] = null;
                    points++;
                }

            }
        } else {
            if (points > 0) {
                points--;
            }
        }
        //loop checks if there is any alien in the new location 
        for (int i = 0; i < aliens.length; i++) {
            if (aliens[i] != null && aliens[i].getX() == playerX && aliens[i].getY() == playerY) {
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
     * player object's X and Y locations with the new position. 
     * If the tile to the left of the player is not empty the method will not update the
     * player position.
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
                }

            }
        } else if ((playerY + 1) == GRID_HEIGHT && tiles[playerX][0] != TileType.BLACK_HOLE) {
            player.setPosition(playerX, 0);
            for (int i = 0; i < asteroids.length; i++) {
                if (asteroids[i] != null && asteroids[i].getX() == playerX && asteroids[i].getY() == 0) {
                    asteroids[i] = null;
                    points++;
                }

            }
        } else {
            if (points > 0) {
                points--;
            }
        }
        //loop checks if there is any alien in the new location 
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
     * moveDirection value other than NONE have their position updated
     * accordingly, and if their new position puts them outside the map or
     * inside a black hole/any kind of a pulsar they are "destroyed". Destroyed asteroids are
     * replaced by creating a new, randomly positioned asteroid in the same
     * index of the asteroids array that the destroyed asteroid used to occupy.
     */
    private void moveAsteroids() {
        for (int i = 0; i < asteroids.length; i++) {
            if (asteroids[i] != null) {
                int asteroidX = asteroids[i].getX();
                int asteroidY = asteroids[i].getY();
                Asteroid.Direction direction = asteroids[i].getMovementDirection();
                int randomIndex = rng.nextInt(getSpawns().size());
                Point point = getSpawns().get(randomIndex);
                //switch statement for each direction
                switch (direction) {
                    case DOWN:
                        if (asteroidY == GRID_HEIGHT - 1) {
                            asteroids[i].setPosition((int) point.getX(), (int) point.getY());
                            getSpawns().remove(randomIndex);
                        } else {
                            asteroids[i].setPosition(asteroidX, asteroidY + 1);
                        }
                        break;
                    case UP:
                        if (asteroidY == 0) {
                            asteroids[i].setPosition((int) point.getX(), (int) point.getY());
                            getSpawns().remove(randomIndex);
                        } else {
                            asteroids[i].setPosition(asteroidX, asteroidY - 1);
                        }
                        break;
                    case RIGHT:
                        if (asteroidX == GRID_WIDTH - 1) {
                            asteroids[i].setPosition((int) point.getX(), (int) point.getY());
                            getSpawns().remove(randomIndex);
                        } else {
                            asteroids[i].setPosition(asteroidX + 1, asteroidY);
                        }
                        break;
                    case LEFT:
                        if (asteroidX == 0) {
                            asteroids[i].setPosition((int) point.getX(), (int) point.getY());
                            getSpawns().remove(randomIndex);
                        } else {
                            asteroids[i].setPosition(asteroidX - 1, asteroidY);
                        }
                        break;
                    case NONE:
                        break;
                }
                asteroidX = asteroids[i].getX();
                asteroidY = asteroids[i].getY();
                //checks if new location is SPACE
                if (tiles[asteroidX][asteroidY] == TileType.BLACK_HOLE || tiles[asteroidX][asteroidY] == TileType.PULSAR_ACTIVE || tiles[asteroidX][asteroidY] == TileType.PULSAR_INACTIVE) {
                    asteroids[i].setPosition((int) point.getX(), (int) point.getY());
                    getSpawns().remove(randomIndex);
                }
            }

        }
    }

    /**
     * Moves all aliens on the current level. The method checks for non-null
     * elements in the aliens array and calls the moveAlien method for each one
     * that is not null.
     * Method also calls aliensLasers() method if number of turns is even
     * and it turns off lasers if number of turns is odd.
     */
    private void moveAliens() {
        for (Alien newAlien : aliens) {
            if (newAlien != null) {
                moveAlien(newAlien);
            }
        }
        if (turnNumber % 2 == 0) {
            aliensLasers();
        }
        else {
            noLasers();
        }
    }

    /**
     * Moves a specific alien in the game. The method updates the X
     * attribute of the alien to reflect its new position 
     * (because each alien can move only in his row)
     *
     * @param a The Alien that needs to be moved
     */
    private void moveAlien(Alien a) {
        int playerX = player.getX();
        int playerY = player.getY();
        int alienX = a.getX();
        int alienY = a.getY();
        int counter = 0;
        boolean randomDirection = rng.nextBoolean();
        
        //If statement moves alien to the right or to the left - it depends on 
        //the variable randomDirection
        if (randomDirection == true && alienX < (GRID_WIDTH - 1) && tiles[alienX + 1][alienY] != TileType.BLACK_HOLE && tiles[alienX + 1][alienY] != TileType.PULSAR_ACTIVE && tiles[alienX + 1][alienY] != TileType.PULSAR_INACTIVE) {
            alienX++;
                if (alienX != playerX || alienY != playerY) {
                        a.setPosition(alienX, alienY);
                }
        }
        else if ((alienX > 0) && tiles[alienX - 1][alienY] != TileType.BLACK_HOLE && tiles[alienX - 1][alienY] != TileType.PULSAR_ACTIVE && tiles[alienX - 1][alienY] != TileType.PULSAR_INACTIVE) {
            alienX--;
                if (alienX != playerX || alienY != playerY) {
                        a.setPosition(alienX, alienY);
                }
        }

        //Loop iterates through asteroids array to check if any of them is in the same position
        //as alien. If yes, alien's health is increased by 10 and asteroid 
        //is moved to different position.
        for (Asteroid asteroid : asteroids) {
            if (asteroid != null && asteroid.getX() == alienX && asteroid.getY() == alienY) {
                counter = 0;
                while (counter < 1) {
                    int randomIndex = rng.nextInt(getSpawns().size());
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
     * Method iterates through aliens array and for each alien it adds
     * every tile to his right to lasers array until it is BLACK_HOLE.
     * @return An array of Laser type objects
     */
    private Laser[] aliensLasers() {
        int counter = 0;
        lasers = new Laser[GRID_WIDTH * GRID_HEIGHT];
        //loop makes every Laser object equal to null
        for (int i = 0; i < lasers.length; i++) {
            lasers[i] = null;            
        }
        for (int i = 0; i < aliens.length; i++) {
            if (aliens[i] != null) {
                int alienX = aliens[i].getX();
                int alienY = aliens[i].getY();
                for (int j = 1; j < 25 - alienX; j++) {
                    if (tiles[alienX + j][alienY] != TileType.BLACK_HOLE) {
                        Laser laser = new Laser(alienX + j, alienY);
                        lasers[counter] = laser;
                        counter++;
                    }
                    //If tile is equal to black hole, the loop is stopped.
                    else {
                        break;
                    }
                }
            }
        }
        //loop checks if player is on the same tile with Laser object,
        //if yes his health is decreased by 20.
        for (int i = 0; i < lasers.length; i++) {
            if (lasers[i] != null && lasers[i].getX() == player.getX() && lasers[i].getY() == player.getY()) {
                if (player.hullStrength > 20) {
                    player.hullStrength -= 20;
                } 
                else {
                    player.hullStrength = 0;
                }
            }      
        }
        return lasers;
    }
    
    /**
     * Method makes all Laser objects in array equal to null
     * @return An array of Laser type objects
     */
    private Laser[] noLasers() {
        for (int i = 0; i < lasers.length; i++) {
                lasers[i] = null;              
            }
        return lasers;
    }

    /**
     * Spawns asteroids in suitable locations in the current level. The method
     * uses the spawns ArrayList to pick suitable positions to add asteroids,
     * removing these positions from the spawns ArrayList as they are used
     * to avoid multiple entities spawning in the same location. 
     * The method creates asteroids by repeatedly instantiating
     * the Asteroid class and setting the X and Y position for the asteroid
     * using the Point object removed from the spawns ArrayList.
     *
     * @return An array of Asteroid objects representing the asteroids for the
     * current level
     */
    private Asteroid[] spawnAsteroids() {
        asteroids = new Asteroid[getSpawns().size()];
        int counter = 0;
        //loop makes every Asteroid object equal to null
        for (int i = 0; i < asteroids.length; i++) {
            asteroids[i] = null;
        }
        while (counter < asteroids.length / 10) {
            int randomIndex = rng.nextInt(asteroids.length);
            Point asteroidPoint = getSpawns().get(randomIndex);
            if (asteroids[randomIndex] == null) {
                getSpawns().remove(randomIndex);
                Asteroid newAsteroid = new Asteroid((int) asteroidPoint.getX(), (int) asteroidPoint.getY());
                asteroids[randomIndex] = newAsteroid;
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
     * found this way result in a reduce of player's strength by 5
     */
    private void pulsarDamage() {
        int playerX = player.getX();
        int playerY = player.getY();
        //loop checks player's position and every position around him
        for (int i = playerX - 1; i <= playerX + 1; i++) {
            for (int j = playerY - 1; j <= playerY + 1; j++) {
                //if statement checks if position is on the board
                if (i >= 0 && i < GRID_WIDTH && j >= 0 && j < GRID_HEIGHT) {
                    if (tiles[i][j] == TileType.PULSAR_ACTIVE) {
                        player.hullStrength -= 10;
                    }
                }
            }
        }
    }
    
    /**
     * The method checks if every blaster
     * is equal to null. If yes, that means there is no blaster on the board, so it calls 
     * fireBlaster() method
     */
    public void blastersOn() {
        int counter = 0;
        blastersControl = 1;
        for (int i = 0; i < blasters.length; i++) {
            if (blasters[i] == null) {
                counter++;
            }
            if (counter == blasters.length) {
                fireBlaster();
            }
        }
    }

    /**
     * Fills blasters array with the Blaster objects, each one with different
     * position and direction of movement.
     * @return An array of updates blasters
     */
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
        for (int i = 0; i < blasters.length; i++) {
            if (blasters[i] != null) {
                int blasterX = blasters[i].getX();
                int blasterY = blasters[i].getY();
                //if the blaster's position is the same as asteroid's position,
                //players's points are increased by 1
                for (int j = 0; j < asteroids.length; j++) {
                    if (asteroids[j] != null) {
                        if (asteroids[j].getX() == blasterX && asteroids[j].getY() == blasterY) {
                            asteroids[j] = null;
                            points++;
                        }
                    }
                }
                //if the blaster's position is the same as alien's position,
                //aliens's health is decreased by 30
                for (int k = 0; k < aliens.length; k++) {
                    if (aliens[k] != null) {
                        if (aliens[k].getX() == blasterX && aliens[k].getY() == blasterY) {
                            if (aliens[k].hullStrength >= 30) {
                                aliens[k].hullStrength -= 30;
                            } else {
                                aliens[k] = null;
                            }
                            blasters[i] = null;
                        }
                    }
                }
            }
        }
        return blasters;
    }

    /**
     * Moves each blaster depending on its direction of movement, only if new position is on the board
     * and is not occupied by BLACK_HOLE. Then checks if new location is occupied by asteroid. If yes, player's
     * points are increased by one, and asteroid is set to null.
     * Checks also if new location is occupied by alien. If yes, alien's health is 
     * decreased by 30.
     * 
     */
    private void moveBlasters() {
        int blasterX;
        int blasterY;
        for (int i = 0; i < blasters.length; i++) {
            if (blasters[i] != null) {
                blasterX = blasters[i].getX();
                blasterY = blasters[i].getY();
                //set new position for blaster depending on the direction of movement
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
                    //checks if there is any asteroid in the new location
                    //if yes, the asteroid disappears (is set to null)
                    //and player's points are increased by 1
                    for (int j = 0; j < asteroids.length; j++) {
                        if (asteroids[j] != null) {
                            if (asteroids[j].getX() == blasterX && asteroids[j].getY() == blasterY) {
                                asteroids[j] = null;
                                points++;
                            }
                        }
                    }
                    //checks if there is any alien in the new location
                    //if yes, it's life is decreased by 30, and the blaster disappears
                    for (int k = 0; k < aliens.length; k++) {
                        if (aliens[k] != null) {
                            if (aliens[k].getX() == blasterX && aliens[k].getY() == blasterY) {
                                if (aliens[k].hullStrength >= 30) {
                                    aliens[k].hullStrength -= 30;
                                } else {
                                    aliens[k] = null;
                                }
                                blasters[i] = null;
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
         * increases BLACK_HOLE_CHANCE and PULSAR_CHANCE by 0,01,
         * resets the value of points and the value of blastersCounter to zero,
         * generates a new level by calling the generateLevel method, 
         * fills lasers array with null values,
         * fills blasters array with null values, fills the spawns ArrayList with suitable
         * spawn locations, then spawns aliens and asteroids. Finally it places
         * the player in the new level by calling the placePlayer() method. 
         */
    private void newLevel() {
        cleared++;
        BLACK_HOLE_CHANCE += 0.01;
        PULSAR_CHANCE += 0.01;
        points = 0;
        blastersCounter = 0;
        generateLevel();
        getSpawns();
        spawnAsteroids();
        spawnAliens();
        createBlastersList();
        noLasers();
        placePlayer(); 

    }

    /**
     * Places the player in a level by choosing a spawn location from the spawns
     * ArrayList, removing the spawn position as it is used. The method sets the
     * players position in the level by calling its setPosition method with the
     * x and y values of the Point taken from the spawns ArrayList.
     */
    private void placePlayer() {
        int randomTale = rng.nextInt(getSpawns().size());
        Point xPoint = getSpawns().get(randomTale);
        int x = (int) xPoint.getX();
        int y = (int) xPoint.getY();
        getSpawns().remove(randomTale);
        player.setPosition(x, y);
    }

    /**
     * Performs a single turn of the game when the user presses a key on the
     * keyboard. This method activates or deactivates pulsars periodically by
     * using the turn attribute, moves blasters if they exist,
     * moves any aliens and asteroids and then checks
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
        moveAliens();
        if (blastersCounter < 5 && blastersControl >= 2) {
            moveBlasters();
            int counter = 0;
            for (int i = 0; i < blasters.length; i++) {
                if (blasters[i] != null) {
                    counter++;
                }
            }
            if (counter > 0) {
                blastersCounter++;
            }
        } 
        //blasters disappear after 5 moves
        else if (blastersCounter >= 5) {
            createBlastersList();
            blastersCounter = 0;
            blastersControl = 0;
        }
        if (player.getHullStrength() < 1) {
            System.exit(0);
        }
        pulsarDamage();
        if (cleared < 10 && points >= 10) {
            newLevel();
        }
        if (cleared >= 10) {
            System.exit(0);
        }
        gui.updateDisplay(tiles, player, aliens, asteroids, blasters, lasers);
        turnNumber++;
        blastersControl++;
        System.out.println("points" + points);
        System.out.println("level" + cleared);
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
        player = spawnPlayer();
        aliens = spawnAliens();
        blasters = createBlastersList();
        lasers = aliensLasers();
        gui.updateDisplay(tiles, player, aliens, asteroids, blasters, lasers);
    }
}

