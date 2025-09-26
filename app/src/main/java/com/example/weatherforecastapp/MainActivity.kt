package com.example.weatherforecastapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Lấy NavController, không cần setupActionBar vì đã bỏ Toolbar
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Nếu muốn lắng nghe chuyển destination, có thể thêm code ở đây
        navController.addOnDestinationChangedListener { _, _, _ ->
            // Xử lý khi điều hướng, nếu cần
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
                .navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
