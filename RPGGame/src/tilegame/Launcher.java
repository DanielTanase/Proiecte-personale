package tilegame;

import tilegame.display.Display;

public class Launcher {
	public static void main(String[] args) {
		Game game = new Game("Tile",1024,760);
		game.start(); 
	}
}
