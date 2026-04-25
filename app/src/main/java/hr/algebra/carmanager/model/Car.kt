package hr.algebra.carmanager.model

data class Car(
    val id: Long = 0,
    val brand: String,
    val model: String,
    val year: Int,
    val registrationNumber: String,
    val mileage: Int,
    val fuelType: String,
    val registrationExpiryDate: String,
    val notes: String,
    val imageUri: String?
)