package hr.algebra.carmanager.ui.fragment

import hr.algebra.carmanager.R
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import hr.algebra.carmanager.data.repository.CarRepository
import hr.algebra.carmanager.databinding.FragmentCarListBinding
import hr.algebra.carmanager.model.Car
import hr.algebra.carmanager.ui.adapter.CarAdapter
import hr.algebra.carmanager.data.prefs.PreferenceManager
import android.widget.Toast
import hr.algebra.carmanager.data.provider.CarContentProvider

class CarListFragment : Fragment() {

    private lateinit var preferenceManager: PreferenceManager
    private var _binding: FragmentCarListBinding? = null
    private val binding get() = _binding!!

    private lateinit var carRepository: CarRepository
    private lateinit var carAdapter: CarAdapter
    private var currentSearchQuery: String = ""

    companion object {
        private const val KEY_SEARCH_QUERY = "search_query"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        carRepository = CarRepository(requireContext())
        preferenceManager = PreferenceManager(requireContext())

        setupRecyclerView()
        insertTestCarsIfNeeded()
        loadCars()
        testContentResolver()
        setupSearch()
        currentSearchQuery = savedInstanceState?.getString(KEY_SEARCH_QUERY).orEmpty()

        if (currentSearchQuery.isNotBlank()) {
            binding.etSearchCars.setText(currentSearchQuery)
        }
        setupAddButton()
    }

    private fun setupRecyclerView() {
        carAdapter = CarAdapter { car ->

            val fragment = CarDetailsFragment().apply {
                arguments = Bundle().apply {
                    putLong("car_id", car.id)
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.rvCars.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCars.adapter = carAdapter
    }

    private fun insertTestCarsIfNeeded() {
        if (carRepository.getAllCars().isNotEmpty()) return

        val testCars = listOf(
            Car(
                brand = "BMW",
                model = "320d",
                year = 2018,
                registrationNumber = "ZG-1234-AB",
                mileage = 145000,
                fuelType = "Diesel",
                registrationExpiryDate = "2026-05-20",
                notes = "Službeni testni automobil",
                imageUri = null
            ),
            Car(
                brand = "Volkswagen",
                model = "Golf 7",
                year = 2017,
                registrationNumber = "ST-9876-CD",
                mileage = 132000,
                fuelType = "Benzin",
                registrationExpiryDate = "2026-09-10",
                notes = "Redovito održavan",
                imageUri = null
            ),
            Car(
                brand = "Toyota",
                model = "Corolla",
                year = 2020,
                registrationNumber = "RI-5555-EF",
                mileage = 76000,
                fuelType = "Hybrid",
                registrationExpiryDate = "2027-01-15",
                notes = "Mala potrošnja",
                imageUri = null
            )
        )

        testCars.forEach { carRepository.insertCar(it) }
    }

    private fun loadCars() {
        val allCars = carRepository.getAllCars()
            .sortedWith(compareBy({ it.brand.lowercase() }, { it.model.lowercase() }))

        val filter = preferenceManager.getDefaultFuelFilter()

        val filteredCars = allCars.filter {
            fuelMatchesFilter(it.fuelType, filter)
        }

        carAdapter.submitList(filteredCars)
    }

    private fun setupSearch() {
        binding.etSearchCars.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) = Unit

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val query = s?.toString().orEmpty()
                currentSearchQuery = query
                val cars = if (query.isBlank()) {
                    carRepository.getAllCars()
                } else {
                    carRepository.searchCars(query)
                }

                carAdapter.submitList(cars)
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun setupAddButton() {
        binding.btnAddCar.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, AddEditCarFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun testContentResolver() {
        val cursor = requireContext().contentResolver.query(
            CarContentProvider.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            Toast.makeText(
                requireContext(),
                getString(R.string.content_resolver_found, it.count),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun fuelMatchesFilter(carFuelType: String, filter: String): Boolean {
        return when (filter) {
            "all" -> true
            "gasoline" -> carFuelType.equals("Benzin", true) ||
                    carFuelType.equals("Gasoline", true)
            "diesel" -> carFuelType.equals("Diesel", true)
            "hybrid" -> carFuelType.equals("Hybrid", true)
            "electric" -> carFuelType.equals("Električni", true) ||
                    carFuelType.equals("Electric", true)
            else -> true
        }
    }

    override fun onResume() {
        super.onResume()
        if (::carAdapter.isInitialized) {
            loadCars()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_QUERY, currentSearchQuery)
    }
}