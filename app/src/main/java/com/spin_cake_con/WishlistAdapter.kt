package com.spin_cake_con

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.spin_cake_con.databinding.WishlistItemBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

class CrimeHolder(
    private val binding: WishlistItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind() {
        binding.AlbumTitle.text = "hiiiiiiii"
    }
}

class WishlistAdapter() : RecyclerView.Adapter<CrimeHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CrimeHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = WishlistItemBinding.inflate(inflater, parent, false)
        return CrimeHolder(binding)
    }

    override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
        //val crime = crimes[position]
        holder.bind()
    }

    override fun getItemCount() = 1 //crimes.size
}
