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
			
			//REFACTORED: Langsung memasukkan nilai harga ke string teks tanpa perantara variabel lokal
						result += "\t" + each.getMovie ().getTitle () + "\t" + String.valueOf (each.getCharge()) + "\n";
			
			//smell 4 : duplicate code. 
			//extract method lalu move ke class Rental, agar Rental bisa menghitung nya lalu di sini hanya perlu memanggil saja
			//REFACTORED: Memanggil getCharge() gaya baru tanpa oper parameter. Customer tidak perlu tahu rumus hitungnya.
		
			//Smell 3: Feature Envy
			//getting a point adalah aturan dari rental bukan aturan customer
			//REFACTORED: Poin murni dihitung oleh Rental, di sini Customer tinggal mengakumulasikan totalnya saja dengan +=
		}
		
		result += "You owed " + String.valueOf (getTotalCharge()) + "\n";
		result += "You earned " + String.valueOf (getTotalFrequentRenterPoints()) + " frequent renter points\n";
		
		return result;
	}

	public String htmlStatement () {
		Enumeration 		rentals 				= this.rentals.elements ();
		String 				result 					= "<h1>Rental Record for <em>" + getName () + "</em></h1>\n<p>\n";
		
		while (rentals.hasMoreElements ()) {
			Rental each = (Rental)rentals.nextElement ();
			
			// 🛠️ REFACTORED: Langsung memasukkan nilai harga ke format HTML
						result += "\t" + each.getMovie ().getTitle () + ": " + String.valueOf (each.getCharge()) + "<br>\n";
						
			//Smell 4: Duplicated Code 
			//Logika perhitungan ini sama persis dengan yang ada di fungsi statement().
			//REFACTORED: htmlStatement() sekarang memakai fungsi getCharge() yang sama dari class Rental.			
		}
		
		result += "</p>\n<p>You owed <em>" + String.valueOf (getTotalCharge()) + "</em></p>\n";
		result += "<p>You earned <em>" + String.valueOf (getTotalFrequentRenterPoints()) + "</em> frequent renter points</p>\n";
		return result;
	}
	
	//REFACTORED : menggantikan variabel lokal totalAmount dengn query method terpusat
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
	//
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