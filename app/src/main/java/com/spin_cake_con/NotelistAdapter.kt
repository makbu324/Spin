package com.spin_cake_con

import MainViewModel
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.spin_cake_con.databinding.NoteItemBinding
import com.spin_cake_con.databinding.WishlistItemBinding


class NoteHolder(
    private val binding: NoteItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Triple<String, Double, String>, fragmentManager: FragmentManager, viewModel: MainViewModel) {
        binding.actualLocation.text = item.first
        binding.actualPrice.text = item.second.toString()
        binding.memo.text = item.third
        binding.root.setOnClickListener {
            viewModel.note_to_view = item
            val fragmentTransaction = fragmentManager.beginTransaction()
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
    }
}

class NotelistAdapter(private val listOfNotes: MutableList<Triple<String, Double, String>>, private val fragmentManager: FragmentManager, private val viewModel: MainViewModel) : RecyclerView.Adapter<NoteHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NoteHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NoteItemBinding.inflate(inflater, parent, false)
        return NoteHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        val item = listOfNotes[position]
        holder.bind(item, fragmentManager, viewModel)
    }

    override fun getItemCount() = listOfNotes.size
}
