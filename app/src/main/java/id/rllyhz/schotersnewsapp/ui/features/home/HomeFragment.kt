package id.rllyhz.schotersnewsapp.ui.features.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import id.rllyhz.schotersnewsapp.databinding.FragmentHomeBinding
import id.rllyhz.schotersnewsapp.utils.Constants
import id.rllyhz.schotersnewsapp.utils.hide
import id.rllyhz.schotersnewsapp.utils.show

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var viewModel: HomeViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            HomeViewModel.Factory(Constants.getRepository(), Constants.dispatchersProvider)
        )[HomeViewModel::class.java]

        viewModel?.let { vm ->
            vm.news.observe(viewLifecycleOwner) { news ->
                if (!news.isNullOrEmpty()) {
                    binding.apply {
                        homeRv.show()
                        homeShimmerLayout.hide()
                        homeShimmerLayout.stopShimmer()
                    }
                }
            }

            vm.shouldLoading.observe(viewLifecycleOwner) { loading ->
                if (loading) {
                    binding.apply {
                        homeRv.hide()
                        homeShimmerLayout.show()
                        homeShimmerLayout.startShimmer()
                    }
                }
            }

            vm.isError.observe(viewLifecycleOwner) { errorOccurred ->
                if (errorOccurred) {
                    binding.apply {
                        homeRv.hide()
                        homeShimmerLayout.hide()
                        homeShimmerLayout.stopShimmer()
                    }
                }
            }

            vm.getTrendingNews()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel = null
    }
}