import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.Image;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Texture;
import org.jsfml.graphics.View;
import org.jsfml.system.Clock;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Keyboard.Key;
import org.jsfml.window.VideoMode;
import org.jsfml.window.event.Event;

public class Main {
	
	public static void main(String[] args) throws IOException {

		final int SCALE = 16 * 2;

		RenderWindow window = new RenderWindow(new VideoMode(1280, 720), "BOI");

		Image tileSheetImage = new Image();
		tileSheetImage.loadFromFile(Paths.get("assets/basictiles.png"));

		Image mapImage = new Image();
		mapImage.loadFromFile(Paths.get("assets/Map.png"));
		
		Texture characterSheet = new Texture();
		try {
			characterSheet.loadFromFile(Paths.get("assets/characters.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Texture tileSheet = new Texture();
		try {
			tileSheet.loadFromFile(Paths.get("assets/basictiles.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		RectangleShape player = new RectangleShape(new Vector2f(SCALE, SCALE));
		player.setTexture(characterSheet);
		player.setTextureRect(new IntRect(16 * 4, 0, 16, 16));
		player.setPosition(SCALE, SCALE);
		
		RectangleShape[] tiles = new RectangleShape[tileSheetImage.getSize().x / 16];
		for (int i = 0; i < tiles.length; i++) {
			tiles[i] = new RectangleShape(new Vector2f(SCALE, SCALE));
			tiles[i].setTexture(tileSheet);
			tiles[i].setTextureRect(new IntRect(16 * i, 0, 16, 16));
		}

		HashMap<Color, Integer> tileToColor = new HashMap<>();
		tileToColor.put(new Color(0, 0, 0), 0);
		tileToColor.put(new Color(0, 140, 33), 11);
		tileToColor.put(new Color(0, 0, 219), 13);
		
		int[][] map = new int[mapImage.getSize().y][mapImage.getSize().x];
		for (int i = 0; i < map.length; ++i) {
			for (int j = 0; j < map[0].length; ++j) {
				map[i][j] = tileToColor.get((mapImage.getPixel(j, i)));
			}
		}
		
		HashMap<Keyboard.Key, Integer> keyToIdleFrame = new HashMap<>();
		keyToIdleFrame.put(Key.W, 48);
		keyToIdleFrame.put(Key.A, 16);
		keyToIdleFrame.put(Key.S, 0);
		keyToIdleFrame.put(Key.D, 32);
		
		View defaultView = (View) window.getDefaultView();
		View view = new View(defaultView.getCenter(), defaultView.getSize());
		view.zoom(0.75f);

		final int ANIM_DELAY = 200;
		final int MOVE_DELAY = 100;
		
		int frame = 4;
		
		Clock timer = new Clock();
		Clock animClock = new Clock();
		
		while (window.isOpen()) {
			for (Event e : window.pollEvents()) {
				if (e.type == Event.Type.CLOSED) {
					window.close();
				}
				
				if (e.type == Event.Type.KEY_RELEASED) {
					frame = 1;
					player.setTextureRect(new IntRect(48 + frame * 16, keyToIdleFrame.get(e.asKeyEvent().key), 16, 16));
				}
			}
			
			int checkRight = (int) (player.getPosition().x / SCALE + 1);
			int checkLeft = (int) (player.getPosition().x / SCALE - 1);
			int checkUp = (int) (player.getPosition().y / SCALE - 1);
			int checkDown = (int) (player.getPosition().y / SCALE + 1);
			
			//Animates Player
			if (Keyboard.isKeyPressed(Key.W) && animClock.getElapsedTime().asMilliseconds() >= ANIM_DELAY) {
				animClock.restart();
				frame++;
				if (frame > 2) frame = 0;
				player.setTextureRect(new IntRect(48 + frame * 16, 16 * 3, 16, 16));
			}
			if (Keyboard.isKeyPressed(Key.S) && animClock.getElapsedTime().asMilliseconds() >= ANIM_DELAY) {
				animClock.restart();
				frame++;
				if (frame > 2) frame = 0;
				player.setTextureRect(new IntRect(48 + frame * 16, 0, 16, 16));
			}
			if (Keyboard.isKeyPressed(Key.A) && animClock.getElapsedTime().asMilliseconds() >= ANIM_DELAY) {
				animClock.restart();
				frame++;
				if (frame > 2) frame = 0;
				player.setTextureRect(new IntRect(48 + frame * 16, 16 * 1, 16, 16));
			}
			if (Keyboard.isKeyPressed(Key.D) && animClock.getElapsedTime().asMilliseconds() >= ANIM_DELAY) {
				animClock.restart();
				frame++;
				if (frame > 2) frame = 0;
				player.setTextureRect(new IntRect(48 + frame * 16, 16 * 2, 16, 16));
			}
			
			//Moves player
			if (Keyboard.isKeyPressed(Key.W) && timer.getElapsedTime().asMilliseconds() > MOVE_DELAY
					&& map[checkUp][(int) (player.getPosition().x / SCALE)] != 0) {
				timer.restart();
				player.move(0, -SCALE);
			}
			if (Keyboard.isKeyPressed(Key.S) && timer.getElapsedTime().asMilliseconds() > MOVE_DELAY
					&& map[checkDown][(int) (player.getPosition().x / SCALE)] != 0) {
				timer.restart();
				player.move(0, SCALE);
			}
			if (Keyboard.isKeyPressed(Key.A) && timer.getElapsedTime().asMilliseconds() > MOVE_DELAY
					&& map[(int) (player.getPosition().y / SCALE)][checkLeft] != 0) {
				timer.restart();
				player.move(-SCALE, 0);
			}
			if (Keyboard.isKeyPressed(Key.D) && timer.getElapsedTime().asMilliseconds() > MOVE_DELAY
					&& map[(int) (player.getPosition().y / SCALE)][checkRight] != 0) {
				timer.restart();
				player.move(SCALE, 0);
			}
			
			view.setCenter(player.getPosition().x, player.getPosition().y);
			window.setView(view);
			window.clear();

			//Renders tiles in view instead of all at once
			int tx = (int) ((view.getCenter().x - window.getSize().x / 2) / SCALE);
			int ty = (int) ((view.getCenter().y - window.getSize().y / 2) / SCALE);

			int endX = (int) ((view.getCenter().x + window.getSize().x / 2) / SCALE) + 1;
			int endY = (int) ((view.getCenter().y + window.getSize().y / 2) / SCALE) + 1;

			if (tx < 0) {
				tx = 0;
			}
			if (ty < 0) {
				ty = 0;
			}
			if (endY > map.length) {
				endY = map.length;
			}
			if (endX > map[0].length) {
				endX = map[0].length;
			}

			for (int i = ty; i < endY; i++) { // y
				for (int j = tx; j < endX; j++) { // x
					tiles[map[i][j]].setPosition(j * SCALE, i * SCALE);
					window.draw(tiles[map[i][j]]);
				}
			}
			window.draw(player);
			window.display();
		}

	}
}