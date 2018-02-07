
public class Connect_Four_AI {

	private int DEPTH;
	private class pos{
		int x;
		int y;
		public pos(int x, int y){
			this.x = x;
			this.y = y;
		}
	}//Class to hold a coordinate
	Connect_Four_Board b;
	public Connect_Four_AI(Connect_Four_Board b, int depth)
	{
		this.b = b;
		DEPTH = depth;
	}
	public void runAI(){
		pos p = evaluate((Connect_Four_Board)b.clone(),DEPTH);//finds optimal place to play
		b.Move(p.x, p.y,false);//moves there
	}
	public pos evaluate(Connect_Four_Board b,int depth){
		//This function creates a game tree by making copies of the board, and playing every valid move on the copies.
		//Then the function scoreEval is called on each. scoreEval() is essentially a recursive function that does the same as this,
		//except returns the best score as opposed to the best position.
		//The "alpha" and "beta" are to remove unneeded branches on the tree, optimizing the performance
		//because of how the alpha-beta optimization works, it is best if moves are tried in best to worst order, ideally.
		//Therefore, we start with a "preliminary search", a quick one, to put them in tentatively the best order
		pos[] p = new pos[Connect_Four_Board.WIDTH];
		Connect_Four_Board[] bcopies = new Connect_Four_Board[Connect_Four_Board.WIDTH];
		int[] scores = new int[Connect_Four_Board.WIDTH];
		int[] prelims = preliminarySearch();
		int index;
		int alpha = 9999;
		int beta = -9999;
		
		for(int k = 0; k < Connect_Four_Board.WIDTH; k++)
		{
			int i = prelims[k];
			int y = 0;
			for(int j = 0; j < Connect_Four_Board.HEIGHT; j++)
			{
				if (b.isValid(i, j)){
					y = j;
					break;
				}
				if (j == Connect_Four_Board.HEIGHT - 1)
				{
					y = -1;
					break;
				}
			}
			if (y != -1){
				p[k] = new pos(i,y);
				bcopies[k] = (Connect_Four_Board)b.clone();
				bcopies[k].Move(p[k].x, p[k].y, true);
				scores[k] = scoreEval(bcopies[k],DEPTH-1,alpha,beta);
			}
			else
				scores[k] = b.redTurn()?-9999:9999;
			if(b.redTurn()){
				if(scores[k] > beta)
					beta = scores[k];
			}
			else
				if(scores[k] < alpha)
					alpha = scores[k];
		}
		if(b.redTurn())
		{
			index = 0;
			for(int i = 0; i < Connect_Four_Board.WIDTH; i++)
			{
				if(scores[i] > scores[index])
					index = i;
			}
		}
		else
		{
			index = 0;
			for(int i = 0; i < Connect_Four_Board.WIDTH; i++)
			{
				if(scores[i] < scores[index])
					index = i;
			}
		}
		return p[index];
	}
	public int scoreEval(Connect_Four_Board b, int depth, int alpha, int beta){
		if (b.winner == -999){//if winner hasn't been established yet, do what evaluate() does:
			//make copies and play each valid move. Then call itself on the copies.
			pos[] p = new pos[Connect_Four_Board.WIDTH];
			Connect_Four_Board[] bcopies = new Connect_Four_Board[Connect_Four_Board.WIDTH];
			int[] scores = new int[Connect_Four_Board.WIDTH];
			int index;
			for(int i = 0; i < Connect_Four_Board.WIDTH; i++)
			{
				int y = 0;
				for(int j = 0; j < Connect_Four_Board.HEIGHT; j++)
				{
					if (b.isValid(i, j)){
						y = j;
						break;
					}
					if (j == Connect_Four_Board.HEIGHT - 1)
					{
						y = -1;
						break;
					}
				}
				if (y != -1){
					p[i] = new pos(i,y);
					bcopies[i] = (Connect_Four_Board)b.clone();
					bcopies[i].Move(p[i].x, p[i].y, true);
					if(depth > 0){//EXIT CONDITION: If Depth = 0, we move on to finally evaluating the board
						scores[i] = scoreEval(bcopies[i],depth -1,alpha,beta);
					}
					else{
						scores[i] = finalEval(bcopies[i]);
					}
				}
				else
					scores[i] = b.redTurn()?-9999:9999;
				if(b.redTurn())
				{
					if(scores[i] >= alpha){
						return scores[i];
					}
					if(scores[i] > beta)
						beta = scores[i];
				}
				else
				{
					if(scores[i] <= beta){
						return scores[i];
					}
					if(scores[i] < alpha)
						alpha = scores[i];
				}
			}
			if(b.redTurn())
			{
				index = 0;
				for(int i = 0; i < Connect_Four_Board.WIDTH; i++)
				{
					if(scores[i] > scores[index])
						index = i;
				}
			}
			else
			{
				index = 0;
				for(int i = 0; i < Connect_Four_Board.WIDTH; i++)
				{
					if(scores[i] < scores[index])
						index = i;
				}
			}
			return scores[index];
		}
		else{//if winner is established, return a score of +or- 999 depending on who won.
			return b.winner*999 + (b.redTurn()?depth:-depth);
		}
	}
	private int finalEval(Connect_Four_Board b) {
		//evaluates the score of the board
		if(b.winner != -999)
			return b.winner*300;
		int score = 0;
		for(int i = 0; i < Connect_Four_Board.WIDTH*Connect_Four_Board.HEIGHT; i++)
		{
			if(b.boardVal(i/Connect_Four_Board.HEIGHT, i%Connect_Four_Board.HEIGHT) == 1)
			{
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
		for(int i = 0; i < Connect_Four_Board.WIDTH; i++){
			int j = -1;
			for(int y = 0; y < Connect_Four_Board.HEIGHT - 1; y++){
				if (b.isValid(i, y)){
					j = y;
					break;
				}
			}
			if (j != -1){
				bcop[i] =(Connect_Four_Board) b.clone();
				bcop[i].Move(i, j, true);
				bcop[i].Move(i, j+1, true);
				if(bcop[i].winner != -999){
					score += bcop[i].winner*40;
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
					score += bcop[i].winner*40;
				}
			}
		}
		score += b.finalEval();
		return score;
	}
	private int[] preliminarySearch(){//Does a quicker, less deep, less accurate version of eval(), and returns the moves, in best to worst order
		pos[] p = new pos[Connect_Four_Board.WIDTH];
		Connect_Four_Board[] bcopies = new Connect_Four_Board[Connect_Four_Board.WIDTH];
		int[] scores = new int[Connect_Four_Board.WIDTH];
		int alpha = 9999;
		int beta = -9999;
		for(int i = 0; i < Connect_Four_Board.WIDTH; i++)
		{
			int y = 0;
			for(int j = 0; j < Connect_Four_Board.HEIGHT; j++)
			{
				if (b.isValid(i, j)){
					y = j;
					break;
				}
				if (j == Connect_Four_Board.HEIGHT - 1)
				{
					y = -1;
					break;
				}
			}
			if (y != -1){
				p[i] = new pos(i,y);
				bcopies[i] = (Connect_Four_Board)b.clone();
				bcopies[i].Move(p[i].x, p[i].y, true);
				scores[i] = scoreEval(bcopies[i],DEPTH/2,alpha,beta);
			}
			else
				scores[i] = b.redTurn()?-9999:9999;
			if(b.redTurn()){
				if(scores[i] > beta)
					beta = scores[i];
			}
			else
				if(scores[i] < alpha)
					alpha = scores[i];
		}
		int[] returnVals = new int[Connect_Four_Board.WIDTH];
		if(b.redTurn())
		{
			int index;
			for(int j = 0; j < Connect_Four_Board.WIDTH; j++){
				index = 0;
				for(int i = 0; i < Connect_Four_Board.WIDTH; i++){
					if (scores[index] < scores[i])
						index = i;
				}
				returnVals[j] = index;
				scores[index] = -99999;
			}
		}
		else
		{
			int index;
			for(int j = 0; j < Connect_Four_Board.WIDTH; j++){
				index = 0;
				for(int i = 0; i < Connect_Four_Board.WIDTH; i++){
					if (scores[index] > scores[i])
						index = i;
				}
				returnVals[j] = index;
				scores[index] = 99999;
			}
		}
		return returnVals;
	}

}
