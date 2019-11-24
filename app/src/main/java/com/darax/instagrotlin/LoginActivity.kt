package com.darax.instagrotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class LoginActivity : AppCompatActivity() {
    //Limpiar la clase de firebase para autenticacion
    var auth: FirebaseAuth? = null
    var callbackManager: CallbackManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        email_button_edittext.setOnClickListener {
            entrarYregistrarse()
        }
        facebook_login_button.setOnClickListener {
            facebookLogin()
        }
        callbackManager = CallbackManager.Factory.create()
        //printHashKey()
    }
    //Esta funcion sirve para crear una HashKey de facebook, solo lo use una vez para obtener la clave
    //https://stackoverflow.com/questions/7506392/how-to-create-android-facebook-key-hash
    //Zu8gyXvmCPK/TBbTDrbmSBHxE+s=
    /*fun printHashKey() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i("TAG", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("TAG", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("TAG", "printHashKey()", e)
        }
    }*/

    //Funcion para inicar sesion o registrarse
    fun entrarYregistrarse() {
        auth?.createUserWithEmailAndPassword(
            email_edittext.text.toString(),
            password_edittext.text.toString()
        )?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //Creacion de una cuenta de usuario
                cambiarAlLayoutPrincipal(task.result?.user)

            } else if (task.exception?.message.isNullOrEmpty()) {
                //Mostrar mensaje de error
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            } else {
                //Acceder si se tiene la cuenta
                registrarseconEmail()
            }
        }
    }

    fun registrarseconEmail() {
        auth?.signInWithEmailAndPassword(
            email_edittext.text.toString(),
            password_edittext.text.toString()
        )?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //Acceder
                cambiarAlLayoutPrincipal(task.result?.user)

            } else {
                //Mostrar el mensaje de error
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun cambiarAlLayoutPrincipal(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }


    fun facebookLogin() {
        LoginManager.getInstance()
            .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    handleFacebookAccesToken(result?.accessToken)
                }

                override fun onCancel() {
                }

                override fun onError(error: FacebookException?) {
                }

            }
            )
    }

    fun handleFacebookAccesToken(token: AccessToken?) {
        var credential = FacebookAuthProvider.getCredential(token?.token!!)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Acceder
                    cambiarAlLayoutPrincipal(task.result?.user)
                } else {
                    //Mostrar el mensaje de error
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

}
