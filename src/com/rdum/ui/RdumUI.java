package com.rdum.ui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import com.rdum.ctr.RdumCtr;
import com.rdum.db.RdumUser;
import com.rdum.tools.RdumStream;
import com.rdum.tools.RdumTools;
import com.rdum.ui.view.RdumAll;
import com.rdum.ui.view.RdumRecycle;
import com.rdum.ui.view.RdumTabs;
import com.rdum.ui.view.RdumView;

public class RdumUI {

	private RdumCtr ctr;
	
	private JFrame fr;
	private JDialog textDialog, expDialog;
	private RdumForm form;

	private int showFlag=0;
	
	private RdumAll all;
	private RdumTabs tabs;
	private RdumRecycle recycle;
	private JLabel state;
	
	JPanel panel;
	
	private RdumView currentPanel;
	private int viewIndex;
	
	public RdumUI() {
		
		ctr = new RdumCtr(this);
		
		fr = new JFrame("Rdum");
		fr.setSize(RdumTools.getDimension());
		fr.setLocationRelativeTo(null);
		fr.setDefaultCloseOperation(3);
		fr.setLayout(new BorderLayout());
		fr.setIconImage(createIcon());
		
		fr.setJMenuBar(createMenuBar());
		
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.addButtons(panel, RdumTools.actions);
		
		final JComboBox box = new JComboBox(RdumTools.viewBoxItem);
		box.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if(e.getStateChange() == ItemEvent.SELECTED){
					RdumUI.this.switchViewPanel(box.getSelectedIndex());
				}
			}
		});
		panel.add(box);
		
		final JTextField tf = new JTextField();
		tf.setPreferredSize(new Dimension(100, 24));
		tf.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == 10){
					currentPanel.search(tf.getText());
				}
			}
		});
		panel.add(tf);
		
		fr.add(panel, BorderLayout.NORTH);
		
		this.fr.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
