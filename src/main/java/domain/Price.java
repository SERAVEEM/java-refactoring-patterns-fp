package domain;

public abstract class Price {
	
	//RECFACTOR : abstract untuk penerapan polymorphism harga (strategy pattern)
	public abstract int getPriceCode();
	public abstract double getCharge(int daysRented);
}
