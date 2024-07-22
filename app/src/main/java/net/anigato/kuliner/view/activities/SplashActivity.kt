package net.anigato.kuliner.view.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import net.anigato.kuliner.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // Pastikan ada layout activity_splash

        // Mendapatkan kota dari intent sebelumnya
        val strCity = intent.getStringExtra("strCity")

        // Menampilkan splash screen selama 2 detik
        Handler(Looper.getMainLooper()).postDelayed({
            // Navigasi ke FoodsActivity setelah penundaan
            val intent = Intent(this, FoodsActivity::class.java)
            intent.putExtra("strCity", strCity)
            startActivity(intent)
            finish()
        }, 1000) // 2000 milidetik = 2 detik
    }
}
