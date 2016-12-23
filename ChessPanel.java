package Five;

import java.awt.Panel;

import javax.swing.JFrame;

public class ChessPanel extends Panel{
	
	// 棋盘参数设置
	int height = 300;
	int width = 300;
	
	
	public ChessPanel() {
		this.setBounds(0, 0, width, height);
	}
	
	public static void main(String[] args) {
		//System.out.println("why not you");
		
		JFrame frame = new JFrame("main frame");
		frame.setContentPane(new ChessPanel());
		frame.setBounds(200, 200, 500, 600); // top-left的位置(x,y)以及矩阵的长宽
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
