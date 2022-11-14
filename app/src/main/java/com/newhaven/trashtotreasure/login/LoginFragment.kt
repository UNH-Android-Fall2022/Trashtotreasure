package com.newhaven.trashtotreasure.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.newhaven.trashtotreasure.R
import com.newhaven.trashtotreasure.home.HomeActivity
import com.newhaven.trashtotreasure.home.TrashToTreasure


class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var btnLogin : Button
    private lateinit var already : TextView
    private lateinit var etEmail : EditText
    private lateinit var etPassword : EditText
    private lateinit var progressBar : ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        setUpView(view);
        // Inflate the layout for this fragment
        return view

    }
    
    

    private fun setUpView(view: View) {
        btnLogin = view.findViewById(R.id.btn_login)
        already = view.findViewById(R.id.tv_already_)
        etEmail = view.findViewById(R.id.et_user_name)
        etPassword = view.findViewById(R.id.et_password)
        progressBar = view.findViewById(R.id.progress_circular)
        already.setOnClickListener {
           findNavController().navigate(R.id.action_login_to_register)
        }
        btnLogin.setOnClickListener { 
            if(etEmail.text.isNotEmpty() && etPassword.text.isNotEmpty())
            signIn(etEmail.text.toString(),etPassword.text.toString())
            else
                Toast.makeText(context, "Please enter email and password.", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun signIn(email: String , password :String){
        progressBar.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    progressBar.visibility = View.GONE
                    try {
                        throw task.exception!!
                    } // if user enters wrong email.
                    catch (invalidEmail: FirebaseAuthInvalidUserException) {
                        Toast.makeText(context, "please enter valid email.", Toast.LENGTH_SHORT)
                            .show()
                    } // if user enters wrong password.
                    catch (wrongPassword: FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(context, "Please enter valid password.", Toast.LENGTH_SHORT)
                            .show()

                        // TODO: Take your action
                    } catch (e: Exception) {
                        // Log.d(TAG, "onComplete: " + e.message)
                    }
                }else{
                    progressBar.visibility = View.GONE
                    val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("MySharedPref",
                        Context.MODE_PRIVATE
                    )
                    val myEdit = sharedPreferences.edit()
                    myEdit.putBoolean("isLogin", true)
                    myEdit.apply()
                    Toast.makeText(context, "Signed in Successfully ", Toast.LENGTH_SHORT).show()
                    activity?.finish()
                  openHome()
                }
            }


    }

    private fun openHome(){
        val intent = Intent(activity,TrashToTreasure::class.java)
        startActivity(intent)
    }

}