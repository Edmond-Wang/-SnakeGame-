import java.awt.*;
import java.awt.event.KeyEvent;

class Snake {

    //初始化
    public BodyNode head;
    public BodyNode tail;
    public int length = 0;

    //初始化origin为蛇的出发起点
    public BodyNode origin = new BodyNode((int)(3+Math.random()*26),(int)(Math.random()*34), Direction.up);

    public Snake() {
        head = origin;
        tail = origin;
        length++;
    }

    public void drawSnake(Graphics g) {
        move();
        for(BodyNode node = head; node != null; node = node.next){
            node.drawNode(g);
        }
    }

    public void keyPress(KeyEvent e) {
        int key = e.getKeyCode();

        switch(key) { // 按键改变头节点方向
            case KeyEvent.VK_W:
                if(head.dir != Direction.down){
                    head.dir = Direction.up;
                }break;
            case KeyEvent.VK_S:
                if(head.dir != Direction.up){
                    head.dir = Direction.down;
                }break;
            case KeyEvent.VK_A:
                if(head.dir != Direction.right){
                    head.dir = Direction.left;
                }break;
            case KeyEvent.VK_D:
                if(head.dir != Direction.left){
                    head.dir = Direction.right;
                }break;
        }

    }

    public void headAdd() { // 移动时头节点增加
        BodyNode node = null;
        switch (head.dir){
            case up:
                node = new BodyNode(head.row-1, head.col, head.dir);
                break;
            case down:
                node = new BodyNode(head.row+1, head.col, head.dir);
                break;
            case left:
                node = new BodyNode(head.row, head.col-1, head.dir);
                break;
            case right:
                node = new BodyNode(head.row, head.col+1, head.dir);
                break;
        }

        node.next = head;
        head.pre = node;
        head = node;
    }

    public void tailDel() { // 移动时尾节点删除
        BodyNode node = tail.pre;
        tail = null;
        node.next = null;
        tail = node;
    }

    public void move() {
        headAdd();
        tailDel();
    }

    public Rectangle getRect() {
        int NODE_LEN = GamePanel.SIDE_LEN;
        return new Rectangle(head.col*NODE_LEN+3, head.row*NODE_LEN+3, NODE_LEN, NODE_LEN);
    }

    public boolean eatEgg(Egg egg){

        if(this.getRect().intersects(egg.getRect())){
            headAdd();
            egg.reset(this);
            return true;
        }else
            return false;
    }

}

class BodyNode {

    public static final int NODE_LEN = GamePanel.SIDE_LEN;

    public int row;
    public int col;
    public Direction dir;

    public BodyNode pre;
    public BodyNode next;

    public BodyNode(int row, int col, Direction dir) {
        this.row = row;
        this.col = col;
        this.dir = dir;
    }

    public void drawNode(Graphics g) {
        g.setColor(Color.GRAY);
        g.fillRect(col*NODE_LEN+3,row*NODE_LEN+3, NODE_LEN, NODE_LEN);
    }
}

enum Direction {
    up, down, left, right
}
