import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// class to implement game play
public class Minesweeper extends JFrame implements MouseListener, ActionListener {

    // Resource loader
    private ClassLoader loader = getClass().getClassLoader();

    // get face images
    private Icon smileyFace = new ImageIcon(loader.getResource("res/smiley.jpg"));
    private Icon sadFace = new ImageIcon(loader.getResource("res/sadface.png"));

    // initialize board variable
    private Board board;

    // setup settings frame to make a new game
    private JFrame setupFrame;

    // Labels to display game info
    private JLabel timerLabel, minesLabel;
    private JLabel smileyFaceLabel; // label for smiley face

    private boolean isPlaying;

    // setup timer & task for timer
    private int secondsPassed = 0;
    ActionListener taskPerformer = new ActionListener() {
        public void actionPerformed(ActionEvent t) {
            secondsPassed++;
            timerLabel.setText("Time: " + Integer.toString(secondsPassed));
        }
    };
    Timer timer = new Timer(1000,taskPerformer);

    // Layout objects: Views of the board and the label area
    private JPanel boardView, labelView;

    // menu objects
    private JMenuBar menuBar;
    private JMenu Game;
    private JMenu Help;
    private JMenuItem New, Exit, HelpMenuItem;

    // sets up buttons for setup menu
    private JRadioButton minesButton;
    private JRadioButton ghostButton;
    private JButton newGame;
    private JRadioButton intermediate;
    private JRadioButton beginner;
    private JRadioButton expert;
    private JRadioButton custom;

    // text fields for user to put in custom size & number of mines
    JTextField sizeTextField;
    JTextField mineTextField;

    // boolean variables to be used later
    private boolean firstClick;

