package domain;

public class Rental
{
	// Smell 1: Lazy Class
	// Kelas ini awalnya cuma berisi field data dan fungsi getter standar tanpa logika bisnis sama sekali.
	// Dulu fungsi kelas ini cuma dijadikan tempat mampir buat kelas Customer mengambil data saat perhitungan.
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
	
	// REFACTORED: Hasil pindahan fungsi (Move Method) dari kelas Customer.
	// Sekarang kelas Rental sudah mandiri untuk menghitung harganya sendiri tanpa perlu oper parameter dari luar.
	public double getCharge() {
		return movie.getCharge(daysRented);
	}

	// REFACTORED: Hasil pindahan logika poin reward dari kelas Customer.
	// Fungsi ini murni mengembalikan poin sewa (1 atau 2 poin) khusus untuk objek transaksi ini saja.
	public int getFrequentRenterPoints() {
		if (getMovie ().getPriceCode () == Movie.NEW_RELEASE && getDaysRented () > 1) {
			return 2;
		}
		return 1;
	}

	private Movie movie;
	private int daysRented;
}