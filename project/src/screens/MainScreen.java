package screens;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.pennypop.project.ProjectApplication;

/**
 * This is where you screen code will go, any UI should be in here
 * 
 * @author Richard Taylor
 */
public class MainScreen implements Screen {

	ProjectApplication application;
	SpriteBatch batch;
	private final Stage stage;
	private Label pennyPopLabel, weatherLabel, cityLabel, conditionLabel, tempWindLabel;
	

	public MainScreen(ProjectApplication application) {
		//use the application field to set screen to the GameScreen
		this.application = application;
		batch = new SpriteBatch();
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, batch);
	}

	/**Add the tables into the stage for drawing*/
	public void addTable() {
		//URL for the API call
		String URLString = "http://api.openweathermap.org/data/2.5/weather?q=San%20Francisco,US&appid=2e32d2b4b825464ec8c677a49531e9ae";
		//tables for proper display;
		Table rootTable = new Table();
		Table menuTable = new Table();
		Table titleTable = new Table();
		Table buttonTable = new Table();
		Table weatherTable = new Table();
		
		//label style for the labels
		BitmapFont font = new BitmapFont(Gdx.files.internal("assets/font.fnt"), Gdx.files.internal("assets/font.png"), false);
		LabelStyle redStyle = new LabelStyle(font, Color.RED);
		LabelStyle blueStyle = new LabelStyle(font, Color.BLUE);
		LabelStyle darkStyle = new LabelStyle(font, new Color(0.5f, 0, 0, 1));
		//PennyPop
		pennyPopLabel = new Label("PennyPop", redStyle);	
		//Weather title table
		weatherLabel = new Label("Current Weather", darkStyle);
		cityLabel = new Label("San Francisco", blueStyle);
		titleTable.add(weatherLabel);
		titleTable.row();
		titleTable.add(cityLabel);
		//sfxButton to play sound
		Image sfxButton = new Image(new Texture(Gdx.files.internal("assets/sfxButton.png")));
		Sound soundClick = Gdx.audio.newSound(Gdx.files.internal("assets/button_click.wav"));
		sfxButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				soundClick.play();
			}
		});
		//apiButton for API call
		Image apiButton = new Image(new Texture(Gdx.files.internal("assets/apiButton.png")));
		apiButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				JSONObject json = getWeatherJSON(URLString);	
				if (json == null) {
					weatherUnavailable();
				} else {
					getWeatherInfo(json);
				}
			}
		});
		//gameButton to set to game screen
		Image gameButton = new Image(new Texture(Gdx.files.internal("assets/gameButton.png")));
		gameButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				application.setScreen(new GameScreen(application));
			}
		});
		//add buttons to button table
		buttonTable.add(sfxButton).space(10);
		buttonTable.add(apiButton).space(10);
		buttonTable.add(gameButton).space(10);
		//hint for the weather info, add labels to weather table
		conditionLabel = new Label("Please click the api button", redStyle);
		tempWindLabel = new Label("to get weather info!", redStyle);
		weatherTable.add(conditionLabel);
		weatherTable.row();
		weatherTable.add(tempWindLabel);
		//put all tables into the menuTable
		menuTable.add(pennyPopLabel).space(0, 0, 20, 100);
		menuTable.add(titleTable);
		menuTable.row();
		menuTable.add(buttonTable).space(0, 0, 0, 100);
		menuTable.add(weatherTable);
		
		rootTable.setFillParent(true);
		rootTable.add(menuTable);
		stage.addActor(rootTable);
	}

	/**get the weather info JSON from URL*/
	public JSONObject getWeatherJSON (String URLString) {
		try {
			URL url = new URL(URLString);
			InputStream is = url.openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				buffer.append(line + "\n");
			}
			if (buffer.length() == 0) {
				return null;
			}
			is.close();
			String JsonStr = buffer.toString();
			reader.close();
			JSONObject json = new JSONObject(JsonStr);
			return json;
		} catch (IOException e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**Set labels to the weather info from returned JSON */
	public void getWeatherInfo(JSONObject json) {
		try {
			JSONObject cloudsObj = json.getJSONObject("clouds");
			int cloudsPercentage = cloudsObj.getInt("all");
			String clouds;
			if (cloudsPercentage >= 60) {
				clouds = "cloudy";
			} else if (cloudsPercentage >= 30) {
				clouds = "slightly cloudy";
			} else {
				clouds = "clear";
			}
			String condition = "Sky is " + clouds;
			conditionLabel.setText(condition);
			JSONObject tempObj = json.getJSONObject("main");
			double tempRaw = tempObj.getDouble("temp");
			int temp = (int) (tempRaw * 9 / 5 - 459.67);
			JSONObject windObj = json.getJSONObject("wind");
			double speed = windObj.getDouble("speed") * 2.23694f;
			String mph = String.format("%.1f", speed);
			String tempWind = temp + " degrees, " + mph + "mph wind";
			tempWindLabel.setText(tempWind);
			tempWindLabel.setFontScale(0.8f);
		} catch (JSONException e) {
			weatherUnavailable();
		}
	}
	
	/**Give proper notification when weather is not available*/
	public void weatherUnavailable() {
		conditionLabel.setText("Weather info not available");
		tempWindLabel.setText("Please check your network condition");
		tempWindLabel.setFontScale(0.8f);
	}

	@Override
	public void dispose() {
		stage.dispose();
		batch.dispose();
	}

	@Override
	public void render(float delta) {
		stage.act(delta);
		stage.draw();	
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, false);
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
		//call dispose method when screen is no long visible
		dispose();
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		//add the tables to the stage when the screen is visible
		addTable();
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
