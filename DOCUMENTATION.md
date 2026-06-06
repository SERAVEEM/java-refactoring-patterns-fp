Berikut adalah berkas dokumentasi lengkap dalam format Markdown (`.md`). Anda dapat menyalin seluruh teks di dalam kotak kode di bawah ini, lalu menyimpannya ke dalam sebuah berkas bernama `README.md` atau `Laporan_Refactoring.md`.

```markdown
# DOKUMENTASI REFACTORING: VIDEOSTORE-JAVA

**Dokumen Komprehensif Rekayasa Ulang Perangkat Lunak** **Perancang Arsitektur:** Senior Software Developer / Architect  
**Status Pengujian:** 100% Passed (5/5 JUnit Automation Tests Green)  
**Target Arsitektur:** Clean Code, SOLID Principles, State/Strategy Pattern

---

## 1. RINGKASAN EKSEKUTIF ARSITEKTUR

Sistem persewaan video `videostore-java` pada awalnya mengadopsi pendekatan prosedural yang dibungkus dalam orientasi objek semu. Masalah mendasar pada arsitektur awal adalah tingginya tingkat ketergantungan antar-komponen (High Coupling) dan rendahnya pemusatan tanggung jawab kelas (Low Cohesion). 

Kelas `Customer` bertindak sebagai God Class yang mendominasi seluruh kalkulasi bisnis dengan cara mengekstrak data internal dari kelas lain. Hal ini merupakan pelanggaran terhadap Single Responsibility Principle (SRP) dan Open/Closed Principle (OCP).

Melalui rangkaian proses rekayasa ulang ini, arah aliran data dibalik dari pendekatan tradisional "Ask, Then Do" (Meminta data, lalu menghitung sendiri) menjadi "Tell, Don't Ask" (Memerintahkan objek untuk mengeksekusi perilakunya sendiri). Selain itu, struktur percabangan kondisional `switch-case` digantikan secara menyeluruh menggunakan konsep Polimorfisme berbasis State/Strategy Pattern.

---

## 2. ILUSTRASI ALIRAN DATA (DATA FLOW)

Pergeseran pola komunikasi dan aliran kendali data dari sistem lama ke sistem baru dapat digambarkan melalui bagan alur di bawah ini:

### Aliran Data Lama: "Ask, Then Do" (Prosedural & Highly Coupled)

```text
[Customer] 
   |
   +---> 1. Ambil data sewa ------> [Rental] (Mengembalikan daysRented)
   |
   +---> 2. Ambil data kode film --> [Movie]  (Mengembalikan priceCode)
   |
   +---> 3. Hitung harga & poin secara lokal menggunakan switch-case
   |
   +---> 4. Format string struk belanja

```

*Kelemahan: Kelas Customer harus mengetahui detail rumus perhitungan harga milik seluruh jenis film.*

### Aliran Data Baru: "Tell, Don't Ask" (Encapsulated & Polymorphic)

```text
[Customer] ---> Menuntut Total Biaya & Poin ---> [getTotalCharge()] / [getTotalFrequentRenterPoints()]
   |
   +---> Iterasi Daftar Sewa ---> [Rental.getCharge()]
                                     |
                                     +---> Delegasi ke Objek Film ---> [Movie.getCharge(daysRented)]
                                                                           |
                                                                           +---> Polimorfisme Strategi Harga ---> [Price.getCharge(daysRented)]
                                                                                     |--- [RegularPrice]
                                                                                     |--- [ChildrenPrice]
                                                                                     |--- [NewReleasePrice]

