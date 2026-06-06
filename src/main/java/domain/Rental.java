package domain;

public class Rental
{
	//Smell 1 : Lazy Class
	//class ini cuman ada field fungsi getter standar, class ini juga tidak punya business logic sama sekali
	//Fungsi kelas ini cuman untuk class Customer mengambil data nya untuk perhitungan
	public Rental (Movie movie, int daysRented) {
		this.movie 		= movie;
		this.daysRented = daysRented;
	}
	
	public int getDaysRented () {
		return daysRented;
	}
	
	public Movie getMovie () {
		return movie;
	}
	
	// REFACTORED: Hasil pindahan (Move Method) dari Customer. 
	// Sekarang Rental sendiri yang menghitung harganya sendiri tanpa bergantung pada parameter dari luar (Tell, Don't Ask).
	public double getCharge() {
		double result = 0;
		switch (getMovie ().getPriceCode ()) {
			case Movie.REGULAR:
				result += 2;
				if (getDaysRented () > 2)
					result += (getDaysRented () - 2) * 1.5;
				break;
			case Movie.NEW_RELEASE:
				result += getDaysRented () * 3;
				break;
			case Movie.CHILDRENS:
				result += 1.5;
				if (getDaysRented () > 3)
					result += (getDaysRented () - 3) * 1.5;
				break;
		}
		return result;
	}

	// REFACTORED: Hasil pindahan logika poin reward. 
	// Mengembalikan poin sewa (1 atau 2) khusus untuk objek transaksi ini saja.
	public int getFrequentRenterPoints() {
		if (getMovie ().getPriceCode () == Movie.NEW_RELEASE && getDaysRented () > 1) {
			return 2;
		}
		return 1;
	}

	private Movie movie;
	private int daysRented;
}