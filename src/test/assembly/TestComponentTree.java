package test.assembly;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * 组件层级查看器
 * @author tomorrow
 *
 */
@SuppressWarnings("serial")
public class TestComponentTree{
	
	private JDialog d ;
	private Container container;
	private JTree tree;
	private TreeViewGlassPanel view;
	
	
	JMenuItem autoRefush;
	JMenuItem autoSelect;
	JMenuItem hideSelect;
	int trow;
	ActionListener viewAction;
	
	public TestComponentTree() {
		this(null);
	}
	
	public TestComponentTree(Container c) {
		setContainer(c);
		
		if(c instanceof JFrame){
			JFrame f = (JFrame) c;
			f.setGlassPane(view = new TreeViewGlassPanel());
			view.setVisible(true);
		}
		
		d = new JDialog();
		d.setJMenuBar(getMenuBar());
		Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
		d.setSize(300, s.height);
		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		d.add(new JScrollPane(getTree()){
			{
				setBorder(null);
				refush();
			}
		}, BorderLayout.CENTER);
		
		/**
		 * 选中展示组件坐标的Action
		 */
		viewAction = new ActionListener() {
			JComponent p ;
			javax.swing.border.Border pb ;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(view != null){
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getPathForRow(tree.getSelectionRows()[0]).getLastPathComponent();
						if(node.getUserObject() instanceof JComponent){
							view.setView((JComponent) node.getUserObject());
						}
					}else{
						if(p != null){
							p.setBorder(pb);
						}
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getPathForRow(tree.getSelectionRows()[0]).getLastPathComponent();
						p = (JComponent) node.getUserObject();
						pb = p.getBorder();
						p.setBorder(BorderFactory.createLineBorder(Color.red, 3));
					}
				} catch (Exception e1) {
				}
			}
		};
	}
	
	/**
	 * 获得组件树
	 * @return
	 */
	private Component getTree() {
		tree = new JTree(){
			{
				
				addMouseMotionListener(new MouseMotionAdapter() {
					@Override
					public void mouseMoved(MouseEvent e) {
						if(!autoSelect.isSelected()) return;
						int selRow = tree.getRowForLocation(e.getX(), e.getY());
				        if(selRow != -1 && trow != selRow) {
				        	tree.setSelectionRow(trow = selRow);
				        	viewAction.actionPerformed(null);
				        }
					}
				});
				addMouseListener(new MouseAdapter() {
					JPopupMenu menu;
					{
						getJPopupMenu();
					}
					
					private void getJPopupMenu() {
						menu = new JPopupMenu();
						JMenuItem m = new JMenuItem("Location");
						m.addActionListener(viewAction);
						menu.setBorder(null);
						menu.add(m);
					}
					
					public void mouseClicked(java.awt.event.MouseEvent e) {
						if(e.getButton() == 3){
							menu.show(tree, e.getX(), e.getY());
						}else if(e.getButton() == 1){
							viewAction.actionPerformed(null);
						}
					};
					public void mouseExited(java.awt.event.MouseEvent e) {
						view.setView(null);
					};
					public void mouseEntered(MouseEvent e) {
						if(autoRefush.isSelected()){
							refush();
						}
					};
				});
			}
		};
		return tree;
	}

	/**
	 * 获得组件树控制菜单
	 * @return
	 */
	private JMenuBar getMenuBar() {
		JMenuBar bar = new JMenuBar();
		bar.setBorder(null);
		JMenu menu = new JMenu("Options");
		autoRefush = new JCheckBoxMenuItem("AutoRefush");
		autoRefush.setSelected(true);
		menu.add(autoRefush);
		autoSelect = new JCheckBoxMenuItem("AutoSelect");
		autoSelect.setSelected(true);
		menu.add(autoSelect);
		hideSelect = new JCheckBoxMenuItem("HideSelect");
		hideSelect.setSelected(true);
		menu.add(hideSelect);
		JMenuItem Refush = new JMenuItem("Refush");
		Refush.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refush();
			}
		});
		menu.add(Refush);
		bar.add(menu);
		return bar;
	}

	/**
	 * 刷新组件树
	 */
	private void refush() {
		trow = -1;
		if(container != null){
			DefaultMutableTreeNode root = new DefaultMutableTreeNode(); 
			
			Component[] cs ;
			if(container instanceof JFrame){
				cs = ((JFrame) container).getRootPane().getComponents();
				root .setUserObject("JFrame");
			}else{
				cs = container.getComponents();
				root .setUserObject("Component");
			}
			
			for(Component c: cs){
				DefaultMutableTreeNode node = getNode(c);
				if(c instanceof JComponent){
					forChild(node, (JComponent) c);
				}
				root.add(node);
			}
			DefaultTreeModel model = new DefaultTreeModel(root);
			tree.setModel(model);
			int i=0;
			while(i < tree.getRowCount()){
				tree.expandRow(i++);
			}
		}
	}
	
	/**
	 * 递归组件树
	 * @param pnode
	 * @param p
	 */
	private void forChild(DefaultMutableTreeNode pnode, JComponent p) {
		for(Component c: p.getComponents()){
			if(hideSelect.isSelected() && (!p.isShowing() || !p.isVisible())){
				continue;
			}
			DefaultMutableTreeNode node = getNode(c);
			if(c instanceof JComponent){
				forChild(node , (JComponent)c);
			}
			pnode.add(node);
		}
	}
	
	/**
	 * 默认组件树节点
	 * @param obj
	 * @return
	 */
	private DefaultMutableTreeNode getNode(Object obj){
		return new DefaultMutableTreeNode(obj){
			@Override
			public String toString() {
				if (userObject == null) {
		            return "";
		        } else if(userObject instanceof JComponent){
		        	return userObject.getClass().getName();
		        }else{
		            return userObject.toString();
		        }
			}
		};
	}

	/**
	 * 设置Container
	 * @param container
	 */
	public void setContainer(Container container) {
		this.container = container;
	}
	
	/**
	 * 现视当前功能
	 */
	public void show(){
//		autoLocation();
		d.setVisible(true);
	}

	/**
	 * 自适应展示Dialog大小位置
	 */
	public void autoLocation() {
		if(container != null){
			Point p = container.getLocation();
			Dimension s= container.getSize();
			if(p.x < d.getWidth()){
				p.x += s.width;
			}else{
				p.x -= d.getWidth();
			}
			d.setLocation(p);
			d.setSize(d.getWidth(), s.height);
		}
	}
//	public static void main(String[] args) {
//		new TestComponentTree(new JFrame()).show();
//	}
}

/**
 * 用于窗口GlassPanel与组件树交互类
 * @author tomorrow
 *
 */
@SuppressWarnings("serial")
class TreeViewGlassPanel extends JComponent{
	
	JComponent c;
	
	public void setView(JComponent c){
		this.c = c;
		this.repaint();
	}
	
	@Override
	public void paint(Graphics g) {
		if(c != null){
			Rectangle rec = c.getBounds();
			Component tc = c;
			if(rec != null){
				while(!(tc.getParent() instanceof javax.swing.JRootPane)){
					if(tc == null || tc.getParent() == null){
						return;
					}
					Point p = tc.getParent().getLocation();
					rec.x += p.x;
					rec.y += p.y;
					tc = tc.getParent();
				}
				
				Graphics2D g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(2));
				g.setColor(Color.red);
				g.drawRect(rec.x+1, rec.y+1, rec.width-2, rec.height-2);
				g.setColor(new Color(0,0,0,80));
				g.fillRect(rec.x+1, rec.y+1, rec.width-2, rec.height-2);
			}
		}
	}
}