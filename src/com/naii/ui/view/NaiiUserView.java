package com.naii.ui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.naii.ctr.NaiiControl;
import com.naii.db.NaiiProperty;
import com.naii.db.dto.NaiiEvent;
import com.naii.db.dto.NaiiValue;
import com.naii.ui.graphics.NaiiLine;
import com.naii.ui.inf.NaiiSwingInterface;

@SuppressWarnings("serial")
public class NaiiUserView extends JPanel implements NaiiSwingInterface{

	private NaiiLine[] lines;
	
	public NaiiUserView() {
		
		setLayout(new BorderLayout());
		add(new JScrollPane(new JPanel(){
			{
//				setLayout(new FlowLayout(FlowLayout.LEADING));
				setLayout(null);
				setBackground(Color.white);
				setPreferredSize(new Dimension(1000, 1500));
				lines = new NaiiLine[5];
				
				add(lines[0] = new NaiiLine("次", "项目人员分布明细"));
				add(lines[1] = new NaiiLine("人", "近期3个月人员明细"));
				add(lines[2] = new NaiiLine("次", "出差地人员明细"));
				add(lines[3] = new NaiiLine("人", "人员级别明细"));
				add(lines[4] = new NaiiLine("人", "人员技能明细"));
				
			}
		}){
			{
				setBorder(null);
			}

			@Override
			public void paint(Graphics g) {
				Dimension 
				d = lines[1].getPreferredSize();
				lines[1].setBounds(310, 10, d.width, d.height);
				d = lines[2].getPreferredSize();
				lines[2].setBounds(310, 200, d.width, d.height);
				
				d = lines[3].getPreferredSize();
				lines[3].setBounds(310, lines[2].getPreferredSize().height + 220, d.width, d.height);
				
				d = lines[4].getPreferredSize();
				lines[4].setBounds(620, 10, d.width, d.height);
				
				d = lines[0].getPreferredSize();
				lines[0].setBounds(10, 10, d.width, d.height);
				super.paint(g);
			}
		});
	}
	
	@Override
	public void renovation() {
		String[] tit;
		Float[] fl;
		Map<String, List<NaiiEvent>> map = NaiiControl.getControl().loadEventViewData("5");

		tit = new String[map.size()];
		fl = new Float[map.size()];

		int i=0;
		for(Entry<String, List<NaiiEvent>> e : map.entrySet()){
			tit[i] = e.getKey();
			fl[i] = (float)e.getValue().size();
			i ++;
		}
		lines[0].resetData(tit, fl);
		
		tit = new String[]{
				"已招入职",
				"已经离职",
				"入资源池",
				"资源池入职"
		};
		fl = NaiiControl.getControl().loadUser3ViewData();
		lines[1].resetData(tit, fl);
		
		map = NaiiControl.getControl().loadEventViewData("2");

		tit = new String[map.size()];
		fl = new Float[map.size()];

		i =0;
		for(Entry<String, List<NaiiEvent>> e : map.entrySet()){
			tit[i] = e.getKey();
			fl[i] = (float)e.getValue().size();
			i ++;
		}
		lines[2].resetData(tit, fl);
	
		
		NaiiValue[] vals = NaiiProperty.getProperty().getFormat(NaiiProperty.KEY_LEVEL);
		tit = new String[vals.length];
		String keys[] = new String[vals.length];
		i=0;
		for(NaiiValue val : vals){
			keys[i] = val.getId();
			tit[i++] = val.getName();
		}
		fl = NaiiControl.getControl().loadUserViewFloat(keys, NaiiProperty.KEY_LEVEL);
		lines[3].resetData(tit, fl);
		
		vals = NaiiProperty.getProperty().getFormat(NaiiProperty.KEY_SKILL);
		tit = new String[vals.length];
		keys = new String[vals.length];
		i=0;
		for(NaiiValue val : vals){
			keys[i] = val.getId();
			tit[i++] = val.getName();
		}
		fl = NaiiControl.getControl().loadUserViewFloat(keys, NaiiProperty.KEY_SKILL);
		lines[4].resetData(tit, fl);
	}
	

	@Override
	public void setValue(Object obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}

}
