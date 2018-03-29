package com.naii.ui.dialog.form;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;

import com.naii.ctr.NaiiControl;
import com.naii.db.dto.NaiiDto;
import com.naii.db.dto.NaiiEvent;
import com.naii.ui.graphics.NaiiLine;
import com.naii.ui.inf.NaiiSwingInterface;

@SuppressWarnings("serial")
public class NaiiItem  extends JPanel implements NaiiSwingInterface{

	String id ;
	NaiiLine lines[];
	
	public NaiiItem() {
		setBackground(Color.white);
		lines = new NaiiLine[2];
		lines[0] = new NaiiLine("次", "项目人员分布明细");
		lines[1] = new NaiiLine("次", "出差地人员明细");
		setLayout(null);
		add(lines[0]);
		add(lines[1]);
	}
	
	public void resetData(NaiiDto formObj){
		id = formObj.id;
		try {
			String[] tit;
			Float[] fl;
			Map<String, List<NaiiEvent>> map = NaiiControl.getControl().loadEventViewData("5", id);

			tit = new String[map.size()];
			fl = new Float[map.size()];

			int i=0;
			for(Entry<String, List<NaiiEvent>> e : map.entrySet()){
				tit[i] = e.getKey();
				fl[i] = (float)e.getValue().size();
				i ++;
			}
			lines[0].resetData(tit, fl);
			Dimension d = lines[0].getPreferredSize();
			lines[0].setBounds(10, 10, d.width, d.height);
			
			
			map = NaiiControl.getControl().loadEventViewData("2", id);

			tit = new String[map.size()];
			fl = new Float[map.size()];

			i =0;
			for(Entry<String, List<NaiiEvent>> e : map.entrySet()){
				tit[i] = e.getKey();
				fl[i] = (float)e.getValue().size();
				i ++;
			}
			lines[1].resetData(tit, fl);
			d = lines[1].getPreferredSize();
			lines[1].setBounds(350, 10, d.width, d.height);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void renovation() {
		
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
