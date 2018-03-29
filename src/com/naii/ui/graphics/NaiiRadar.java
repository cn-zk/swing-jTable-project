package com.naii.ui.graphics;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.naii.ctr.NaiiControl;
import com.naii.db.NaiiConfig;
import com.naii.db.NaiiProperty;
import com.naii.db.dto.NaiiJsonDto;
import com.naii.db.dto.NaiiValue;
import com.naii.tools.NaiiLog;

import test.assembly.TestFrame;

/**
 * 雷达图
 * @author TouchWin
 *
 */
@SuppressWarnings("serial")
public class NaiiRadar extends JPanel{
	
	private static final int POINT_R = 4;
	private static final int DEFAULT_STROKE = 3;
	
	private NaiiJsonDto[] datas;
	private NaiiValue[] items;
	
	private String[] titles;
	
	private Color[] colors = new Color[]{
			new Color(67,67,72),
			new Color(124,181,236),
			new Color(255,101,0)
	};
	
	public NaiiRadar(String[] titles) {
		this.titles = titles;
		setBackground(Color.white);
		setLayout(new BorderLayout());
		
		add(new NaiiRadarInner(), BorderLayout.CENTER);
	}

	public void resetData(NaiiValue[] names, NaiiJsonDto... loadCache) {
		datas = loadCache;
		items = names;
		repaint();
	}
	
	class NaiiRadarInner extends JComponent{
		
		Point pm ;
		
//		public NaiiRadarInner() {
//			super();
//			addMouseMotionListener(new MouseMotionAdapter() {
//				@Override
//				public void mouseMoved(MouseEvent e) {
//					pm = e.getPoint();
//					repaint();
//				}
//			});
//			addMouseListener(new MouseAdapter() {
//				@Override
//				public void mouseExited(MouseEvent e) {
//					pm = null;
//					repaint();
//				}
//			});
//		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			
			Graphics2D g2 = ((Graphics2D)g);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			Dimension d = getSize();

			Point p = new Point(d.width /2, d.height /2);
			
			double r = (d.width > d.height ? d.height : d.width) / 2;
			r -= 30;
			
			drawRadar((Graphics2D) g, p, r);
			drawRadar((Graphics2D) g, p, r/3 * 2);
			drawRadar((Graphics2D) g, p, r/3);
			
			drawRadarMap((Graphics2D) g, p, r + 10);
			
			if(pm != null){
				g.drawLine(pm.x, pm.y, p.x, p.y);
			}
			
			paintString(g2);
			
//			g.drawArc((int)(p.x - r),(int)(p.y -r), (int)(r * 2), (int)(r * 2), 0, 360);
		}
		
		private Point drawPoint(Point p, double r, int index){
			double rr = r /2;
			double c = Math.sqrt(Math.pow(r, 2) - Math.pow(rr, 2));
			int[] xPoints = new int[]{
					p.x,
					(int) (p.x - c),
					(int) (p.x - c),
					p.x,
					(int) (p.x + c),
					(int) (p.x + c)
				};
			int[] yPoints = new int[]{
					(int) (p.y - r),
					(int) (p.y - c/2),
					(int) (p.y + c/2),
					(int) (p.y + r),
					(int) (p.y + c/2),
					(int) (p.y - c/2)
				};
			return new Point(xPoints[index], yPoints[index]);
		}
		
		private void drawRadar(Graphics2D g, Point p, double r){
			
			double rr = r /2;
			double c = Math.sqrt(Math.pow(r, 2) - Math.pow(rr, 2));
			g.setColor(new Color(191, 191, 191));
			int[] xPoints = new int[]{
					p.x,
					(int) (p.x - c),
					(int) (p.x - c),
					p.x,
					(int) (p.x + c),
					(int) (p.x + c)
				};
			int[] yPoints = new int[]{
					(int) (p.y - r),
					(int) (p.y - c/2),
					(int) (p.y + c/2),
					(int) (p.y + r),
					(int) (p.y + c/2),
					(int) (p.y - c/2)
				};
			
			g.drawPolygon(xPoints, yPoints, 6);
			
			g.drawLine(xPoints[0], yPoints[0], xPoints[3], yPoints[3]);
			g.drawLine(xPoints[1], yPoints[1], xPoints[4], yPoints[4]);
			g.drawLine(xPoints[2], yPoints[2], xPoints[5], yPoints[5]);
		}
		
