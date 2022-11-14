package com.newhaven.trashtotreasure.home.ui.profile

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.newhaven.trashtotreasure.R
import com.newhaven.trashtotreasure.databinding.FragmentNotificationsBinding
import com.newhaven.trashtotreasure.home.TrashToTreasure

class ProfileFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

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


        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
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

        val etFlatNo = dialog.findViewById<EditText>(R.id.etName)
        val etCity = dialog.findViewById<EditText>(R.id.etPhone)
        val etCountry = dialog.findViewById<EditText>(R.id.et_mobile)
        val etPostalCode = dialog.findViewById<EditText>(R.id.etHome)
        val etLandMark = dialog.findViewById<EditText>(R.id.etEmail)
        dialog.findViewById<Button>(R.id.submit).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show();


    }

}