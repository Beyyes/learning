package blcakwhite;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ChessPanel extends JPanel{
    
    // 棋盘参数设置
    static int top = 100; // 离顶端间隔
    static int gap = 30; // button大小,高度
    
    int n = 10; // 棋盘大小
    int color = -1; //-1表示初始,0表示白色,1表示黑色
    
    int status[][] = new int[n][n]; //棋盘状态,-1表示无子,0表示白棋,1表示黑棋
    int step = 40; // 棋盘方块之间的间隔
    int len = step * n;
    int width = step*n;
    
    
    public ChessPanel() {
        width = step * n;
        this.setBounds(0, 0, width, width);
        this.setVisible(true);
        this.setLayout(null);
        JButton startButton = new JButton("开始");
        JButton endButton = new JButton("结束");
        JButton restartButton = new JButton("重置");
        this.add(startButton);
        this.add(endButton);
        this.add(restartButton);
        startButton.setBounds(gap, gap, 80, gap);  // Panel没设置layout布局，必须设置button的bounds
        endButton.setBounds(gap+80, gap, 80, gap);
        restartButton.setBounds(gap+80+80, gap, 80, gap);
    
        // 对按钮设置监听状态
        startButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent arg0) {
                init();
            }
        });
        
        // 对按钮设置监听状态
        endButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(ChessPanel.this, "hehe");
            }
        });
        
        this.addMouseListener(new MouseListener() {
            
            @Override
            public void mouseReleased(MouseEvent e) {
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX(), y = e.getY();
                System.out.println("点击坐标:" + e.getX() +","+ e.getY());
                int i = x / step, j = y / step;
                
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
            }
        });
    }
    
    
    /**
     * 初始化棋盘状态
     */
    public void init() {
        Arrays.fill(status, -1);
        color = 0; // 初始化先白子下棋
        // 开始画棋盘
        
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        drawBoard(g);
        
        for (int i = 0;i < n;i++) {
            for (int j = 0;j < n;j++) {
                drawChess(g, i, j, color);
            }
        }
    }
    
    public void drawBoard(Graphics g){
        g.setColor(Color.BLACK);
        // 画横线, 两y坐标保持一致
        for (int i = 0;i <= n;i++) {
            g.drawLine(0, top+step*i, width, top+step*i); 
        }
        // 画竖线, 两x坐标保持一致,y始终不变
        for (int i = 0;i <= n;i++) {
            g.drawLine(i*step, top, i*step, top+width); 
        }
    }
    
    public void drawChess(Graphics g, int i, int j, int color){
//        if(color == -1) {
//            return;
//        }
        g.setColor(Color.black);
        //int x = 
        g.fillOval(i, j, step, step);
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("main frame");
        ChessPanel panel = new ChessPanel();
        frame.setContentPane(panel);
        System.out.println(panel.getWidth());
        frame.setBounds(200, 200, panel.getWidth()+30, panel.getHeight()+top+50); // top-left的位置(x,y)以及矩阵的长宽
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
