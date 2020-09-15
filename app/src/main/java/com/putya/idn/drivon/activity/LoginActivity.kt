package com.putya.idn.drivon.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.putya.idn.drivon.R
import com.putya.idn.drivon.model.User
import com.putya.idn.drivon.utils.Constant
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {
    var googleSignInClient: GoogleSignInClient? = null
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        auth = FirebaseAuth.getInstance()

        btn_google_sign.onClick {
            signIn()
        }
        sign_link.onClick {
            if (et_user_email_signin.text.isNotEmpty() &&
                et_password_signin.text.isNotEmpty()
            ) {
                authUserSignIn(
                    et_user_email_signin.text.toString(),
                    et_password_signin.text.toString()
                )
            }
        }
    }

    private fun authUserSignIn(email: String, password: String) {
        var status: Boolean? = null

        auth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity<MainActivity>()
                    finish()
                } else {
                    toast("Login Failed")
                    Log.e("Error", "Message")
                }
            }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, 4)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 4) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {

            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        var uid = String()
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth?.currentUser
                    checkDatabase(task.result?.user?.uid, account)
                    uid = user?.uid.toString()
                } else {

                }
            }
    }

    private fun checkDatabase(uid: String?, account: GoogleSignInAccount?) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(Constant.tb_user)
        val query = myRef.orderByChild("UID").equalTo(auth?.uid)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    startActivity<MainActivity>()
                } else {
                    account?.displayName?.let {
                        account.email?.let { it1 ->
                            insertUser(it, it1, " ", uid)
                        }
                    }
                }
            }
        })
    }

    private fun insertUser(
        name: String,
        email: String,
        phoneNumber: String,
        IDuser: String?
    ): Boolean {
        val user = User()
        user.email = email
        user.name = name
        user.phoneNumber = phoneNumber
        user.uid = auth?.uid

        val database = FirebaseDatabase.getInstance()
        val key = database.reference.push().key
        val myRef = database.getReference(Constant.tb_user)

        myRef.child(key ?: " ").setValue(user)

        startActivity<AuthenticationActivity>(Constant.key to key)

        return true
    }
}
