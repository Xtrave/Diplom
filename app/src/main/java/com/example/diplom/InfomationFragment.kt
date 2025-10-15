package com.example.diplom

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment

class InfomationFragment : Fragment(R.layout.fragment_infomation) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val equipment: TextView = view.findViewById(R.id.equipment)

        val company = arguments?.getParcelable<Company>("company_key")

        company?.let {
            equipment.text = company.equipment
        }

    }

}


