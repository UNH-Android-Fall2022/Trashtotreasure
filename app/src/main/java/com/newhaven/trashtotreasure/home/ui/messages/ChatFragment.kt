package com.newhaven.trashtotreasure.home.ui.messages

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.newhaven.trashtotreasure.R
import com.newhaven.trashtotreasure.databinding.FragmentChatBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatFragment : Fragment() {

    val auth = FirebaseAuth.getInstance()
    var user = auth.currentUser
    val count: Int? = null
    val firestore = FirebaseFirestore.getInstance()
    var collectionType: String? = null
    val chatMessages = ArrayList<ChatMessage>()
    var chatRegistration: ListenerRegistration? = null
    var chatRegistration1: ListenerRegistration? = null
    var username: String? = null
    var currentuser: String? = null
    var uuid: String? = "user"
    var myuuid : String? = null
    var isInserted: Boolean = false
    var FirebaseToken: String? = null
    lateinit var mContext: Activity
    private var startdate : String? = null

    private var _binding: FragmentChatBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

      uuid = arguments?.getString("eid")
      //  username = arguments?.getString("username")
      //  currentuser = arguments?.getString("currentuser")


     //   val bundle = bundleOf("uid" to uuid)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
        menu.findItem(R.id.menu_main_setting).isVisible = false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        checkUser()
        if (uuid != null) {
            if (uuid != "") {
             //   getRegisteredToken(uuid!!)
            }
        }

        initList()
        setViewListeners()
    }

    private fun setOneToOneChat(uid1: String, uid2: String): String? {
//Check if user1’s id is less than user2's
        return if (uid1 < uid2) {
            uid1 + uid2
        } else {
            uid2 + uid1
        }
    }




    private fun setViewListeners() {
        binding.buttonSend.setOnClickListener {
            collectionType?.let { it1 -> sendChatMessage(it1) }
        }
    }

    private fun initList() {

        val sh = activity?.getSharedPreferences("MySharedPref", AppCompatActivity.MODE_PRIVATE)
        myuuid =   sh?.getString("uuid", "")

        binding.listChat.layoutManager = LinearLayoutManager(requireContext())
        val adapter = uuid
            ?.let { ChatAdapter(chatMessages, it, "admin", false, null) }
        binding.listChat.adapter = adapter
        binding.listChat.scrollToPosition(chatMessages.size - 1)



        collectionType = uuid?.let { myuuid?.let { it1 -> setOneToOneChat(it, it1) } }
        collectionType?.let { listenForChatMessages(it) }
    }
    private fun listenForChatMessages(collectionType: String) {
        chatRegistration =
            firestore.collection("chatChannel").document(collectionType).collection("messages")
                .orderBy(
                    "timestamp"
                )
                .addSnapshotListener { messageSnapshot, exception ->
                 //   NetworkUtils.isInserted = false
                    //is chat's empty
                    if (messageSnapshot == null || messageSnapshot.isEmpty) {
                     //   showPopUPGroupMessage()
                        return@addSnapshotListener
                    }
                    chatMessages.clear()


                    for (messageDocument in messageSnapshot.documents) {
                        chatMessages.add(
                            ChatMessage(
                                messageDocument["text"] as? String,
                                messageDocument["user"] as? String,
                                messageDocument["reciever"] as? String,
                                messageDocument["timestamp"] as? Date,
                                messageDocument["username"] as? String,
                                messageDocument["time"] as? String,
                                messageDocument["sentDate"] as? String
                            )
                        )
                    }
                    binding.listChat.adapter?.notifyDataSetChanged()
                    binding.listChat.scrollToPosition(chatMessages.size - 1)
                    Log.d("messages", chatMessages.toString())

                }
    }

    private fun checkUser() {
        chatRegistration1 = firestore.collection("user")
            .addSnapshotListener { messageSnapshot, exception ->

                if (messageSnapshot == null || messageSnapshot.isEmpty)
                    return@addSnapshotListener

                for (messageDocument in messageSnapshot.documents) {
                    if ((messageDocument["uid"] as? String).equals(user?.uid)) {
                        username = messageDocument["name"] as? String
                        Log.d("username", username + "")
                    }
                }
            }
    }

    private fun sendChatMessage(collectionType: String) {
        val message = binding.edittextChat.text.toString()
        binding.edittextChat.setText("")
//        binding.edittextChat.hideKeyboard()
        val currentTime: String =
            SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        Log.d("time", currentTime)


        if (message.isNotEmpty() || message != "") {
            firestore.collection("chatChannel").document(collectionType).collection("messages")
                .add(
                    mapOf(
                        Pair("text", message.trim()),
                        Pair("user", user?.uid),
                        Pair("reciever", uuid),
                        Pair("timestamp", Timestamp.now()),
                        Pair("username", user?.displayName),
                        Pair("time", currentTime),
                        Pair("sentDate",startdate)
                    )
                ).addOnSuccessListener {

                    firestore.collection("chatChannel").document(collectionType)
                        .collection("messages").document(
                            it.id
                        ).get().addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot != null) {
                                if (documentSnapshot["reciever"] as? String != uuid
                                ) {
                                  //  NetworkUtils.isInserted = true

//                                    if (FirebaseToken != null) {
//
//                                        viewModel.sendNotificationToPatner(
//                                            FirebaseToken!!,
//                                            "Chat Notification",
//                                            "You have received a message from"+" " + arguments?.getString("username")!!,
//                                            arguments?.getString("username")!!,
//                                            message.trim()
//                                        )
//                                    }

                                }
                            }
                        }
                    it.id

                }
        }
    }
}