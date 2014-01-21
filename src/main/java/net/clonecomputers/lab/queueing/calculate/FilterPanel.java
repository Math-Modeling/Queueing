package net.clonecomputers.lab.queueing.calculate;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.*;
import java.util.List;

import net.clonecomputers.lab.queueing.calculate.filters.*;

@SuppressWarnings("serial")
public class FilterPanel extends JPanel {
	private List<Filter> activeFilters = new ArrayList<Filter>();
	private JList filterList;
	private StatsMain main;
	private JButton preferredDefaultButton;
	
	public FilterPanel(StatsMain main){
		this.main = main;
	}

	public void initGUI(Set<Filter> filters) {
		filterList = new JList(filters.toArray());
		filterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		filterList.setCellRenderer(new DefaultListCellRenderer() {
			
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				return super.getListCellRendererComponent(list, value.getClass().getSimpleName(), index, isSelected, cellHasFocus);
			}
			
		});
		
		final JList activeFilterJList = new JList();
		activeFilterJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		activeFilterJList.setCellRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				return super.getListCellRendererComponent(list, value.getClass().getSimpleName(), index, isSelected, cellHasFocus);
			}
		});
		activeFilterJList.setModel(new AbstractListModel() {
			@Override public int getSize() {
				return activeFilters.size();
			}
			@Override public Object getElementAt(int i) {
				return activeFilters.get(i);
			}
		});
		
		this.setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
		JPanel navigationPanel = new JPanel();
		navigationPanel.setLayout(new BoxLayout(navigationPanel,BoxLayout.PAGE_AXIS));
		navigationPanel.setBackground(Color.WHITE);
		
		JButton up = new JButton("↑");
		JButton down = new JButton("↓");
		JButton insert = new JButton("→");
		JButton delete = new JButton("⌫");
		JButton filter = new JButton("Filter!");
		this.preferredDefaultButton = filter;
		
		up.setPreferredSize(new Dimension(20,20));
		down.setPreferredSize(new Dimension(20,20));
		delete.setPreferredSize(new Dimension(20,20));
		insert.setPreferredSize(new Dimension(20,20));
		//filter.setPreferredSize(new Dimension(50,20));
		
		navigationPanel.add(up);
		navigationPanel.add(down);
		navigationPanel.add(Box.createVerticalGlue());
		navigationPanel.add(delete);

		JPanel insertPanel = new JPanel();
		insertPanel.setLayout(new BoxLayout(insertPanel, BoxLayout.LINE_AXIS));
		insertPanel.add(Box.createHorizontalGlue());
		insertPanel.add(insert);
		insertPanel.add(Box.createHorizontalGlue());
		
		JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.LINE_AXIS));
		filterPanel.add(Box.createHorizontalGlue());
		filterPanel.add(filter);
		filterPanel.add(Box.createHorizontalGlue());

		buttonPanel.add(Box.createVerticalGlue());
		buttonPanel.add(insertPanel);
		buttonPanel.add(Box.createVerticalGlue());
		buttonPanel.add(filterPanel);
		//FIXME: fix alignment problems in butttonPanel
		
		insert.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				activeFilters.add((Filter)filterList.getSelectedValue());
				activeFilterJList.updateUI();
			}
		});
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int i = activeFilterJList.getSelectedIndex();
				if(i == -1) return;
				activeFilters.remove(i);
				activeFilterJList.clearSelection();
				activeFilterJList.updateUI();
			}
		});
		up.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = activeFilterJList.getSelectedIndex();
				if(i == 0 || i == -1) return;
				Filter f = activeFilters.get(i);
				activeFilters.set(i, activeFilters.get(i-1));
				activeFilters.set(i-1, f);
				activeFilterJList.setSelectedIndex(i-1);
				activeFilterJList.updateUI();
			}
		});
		down.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = activeFilterJList.getSelectedIndex();
				if(i == activeFilters.size()-1 || i == -1) return;
				Filter f = activeFilters.get(i);
				activeFilters.set(i, activeFilters.get(i+1));
				activeFilters.set(i+1, f);
				activeFilterJList.setSelectedIndex(i+1);
				activeFilterJList.updateUI();
			}
		});
		filter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				main.filterData();
			}
		});
		
		this.add(new JScrollPane(filterList));
		this.add(buttonPanel);
		this.add(new JScrollPane(activeFilterJList));
		this.add(navigationPanel);
	}

	public List<Filter> getActiveFilters() {
		return activeFilters;
	}
	
	public void activate() {
		this.getRootPane().setDefaultButton(this.preferredDefaultButton);
	}
	
	public void deactivate() {
		
	}
}
