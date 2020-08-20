import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class GameFrame extends JFrame {

    void launchGame() {

        this.setTitle("SnakeGame");
        this.setBounds(50,50,920,641);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setResizable(false);

        Container container = this.getContentPane();
        container.setBackground(new Color(100,128,100));
        GamePanel gp = new GamePanel();
        gp.setBounds(15,15,
                GamePanel.COL*GamePanel.SIDE_LEN+6,
                GamePanel.ROW*GamePanel.SIDE_LEN+6
        );
        gp.setBackground(Color.BLACK);
        gp.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2,
                Color.WHITE)
        );
        container.add(gp);
        //this.addWindowListener();
        this.setVisible(true);
    }

    public static void main(String[] args) {

        GameFrame gf = new GameFrame();
        gf.launchGame();
    }
}

class GamePanel extends JPanel {

    public static final int ROW = 28;
    public static final int COL = 35;
    public static final int SIDE_LEN = 20;
    public PaintThread paintThread = new PaintThread();
    Snake snake = new Snake();
    Egg egg = new Egg();
    public boolean running = false;
    public boolean gameOver = false;

    public GamePanel() {
        this.setFocusable(true); // 重新设定焦点使得panel中的监听控件有效
        this.addKeyListener(new KeyMonitor());
        new Thread(new PaintThread()).start();
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);
        this.drawGrid(g);
        snake.drawSnake(g);
        egg.drawEgg(g);
    }

    public void drawGrid(Graphics g) {

        g.setColor(new Color(128,128,128));
        for(int i=0; i<COL; i++){
            g.drawLine(i*SIDE_LEN+3,3,i*SIDE_LEN+3,ROW*SIDE_LEN+3);
        }
        for(int i=0; i<ROW; i++){
            g.drawLine(3,i*SIDE_LEN+3,COL*SIDE_LEN+3,i*SIDE_LEN+3);
        }
    }

    class PaintThread implements Runnable {


        @Override
        public void run() {
            while(true) {
                state();
                if(running && !gameOver) {
                    repaint();
                    snake.eatEgg(egg);
                    checkDead();
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void state(){
            if(gameOver) {
                System.out.println("游戏已结束，蛇挂掉了");
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else
            if(running)
                System.out.println("游戏运行中");
            else{
                System.out.println("游戏暂停中");
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class KeyMonitor extends KeyAdapter{

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if(key == KeyEvent.VK_SPACE) {
                running = !running;
            }
            if(key == KeyEvent.VK_R) {
                egg.reset(snake);
            }
            else{
                snake.keyPress(e);
            }
        }
    }

    public void checkDead() {
        if(snake.head.row<0 || snake.head.row>27 || snake.head.col<0 || snake.head.col>34) {
            gameOver = !gameOver;
        }
        for(BodyNode body = snake.head.next; body != null; body = body.next){
            if(snake.head.row == body.row && snake.head.col == body.col){
                gameOver = !gameOver;
            }
        }
    }
}

