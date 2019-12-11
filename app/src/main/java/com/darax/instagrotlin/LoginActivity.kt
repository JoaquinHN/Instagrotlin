package com.darax.instagrotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider as FacebookAuthProvider1
import kotlin.collections.listOf as listOf1


class LoginActivity : AppCompatActivity() {
    //Limpiar la clase de firebase para autenticacion
    private var auth: FirebaseAuth? = null
    private var callbackManager: CallbackManager? = null
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
    ////////////////////////////////
    /*
    public override fun onStart() {
        super.onStart()
        moveMainPage(auth?.currentUser)
    }

    fun moveMainPage(user:FirebaseUser?){
        if(user != null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }*/

    //Funcion para inicar sesion o registrarse
    private fun entrarYregistrarse() {
        auth?.createUserWithEmailAndPassword(
            email_edittext.text.toString(),
            password_edittext.text.toString()
        )?.addOnCompleteListener { task ->
            when {
                task.isSuccessful -> //Creacion de una cuenta de usuario
                    cambiarAlLayoutPrincipal(task.result?.user)
                task.exception?.message.isNullOrEmpty() -> //Mostrar mensaje de error
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                else -> //Acceder si se tiene la cuenta
                    registrarseconEmail()
            }
        }
    }

    private fun registrarseconEmail() {
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

    private fun cambiarAlLayoutPrincipal(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }


    private fun facebookLogin() {
        LoginManager.getInstance()
            .logInWithReadPermissions(this, listOf1("public_profile", "email"))
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
        val credential = FacebookAuthProvider1.getCredential(token?.token!!)
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
