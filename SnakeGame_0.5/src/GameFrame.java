import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.PublicKey;

class GameFrame extends JFrame {

    public static double ver = 0.5;
    void launchGame() {

        this.setTitle("SnakeGame");
        this.setBounds(50,50,940,650);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setResizable(false);

        Container container = this.getContentPane();
        //container.setBackground(new Color(105, 135, 105, 255));

        GamePanel gp = new GamePanel(this);
        container.add(gp);
        container.add(new Menu(gp, this));

        this.setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        setColor(GamePanel.interfaceC);
    }

    public void setColor(int c) {
        if(c == 0){
            this.getContentPane().setBackground(new Color(105, 135, 105, 255));
        }else if(c == 1){
            this.getContentPane().setBackground(new Color(213, 183, 139, 255));
        }else if(c == 2){
            this.getContentPane().setBackground(new Color(13, 163, 163, 255));
        }
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
    static Snake snake = new Snake();
    static Egg egg = new Egg();
    public static boolean running = false;
    public static boolean gameOver = false;
    public static int gameScore = 0;
    public static String gameStatus;

    public static int eggC = 0;
    public static int snakeC = 0;
    public static int interfaceC = 0;

    GameFrame gf;

    public GamePanel(GameFrame gf) {

        this.gf = gf;
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
                    gf.repaint();
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
            gameStatus = "<html>游戏进行中<br><br><font color=\"\">空格以暂停</font></html>";
        else if(!running && !gameOver)
            gameStatus = "<html>游戏暂停中<br><br>空格以继续</html>";
        else
            gameStatus = "<html>" + "游戏已结束" + "<br>" +
                    "&nbsp你的蛇" + "<br>" + "挂&nbsp掉&nbsp了" + "</html>";

        Menu.status.setText(GamePanel.gameStatus);
    }

    public void stop() {
        running = false;
    }

}

class Menu extends JPanel {

    GamePanel gp;
    JLabel version = new JLabel("version "+GameFrame.ver, JLabel.CENTER);
    JLabel author = new JLabel("by Ed.W", JLabel.CENTER);
    static JLabel status = new JLabel("游戏暂停中", JLabel.CENTER);
    JLabel scoreTitle = new JLabel("Score", JLabel.CENTER);
    JLabel score = new JLabel(GamePanel.gameScore+"", JLabel.CENTER);
    JButton restart = new JButton("新游戏");
    JButton instruction = new JButton("操作说明");
    JButton settings = new JButton("游戏设置");

    public Menu(GamePanel gp, GameFrame gf) {

        this.gp = gp;
        this.setLayout(null);

        setBounds(735, 15, 165, GamePanel.ROW*GamePanel.SIDE_LEN+6);
        setBackground(new Color(45, 115, 85, 255));
        setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,
                Color.WHITE)
        );

        version.setBounds(0, 510, 165, 20);
        version.setFont(new Font("仿宋", Font.ITALIC, 20));
        version.setForeground(new Color(14, 14, 14, 181));
        add(version);

        author.setBounds(0,535, 165, 20);
        author.setFont(new Font("Segoe Print",Font.BOLD, 16));
        author.setForeground(new Color(38, 36, 36));
        add(author);

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
        score.setForeground(scoreC(GamePanel.eggC));
        add(score);

