package cs361.battleships.models;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BoardTest {

    @Test
    public void testDoubleattack(){
        Board board = new Board();
        board.attack(5, 'A');
        Result r = board.attack(5, 'A');
        assertEquals(AttackStatus.INVALID, r.getResult());
    }

    @Test
    public void testInvalidPlacement() {
        Board board = new Board();
        assertFalse(board.placeShip(new Minesweeper(), 11, 'C', true));
        assertFalse(board.placeShip(new Minesweeper(), 6, 'J', false));
    }
    @Test
    public void testPartialInvalidPlacement() { //tests if the ship will be rejected if it hangs off the edge
        Board board = new Board();
        assertFalse(board.placeShip(new Minesweeper(), 10, 'J', true));
        assertFalse(board.placeShip(new Minesweeper(), 6, 'J', false));
    }
    @Test
    public void testNormalPlacement() {
        Board board = new Board();
        assertTrue(board.placeShip(new Minesweeper(), 6, 'D', false));
    }
    @Test
    public void testOverlapPlacement() {
        Board board = new Board();
        assertTrue(board.placeShip(new Minesweeper(), 6, 'D', false));
        assertFalse(board.placeShip(new Destroyer(), 6, 'E', false));
    }
    @Test
    public void testShipTypes() {
        Board board = new Board();
        assertTrue(board.placeShip(new Minesweeper(), 1, 'A', false));
        assertTrue(board.placeShip(new Destroyer(), 2, 'A', false));
        assertTrue(board.placeShip(new Battleship(), 3, 'A', false));
    }

    @Test
    public void testEmptyShipsOnBoardByDefault() {
        // Verifies a board has no ships when first created
        Board board = new Board();
        assertEquals(0, board.getShips().size());
    }

    @Test
    public void testEmptyAttacksOnBoardByDefault() {
        // Verifies a board has no attacks when first created
        Board board = new Board();
        assertEquals(0, board.getAttacks().size());
    }

    @Test
    public void testAddingShip() {
        // Tests adding a single ship and getting it back from the board
        Board board = new Board();
        List<Ship> ships = new ArrayList<>();
        ships.add(new Minesweeper());
        board.setShips(ships);
        assertEquals(1, board.getShips().size());
    }

    @Test
    public void testAddingDuplicateShips() {
        // Tests adding a duplicate ship, which should return false
        Board b = new Board();
        // setup initial ships
        assertTrue(b.placeShip(new Minesweeper(), 1, 'A', false));
        assertTrue(b.placeShip(new Destroyer(), 2, 'A', false));
        assertTrue(b.placeShip(new Battleship(), 3, 'A', false));

        // test duplicating them, should all return false
        assertFalse(b.placeShip(new Minesweeper(), 4, 'A', false));
        assertFalse(b.placeShip(new Destroyer(), 5, 'A', false));
        assertFalse(b.placeShip(new Battleship(), 6, 'A', false));
    }

    @Test
    public void testAddingAttack() {
        // Tests adding a single attack and getting it back from the board
        Board board = new Board();
        List<Result> attacks = new ArrayList<>();
        attacks.add(new Result());
        board.setAttacks(attacks);
        assertEquals(1, board.getAttacks().size());
    }

    /**
     * Tests attacking invalid coordinates
     */
    @Test
    public void testAttackInvalid() {
        Board board = new Board();

        Result r = board.attack(-1,'A');
        assertEquals(AttackStatus.INVALID, r.getResult());

        r = board.attack(0,'A');
        assertEquals(AttackStatus.INVALID, r.getResult());

        r = board.attack(11,'A');
        assertEquals(AttackStatus.INVALID, r.getResult());
    }

    /**
     * Tests attack miss
     */
    @Test
    public void testAttackMiss() {
        Board board = new Board();
        Result r = board.attack(1,'A');
        assertEquals(AttackStatus.MISS, r.getResult());
    }

    /**
     * Tests attack hit
     */
    @Test
    public void testAttackHitAndSunk() {
        Board board = new Board();
        List<Ship> ships = new ArrayList<>();

        // create a new minesweeper to set
        Ship s = new Minesweeper();
        List<Square> spaces = new ArrayList<>();
        spaces.add(new Square(1,'A'));
        spaces.add(new Square(1,'B'));
        s.setOccupiedSquares(spaces);

        ships.add(s);

        board.setShips(ships);
        Result r = board.attack(1,'A');
        assertEquals(AttackStatus.HIT, r.getResult());
    }

    /**
     * Tests attack sunk
     */
    @Test
    public void testAttackSunk() {
        Board board = new Board();
        List<Ship> ships = new ArrayList<>();

        // add 3 ships
        for(int x = 0; x < 3; x++) {
            Ship s = new Minesweeper();
            List<Square> spaces = new ArrayList<>();
            spaces.add(new Square(x+1, 'A'));
            spaces.add(new Square(x+1, 'B'));
            s.setOccupiedSquares(spaces);
            ships.add(s);
        }

        board.setShips(ships);
        // sink the 2nd ship
        board.attack(1,'A');
        Result r = board.attack(1,'B');
        assertEquals(AttackStatus.SUNK, r.getResult());

        // check to see that the last 2 events were sunk, preceded by 1 hits
        // 2nd hit became a sunk automatically
        List<Result> attacks = board.getAttacks();
        assertEquals(3, attacks.size());
        assertEquals(AttackStatus.HIT, attacks.get(0).getResult());
        assertEquals(AttackStatus.SUNK, attacks.get(1).getResult());
        assertEquals(AttackStatus.SUNK, attacks.get(2).getResult());
    }

    @Test
    public void testCaptainwithArmor(){
        Board board = new Board();
        List<Ship> ships = new ArrayList<>();

        for(int x = 0; x < 3; x++){
            Ship s = new Battleship();
            Square captain = new Square(x+1, 'C');
            captain.setCaptainsQuarters(true);
            List<Square> spaces = new ArrayList<>();
            spaces.add(new Square(x+1, 'A'));
            spaces.add(new Square(x+1, 'B'));
            spaces.add(captain);
            spaces.add(new Square(x+1, 'D'));
            s.setOccupiedSquares(spaces);
            ships.add(s);
        }

        board.setShips(ships);
        // Attack the captain's quarters of the 2nd ship
        board.attack(1, 'C');
        Result r = board.attack(1, 'C');
        assertEquals(AttackStatus.SUNK, r.getResult());
    }

    @Test
    public void TestCaptainwithoutArmor(){
        Board board = new Board();
        List<Ship> ships = new ArrayList<>();

        for(int x = 0; x < 3; x++){
            Ship s = new Minesweeper();
            Square captain = new Square(x+1, 'A');
            captain.setCaptainsQuarters(true);
            List<Square> spaces = new ArrayList<>();
            spaces.add(new Square(x+1, 'B'));
            spaces.add(captain);
            s.setOccupiedSquares(spaces);
            ships.add(s);
        }

        board.setShips(ships);
        // Attack the captain's quarters of the 2nd ship
        Result r = board.attack(1, 'A');
        //Result r = board.attack(1, 'C');
        assertEquals(AttackStatus.SUNK, r.getResult());
    }

    @Test
    // Attacks a portion of the ship that was auto-sunk by the captain method
    public void TestAttackonCaptainSunk(){
        Board board = new Board();
        List<Ship> ships = new ArrayList<>();

        for(int x = 0; x < 3; x++){
            Ship s = new Minesweeper();
            Square captain = new Square(x+1, 'A');
            captain.setCaptainsQuarters(true);
            List<Square> spaces = new ArrayList<>();
            spaces.add(new Square(x+1, 'B'));
            spaces.add(captain);
            s.setOccupiedSquares(spaces);
            ships.add(s);
        }

        board.setShips(ships);
        // Attack the captain's quarters of the 2nd ship
        board.attack(1, 'A');
        Result r = board.attack(1, 'B');
        //Result r = board.attack(1, 'C');
        assertEquals(AttackStatus.INVALID, r.getResult());
    }

    /**
     * Tests attack surrender
     */
    @Test
    public void testAttackSurrender() {
        Board board = new Board();
        List<Ship> ships = new ArrayList<>();

        // create a new minesweeper to set
        Ship s = new Minesweeper();
        List<Square> spaces = new ArrayList<>();
        spaces.add(new Square(1,'A'));
        spaces.add(new Square(1,'B'));
        s.setOccupiedSquares(spaces);

        ships.add(s);

        board.setShips(ships);
        board.attack(1,'A');
        Result r = board.attack(1,'B');
        assertEquals(AttackStatus.SURRENDER, r.getResult());
    }

    /**
     * Tests default sonar values when a new board is created
     */
    @Test
    public void testSonarDefaults() {
        Board b = new Board();
        assertFalse(b.getSonarEnabled());
        assertEquals(2, b.getSonarCount());

        b.setSonarEnabled(true);
        b.setSonarCount(1);

        assertTrue(b.getSonarEnabled());
        assertEquals(1, b.getSonarCount());

    }

    @Test
    public void testSonarPulses() {
        Board b = new Board();
        b.setSonarEnabled(true);
        assertFalse(b.sonar(0,1));
        assertFalse(b.sonar(1,0));
        assertFalse(b.sonar(1,11));
        assertFalse(b.sonar(11,1));
        assertFalse(b.sonar(0,0));
        assertFalse(b.sonar(11,11));

        assertTrue(b.sonar(4, 3)); // 4,C

        // check that we were given 13 results
        List<Result> results = b.getAttacks();

        assertEquals(13, results.size());

        // check each set
        int x = 0;
        assertEquals(2, results.get(x).getLocation().getRow());
        assertEquals('C', results.get(x++).getLocation().getColumn());

        assertEquals(6, results.get(x).getLocation().getRow());
        assertEquals('C', results.get(x++).getLocation().getColumn());

        assertEquals(4, results.get(x).getLocation().getRow());
        assertEquals('A', results.get(x++).getLocation().getColumn());

        assertEquals(4, results.get(x).getLocation().getRow());
        assertEquals('E', results.get(x++).getLocation().getColumn());

        assertEquals(3, results.get(x).getLocation().getRow());
        assertEquals('B', results.get(x++).getLocation().getColumn());

        assertEquals(4, results.get(x).getLocation().getRow());
        assertEquals('B', results.get(x++).getLocation().getColumn());

        assertEquals(5, results.get(x).getLocation().getRow());
        assertEquals('B', results.get(x++).getLocation().getColumn());

        assertEquals(3, results.get(x).getLocation().getRow());
        assertEquals('C', results.get(x++).getLocation().getColumn());

        assertEquals(4, results.get(x).getLocation().getRow());
        assertEquals('C', results.get(x++).getLocation().getColumn());

        assertEquals(5, results.get(x).getLocation().getRow());
        assertEquals('C', results.get(x++).getLocation().getColumn());

        assertEquals(3, results.get(x).getLocation().getRow());
        assertEquals('D', results.get(x++).getLocation().getColumn());

        assertEquals(4, results.get(x).getLocation().getRow());
        assertEquals('D', results.get(x++).getLocation().getColumn());

        assertEquals(5, results.get(x).getLocation().getRow());
        assertEquals('D', results.get(x).getLocation().getColumn());


    }
}
