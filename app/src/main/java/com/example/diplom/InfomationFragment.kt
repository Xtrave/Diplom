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
        val company_name: TextView = view.findViewById(R.id.company_name)
        val company_type: TextView = view.findViewById(R.id.company_type)
        val raiting: TextView = view.findViewById(R.id.raiting)
        val activity: TextView = view.findViewById(R.id.activity)
        val experience: TextView = view.findViewById(R.id.experience)
        val certificates: TextView = view.findViewById(R.id.certificates)
        val location: TextView = view.findViewById(R.id.location)
        val phone: TextView = view.findViewById(R.id.phone)
        val mail: TextView = view.findViewById(R.id.mail)


        val company = arguments?.getParcelable<Company>("company_key")

        company?.let {
            company_name.text = company.name
            company_type.text = company.short_description
            raiting.text = company.rating
            activity.text = company.activity
            experience.text = company.experience
            certificates.text = company.certificates
            location.text = company.location
            equipment.text = company.equipment
            phone.text = company.phone
            mail.text = company.email
        }

    }

}


