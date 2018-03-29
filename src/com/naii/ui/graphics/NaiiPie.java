package com.naii.ui.graphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import com.naii.ui.inf.NaiiViewInterface;

import test.assembly.TestFrame;

@SuppressWarnings("serial")
public class NaiiPie extends NaiiViewInterface {
	
	public NaiiPie() {
		
	}
	
	public NaiiPie(String unit) {
		this();
		setUnit(unit);
	}
	
	@Override
	public void paint(Graphics g) {
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		
		Dimension d = getSize();
		Point cent = new Point(d.width/2, d.height/2);
		
		int s = cent.x > cent.y ? cent.y : cent.x;
		int r = cent.x > cent.y ? d.height : d.width;
		
		
		g.setColor(Color.orange);
		g.fillOval(cent.x - s , cent.y - s, r, r);
	}
	
	public void resetData(String[] tit, Float f){
		
	}
	
	public static void main(String[] args) {
		new TestFrame() {
			
			@Override
			public Component getContent() {
				return new NaiiPie(){
					{
						resetData(new String[]{
							"占有值",	
							"占有值",	
							"占有值"	
						},
						new Float[]{
							3f,
							5f,
							10f
						});
					}
				};
			}
		}.show();
	}

	@Override
	public BufferedImage createImage() {
		// TODO Auto-generated method stub
		return null;
	}

}
