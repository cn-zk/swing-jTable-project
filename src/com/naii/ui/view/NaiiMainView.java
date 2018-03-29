package com.naii.ui.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.naii.ctr.NaiiChangeEvent;
import com.naii.ctr.NaiiChangeListener;
import com.naii.ctr.NaiiControl;
import com.naii.db.dto.NaiiDto;
import com.naii.db.dto.NaiiEquipment;
import com.naii.db.dto.NaiiHistory;
import com.naii.db.dto.NaiiUser;
import com.naii.tools.NaiiLog;
import com.naii.tools.NaiiTools;
import com.naii.ui.inf.NaiiSwingInterface;
import com.naii.ui.swing.NaiiListPanel;
import com.naii.ui.swing.NaiiTabbPanel;
import com.naii.ui.swing.NaiiTable;

@SuppressWarnings("serial")
public class NaiiMainView extends JComponent {

	private NaiiTable first;

	private JTabbedPane tab;
	
	public NaiiMainView() {
		
		setLayout(new BorderLayout());
		
		tab = new NaiiTabbPanel();
		tab.setTabPlacement(JTabbedPane.BOTTOM);
		
		tab.addTab("人员信息", getUsePanel());
		tab.addTab("设备信息", getEquipmentPanel());
		tab.addTab("日志信息", getHistoryPanel());
		tab.addTab("公司综合", getCompanyPanel());
		
		tab.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				JTabbedPane t = (JTabbedPane) e.getSource();
				changedComponent(t.getComponent(t.getSelectedIndex()));
				changedButtons(t.getComponent(t.getSelectedIndex()));
			}
		});
		
		this.add(tab);
		
		NaiiControl.getControl().addChangeListener(new NaiiChangeListener() {
			@Override
			public void changeData(NaiiChangeEvent ce) {
				if(ce.option == NaiiChangeEvent.OPTION_IMOROT){
					return;
				}
				Component c = tab.getSelectedComponent();
				if( c instanceof NaiiTabbPanel){
					changedComponent(((NaiiTabbPanel) c).getSelectedComponent());
				}
			}
		});
	}

	private Component getUsePanel() {
		JTabbedPane tab = new NaiiTabbPanel();
		tab.addTab("在职人员",first = new NaiiTable(true){
			@Override
			public void renovation() {
				try {
					setNaiiDtoModel(2, 
							NaiiControl.getControl().queryNaiiDto(new NaiiUser(), NaiiControl.FILTER_ENTRY),
							NaiiTools.FILTER_USERS);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		tab.addTab("资源池", new NaiiTable(true){
			@Override
			public void renovation() {
				try {
					setNaiiDtoModel(2, 
							NaiiControl.getControl().queryNaiiDto(new NaiiUser(), NaiiControl.FILTER_RESUCE),
							NaiiTools.FILTER_RESOURCE);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		tab.addTab("离职人员", new NaiiTable(true){
			@Override
			public void renovation() {
				try {
					setNaiiDtoModel(2, 
							NaiiControl.getControl().queryNaiiDto(new NaiiUser(), NaiiControl.FILTER_QUIT),
							NaiiTools.FILTER_QUIT);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		tab.addTab("回收站", new NaiiListPanel(){
			@Override
			public void renovation() {
				setList(NaiiControl.getControl().readRecycleList(),
						new NaiiUser());
			}
		});
		tab.addTab("人员综合", new NaiiUserView());
		
		tab.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				changedComponent(e.getSource());
				changedButtons(e.getSource());
			}
		});
		
		return tab;
	}

	private Component getHistoryPanel(){
		JTabbedPane tab = new NaiiTabbPanel();
		tab.addTab("全部日志", new NaiiListPanel(false){
			{
				setNaiiTableForamt("yyyy/MM/dd hh:ss:mm");
			}
			@Override
			public void renovation() {
				setList(new File(NaiiControl.getControl().getPath(new NaiiHistory())).listFiles(),
						new NaiiHistory());
			}
		});
		return tab;
	}
	
	private Component getEquipmentPanel() {
		JTabbedPane tab = new NaiiTabbPanel();
		tab.addTab("台式机", new NaiiTable(true){
			@Override
			public void renovation() {
				try {
					setNaiiDtoModel(2, 
							NaiiControl.getControl().queryNaiiDto(new NaiiEquipment(), NaiiControl.FILTER_USE0),
							NaiiTools.FILTER_USERS);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		tab.addTab("笔记本", new NaiiTable(true){
			@Override
			public void renovation() {
				try {
					setNaiiDtoModel(2, 
							NaiiControl.getControl().queryNaiiDto(new NaiiEquipment(), NaiiControl.FILTER_USE1),
							NaiiTools.FILTER_USERS);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		tab.addTab("个人笔记本", new NaiiTable(true){
			@Override
			public void renovation() {
				try {
					setNaiiDtoModel(2, 
							NaiiControl.getControl().queryNaiiDto(new NaiiEquipment(), NaiiControl.FILTER_USE2),
							NaiiTools.FILTER_USERS);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		tab.addTab("空闲设备", new NaiiTable(true){
			@Override
			public void renovation() {
				try {
					setNaiiDtoModel(2, 
							NaiiControl.getControl().queryNaiiDto(new NaiiEquipment(), NaiiControl.FILTER_EMP),
							NaiiTools.FILTER_USERS);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		tab.addTab("回收站", new NaiiListPanel(){
			@Override
			public void renovation() {
				setList(NaiiControl.getControl().readRecycleList(),
						new NaiiEquipment());
			}
		});
		tab.addTab("设备综合", new NaiiEquipmentView());
		
		tab.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				changedComponent(e.getSource());
				changedButtons(e.getSource());
			}
		});
		return tab;
	}
	
	private Component getCompanyPanel() {
		JPanel panel = new JPanel();
		return panel;
	}

	public void initialize() {
		if(first != null){
			first.renovation();
		}
	}
	
	private void changedButtons(Object obj){
		
		NaiiMainButs buts = NaiiControl.getControl().getNaiiUI().getButtons();
		if(obj instanceof NaiiTabbPanel){
			
			NaiiTabbPanel tp = (NaiiTabbPanel) obj;
			obj = tp.getSelectedComponent();
			if(obj instanceof NaiiTable){
				buts.switchButs(NaiiMainButs.SET);
			}else if(obj instanceof NaiiListPanel){
				buts.switchButs(NaiiMainButs.BACK);
			}else{
				buts.switchButs(NaiiMainButs.HIDE);
			}
		}else{
			buts.switchButs(NaiiMainButs.HIDE);
		}
	}
	
	private void changedComponent(Object obj){
		if(obj instanceof NaiiSwingInterface){
			NaiiLog.log("[changedComponent]");
			((NaiiSwingInterface) obj).renovation();
		} 
	}

	public void setSelect(String text) {
		switch (tab.getSelectedIndex()) {
		case 0:
		case 1:
			JTabbedPane t = (JTabbedPane) tab.getComponent(tab.getSelectedIndex());
			Component c = t.getComponent(t.getSelectedIndex());
			if(c instanceof NaiiTable){
				NaiiTable nt = (NaiiTable) c;
				nt.searchRowField(text);
			}else if(c instanceof NaiiListPanel){
				NaiiListPanel nl = (NaiiListPanel) c;
				nl.getNaiiTable().searchRowField(text);
			}
			break;

		default:
			break;
		}
	}
	
	private NaiiTable getShowNaiiTable(){
		Component p = tab.getSelectedComponent();
		if(p instanceof NaiiTabbPanel){
			p = ((NaiiTabbPanel) p).getSelectedComponent();
			if(p instanceof NaiiTable){
				return (NaiiTable) p;
			}
		}
		
		return null;
	}
	
	private NaiiListPanel getShowNaiiList(){
		Component p = tab.getSelectedComponent();
		if(p instanceof NaiiTabbPanel){
			p = ((NaiiTabbPanel) p).getSelectedComponent();
			if(p instanceof NaiiListPanel){
				return (NaiiListPanel) p;
			}
		}
		return null;
	}
	
	public NaiiDto getSelectObject(){
		NaiiTable tab = getShowNaiiTable();
		return tab != null ? tab.getSelectRowObject() : null;
	}

	public NaiiDto getCreateObject() {
		return tab.getSelectedIndex() == 0 ? new NaiiUser() : new NaiiEquipment();
	}

	public NaiiDto[] getCheckedIds() {
		return getShowNaiiTable().getSelectRowIds();
	}

	public Map<String,Object> getSelectRows() {
		
		NaiiListPanel list = getShowNaiiList();

		if(list == null){
			return null;
		}
		
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("list", list.getSelectRows());
		map.put("date", list.getSelectDate());
		
		return map;
	}

	public void refush() {
		NaiiTable tab = getShowNaiiTable();
		if(tab != null){
			tab.renovation();
		}
	}
}
