import java.awt.*;
public class Connect_Four_Board {
	public static final int HEIGHT = 6;//Height of Connect_Four_Board in squares
	public static final int WIDTH = 7;//Width in squares
	private int[][] board = new int[WIDTH][HEIGHT];//Holds values 1 for a red piece, -1 for a black piece, and 0 for empty
	private boolean redTurn;
	private boolean isOver;//If game is over, this is to stop everything
	public boolean redIsHuman;//True if the red player is human
	public boolean blackIsHuman;//True is black player is human
	private int x_pos;//Offset of the board when it is displayed
	private int y_pos;
	public int size;//size of a square
	public int winner = -999;//1 if red has won, -1 if black has won, 0 if tie
	public int boardVal = 0;//"score" of the board. Determines who is winning at the time by analyzing board
	public int depth;//depth of the AI game tree search algorithm.
	public boolean AIisRunning = false;//used to stop user input when AI is running
	private int angle = 0;//angle of arc in "loading" symbol
	private String allMovesSoFar;
	
	public void changeTurnAI(){//Only used by the AI for evaluational purposes
		redTurn = !redTurn;
	}
	public Connect_Four_Board(boolean redStarts, boolean redHuman, boolean blackHuman, int x, int y, int pixPerSquare, boolean inAI, int depth){
		this.depth = depth;
		redTurn = redStarts;
		redIsHuman = redHuman;
		blackIsHuman = blackHuman;
		x_pos = x;
		y_pos = y;
		size = pixPerSquare;
		allMovesSoFar="";
		for(int i = 0; i < WIDTH*HEIGHT; i++)
		{
			board[i/HEIGHT][i%HEIGHT] = 0;
		}
	}
	public void undoLastMove(){
		
	}
	private void setVal(int x, int y, int val){//Used only in clone() method
		board[x][y] = val;
	}	
	public Object clone(){//returns an equivalent board. Used in AI
		Connect_Four_Board b = new Connect_Four_Board(redTurn,redIsHuman,blackIsHuman,x_pos,y_pos,size,true,0);
		for(int i = 0; i < WIDTH*HEIGHT; i++)
			b.setVal(i/HEIGHT,i%HEIGHT,board[i/HEIGHT][i%HEIGHT]);
		return b;
	}
	public int boardVal(int x, int y){
		return board[x][y];
	}
	public boolean redTurn(){
		return redTurn;
	}
	public void Move(int x, int y, boolean inAI){//the inAI is true if the AI is calling Move().
		if(inAI || isValid(x,y))//CHANGE MADE WITHOUT READING FULL CODE. if something breaks try removing the first condition
		{
			board[x][y] = redTurn?1:-1;
			changeTurn(inAI);
			allMovesSoFar.concat(""+x);
		}
		else System.out.println("Invalid move");
	}
	private void changeTurn(boolean inAI){
		int boardVal = Score();//updates score of the board
		//sees if there is a winner
		int winner = 0;
		if (boardVal == 999)
			winner = 1;
		else if(boardVal == -999)
			winner = -1;
		if (winner != 0)
		{
			isOver = true;
			this.winner = winner;
			if(!inAI)//If not in AI, print the winner, along with a representation of the board.
			{
				System.out.println(this);
				System.out.println(((winner == 1)?"red":"black")+ " Wins!!!");
			}
			return;
		}
		//checks for tie
		boolean isTie = true;
		for(int i = 0; i < WIDTH*HEIGHT; i++)
		{
			if(board[i/HEIGHT][i%HEIGHT] == 0)
				isTie = false;
		}
		if(isTie)
		{
			isOver = true;
			this.winner = 0;
			if(!inAI)
			{	
				System.out.println(this);
				System.out.println("Tie!!!");
			}
			return;
		}
		redTurn = !redTurn;
	}
	public boolean isValid(int x, int y)
	{//returns true if the move is valid
		if(board[x][y] != 0)
			return false;
		if(x < 0 || x >= WIDTH)
			return false;
		if(y < 0 || y >= HEIGHT)
			return false;
		if(y != 0)
			if(board[x][y-1] == 0)//ensures that no pieces can be placed without a piece underneath them.
				return false;
		return true;
	}
	public void MouseDown(int real_x, int real_y)
	{
		if(!AIisRunning)//cannot take input if AI is running
			if(!isOver)//cannot take input if game is over
				if(real_x > x_pos && real_x < x_pos + WIDTH*size && real_y > y_pos && real_y < y_pos + HEIGHT*size)
				{
					int x = (real_x - x_pos) / size;//converts the pixel value of the clicck into which row was clicked on
					for(int y = 0; y < HEIGHT; y++)
					{
						if(isValid(x,y)){//if move is valid, moves there
							Move(x,y,false);
							break;
						}
					}
				}
	}
	public synchronized void paint(Graphics g)
	{
		
		for(int i = 0; i <= WIDTH; i++)
		{//veritcal lines of board
			g.fillRect(x_pos + i*size - (size/10 + 1)/2, y_pos, size/10 + 1, HEIGHT*size);
		}
		for(int j = 0; j <= HEIGHT; j++)
		{//horizontal lines of board
			g.fillRect(x_pos, y_pos + j*size -(size/10 + 1)/2, WIDTH*size, size/10 + 1);
		}
		for(int i = 0; i < WIDTH; i++)//pieces
			for(int j = 0; j < HEIGHT; j++)
				if(board[i][j] == 1)
				{
					g.setColor(Color.red);
					g.fillOval(x_pos + size*i + size/4, y_pos + size*(HEIGHT-1-j)+size/4, size/2, size/2);
				}
				else if (board[i][j] == -1)
				{
					g.setColor(Color.black);
					g.fillOval(x_pos + size*i + size/4, y_pos + size*(HEIGHT-1-j)+size/4, size/2, size/2);
				}
		if(AIisRunning){//Loading screen
			g.setColor(new Color(0,0,255,100));
			g.fillRect(0, 0, size*Connect_Four_Board.WIDTH, size*Connect_Four_Board.HEIGHT);
			g.setColor(Color.BLUE);
			g.fillArc(size*(WIDTH-1)/2, size*(HEIGHT-1)/2 + 10, size, size, angle, 50);
			angle+= 5;
			g.setFont(new Font("serif",Font.PLAIN,5*size/6));
			g.setColor(Color.RED);
			g.drawString("Thinking...",size*(WIDTH-3)/2, size*(HEIGHT-1)/2);
		}
		else if(winner != -999){//Colors screen with a translucent shade of the winner's color
			g.setColor(winner == 1?new Color(255,0,0,100):new Color(0,0,0,100));
			g.fillRect(0, 0, size*Connect_Four_Board.WIDTH, size*Connect_Four_Board.HEIGHT);
		}
		//colors the "winning wheel" at the bottom, that shows who is winning
		g.setColor(Color.BLUE);
		g.fillArc(size*(WIDTH-2)/2, size*HEIGHT+1, 2*size, 2*size, 0, 180);
		g.setColor(Color.CYAN);
		boardVal = Connect_Four_BoardVal(this);
		if(boardVal > 100)
			boardVal = 100;
		if(boardVal < -100)
			boardVal = -100;
		g.fillArc(size*(WIDTH-2)/2, size*HEIGHT+1, 2*size, 2*size,(int)(180*Math.acos(boardVal/100.0)/Math.PI-5),10);
		g.setColor(Color.BLACK);
		g.fill3DRect(size*(WIDTH-3)/2, size*HEIGHT+1, size/2, size/2, true);
		g.setColor(Color.RED);
		g.fill3DRect(size*(WIDTH+2)/2, size*HEIGHT+1, size/2, size/2, true);
	}
	public synchronized void setSize(int i){
		size = i;
	}
	public String toString()

