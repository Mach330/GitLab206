package voxspell;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class TableValues extends AbstractTableModel{

	private List<Word> _wordData = new ArrayList<Word>();
	private String[] columnNames = {"Word", "Mastered", "Faulted", "Failed"};

	private final String _savePath = ".sav/SaveData";
	private final String _wordPath = "wordlist";
	private final String _reviewPath = ".sav/review";
	
	private List<String> _wordlist = new ArrayList<String>();
	private List<String> _review = new ArrayList<String>();
	
	private List<Word> _wordsToRemove = new ArrayList<Word>();

	//add loading logic from the save file in the constructor
	public TableValues(){

		try {
			//load words from saved data
			if (new File(_savePath).isFile()){
				for (String line : Files.readAllLines(Paths.get(_savePath), StandardCharsets.UTF_8)) {
					String[] parts = line.split(",");
					_wordData.add(new Word(parts[0], Integer.valueOf(parts[1]),
							Integer.valueOf(parts[2]),Integer.valueOf(parts[3])));
				}
			}
			
			//instantiate _review (if it exists)
			if(new File(_reviewPath).isFile()){
				for(String line : Files.readAllLines(Paths.get(_reviewPath), StandardCharsets.UTF_8)){
					if(line.length() >= 1){
						_review.add(line.toLowerCase());
						//System.out.println(line);
					}
				}
			}
			
			//edit to be updated with new words from wordlist
			//add words that are not in _wordData
			if(new File(_wordPath).isFile()){
				//instantiate _wordlist
				for (String line : Files.readAllLines(Paths.get(_wordPath), StandardCharsets.UTF_8)) {
					if(line.length() >= 1){
						_wordlist.add(line.toLowerCase());
					}	
				}
				
				//add words that are in _wordlist but not in wordData
				for(String word:_wordlist){
					if(!_wordData.contains(new Word(word))){
						_wordData.add(new Word(word));
					}
				}
				//remove words that are in _wordData but not _wordlist
				for(Word word:_wordData){
					if(!_wordlist.contains(word.getWord())){
						_wordsToRemove.add(word);
					}
				}

				for(Word word:_wordsToRemove){
					_wordData.remove(word);
				}
				
			} else {
				//show a frame if there was a problem loading data. not the best errorhandling system, but works as a simple prototype
				//is not in the catch statements as file existence is always checked first
				JFrame saveFrame = new JFrame();
				JOptionPane.showMessageDialog(saveFrame, "There was a problem loading the data. The system will now exit", 
						"LOAD STATUS", JOptionPane.INFORMATION_MESSAGE);
				System.exit(1);
				//System.out.println("Problem loading files");
			}
			//if there is a problem with loading data, a popup will state that there is, and exit the system
		} catch (NumberFormatException e) {
			JFrame saveFrame = new JFrame();
			JOptionPane.showMessageDialog(saveFrame, "There was a problem loading the data. The system will now exit", 
					"LOAD STATUS", JOptionPane.INFORMATION_MESSAGE);
			System.exit(1);
		} catch (IOException e) {
			JFrame saveFrame = new JFrame();
			JOptionPane.showMessageDialog(saveFrame, "There was a problem loading the data. The system will now exit", 
					"LOAD STATUS", JOptionPane.INFORMATION_MESSAGE);
			System.exit(1);
		}
	}

	//methods used by the JTable that references this AbstractTableModel
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public int getRowCount() {
		return _wordData.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return _wordData.get(rowIndex).getValueAt(columnIndex);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		_wordData.get(rowIndex).increment(columnIndex - 1);
		fireTableCellUpdated(rowIndex, columnIndex);
	}

	//incrementing a word's mastery levels, and fire TableCellUpdated
	public void increment(String wordName, int type){
		int rowIndex = 0;
		for (Word word :_wordData){
			if(word.getWord().toLowerCase().equals(wordName.toLowerCase())){
				//System.out.println("Incrementing " + word + " type: " + type);
				word.increment(type);
				break;
			}
			rowIndex += 1;
		}
		fireTableCellUpdated(rowIndex, type + 1);
	}

	//get the word value from it's index
	public String getWord(int rowIndex){
		return _wordData.get(rowIndex).getWord();
	}

	//add the saving logic
	public int saveValues(){
		File yourFile = new File(_savePath);
		if(!yourFile.exists()) {
			try {
				new File(".sav").mkdir();
				yourFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return 2;
			}
		} 
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(_savePath), "utf-8"))) {
			for (Word word:_wordData){
				writer.write(word.toString());
				writer.write(System.getProperty("line.separator"));
			}
		} catch (IOException e) {
			e.printStackTrace();
			return 2;
		}

		File reviewFile = new File(_reviewPath);
		if(!reviewFile.exists()) {
			try {
				new File(".sav").mkdir();
				reviewFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return 2;
			}
		} 
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(_reviewPath), "utf-8"))) {
			for (String word:_review){
				writer.write(word);
				writer.write(System.getProperty("line.separator"));
			}
		} catch (IOException e) {
			e.printStackTrace();
			return 2;
		}

		return 0;
	}

	//the clearing logic. asks the user if they are sure, and resets all the mastery levels and resets the review words
	//calls saveValues() so the clearing is permanent
	public int clearStats(){

		JFrame frame = new JFrame();
		int dialogResult = JOptionPane.showConfirmDialog(frame, 
				"Are you sure? \n Once cleared it cannot be recovered!", 
				"CLEAR STATUS", JOptionPane.YES_NO_OPTION);

		if(dialogResult == JOptionPane.YES_OPTION){
			for(Word word: _wordData){
				word.reset();
			}
			fireTableDataChanged();

			_review = new ArrayList<String>();
			return saveValues();
		}
		return -1;
	}

	//method to retrieve a list of words for testing purposes
	public List<String> getWordsToTest(String wordlist){
		if(wordlist.toLowerCase().equals("review")){
			return _review;
		} else {
			return _wordlist;
		}
	}

	//adding a word to the review list
	public boolean addToReview(String word){
		if (!_review.contains(word)){
			_review.add(word);
			return true;
		}
		return false;
	}

	//removing a word from the review list
	public void removeFromReview(String word){
		if(_review.contains(word)){
			_review.remove(word);
		}
	}
}