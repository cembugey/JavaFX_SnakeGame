
import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


/*NOTES:
	* The snake gets faster with every meal
	* Corners are the squares of which the snake consists
	* The enum 'Dir' contains the directions for the controls
	* I create a VBox and a Canvas as background. GraphicContext to paint the snake
	* The Animation Timer is our flip book
	* 1 Mio ticks = i get a new frame every 'speed' second. More speed = more frames = faster snake!
	* Game Over if the snake touches a border

*/
public class Snake extends Application {
	
	private int speed = 5;    // Speed of the snake
	private int foodcolor = 0;     // Color of the food
	private int width = 20;
	private int height = 20;
	private int foodX = 0;    // x coordinate of the food
	private int foodY = 0;    // y coordinate of the food
	private int cornersize = 25;
	private ArrayList<Cell> snake = new ArrayList<>();  // Cells are the units of the snake
	private Direction direction = Direction.left;
	private boolean gameOver = false;
	private MenuBar mBar;

	// Menu item to add to File menu
	private MenuItem fileNewGameItem;
	
	
	private boolean boostFlag = false;
	private boolean pauseFlag = false;
	
	private long startTime;
	
	
	private int countBoost = 0;     // Count how many times snake got speed boost 
	private int countFood = 0;		// Count how many times snake ate food

	public enum Direction {  // The enum 'Direction' contains the directions for the controls
		left, right, up, down
	}
	

	public static class Cell {
		int x;
		int y;

		public Cell(int x, int y) {
			this.x = x;
			this.y = y;
		}

	}
	
	@Override
	public void init() {
		
		mBar = new MenuBar();
		// File menu
		Menu fileMenu = new Menu("File");
		// Menu item to add to File menu
		fileNewGameItem = new MenuItem("New Game");
		fileMenu.getItems().add(fileNewGameItem);

		
		// Exit item
		MenuItem fileExitItem = new MenuItem("Exit");
		fileMenu.getItems().add(fileExitItem);
		//Handle click events on the exit menu item.
		fileExitItem.setOnAction(ae -> Platform.exit());
		
		mBar.getMenus().add(fileMenu);
		
		// Help menu
		Menu fileHelp = new Menu("Help");
		// Menu item to add to Help menu
		MenuItem fileControlsItem = new MenuItem("Controls");
		fileHelp.getItems().add(fileControlsItem);
		
		//Handle click events on the About menu item
		fileControlsItem.setOnAction(ae -> {
			// show name and student number in a dialog
			showDialog();					
			});  
				
		mBar.getMenus().add(fileHelp);
		
	}

