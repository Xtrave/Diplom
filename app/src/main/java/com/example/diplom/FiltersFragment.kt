package com.example.diplom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment

class FiltersFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_filters, container, false)

        val rgActivity = view.findViewById<RadioGroup>(R.id.rg_activity)
        val rgCity = view.findViewById<RadioGroup>(R.id.rg_city)
        val rgRating = view.findViewById<RadioGroup>(R.id.rg_rating)
        val rgExperience = view.findViewById<RadioGroup>(R.id.rg_experience)

        val cbIso9001 = view.findViewById<CheckBox>(R.id.cb_cert_iso9001)
        val cbIso14001 = view.findViewById<CheckBox>(R.id.cb_cert_iso14001)
        val cbIatf = view.findViewById<CheckBox>(R.id.cb_cert_iatf)
        val cbAs9100 = view.findViewById<CheckBox>(R.id.cb_cert_as9100)
        val cbCe = view.findViewById<CheckBox>(R.id.cb_cert_ce)
        val cbRohs = view.findViewById<CheckBox>(R.id.cb_cert_rohs)
        val cbQualicoat = view.findViewById<CheckBox>(R.id.cb_cert_qualicoat)
        val cbEn1090 = view.findViewById<CheckBox>(R.id.cb_cert_en1090)
        val cbIso3834 = view.findViewById<CheckBox>(R.id.cb_cert_iso3834)

        val btnApply = view.findViewById<Button>(R.id.btn_apply)
        val btnReset = view.findViewById<Button>(R.id.btn_reset)

        btnReset.setOnClickListener {
            rgActivity.clearCheck()
            rgCity.clearCheck()
            rgRating.clearCheck()
            rgExperience.clearCheck()
            cbIso9001.isChecked = false
            cbIso14001.isChecked = false
            cbIatf.isChecked = false
            cbAs9100.isChecked = false
            cbCe.isChecked = false
            cbRohs.isChecked = false
            cbQualicoat.isChecked = false
            cbEn1090.isChecked = false
            cbIso3834.isChecked = false
        }

        btnApply.setOnClickListener {
            val activity = rgActivity.findViewById<RadioButton>(rgActivity.checkedRadioButtonId)?.text?.toString()
            val city = rgCity.findViewById<RadioButton>(rgCity.checkedRadioButtonId)?.text?.toString()
            val ratingMin = when (rgRating.checkedRadioButtonId) {
                R.id.rb_rating_49 -> 4.9
                R.id.rb_rating_47 -> 4.7
                R.id.rb_rating_45 -> 4.5
                else -> null
            }
            val experienceBand = when (rgExperience.checkedRadioButtonId) {
                R.id.rb_exp_lt5 -> "<5"
                R.id.rb_exp_5_10 -> "5-10"
                R.id.rb_exp_10_15 -> "10-15"
                R.id.rb_exp_gt15 -> ">15"
                else -> null
            }
            val certs = mutableListOf<String>()
            if (cbIso9001.isChecked) certs.add("ISO 9001")
            if (cbIso14001.isChecked) certs.add("ISO 14001")
            if (cbIatf.isChecked) certs.add("IATF 16949")
            if (cbAs9100.isChecked) certs.add("AS9100")
            if (cbCe.isChecked) certs.add("CE")
            if (cbRohs.isChecked) certs.add("RoHS")
            if (cbQualicoat.isChecked) certs.add("Qualicoat")
            if (cbEn1090.isChecked) certs.add("EN 1090")
            if (cbIso3834.isChecked) certs.add("ISO 3834")

            println("FiltersFragment: Sending filters:")
            println("  Activity: $activity")
            println("  City: $city")
            println("  RatingMin: $ratingMin")
            println("  Experience: $experienceBand")
            println("  Certificates: $certs")

            val result = Bundle().apply {
                putString("filter_activity", activity)
                putString("filter_city", city)
                if (ratingMin != null) putDouble("filter_rating_min", ratingMin)
                if (experienceBand != null) putString("filter_experience", experienceBand)
                putStringArrayList("filter_certs", ArrayList(certs))
            }

            parentFragmentManager.setFragmentResult("filters_result", result)
            parentFragmentManager.popBackStack()
        }

        return view
    }
}