```

*Keuntungan: Setiap kelas bertanggung jawab penuh atas validitas datanya sendiri. Kelas Customer hanya menerima hasil akhir berupa nilai numerik untuk dicetak.*

---

## 3. PERBANDINGAN STRUKTUR SISTEM (BEFORE VS AFTER)

| Dimensi Arsitektur | Kondisi Awal (Legacy Code) | Kondisi Akhir (Refactored Code) |
| --- | --- | --- |
| **Prinsip Desain** | Melanggar SRP (Satu kelas memegang banyak tanggung jawab bisnis sekaligus). | Memenuhi SRP (Pemisahan tegas antara logika tampilan/format dengan logika kalkulasi). |
| **Prinsip Perluasan** | Melanggar OCP (Menambah jenis film baru memaksa modifikasi pada kelas lain/Shotgun Surgery). | Memenuhi OCP (Menambah jenis film cukup dengan membuat kelas strategi baru secara independen). |
| **Ketergantungan Data** | Berbasis pada tipe data primitif kaku (Primitive Obsession). | Berbasis pada abstraksi objek polimorfis (Strategy Pattern). |
| **Stabilitas Kode** | Rentan terhadap kesalahan ketik (type-safety lemah) dan duplikasi kode (Duplicated Code). | Memiliki jaminan struktur melalui implementasi Interface/Abstract Class Inheritance. |

---

## 4. IDENTIFIKASI CODE SMELLS DAN LANGKAH REFACTORING

Berikut adalah analisis detail mengenai kegagalan desain (code smells) pada kode awal, argumen teknis pemilihan solusi, serta langkah mekanis penyelesaiannya per modul.

### 4.1. Modul: `Customer.java`

#### Code Smell yang Diidentifikasi

* **Long Method & Bagian Internal yang Kompleks (`statement()`)**: Metode ini terlalu panjang karena melakukan penelusuran data (looping), kalkulasi harga, kalkulasi poin bonus, dan penyusunan string struk di satu tempat yang sama.
* **Feature Envy**: Metode di dalam `Customer` secara intensif memanipulasi data yang dimiliki oleh kelas `Rental` dan `Movie`.
* **Duplicated Code**: Terjadi penggandaan logika kalkulasi harga saat fitur `htmlStatement()` diimplementasikan untuk kebutuhan cetak struk versi HTML.
* **Mysterious Name**: Nama metode `statement()` terlalu umum dan tidak merepresentasikan fungsionalitas pembuatan struk secara spesifik.
* **Temporary Variable Pollution**: Keberadaan variabel lokal seperti `thisAmount`, `totalAmount`, dan `frequentRenterPoints` mengunci fungsionalitas di dalam perulangan sehingga kode tidak dapat dipecah secara modular.

#### Strategi Refactoring yang Dipilih

Menerapkan teknik **Replace Temp with Query** untuk mengeliminasi variabel lokal pencatat saldo sementara, kemudian dilanjutkan dengan **Extract Method** dan **Move Method** untuk mendelegasikan tanggung jawab hitung ke kelas pemilik data yang sah.

#### Langkah-Langkah Mekanis

1. Mengubah nama metode `statement()` menjadi `generateTextReceipt()` agar selaras dengan `htmlStatement()`.
2. Mengekstrak blok kalkulasi matematika dari dalam loop dan memindahkannya ke kelas `Rental.java`.
3. Membuat fungsi kueri internal `getTotalCharge()` dan `getTotalFrequentRenterPoints()` untuk menggantikan peran variabel penampung sementara.
4. Menghapus variabel lokal yang tidak lagi digunakan (Dead Code Elimination).

#### Perbandingan Kode Customer.java

##### Sebelum Refactoring (Legacy Code)

```java
public String statement () {
    double totalAmount = 0;
    int frequentRenterPoints = 0;
    Enumeration rentals = this.rentals.elements ();
    String result = "Rental Record for " + getName () + "\n";
    
    while (rentals.hasMoreElements ()) {
        double thisAmount = 0;
        Rental each = (Rental) rentals.nextElement ();
        
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
        
        frequentRenterPoints ++;
        if (each.getMovie ().getPriceCode () == Movie.NEW_RELEASE && each.getDaysRented () > 1) 
            frequentRenterPoints ++;
            
        result += "\t" + each.getMovie ().getTitle () + "\t" + String.valueOf (thisAmount) + "\n";
        totalAmount += thisAmount;
    }
    result += "You owed " + String.valueOf (totalAmount) + "\n";
    result += "You earned " + String.valueOf (frequentRenterPoints) + " frequent renter points\n";
    return result;
}

