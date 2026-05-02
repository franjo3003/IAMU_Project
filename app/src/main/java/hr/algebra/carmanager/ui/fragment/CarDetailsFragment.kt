package hr.algebra.carmanager.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import hr.algebra.carmanager.R
import hr.algebra.carmanager.data.repository.CarRepository
import hr.algebra.carmanager.databinding.FragmentCarDetailsBinding
import hr.algebra.carmanager.ui.adapter.CarDetailsPagerAdapter

class CarDetailsFragment : Fragment() {

    private var _binding: FragmentCarDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: CarRepository
    private var carId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        carId = arguments?.getLong("car_id") ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        repository = CarRepository(requireContext())

        val car = repository.getCarById(carId)
        if (car == null) {
            Toast.makeText(requireContext(), getString(R.string.car_not_found), Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        binding.tvDetailsTitle.text = "${car.brand} ${car.model}"
        binding.carStatusView.setRegistrationExpiryDate(car.registrationExpiryDate)
        binding.viewPagerCarDetails.adapter = CarDetailsPagerAdapter(this, carId)

        binding.btnEdit.setOnClickListener {
            val fragment = AddEditCarFragment().apply {
                arguments = Bundle().apply {
                    putLong("car_id", carId)
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.btnDelete.setOnClickListener {
            repository.deleteCar(carId)
            Toast.makeText(requireContext(), getString(R.string.car_deleted), Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}