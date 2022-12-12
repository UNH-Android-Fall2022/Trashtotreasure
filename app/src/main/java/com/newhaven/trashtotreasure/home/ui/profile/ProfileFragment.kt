package com.newhaven.trashtotreasure.home.ui.profile

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.newhaven.trashtotreasure.R
import com.newhaven.trashtotreasure.databinding.FragmentProfileBinding
import com.newhaven.trashtotreasure.home.Constants
import com.newhaven.trashtotreasure.login.LoginActivity
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private var db: FirebaseFirestore? = null
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var uid: String
    private var profile: Profile? = null
    private var profilePath: String = ""

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
        db = FirebaseFirestore.getInstance()
        getData()
        profile.let {
            if (it != null) {
                setData(it)
            } else {
                setData()
            }
        }
        return root
    }

    private fun getData(): Profile? {
        binding.progressCircular.visibility = View.VISIBLE
        FirebaseFirestore.getInstance().collection(Constants.PROFILEDETAILS).get()
            .addOnSuccessListener {
                binding.progressCircular.visibility = View.GONE
                for (documents in it.documents) {
                    if (documents.data?.get("uid")
                            .toString() == FirebaseAuth.getInstance().currentUser?.uid.toString()
                    ) {
                        if (documents != null) {
                            profile = Profile(
                                documents.data?.get("name").toString(),
                                documents.data?.get("email").toString(),
                                documents.data?.get("mobile").toString(),
                                documents.data?.get("landline").toString(),
                                documents.data?.get("address").toString(),
                                documents.data?.get("profileimg").toString()
                            )
                            profile?.let { setData(it) }
                        }
                    }
                }
            }
            .addOnFailureListener {
                binding.progressCircular.visibility = View.GONE
            }
        return profile
    }

    private fun setData(profilee: Profile) {
        val user = Firebase.auth.currentUser

        user?.let {
            // Name, email address, and profile photo Url
            uid = user.uid
            name = user.displayName.toString()
            email = user.email.toString()
            binding.tvName.text = name
            binding.tvEmail.text = email
            binding.tvAddress.text = profilee.address
            binding.tvMobile.text = profilee.contact
            binding.tvHome.text = profilee.landline
        }
        if (profilee.profilePath!="") {
            FirebaseStorageUtil.pathToReference(profilee.profilePath).downloadUrl.addOnSuccessListener {
                Glide.with(this).load(it.toString()).into(binding.profileImage)
            }.addOnFailureListener {

            }
        }

        binding.btnSignout.setOnClickListener {
            val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
                "MySharedPref",
                Context.MODE_PRIVATE
            )
            val myEdit = sharedPreferences.edit()
            myEdit.putBoolean("isLogin", false)
            myEdit.putString("uuid", "")
            myEdit.apply()
            activity?.finish()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.uploadPic.setOnClickListener {
            val camera_intent =
                Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(camera_intent, Companion.pic_id)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Match the request 'pic id with requestCode
        if(resultCode != RESULT_CANCELED) {
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
                FirebaseStorageUtil.uploadProfilePhoto(selectedImageBytes) { imagePath ->
                    profilePath = imagePath
                    FirebaseFirestore.getInstance().collection(Constants.PROFILEDETAILS).document(FirebaseAuth.getInstance().currentUser?.uid.toString()).update("profileimg",profilePath).addOnSuccessListener {
                        binding.progressCircular.visibility = View.GONE
                        Toast.makeText(requireContext(), "Profile photo uploaded.", Toast.LENGTH_SHORT)
                            .show()
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Profile photo upload failed.", Toast.LENGTH_SHORT)
                            .show()
                    }

                    FirebaseStorageUtil.pathToReference(imagePath).downloadUrl.addOnSuccessListener {
                        Glide.with(this).load(it.toString()).into(binding.profileImage)
                    }.addOnFailureListener {

                    }

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


    private fun setData() {
        val user = Firebase.auth.currentUser
        user?.let {
            // Name, email address, and profile photo Url
            uid = user.uid
            name = user.displayName.toString()
            email = user.email.toString()
            binding.tvName.text = name
            binding.tvEmail.text = email
            binding.tvAddress.text = "-"
            binding.tvMobile.text = "-"
            binding.tvHome.text = "-"
        }
        binding.btnSignout.setOnClickListener {
            val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
                "MySharedPref",
                Context.MODE_PRIVATE
            )
            val myEdit = sharedPreferences.edit()
            myEdit.putBoolean("isLogin", false)
            myEdit.putString("uuid", "")
            myEdit.apply()
            activity?.finish()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
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
            activity?.let { showDialog(it) }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showDialog(activity: Activity) {

        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_profile);

        val etPhone = dialog.findViewById<EditText>(R.id.etPhone)
        val etMobile = dialog.findViewById<EditText>(R.id.et_mobile)
        val etHome = dialog.findViewById<EditText>(R.id.etHome)
        val progressCircular = dialog.findViewById<ProgressBar>(R.id.progress_circular)
        dialog.findViewById<Button>(R.id.submit).setOnClickListener {
            val profileDetails = hashMapOf(
                "uid" to uid,
                "name" to name,
                "mobile" to etPhone.text.toString(),
                "landline" to etMobile.text.toString(),
                "address" to etHome.text.toString(),
                "email" to email,
                "profileimg" to profilePath
            )
            progressCircular.visibility = View.VISIBLE

            db?.collection(Constants.PROFILEDETAILS)?.document(uid)?.set(profileDetails)
                ?.addOnSuccessListener {
                    Log.d(Constants.EVENTDETAILS, "Saved Successfully")
                    progressCircular.visibility = View.GONE
                    val data = getData()
                    if (data != null) {
                        setData(data)
                    }
                    dialog.dismiss()
                }?.addOnFailureListener {
                progressCircular.visibility = View.GONE
                Log.d(Constants.EVENTDETAILS, "Failed to insert data")
                dialog.dismiss()
            }
        }
        dialog.show();

        dialog.setCancelable(true)
    }

    companion object {
        private const val pic_id = 1
    }


}