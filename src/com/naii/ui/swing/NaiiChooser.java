package com.naii.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.naii.ctr.NaiiControl;
import com.naii.db.dto.NaiiUser;
import com.naii.tools.NaiiLog;
import com.naii.tools.NaiiTools;
import com.naii.ui.inf.NaiiClickListener;
import com.naii.ui.inf.NaiiSwingInterface;

@SuppressWarnings("serial")
public class NaiiChooser extends JComponent implements NaiiSwingInterface{

	private String id;
	private JDialog dialog;
	private NaiiTable table;
	protected Dimension size;
	protected String filter;
	protected JLabel la;
	
	public NaiiChooser() {
		setBorder(BorderFactory.createLineBorder(Color.gray));
		setLayout(new BorderLayout());
		
		la = new JLabel();
		
//		la.setHorizontalAlignment(JLabel.CENTER);
		la.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
		la.setCursor(new Cursor(Cursor.HAND_CURSOR));
		la.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				showDialog();
			}
		});
		add(la, BorderLayout.CENTER);
		
		JButton b = new JButton("X");
		b.setFocusable(false);
		b.setBorder(null);
		b.setMargin(null);
		b.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setValue(null);
			}
		});
		b.setPreferredSize(new Dimension(25,1));
		add(b, BorderLayout.EAST);
		
		filter = NaiiControl.FILTER_ENTRY;
		size = new Dimension(350, 400);
	}

	public void showDialog() {
		if(dialog == null){
			dialog = new JDialog(NaiiControl.getControl().currentWondow());
			dialog.setModal(true);
			dialog.setSize(size);
			dialog.setResizable(false);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			
			
			JPanel p = new JPanel();
			p.setPreferredSize(new Dimension(1, 30));
			p.setLayout(new FlowLayout(FlowLayout.RIGHT, 5,5));
			p.add(new JTextField(20){
				{
					addKeyListener(new KeyAdapter() {
						public void keyPressed(KeyEvent e) {
							if(e.getKeyCode() == 10){
								String text = getText();
								NaiiLog.log(text);
								table.searchRowField(getText());;
							}
						};
					});
				}
			});
			dialog.add(p, BorderLayout.NORTH);
			
			dialog.add(table = new NaiiTable(){
				{
					setClickListener(new NaiiClickListener(){

						@Override
						public void dblClickRow(NaiiTable table) {
							NaiiChooser.this.setValue(table.getSelectRowObject().id);
							closeForm();
						}
						
					});
				}
				@Override
				public void renovation() {
					try {
						setNaiiDtoModel(0, 
								NaiiControl.getControl().queryNaiiDto(new NaiiUser(), filter),
								true,
								NaiiTools.FILTER_IN_USERS);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}, BorderLayout.CENTER);
			
			JPanel south = new JPanel();
			south.setLayout(new FlowLayout(FlowLayout.RIGHT));
			south.add(new JButton("ok"){
				{
					addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							NaiiChooser.this.setValue(table != null ? table.getSelectRowObject().id : null);
							closeForm();
						}
					});
				}
			});
			south.add(new JButton("cancle"){
				{
					addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent arg0) {
							closeForm();
						}
					});
				}
			});
			dialog.add(south, BorderLayout.SOUTH);
		}
		dialog.setLocationRelativeTo(NaiiControl.getControl().currentWondow());
		new Thread(new Runnable() {
			@Override
			public void run() {
				dialog.setVisible(true);				
			}
		}).start();
		
		table.renovation();
		if(id != null)
			table.searchRowField(la.getText());
		else
			table.selectRow(0);
	}
	
	private void closeForm(){
		dialog.dispose();
	}

	@Override
	public void renovation() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setValue(Object obj) {
		id = NaiiTools.string(obj);
		la.setText(NaiiTools.getUserName(id));
	}
	
	public Object getValue(){
		return id;
	}
}
