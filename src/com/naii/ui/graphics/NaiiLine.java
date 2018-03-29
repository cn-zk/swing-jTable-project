package com.naii.ui.graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JLabel;

import com.naii.ctr.NaiiControl;
import com.naii.ui.inf.NaiiViewInterface;

import test.assembly.TestFrame;

@SuppressWarnings("serial")
public class NaiiLine extends NaiiViewInterface{

	private int thick = 20;
	
	private int width = 300;
	
	private Insets margin = new Insets(0, 5, 5, 5);
	private Insets margin_ = new Insets(0, 5, 5, 5);
	
	private boolean autoHeight = true;
	private JComponent c;
	private JLabel t ;
	
	public NaiiLine(String unit) {
		this();
		setUnit(unit);
		t.setVisible(false);
	}
	
	public NaiiLine(String unit, String title) {
		this(unit);
		t.setText(title);
		t.setVisible(true);
	}
	
	public NaiiLine() {
		createView();
		addListener(c);
	}

	private void createView() {
		c = new JComponent() {
			@Override
			public void paint(Graphics g) {
			
				paintImg(g, getSize(), false);
				
				drawToolTipPoint(g);
//				g.drawRect(margin.left, margin.top, d.width, d.height);
			}
		};
		setLayout(new BorderLayout());
		
		add(t = new JLabel("", 0), BorderLayout.NORTH);
		t.setText(null);
		add(c, BorderLayout.CENTER);
		t.setOpaque(true);
		t.setBackground(Color.white);
		t.setFont(new Font("微软雅黑", 0, 14));
		t.setPreferredSize(new Dimension(1, 25));
	}

	public void resetData(String[] tits, Float[] vals) {
		super.resetData(tits, vals);
		toolTips = new Rectangle[tits.length];
		if(autoHeight){
			c.setPreferredSize(new Dimension(width, thick * tits.length + 50));
		}
		
		revalidate();
	}
	
	@Override
	public BufferedImage createImage(){
		Dimension d = new Dimension(width, thick * tits.length + 60);
		BufferedImage buf = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		
		Graphics g = buf.createGraphics();
		paintImg(g, d, true);
		FontMetrics fm = g.getFontMetrics();
		int w = fm.stringWidth(t.getText());
		g.setColor(Color.black);
		g.drawString(t.getText(), d.width/2 - w/2, margin_.top + fm.getAscent()/2);
		g.dispose();
		
		return buf;
	}
	
	private void paintImg(Graphics g, Dimension size, boolean num){
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, size.width, size.height);
		
		// get string size.
		Dimension d = getViewSize(g);
		
		// compute top.
		int th = size.height/ 2 - d.height/2;
		if(th > margin.top){
			margin_.top = th;
		}
		// new rectangle.
		Rectangle rec = new Rectangle(margin_.left, margin_.top, d.width, d.height);
		if(num){
			rec.y += 10;
		}
		
		// draw tits.
		String keys[] = drawString(g, rec);
		
