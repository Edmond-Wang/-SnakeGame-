import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class GameFrame extends JFrame {

    public static double ver = 0.3;
    void launchGame() {

        this.setTitle("SnakeGame");
        this.setBounds(50,50,940,650);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setResizable(false);

        Container container = this.getContentPane();
        container.setBackground(new Color(105, 135, 105));

        container.add(new GamePanel());
        container.add(new Menu());
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
    Snake snake = new Snake();
    Egg egg = new Egg();
    public static boolean running = false;
    public static boolean gameOver = false;
    public static int gameScore = 0;

    public GamePanel() {

        setBounds(15,15,
                COL*SIDE_LEN+6,
                ROW*SIDE_LEN+6
        );
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2,
                Color.WHITE)
        );
        this.setFocusable(true); // 重新设定焦点使得panel中的监听控件有效
        this.addKeyListener(new KeyMonitor());
        new Thread(new PaintThread()).start(); // 启动线程开始游戏
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
                state(); // 删了这句导致无法正常运行，原因待查
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

class Menu extends JPanel {

    JLabel version = new JLabel("version "+GameFrame.ver, JLabel.CENTER);
    JLabel scoreTitle = new JLabel("Score", JLabel.CENTER);
    JLabel score = new JLabel(GamePanel.gameScore+"", JLabel.CENTER);

    public Menu() {

        this.setLayout(null);

        setBounds(735, 15, 165, GamePanel.ROW*GamePanel.SIDE_LEN+6);
        setBackground(new Color(45, 115, 85, 255));
        setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,
                Color.WHITE)
        );

        version.setBounds(0, 530, 165, 20);
        version.setFont(new Font("仿宋", Font.ITALIC, 20));
        version.setForeground(new Color(14, 14, 14, 181));
        add(version);

        scoreTitle.setBounds(0, 20, 165, 30);
        scoreTitle.setFont(new Font("宋体", 1, 25));
        scoreTitle.setForeground(Color.WHITE);
        add(scoreTitle);

        score.setBounds(0, 70, 165, 50);
        score.setFont(new Font("宋体", 1, 30));
        score.setForeground(Color.ORANGE);
        add(score);

        new Thread(new MenuThread()).start();

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g1 = (Graphics2D)g;
        g1.setColor(Color.WHITE);
        g1.drawLine(0, 6*GamePanel.SIDE_LEN+3,
                165, 6*GamePanel.SIDE_LEN+3);
    }

    class MenuThread implements Runnable {

        @Override
        public void run() {
            while(!GamePanel.gameOver) {
                score.setText(GamePanel.gameScore+"");
            }

        }
    }
}

