const uWS = require('uWebSockets.js');

// Gunakan TextDecoder & Encoder untuk konversi biner ke string (Sangat hemat memori)
const decoder = new TextDecoder('utf-8');
const encoder = new TextEncoder();

// Inisialisasi Aplikasi uWS
uWS.App().ws('/*', {
  // --- OPTIMALISASI MEMORI & KEAMANAN ---
  compression: uWS.SHARED_COMPRESSOR, // Kompresi data untuk hemat bandwidth
  maxPayloadLength: 64 * 1024,        // Batasi maks pesan masuk 64KB (Cukup untuk chat/data teks)
  idleTimeout: 60,                    // Putus otomatis jika client tidak kirim data selama 60 detik

  // --- EVENT HANDLERS ---
  
  // 1. Saat Client Konek
  open: (ws) => {
    console.log('Client terhubung ke server');
    
    // Gabungkan client ke ruangan global (fitur bawaan uWS, tanpa library tambahan)
    ws.subscribe('ruang-utama');
    
    // Kirim pesan selamat datang ke client ini saja
    ws.send(encoder.encode(JSON.stringify({ event: 'info', message: 'Selamat datang di server uWS!' })));
  },

  // 2. Saat Menerima Pesan dari Client
  message: (ws, message, isBinary) => {
    try {
      // uWS menerima pesan dalam bentuk ArrayBuffer (Biner). Ubah ke String.
      const messageString = decoder.decode(message);
      const parsedData = JSON.parse(messageString);

      console.log('Pesan masuk:', parsedData);

      // Contoh Broadcast: Kirim ke SEMUA client yang subscribe 'ruang-utama'
      // Kodenya sangat efisien, uWS menangani broadcast di level C++
      ws.publish('ruang-utama', encoder.encode(JSON.stringify({
        event: 'broadcast',
        sender: 'User',
        text: parsedData.text || ''
      })), isBinary);

    } catch (error) {
      // Jika format pesan bukan JSON, kirim error ke pengirim
      ws.send(encoder.encode(JSON.stringify({ error: 'Format pesan harus JSON yang valid' })));
    }
  },

  // 3. Saat Client Putus Koneksi
  close: (ws, code, message) => {
    console.log('Client terputus. Kode:', code);
    // uWS otomatis menghapus client dari semua subscribe, jadi memori langsung bersih!
  }

}).listen(8080, (listenSocket) => {
  if (listenSocket) {
    console.log('Server uWS Berjalan Lancar di Port 8080');
    console.log('RAM Aman, Siap Menampung Ribuan User!');
  } else {
    console.log('Gagal menjalankan server di port 8080');
  }
});