        restart.setBounds(32, 160, 99, 40);
        restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gp.restartGame();
                gp.requestFocus(true);
            }
        });
        add(restart);

        instruction.setBounds(32, 230, 99, 40);
        instruction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gp.stop();
                new Message(gf);
                gp.requestFocus(true);

            }
        });
        add(instruction);

        settings.setBounds(32, 300, 99, 40);
        settings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gp.stop();
                new Options(gf);
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
        g1.drawLine(0, 19*GamePanel.SIDE_LEN+3,
                165, 19*GamePanel.SIDE_LEN+3);
        setColor(GamePanel.interfaceC);
    }

    class MenuThread implements Runnable {

        @Override
        public void run() {
            while(!GamePanel.gameOver) {
                score.setText(GamePanel.gameScore+"");
                score.setForeground(scoreC(GamePanel.eggC));
                //GamePanel.judgeStatus();
                //status.setText(GamePanel.gameStatus);
            }

        }
    }

    static class Message extends JDialog {
        public Message(GameFrame gf){
            super(gf, "操作说明", true);
            this.setBackground(Color.green);
            String message = "<html><font color=\"red\">" +
                    "空格键：暂停游戏/继续游戏" +
                    "</font><br><br>" +
                    "<font color=\"blue\">" +
                    "W A S D：控制蛇 上 下 左 右 移动" +
                    "</font><br><br><font color=\"green\">" +
                    "其他：右下角为状态栏" + "</font></html>";
            Container container = getContentPane();
            JLabel m = new JLabel(message, JLabel.CENTER);
            m.setFont(new Font("宋体", 1, 18));
            m.setBackground(Color.green);
            container.add(m);
            setBounds(1000,100,500,300);
            this.setVisible(true);
        }
    }

    public void setColor(int c) {
        if(c == 0){
            this.setBackground(new Color(45, 115, 85, 255));
        }else if(c == 1){
            this.setBackground(new Color(135, 115, 85, 255));
        }else if(c == 2){
            this.setBackground(new Color(13, 70, 80, 255));
        }
    }

    public Color scoreC(int c) {
        if(c == 0){
            return(new Color(255, 180, 30));
        }else if(c == 1){
            return Color.WHITE;
        }else if(c == 2){
            return Color.RED;
        }else
            return null;
    }

    static class Options implements ItemListener {

        JLabel eggColor = new JLabel("蛋的颜色：");
        JRadioButton eggColor1 = new JRadioButton("默认", (GamePanel.eggC==0));
        JRadioButton eggColor2 = new JRadioButton("白色", (GamePanel.eggC==1));
        JRadioButton eggColor3 = new JRadioButton("红色", (GamePanel.eggC==2));

        JLabel snakeColor = new JLabel("蛇的颜色：");
        JRadioButton snakeColor1 = new JRadioButton("默认", (GamePanel.snakeC==0));
        JRadioButton snakeColor2 = new JRadioButton("绿色", (GamePanel.snakeC==1));
        JRadioButton snakeColor3 = new JRadioButton("蓝色", (GamePanel.snakeC==2));

        JLabel interfaceColor = new JLabel("界面颜色：");
        JRadioButton interfaceColor1 = new JRadioButton("默认", (GamePanel.interfaceC==0));
        JRadioButton interfaceColor2 = new JRadioButton("样式1", (GamePanel.interfaceC==1));
        JRadioButton interfaceColor3 = new JRadioButton("样式2", (GamePanel.interfaceC==2));

        public Options(GameFrame gf) {
            JDialog op = new JDialog(gf, "设置", true);
            op.setLayout(null);
            op.setBounds(1000,100,500,300);

            //*****************************************************************************

            eggColor.setBounds(30,20,150,30);
            eggColor.setFont(new Font("宋体", 1, 20));
            eggColor1.setBounds(180,20,100,30);
            eggColor2.setBounds(280,20,100,30);
            eggColor3.setBounds(380,20,100,30);

            ButtonGroup eggColorG = new ButtonGroup();
            eggColorG.add(eggColor1);
            eggColorG.add(eggColor2);
            eggColorG.add(eggColor3);

            //*****************************************************************************

            snakeColor.setBounds(30,80,150,30);
            snakeColor.setFont(new Font("宋体", 1, 20));
            snakeColor1.setBounds(180,80,100,30);
            snakeColor2.setBounds(280,80,100,30);
            snakeColor3.setBounds(380,80,100,30);

            ButtonGroup snakeColorG = new ButtonGroup();
            snakeColorG.add(snakeColor1);
            snakeColorG.add(snakeColor2);
            snakeColorG.add(snakeColor3);

            //*****************************************************************************

            interfaceColor.setBounds(30,140,150,30);
            interfaceColor.setFont(new Font("宋体", 1, 20));
            interfaceColor1.setBounds(180,140,100,30);
            interfaceColor2.setBounds(280,140,100,30);
            interfaceColor3.setBounds(380,140,100,30);

            ButtonGroup interfaceColorG = new ButtonGroup();
            interfaceColorG.add(interfaceColor1);
            interfaceColorG.add(interfaceColor2);
            interfaceColorG.add(interfaceColor3);

            //*****************************************************************************

            op.add(eggColor);
            op.add(eggColor1);
            op.add(eggColor2);
            op.add(eggColor3);
            op.add(snakeColor);
            op.add(snakeColor1);
            op.add(snakeColor2);
            op.add(snakeColor3);
            op.add(interfaceColor);
            op.add(interfaceColor1);
            op.add(interfaceColor2);
            op.add(interfaceColor3);

            eggColor1.addItemListener(this);
            eggColor2.addItemListener(this);
            eggColor3.addItemListener(this);
            snakeColor1.addItemListener(this);
            snakeColor2.addItemListener(this);
            snakeColor3.addItemListener(this);
            interfaceColor1.addItemListener(this);
            interfaceColor2.addItemListener(this);
            interfaceColor3.addItemListener(this);

            op.setVisible(true);



        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            if(e.getSource() == eggColor1) {
                GamePanel.eggC = 0;
            }else if(e.getSource() == eggColor2){
                GamePanel.eggC = 1;
                System.out.println(GamePanel.eggC);
            }else if(e.getSource() == eggColor3){
                GamePanel.eggC = 2;
            }

            if(e.getSource() == snakeColor1){
                GamePanel.snakeC = 0;
            }else if(e.getSource() == snakeColor2){
                GamePanel.snakeC = 1;
            }else if(e.getSource() == snakeColor3){
                GamePanel.snakeC = 2;
            }

            if(e.getSource() == interfaceColor1){
                GamePanel.interfaceC = 0;
            }else if(e.getSource() == interfaceColor2){
                GamePanel.interfaceC = 1;
            }else if(e.getSource() == interfaceColor3){
                GamePanel.interfaceC = 2;
            }
        }
    }
}

