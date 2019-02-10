package Code;


public class FoodPositive implements Food{
	private int x;
	private int y;
	
	public FoodPositive (int x, int y) {
		this.x = x;
		this.y = y;
	}

	
	public boolean equals(Object o ) {
		if(!(o instanceof FoodPositive)) return false;
		FoodPositive f = (FoodPositive) o;
		return f.x==this.x && f.y==this.y;
	}

	@Override
	public int getX() {
		// TODO Auto-generated method stub
		return x;
	}

	@Override
	public int getY() {
		// TODO Auto-generated method stub
		return y;
	}

}
