package com.spin_cake_con

import MainViewModel
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager


@Keep
class SettingsFragment: Fragment() {
    companion object {
        const val TAG = "SettingsFragment"
    }

    private val viewModel by activityViewModels<MainViewModel>()
    private lateinit var viewPager: ViewPager
    private lateinit var pagesAdapter: SearchPagesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setAllowHomeButton(true)

        val clickWood: MediaPlayer = MediaPlayer.create(view.context, R.raw.wood_click)

        view.findViewById<Switch>(R.id.simpleSwitch).isChecked = viewModel.sound_effects_on

        view.findViewById<Switch>(R.id.simpleSwitch).setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.sound_effects_on = true
                clickWood.start()
            }
            else
                viewModel.sound_effects_on = false
        })


    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setAllowHomeButton(false)
    }

}