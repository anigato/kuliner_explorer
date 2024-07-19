package net.anigato.kuliner.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mendapatkan kota dari intent sebelumnya
        val strCity = intent.getStringExtra("strCity")

        // Langsung navigasi ke FoodsActivity
        val intent = Intent(this, FoodsActivity::class.java)
        intent.putExtra("strCity", strCity)
        startActivity(intent)
        finish()
    }
}
