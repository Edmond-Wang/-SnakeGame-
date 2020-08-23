import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class GameFrame extends JFrame {

    public static double ver = 0.4;
    void launchGame() {

        this.setTitle("SnakeGame");
        this.setBounds(50,50,940,650);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setResizable(false);

        Container container = this.getContentPane();
        container.setBackground(new Color(105, 135, 105));

        GamePanel gp = new GamePanel();
        container.add(gp);
        container.add(new Menu(gp));

        this.setVisible(true);
    }

    public static void main(String[] args) {

        new GameFrame().launchGame();
    }
}

class GamePanel extends JPanel {

    public static final int ROW = 28;
    public static final int COL = 35;
    public static final int SIDE_LEN = 20;
    static Snake snake = new Snake();
    static Egg egg = new Egg();
    public static boolean running = false;
    public static boolean gameOver = false;
    public static int gameScore = 0;
    public static String gameStatus;

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
            judgeStatus();
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

    public void restartGame() {
        snake = new Snake();
        egg = new Egg();
        gameScore = 0;
        repaint();
        if(running)
            running = false;
        if(gameOver)
            gameOver = false;
    }

    public static void judgeStatus() {
        if(running && !gameOver)
            gameStatus = "游戏运行中";
        else if(!running && !gameOver)
            gameStatus = "游戏暂停中";
        else
            gameStatus = "<html>" + "游戏已结束" + "<br>" +
                    "&nbsp你的蛇" + "<br>" + "挂&nbsp掉&nbsp了" + "</html>";

        Menu.status.setText(GamePanel.gameStatus);
    }

}

class Menu extends JPanel {

    GamePanel gp;
    JLabel version = new JLabel("version "+GameFrame.ver, JLabel.CENTER);
    static JLabel status = new JLabel("游戏暂停中", JLabel.CENTER);
    JLabel scoreTitle = new JLabel("Score", JLabel.CENTER);
    JLabel score = new JLabel(GamePanel.gameScore+"", JLabel.CENTER);
    JButton restart = new JButton("新游戏");
    JButton instruction = new JButton("操作说明");
    JButton settings = new JButton("游戏设置");

    public Menu(GamePanel gp) {

        this.gp = gp;
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

        status.setBounds(0, 400, 165, 80);
        status.setFont(new Font("宋体", Font.ITALIC, 20));
        status.setForeground(Color.WHITE);
        add(status);

        scoreTitle.setBounds(0, 20, 165, 30);
        scoreTitle.setFont(new Font("宋体", 1, 25));
        scoreTitle.setForeground(Color.WHITE);
        add(scoreTitle);

        score.setBounds(0, 70, 165, 50);
        score.setFont(new Font("宋体", 1, 30));
        score.setForeground(Color.ORANGE);
        add(score);

        restart.setBounds(32, 160, 99, 40);
        restart.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                gp.restartGame();
                gp.requestFocus(true);


            }
        });
        add(restart);

        instruction.setBounds(32, 230, 99, 40);
        instruction.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                gp.requestFocus(true);
            }
        });
        add(instruction);

        settings.setBounds(32, 300, 99, 40);
        settings.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                gp.requestFocus(true);
            }
        });
        add(settings);

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
                //GamePanel.judgeStatus();
                //status.setText(GamePanel.gameStatus);
            }

        }
    }
}

