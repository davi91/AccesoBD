package utils;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringExpression;
import main.BDApp;

public class NumberBinding extends BooleanBinding {

	private StringExpression precio;
	
	public NumberBinding(StringExpression precio) {
		
		this.precio = precio;
		bind(precio);
	}
	
	@Override
	protected boolean computeValue() {
		
		try {
			
			if( precio.get() == null || precio.get().isEmpty() ) {
				return false;
			}
			
			float p = Float.parseFloat(precio.get());
			
			if( p < BDApp.getMinprecioresidencia() ) {
				return false;
			}
			
			return true;
			
		} catch(NumberFormatException e) {
			
		}
		
		return false;
	}

}
