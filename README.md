# 💬 PrivChat - WebSocket Chat Application

![Platform Android](https://shields.io)

**PrivChat** adalah aplikasi *chatting* berbasis Android yang menggunakan metode **WebSocket** untuk komunikasi *real-time* yang cepat, ringan, dan aman. Anda bisa langsung bergabung ke forum global atau membuat ruang obrolan privat Anda sendiri!

---

## 🚀 Fitur Utama
* **Real-time Chatting:** Komunikasi instan tanpa *delay* berkat teknologi WebSocket (`wss://`).
* **Profil Kustom:** Bebas masukkan nama pengguna dan unggah foto profil sesuka Anda.
* **Forum Publik:** Berinteraksi langsung dengan pengguna lain di forum utama.
* **Self-Hosting Friendly:** Bebas hosting API sendiri untuk keamanan dan privasi tingkat tinggi.

---

## 🛠️ Langkah Penggunaan & Instalasi

### 1. Unduh & Instal Aplikasi
1. Clone repositori ini atau unduh file berkas aplikasi ke perangkat Anda.
2. Instal file APK **PrivChat** ke *smartphone* Android Anda.

### 2. Hubungkan ke Server API
Secara bawaan, jika Anda ingin langsung bergabung dengan admin dan komunitas di forum global, Anda dapat menggunakan server publik berikut:
* **Server Default:** `wss://aditya.wsip.uno`

> 📝 **Cara Masuk:** Cukup buka aplikasi, masukkan nama panggilan (username) Anda, unggah foto profil bebas, lalu Anda siap mengobrol di forum!

---

## 🔒 Ingin Lebih Privat? (Buat Server Sendiri)

Jika Anda ingin obrolan Anda **jauh lebih aman, privat, dan memiliki forum sendiri**, Anda bisa meng-hosting file `api-websocket.js` secara mandiri.

### Cara Mendeploy API di Termux (Android):
1. Buka aplikasi **Termux** di perangkat Android Anda.
2. Instal Node.js terlebih dahulu dengan perintah:
   ```bash
   pkg update && pkg install nodejs -y
   ```
3. Pindahkan file `api-websocket.js` ke direktori Termux Anda.
4. Jalankan server WebSocket menggunakan perintah:
   ```bash
   node api-websocket.js
   ```
5. Sesuaikan alamat IP/URL WebSocket di aplikasi Android Anda ke server lokal Termux Anda tersebut.

---

## 📁 Struktur Repositori
* `src/` & `res/` — Kode sumber dan aset UI aplikasi Android.
* `api-websocket.js` — Dokumen backend server berbasis WebSocket.
* `libs/` — *Library* pendukung Java-WebSocket dan SLF4J.

⭐ *Jika Anda menyukai proyek ini, jangan ragu untuk memberikan **Star** pada repositori ini!*
