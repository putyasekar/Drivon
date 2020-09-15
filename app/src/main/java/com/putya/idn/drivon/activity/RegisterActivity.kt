package com.putya.idn.drivon.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.putya.idn.drivon.R
import com.putya.idn.drivon.model.User
import com.putya.idn.drivon.utils.Constant
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity

class RegisterActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        btn_signup.onClick {
            if (et_user_name_signup.text.isNotEmpty() &&
                et_user_email_signup.text.isNotEmpty() &&
                et_user_phone_signup.text.isNotEmpty() &&
                et_password_signup.text.isNotEmpty() &&
                et_confirm_password_signup.text.isNotEmpty()
            ) {
                authUserSignUp(
                    et_user_email_signup.text.toString(),
                    et_password_signup.text.toString()
                )
            }
        }
    }

    private fun authUserSignUp(email: String, password: String): Boolean? {
        auth = FirebaseAuth.getInstance()

        var status: Boolean? = null
        val TAG = "Tag"

        auth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (insertUser(
                            et_user_name_signup.text.toString(),
                            et_user_email_signup.text.toString(),
                            et_user_phone_signup.text.toString(),
                            task.result?.user!!
                        )
                    ) {
                        startActivity<LoginActivity>()
                    }
                } else {
                    status = false
                }
            }
        return status
    }

    private fun insertUser(
        name: String,
        email: String,
        phoneNumber: String,
        user: FirebaseUser
    ): Boolean {
        var user = User()
        user.uid = user.uid
        user.name = name
        user.email = email
        user.phoneNumber = phoneNumber

        val database = FirebaseDatabase.getInstance()
        var key = database.reference.push().key
        val myRef = database.getReference(Constant.tb_user)

        myRef.child(key!!).setValue(user)

        return true
    }
}