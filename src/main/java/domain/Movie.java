package domain;

public class Movie
{
	// Smell 1: Primitive Obsession 
	// Awalnya kategori film cuma ditunjukkan pakai tipe data primitif int biasa (0, 1, 2).
	// Hal ini bikin kode tidak punya type safety dan kurang fleksibel kalau sistemnya makin besar.
	public static final int CHILDRENS	= 2;
	public static final int REGULAR 	= 0;
	public static final int NEW_RELEASE = 1;
	
	private String title;
	private Price price;
	
	public Movie (String title, int priceCode) {
		this.title 		= title;
		setPriceCode(priceCode); // REFACTOR: Panggil lewat setter agar pembuatan objek strateginya langsung terpicu
	}
	
	// Smell 3: Switch Case (OTG / Orang Tanpa Gejala)
	// Fungsi ini awalnya cuma melempar angka kaku yang memaksa kelas lain buat pakai struktur switch case atau if-else.
	// Sekarang fungsi getter ini sudah sembuh karena dia langsung mengambil kode resmi dari objek Price yang aktif.
	public int getPriceCode () {
		return price.getPriceCode();
	}
	
	public void setPriceCode (int code) {
		// REFACTOR: Menghapus ketergantungan angka primitif dengan menerapkan State/Strategy Pattern
		switch(code) {
			case REGULAR: 
				price = new RegularPrice();
				break;
			case CHILDRENS: 
				price = new ChildrenPrice(); // Sesuaikan dengan nama file kelasmu, misalnya ChildrensPrice
				break;
			case NEW_RELEASE:
				price = new NewReleasePrice();
				break;
		}
	}
	
	public String getTitle () {
		return title;
	}
	
	// REFACTOR: Fungsi baru untuk meneruskan tugas hitung harga ke objek Price masing-masing
	public double getCharge(int daysRented) {
		return price.getCharge(daysRented);
	}
}