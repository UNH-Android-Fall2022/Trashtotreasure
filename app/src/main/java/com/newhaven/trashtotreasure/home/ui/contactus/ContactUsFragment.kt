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
import java.text.SimpleDateFormat
import java.util.*

class ContactUsFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    var user = auth.currentUser
    private val fireStoreInstance = FirebaseFirestore.getInstance()
    private var collectionType: String? = null
    private val chatMessages = ArrayList<ContactUs>()
    private var chatRegistration: ListenerRegistration? = null
    var eventId: String? = "user"
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

    private fun setOneToOneChat(uid1: String, uid2: String): String {
        return uid2 + uid1
    }

    private fun setViewListeners() {
        binding.buttonSend.setOnClickListener {
            collectionType?.let { it1 -> sendChatMessage(it1) }
        }
    }

    private fun initList() {
        val sh = activity?.getSharedPreferences("MySharedPref", AppCompatActivity.MODE_PRIVATE)
        myuuid = sh?.getString("uuid", "")
        binding.listChat.layoutManager = LinearLayoutManager(requireContext())
        val isAdmin = sh?.getBoolean("isAdmin",false)
        val adapter = recieveruuid
            ?.let { ContactUsAdapter(chatMessages, it, "admin", false, null,requireContext()) }
        binding.listChat.adapter = adapter
        binding.listChat.scrollToPosition(chatMessages.size - 1)
        collectionType = eventId?.let { recieveruuid?.let { it1 -> setOneToOneChat(it, it1) } }
        collectionType?.let { listenForChatMessages(it) }
    }

    private fun listenForChatMessages(collectionType: String) {
        chatRegistration =
            fireStoreInstance.collection("contactus").document(collectionType).collection("messages")
                .orderBy(
                    "timestamp"
                )
                .addSnapshotListener { messageSnapshot, exception ->
                    if (messageSnapshot == null || messageSnapshot.isEmpty) {
                        return@addSnapshotListener
                    }
                    chatMessages.clear()
                    for (messageDocument in messageSnapshot.documents) {
                        chatMessages.add(
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
                    binding.listChat.scrollToPosition(chatMessages.size - 1)
                }
    }
    private fun sendChatMessage(collectionType: String) {
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
                        Pair("text", message.trim()),
                        Pair("user", user?.uid),
                        Pair("reciever", eventId),
                        Pair("timestamp", Timestamp.now()),
                        Pair("username", user?.displayName),
                        Pair("time", currentTime),
                        Pair("sentDate", startdate),
                        Pair("isAdmin",isAdmin)
                    )
                ).addOnSuccessListener {

                    fireStoreInstance.collection("contactus").document(collectionType)
                        .collection("messages").document(
                            it.id
                        ).get().addOnSuccessListener { documentSnapshot ->
                            Log.d("Message","Inserted")
                        }
                    it.id

                }
        }
    }

    private fun View.hideKeyboard() {
        val imm =
            context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}