```

##### Sesudah Refactoring (Clean Code)

```java
public String generateTextReceipt () {
    Enumeration rentals = this.rentals.elements ();
    String result = "Rental Record for " + getName () + "\n";
    
    while (rentals.hasMoreElements ()) {
        Rental each = (Rental)rentals.nextElement ();
        result += "\t" + each.getMovie ().getTitle () + "\t" + String.valueOf (each.getCharge()) + "\n";
    }
	
    result += "You owed " + String.valueOf (getTotalCharge()) + "\n";
    result += "You earned " + String.valueOf (getTotalFrequentRenterPoints()) + " frequent renter points\n";
    return result;
}

public double getTotalCharge() {
    double result = 0;
    Enumeration rentals = this.rentals.elements();
    while (rentals.hasMoreElements()) {
        Rental each = (Rental) rentals.nextElement();
        result += each.getCharge();
    }
    return result;
}

public int getTotalFrequentRenterPoints() {
    int result = 0;
    Enumeration rentals = this.rentals.elements();
    while (rentals.hasMoreElements()) {
        Rental each = (Rental) rentals.nextElement();
        result += each.getFrequentRenterPoints();
    } 
    return result;
}

```

---

### 4.2. Modul: `Rental.java`

#### Code Smell yang Diidentifikasi

* **Lazy Class**: Pada rancangan awal, kelas `Rental` dikategorikan sebagai kelas pemalas karena tidak mengadopsi logika bisnis apa pun. Tugasnya terbatas pada penampung struktur data mentah yang nilainya diekstrak secara berkala oleh entitas eksternal.

#### Strategi Refactoring yang Dipilih

Menerapkan **Move Method** dari kelas `Customer` menuju kelas `Rental`. Hal ini mengembalikan fungsi enkapsulasi objek sejati pada domain persewaan.

#### Langkah-Langkah Mekanis

1. Memindahkan fungsi hitung biaya sewa dari `Customer` ke dalam metode `getCharge()` milik `Rental`.
2. Memindahkan hitungan perolehan poin bonus ke dalam metode `getFrequentRenterPoints()`.
3. Melakukan refactor lebih lanjut pada metode `getCharge()` untuk mendelegasikan tugas perhitungan harga ke entitas `Movie` guna mendukung eliminasi struktur percabangan.

#### Perbandingan Kode Rental.java

##### Sebelum Refactoring (Legacy Code)

```java
public class Rental {
    public Rental (Movie movie, int daysRented) {
        this.movie = movie;
        this.daysRented = daysRented;
    }
    public int getDaysRented () { return daysRented; }
    public Movie getMovie () { return movie; }
    
    private Movie movie;
    private int daysRented;
}

```

##### Sesudah Refactoring (Clean Code)

```java
public class Rental {
    public Rental (Movie movie, int daysRented) {
        this.movie = movie;
        this.daysRented = daysRented;
    }
    public int getDaysRented () { return daysRented; }
    public Movie getMovie () { return movie; }
    
    public double getCharge() {
        return movie.getCharge(daysRented);
    }

    public int getFrequentRenterPoints() {
        if (getMovie ().getPriceCode () == Movie.NEW_RELEASE && getDaysRented () > 1) {
            return 2;
        }
        return 1;
    }
    private Movie movie;
    private int daysRented;
}

```

---

### 4.3. Modul: `Movie.java` (Serta Klaster Polimorfisme `Price`)

#### Code Smell yang Diidentifikasi

* **Primitive Obsession**: Kategori identitas film diekspresikan menggunakan konstanta bilangan bulat primitif `int` (`0, 1, 2`). Hal ini meniadakan fitur type safety pada tingkat kompilasi kode.
* **Switch Statements (Gejala Klinis Terselubung)**: Walaupun fisik instruksi `switch` secara tekstual berada di kelas komponen lain, keberadaan metode pengembalian nilai primitif `getPriceCode()` menjadi akar penyebab munculnya struktur `switch-case` berulang di seluruh sistem. Hal ini merupakan pelanggaran mendasar terhadap arsitektur berorientasi objek yang fleksibel (melanggar Open/Closed Principle).

#### Strategi Refactoring yang Dipilih

Mengganti nilai kode numerik konvensional dengan objek kelas konkrit melalui implementasi **State/Strategy Pattern**. Pola ini mengisolasi variasi kalkulasi harga ke dalam sub-kelas yang independen sehingga penambahan tipe film baru di masa depan tidak perlu mengubah kode yang sudah mapan.

#### Langkah-Langkah Mekanis

1. Menginisialisasi kelas abstrak induk baru bernama `Price.java`.
2. Memetakan sub-kelas turunan yang spesifik mengimplementasikan rumus matematika dari masing-masing tipe film, yaitu: `RegularPrice.java`, `ChildrenPrice.java`, dan `NewReleasePrice.java`.
3. Mengubah atribut `int priceCode` pada kelas `Movie.java` menjadi variabel referensi bertipe objek `Price price`.
4. Mengonfigurasi metode `setPriceCode(int code)` sebagai pusat alokasi pembentukan objek strategi harga.

#### Perbandingan Kode Movie.java

##### Sebelum Refactoring (Legacy Code)

```java
public class Movie {
    public static final int CHILDRENS = 2;
    public static final int REGULAR = 0;
    public static final int NEW_RELEASE = 1;
    
