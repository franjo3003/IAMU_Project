package hr.algebra.carmanager.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import hr.algebra.carmanager.databinding.FragmentAboutBinding
import hr.algebra.carmanager.network.CarApiClient
import hr.algebra.carmanager.utils.ConnectivityUtils
import kotlin.concurrent.thread
import hr.algebra.carmanager.R

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnFetchData.setOnClickListener {
            fetchDataFromInternet()
        }
    }

    private fun fetchDataFromInternet() {
        if (!ConnectivityUtils.isInternetAvailable(requireContext())) {
            Toast.makeText(requireContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
            return
        }

        binding.tvApiResult.text = getString(R.string.fetching_car_makes)

        thread {
            try {
                val result = CarApiClient().fetchCarFact()

                mainHandler.post {
                    binding.tvApiResult.text = result
                }
            } catch (e: Exception) {
                mainHandler.post {
                    binding.tvApiResult.text = getString(R.string.fetch_error)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}