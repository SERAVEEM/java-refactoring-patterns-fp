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
	
	//Smell : Mysterious Name -> REFACTORED: Diubah dari statement() menjadi generateTextReceipt() agar lebih deskriptif
	public String generateTextReceipt () {
		Enumeration 		rentals 				= this.rentals.elements ();
		String 				result 					= "Rental Record for " + getName () + "\n";
		
		//Smell 1 : Long Method
		/*fungsi statement() ini melanggar SRP, dia looping data, hitung harga film, hitung point reward, format teks struk*/
		
		while (rentals.hasMoreElements ()) {
			Rental each = (Rental)rentals.nextElement ();
			
			// REFACTORED & SEMBUH:
			// - Smell 4 (Duplicate Code): Diatasi dengan Extract & Move Method ke Rental.java
			// - Smell 3 (Feature Envy): Diatasi dengan membiarkan Rental menghitung datanya sendiri (Tell, Don't Ask)
			// - Temp Variable Pollution: Mengeliminasi variabel 'thisAmount' dan langsung mencetak nilainya
			result += "\t" + each.getMovie ().getTitle () + "\t" + String.valueOf (each.getCharge()) + "\n";
		}
		
		// 🛠️ REFACTORED: Menggantikan akumulasi variabel lokal totalAmount dan frequentRenterPoints dengan query method terpusat
		result += "You owed " + String.valueOf (getTotalCharge()) + "\n";
		result += "You earned " + String.valueOf (getTotalFrequentRenterPoints()) + " frequent renter points\n";
		
		return result;
	}

	public String htmlStatement () {
		Enumeration 		rentals 				= this.rentals.elements ();
		String 				result 					= "<h1>Rental Record for <em>" + getName () + "</em></h1>\n<p>\n";
		
		while (rentals.hasMoreElements ()) {
			Rental each = (Rental)rentals.nextElement ();
			
			// REFACTORED: 
			// Smell 4 (Duplicated Code) pada logika perhitungan harga dan poin di htmlStatement() 
			// kini punah karena langsung memanggil fungsi terpusat dari objek masing-masing.
			result += "\t" + each.getMovie ().getTitle () + ": " + String.valueOf (each.getCharge()) + "<br>\n";
		}
		
		result += "</p>\n<p>You owed <em>" + String.valueOf (getTotalCharge()) + "</em></p>\n";
		result += "<p>You earned <em>" + String.valueOf (getTotalFrequentRenterPoints()) + "</em> frequent renter points</p>\n";
		return result;
	}
	
	//REFACTORED : menggantikan variabel lokal totalAmount dengan query method terpusat
	public double getTotalCharge() {
		double result = 0;
		Enumeration rentals = this.rentals.elements();
		while (rentals.hasMoreElements()) {
			Rental each = (Rental) rentals.nextElement();
			result += each.getCharge();
		}
		return result;
	}

	//REFACTORED : Menggantikan variabel lokal frequentRenterPoints dengan query method terpusat 
	public int getTotalFrequentRenterPoints() {
		int result = 0;
		Enumeration rentals = this.rentals.elements();
		while (rentals.hasMoreElements()) {
			Rental each = (Rental) rentals.nextElement();
			result += each.getFrequentRenterPoints();
		} 
		return result;
	}
	
	private String name;
	private Vector rentals = new Vector ();
}