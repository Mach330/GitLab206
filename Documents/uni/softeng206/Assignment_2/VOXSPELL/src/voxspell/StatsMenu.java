package voxspell;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableRowSorter;

@SuppressWarnings("serial")
public class StatsMenu extends AbstractMenu implements ActionListener{

	private TableValues _tv;

	public StatsMenu (Voxspell voxspell){
		_voxspell = voxspell;
		
		//setup the buttons. the back button links to Voxspell
		JButton clearBtn = new JButton("Clear");
		clearBtn.addActionListener(this);
		JButton statsBackBtn = new JButton("Back");
		statsBackBtn.addActionListener(_voxspell);
		JButton statsSaveBtn = new JButton("Save");
		statsSaveBtn.addActionListener(this);
		
		//setting up a RowFilter object that means only words that have been tested
		//are shown
		RowFilter<Object,Object> testedFilter = new RowFilter<Object,Object>() {
			  public boolean include(Entry<?, ?> entry) {
			    for(int m = 3; m > 0; m--){
			    	if(!entry.getStringValue(m).equals("0")){
			    		return true;
			    	}
			    }
			    return false;
			 }
			};
		
		//setting up the JTable, overriding the isCellEditable 
		//so no cells can be edited
		_tv = new TableValues();
		JTable stats = new JTable(_tv){
			
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false, cannot be edited
		       return false;
		    }
		};
		
		//creating the sorter objects.
		//this one allows the user to resort the table using the table headers
		stats.setAutoCreateRowSorter(true);
		stats.getTableHeader().setReorderingAllowed(false);
		TableRowSorter<TableValues> sorter = new TableRowSorter<TableValues>(_tv);
		sorter.setRowFilter(testedFilter);
		stats.setRowSorter(sorter);
		
		//sorting the table in alphabetical order
		List<RowSorter.SortKey> sortKeys = new ArrayList<>(); 
		sortKeys.add(new RowSorter.SortKey(0,  SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
		
		//adding a scrollPane to the table
		JScrollPane scrollPane = new JScrollPane(stats);
		scrollPane.setPreferredSize(new Dimension(335,242));
		
		//creating a panel for the buttons
		JPanel statsBtnPanel = new JPanel(new FlowLayout());
		statsBtnPanel.add(statsSaveBtn);
		statsBtnPanel.add(clearBtn);
		statsBtnPanel.add(statsBackBtn);
		
		//adding everything to the pane
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		add(statsBtnPanel, BorderLayout.PAGE_END);
	}
	
	//method allowing the same TableValues object to be shared among classes
	public TableValues getTableValues(){
		return _tv;
	}

	//Statsmenu has the actionlisteners for the save and clear menus, forwards the 
	//calls to TableValues
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		int returnValue;
		JFrame saveFrame = new JFrame();
		
		switch(command.toLowerCase()) {
		case "save":
			returnValue = _tv.saveValues();
			if(returnValue == 0){
				JOptionPane.showMessageDialog(saveFrame, "Succesfully saved!", "SAVE STATUS", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(saveFrame, "There was a problem saving the data.", 
						"SAVE STATUS", JOptionPane.INFORMATION_MESSAGE);
			}
			break;
		case "clear":
			returnValue = _tv.clearStats();
			if(returnValue == -1){
			} else if(returnValue == 0){
				JOptionPane.showMessageDialog(saveFrame, "Succesfully cleared!", "CLEAR STATUS", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(saveFrame, "There was a problem clearing the data.", 
						"CLEAR STATUS", JOptionPane.INFORMATION_MESSAGE);
			}
			break;
	}
		
	}
}
