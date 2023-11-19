package presentation;

import domain.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;


/**
 * VintageGUI class represents the graphical user interface for the Vintage game.
 * It extends the JFrame class and provides a window to interact with the game.
 */
public class VintageGUI extends JFrame{

    // Fields
    private final Color backGroundColor = new Color(106, 90, 205);
    private static final  Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static final int WIDTH = screenSize.width/2;
    private static final int HIGH =  screenSize.height/2;
    private static final Dimension PREFERRED_DIMENSION =
                         new Dimension(WIDTH,HIGH);
    
    private boolean lastMove;
    private int[] lastMovePos;
    
    //Menu
    private JMenuItem menuNew;
    private JMenuItem menuOpen;
    private JMenuItem menuSave;
    private JMenuItem menuClose;
    
    //Main screen
    private JPanel panelElements;
    private JPanel panelGamer1;
    private JLabel score1;
    
    //Game board
    private JButton startButton;
    private JPanel panelBoard;
    private JButton[][] boxes;
    private ImageIcon[] pictures;
    private boolean sizeButton;
    
    
    private JPanel panelGamer2;
    private JLabel score2;
    private Vintage vintage;


	/**
     	* Constructor for VintageGUI class. Initializes the GUI components.
     	*/
	private VintageGUI() {
		prepareElements();				//Vista
		prepareActions();				//Controlador
	}


	
	/**
        * The entry point for the VintageGUI application.
        * @param args Command-line arguments (not used in this application).
        */
	public static void main(String[] args) {
		VintageGUI gui =  new VintageGUI(); 
		gui.setVisible(true);
	}


	/**
        * Prepares the initial state of the GUI, including the frame title, size, menu bar, and game board.
        */
	private void prepareElements() {
		setTitle("Vintage");	
        setSize(PREFERRED_DIMENSION);
        int x = (screenSize.width - WIDTH)/2;
        int y = (screenSize.height - HIGH)/2;
        setLocation(x,y);
        pictures = new ImageIcon[Vintage.numGems];
		for (int i = 0; i < Vintage.numGems;i++) {
			String name = i+".png";
			pictures[i] = new ImageIcon("src/resources/images/"+name);
		}
        setJMenuBar(prepareElementsMenu());   
        prepareElementsBoard();
	}
	
	private JMenuBar prepareElementsMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu gameMenu = new JMenu("Menu");
		
		menuNew = new JMenuItem("New");
		menuOpen = new JMenuItem("Open");
		menuSave = new JMenuItem("Save");
		menuClose = new JMenuItem("Close");
		
		gameMenu.add(menuNew);
		gameMenu.add(menuOpen);
		gameMenu.add(menuSave);
		gameMenu.addSeparator();
		gameMenu.add(menuClose);
		
