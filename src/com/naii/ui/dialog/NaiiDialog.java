package com.naii.ui.dialog;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.naii.ctr.NaiiControl;
import com.naii.db.NaiiProperty;
import com.naii.db.dto.NaiiDto;
import com.naii.tools.NaiiLog;
import com.naii.ui.dialog.form.NaiiForm;
import com.naii.ui.inf.NaiiSwingInterface;
import com.naii.ui.swing.NaiiTabbPanel;

@SuppressWarnings("serial")
public abstract class NaiiDialog extends JDialog{
	
	protected NaiiForm form;
	
	protected JButton save , cancle;
	protected NaiiTabbPanel tab;
	protected NaiiDto formObj;
	
	private int windowIndex = -1;

	public NaiiDialog() {
		super(NaiiControl.getControl().currentWondow());
		setModal(true);
		setLayout(new BorderLayout());
		
		tab = new NaiiTabbPanel();
		tab.setTabPlacement(JTabbedPane.BOTTOM );
		tab.addTab("    属    性    ", form = new NaiiForm());
		
		add(tab, BorderLayout.CENTER);
		
		tab.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				Component c = tab.getSelectedComponent();
				if(c instanceof NaiiSwingInterface){
					((NaiiSwingInterface) c).renovation();
				}
				if(tab.getSelectedIndex() == 0){
					save.setEnabled(true);
				}else{
					save.setEnabled(false);
				}
				cancle.requestFocus();
			}
		});
		
		tab.setFocusable(false);
		
		JPanel south = new JPanel();
		south.setLayout(new FlowLayout(FlowLayout.RIGHT));
		south.add(save = new JButton("save"){
			{
				addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						saveFormObject(form.getObject());
						closeForm();
					}
				});
			}
		});
		south.add(cancle = new JButton("cancle"){
			{
				addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						closeForm();
					}
				});
			}
		});
		add(south, BorderLayout.SOUTH);
		
		setResizable(false);
		setSize(new Dimension(480, 480));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		
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
	                	if(keyEvent.isControlDown() && NaiiDialog.this.isDisplayable()){
	                		if(keyEvent.getKeyCode() == 83){
	                			save.doClick();
	                		}else if(keyEvent.getKeyCode() == 87){
	                			cancle.doClick();
	                		}
	                	}
	                }  
	            }  
			}
        	
        }, AWTEvent.KEY_EVENT_MASK);
        addWindowListener(new WindowAdapter(){
        	@Override
        	public void windowClosing(WindowEvent arg0) {
        		NaiiControl.getControl().removeWindow(windowIndex);
        	}
        	@Override
        	public void windowActivated(WindowEvent e) {
        		cancle.requestFocus();
        	}
        });
        init();
	}
	
	public void init(){
		
	}
	
	public void saveFormObject(NaiiDto naiiDto){
		
	}
	
	
	public NaiiForm getForm() {
		return form;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void show() {
		windowIndex = NaiiControl.getControl().registWindow(this);
		super.show();
	}


	public void openForm(NaiiDto formObj) { 
		this.formObj = formObj;
		setTitle(NaiiProperty.getProperty().getCompanyName());
		setLocationRelativeTo(NaiiControl.getControl().currentWondow());
		NaiiLog.log("openForm="+formObj.toJSON());
		form.resetComponent(formObj);
		tab.setSelectedIndex(0);
		new Thread(new Runnable() {
			public void run() {
				
				setVisible(true);		
			}
		}).start();
	}
	
	public void closeForm(){
		NaiiControl.getControl().removeWindow(windowIndex);
		NaiiDialog.this.dispose();
	}

}
