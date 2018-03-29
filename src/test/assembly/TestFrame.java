package test.assembly;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

public abstract class TestFrame{

	protected JFrame fr ;
	protected TestComponentTree tree;

	
	public TestFrame() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		fr = new JFrame("test frame");
		fr.setJMenuBar(createMenu());
		Component c = getContent();
		if(c != null){
			fr.add(c);
		}
		fr.setSize(getSize());
		fr.setDefaultCloseOperation(3);
		fr.setLocationRelativeTo(null);
	}

	protected Dimension getSize(){
		return new Dimension(900, 500);
	}

	private JMenuBar createMenu() {
		JMenuBar bar = new JMenuBar();
		JMenu options = new JMenu("Options");
		JMenuItem viewComponentTree = new JMenuItem("ViewComponentTree");
		viewComponentTree.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(tree == null){
					tree = new TestComponentTree(fr);
				}
				tree.show();
			}
		});
		options.add(viewComponentTree);
		bar.add(options);
		bar.setBorder(null);
		return bar;
	}

	public void show() {
		fr.setVisible(true);
		fr.getContentPane().revalidate();
	}
	
	public abstract Component getContent();

	public JFrame getFrame() {
		return fr;
	}
	
	public void addMenuItem(){
		
	}
}
