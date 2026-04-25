package hr.algebra.carmanager.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import hr.algebra.carmanager.ui.fragment.CarBasicInfoFragment
import hr.algebra.carmanager.ui.fragment.CarExtraInfoFragment

class CarDetailsPagerAdapter(
    fragment: Fragment,
    private val carId: Long
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CarBasicInfoFragment.newInstance(carId)
            else -> CarExtraInfoFragment.newInstance(carId)
        }
    }
}