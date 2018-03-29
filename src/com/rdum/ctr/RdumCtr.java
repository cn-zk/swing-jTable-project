package com.rdum.ctr;

import java.io.File;

import javax.swing.JOptionPane;

import com.rdum.db.RdumUser;
import com.rdum.tools.RdumStream;
import com.rdum.tools.RdumTools;
import com.rdum.ui.RdumUI;

public class RdumCtr {

	private RdumUI ui;
	private String frame_option;
	
	public RdumCtr(RdumUI ui) {
		this.ui = ui;
	}

	public void buttonAction(String option) {
		if("add".equals(option)){
			frame_option = option;
			ui.showTextDialog(option);
		}else if("edit".equals(option)){
			frame_option = option;
			if(ui.getSelectIndex() != -1){
				ui.showTextDialog(option);
			}
		}else if("del".equals(option)){
			if(JOptionPane.showConfirmDialog(ui.getFrame(),"是否要继续删除？", "提示", JOptionPane.OK_CANCEL_OPTION) ==0){
				RdumTools.db.recycle(ui.getSelectUser());
				ui.flushView();
			}
		}else if("save".equals(option)){
			boolean isAdd = "add".equals(frame_option) ;
			RdumUser user = ui.getFormUser();
			if(isAdd && RdumTools.contentUser(user)){
				JOptionPane.showMessageDialog(ui.getFrame(), "["+user.name+ "] 名字重复");
				return;
			}
			try {
				RdumTools.db.save(user, isAdd ? -1:ui.getSelectIndex());
				ui.flushView();
				ui.getDialog().dispose();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(ui.getFrame(), "保存失败!");
			}
		}else if("cancel".equals(option)){
			ui.getDialog().dispose();
		}
	}

	public void exp(File file, String item) {
		if(RdumTools.EXP_BOX_ITEM[0].equals(item)){
			RdumStream.outDum(file);
		}
		ui.getDialog().dispose();
	}

}
