package com.naii.ui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.naii.tools.PaintTool;
import com.naii.tools.Threading;

@SuppressWarnings("serial")
public class NaiiLoadView extends JDialog{
	Image image = null,image1;
	
	JTextArea text ;
	JScrollPane js;
	JComponent comp;
	
	boolean print = true;
	
	public NaiiLoadView(final Runnable run) {
		
		setUndecorated(true);
		setAlwaysOnTop(true);
		setSize(new Dimension(350, 350));
		setBackground(Color.white);
//		setResizable(false);
		try {
			image = image1 = ImageIO.read(getClass().getResourceAsStream("bg.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final Dimension d = PaintTool.adjustImageSize(image.getWidth(null), image.getHeight(null), 350, 350, PaintTool.ASTRICT_WIDHT_AND_HEIGHT);
		add(comp = new JComponent() {
			{
				setLayout(new BorderLayout());
				
				add(js = new JScrollPane(text = new JTextArea(){
					{
						setFont(new Font("微软雅黑",0,10));
						setBorder(null);
						setOpaque(false);
						setLineWrap(true);
						setFocusable(false);
						setForeground(Color.cyan);
					}
				}){
					{
						getViewport().setOpaque(false);
						setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
						setOpaque(false);
						setBorder(null);
					}
				});
				
				
				new Threading() {
					int h= 0, ih, iw;
					float f=0.5f;
					@Override
					public boolean runner() throws InterruptedException {
						BufferedImage im = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_RGB);
						for(int x=0;x<im.getWidth();x++){
							for(int y = 0;y < ih ; y ++){
								Color c = PaintTool.getPointRGB((BufferedImage)image1, x, y);
								if(c == null)
									break;
								int rgb = c.getRGB();
								if(y < h){
									 
									rgb = 256-
									c.getRGB();
								}
								im.setRGB(x, y, rgb);
							}
						}
						image = im;
//						sleep(500);
						comp.repaint();
						int s = (int)((float)(ih-h) * f);
						if(s < 1){
							s = 1;
						}
						h += s;
						return h <= ih;
					}
					public void run() {
						ih = image1.getHeight(null); 
						iw = image1.getWidth(null);
						super.run();
						if(run != null)
							run.run();
					};
				}.start();
				
			}
			@Override
			public void paint(Graphics g) {
				g.drawImage(image, 0, 0, d.width, d.height, this);
				super.paint(g);
			}
		}, BorderLayout.CENTER);
		
		setBackground(Color.white);
		
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	
	public void setText(String t) {
		text.setText(text.getText()+"\n"+t);
		js.getVerticalScrollBar().setValue(text.getHeight());
		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isPrint(){
		return print;
	}

	public void hidden() {
		print = false;
		dispose();
	}
	
//	public static void main(String[] args) {
//		new NaiiLoadView(null);
//	}
}
