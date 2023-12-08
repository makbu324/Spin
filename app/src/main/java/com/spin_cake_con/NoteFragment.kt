package com.spin_cake_con

import MainViewModel
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar


@Keep
class NoteFragment : Fragment() {

    companion object {
        const val TAG = "NoteFragment"
    }

    private val viewModel by activityViewModels<MainViewModel>()
    lateinit var location_text: EditText
    lateinit var price_text: EditText
    lateinit var memo_text: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.write_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val trashSound: MediaPlayer = MediaPlayer.create(view.context, R.raw.garbage_noise)

        location_text = view.findViewById<EditText>(R.id.actual_location)
        price_text = view.findViewById<EditText>(R.id.actual_price)
        memo_text = view.findViewById<EditText>(R.id.memo)

        if (!viewModel.adding_new_note) {
            location_text.setText(viewModel.note_to_view.first)
            price_text.setText(viewModel.note_to_view.second.toString())
            memo_text.setText(viewModel.note_to_view.third)
        }

        val replaceIndex = viewModel.THE_WISHLIST.indexOf(viewModel.album_to_view)
        view.findViewById<ImageButton>(R.id.trash_button).setOnClickListener {
            //remove the note
            if (!viewModel.adding_new_note) {
                viewModel.album_to_view.list_of_prices.remove(viewModel.note_to_view)
                viewModel.THE_WISHLIST[replaceIndex] = viewModel.album_to_view
                parentFragmentManager.popBackStack()
            } else {
                Snackbar.make(view, "Return to previous page to cancel ADD.",Snackbar.LENGTH_LONG).show()
            }
        }

        view.findViewById<ImageButton>(R.id.ok_button).setOnClickListener {
            if (viewModel.adding_new_note) {
                try {
                    viewModel.album_to_view.list_of_prices.add(Triple(location_text.getText().toString(), price_text.getText().toString().toDouble(), memo_text.getText().toString()))
                    viewModel.THE_WISHLIST[replaceIndex] = viewModel.album_to_view
                    parentFragmentManager.popBackStack()
                    if (viewModel.sound_effects_on)
                        trashSound.start()
                } catch (e: Exception) {
                    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                    Snackbar.make(view, "Please enter a valid price in decimal!",Snackbar.LENGTH_LONG).show()
                }
            } else {
                try {
                    val replaceIndex_Note = viewModel.album_to_view.list_of_prices.indexOf(viewModel.note_to_view)
                    viewModel.album_to_view.list_of_prices[replaceIndex_Note] = Triple(location_text.getText().toString(), price_text.getText().toString().toDouble(), memo_text.getText().toString())
                    viewModel.THE_WISHLIST[replaceIndex] = viewModel.album_to_view
                    parentFragmentManager.popBackStack()
                    if (viewModel.sound_effects_on)
                        trashSound.start()
                } catch (e: Exception) {
                    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                    Snackbar.make(view, "Please enter a valid price in decimal!!",Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.adding_new_note = false
    }
}