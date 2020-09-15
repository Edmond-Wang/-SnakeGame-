import java.awt.*;

public class Egg {
    public static final int NODE_LEN = GamePanel.SIDE_LEN;

    public int row;
    public int col;

    public void drawEgg(Graphics g) {
        //g.setColor(Color.yellow);
        setColor(GamePanel.eggC, g);
        g.fillOval(col*NODE_LEN+3,row*NODE_LEN+3, NODE_LEN, NODE_LEN);
    }

    public Egg() {
        this.row = (int)(1+Math.random()*27);
        this.col = (int)(1+Math.random()*34);
    }

    public Rectangle getRect(){
        return new Rectangle(col*NODE_LEN+3, row*NODE_LEN+3, NODE_LEN, NODE_LEN);
    }

    public void reset(Snake snake) {

        this.row = (int)(1+Math.random()*27);
        this.col = (int)(1+Math.random()*34);

        if(snake.eatEgg(this)){
            this.reset(snake);
        }
    }

    public static void setColor(int c, Graphics g) {
        if(c == 0){
            g.setColor(new Color(255, 180, 30));
        }else if(c == 1){
            g.setColor(Color.WHITE);
        }else if(c == 2){
            g.setColor(Color.RED);
        }
    }

}
