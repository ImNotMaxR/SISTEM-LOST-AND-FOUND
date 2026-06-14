# 🔍 Sistem Lost and Found

Aplikasi **Sistem Manajemen Barang Hilang dan Temuan** berbasis desktop yang dibangun menggunakan **Java** dengan antarmuka grafis (GUI). Sistem ini memudahkan pengelolaan laporan barang hilang dan barang temuan secara terorganisir dan efisien.

---

## 📖 Tentang Aplikasi

Sistem Lost and Found dirancang untuk membantu institusi atau organisasi dalam mengelola data barang hilang dan temuan. Pengguna dapat melaporkan barang yang hilang, mencatat barang yang ditemukan, serta memantau status pencocokan antara keduanya.

---

## ✨ Fitur Utama

- **Laporan Barang Hilang** — Input dan kelola data barang yang dilaporkan hilang
- **Laporan Barang Temuan** — Catat barang yang ditemukan beserta detailnya
- **Manajemen Data** — Tambah, ubah, hapus, dan tampilkan data (CRUD)
- **Database Terintegrasi** — Penyimpanan data menggunakan MySQL
- **Antarmuka Grafis (GUI)** — Tampilan desktop yang mudah digunakan
- **Pencarian Data** — Cari barang berdasarkan nama, kategori, atau status

---

## 🚀 Cara Menjalankan

### Prasyarat

- **Java JDK 11** atau lebih baru
- **Apache Maven** terinstall
- **MySQL Server** berjalan di lokal
- **NetBeans IDE** atau IDE Java lainnya (opsional)

### Langkah-langkah

1. Clone repository ini:
   ```bash
   git clone https://github.com/ImNotMaxR/SISTEM-LOST-AND-FOUND.git
   cd SISTEM-LOST-AND-FOUND
   ```

2. Buat database MySQL dan import schema:
   ```sql
   CREATE DATABASE lost_and_found;
   USE lost_and_found;
   -- Import file SQL yang tersedia di folder database/
   ```

3. Sesuaikan konfigurasi koneksi database di file konfigurasi project (host, username, password).

4. Build project menggunakan Maven:
   ```bash
   mvn clean install
   ```

5. Jalankan aplikasi:
   ```bash
   mvn exec:java
   ```
   Atau jalankan file `.jar` hasil build di folder `target/`.

---

## 📁 Struktur Project

```
SISTEM-LOST-AND-FOUND/
├── src/
│   └── main/
│       └── java/
│           └── com/          # Source code utama (Java)
├── pom.xml                   # Konfigurasi Maven & dependency
└── README.md
```

---

## 🛠️ Teknologi

| Teknologi | Keterangan |
|-----------|------------|
| Java | Bahasa pemrograman utama |
| Java Swing / AWT | Framework GUI desktop |
| MySQL | Database penyimpanan data |
| JDBC | Koneksi Java ke MySQL |
| Apache Maven | Build tool & dependency management |

---

## 👥 Tim Pengembang

Proyek ini dikembangkan sebagai tugas kelompok mata kuliah **Dasar Pemrograman Berorientasi Objek (DPBO)**.

| Nama | GitHub |
|------|--------|
| Rico Rezkya | [@ImNotMaxR](https://github.com/ImNotMaxR) |
| Evan Oktavianus | [@kzitoo](https://github.com/kzitoo) |
| Fadil Fauzi Firmansyah | [FadilFauzi-SE](https://github.com/FadilFauzi-SE) |
| Muhammad Kautsar Sangadji | — |
| Aurellya Sabrina Putri Shoury | — |

---

## 📄 Lisensi

Repository ini bersifat publik untuk keperluan akademik. Hubungi tim pengembang untuk informasi lebih lanjut.
