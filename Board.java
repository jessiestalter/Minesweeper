import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

// class to implement the game board
public class Board {
    // variables for size of board & number of mines
    private int gridW;
    private int gridH;
    private int numMines;

    // array of buttons
    public JButton buttons[][];

    // arrays to represent the status of the board
    private int[][] mines;
    private boolean[][] flags;
    private boolean [][] revealed;

    public boolean isMines;
    public boolean isHalloween;

    // constructor, sets up the board
    public Board(int size, ActionListener AL, MouseListener ML) {
        gridW = size;
        gridH = size;
        setupBoard(gridW, gridH, AL, ML);
        isMines = true;
        isHalloween = false;
    }

    // function to set the size of the board
    public void setSize(int gridSize) {
        gridW = gridSize;
        gridH = gridSize;
    }

    // functions to return gridW & gridH
    public int getGridW () {
        return gridW;
    }
    public int getGridH () {
        return gridH;
    }

    // function to set the number of mines
    public void setNumMines(int numberMines) {
        numMines = numberMines;
    }

    // function to return the number of mines
    public int getNumMines() {
        return numMines;
    }

    // function to check if it is a mine
    public boolean checkIfMine(int x, int y) {
        if (mines[x][y] == 1)
            return true;
        else
            return false;
    }

    public void setIfMine(int x, int y, int bool) {
        mines[x][y] = bool;
    }

    // function to check if it is revealed
    public boolean checkIfRevealed(int x, int y) {
        if (revealed[x][y])
            return true;
        else
            return false;
    }

    // function to check if it is flagged
    public boolean checkIfFlagged(int x, int y) {
        if (flags[x][y])
            return true;
        else
            return false;
    }

    // function to toggle a flagged button
    public void toggleFlag(int x, int y) {
        if (flags[x][y]) {
            flags[x][y] = false;
            buttons[x][y].setIcon(null);
        }
        else {
            flags[x][y] = true;
            buttons[x][y].setIcon(new javax.swing.ImageIcon(getClass().getResource("res/flag.jpg")));
        }
    }

    // function to set up the board
    public void setupBoard(int gridW, int gridH, ActionListener AL, MouseListener ML) {
        // initialize array variables
        mines = new int[gridW][gridH];
        flags = new boolean[gridW][gridH];
        revealed = new boolean[gridW][gridH];
        flags = new boolean[gridW][gridH];
        buttons = new JButton[gridW][gridH];

        // place the mines
        // placeMines();

        int count = 1;
        for (int x = 0; x < gridW; x++) {
            for (int y = 0; y < gridH; y ++) {
                mines[x][y] = 0;
                flags[x][y] = false;
                revealed[x][y] = false;
                buttons[x][y] = new JButton();
                buttons[x][y].setPreferredSize(new Dimension(25,25));
                buttons[x][y].addActionListener(AL);
                buttons[x][y].addMouseListener(ML);
                buttons[x][y].setActionCommand(String.valueOf(count)); // give each a unique action command
                buttons[x][y].setBackground(Color.LIGHT_GRAY);
                buttons[x][y].setMargin(new Insets(0,0,0,0));
                count++;
            }
        }
    }

    // function to place the mines on the board
    public void placeMines(){
        int i = 0;
        while(i < numMines){
            int x = (int)(Math.random() * gridW);
            int y = (int)(Math.random() * gridH);
            while (mines[x][y] == 1)  { // if there is already a mine there
                x = (int)(Math.random() * gridW);
                y = (int)(Math.random() * gridH);
            }
            mines[x][y] = 1;
            i++;
        }
    }

    // function to get rid of mines
    public void clearMines() {
        for (int x = 0; x < gridW; x++) {
            for (int y = 0; y < gridH; y++) {
                mines[x][y] = 0;
            }
        }
    }

    // Function to fill a JPanel with buttons
    public void fillBoardView(JPanel panel) {

        for (int x = 0; x < gridW; x++) {
            for (int y = 0; y < gridH; y++) {
                panel.add(buttons[x][y]);
            }
        }
    }

    // function to return if an x,y pair is out of bounds
    public boolean outOfBounds(int x, int y) {
        return x < 0 || y < 0 || x >= gridW || y >= gridH;
    }

    // function to count the number of surrounding bombs to a x,y pair
    public int findSurroundingBombs(int x, int y) {
        int count = 0;
        if(outOfBounds(x,y))
            return 0;
        for (int ix = -1; ix <= 1; ix++) {
            for (int iy = -1; iy <= 1; iy++) {
                if(!outOfBounds(ix + x, iy + y))
                    count += mines[ix+x][iy+y];
            }
        }
        return count;
    }

    // function to reveal surrounding cells up until they have a bomb near them
    public void reveal(int x, int y) {
        if (outOfBounds(x,y))
            return;
        if (revealed[x][y] || mines[x][y] == 1)
            return;
        revealed[x][y] = true;
        if (findSurroundingBombs(x,y) == 0) {
            buttons[x][y].setBackground(Color.GRAY);
        }
        else if (findSurroundingBombs(x,y) == 1) {
            buttons[x][y].setBackground(Color.GRAY);
            buttons[x][y].setText("1");
            return;
        }
        else if (findSurroundingBombs(x,y) == 2) {
            buttons[x][y].setBackground(Color.GRAY);
            buttons[x][y].setText("2");
            return;
        }
        else if (findSurroundingBombs(x,y) == 3) {
            buttons[x][y].setBackground(Color.GRAY);
            buttons[x][y].setText("3");
            return;
        }
        else if (findSurroundingBombs(x,y) == 4) {
            buttons[x][y].setBackground(Color.GRAY);
            buttons[x][y].setText("4");
            return;
        }
        reveal(x-1,y-1);
        reveal(x-1,y+1);
        reveal(x+1,y-1);
        reveal(x+1,y+1);
        reveal(x-1,y);
        reveal(x+1,y);
        reveal(x,y-1);
        reveal(x,y+1);
    }


    // function to show all the bombs (after a user loses)
    public void showMines() {
        for (int x = 0; x < gridW; x++) {
            for (int y = 0; y < gridH; y++) {
                if (mines[x][y] == 1 && isMines) {
                    buttons[x][y].setIcon(new javax.swing.ImageIcon(getClass().getResource("res/bomb.jpeg")));
                    buttons[x][y].setBackground(Color.WHITE);
                }
                else if (mines[x][y] == 1 && isHalloween) {
                    buttons[x][y].setIcon(new javax.swing.ImageIcon(getClass().getResource("res/ghost.jpeg")));
                    buttons[x][y].setBackground(Color.WHITE);
                }
            }
        }
    }
}