package id.rllyhz.schotersnewsapp.ui.features.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import id.rllyhz.schotersnewsapp.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var editorListener: TextView.OnEditorActionListener? = null

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
        binding.searchLoadingAnimView.playAnimation()
    }

    override fun onPause() {
        super.onPause()
        binding.searchLoadingAnimView.pauseAnimation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createEditorListener()

        binding.apply {
            searchEtSearch.setOnEditorActionListener(editorListener)
        }
    }

    private fun searchNews() {
        val text = binding.searchEtSearch.text.toString()
        Toast.makeText(requireContext(), "Searching $text...", Toast.LENGTH_SHORT).show()
    }

    private fun createEditorListener() {
        editorListener = TextView.OnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> searchNews()
            }
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.searchEtSearch.setOnEditorActionListener(null)
        editorListener = null
        _binding = null
    }
}