package com.newhaven.trashtotreasure.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.SignInMethodQueryResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.newhaven.trashtotreasure.R
import com.newhaven.trashtotreasure.utils.NetworkUtils


class RegisterFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var btnSignUp: Button
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etReEnterPassword: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var already : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth
        auth = Firebase.auth
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        setupView(view)
        return  view
    }

    private fun setupView(view: View) {
    btnSignUp = view.findViewById(R.id.btn_login)
        etEmail = view.findViewById<EditText>(R.id.et_user_name)
        etPassword = view.findViewById<EditText>(R.id.et_password)
        etReEnterPassword = view.findViewById<EditText>(R.id.et_confirm_password)
        progressBar = view.findViewById(R.id.progress_circular)
        already = view.findViewById(R.id.tv_already_)
        btnSignUp.setOnClickListener {
            if (etEmail.text.isNotEmpty() && etPassword.text.isNotEmpty() && etReEnterPassword.text.isNotEmpty()) {
                if (NetworkUtils.isNetworkAvailable(context)) {
                    progressBar.visibility = View.VISIBLE
                    createAccount(etEmail.text.toString(), etPassword.text.toString())
                } else
                    Toast.makeText(context, "No Internet Connectivity", Toast.LENGTH_SHORT).show()
            }else
                Toast.makeText(context, "Please fill all details and try again.", Toast.LENGTH_SHORT).show()
        }
        already.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }
    }


    override fun onStart() {
        super.onStart()
            val currentUser = auth.currentUser
            if(currentUser != null){
               reload();
            }

    }

    private fun checkUserIsExistOrNot(email: String) : Boolean{
        var isNewUser  = false
        progressBar.visibility = View.VISIBLE
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                isNewUser = task.result.signInMethods!!.isEmpty()
                progressBar.visibility = View.GONE
            }
        return isNewUser
    }

    private fun createAccount(email: String, password: String) {
      //  if(checkUserIsExistOrNot(email)) {
            auth.createUserWithEmailAndPassword(email, password)

                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        Toast.makeText(context, "User signup successfully.", Toast.LENGTH_SHORT)
                            .show()
                        progressBar.visibility = View.GONE
                        findNavController().navigate(R.id.action_register_to_login)

                    } else {
                        progressBar.visibility = View.GONE

                        try {
                            throw task.exception!!
                        }catch (emailExist : FirebaseAuthUserCollisionException){
                            findNavController().navigate(R.id.action_register_to_login)
                            Toast.makeText(context, "User Already exist. please login", Toast.LENGTH_SHORT).show()
                        }


                      //  Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }

                }
      //  }else {


    }
    private fun reload() {

    }
}