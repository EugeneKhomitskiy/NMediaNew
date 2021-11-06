package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import ru.netology.nmedia.BuildConfig.BASE_URL
import ru.netology.nmedia.databinding.FragmentImageBinding

class ImageFragment : Fragment() {

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar!!.hide()
    }

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

        val url = "${BASE_URL}/media/${arguments?.getString("url")}"

        Glide.with(this)
            .load(url)
            .into(binding.imageView)

        return binding.root
    }
}