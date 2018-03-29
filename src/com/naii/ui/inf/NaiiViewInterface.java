package com.naii.ui.inf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public abstract class NaiiViewInterface extends JComponent {

	protected String[] tits;
	protected Float[] vals;
	protected String unit = "f";

	protected Rectangle[] toolTips;
	
	protected Point toolTipPoint;
	protected int index;

	protected void addListener(final JComponent c){
		c.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				toolTipPoint = null;
				c.repaint();
			}
		});
		
		c.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if(toolTips == null){
					return;
				}
				
				Point p = e.getPoint();
				int i=0;
				for(Rectangle rec : toolTips){
					if(rec.contains(p)){
						toolTipPoint = p;
						index = i; 
						
						c.repaint();
						return;
					}
					i++;
				}
				if(toolTipPoint != null){
					toolTipPoint = null;
					c.repaint();
				}else{
					toolTipPoint = null;
				}
			}
			
		});
	}
	
	
	protected void drawToolTipPoint(Graphics g){
		// draw toolTip.
		if(toolTipPoint != null){
			g.setColor(new Color(255,255,255,150));
			Rectangle highlight = toolTips[index];
			g.fillRect(highlight.x, highlight.y, highlight.width, highlight.height);
			
			String str = vals[index]+" "+	this.unit;
			Font f = g.getFont();
			g.setFont(new Font("黑体", 1, 13));
			FontMetrics fm = g.getFontMetrics();
			int ww = fm.stringWidth(str);
			Dimension s = new Dimension(80, 30);
			g.setColor(new Color(255,255,255,200));
			
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING , RenderingHints.VALUE_ANTIALIAS_ON);
			int space = 5;
			if(getWidth() /2 > toolTipPoint.x){
				
				g.fillRoundRect(toolTipPoint.x + space, toolTipPoint.y - s.height/2, s.width, s.height, 8, 8);
				g.setColor(Color.gray);
				g.drawRoundRect(toolTipPoint.x + space, toolTipPoint.y - s.height/2, s.width, s.height, 8, 8);
				
				g.setColor(Color.black);
				g.drawString(str, toolTipPoint.x + space + s.width/2 - ww / 2 , toolTipPoint.y+ (s.height/2 - fm.getHeight()/2));
				g.setFont(f);
			}else{
				g.fillRoundRect(toolTipPoint.x - space - s.width, toolTipPoint.y - s.height/2, s.width, s.height, 8, 8);
				g.setColor(Color.gray);
				g.drawRoundRect(toolTipPoint.x - space - s.width, toolTipPoint.y - s.height/2, s.width, s.height, 8, 8);
				
				g.setColor(Color.black);
				g.drawString(str, toolTipPoint.x - space - s.width/2 -ww / 2 , toolTipPoint.y + (s.height/2 - fm.getHeight()/2));
				g.setFont(f);
			}
		}
	}
	
	public abstract BufferedImage createImage();
	
//	protected void drawTitle(Graphics g, int y){
//		g.setColor(Color.black);
//		FontMetrics fm = g.getFontMetrics();
//		int w = fm.stringWidth(title);
//		w = getWidth()/2 - w/2;
//		Font f = g.getFont();
//		g.setFont(new Font("微软雅黑", 1, 13));
//		g.drawString(title, w, y - (titleHeight/2 - fm.getHeight()/2));
//		g.setFont(f);
//	}
	
	public void resetData(String[] tits, Float[] vals){
		this.tits = tits;
		this.vals = vals;
	}
	
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getUnit() {
		return unit;
	}
	
}
