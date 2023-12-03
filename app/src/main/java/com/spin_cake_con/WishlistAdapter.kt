package com.spin_cake_con

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.spin_cake_con.databinding.WishlistItemBinding


class CrimeHolder(
    private val binding: WishlistItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(album: Album) {
        val imageBytes = Base64.decode(album.base64_album_art, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        binding.albumImage.setImageBitmap(decodedImage)

        binding.AlbumTitle.text = album.title
        binding.ArtistName.text = album.artist
        binding.Year.text = album.year
    }
}

class WishlistAdapter(private val listOfAlbums: MutableList<Album>) : RecyclerView.Adapter<CrimeHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CrimeHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = WishlistItemBinding.inflate(inflater, parent, false)
        return CrimeHolder(binding)
    }

    override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
        val item = listOfAlbums[position]
        holder.bind(item)
    }

    override fun getItemCount() = listOfAlbums.size
}
