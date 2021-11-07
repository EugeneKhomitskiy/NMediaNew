package ru.netology.nmedia.activity

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import ru.netology.nmedia.BuildConfig.BASE_URL
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentImageBinding

class ImageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentImageBinding.inflate(
            inflater,
            container,
            false
        )
        // Меняем цвет панелей на черный
        val activity = requireActivity()
        if (activity is AppCompatActivity) {
            val statusBarColor = ContextCompat.getColor(requireActivity(), R.color.black)
            activity.window.statusBarColor = statusBarColor
            activity.supportActionBar?.setBackgroundDrawable(ColorDrawable(statusBarColor))
        }

        val url = "${BASE_URL}/media/${arguments?.getString("url")}"

        Glide.with(this)
            .load(url)
            .into(binding.imageView)

        return binding.root
    }
    // При закрытии фрагмента восстанавливаем исходные цвета
    override fun onDestroyView() {
        super.onDestroyView()
        val activity = requireActivity()
        if (activity is AppCompatActivity) {
            val statusBarColor = ContextCompat.getColor(requireActivity(), R.color.design_default_color_primary_variant)
            val actionBarColor = ContextCompat.getColor(requireActivity(), R.color.design_default_color_primary)
            activity.window.statusBarColor = statusBarColor
            activity.supportActionBar?.setBackgroundDrawable(ColorDrawable(actionBarColor))
        }
    }
}