package com.naii.ui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.filechooser.FileFilter;

import com.naii.ctr.NaiiControl;
import com.naii.db.dto.NaiiDto;
import com.naii.ui.dialog.exp.NaiiExportDialog;
import com.naii.ui.dialog.form.NaiiFormDialog;
import com.naii.ui.dialog.imp.NaiiImportDialog;
import com.naii.ui.view.NaiiLoadView;
import com.naii.ui.view.NaiiMainButs;
import com.naii.ui.view.NaiiMainView;

public class NaiiUI {

	private NaiiLoadView load;
	private NaiiMainView main;
	private NaiiMainButs buts;
	
	private NaiiFormDialog form;
	private NaiiImportDialog imp;
	private NaiiExportDialog exp;
	
	public JComponent glassPanel;
	
	public NaiiUI(Runnable run) {
		load = new NaiiLoadView(run);
	}
	
	public void init(){

		main = new NaiiMainView();
		buts = new NaiiMainButs(this);
		
//		TestFrame fr = new TestFrame() {
//			
//			@Override
//			public Component getContent() {
//				JPanel p = new JPanel();
//				p.setLayout(new BorderLayout());
//				p.add(buts, BorderLayout.NORTH);
//				p.add(main, BorderLayout.CENTER);
//				return p;
//			}
//		};
//		NaiiControl.getControl().registWindow(fr.getFrame());
		
		JFrame fr = new JFrame("Naii");
		NaiiControl.getControl().registWindow(fr);
		fr.setSize(new Dimension(800, 600));
		fr.setMinimumSize(new Dimension(400, 400));
		fr.setLocationRelativeTo(null);
		fr.setDefaultCloseOperation(3);
		fr.setLayout(new BorderLayout());
		fr.setIconImage(createIcon());
		
		fr.setJMenuBar(createMenuBar());
		
		fr.add(buts, BorderLayout.NORTH);
		fr.add(main, BorderLayout.CENTER);
		
//		fr.setGlassPane(glassPanel = new JLabel());
//		fr.getGlassPane().setVisible(true);
//		glassPanel.setLayout(new BorderLayout());
//		glassPanel.add(new JTextField(20), BorderLayout.SOUTH);
		
		Toolkit tk = Toolkit.getDefaultToolkit();  
        tk.addAWTEventListener(new AWTEventListener (){

			@Override
			public void eventDispatched(AWTEvent event) {
				if (event.getClass() == KeyEvent.class) {  
	                // 被处理的事件是键盘事件.  
	                KeyEvent keyEvent = (KeyEvent) event;  
	                if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {  
	                    //按下时你要做的事情  
	                	if(keyEvent.getKeyCode() == 116){
	                		try {
								main.refush();
							} catch (Exception e) {
								e.printStackTrace();
							}
	                	}
	                } else if (keyEvent.getID() == KeyEvent.KEY_RELEASED) {  
	                    //放开时你要做的事情  
//	                	if(keyEvent.isControlDown() && keyEvent.getKeyCode() == 82){
//	                		currentPanel.refush();
//	                	}else if(keyEvent.getKeyCode() == 116){
//	                		currentPanel.refush();
//	                	}
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
			private JFileChooser fc = null;
			@Override
			public void actionPerformed(ActionEvent e) {
				if(fc == null){
					fc = new JFileChooser();
					fc.setMultiSelectionEnabled(false);
					fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fc.setFileFilter(new FileFilter() {
						@Override
						public String getDescription() {
							return "xls文件(*.xls)";
						}
						
						@Override
						public boolean accept(File f) {
							if(f.getName().endsWith(".xls") || f.isDirectory()){
								return true;
							}
							return false;
						}
					});
				}
				if(fc.showOpenDialog(NaiiControl.getControl().currentWondow()) == 0){
					showImportDialog(fc.getSelectedFile());
				}
			}
		});
		file.add(imp);
		
		JMenuItem exp = new JMenuItem("Export");
		exp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showExportDialog();
			}
		});
		file.add(exp);
		bar.add(file);

		JMenu company = new JMenu("Company");
		JMenuItem rongda = new JRadioButtonMenuItem("荣大信息");
		rongda.setSelected(true);
		rongda.setEnabled(false);
		company.add(rongda);
		bar.add(company);
		
		return bar;
	}


	private Image createIcon() {
		BufferedImage img= new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, 50, 50);
		g.setFont(new Font("微软雅黑", 1, 65));
		g.setColor(Color.blue);
		g.drawString("N", 0, 49);
		g.dispose();
		return img;
	}
	
	public void showImportDialog(File f){
		if(imp == null){
			imp = new NaiiImportDialog();
		}
		try {
			imp.showDialog(f);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(NaiiControl.getControl().currentWondow(), "文件异常无法导入：\n"+e.getMessage());
		}
	}
	
	public void showExportDialog(){
		if(exp == null){
			exp = new NaiiExportDialog();
		}
		try {
			exp.showDialog();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(NaiiControl.getControl().currentWondow(), "导出异常：\n"+e.getMessage());
		}
	}
	
	public void showFormDialog(NaiiDto formObj){
		if(form == null){
			form = new NaiiFormDialog();
		}
		form.openForm(formObj);
	}

	public NaiiMainView getMain() {
		return main;
	}

	public Dialog getShowDialog() {
		return form;
	}


	public NaiiMainButs getButtons() {
		return buts;
	}
	
	public NaiiLoadView getLoad() {
		return load;
	}
	
	public void hideLoad(){
		main.initialize();
		load.hidden();
		NaiiControl.getControl().currentWondow().setVisible(true);
	}
}
