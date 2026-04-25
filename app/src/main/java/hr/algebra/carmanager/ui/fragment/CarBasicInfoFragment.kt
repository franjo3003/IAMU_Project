package hr.algebra.carmanager.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hr.algebra.carmanager.data.repository.CarRepository
import hr.algebra.carmanager.databinding.FragmentCarBasicInfoBinding
import hr.algebra.carmanager.R

class CarBasicInfoFragment : Fragment() {

    private var _binding: FragmentCarBasicInfoBinding? = null
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
        _binding = FragmentCarBasicInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val car = CarRepository(requireContext()).getCarById(carId) ?: return

        binding.tvTitle.text = "${car.brand} ${car.model}"
        binding.tvYear.text = getString(R.string.year_format, car.year)
        binding.tvFuel.text = getString(R.string.fuel_format, car.fuelType)
        binding.tvMileage.text = getString(R.string.mileage_format, car.mileage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CAR_ID = "car_id"

        fun newInstance(carId: Long): CarBasicInfoFragment {
            return CarBasicInfoFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_CAR_ID, carId)
                }
            }
        }
    }
}