    // constructor
    // allows tha driver to assign a variable size and variable number of mines
    public Minesweeper(int initialGridSize, int initialNumMines) {
        super("Minesweeper"); // add title

        isPlaying = true;
        // give values to boolean variables
        firstClick = true;

        // Allocate the interface elements
        timerLabel = new JLabel("Time: 00");
        minesLabel = new JLabel("Mines: " + initialNumMines);

        timer.start(); // start timer

        // Allocate panels to hold interface
        labelView = new JPanel();  // used to hold labels
        boardView = new JPanel();  // used to hold game board

        int boardSize = initialGridSize * 30;
        boardView.setSize(new Dimension(boardSize,boardSize));

        // setup menu items
        menuBar = new JMenuBar();
        Game = new JMenu("Game");
        menuBar.add(Game);
        Help = new JMenu("Help");
        menuBar.add(Help);

        New = new JMenuItem("New Game");
        New.addActionListener(this);
        Game.add(New);

        Exit = new JMenuItem("Exit");
        Exit.addActionListener(this);
        Game.add(Exit);

        HelpMenuItem = new JMenuItem("Help");
        HelpMenuItem.addActionListener(this);
        Help.add(HelpMenuItem);

        // Get the content pane, onto which everything is eventually added
        Container c = getContentPane();

        // Setup the game board
        board = new Board(initialGridSize, this, this);

        // Add the game board to the board layout area
        boardView.setLayout(new GridLayout(initialGridSize, initialGridSize, 2, 2));

        // Add required interface elements to the "label" JPanel
        // labelView.setLayout(new GridLayout(1, 4, 2, 2));
        labelView.add(timerLabel);
        // add smiley face to the JLabel
        smileyFaceLabel = new JLabel();
        smileyFaceLabel.setIcon(smileyFace);
        labelView.add(smileyFaceLabel);

        labelView.add(minesLabel);

        // Both panels should now be individually laid out
        // Add panels to the container
        c.add(labelView, BorderLayout.NORTH);
        c.add(menuBar, BorderLayout.CENTER);
        c.add(boardView, BorderLayout.SOUTH);


        board.setNumMines(initialNumMines);
        board.placeMines();
        board.fillBoardView(boardView);

        setSize(boardSize, boardSize + 100);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // handle clicks
    public void actionPerformed(ActionEvent e) {
        // for menu items
        // if exit is clicked
        if (e.getSource().equals(Exit)) {
            System.exit(0);
        }

        // if new is clicked
        else if (e.getSource().equals(New)) {
            setupFrame = new JFrame("Setup New Game");
            setupFrame.setSize(170,190);
            createSetupFrame(setupFrame); // calls function to create the setup frame
            setupFrame.setLocationRelativeTo(null);
            setupFrame.setVisible(true);
        }

        // if help is clicked
        else if (e.getSource().equals(HelpMenuItem)) {
            // creates & displays a help menu
            JFrame helpFrame = new JFrame("Help Menu");
            helpFrame.setLocationRelativeTo(null);
            helpFrame.setSize(new Dimension(450,275));
            helpFrame.setVisible(true);
            JTextArea helpText = new JTextArea();
            helpText.setText("\n          Minesweeper is a logic game where mines are hidden in a grid of " +
                    "\n          squares. The object of this game is to open all safe squares in the " +
                    "\n          quickest time possible. Start by clicking a square on the board; " +
                    "\n          this will reveal a number or an opening surrounded by numbers. Each " +
                    "\n          number tells you how many mines touch the square. The game is won by " +
                    "\n          clearing all the safe squares and lost if a mine is clicked. To help " +
                    "\n          the player avoid hitting a mine, the location of a suspected mine can " +
                    "\n          be marked by flagging it by right clicking. There are three levels of " +
                    "\n          difficulty to choose from: beginner is a 4x4 grid with 4 mines, " +
                    "\n          intermediate is a 8x8 grid with 15 mines, and expert is a 12x12 grid " +
                    "\n          with 40 mines. There is also the ability to create custom levels. It is " +
                    "\n          also possible to switch the theme of the game from minesweeper to " +
                    "\n          Halloween, which changes the images of the mines.");
            helpText.setEditable(false);
            helpFrame.add(helpText);
        }

        // if beginner is selected
        else if (e.getSource().equals(beginner)) {
            board.setSize(4);
            board.setNumMines(4);
        }

        // if intermediate is selected
        else if (e.getSource().equals(intermediate)) {
            board.setSize(8);
            board.setNumMines(15);
        }

        // if expert is selected
        else if (e.getSource().equals(expert)) {
            board.setSize(12);
            board.setNumMines(40);
        }

        // if custom is selected
        else if (e.getSource().equals(custom)) {
            // make text fields editable
            sizeTextField.setEditable(true);
            mineTextField.setEditable(true);
        }

        // if new game is clicked
        else if (e.getSource().equals(newGame)) {
            restartGame();
            setupFrame.dispose();
        }

        // if mines is selected
        else if (e.getSource().equals(minesButton)) {
            board.isMines = true;
            board.isHalloween = false;
        }

        // if halloween is selected
        else if (e.getSource().equals(ghostButton)) {
            board.isHalloween = true;
            board.isMines = false;
        }

        // handles clicking on game buttons
        else {
            if (isPlaying) { // makes it so you can only click the buttons when game is in play
                int buttonLocation = Integer.parseInt(e.getActionCommand());

                // convert the action commands into x and y variables
                int x = 0, y = 0;
                for (int i = 0; i <= board.getGridW(); i++) {
                    if (buttonLocation > (board.getGridW() * i) && buttonLocation <= (board.getGridW() * (i + 1)))
                        x = i;
                }
                y = (board.getGridW() * (x + 1)) - buttonLocation;
                y = board.getGridW() - y;
                y = y - 1;

                // if first click is a mine, replace mines
                while (firstClick && board.checkIfMine(x, y)) {
                    board.clearMines();
                    board.placeMines();
                    board.checkIfMine(x,y);
                }
                firstClick = false;

                // if the button is not flagged and is a mine, game over
                if (board.checkIfMine(x, y) && !board.checkIfFlagged(x, y)) {
                    smileyFaceLabel.setIcon(sadFace);
                    board.showMines();
                    timer.stop();
                    isPlaying = false;
                }
                // if the button is not flagged, reveal
                else if (!board.checkIfFlagged(x, y))
                    board.reveal(x, y);

                // determine if the user has won
                boolean victory = false;
                for (int i = 0; i < board.getGridW(); i++) {
                    for (int j = 0; j < board.getGridH(); j++) {
                        if (board.checkIfMine(i, j) || board.checkIfRevealed(i, j))
                            victory = true;
                        else {
                            victory = false;
                            return;
                        }
                    }
                }

                // if the user has won, display the message
                if (victory) {
                    timer.stop();
                    isPlaying = false;
                    JOptionPane.showMessageDialog(null, "Congratulations! You won!");
                }
            }
        }
    }

    // handle right clicks (for flags)
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            JButton currButton = (JButton)e.getSource();
            int buttonLocation = Integer.parseInt(currButton.getActionCommand());

            // convert the action commands into x and y variables
            int x = 0, y = 0;
            for (int i = 0; i <= board.getGridW(); i++ ) {
                if (buttonLocation > (board.getGridW() * i) && buttonLocation <= (board.getGridW() * (i+1)))
                    x = i;
            }
            y = (board.getGridW() * (x+1)) - buttonLocation;
            y = board.getGridW() - y;
            y = y - 1;

            if (!board.checkIfRevealed(x,y)) {
                board.toggleFlag(x, y);

                if (board.checkIfFlagged(x, y)) {
                    board.setNumMines(board.getNumMines() - 1);
                } else {
                    board.setNumMines(board.getNumMines() + 1);
                }
                minesLabel.setText("Mines: " + board.getNumMines());
            }
        }
    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    // function to create the frame to setup a new game
    public void createSetupFrame(JFrame frame) {
        JPanel gameOptionsPanel = new JPanel();
        minesButton = new JRadioButton("Minesweeper Theme");
        ghostButton = new JRadioButton("Halloween Theme");

        minesButton.addActionListener(this);
        ghostButton.addActionListener(this);

        ButtonGroup optionsGroup = new ButtonGroup();
        optionsGroup.add(minesButton);
        optionsGroup.add(ghostButton);

        gameOptionsPanel.add(minesButton);
        gameOptionsPanel.add(ghostButton);

        JPanel radioButtonPanel = new JPanel();

        beginner = new JRadioButton("Beginner");

        intermediate = new JRadioButton("Intermediate");

        expert = new JRadioButton("Expert");

        custom = new JRadioButton("Custom");

        ButtonGroup group = new ButtonGroup();
        group.add(beginner);
        group.add(intermediate);
        group.add(expert);
        group.add(custom);

        beginner.addActionListener(this);
        intermediate.addActionListener(this);
        expert.addActionListener(this);
        custom.addActionListener(this);

        newGame = new JButton("New Game");
        newGame.addActionListener(this);

        // create labels
        JLabel blankLabel = new JLabel(" ");
        JLabel sizeLabel = new JLabel("Size");
        JLabel minesLabel = new JLabel("Mines");
        JLabel fourLabel = new JLabel("4");
        JLabel fourLabel2 = new JLabel("4");
        JLabel eightLabel = new JLabel("8");
        JLabel fifteenLabel = new JLabel("15");
        JLabel twelveLabel = new JLabel("12");
        JLabel fortyLabel = new JLabel("40");

        // create text fields & implement their action listeners
        sizeTextField = new JTextField();
        sizeTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int size = Integer.parseInt(sizeTextField.getText());
                board.setSize(size);
                if (size < 3 || size > 12) {
                    JOptionPane.showMessageDialog(null, "Size must be in the range 3-12");
                }
                else
                    board.setSize(size);
            }
        });
        mineTextField = new JTextField();
        mineTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int mines = Integer.parseInt(mineTextField.getText());
                int maxMines = (board.getGridW() * board.getGridH()) / 2;
                if (mines < 2 || mines > maxMines) {
                    JOptionPane.showMessageDialog(null, "Number of mines must be in the range 2 - " + maxMines);
                }
                else
                    board.setNumMines(mines);
            }
        });
        sizeTextField.setEditable(false);
        mineTextField.setEditable(false);

        // add elements to radio button panel
        radioButtonPanel.setLayout(new GridLayout(5,4));
        radioButtonPanel.add(blankLabel);
        radioButtonPanel.add(sizeLabel);
        radioButtonPanel.add(minesLabel);
        radioButtonPanel.add(beginner);
        radioButtonPanel.add(fourLabel);
        radioButtonPanel.add(fourLabel2);
        radioButtonPanel.add(intermediate);
        radioButtonPanel.add(eightLabel);
        radioButtonPanel.add(fifteenLabel);
        radioButtonPanel.add(expert);
        radioButtonPanel.add(twelveLabel);
        radioButtonPanel.add(fortyLabel);
        radioButtonPanel.add(custom);
        radioButtonPanel.add(sizeTextField);
        radioButtonPanel.add(mineTextField);

        // add elements to frame
        frame.add(gameOptionsPanel, BorderLayout.NORTH);
        frame.add(radioButtonPanel, BorderLayout.CENTER);
        frame.add(newGame, BorderLayout.SOUTH);
    }

    // function to restart the game
    public void restartGame()
    {
        isPlaying = true;

        smileyFaceLabel.setIcon(smileyFace);
        secondsPassed = 0;
        timer.start();
        firstClick = true;

        timerLabel.setText("Time: 00");
        minesLabel.setText("Mines: " + board.getNumMines());

        int boardSize = board.getGridW() * 30;
        boardView.setSize(new Dimension(boardSize, boardSize));
        this.setSize(boardSize, boardSize + 100);

        // Clear the boardView and have the gameBoard generate a new layout
        boardView.removeAll();
        boardView.revalidate();
        boardView.repaint();
        board.setupBoard(board.getGridW(), board.getGridH(), this, this);
        board.placeMines();
        boardView.setLayout(new GridLayout(board.getGridW(), board.getGridH(), 2, 2));
        board.fillBoardView(boardView);
    }

    // main function
    public static void main(String[] args) {
        Minesweeper M = new Minesweeper(8, 15);

        M.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { System.exit(0); }
        });
    }
}