package com.putya.idn.drivon.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.google.firebase.database.FirebaseDatabase
import com.putya.idn.drivon.R
import com.putya.idn.drivon.utils.Constant
import kotlinx.android.synthetic.main.activity_authentication.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val key = intent.getStringExtra(Constant.key)
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(Constant.tb_user)

        autentikasiSubmit.onClick {
            if (autentikasiNomerHp.text.toString().isNotEmpty()) {
                myRef.child(key).child("Hp").setValue(autentikasiNomerHp.text.toString())
                startActivity<MainActivity>()
            } else toast("Can not be empty")
        }
    }
}