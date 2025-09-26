package com.example.weatherforecastapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.splash_fragment_a1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        requireActivity().window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN

        val btnGetStarted = view.findViewById<View>(R.id.btnGetStarted)
        btnGetStarted.setOnClickListener {
            findNavController().navigate(R.id.action_splashFragment_to_a2Fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        (activity as? AppCompatActivity)?.supportActionBar?.show()
        requireActivity().window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_VISIBLE
    }
}
