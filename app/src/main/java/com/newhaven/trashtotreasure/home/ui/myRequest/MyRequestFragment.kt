package com.newhaven.trashtotreasure.home.ui.myRequest

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.newhaven.trashtotreasure.R
import com.newhaven.trashtotreasure.databinding.FragmentDashboardBinding
import com.newhaven.trashtotreasure.home.Constants


class MyRequestFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    var eventList: ArrayList<Event> = ArrayList()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RequestListAdapter


    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        getRequestList()
        return root
    }

    private fun getRequestList() {
        binding.progressCircular.visibility = View.VISIBLE
        FirebaseFirestore.getInstance().collection(Constants.EVENTDETAILS).get()
            .addOnSuccessListener {
                for (documents in it.documents) {
                    if (documents.data?.get("uid").toString() == FirebaseAuth.getInstance().currentUser?.uid.toString()) {
                        if (documents != null) {
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
                }
                binding.progressCircular.visibility = View.GONE
                linearLayoutManager = LinearLayoutManager(requireContext())
                binding.rvEvents.layoutManager = linearLayoutManager
                adapter = RequestListAdapter(requireContext(),eventList)
                binding.rvEvents.adapter = adapter
            }.addOnFailureListener {
                binding.progressCircular.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
            }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu,menu)
        menu.findItem(R.id.menu_main_setting).isVisible = false
    }

}

