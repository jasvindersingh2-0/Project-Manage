package utils

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import com.example.projectmanage.activities.CreateBoardActivity


object Constants {
    const val USERS : String = "users"
    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2
    const val MOBILE : String = "mobile"
    const val IMAGE : String = "image"
    const val NAME : String = "name"
    const val BOARDS : String = "boards"
    fun showImageChooser(pickImageLauncher : ActivityResultLauncher<Intent>) {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(galleryIntent) // Use the Activity Result Launcher
    }
}