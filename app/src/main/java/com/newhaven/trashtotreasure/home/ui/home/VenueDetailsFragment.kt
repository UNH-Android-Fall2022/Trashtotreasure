package com.newhaven.trashtotreasure.home.ui.home

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ProgressBar
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.newhaven.trashtotreasure.R
import com.newhaven.trashtotreasure.databinding.FragmentVenueDetailsBinding
import com.newhaven.trashtotreasure.home.Constants
import com.newhaven.trashtotreasure.home.TrashToTreasure

class VenueDetailsFragment : Fragment() {

    private var _binding : FragmentVenueDetailsBinding ? = null
    private val binding get() = _binding!!
    private var flat = ""
    private var land = ""
    private var city = ""
    private var country = ""
    private var pin = ""
    private var db : FirebaseFirestore? =null
    val user = Firebase.auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        flat = arguments?.getString("flat").toString()
        land = arguments?.getString("land").toString()
        city = arguments?.getString("city").toString()
        country = arguments?.getString("country").toString()
        pin = arguments?.getString("postal").toString()
        db  = FirebaseFirestore.getInstance()

        _binding = FragmentVenueDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.tvAddress.text = "Address : $flat,$land,$city,\n$country,$pin"
        binding.venueSubmit.setOnClickListener {
            submitEventInfo(addressDetails = AddressDetails(flat,land,city,country,pin),binding.etName.text.toString(),binding.etContact.text.toString())
        }

     return root
    }


    private fun submitEventInfo(addressDetails: AddressDetails,name: String,contact: String) {
        val eventDetails = hashMapOf(
            "eid" to "EV${idGenerator()}",
            "uid" to user?.uid,
            "name" to name,
            "contact" to contact,
            "address" to addressDetails.toString(),
            "isApproved" to false
        )
        binding.progressCircular.visibility = View.VISIBLE
        db?.collection(Constants.EVENTDETAILS)?.document(eventDetails["eid"].toString())?.set(eventDetails)?.addOnSuccessListener {
            binding.progressCircular.visibility = View.GONE
            showDialog(addressDetails,name,contact)
            Log.d(Constants.EVENTDETAILS , "Saved Successfully")
        //    findNavController().navigate(R.id.action_venueDetailsFragment_to_navigation_dashboard)
        }?.addOnFailureListener {
            binding.progressCircular.visibility = View.GONE
            Log.d(Constants.EVENTDETAILS , "Failed to insert data")
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
      //  viewModel = ViewModelProvider(this).get(VenueDetailsViewModel::class.java)

    }

    private fun showDialog(addresses: AddressDetails, name: String, contact: String) {
        val dialog = Dialog(activity as TrashToTreasure)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_confirm);
        dialog.findViewById<Button>(R.id.submit).setOnClickListener {
            findNavController().navigate(R.id.action_venueDetailsFragment_to_navigation_dashboard)
            dialog.dismiss()
        }
        dialog.show();
    }

    private fun idGenerator() =(100000..999999).random()
}