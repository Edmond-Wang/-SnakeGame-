import java.awt.*;

public class Egg {
    public static final int NODE_LEN = GamePanel.SIDE_LEN;

    public int row;
    public int col;

    public void drawEgg(Graphics g) {
        g.setColor(Color.yellow);
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

}
