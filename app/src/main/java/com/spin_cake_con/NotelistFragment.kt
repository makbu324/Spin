package com.spin_cake_con

import MainViewModel
import android.app.Activity
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.spin_cake_con.databinding.FragmentNotelistBinding


@Keep
class NotelistFragment: Fragment() {
    private var _binding: FragmentNotelistBinding? = null
    private val viewModel by activityViewModels<MainViewModel>()

    companion object {
        const val TAG = "NotelistFragment"
    }
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotelistBinding.inflate(inflater, container, false)

        binding.noteRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        binding.noteRecyclerView.adapter = NotelistAdapter(viewModel.album_to_view.list_of_prices, parentFragmentManager, viewModel)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tickSound: MediaPlayer = MediaPlayer.create(view.context, R.raw.ok_paper)
        val flipSound: MediaPlayer = MediaPlayer.create(view.context, R.raw.paper_flip)
        viewModel.setAllowBackButton(true)
        viewModel.setAllowHomeButton(false)
        view.findViewById<ImageButton>(R.id.add_button).setOnClickListener{
            if (viewModel.sound_effects_on)
                tickSound.start()
            viewModel.adding_new_note = true
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            fragmentTransaction.replace(R.id.nav_host, NoteFragment(), NoteFragment.TAG)
            fragmentTransaction.addToBackStack(NoteFragment.TAG)
            fragmentTransaction.commit()
        }
        val imm = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)

        if (viewModel.sound_effects_on)
            flipSound.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}