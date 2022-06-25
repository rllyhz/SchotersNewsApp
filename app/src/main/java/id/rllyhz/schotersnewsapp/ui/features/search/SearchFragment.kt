package id.rllyhz.schotersnewsapp.ui.features.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import id.rllyhz.schotersnewsapp.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.loadingAnimView.playAnimation()
    }

    override fun onPause() {
        super.onPause()
        binding.loadingAnimView.pauseAnimation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}