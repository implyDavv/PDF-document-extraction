package pdfHandle;

public class Coordinate {
	 private int x;
	 private int y;

	    public Coordinate(int x, int y) {
	        this.x = x;
	        this.y = y;
	    }

	    public int getX() {
	        return x;
	    }

	    public void setX(int x) {
	        this.x = x;
	    }

	    public int getY() {
	        return y;
	    }

	    public void setY(int y) {
	        this.y = y;
	    }

	    @Override
	    public String toString() {
	        return "Coordinate{" +
	                "x=" + x +
	                ", y=" + y +
	                '}';
	    }

	    @Override
	    public boolean equals(Object obj) {
	        Coordinate coordinate = (Coordinate) obj;
	        if(coordinate.getX() == this.x && Math.abs(coordinate.getY() - this.y) <= 2){
	            return true;
	        }
	        if(coordinate.getY() == this.y && Math.abs(coordinate.getX() - this.x) <= 2){
	            return true;
	        }
	        if(Math.abs(coordinate.getY() - this.y) <= 2 && Math.abs(coordinate.getX() - this.x) <= 2){
	            return true;
	        }
	        return false;
	    }

}
