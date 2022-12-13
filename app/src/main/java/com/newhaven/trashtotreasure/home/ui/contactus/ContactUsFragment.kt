package com.newhaven.trashtotreasure.home.ui.contactus

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.newhaven.trashtotreasure.R
import com.newhaven.trashtotreasure.databinding.FragmentChatBinding
import com.newhaven.trashtotreasure.home.Constants
import java.text.SimpleDateFormat
import java.util.*

class ContactUsFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    var user = auth.currentUser
    private val fireStoreInstance = FirebaseFirestore.getInstance()
    private var collectionType: String? = null
    private val contactUsMessages = ArrayList<ContactUs>()
    private var chatRegistration: ListenerRegistration? = null
    var eventId: String? = ""
    var myuuid: String? = null
    private var recieveruuid: String? = null
    private var startdate: String? = null
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        eventId = arguments?.getString("eid")
        recieveruuid = arguments?.getString("uid")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
        menu.findItem(R.id.menu_main_setting).isVisible = false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initList()
        setViewListeners()
    }

    private fun setChannel(user: String, eid: String): String {
        return eid + user
    }

    private fun setViewListeners() {
        binding.buttonSend.setOnClickListener {
            collectionType?.let { it1 -> sendMessage(it1) }
        }
    }

    private fun initList() {
        val sh = activity?.getSharedPreferences("MySharedPref", AppCompatActivity.MODE_PRIVATE)
        myuuid = sh?.getString("uuid", "")
        binding.listChat.layoutManager = LinearLayoutManager(requireContext())
        val adapter = recieveruuid
            ?.let { ContactUsAdapter(contactUsMessages, it, "admin") }
        binding.listChat.adapter = adapter
        binding.listChat.scrollToPosition(contactUsMessages.size - 1)
        collectionType = eventId?.let { recieveruuid?.let { it1 -> setChannel(it, it1) } }
        collectionType?.let { listenForMessages(it) }
    }

    private fun listenForMessages(collectionType: String) {
        chatRegistration =
            fireStoreInstance.collection("contactus").document(collectionType).collection("messages")
                .orderBy(
                    "timestamp"
                )
                .addSnapshotListener { messageSnapshot, exception ->
                    if (messageSnapshot == null || messageSnapshot.isEmpty) {
                        return@addSnapshotListener
                    }
                    contactUsMessages.clear()
                    for (messageDocument in messageSnapshot.documents) {
                        contactUsMessages.add(
                            ContactUs(
                                messageDocument["text"] as? String,
                                messageDocument["user"] as? String,
                                messageDocument["reciever"] as? String,
                                messageDocument["timestamp"] as? Date,
                                messageDocument["username"] as? String,
                                messageDocument["time"] as? String,
                                messageDocument["sentDate"] as? String,
                                messageDocument["isAdmin"] as? Boolean
                            )
                        )
                    }
                    binding.listChat.adapter?.notifyDataSetChanged()
                    binding.listChat.scrollToPosition(contactUsMessages.size - 1)
                }
    }
    private fun sendMessage(collectionType: String) {
        val message = binding.edittextChat.text.toString()
        binding.edittextChat.setText("")
        val sh = activity?.getSharedPreferences("MySharedPref", AppCompatActivity.MODE_PRIVATE)
        val isAdmin = sh?.getBoolean("isAdmin",false)
     binding.edittextChat.hideKeyboard()
        val currentTime: String =
            SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        if (message.isNotEmpty() || message != "") {
            fireStoreInstance.collection("contactus").document(collectionType).collection("messages")
                .add(
                    mapOf(
                        Pair(Constants.TEXT, message.trim()),
                        Pair(Constants.USER, user?.uid),
                        Pair(Constants.RECIEVER, eventId),
                        Pair(Constants.TIMESTAMP, Timestamp.now()),
                        Pair(Constants.USERNAME, user?.displayName),
                        Pair(Constants.TIME, currentTime),
                        Pair(Constants.SENTDATE, startdate),
                        Pair(Constants.ISADMIN,isAdmin)
                    )
                ).addOnSuccessListener {
                Log.d("message","inserted")
                }
        }
    }

    private fun View.hideKeyboard() {
        val imm =
            context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}