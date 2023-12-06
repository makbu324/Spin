package com.spin_cake_con

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.spin_cake_con.databinding.FragmentWishlistBinding
import MainViewModel
import android.widget.Button
import android.widget.ImageButton

@Keep
class WishlistFragment: Fragment() {
    private var _binding: FragmentWishlistBinding? = null
    private val viewModel by activityViewModels<MainViewModel>()

    companion object {
        const val TAG = "WishlistFragment"
    }
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    //private val crimeListViewModel: CrimeListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        binding.recyclerView.adapter = WishlistAdapter(viewModel.THE_WISHLIST, parentFragmentManager, viewModel)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setAllowHomeButton(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (!viewModel.looking_at_wishlist_from_result) {
            viewModel.setAllowHomeButton(false)
            viewModel.looking_at_wishlist_from_result = false
        }
    }

}