//				System.out.println("1");
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
//				System.out.println("6");
			}
		});
		
		switchViewPanel(0);
		fr.add(state = new JLabel(currentPanel.getState()
				), BorderLayout.SOUTH);
		fr.setVisible(true);
		
		
		Toolkit tk = Toolkit.getDefaultToolkit();  
        tk.addAWTEventListener(new AWTEventListener (){

			@Override
			public void eventDispatched(AWTEvent event) {
				if (event.getClass() == KeyEvent.class) {  
	                // 被处理的事件是键盘事件.  
	                KeyEvent keyEvent = (KeyEvent) event;  
	                if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {  
	                    //按下时你要做的事情  
	                } else if (keyEvent.getID() == KeyEvent.KEY_RELEASED) {  
	                    //放开时你要做的事情  
	                	if(keyEvent.isControlDown() && keyEvent.getKeyCode() == 82){
	                		currentPanel.refush();
	                	}else if(keyEvent.getKeyCode() == 116){
	                		currentPanel.refush();
	                	}
	                }  
	            }  
			}
        	
        }, AWTEvent.KEY_EVENT_MASK);
	}
	
	private JMenuBar createMenuBar() {
		JMenuBar bar = new JMenuBar();
		
		JMenu file = new JMenu("File");

		JMenuItem imp = new JMenuItem("Import");
		imp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setMultiSelectionEnabled(false);
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
//				fc.setFileFilter(new FileFilter() {
//					@Override
//					public String getDescription() {
//						return "Rdum文件(*.dum)";
//					}
//					
//					@Override
//					public boolean accept(File f) {
//						if(f.getName().endsWith(".dum")){
//							return true;
//						}
//						return false;
//					}
//				});
				if(fc.showOpenDialog(fr) == 0){
					try {
						RdumStream.inDum(fc.getSelectedFile());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		file.add(imp);
		
		JMenuItem exp = new JMenuItem("Export");
		exp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showExpDialog();
			}
		});
		file.add(exp);
		bar.add(file);
		
		return bar;
	}

	private RdumView getViewPanel(int i){
		viewIndex = i;
		RdumView panel = null;
		switch (i) {
		case 0:
			if(all == null){
				all = new RdumAll();
			}
			panel = all;
			break;
		case 1:
			if(tabs == null){
				tabs = new RdumTabs();
			}
			panel = tabs;
			break;
		case 2:
			if(recycle == null){
				recycle = new RdumRecycle();
			}
			panel = recycle;
			break;
		}
		return panel;
	}

	protected void switchViewPanel(int i) {
		
		if(currentPanel != null){
			fr.remove(currentPanel);
		}
		
		switch (i) {
		case 0:
			for(Component c : panel.getComponents()){
				if(c instanceof JButton){
					((JButton)c).setEnabled(true);
				}
			}
			break;
		case 1:
		case 2:
			for(Component c : panel.getComponents()){
				if(c instanceof JButton){
					((JButton)c).setEnabled(false);
				}
			}
			break;
		}
		
		currentPanel = getViewPanel(i);
		
		fr.add(currentPanel, BorderLayout.CENTER);
		fr.revalidate();
		flushState();
	}

	private Image createIcon() {
		BufferedImage img= new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, 50, 50);
		g.setFont(new Font("微软雅黑", 1, 65));
		g.setColor(Color.blue);
		g.drawString("R", 0, 49);
		g.dispose();
		return img;
	}

	public int getSelectIndex() {
		return all.getFixedTable().getSelectRow();
	}
	public RdumUser getSelectUser(){
		return all.getFixedTable().getSelectRow() == -1 ?
				null : RdumTools.db.getUsers().get(all.getFixedTable().getSelectRow());
	}

	public void addButtons(JComponent panel, String[][] options, final Runnable ...run){
		int i=0;
		for(final String[] option: options){
			JButton b = new JButton(option[1]);
			
			final Runnable r = run != null && run.length > i ? run[i] : null;
			
			b.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(r != null){
						r.run();
					}else
					ctr.buttonAction(option[0]);
				}
			});
			panel.add(b);
			i++;
		}
	}
	public void showTextDialog(String option){
		showFlag = 1;
		if(this.textDialog == null){
			this.textDialog = new JDialog(fr, true);
			this.textDialog.setResizable(false);
			this.textDialog.setSize(new Dimension(600, 560));
			this.textDialog.setLayout(new BorderLayout());
			this.textDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			this.textDialog.setLocationRelativeTo(fr);
			
			this.textDialog.add(form =new RdumForm(), BorderLayout.CENTER);
			
			JPanel south = new JPanel();
			south.setLayout(new FlowLayout(FlowLayout.RIGHT));
			this.addButtons(south, RdumTools.options);
			this.textDialog.add(south, BorderLayout.SOUTH);
			this.textDialog.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					// TODO Auto-generated method stub
					showFlag = 0;
				}
			});
		}
		
		if("edit".equals(option)){
			form.setUser(getSelectUser());
		}else{
			form.setUser(null);
		}
		
		this.textDialog.setTitle(RdumTools.convertOption(option));
		this.textDialog.setVisible(true);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void showExpDialog(){
		showFlag = 2;
		if(expDialog == null){
			expDialog = new JDialog(fr, true);
			expDialog.setTitle("导出");
			expDialog.setSize(300, 200);
			expDialog.setLocationRelativeTo(fr);
			expDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			
			JPanel cen = new JPanel();
			cen.setBackground(Color.white);
			cen.setLayout(new FlowLayout());
			
			cen.add(new JLabel(){
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{
					setCursor(new Cursor(Cursor.HAND_CURSOR));
					setPreferredSize(new Dimension(200, 25));
					setBorder(BorderFactory.createLineBorder(Color.gray));
					final JLabel la = this;
					addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							JFileChooser fc = new JFileChooser();
							fc.setMultiSelectionEnabled(false);
							fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							if(fc.showOpenDialog(fr) == 0){
								la.setText(fc.getSelectedFile().getPath());
							}
						}
					});
				}
			});
			
			final JComboBox box = new JComboBox(RdumTools.EXP_BOX_ITEM);
			box.setPreferredSize(new Dimension(200, 25));
			cen.add(box);
			expDialog.add(cen, BorderLayout.CENTER);
			
			JPanel south = new JPanel();
			south.setLayout(new FlowLayout(FlowLayout.RIGHT));
			this.addButtons(south, RdumTools.exps, new Runnable() {
				public void run() {
					JLabel la = (JLabel)box.getParent().getComponent(0);
					if("".equals(la.getText())){
						JOptionPane.showMessageDialog(expDialog, "请选择保存目录！");
					}else
					ctr.exp(new File(la.getText()), (String )box.getSelectedItem());
				}
			});
			expDialog.add(south, BorderLayout.SOUTH);
			expDialog.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					// TODO Auto-generated method stub
					showFlag = 0;
				}
			});
		}
		expDialog.setVisible(true);
		
	}
	
	public JDialog getDialog(){
		JDialog w = null;
		switch (showFlag) {
		case 1:
			w = textDialog;
			break;
		case 2:
			w = expDialog;
			break;
		}
		return w;
	}

	public Frame getFrame() {
		return fr;
	}

	public RdumUser getFormUser() {
		return form.getUser();
	}

	public void flushView(){
		currentPanel.refush();
		flushState();
	}
	
	public void flushState() {
		if(state == null){
			return;
		}
		String str = currentPanel.getState();
		state.setText(str == null ? " " : str);
	}
}
