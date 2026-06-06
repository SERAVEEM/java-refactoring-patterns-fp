package domain;

public class Rental
{
	//Smell 1 : Lazy Class
	//class ini cuman ada field fungsi getter standar, class ini juga tidak punya business logic sama sekali
	// Fungsi kelas ini cuman untuk class Customer mengambil data nya untuk perhitungan
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
	
	private Movie movie;
	private int daysRented;
}