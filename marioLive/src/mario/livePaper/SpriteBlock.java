package mario.livePaper;

public class SpriteBlock extends AnimatedSprite {
	enum containerTypes {
		Coin10(0),
		Coin5(1),
		Empty(2),
		Mushroom(3),
		GreenMushroom(4),
		Star(5),
		FireFlower(6);
		
		private int code;
		
		private containerTypes(int c) {
			code = c;
		}
		
		public int getCode() {
			return code;
		}
	}
	private containerTypes mType;
	private int mRemain;
	
	public void bounce() {
		super.bounce();
		
	}
}
