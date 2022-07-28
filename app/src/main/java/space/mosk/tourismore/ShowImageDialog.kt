package space.mosk.tourismore

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import org.w3c.dom.Text

class ShowImageDialog(val url : String): DialogFragment() {
    private lateinit var img : ImageView
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;

            val dialogView = inflater.inflate(R.layout.dialog_image_layout, null)
            builder.setView(dialogView)
            img = dialogView.findViewById(R.id.showImg)
            Glide.with(dialogView).load(url).into(img)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}