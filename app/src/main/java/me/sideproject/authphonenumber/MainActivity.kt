package me.sideproject.authphonenumber

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toast.*
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    var tokenF = ""
    var vid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        var msg = ""
      val   callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
          override fun onCodeAutoRetrievalTimeOut(p0: String) {
              super.onCodeAutoRetrievalTimeOut(p0)
          Log.d("Authphone" , " $p0")
          }

          override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                textView.textSize = 18F
                textView.text = "Verified"
                Log.d("Authphone", "onVerificationCompleted:$credential")
              msg = credential.smsCode.toString()
Log.d("Code" , msg)
              if (!credential.smsCode.isNullOrEmpty()){
                  val cred = PhoneAuthProvider.getCredential(vid, credential.smsCode.toString().trim())
                  signInWithPhoneAuthCredential(cred)
              }
            }
            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("Authphone", "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    Log.d("Authphone" , "InValid")
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Log.d("Authphone" , "InValidMainyRequests")

                }

                // Show a message and update the UI
                // ...
                Toast.makeText(applicationContext , "Hiiiii" , LENGTH_SHORT).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                msg = verificationId

                Log.d("Authphone", "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                Log.d("Authphone" , "On codeSent $token")
vid=verificationId

// ...


            }
        }
        fab.setOnClickListener { view ->
            val  phoneNumber = editText.text.toString()
       val auth =      PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                this, // Activity (for callback binding)
                callbacks) // OnVerificationStateChangedCallbacks

            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }



        button.setOnClickListener {

            val credenti =                  PhoneAuthProvider.getCredential(vid ,editText2.text.toString().trim() )
            signInWithPhoneAuthCredential(credenti)
            Log.d("Code" , "${editText2.text.toString()}")

        }
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Log.d("Authphone" , "Code :${credential.toString()}")

      val auth :FirebaseAuth = FirebaseAuth.getInstance()
                   auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Code" , "${credential}")
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Authophone", "signInWithCredential:success")
textView.textSize =25F
                    textView.text = "Verrrrified"
                    val usFirebaseAer = task.result?.user
                    // ...
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("Authphone", "signInWithCredential:failure", task.exception)

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Log.d("Authophone", "signInWithCredential:success ${task.exception}")
                        textView.textSize =25F
                        textView.text = "NOT Verrrrified"

                    }
                }
            }
    }


}