		private void drawRadarMap(Graphics2D g, Point p, double r){
			if(datas == null){
				return;
			}
			double rr = r /2;
			double c = Math.sqrt(Math.pow(r, 2) - Math.pow(rr, 2));
			g.setColor(Color.black);
			g.setFont(new Font("微软雅黑", 0, 14));
			int[] xPoints = new int[]{
					p.x,
					(int) (p.x - c),
					(int) (p.x - c),
					p.x,
					(int) (p.x + c),
					(int) (p.x + c)
				};
			int[] yPoints = new int[]{
					(int) (p.y - r),
					(int) (p.y - c/2),
					(int) (p.y + c/2),
					(int) (p.y + r),
					(int) (p.y + c/2),
					(int) (p.y - c/2)
				};
			

			int max = 0, i = 0;
			
			// check max value;
			for(int z=0;z<datas.length;z++){
				for(Entry<String, String> entry : datas[z].getJSON_MAP().entrySet()){
					if(Float.parseFloat(entry.getValue()) > max){
						max = (int) Float.parseFloat(entry.getValue());
					}
				}
			}
			
			// draw items;
			for(NaiiValue v : items){
				int w = g.getFontMetrics().stringWidth(v.getName()) /2;
				g.drawString(v.getName(), xPoints[i]-w, yPoints[i]+5);
				i++;
			}
			
			// check two
			float maxx = max;
			while(max % 5 != 0 || max % 3 != 0 || maxx == max){
				++ max;
			}

			// paint number
			g.setFont(new Font("微软雅黑", 0, 13));
			int f = max /3, pf = (int) ((r-20) /3);
			for(i=0;i<3;i++){
				g.drawString(""+(f + f* i), p.x, p.y - (pf + pf * i));
			}
			
			r = r -10;

			// draw value.
			for(int z=0;z<datas.length;z++){
				g.setColor(colors[z]);
				i=0;
				
				for(NaiiValue nv : items){
					Float v = Float.parseFloat(datas[z].getJSON_MAP().get(nv.getId()));
					double rn = v/(double)max * r;
					Point pt = drawPoint(p, rn, i);
					xPoints[i] = pt.x;
					yPoints[i] = pt.y;
					
					g.fillOval(pt.x-POINT_R, pt.y-POINT_R, POINT_R*2, POINT_R*2);
					++i;
				}
				g.setStroke(new BasicStroke(DEFAULT_STROKE));
				g.drawPolygon(xPoints, yPoints, 6);
			}
		}
		
		public void paintString(Graphics g) {

			g.setFont(new Font("微软雅黑", 1, 12));
			
			for(int i=0;i<titles.length;i++){
				
				g.setColor(colors[i]);
				g.drawString(titles[i], 25, i*30+23);
				
				Graphics2D g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(1));
				g2.fillOval(7, i*30+20-POINT_R, POINT_R*2, POINT_R*2);
				g2.setStroke(new BasicStroke(DEFAULT_STROKE));
				g2.drawLine(0, i*30+20, 20, i*30+20);
			}
			
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		NaiiControl.getControl().testLoad();
		new TestFrame() {
			
			@Override
			public Component getContent() {
				// TODO Auto-generated method stub
				return new NaiiRadar(new String[]{
						"过去12个月",
						"最近3个月",
						"当前月"
					}){
					{
						String id = "29851618433924";
						NaiiLog.log("event="+id);
						resetData(
								NaiiProperty.getProperty().getFormats(NaiiConfig.KEY_EVENT, NaiiProperty.CACHE_EVENT),
								NaiiControl.getControl().loadEventCache(
								id,
								12),
								NaiiControl.getControl().loadEventCache(
								id,
								3),
								NaiiControl.getControl().loadEventCache(
								id,
								1));
					}
				};
			}
		}.show();
	}
}
