package hr.algebra.carmanager.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hr.algebra.carmanager.data.repository.CarRepository
import hr.algebra.carmanager.databinding.FragmentCarExtraInfoBinding
import hr.algebra.carmanager.R

class CarExtraInfoFragment : Fragment() {

    private var _binding: FragmentCarExtraInfoBinding? = null
    private val binding get() = _binding!!

    private var carId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        carId = arguments?.getLong(ARG_CAR_ID) ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarExtraInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val car = CarRepository(requireContext()).getCarById(carId) ?: return

        binding.tvRegistrationNumber.text = getString(R.string.registration_number_format, car.registrationNumber)
        binding.tvRegistrationExpiry.text = getString(R.string.registration_expiry_format, car.registrationExpiryDate)
        binding.tvNotes.text = getString(R.string.notes_format, car.notes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CAR_ID = "car_id"

        fun newInstance(carId: Long): CarExtraInfoFragment {
            return CarExtraInfoFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_CAR_ID, carId)
                }
            }
        }
    }
}