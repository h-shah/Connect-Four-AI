import java.awt.*;
import java.applet.*;
public class Connect_Four_driver extends Applet implements Runnable{
	
	Connect_Four_Board b1 = new Connect_Four_Board(true,true,false,0,0,createsize(),false,6);
	//to change AI difficulty, change the last parameter of the above.
	//4 is easy, but very quick. 6 is optimal, and medium. 8 is hard, but fairly slow. Keep as an even number.
	Connect_Four_AI ai = new Connect_Four_AI(b1,b1.depth);
	public static void main(String [] args){
		//Runs the applet in a window
		Connect_Four_driver theApplet = new Connect_Four_driver();
		theApplet.init(); 
		theApplet.start(); 
		javax.swing.JFrame window = new javax.swing.JFrame("Connect Four");
		window.setContentPane(theApplet);
		window.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		window.setSize(500,500);
		window.setVisible(true);
		window.setBackground(Color.WHITE);
    }
	public int createsize(){
		 //creates the size of one square on the board.
		 int a = (int) (this.getSize().getHeight()/Connect_Four_Board.HEIGHT);
		 int b = (int) (this.getSize().getWidth()/Connect_Four_Board.WIDTH);
		 if (a > b)
			 return Connect_Four_Board.WIDTH*b/(Connect_Four_Board.WIDTH+1);
		 else
			 return Connect_Four_Board.HEIGHT*a/(Connect_Four_Board.HEIGHT+1);
	}
	public void start (){
		//Sets one thread for AI thinking, and the other to repaint the Applet
		//Two different threads so that the program does not freeze when the Connect_Four_AI is thinking.
		Thread th = new Thread(this);
		th.setPriority(10);
		th.start ();
		Thread painter = new Thread(new Runnable(){
			public void run(){
				while(true){
					repaint();
					b1.setSize(createsize());
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
					}
				}
			}
		});
		painter.setPriority(1);
		painter.start();
	}
	public void run (){
		//Runs AI if it is the AI's turn and it is not already running
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		while (true)
		{ 
			b1.setSize(createsize());
			if(b1.winner == -999)
				if(b1.redTurn() && !b1.redIsHuman){
					b1.AIisRunning = true;
					ai.runAI();
					b1.AIisRunning = false;
				}
				else if(!(b1.redTurn() || b1.blackIsHuman)){
					b1.AIisRunning = true;
					ai.runAI();
					b1.AIisRunning = false;
				}
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		}
	}
	public boolean mouseDown(Event evt, int x, int y){
		b1.MouseDown(x,y);
		repaint();
		return true;
	}
	public void paint(Graphics g){
		b1.paint(g);
	}
	//ALL CODE AFTER THIS IS FOR IMAGE BUFFERING
	private Image dbImage;
	private Graphics dbg;
	public void update (Graphics g) 
	{
	// initialize buffer 
	dbImage = createImage (this.getSize().width, this.getSize().height); 
	dbg = dbImage.getGraphics ();
	// clear screen
	dbg.setColor (getBackground ()); 
	dbg.fillRect (0, 0, this.getSize().width, this.getSize().height); 
	// draw new screen
	dbg.setColor (getForeground()); 
	paint (dbg); 
	// draw image on the screen
	g.drawImage (dbImage, 0, 0, this); 
	} 
}