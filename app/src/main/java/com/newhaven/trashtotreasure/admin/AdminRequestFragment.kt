package com.newhaven.trashtotreasure.admin

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.newhaven.trashtotreasure.AdminActivity
import com.newhaven.trashtotreasure.R
import com.newhaven.trashtotreasure.databinding.FragmentAdminRequestBinding
import com.newhaven.trashtotreasure.home.Constants
import com.newhaven.trashtotreasure.home.TrashToTreasure
import com.newhaven.trashtotreasure.home.ui.myRequest.Event
import com.newhaven.trashtotreasure.home.ui.myRequest.OnContactUsClick

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AdminRequestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminRequestFragment : Fragment(),OnContactUsClick {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentAdminRequestBinding? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: AdminRequestListAdapter

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminRequestBinding.inflate(inflater, container, false)
        val root: View = binding.root
        getRequestList()
        return root
    }

    private fun getRequestList() {
        binding.progressCircular.visibility = View.VISIBLE
        val eventList: ArrayList<Event> = ArrayList()
        FirebaseFirestore.getInstance().collection(Constants.EVENTDETAILS).get()
            .addOnSuccessListener {
                for (documents in it.documents) {
                        if (documents != null) {
                            Log.d("documentId",documents.id)
                            eventList.add(
                                Event(
                                    documents.data?.get("eid").toString(),
                                    documents.data?.get("uid").toString(),
                                    documents.data?.get("name").toString(),
                                    documents.data?.get("contact").toString(),
                                    documents.data?.get("address").toString(),
                                    documents.data?.get("isApproved") as Boolean
                                )
                            )
                        }
                }
                binding.progressCircular.visibility = View.GONE
                linearLayoutManager = LinearLayoutManager(requireContext())
                binding.rvEventsAdmin.layoutManager = linearLayoutManager
                adapter = AdminRequestListAdapter(requireContext(),eventList,this)
                binding.rvEventsAdmin.adapter = adapter
            }.addOnFailureListener {
                binding.progressCircular.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdminRequestFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminRequestFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(eid: String) {
        val bundle = bundleOf( "eid" to eid)
        findNavController().navigate(R.id.action_navigation_request_to_chatFragment2,bundle)
    }

    override fun onApproveClick(eid:String, event: HashMap<String, Any>) {
        binding.progressCircular.visibility = View.VISIBLE
        FirebaseFirestore.getInstance().collection(Constants.EVENTDETAILS).document(eid).update(
            event as Map<String, Any>
        ).addOnSuccessListener {
            binding.progressCircular.visibility = View.GONE
            Toast.makeText(context, "Request is approved please refresh page.", Toast.LENGTH_SHORT).show()
            Log.d("update","updated")
        }.addOnFailureListener {
            binding.progressCircular.visibility = View.GONE
            Toast.makeText(context, "update failed.", Toast.LENGTH_SHORT).show()
            Log.d("update","update failed")
        }
    }

    override fun onDriverClick(eid: String) {
        showDriverDialog(eid)
    }

    private fun showDriverDialog(eid: String) {

        val dialog = Dialog(activity as AdminActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_driver);
        val etName = dialog.findViewById<EditText>(R.id.etDriver)
        val etPhone = dialog.findViewById<EditText>(R.id.et_mobile)
        val progressCircular = dialog.findViewById<ProgressBar>(R.id.progress_circular)
        dialog.findViewById<Button>(R.id.submit).setOnClickListener {
            progressCircular.visibility = View.VISIBLE
            val driverDetails = hashMapOf(
                "eid" to eid,
                "mobile" to etPhone.text.toString(),
                "drivername" to etName.text.toString()
            )
            progressCircular.visibility = View.VISIBLE

            FirebaseFirestore.getInstance().collection("driverDetails").document().set(driverDetails)
                .addOnSuccessListener {
                    Log.d("driverinfo" , "Saved Successfully")
                    progressCircular.visibility = View.GONE
                    dialog.dismiss()
        //                findNavController().navigate(R.id.action_venueDetailsFragment_to_navigation_dashboard)
                }.addOnFailureListener {
                progressCircular.visibility = View.GONE
                Log.d("driverinfo" , "Failed to insert data")
                dialog.dismiss()
            }
        }
        dialog.show();

        dialog.setCancelable(true)
    }

}