package voxspell;

/**
 * this class represents a word. It contains the word, as a string, 
 * and the amount of times the word has been mastered, faulted and failed, as ints.
 * 
 * @author mvan439
 *
 */
public class Word{

	private String _word;
	private int _mastered;
	private int _faulted;
	private int _failed;
	
	//constructor for a new word without history
	public Word(String word){
		_word = word;
	}
	
	//constructor for a word that has history
	public Word(String word, int mastered, int faulted, int failed){
		_word = word;
		_mastered = mastered;
		_faulted = faulted;
		_failed = failed;
	}
	
	//method to increment a mastery value in a word, classes
	//that use this method use the final variables in the AbstractMenu class
	public void increment(int value){
		if(value == 0){
			_mastered += 1;
		} else if(value == 1){
			_faulted += 1;
		} else if(value == 2){
			_failed += 1;
		}
	}
	
	//method to reset a word's statistics, for the clearStats option
	public void reset(){
		_mastered = 0;
		_faulted = 0;
		_failed = 0;
	}

	//method used by TableValues, to get values for the stats table
	public Object getValueAt(int columnIndex) {
		switch(columnIndex){
		case 0:
			return _word;
		case 1:
			return _mastered;
		case 2:
			return _faulted;
		case 3:
			return _failed;
		}
		return 0;
	}
	
	//getter for the word
	public String getWord(){
		return _word;
	}
	
	//toString method used for the save utility
	public String toString(){
		return _word + "," + _mastered + "," + _faulted + "," + _failed;
	}

	//override equals used for part of the loading. we only care about the value within the word variable(in this case)
	@Override
	public boolean equals(Object object){
		boolean sameSame = false;
		if (object != null && object instanceof Word){
			sameSame = this._word.equals(((Word) object).getWord());
		}
		return sameSame;
	}
}