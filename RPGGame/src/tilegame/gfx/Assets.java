package tilegame.gfx;

import java.awt.image.BufferedImage;

public class Assets {
	
	public static final int width = 32, height = 32;
	
	public static BufferedImage player1, dirt, grass, stone, tree;
	
	
	public static void init() {
		SpriteSheet sheet = new SpriteSheet(ImageLoader.loadImage("/textures/assets.png"));
		SpriteSheet sheet2 = new SpriteSheet(ImageLoader.loadImage("/textures/test.png"));
		
		player1 = sheet2.crop(0, 0, width, height);
		dirt = sheet.crop(width * 2, 0, width, height);
		grass = sheet.crop(width, 0, width, height);
		stone = sheet.crop(width * 2, height, width, height);
		tree = sheet.crop(width * 12, height*5, width, height);
		
	}
	
}