	public void start(Stage primaryStage) {
		try {
			newFood();
			// Created a VBox and a Canvas as background. GraphicContext to paint the snake
			VBox root = new VBox();
			Canvas c = new Canvas(width * cornersize, height * cornersize);
			GraphicsContext gc = c.getGraphicsContext2D();
			fileNewGameItem.setOnAction(ae -> { 
				newGame();	
				});
			root.getChildren().add(mBar);
			root.getChildren().add(c);
			
			

			new AnimationTimer() {  
				long lastTick = 0;
				
				public void handle(long now) {
										
					if (lastTick == 0) {
						lastTick = now;
						tick(gc);
						return;
					}
					try {
					// 1 Mio ticks = i get a new frame every 'speed' second. More speed = more frames = faster snake!
					if (now - lastTick > 1000000000 / speed) {
						lastTick = now;
						tick(gc);
					}
					}
					catch(Exception e) {
						
					}
				}
				
				
			}.start();

			Scene scene = new Scene(root, width * cornersize, height * cornersize);

			// Set the Keyboard Controls
			scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
				if (key.getCode() == KeyCode.W) {
					direction = Direction.up;
				}
				if (key.getCode() == KeyCode.A) {
					direction = Direction.left;
				}
				if (key.getCode() == KeyCode.S) {
					direction = Direction.down;
				}
				if (key.getCode() == KeyCode.D) {
					direction = Direction.right;
				}
				// SPEED BOOST
				if (key.getCode() == KeyCode.C) {
					boostSnake(gc);
				}
				// PAUSE/RESUME GAME
				if (key.getCode() == KeyCode.P) {
					pause(gc);
				}
				// RESTART GAME
				if (key.getCode() == KeyCode.R) {
					newGame();
				}
				// EXIT GAME
				if (key.getCode() == KeyCode.ESCAPE) {
					Platform.exit();
				}

			});
			// Set the snake to 3 parts (Corners) at the beginning
			// add start snake parts
			snake.add(new Cell(width / 2, height / 2));
			snake.add(new Cell(width / 2, height / 2));
			snake.add(new Cell(width / 2, height / 2));
			
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("SNAKE GAME");
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// tick
	public void tick(GraphicsContext gc) {
		if (gameOver) {    // Fill 'Game Over' in RED in size 50 on position 100, 250
			gc.setFill(Color.WHITE);
			gc.setFont(Font.font ("Courier New", 50));
			gc.fillText("GAME OVER", 100, 250);
			gc.setFont(Font.font ("Courier New", 20));
			gc.fillText("Press R to restart", 125, 280);
			return;
		}		
		else {
			gc.fillText("", 100, 250);     //ADDED!!!!
		}
		
		if (pauseFlag) {
			gc.setFill(Color.WHITE);
			gc.setFont(new Font("", 30));
			gc.fillText("Paused", 400, 470);
		}
		

		for (int i = snake.size() - 1; i >= 1; i--) {
			snake.get(i).x = snake.get(i - 1).x;
			snake.get(i).y = snake.get(i - 1).y;
		}
		// Choose the direction of the snake
		switch (direction) {
		case up:
			snake.get(0).y--;
			if (snake.get(0).y < 0) {
				gameOver = true;
			}
			break;                                 // Game Over if the snake touches a border
		case down:
			snake.get(0).y++;
			if (snake.get(0).y > height) {
				gameOver = true;
			}
			break;
		case left:
			snake.get(0).x--;
			if (snake.get(0).x < 0) {
				gameOver = true;
			}
			break;
		case right:
			snake.get(0).x++;
			if (snake.get(0).x > width) {
				gameOver = true;
			}
			break;

		}

		// eat food
		if (foodX == snake.get(0).x && foodY == snake.get(0).y) {
			snake.add(new Cell(-1, -1));   // Let the snake grow
			newFood();
		}

		// self destroy
		for (int i = 1; i < snake.size(); i++) {   
			if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {
				gameOver = true;    // Game Over if the snake hit the snake
			}
		}

		// fill background
		gc.setFill(Color.BLACK);     // Fill the background in black
		gc.fillRect(0, 0, width * cornersize, height * cornersize);

		// score
		gc.setFill(Color.WHITE);
		gc.setFont(new Font("", 30));
		gc.fillText("Score: " + (speed - 6 - countBoost * 5), 370, 25);   // Fill the score in white with standard font on position 10,3
		
		
		// random food color
		Color cc = Color.WHITE;

		switch (foodcolor) {
		case 0:
			cc = Color.PURPLE;
			break;
		case 1:
			cc = Color.LIGHTBLUE;
			break;
		case 2:
			cc = Color.YELLOW;
			break;
		case 3:
			cc = Color.PINK;
			break;
		case 4:
			cc = Color.ORANGE;
			break;
		}
		gc.setFill(cc);
		gc.fillOval(foodX * cornersize, foodY * cornersize, cornersize, cornersize);

		if (boostFlag) {
		// snake
		for (Cell c : snake) {    // Paint the snake with random colors
			gc.setFill(randomColor());
			gc.fillOval(c.x * cornersize, c.y * cornersize, cornersize - 7, cornersize - 7);
		}
		Cell head = snake.get(0);
		gc.fillRoundRect(head.x * cornersize, head.y * cornersize, cornersize - 2, cornersize - 2, 5, 5);
		}
		
		else {  // Paint the snake
			for (Cell c : snake) {    
				gc.setFill(Color.LIMEGREEN);
				gc.fillOval(c.x * cornersize, c.y * cornersize, cornersize - 7, cornersize - 7);
			}
			Cell head = snake.get(0);
			gc.fillRoundRect(head.x * cornersize, head.y * cornersize, cornersize - 2, cornersize - 2, 5, 5);
		}

	}
	
	
	public void pause(GraphicsContext gc) {
		int tempSpeed = 5 + countBoost * 5 + countFood;
		if (!pauseFlag) {
			pauseFlag = true;
			gc.setFill(Color.WHITE);
			gc.setFont(new Font("", 30));
			gc.fillText("Paused", 400, 470);
			speed = 0;
			return;
		}
		else {
			pauseFlag = false;
			speed = tempSpeed;
			gc.setFill(Color.WHITE);
			gc.setFont(new Font("", 30));
			gc.fillText("", 400, 470);
			return;
		}
	}
	
	public void boostSnake(GraphicsContext gc) {
		
		
		if (!boostFlag) {
			boostFlag = true;
			speed += 5;
			countBoost++;
			startTime = System.currentTimeMillis();
		}
		else {
			boostFlag = false;
			speed -= 5;
			countBoost--;
		}
	}
	
