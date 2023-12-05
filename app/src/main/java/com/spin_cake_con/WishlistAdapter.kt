package com.spin_cake_con

import MainViewModel
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.spin_cake_con.databinding.WishlistItemBinding


class CrimeHolder(
    private val binding: WishlistItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(album: Album, fragmentManager: FragmentManager, viewModel: MainViewModel) {
        if (album.title != "") {
            val imageBytes = Base64.decode(album.base64_album_art, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            binding.albumImage.setImageBitmap(decodedImage)

            binding.AlbumTitle.text = album.title
            binding.ArtistName.text = album.artist
            binding.Year.text = album.year
            binding.root.setOnClickListener {
                viewModel.album_to_view = album
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                fragmentTransaction.replace(R.id.nav_host, AlbumInfoFragment(), AlbumInfoFragment.TAG)
                fragmentTransaction.addToBackStack(AlbumInfoFragment.TAG)
                fragmentTransaction.commit()
            }
        } else {
            binding.byArtist.text = ""
        }
    }
}

class WishlistAdapter(private val listOfAlbums: MutableList<Album>, private val fragmentManager: FragmentManager, private val viewModel: MainViewModel) : RecyclerView.Adapter<CrimeHolder>() {
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
        holder.bind(item, fragmentManager, viewModel)
    }

    override fun getItemCount() = listOfAlbums.size
}
