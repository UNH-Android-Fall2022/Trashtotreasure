package com.newhaven.trashtotreasure.home.ui.home

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.newhaven.trashtotreasure.R
import com.newhaven.trashtotreasure.databinding.FragmentVenueDetailsBinding
import com.newhaven.trashtotreasure.home.Constants
import com.newhaven.trashtotreasure.home.TrashToTreasure
import com.newhaven.trashtotreasure.home.ui.profile.ProfileFragment
import com.newhaven.trashtotreasure.home.ui.profile.ProfileUtil
import java.io.ByteArrayOutputStream

class VenueDetailsFragment : Fragment() {

    private var _binding : FragmentVenueDetailsBinding ? = null
    private val binding get() = _binding!!
    private var flat = ""
    private var land = ""
    private var city = ""
    private var country = ""
    private var pin = ""
    private var foodImg = ""
    private var db : FirebaseFirestore? =null
    val user = Firebase.auth.currentUser

    companion object{
        private const val pic_id = 12
    }

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
            if(binding.etName.text.isNotEmpty() && binding.etContact.text.isNotEmpty() && binding.etBehalf.text.isNotEmpty()) {
                submitEventInfo(
                    addressDetails = AddressDetails(flat, land, city, country, pin),
                    binding.etName.text.toString(),
                    binding.etContact.text.toString()
                )
            }else{
                Toast.makeText(requireContext(), "Please fill all fields.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.addPhotos.setOnClickListener {
            val camera_intent =
                Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(camera_intent, pic_id)
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
            "photoimg" to foodImg,
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Match the request 'pic id with requestCode
        if(resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == pic_id) {
                // BitMap is data structure of image file which store the image in memory
                val photo = data!!.extras!!["data"]
                // Set the image in imageview for display
                // click_image_id.setImageBitmap(photo)

                val selectedImagePath =
                    context?.let { getImageUri(it, data.extras!!["data"] as Bitmap) }


                val selectedImageBmp =
                    MediaStore.Images.Media.getBitmap(context?.contentResolver, selectedImagePath)

                val outputStream = ByteArrayOutputStream()

                selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                val selectedImageBytes = outputStream.toByteArray()
                binding.progressCircular.visibility = View.VISIBLE
                ProfileUtil.uploadFoodImage(selectedImageBytes) { imagePath ->
                    foodImg = imagePath
                    Toast.makeText(requireContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }


    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }
}