	public static Color randomColor() {
		int color = (int)(Math.random() * 10);
		
		switch (color) {
		case 0:
			return Color.PURPLE;
		case 1:
			return Color.BLUE;	
		case 2:
			return Color.YELLOW;
		case 3:
			return Color.RED;
		case 4:
			return Color.ORANGE;
		case 5:
			return Color.WHITE;
		case 6:
			return Color.GOLD;
		case 7:
			return Color.GRAY;
		case 8:
			return Color.GREEN;
		default:
			return Color.BROWN;
		}
	}
	
	
	// I place a new food on random location foodX * foodY on the canvas(if there is no snake)
	// food
	public void newFood() {
		start: while (true) {
			foodX = (int)(Math.random() * width);
			foodY = (int)(Math.random() * height);

			for (Cell c : snake) {
				if (c.x == foodX && c.y == foodY) {
					continue start;
				}
			}
			// Choose a new color and increase the speed
			foodcolor = (int)(Math.random() * 5);
			countFood++;
			speed++;
			break;

		}
	}
	
	public void newGame() {
			gameOver = false;
			boostFlag = false;
			newFood();
			speed = 6;
			countBoost = 0;
			snake.clear();
			snake.add(new Cell(width / 2, height / 2));
			snake.add(new Cell(width / 2, height / 2));
			snake.add(new Cell(width / 2, height / 2));	
	}
	
	// A methods that opens a dialog(after clicking 'Controls')
	public void showDialog(){
			// Create a new stage
			Stage dialog = new Stage();
			// Set the width and height
			dialog.setWidth(200);
			dialog.setHeight(380);
			dialog.setTitle("Controls");
			
			dialog.setMaximized(false);
			dialog.setAlwaysOnTop(true);
								
			
			Label lblButtons = new Label("Buttons");
			Label lblUp = new Label("                    UP");
			Label lblLeft = new Label("                 LEFT");
			Label lblDown = new Label("             DOWN");
			Label lblRight = new Label("              RIGHT");
			Label lblBoost = new Label("             BOOST");
			Label lblPause = new Label("PAUSE/RESUME");
			Label lblRestart = new Label("          RESTART");
			
			TextField txtfW = new TextField("    W");
			TextField txtfA = new TextField("    A");
			TextField txtfS = new TextField("    S");
			TextField txtfD = new TextField("    D");
			TextField txtfC = new TextField("    C");
			TextField txtfV = new TextField("    V");
			TextField txtfR = new TextField("    R");
			
			txtfW.setMaxWidth(50);
			txtfA.setMaxWidth(50);
			txtfS.setMaxWidth(50);
			txtfD.setMaxWidth(50);
			txtfC.setMaxWidth(50);
			txtfV.setMaxWidth(50);
			txtfR.setMaxWidth(50);
					
			// Button of the dialog
			Button btnOK = new Button("OK");  

			// Set the width of the button
			btnOK.setMinWidth(50);
				
			// Handle clicks on the button
			btnOK.setOnAction(ae -> {
				dialog.close();		
			}); 
			
			
			// Create a layout
			GridPane dgp = new GridPane();
			// Add labels, text fields and the button to the layout
			dgp.add(lblButtons, 2, 0);
			dgp.add(lblUp, 0, 1, 2, 1);
			dgp.add(lblLeft, 0, 2, 2, 1);
			dgp.add(lblDown, 0, 3, 2, 1);
			dgp.add(lblRight, 0, 4, 2, 1);
			dgp.add(lblBoost, 0, 5, 2, 1);
			dgp.add(lblPause, 0, 6, 2, 1);
			dgp.add(lblRestart, 0, 7, 2, 1);
			
			dgp.add(txtfW, 2, 1);
			dgp.add(txtfA, 2, 2);
			dgp.add(txtfS, 2, 3);
			dgp.add(txtfD, 2, 4);
			dgp.add(txtfC, 2, 5);
			dgp.add(txtfV, 2, 6);
			dgp.add(txtfR, 2, 7);
			
			dgp.add(btnOK, 2, 9);
				
			// Extra configuration for the layout
			dgp.setPadding(new Insets(15));
			dgp.setHgap(10);
			dgp.setVgap(10);

					
			// Create a scene
			Scene s = new Scene(dgp,300,250);
			// Add a style sheet
			//s.getStylesheets().add("style_CardsHiLoGUI.css"); 
			// Set the scene
			dialog.setScene(s);
			dialog.show();
				
		}//showDialog()
	

	public static void main(String[] args) {
		launch(args);
	}
}