	{
		String s = "";
		for(int j = HEIGHT - 1; j >= 0; j--){
		for(int i = 0; i < WIDTH; i++)
		{
			if(board[i][j] == 1)
				s+= "r ";
			else if (board[i][j] == -1)
				s+= "b ";
			else
				s+= "  ";
		}
		s+="\n";
		}
		return s;
	}
	public int Score(){//Scores board, based on how many 2 in a rows, and 3 in a rows each person has
		int score = 0;
		for(int i = 0; i < HEIGHT; i++)
		{
			int inARow = 0;
			int checkVal = 0;
			for(int j = 0; j < WIDTH; j++)
			{
				if(board[j][i] == checkVal)
					inARow++;
				else
				{
					checkVal = board[j][i];
					inARow = 1;
				}
				if(checkVal != 0)
				{
					score += inARow*checkVal;
					if(inARow == 4)
						return checkVal*999;
				}
			}
		}
		for(int i = 0; i < WIDTH; i++)
		{
			int inARow = 0;
			int checkVal = 0;
			for(int j = 0; j < HEIGHT; j++)
			{
				if(board[i][j] == checkVal)
					inARow++;
				else
				{
					checkVal = board[i][j];
					inARow = 1;
				}
				if(checkVal != 0)
				{
					score += inARow*checkVal;
					if(inARow == 4)
						return checkVal*999;
				}
			}
		}
		for(int startX = 0; startX < WIDTH - 3; startX++)
		{
			int inARow = 0;
			int checkVal = 0;
			for(int i = 0; startX + i < WIDTH && i < HEIGHT; i++)
			{
				if(board[startX + i][i] == checkVal)
					inARow++;
				else
				{
					checkVal = board[startX + i][i];
					inARow = 1;
				}
				if(checkVal != 0)
				{
					score += inARow*checkVal;
					if(inARow == 4)
						return checkVal*999;
				}
			}
		}
		for(int startY = 0; startY < HEIGHT - 3; startY++)
		{
			int inARow = 0;
			int checkVal = 0;
			for(int i = 0;i < WIDTH && startY + i <HEIGHT; i++)
			{
				if(board[i][i + startY] == checkVal)
					inARow++;
				else
				{
					checkVal = board[i][startY + i];
					inARow = 1;
				}
				if(checkVal != 0)
				{
					score += inARow*checkVal;
					if(inARow == 4)
						return checkVal*999;
				}
			}
		}
		for(int startX = 0; startX < WIDTH - 3; startX++)
		{
			int inARow = 0;
			int checkVal = 0;
			for(int i = 0; startX + i < WIDTH && HEIGHT - i - 1 >= 0; i++)
			{
				if(board[startX + i][HEIGHT - i - 1] == checkVal)
					inARow++;
				else
				{
					checkVal = board[startX + i][HEIGHT - i - 1];
					inARow = 1;
				}
				if(checkVal != 0)
				{
					score += inARow*checkVal;
					if(inARow == 4)
						return checkVal*999;
				}
			}
		}
		for(int startY = 3; startY < HEIGHT; startY++)
		{
			int inARow = 0;
			int checkVal = 0;
			for(int i = 0; i < WIDTH && startY - i >= 0; i++)
			{
				if(board[i][startY - i] == checkVal)
					inARow++;
				else
				{
					checkVal = board[i][startY - i];
					inARow = 1;
				}
				if(checkVal != 0)
				{
					score += inARow*checkVal;
					if(inARow == 4)
						return checkVal*999;
				}
			}
		}
		return score;
		
	}
	public int finalEval()
	{//The final score evaluation of the game tree in the AI thinking process
		int score = 0;
		for(int i = 0; i < HEIGHT; i++)
		{
			int inARow = 0;
			int checkVal = 0;
			for(int j = 0; j < WIDTH; j++)
			{
				if(board[j][i] == checkVal)
					inARow++;
				else
				{
					checkVal = board[j][i];
					inARow = 1;
				}
				if(inARow == 3 && checkVal != 0)
				{
					score += 3*checkVal;
					break;
				}
			}
		}
		for(int i = 0; i < WIDTH; i++)
		{
			int inARow = 0;
			int checkVal = 0;
			for(int j = 0; j < HEIGHT; j++)
			{
				if(board[i][j] == checkVal)
					inARow++;
				else
				{
					checkVal = board[i][j];
					inARow = 1;
				}
				if(inARow == 3 && checkVal != 0)
				{
					score += 3*checkVal;
					break;
				}
			}
		}
		for(int startX = 0; startX < WIDTH - 3; startX++)
		{
			int inARow = 0;
			int checkVal = 0;
			for(int i = 0; startX + i < WIDTH && i < HEIGHT; i++)
			{
				if(board[startX + i][i] == checkVal)
					inARow++;
				else
				{
					checkVal = board[startX + i][i];
					inARow = 1;
				}
				if(inARow == 3 && checkVal != 0)
				{
					score += 3*checkVal;
					break;
				}
			}
		}
		for(int startY = 0; startY < HEIGHT - 3; startY++)
		{
			int inARow = 0;
			int checkVal = 0;
			for(int i = 0;i < WIDTH && startY + i <HEIGHT; i++)
			{
				if(board[i][i + startY] == checkVal)
					inARow++;
				else
				{
					checkVal = board[i][startY + i];
					inARow = 1;
				}
				if(inARow == 3 && checkVal != 0)
				{
					score += 3*checkVal;
					break;
				}
			}
		}
		for(int startX = 0; startX < WIDTH - 3; startX++)
		{
			int inARow = 0;
			int checkVal = 0;
			for(int i = 0; startX + i < WIDTH && HEIGHT - i - 1 >= 0; i++)
			{
				if(board[startX + i][HEIGHT - i - 1] == checkVal)
					inARow++;
				else
				{
					checkVal = board[startX + i][HEIGHT - i - 1];
					inARow = 1;
				}
				if(inARow == 3 && checkVal != 0)
				{
					score += 3*checkVal;
					break;
				}
			}
		}
		for(int startY = 3; startY < HEIGHT; startY++)
		{
			int inARow = 0;
			int checkVal = 0;
			for(int i = 0; i < WIDTH && startY - i >= 0; i++)
			{
				if(board[i][startY - i] == checkVal)
					inARow++;
				else
				{
					checkVal = board[i][startY - i];
					inARow = 1;
				}
				if(inARow == 3 && checkVal != 0)
				{
					score += 3*checkVal;
					break;
				}
			}
		}
		return score;
	}
	private int Connect_Four_BoardVal(Connect_Four_Board b){//Scores the board
		if(b.winner != -999)
			return b.winner*300;
		int score = 0;
		for(int i = 0; i < Connect_Four_Board.WIDTH*Connect_Four_Board.HEIGHT; i++)
		{
			if(b.boardVal(i/Connect_Four_Board.HEIGHT, i%Connect_Four_Board.HEIGHT) == 1)
			{//For each pair of adjacent pieces, add 1 point.
				for(int x = i/Connect_Four_Board.HEIGHT - 1; x <= i/Connect_Four_Board.HEIGHT + 1; x++)
					for(int y = i%Connect_Four_Board.HEIGHT - 1; y <= i%Connect_Four_Board.HEIGHT + 1;y++)
					{
						if(x >= 0 && x < Connect_Four_Board.WIDTH && y >= 0 && y < Connect_Four_Board.HEIGHT)
						{
							if (b.boardVal(x, y) == b.boardVal(i/Connect_Four_Board.HEIGHT, i%Connect_Four_Board.HEIGHT))
									score += b.boardVal(i/Connect_Four_Board.HEIGHT, i%Connect_Four_Board.HEIGHT);
							else if(b.boardVal(x, y) == -b.boardVal(i/Connect_Four_Board.HEIGHT, i%Connect_Four_Board.HEIGHT))
								score -= b.boardVal(i/Connect_Four_Board.HEIGHT, i%Connect_Four_Board.HEIGHT);
						}
						else
							score -= b.boardVal(i/Connect_Four_Board.HEIGHT, i%Connect_Four_Board.HEIGHT);
					}
			}
		}
		Connect_Four_Board[] bcop = new Connect_Four_Board[Connect_Four_Board.WIDTH];
		//for each winning threat (a position that an opponent cannot play, or you win), add 20 points
		for(int i = 0; i < Connect_Four_Board.WIDTH; i++){
			int j = -1;
			for(int y = 0; y < Connect_Four_Board.HEIGHT - 1; y++){
				if (b.isValid(i, y)){
					j = y;
					break;
				}
			}
			if (j != -1){
				bcop[i] = (Connect_Four_Board) b.clone();
				bcop[i].Move(i, j, true);
				bcop[i].Move(i, j+1, true);
				if(bcop[i].winner != -999){
					score += bcop[i].winner*20;
				}
			}
		}
		for(int i = 0; i < Connect_Four_Board.WIDTH; i++){
			int j = -1;
			for(int y = 0; y < Connect_Four_Board.HEIGHT - 1; y++){
				if (b.isValid(i, y)){
					j = y;
					break;
				}
			}
			if (j != -1){
				bcop[i] = (Connect_Four_Board)b.clone();
				bcop[i].changeTurnAI();
				bcop[i].Move(i, j, true);
				bcop[i].Move(i, j+1, true);
				if(bcop[i].winner != -999){
					score += bcop[i].winner*20;
				}
			}
		}
		//Adds the final evaluation to the score.
		score += b.finalEval();
		return score;
	}
}