		// draw bra.
		drawLine(g, rec, keys, size, num);
	}
	
	/**
	 * 获得视图大小
	 * @param g
	 * @return
	 */
	private Dimension getViewSize(Graphics g){
		int max = 0, t = 0;
		
		FontMetrics fm = g.getFontMetrics();
		
		for(String key : tits){
			t = fm.stringWidth(key);
			if(max < t){
				max = t ;
			}
		}
		return new Dimension( max, thick * tits.length + 25);
	}
	
	/**
	 * 绘制字符标题
	 * @param g
	 * @param rec
	 * @return
	 */
	private String[] drawString(Graphics g, Rectangle rec){
		g.setColor(Color.BLACK);
		
		FontMetrics fm = g.getFontMetrics();
		// 字体高度
		int h = fm.getHeight();
		
		// 字体高度补偿值
		int tb = 2;
		
		// 计算后得到的居中值
		h = thick/2 - h/2 + h- tb;
		
		String[] keys = new String[tits.length];
		int i =0;
		for(String key : tits){
			keys[i] = key;
			g.drawString(key, rec.x, rec.y + (i++*thick+h));
		}
		
		g.setColor(Color.GRAY);
		int ts = 5;
		for(i=0;i<tits.length-1;i++){
			int y = rec.y + thick * (i+1);
			g.drawLine(rec.width + rec.x, y, rec.x + rec.width + ts, y);
		}
		g.drawLine(rec.x+rec.width+ts, rec.y , rec.x+rec.width+ts, rec.y + rec.height- 15);
		return keys;
	}
	
	/**
	 * 绘制数值条
	 * @param g
	 * @param rec
	 * @param keys
	 * @param size 
	 * @param num 
	 */
	private void drawLine(Graphics g, Rectangle rec, String[] keys, Dimension size, boolean num){
		g.setColor(Color.ORANGE);

		int ts = 5, sp = 2, w = size.width -rec.x -rec.width-ts*2 - margin_.right;
		
		float max = 0;
		for(Float f : vals){
			if(max < f){
				max = f;
			}
		}
		
		// check two
		float maxx = max;
		while(max % 5 != 0 || maxx == max){
			++ max;
		}
		
		FontMetrics fm = g.getFontMetrics();
		
		for(int i=0;i<keys.length;i++){
			Float f = vals[i];
			int v = (int)(w  * f/max);	
			g.fillRect(rec.x+rec.width+ts*2 , rec.y + (thick * i) + sp, v, thick - sp);
			toolTips[i] = new Rectangle(rec.x+rec.width+ts*2 , rec.y + (thick * i) + sp, v, thick - sp);
			
			if(num){
				Rectangle sr = toolTips[i];
				g.setColor(Color.gray);
				String str = f+" "+unit;
				int sw = fm.stringWidth(str);
				
				int x = sw < sr.width ? sr.x + sr.width - sw : sr.x + sr.width + 5;
				
				g.drawString(str, x, sr.y + sr.height/2 + fm.getAscent()/2);
				g.setColor(Color.orange);
			}
		}
		
		// draw bottom line.
		int tx = rec.x+rec.width+ts,
				ty = rec.y + rec.height-15,
				tw = size.width - margin_.right;
			
		g.setColor(Color.GRAY);
		g.drawLine(tx, ty, tw, ty);
		
		int t = (int) (max/5);
		int lw = w / t;
		
		
		if(t < 10){
			// draw bottom number.
			for(int i=1;i< t;i++){
				g.setColor(Color.GRAY);
				g.drawLine(lw * i + rec.x + rec.width + 10, ty-5, 
						lw * i + rec.x + rec.width + 10, ty);
				g.setColor(Color.BLACK);
				g.drawString(i*5 +"", lw * i + rec.x + rec.width + 5, ty+15);
			}	
		}else{
			for(int i : new int[]{0, t/2, t-1}){
				g.setColor(Color.GRAY);
				g.drawLine(lw * i + rec.x + rec.width + 10, ty-5, 
						lw * i + rec.x + rec.width + 10, ty);
				g.setColor(Color.BLACK);
				g.drawString(i*5 +"", lw * i + rec.x + rec.width + 5, ty+15);
			}
		}
	}

	public void setDefaultWidth(int width){
		this.width = width;
	}
	
	public static void main(String[] args) throws Exception {
		
		NaiiControl.getControl().testLoad();
		
		new TestFrame() {
			
			@Override
			public Component getContent() {
				return new NaiiLine("f", "22222222222"){
					{
						String[] tit = new String[]{
								"未用设备",
								"笔记本",
								"个人设备",
								"台式机",
								"设备总数"
						};
						Float[] fl = NaiiControl.getControl().loadEquipmentViewFloat();
						fl[0] = 309f;
						fl[1] = 500f;
								fl[3] = 103f;
						resetData(tit, fl);
					}
				};
			}
		}.show();
	}
}