		menuBar.add(gameMenu);
		menuBar.setBorderPainted(true);
		menuBar.setBackground(backGroundColor);
		return menuBar;
		
	}
	
	/**
        * Prepares the menu bar with menu items and sets up their action listeners.
        * @return JMenuBar object containing the prepared menu.
        */
	private void prepareElementsBoard() {
		JPanel board =  new JPanel();
		board.setLayout(new BorderLayout());
		prepareElementsBoardStart();
		board.add(panelBoard, BorderLayout.CENTER);
		panelElements = board;
		getContentPane().add(panelElements);
	}


	/**
        * Prepares the initial game board layout for the start of a new game.
        */
	private void prepareElementsBoardInGame() {
		getContentPane().removeAll();
		JPanel board =  new JPanel();
		board.setLayout(new BorderLayout());
		panelGamer1 = new JPanel();
		panelGamer2 = new JPanel();
		panelBoard = new JPanel();
		board.add(panelBoard, BorderLayout.CENTER);
		board.add(panelGamer1, BorderLayout.NORTH);
		board.add(panelGamer2, BorderLayout.SOUTH);
		panelElements = board;
		getContentPane().add(panelElements);
	}
	
	
	/**
        * Prepares the game board layout during an active game.
        */
	private void prepareElementsBoardStart() {
		panelBoard = new JPanel();
		panelBoard.setBorder(new CompoundBorder(new EmptyBorder(WIDTH/64,WIDTH/32,WIDTH/64,WIDTH/32),
												 new TitledBorder("New game")));
		startButton =new JButton("Start new game");
		panelBoard.add(startButton);
	}


 	/**
     	* Prepares the game board layout during an active game.
     	*/
	private void prepareElementsBoardGame() {
		panelBoard.removeAll();
		CompoundBorder border = new CompoundBorder(new EmptyBorder(0,WIDTH/4,0,WIDTH/4),
				new TitledBorder(""));
		panelBoard.setBorder(border);
		panelBoard.setLayout(new GridLayout(vintage.size,vintage.size,5,5));
		boxes = new JButton[vintage.size][vintage.size];
		sizeButton=false;
		lastMove=false;
		for (int i = 0; i < vintage.size ; i++) {
		    for (int j = 0; j < vintage.size ; j++) {
		    	boxes[i][j] = new JButton();
		    	panelBoard.add(boxes[i][j]);
		    	boxes[i][j].setBackground(new Color(188, 143, 143));
		    	int x = i; int y = j;
		    	if (!sizeButton) {
		    		SwingUtilities.invokeLater(() -> {
		    			System.out.println(boxes[x][y].getWidth()+" "+boxes[x][y].getHeight());
		    			recalculatePicturesSize(boxes[x][y].getWidth(),boxes[x][y].getHeight());});
		    		sizeButton=true;
		    	} 
		    }
		}
		SwingUtilities.invokeLater(() -> {updateBoard();});
		panelBoard.setBackground(backGroundColor);
	}
	
	
	
	
	/**
     	* Prepares the game board layout for player one during an active game.
     	*/
	private void prepareElementsBoardGameGamer1() {
		panelGamer1.removeAll();
		panelGamer1.setBorder(new CompoundBorder(new EmptyBorder(WIDTH/64,WIDTH/32,WIDTH/64,WIDTH/32),
												 new TitledBorder("Player one")));
		panelGamer1.add(new JLabel("Name player one: "+vintage.player1));
		score1 = new JLabel("Score player one: "+vintage.player1Score);
		panelGamer1.add(score1);
		panelGamer1.setBackground(backGroundColor);
	}
	
	
	/**
     	* Prepares the game board layout for player two during an active game.
     	*/
	private void prepareElementsBoardGameGamer2() {
		panelGamer2.removeAll();
		panelGamer2.setBorder(new CompoundBorder(new EmptyBorder(0,WIDTH/32,WIDTH/64,WIDTH/32),
				 new TitledBorder("Player two")));
		panelGamer2.add(new JLabel("Name player two: "+vintage.player2));
		score2 = new JLabel("Score player two: "+vintage.player2Score);
		panelGamer2.add(score2);
		panelGamer2.setBackground(backGroundColor);
	}


	/**
     	* Prepares the actions for menu items, start button, and game board buttons.
     	*/
	private void prepareActions() {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		 addWindowListener(new WindowAdapter() {
			 public void windowClosing(WindowEvent ev){
				 closeApp();
			 }
		 });
		 prepareMenuActions();
		 start();
	}
	
	
	/**
     	* Prepares the action listeners for menu items, such as creating a new game, saving, opening, and closing the application.
     	*/
	private void prepareMenuActions() {
		menuNew.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				 String size = JOptionPane.showInputDialog(null,"Give me the size of the game board");
				 String player1 = JOptionPane.showInputDialog(null,"Name player one: ");
				 String player2 = JOptionPane.showInputDialog(null,"Name player two: ");
				 try {
					 vintage = new Vintage(Integer.parseInt(size),player1,player2);
				 }
				 catch(Exception ex) {
					 vintage = new Vintage(8,player1,player2);
				 }
				 prepareElementsBoardInGame();
				 refresh();
			 }
		 });
		menuSave.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				 JFileChooser chooser = new JFileChooser();
				 FileNameExtensionFilter filter = new FileNameExtensionFilter(
					        "Vintage data", "dat");
				 chooser.setFileFilter(filter);
				 int returnVal = chooser.showSaveDialog(null);
				 if(returnVal == JFileChooser.APPROVE_OPTION) {
					 JOptionPane.showMessageDialog(null, 
							 		 "You are trying to save the game with the next name: " 
									 + chooser.getSelectedFile().getName());
					 vintage.guardarPartida(chooser.getSelectedFile().getAbsolutePath()+".dat");
				 }
			 }
		 });		
		 menuOpen.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				 JFileChooser chooser = new JFileChooser();
				 FileNameExtensionFilter filter = new FileNameExtensionFilter(
						 "Vintage data", "dat");
				 chooser.setFileFilter(filter);
				 int returnVal = chooser.showOpenDialog(null);
				 if(returnVal == JFileChooser.APPROVE_OPTION) {
					 JOptionPane.showMessageDialog(null, 
							 		 "You are trying to open the file with the next name: " 
									 + chooser.getSelectedFile().getName());
					 vintage = vintage.cargarPartida(chooser.getSelectedFile().getAbsolutePath());
					 refresh();
				 }
			 }
		 });
		 menuClose.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				 closeApp();
			 }
		 });
	}
	
	
	/**
     	* Refreshes the GUI components to reflect the current state of the game.
     	*/
	private void refresh() {
		prepareElementsBoardGameGamer1();
		prepareElementsBoardGameGamer2();
		prepareElementsBoardGame();
		prepareBoardGameActions();
		SwingUtilities.invokeLater(() -> {
			 panelElements.revalidate();
			 panelElements.repaint();
		});
	}
	
	
	/**
     	* Initiates the start button action to begin a new game.
     	*/
	private void start() {
		startButton.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				 String size = JOptionPane.showInputDialog(null,"Give me the size of the game board");
				 String player1 = JOptionPane.showInputDialog(null,"Name player one: ");
				 String player2 = JOptionPane.showInputDialog(null,"Name player two: ");
				 try {
					 vintage = new Vintage(Integer.parseInt(size),player1,player2);
				 }
				 catch(Exception ex) {
					 vintage = new Vintage(8,player1,player2);
				 }
				 prepareElementsBoardInGame();
				 refresh();
			 }
		 });
		
	}


	/**
     	* Prepares the action listeners for the game board buttons.
    	 */
	private void prepareBoardGameActions() {
		if (vintage == null)return;
		for (int i = 0; i < vintage.size ; i++) {
		    for (int j = 0; j < vintage.size ; j++) {
		    	int [] pos = {i,j};
		        boxes[i][j].addActionListener(new ActionListener() {
					 public void actionPerformed(ActionEvent e) {
						 if (lastMove) {
							boolean validity=vintage.play(pos,lastMovePos);
							if (validity) 
								updateBoard();
							else {
								Timer timer = new Timer(1000, new ActionListener() {
				                @Override
				                public void actionPerformed(ActionEvent evt) {
				                        JOptionPane.getRootFrame().dispose();
				                    }
				                });
				                timer.setRepeats(false);
				                timer.start();
				                JOptionPane.showMessageDialog(null, "JUGADA INVALIDA");
				                timer.restart();
							}
							lastMove=false;
						 }
						 else {
							 lastMove = true;
							 lastMovePos = pos; 
						 }
					 }
				 });
		    }
		}
		
	}


	/**
     	* Updates the game board GUI based on the current state of the game.
    	*/
	private void updateBoard() {
		/*
		 * while (!vintage.nextStates.isEmpty()) {
			int[][] gameStatus = vintage.nextStates.poll();
			for (int i = 0; i < vintage.size ; i++) {
			    for (int j = 0; j < vintage.size ; j++) {
			    	System.out.print(gameStatus[i][j]+" ");
			    	boxes[i][j].setIcon(null);
			    	boxes[i][j].setIcon(pictures[gameStatus[i][j]]);
			    }System.out.println();	
			}
			System.out.println();
			SwingUtilities.invokeLater(() -> {
				 panelElements.revalidate();
				 panelElements.repaint();
			});
			try {
	            Thread.sleep(100);
	            
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
		}*/
		int[][] gameStatus = vintage.getState();
		boolean[][] gameWin = vintage.getVisited();
		for (int i = 0; i < vintage.size ; i++) {
		    for (int j = 0; j < vintage.size ; j++) {
		    	boxes[i][j].setIcon(null);
		    	boxes[i][j].setIcon(pictures[gameStatus[i][j]]);
		    	if (gameWin[i][j]) 
		    		boxes[i][j].setBackground(new Color(64, 64, 64)); 
		    }
		}
		panelGamer1.remove(score1);
		score1 = new JLabel("Score player one: "+vintage.player1Score);
		panelGamer1.add(score1);
		panelGamer2.remove(score2);
		score2 = new JLabel("Score player two: "+vintage.player2Score);
		panelGamer2.add(score2);
	}
	
	/**
     	* Closes the application, prompting the user for confirmation.
     	*/
	private void closeApp() {
		int yesNo = JOptionPane.showOptionDialog(null,"Are you sure you want exit?","Warning",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.CANCEL_OPTION,
					null,
					null,"No");
		if (yesNo == JOptionPane.YES_OPTION) {
			setVisible(false);
			System.exit(0);
		}
	}

	/**
     	* Recalculates the size of the gem pictures based on the specified width and height.
     	* @param w The new width for the gem pictures.
     	* @param h The new height for the gem pictures.
     	*/
	private void recalculatePicturesSize(int w, int h) {
		for(int i = 0; i < Vintage.numGems;i++) {
			Image image = pictures[i].getImage();
			Image newImage = image.getScaledInstance(w,h,Image.SCALE_SMOOTH);
			pictures[i] = new ImageIcon(newImage);
		}
	}
}
	
	
