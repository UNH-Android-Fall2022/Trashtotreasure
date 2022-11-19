package com.newhaven.trashtotreasure.home.ui.profile

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.newhaven.trashtotreasure.R
import com.newhaven.trashtotreasure.databinding.FragmentProfileBinding
import com.newhaven.trashtotreasure.home.Constants
import com.newhaven.trashtotreasure.home.TrashToTreasure

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private var db : FirebaseFirestore? =null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }





    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        db  = FirebaseFirestore.getInstance()
        setData()
        return root
    }

    private fun setData() {
        val user = Firebase.auth.currentUser
        user?.let {
            // Name, email address, and profile photo Url
            val name = user.displayName
            val email = user.email
            binding.tvName.text = name
            binding.tvEmail.text = email
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
        menu.findItem(R.id.menu_main_setting).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if (id == R.id.menu_main_setting) {
            showDialog()
        }
        return super.onOptionsItemSelected(item)


    }



    private fun showDialog() {

        val dialog = Dialog(activity as TrashToTreasure)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_profile);

        val etName = dialog.findViewById<EditText>(R.id.etName)
        val etPhone = dialog.findViewById<EditText>(R.id.etPhone)
        val etMobile = dialog.findViewById<EditText>(R.id.et_mobile)
        val etHome = dialog.findViewById<EditText>(R.id.etHome)
        val etEmail = dialog.findViewById<EditText>(R.id.etEmail)
        dialog.findViewById<Button>(R.id.submit).setOnClickListener {
            val profileDetails = hashMapOf(
                "name" to etName.text.toString(),
                "phone" to etPhone.text.toString(),
                "mobile" to etMobile.text.toString(),
                "home" to etHome.text.toString(),
                "email" to etEmail.text.toString()
            )


            db?.collection(Constants.PROFILEDETAILS)?.document()?.set(profileDetails)?.addOnSuccessListener {
                Log.d(Constants.EVENTDETAILS , "Saved Successfully")
                dialog.dismiss()
//                findNavController().navigate(R.id.action_venueDetailsFragment_to_navigation_dashboard)
            }?.addOnFailureListener {
                Log.d(Constants.EVENTDETAILS , "Failed to insert data")
                dialog.dismiss()
            }
        }
        dialog.show();
    }


}