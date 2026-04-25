package hr.algebra.carmanager.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import hr.algebra.carmanager.data.repository.CarRepository
import hr.algebra.carmanager.databinding.FragmentAddEditCarBinding
import hr.algebra.carmanager.model.Car
import android.content.Intent
import hr.algebra.carmanager.receiver.CarActionReceiver
import android.app.DatePickerDialog
import android.widget.ArrayAdapter
import java.util.Calendar
import hr.algebra.carmanager.R

class AddEditCarFragment : Fragment() {

    private var _binding: FragmentAddEditCarBinding? = null
    private val binding get() = _binding!!

    private lateinit var fuelTypes: List<String>
    private lateinit var repository: CarRepository
    private var carId: Long = -1
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        carId = arguments?.getLong("car_id") ?: -1
        isEditMode = carId != -1L
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditCarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        repository = CarRepository(requireContext())
        fuelTypes = resources.getStringArray(R.array.fuel_types).toList()

        setupFuelSpinner()
        setupDatePicker()

        if (isEditMode) {
            loadCarData()
        }

        binding.btnSaveCar.setOnClickListener {
            saveCar()
        }
    }

    private fun loadCarData() {
        val car = repository.getCarById(carId) ?: return

        binding.etBrand.setText(car.brand)
        binding.etModel.setText(car.model)
        binding.etYear.setText(car.year.toString())
        binding.etRegistrationNumber.setText(car.registrationNumber)
        binding.etMileage.setText(car.mileage.toString())
        val fuelIndex = fuelTypes.indexOf(car.fuelType).takeIf { it >= 0 } ?: 0
        binding.spFuelType.setSelection(fuelIndex)
        binding.etRegistrationExpiryDate.setText(car.registrationExpiryDate)
        binding.etNotes.setText(car.notes)
    }

    private fun saveCar() {
        val brand = binding.etBrand.text.toString().trim()
        val model = binding.etModel.text.toString().trim()
        val yearText = binding.etYear.text.toString().trim()
        val registrationNumber = binding.etRegistrationNumber.text.toString().trim()
        val mileageText = binding.etMileage.text.toString().trim()
        val fuelType = binding.spFuelType.selectedItem.toString()
        val registrationExpiryDate = binding.etRegistrationExpiryDate.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()

        if (
            brand.isBlank() ||
            model.isBlank() ||
            yearText.isBlank() ||
            registrationNumber.isBlank() ||
            mileageText.isBlank() ||
            registrationExpiryDate.isBlank()
        ) {
            Toast.makeText(requireContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            return
        }

        val year = yearText.toIntOrNull()
        val mileage = mileageText.toIntOrNull()

        if (year == null || mileage == null) {
            Toast.makeText(requireContext(), getString(R.string.invalid_numbers), Toast.LENGTH_SHORT).show()
            return
        }

        val car = Car(
            id = if (isEditMode) carId else 0,
            brand = brand,
            model = model,
            year = year,
            registrationNumber = registrationNumber,
            mileage = mileage,
            fuelType = fuelType,
            registrationExpiryDate = registrationExpiryDate,
            notes = notes,
            imageUri = null
        )

        if (isEditMode) {
            repository.updateCar(car)
            sendCarChangedBroadcast(getString(R.string.car_updated))
        } else {
            repository.insertCar(car)
            sendCarChangedBroadcast(getString(R.string.car_added))
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, CarListFragment())
            .commit()
    }

    private fun sendCarChangedBroadcast(message: String) {
        val intent = Intent(requireContext(), CarActionReceiver::class.java).apply {
            action = CarActionReceiver.ACTION_CAR_CHANGED
            putExtra(CarActionReceiver.EXTRA_MESSAGE, message)
        }

        requireContext().sendBroadcast(intent)
    }

    private fun setupFuelSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            fuelTypes
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spFuelType.adapter = adapter
    }

    private fun setupDatePicker() {
        binding.etRegistrationExpiryDate.setOnClickListener {
            val calendar = Calendar.getInstance()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = String.format(
                        "%04d-%02d-%02d",
                        selectedYear,
                        selectedMonth + 1,
                        selectedDay
                    )

                    binding.etRegistrationExpiryDate.setText(formattedDate)
                },
                year,
                month,
                day
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}