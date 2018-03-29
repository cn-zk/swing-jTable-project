package com.naii.ui.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.naii.ctr.NaiiControl;
import com.naii.db.dto.NaiiDto;
import com.naii.tools.NaiiLog;
import com.naii.ui.NaiiUI;

@SuppressWarnings("serial")
public class NaiiMainButs extends JPanel{

	public static final String SET	="set";
	public static final String BACK	="back";
	public static final String HIDE	="hide";
	
	private NaiiUI naiiUI;
	
	public NaiiMainButs(NaiiUI ui) {
		this.naiiUI = ui;
		setLayout(new BorderLayout());
		
		addOptionButs();
		
		add(new JTextField(20){
			{
				addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent e) {
						if(e.getKeyCode() == 10){
							String text = ((JTextField)e.getSource()).getText();
							NaiiLog.log(text);
							naiiUI.getMain().setSelect(text);
						}
					};
				});
			}
		}, BorderLayout.EAST);
	}

	private void addOptionButs() {
		
		JPanel p = new JPanel();
		p.setPreferredSize(new Dimension(1,25));
		p.setLayout(new FlowLayout(FlowLayout.LEFT, 5,1));
		p.add(new JButton("Add"){
			{
				addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						naiiUI.showFormDialog(naiiUI.getMain().getCreateObject());
					}
				});
			}
		});
		p.add(new JButton("Edit"){
			{
				addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						if(naiiUI.getMain().getSelectObject() != null){
							naiiUI.showFormDialog(naiiUI.getMain().getSelectObject());						
						}else{
							JOptionPane.showMessageDialog(NaiiControl.getControl().currentWondow(), "请选择编辑记录!");
						}
					}
				});
			}
		});
		p.add(new JButton("Dels"){
			{
				addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						NaiiDto[] dtos = naiiUI.getMain().getCheckedIds();
						if(dtos == null || dtos.length < 1){
							JOptionPane.showMessageDialog(NaiiControl.getControl().currentWondow(), "请选择删除记录!");
						}else if(JOptionPane.showConfirmDialog(NaiiControl.getControl().currentWondow(), "是否删除所选"+dtos.length+"条纪录?", "提示", JOptionPane.YES_NO_OPTION)==0){
							NaiiControl.getControl().removeObject(dtos);
						}
					}
				});
			}
		});
		p.add(new JButton("Back"){
			{
				setVisible(false);
				addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						
						Map<String, Object> map = naiiUI.getMain().getSelectRows();
						
						if(map == null || map.size() < 1){
							JOptionPane.showMessageDialog(NaiiControl.getControl().currentWondow(), "无法撤回记录!");
							return;
						}
						NaiiControl.getControl().backObject(
								(NaiiDto[])map.get("list"),
								(String)map.get("date"));
					}
				});
			}
		});
		add(p, BorderLayout.CENTER);
		
		for(Component c : p.getComponents()){
			if(c instanceof JButton){
				((JButton) c).setFocusable(false);
			}
		}
	}

	public void switchButs(String t) {
		if(SET.equals(t)){
			visibleOptions(true);
			visibleBack(false);
		}else if(BACK.equals(t)){
			visibleOptions(false);
			visibleBack(true);
		}else{
			visibleOptions(false);
			visibleBack(false);
		}
	}
	
	public void visibleOptions(boolean b){
		Component[] cs = ((JComponent)getComponents()[0]).getComponents();
		for(int i=0;i<3;i++){
			if(cs[i] instanceof JButton){
				((JButton) cs[i]).setVisible(b);
			}
		}
	}
	
	public void visibleBack(boolean b){
		((JComponent)getComponents()[0]).getComponent(3).setVisible(b);
	}
	
	public void clickEdit(){
		((JButton)((JComponent)getComponent(0)).getComponent(1)).doClick();
	}
}
