package com.example.diplom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CompanyListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_company_list, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.company_recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val companies = listOf(
            Company("ООО «ТехМеталл»", "Металлообработка", "Опыт 12 лет, ISO 9001"),
            Company("ЗАО «ЭлектроМаш»", "Электроника", "Производство печатных плат"),
            Company("ООО «WoodLine»", "Деревообработка", "Изделия из массива дерева"),
            Company("ИП Иванов", "3D-печать", "Изготовление прототипов")
        )

        recyclerView.adapter = CompanyAdapter(companies)
        return view
    }
}
