package com.rdum.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.rdum.db.RdumRetention;
import com.rdum.db.RdumUser;
import com.rdum.tools.RdumTools;
import com.rdum.ui.view.RdumDate;
import com.rdum.ui.view.RdumGroup;

@SuppressWarnings("serial")
public class RdumForm extends JPanel{

	private Map<String, JComponent> map;
	
	private RdumUser user;
	
	public RdumForm() {
		Class<?> c = RdumUser.class;
		Field[] fds = c.getDeclaredFields();
		this.setBackground(Color.white);
		this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 8 ,8));
		map = new HashMap<String, JComponent>();
		for(Field fd: fds){
			RdumRetention rr = fd.getAnnotation(RdumRetention.class);
			
			boolean flag = fd.getName().equals("remark");
			
			JLabel p = new JLabel();
			p.setPreferredSize(new Dimension(flag ?
					540 : 265, flag ? 100: 25));
			p.setLayout(new BorderLayout());
			p.add(new JLabel(rr.name()+":"){
				{
					setPreferredSize(new Dimension(80, 1));
				}
			}, BorderLayout.WEST);
			
			JComponent comp = getTypeComp(rr.type(), fd.getName());
			p.add(comp, BorderLayout.CENTER);
			this.add(p);
			map.put(fd.getName(), comp);
		}
	}

	public void setUser(RdumUser user){
		if(user == null){
			try {
				this.user = new RdumUser();
			} catch (Exception e) {
				e.printStackTrace();
			}
			for(String key : map.keySet()){
				setValue(map.get(key), 
						getDefaultValue(key));
			}
		}else{
			this.user =user;
			for(String key : map.keySet()){
				try {
					setValue(map.get(key), 
					RdumUser.class.getField(key).get(user));
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private Object getDefaultValue(String key) {
		Object val = null;
		if("id".equals(key)){
			val = System.nanoTime();
		}else if("entry".equals(key)){
			val = new Date();
		}else {
			val = "";
		}
		return val;
	}
	
	private Object getValue(JComponent comp) {
		Object val = null;
		if(comp.getClass() == JScrollPane.class){
			val = ((JTextArea)((JScrollPane) comp).getViewport().getView()).getText();
		}else if(comp.getClass() == JTextField.class){
			val = ((JTextField) comp).getText();
		}else if(comp.getClass() == JComboBox.class){
			val = ((JComboBox) comp).getSelectedItem();
		}else if(comp.getClass() == RdumDate.class){
			val = ((RdumDate) comp).getValue();
		}else if(comp.getClass() == RdumGroup.class){
			val = ((RdumGroup) comp).getValue();
		}
		
		if(val != null && val instanceof String){
			val = ((String)val).trim();
		}
		return val;
	}
	private void setValue(JComponent comp , Object value){
		if(comp.getClass() == JScrollPane.class){
			((JTextArea)((JScrollPane) comp).getViewport().getView()).setText(RdumTools.string(value));
		}else if(comp.getClass() == JTextField.class){
			((JTextField) comp).setText(RdumTools.string(value));
		}else if(comp.getClass() == JComboBox.class){
			((JComboBox) comp).setSelectedItem(RdumTools.string(value));
		}else if(comp.getClass() == RdumDate.class){
			((RdumDate) comp).setValue(value instanceof String ? null: (Date)value);
		}else if(comp.getClass() == RdumGroup.class){
			((RdumGroup) comp).setValue(RdumTools.string(value));
		}
		
	}
	private JComponent getTypeComp(String type, String fd) {
		
		JComponent comp;
		
		if("combobox".equals(type)){
			JComboBox box = new JComboBox();
			box.setModel(new DefaultComboBoxModel(RdumTools.db.getMsArray(fd)));
			comp = box;
		}else if("textarea".equals(type)){
			JTextArea ta = new JTextArea();
			ta.setLineWrap(true);
			comp = new JScrollPane(ta);
		}else if("date".equals(type)){
			comp = new RdumDate();
		}else if("user".equals(type)){
			comp = new RdumGroup();
		}else{
			JTextField tf = new JTextField();
			if("id".equals(fd)){
				tf.setEditable(false);
			}
			comp = tf;
		}
		comp.setPreferredSize(new Dimension(200, 30));
		return comp;
	}

	public RdumUser getUser() {
		for(String key : map.keySet()){
			try {
				RdumUser.class.getField(key).set(user, getValue(map.get(key)));
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
		}
		return user;
	}

}
