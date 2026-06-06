package domain;

public class Movie
{
	//Smell 1: Primitive Obsession 
	//Kategori film di tunjukan menggunakan tipe data primitif (int) '0, 1, 2'
	//Ini menyebbakan tidak ada type safety, kalau semisal nanti tiba tiba ada object movie 
	public static final int CHILDRENS	= 2;
	public static final int REGULAR 	= 0;
	public static final int NEW_RELEASE = 1;
	
	private String title;
	private int priceCode;
	
	public Movie (String title, int priceCode) {
		this.title 		= title;
		this.priceCode 	= priceCode;
	}
	
	//Smell 3: swtich case
	//PriceCOde cuman mengembalikan angka biner yang kaku
	//fugngsi getter ini memaksa class lain (Customer/retail) untuk menggunnakan struktur switch case atau if else demi tahu cara memperlakukan tipe film nya
	//
	public int getPriceCode () {
		return priceCode;
	}
	
	public void setPriceCode (int code) {
		priceCode = code;
	}
	
	public String getTitle () {
		return title;
	}
	
}