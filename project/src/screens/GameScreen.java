package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.pennypop.project.Connect4Game;
import com.pennypop.project.ProjectApplication;

/**
 * The game screen for the Connect 4 Game
 * 
 * @author Minquan Wang
 */
public class GameScreen implements Screen {
	
	ProjectApplication application;
	Stage stage;
	SpriteBatch batch;
	LabelStyle redStyle, yellowStyle, blueStyle, greenStyle;
	Label gameLabel, playerLabel, newGameLabel;
	Texture board, redDisc, yellowDisc;
	Connect4Game game;
	
	public final int DISK_SIZE = 64;
	public final int SQUARE_SIZE = 80;
	public final int BOARD_X_OFFSET = 300;
	public final int BOARD_Y_OFFSET = 50;
	public final int ROW = 6;
	public final int COLUMN = 7;
	public final int BOARD_WIDTH = SQUARE_SIZE * COLUMN;
	public final int BOARD_HEIGHT = SQUARE_SIZE * ROW;
	
	
	public GameScreen(ProjectApplication application) {
		//take ProjectApplication as parameter in order to set screen back to MainScree
		this.application = application;
		batch = new SpriteBatch();
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, batch);
		game = new Connect4Game();
	}

	@Override
	public void dispose() {
		//dispose the stage, batch and all textures
		stage.dispose();
		board.dispose();
		redDisc.dispose();
		yellowDisc.dispose();
		batch.dispose();
	}

	@Override
	public void hide() {
		//dispose the screen when no longer visible
		Gdx.input.setInputProcessor(null);
		dispose();
	}

	@Override
	public void render(float delta) {
		stage.act(delta);
		stage.draw();
		batch.begin();
		drawBoard();
		if (Gdx.input.justTouched()) {
			int col = getColumn(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
			//if the input is valid and game is not tie or over
			if (col > -1 && !game.isOver() && !game.isTie()) {
				gameLabel.setText("Game started! Connect four to win!");
				gameLabel.setX(250);
				//put the disk into the specific col of board, return false if selected col is full;
				boolean isPut = game.put(col);
				if (isPut) {
					//update playerLabel after move
					int player = game.getPlayer();
					if (player == 1) {
						playerLabel.setText("Red's Move!");
						playerLabel.setStyle(redStyle);
					} else {
						playerLabel.setText("Yellow's Move!");
						playerLabel.setStyle(yellowStyle);
					}
					//Check whether the game is tie or over
					if (game.isTie()) {
						gameLabel.setText("Wow! It's a tie!");
						playerLabel.setText("Want a new game?");
						gameLabel.setX(400);
					}
					if (game.isOver()) {
						//if the current player is yellow, it means red win the last step
						if (game.getPlayer() == 2) {
							gameLabel.setText("Red Won! Congratulations!");
							gameLabel.setX(325);
						} else {
							gameLabel.setText("Yellow Won! Congratulations!");
							gameLabel.setX(300);
						}
						playerLabel.setText("Want a new game?");
					}
				}
			}
		}
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, false);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		//Add the button, labels and load the textures when screen become visible
		addButtons();
		addLabel();
		loadTexture();
	}

	/**Add the backButton and newGameButton to the stage*/
	public void addButtons() {
		Image backButton = new Image(new Texture(Gdx.files.internal("assets/backButton.png")));
		Image newGameButton = new Image(new Texture(Gdx.files.internal("assets/newgameButton.png")));
		backButton.setPosition(50, 50);
		newGameButton.setPosition(50, 100);
		backButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				application.setScreen(new MainScreen(application));
			}
		});
		newGameButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game = new Connect4Game();
				//update the labels here;
				playerLabel.setText("Red's Move!");
				playerLabel.setStyle(redStyle);
				gameLabel.setText("Click the columns to start a new Connect 4 Game!");
				gameLabel.setX(125);
			}
			
		});
		stage.addActor(backButton);
		stage.addActor(newGameButton);
	}
	
	/**Load the board and disc Texture for batch to draw*/
	public void loadTexture() {
		board = new Texture(Gdx.files.internal("assets/board.png"));
		redDisc = new Texture(Gdx.files.internal("red.png"));
		yellowDisc = new Texture(Gdx.files.internal("yellow.png"));
	}
	
	/**Create the Labels add to the stage*/
	public void addLabel() {
		BitmapFont font = new BitmapFont(Gdx.files.internal("assets/font.fnt"), Gdx.files.internal("assets/font.png"), false);
		redStyle = new LabelStyle(font, Color.RED);
		yellowStyle = new LabelStyle(font, new Color(1f, 0.9f, 0f, 1f));
		blueStyle = new LabelStyle(font, Color.BLUE);
		greenStyle = new LabelStyle(font, Color.GREEN);
		//label for game info and result
		gameLabel = new Label("Click the columns to start a new Connect 4 Game!", blueStyle);
		gameLabel.setPosition(125, BOARD_Y_OFFSET + BOARD_HEIGHT + 50);
		//label to show the current player, initialized with red
		playerLabel = new Label("Red's Move!", redStyle);
		playerLabel.setPosition(BOARD_X_OFFSET + BOARD_WIDTH + 50, BOARD_Y_OFFSET + BOARD_HEIGHT / 2);
		stage.addActor(gameLabel);
		stage.addActor(playerLabel);
	}
	
	/**Get the column number based on the x position clicked, the full screen mode may cause error here*/
	public int getColumn(int x, int y) {
		//check weather the click is valid
		if (y < BOARD_Y_OFFSET || y > BOARD_Y_OFFSET + BOARD_HEIGHT || x < BOARD_X_OFFSET || x > BOARD_X_OFFSET + BOARD_WIDTH) {
			return -1;
		}
		return (x - BOARD_X_OFFSET) / SQUARE_SIZE;
	}
	
	/**Draw the board and the discs on board based on the boardArray */
	public void drawBoard() {
		batch.draw(board, BOARD_X_OFFSET, BOARD_Y_OFFSET);
		int[][] boardArray = game.getBoard();
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COLUMN; j++) {
				if (boardArray[i][j] == 0) {
					continue;
				}
				if (boardArray[i][j] == 1) {
					batch.draw(redDisc, BOARD_X_OFFSET + (SQUARE_SIZE - DISK_SIZE) / 2 + SQUARE_SIZE * j, 
							BOARD_Y_OFFSET + (SQUARE_SIZE - DISK_SIZE) / 2 + SQUARE_SIZE * i);
				} else {
					batch.draw(yellowDisc, BOARD_X_OFFSET + (SQUARE_SIZE - DISK_SIZE) / 2 + SQUARE_SIZE * j, 
							BOARD_Y_OFFSET + (SQUARE_SIZE - DISK_SIZE) / 2 + SQUARE_SIZE * i);
				}
			}
		}
	}

	@Override
	public void pause() {
		// Irrelevant on desktop, ignore this
	}

	@Override
	public void resume() {
		// Irrelevant on desktop, ignore this
	}
	
}
