package com.naii.ctr;

public class NaiiChangeEvent {

	public static final int OPTION_SAVE		=0;
	public static final int OPTION_REMOVE	=1;
	public static final int OPTION_DELETE	=2;
	public static final int OPTION_BACK 	=3;
	public static final int OPTION_IMOROT	=4;
	
	public int option;
	public Object source;
	
	public NaiiChangeEvent(Object source, int option) {
		this.source = source;
		this.option = option;
	}
	
	
}
