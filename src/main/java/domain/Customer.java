package domain;

import java.util.Vector;
import java.util.Enumeration;

public class Customer 
{
	public Customer (String name) {
		this.name = name;
	}
	
	public void addRental (Rental rental) {
		rentals.addElement (rental); 
	}
	
	public String getName () {
		return name;
	}
	
	public String statement () {
		double 				totalAmount 			= 0;
		int					frequentRenterPoints 	= 0;
		Enumeration 		rentals 				= this.rentals.elements ();
		String 				result 					= "Rental Record for " + getName () + "\n";
		
		//Smell 1 : Long Method
		/*fungsi statement() ini melanggar SRP, dia looping data, hitung harga film, hitung point reward, format teks struk*/
		
		while (rentals.hasMoreElements ()) {
			double 		thisAmount = 0;
			Rental each = (Rental)rentals.nextElement ();
			
			// Smell 2 : feature envy and switch statement
			// Class customer can edit tenral (each.getDaysRenteng()) and movie data (getPriceCode())
			// aturan untuk harga sewa harusnya ada di dalam class rental bukan di customer
			switch (each.getMovie ().getPriceCode ()) {
				case Movie.REGULAR:
					thisAmount += 2;
					if (each.getDaysRented () > 2)
						thisAmount += (each.getDaysRented () - 2) * 1.5;
					break;
				case Movie.NEW_RELEASE:
					thisAmount += each.getDaysRented () * 3;
					break;
				case Movie.CHILDRENS:
					thisAmount += 1.5;
					if (each.getDaysRented () > 3)
						thisAmount += (each.getDaysRented () - 3) * 1.5;
					break;
			}
			
			//Smell 3: Feature Envy
			// getting a point adalah aturan dari rental bukan aturan customer
			frequentRenterPoints++;
			
			if (each.getMovie ().getPriceCode () == Movie.NEW_RELEASE 
					&& each.getDaysRented () > 1)
				frequentRenterPoints++;
				
			result += "\t" + each.getMovie ().getTitle () + "\t"
								+ String.valueOf (thisAmount) + "\n";
			totalAmount += thisAmount;
				
		}
		
		result += "You owed " + String.valueOf (totalAmount) + "\n";
		result += "You earned " + String.valueOf (frequentRenterPoints) + " frequent renter points\n";
		
		
		return result;
	}
	

	private String name;
	private Vector rentals = new Vector ();
}