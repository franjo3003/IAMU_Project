package hr.algebra.carmanager.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hr.algebra.carmanager.databinding.ItemCarBinding
import hr.algebra.carmanager.model.Car
import hr.algebra.carmanager.R
import android.content.Context

class CarAdapter(
    private val onCarClick: (Car) -> Unit
) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    private val cars = mutableListOf<Car>()

    fun submitList(newCars: List<Car>) {
        cars.clear()
        cars.addAll(newCars)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = ItemCarBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.bind(cars[position])
    }

    override fun getItemCount(): Int = cars.size

    inner class CarViewHolder(
        private val binding: ItemCarBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(car: Car) {
            binding.tvCarTitle.text = "${car.brand} ${car.model}"

            val fuelDisplayName = getFuelDisplayName(binding.root.context, car.fuelType)

            binding.tvCarDetails.text = binding.root.context.getString(
                R.string.car_card_details,
                car.year,
                fuelDisplayName,
                car.mileage
            )

            binding.tvRegistration.text = binding.root.context.getString(
                R.string.registration_valid_until,
                car.registrationExpiryDate
            )

            binding.root.setOnClickListener {
                onCarClick(car)
            }
        }
    }

    private fun getFuelDisplayName(context: Context, fuelType: String): String {
        return when (fuelType.lowercase()) {
            "benzin", "gasoline" -> context.getString(R.string.fuel_gasoline)
            "diesel" -> context.getString(R.string.fuel_diesel)
            "hybrid" -> context.getString(R.string.fuel_hybrid)
            "električni", "electric" -> context.getString(R.string.fuel_electric)
            else -> fuelType
        }
    }
}