package com.naii.ui.dialog.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.naii.db.NaiiProperty;
import com.naii.db.annotation.NaiiRetention;
import com.naii.db.dto.NaiiDto;
import com.naii.tools.Assist;
import com.naii.tools.NaiiTools;
import com.naii.ui.inf.NaiiSwingInterface;
import com.naii.ui.swing.NaiiChooser;
import com.naii.ui.swing.NaiiComboBox;
import com.naii.ui.swing.NaiiDate;
import com.naii.ui.swing.NaiiGroup;
import com.naii.ui.swing.NaiiMoney;

public class NaiiForm extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private NaiiDto obj;
	
	private Map<String, JComponent> map;
	
	private JComponent cent;
	
	@SuppressWarnings("serial")
	public NaiiForm() {
		setLayout(new BorderLayout());
		add(new JScrollPane(cent = new JPanel(){
			{
				setBackground(Color.white);
				setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
				setLayout(new FlowLayout(FlowLayout.LEFT, 8 ,8));
				setPreferredSize(new Dimension(100, 1));
			}
		}){
			{
				setBorder(null);
			}
		}, BorderLayout.CENTER);
	}
	
	@SuppressWarnings("serial")
	public void resetComponent(NaiiDto obj){
		
		cent.removeAll();
		if(obj == null){
		}else{
			Class<?> c = obj.getClass();
//			if(this.obj == null || c != this.obj.getClass()){
				this.obj = obj;
				
				Field[] fds = c.getFields();
				map = new HashMap<String, JComponent>();
				for(Field fd: fds){
					NaiiRetention rr = fd.getAnnotation(NaiiRetention.class);
					if(rr.temp() || rr.hide()){
						continue;
					}
					final boolean flag = fd.getName().equals("remark");
					
					JPanel p = new JPanel();
					p.setBackground(Color.white);
					p.setPreferredSize(new Dimension(flag ?
							410 : 200, flag ? 80: 25));
					p.setLayout(new BorderLayout());
					p.add(new JLabel(rr.name()+":"){
						{
							setPreferredSize(new Dimension(70, 1));
							if(flag){
								setVerticalAlignment(TOP);
							}
						}
					}, BorderLayout.WEST);
					
					JComponent comp = getTypeComp(rr.type(), fd.getName());
					p.add(comp, BorderLayout.CENTER);
					cent.add(p);
					map.put(fd.getName(), comp);
				}
//				getParent().revalidate();
//			}
			for(String key : map.keySet()){
				try {
					Object val = c.getField(key).get(obj);
					if(val == null && obj.id == null){
						val = getDefaultValue(key);
					}
					setValue(map.get(key), val);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private Object getDefaultValue(String key) {
		Object val = null;
		if("id".equals(key)){
			val = NaiiTools.getId();
		}else if(Assist.contains(new String[]{"entry", "create", "date"}, key)){
			val = new Date();
		}else {
			val = "";
		}
		return val;
	}
	
	private Object getValue(JComponent comp) {
		Object val = null;
		if(comp instanceof NaiiSwingInterface){
			val = ((NaiiSwingInterface) comp).getValue();
		}else if(comp instanceof JScrollPane){
			val = ((JTextArea)((JScrollPane) comp).getViewport().getView()).getText();
		}else if(comp instanceof JTextField){
			val = ((JTextField) comp).getText();
		}
		if(val != null && val instanceof String){
			val = ((String)val).trim();
		}
		return val;
	}
	
	private void setValue(JComponent comp , Object value){
		if(comp instanceof NaiiSwingInterface){
			((NaiiSwingInterface) comp).setValue(value);
		}else if(comp instanceof JScrollPane){
			((JTextArea)((JScrollPane) comp).getViewport().getView()).setText(NaiiTools.string(value));
		}else if(comp instanceof JTextField){
			((JTextField) comp).setText(NaiiTools.string(value));
		}else{
			System.err.println("NaiiForm.setValue");
		}
		
	}
	private JComponent getTypeComp(String type, String fd) {
		
		JComponent comp;
		if("combobox".equals(type)){
			NaiiComboBox box = new NaiiComboBox();
			box.setData(NaiiProperty.getProperty().getFormat(fd));
			comp = box;
		}else if("textarea".equals(type)){
			JTextArea ta = new JTextArea();
			ta.setLineWrap(true);
			comp = new JScrollPane(ta);
		}else if("date".equals(type)){
			comp = new NaiiDate();
		}else if("group".equals(type)){
			comp = new NaiiGroup();
		}else if("user".equals(type)){
			comp = new NaiiChooser();
		}else if("number".equals(type)){
			comp = new NaiiMoney();
		}else{
			JTextField tf = new JTextField();
			if("id".equals(fd) || fd.lastIndexOf("Id") == fd.length() - 2){
				tf.setEditable(false);
			}
			comp = tf;
		}
		comp.setPreferredSize(new Dimension(200, 30));
		return comp;
	}

	public NaiiDto getObject() {
		for(String key : map.keySet()){
			try {
				obj.getClass().getField(key).set(obj, getValue(map.get(key)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return obj;
	}

}
