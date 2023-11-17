package presentation;

import domain.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class VintageGUI extends JFrame{
	
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
    
    //Pantalla principal
    private JPanel panelElements;
    private JPanel panelGamer1;
    private JLabel score1;
    //Patlla tablero
    private JButton startButton;
    private JPanel panelBoard;
    private JButton[][] boxes;
    private ImageIcon[] pictures;
    private boolean sizeButton;
    
    
    private JPanel panelGamer2;
    private JLabel score2;
    
	private Vintage vintage;
	
	private VintageGUI() {
		prepareElements();				//Vista
		prepareActions();				//Controlador
	}
	
	public static void main(String[] args) {
		VintageGUI gui =  new VintageGUI(); 
		gui.setVisible(true);
	}
	
	private void prepareElements() {
		setTitle("Vintage");	
        setSize(PREFERRED_DIMENSION);
        int x = (screenSize.width - WIDTH)/2;
        int y = (screenSize.height - HIGH)/2;
        setLocation(x,y);
        
        setLayout(new BorderLayout());
        setJMenuBar(prepareElementsMenu());
        panelElements = prepareElementsBoard();
        getContentPane().add(panelElements);
        
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
	
	private JPanel prepareElementsBoard() {
		pictures = new ImageIcon[Vintage.numGems];
		for (int i = 0; i < Vintage.numGems;i++) {
			String name = i+".png";
			pictures[i] = new ImageIcon("src/resources/images/"+name);
		}
		JPanel board =  new JPanel();
		board.setLayout(new BorderLayout());
		prepareElementsBoardStart();
		panelGamer1 = new JPanel();
		panelGamer2 = new JPanel();
		board.add(panelBoard, BorderLayout.CENTER);
		board.add(panelGamer1, BorderLayout.NORTH);
		board.add(panelGamer2, BorderLayout.SOUTH);
		return board;
	}
	
	private void prepareElementsBoardStart() {
		panelBoard = new JPanel();
		panelBoard.setBorder(new CompoundBorder(new EmptyBorder(WIDTH/64,WIDTH/32,WIDTH/64,WIDTH/32),
												 new TitledBorder("New game")));
		startButton =new JButton("Start new game");
		panelBoard.add(startButton);
	}
	
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
	
	private void prepareElementsBoardGameGamer1() {
		panelGamer1.removeAll();
		panelGamer1.setBorder(new CompoundBorder(new EmptyBorder(WIDTH/64,WIDTH/32,WIDTH/64,WIDTH/32),
												 new TitledBorder("Player one")));
		panelGamer1.add(new JLabel("Name player one: "+vintage.player1));
		score1 = new JLabel("Score player one: "+vintage.player1Score);
		panelGamer1.add(score1);
		panelGamer1.setBackground(backGroundColor);
	}
	
	private void prepareElementsBoardGameGamer2() {
		panelGamer2.removeAll();
		panelGamer2.setBorder(new CompoundBorder(new EmptyBorder(0,WIDTH/32,WIDTH/64,WIDTH/32),
				 new TitledBorder("Player two")));
		panelGamer2.add(new JLabel("Name player two: "+vintage.player2));
		score2 = new JLabel("Score player two: "+vintage.player2Score);
		panelGamer2.add(score2);
		panelGamer2.setBackground(backGroundColor);
	}

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
	
	private void prepareMenuActions() {
		menuNew.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				 String sizeS = JOptionPane.showInputDialog(null,"Give me the size of the game board");
				 Integer size = 0;
				 try {size = Integer.parseInt(sizeS);}catch(Exception ex) {System.out.println(ex.getMessage());}
				 String player1 = JOptionPane.showInputDialog(null,"Name player one: ");
				 String player2 = JOptionPane.showInputDialog(null,"Name player two: ");
				 vintage = new Vintage(size,player1,player2);
				 updatePanel();
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
					 updatePanel();
				 }
			 }
		 });
		 menuClose.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				 closeApp();
			 }
		 });
	}
	
	private void updatePanel() {
		prepareElementsBoardGameGamer1();
		prepareElementsBoardGameGamer2();
		prepareElementsBoardGame();
		prepareBoardGameActions();
		SwingUtilities.invokeLater(() -> {
			 panelElements.revalidate();
			 panelElements.repaint();
		});
	}
	
	private void start() {
		startButton.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				 String size = JOptionPane.showInputDialog(null,"Give me the size of the game board");
				 String player1 = JOptionPane.showInputDialog(null,"Name player one: ");
				 String player2 = JOptionPane.showInputDialog(null,"Name player two: ");
				 vintage = new Vintage(Integer.parseInt(size),player1,player2);
				 updatePanel();
				 
			 }
		 });
		
	}
	
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
	private void updateBoard() {
		int[][] gameStatus = vintage.getState();
		boolean[][] gameWin = vintage.getVisited();
		for (int i = 0; i < vintage.size ; i++) {
		    for (int j = 0; j < vintage.size ; j++) {
		    	boxes[i][j].setIcon(null);
		    	if (gameWin[i][j]) 
		    		boxes[i][j].setBackground(new Color(64, 64, 64)); 
		    	boxes[i][j].setIcon(pictures[gameStatus[i][j]]);
		    }
		}
		panelGamer1.remove(score1);
		score1 = new JLabel("Score player one: "+vintage.player1Score);
		panelGamer1.add(score1);
		panelGamer2.remove(score2);
		score2 = new JLabel("Score player two: "+vintage.player2Score);
		panelGamer2.add(score2);
	}
	
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

	private void recalculatePicturesSize(int w, int h) {
		for(int i = 0; i < Vintage.numGems;i++) {
			Image image = pictures[i].getImage();
			Image newImage = image.getScaledInstance(w,h,Image.SCALE_SMOOTH);
			pictures[i] = new ImageIcon(newImage);
		}
	}
}
	
	