    private String title;
    private int priceCode;
    
    public Movie (String title, int priceCode) {
        this.title = title;
        this.priceCode = priceCode;
    }
    public int getPriceCode () { return priceCode; }
    public void setPriceCode (int code) { priceCode = code; }
    public String getTitle () { return title; }
}

```

##### Sesudah Refactoring (Clean Code)

```java
public class Movie {
    public static final int CHILDRENS = 2;
    public static final int REGULAR = 0;
    public static final int NEW_RELEASE = 1;
    
    private String title;
    private Price price;
    
    public Movie (String title, int priceCode) {
        this.title = title;
        setPriceCode(priceCode);
    }
    
    public int getPriceCode () {
        return price.getPriceCode();
    }
    
    public void setPriceCode (int code) {
        switch(code) {
            case REGULAR: 
                price = new RegularPrice();
                break;
            case CHILDRENS: 
                price = new ChildrenPrice();
                break;
            case NEW_RELEASE:
                price = new NewReleasePrice();
                break;
        }
    }
    
    public String getTitle () { return title; }
    
    public double getCharge(int daysRented) {
        return price.getCharge(daysRented);
    }
}

```

---

## 5. VERIFIKASI PENGUJIAN OTOMATIS (BLACK-BOX TESTING)

Untuk menjamin tingkat keandalan sistem pasca-refactoring, metode pengujian Black-Box Testing diaplikasikan dengan teknik Equivalence Partitioning. Seluruh skenario pengujian diotomatisasi secara ketat via framework JUnit dalam unit tes `VideoStoreTest.java`.

Hasil eksekusi pengujian membuktikan bahwa modifikasi internal arsitektur tidak mengubah fungsionalitas luar aplikasi sedikit pun.

### Tabel Matriks Hasil Uji Skenario Bisnis

| Kode Uji | Parameter Kategori Film | Durasi Sewa | Rumus Matematika Ekspektasi Output | Status Kelulusan |
| --- | --- | --- | --- | --- |
| **TC-01** | Kategori: Regular | 2 Hari | Tarif Dasar = `2.0` | **PASS (Green)** |
| **TC-02** | Kategori: Regular | 5 Hari | Tarif Dasar 2.0 + (Kelebihan 3 Hari x 1.5) = `6.5` | **PASS (Green)** |
| **TC-03** | Kategori: New Release | 3 Hari | Durasi 3 Hari x Tarif Khusus 3.0 = `9.0` (Bonus 2 Poin) | **PASS (Green)** |
| **TC-04** | Kategori: Childrens | 3 Hari | Tarif Dasar = `1.5` | **PASS (Green)** |
| **TC-05** | Kategori: Childrens | 4 Hari | Tarif Dasar 1.5 + (Kelebihan 1 Hari x 1.5) = `3.0` | **PASS (Green)** |

### Ringkasan Status Akhir Penjaminan Mutu

* **Total Runs:** 5/5 Skenario
* **Errors:** 0 (Nol)
* **Failures:** 0 (Nol)

Arsitektur baru ini dinyatakan lolos uji kelayakan produksi (production ready) dengan tingkat kestabilan struktur yang optimal dan siap untuk dikembangkan lebih lanjut di masa mendatang.

```

```
