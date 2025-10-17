package com.example.diplom

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class CompanyListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var companyAdapter: CompanyAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyStateText: TextView
    private lateinit var searchInput: EditText
    private lateinit var filtersButton: Button

    private var allCompanies = mutableListOf<Company>()
    private var filteredCompanies = mutableListOf<Company>()

    private var currentFilterActivity: String? = null
    private var currentFilterCity: String? = null
    private var currentFilterRatingMin: Double? = null
    private var currentFilterExperienceBand: String? = null
    private var currentFilterCertificates: List<String> = emptyList()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_company_list, container, false)

        recyclerView = view.findViewById(R.id.RecyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        emptyStateText = view.findViewById(R.id.emptyStateText)
        searchInput = view.findViewById(R.id.search_input)
        filtersButton = view.findViewById(R.id.btn_filters)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        companyAdapter = CompanyAdapter { company ->
            openCompanyDetails(company)
        }

        recyclerView.adapter = companyAdapter

        setupSearchInput()
        setupFiltersButton()
        setupFiltersResultListener()
        loadCompaniesFromFirebase()

        return view
    }
    private fun setupFiltersResultListener() {
        parentFragmentManager.setFragmentResultListener("filters_result", viewLifecycleOwner) { _, bundle ->
            val activity = bundle.getString("filter_activity")
            val city = bundle.getString("filter_city")
            val ratingMin = if (bundle.containsKey("filter_rating_min")) bundle.getDouble("filter_rating_min") else null
            val expBand = bundle.getString("filter_experience")
            val certs = bundle.getStringArrayList("filter_certs") ?: arrayListOf()

            applyFilters(activity, city, ratingMin, expBand, certs)
        }
    }

    private fun applyFilters(
        activity: String?,
        city: String?,
        ratingMin: Double?,
        experienceBand: String?,
        certificates: List<String>
    ) {
        currentFilterActivity = activity
        currentFilterCity = city
        currentFilterRatingMin = ratingMin
        currentFilterExperienceBand = experienceBand
        currentFilterCertificates = certificates

        val currentQuery = searchInput.text?.toString().orEmpty()
        filterCompanies(currentQuery)

    }

    private fun parseExperienceYears(text: String): Int? {
        val normalized = text.lowercase()
            .replace("год", "")
            .replace("лет", "")
            .replace("года", "")
            .trim()
        return normalized.filter { it.isDigit() }.toIntOrNull()
    }
    private fun setupFiltersButton() {
        filtersButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FiltersFragment())
                .addToBackStack(null)
                .commit()
        }
    }
        private fun openCompanyDetails(company: Company) {
            val bundle = Bundle().apply {
                putParcelable("company_key", company)
            }

            val infomationFragment = InfomationFragment().apply {
                arguments = bundle
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, infomationFragment)
                .addToBackStack(null)
                .commit()
    }
    private fun loadCompaniesFromFirebase() {
        val db = Firebase.firestore

        showLoadingState()

        db.collection("companies")
            .get()
            .addOnSuccessListener { result ->

                allCompanies.clear()
                filteredCompanies.clear()

                for (document in result) {
                    val company = Company.fromDocument(document)
                    allCompanies.add(company)
                }

                if (allCompanies.isEmpty()) {
                    showEmptyState()
                } else {
                    val currentQuery = searchInput.text?.toString().orEmpty()
                    filterCompanies(currentQuery)
                }
            }
    }

     private fun setupSearchInput() {
        searchInput.isFocusable = true
        searchInput.isFocusableInTouchMode = true
        searchInput.isClickable = true

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterCompanies(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchInput.setOnClickListener {
            searchInput.requestFocus()
            showKeyboard()
        }

        searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showKeyboard()
            }
        }
    }

    private fun filterCompanies(query: String) {
        filteredCompanies.clear()

        val base = filterByCurrentFilters(allCompanies)

        if (query.isBlank()) {
            filteredCompanies.addAll(base)
        } else {
            val searchQuery = query.lowercase().trim()
            for (company in base) {
                if (companyMatchesQuery(company, searchQuery)) {
                    filteredCompanies.add(company)
                }
            }
        }

        if (filteredCompanies.isEmpty() && allCompanies.isNotEmpty()) {
            val reason = if (query.isBlank()) "Нет компаний по выбранным фильтрам" else "По запросу \"$query\" ничего не найдено"
            showEmptyState(reason)
        } else {
            showDataState(filteredCompanies)
        }
    }

    private fun filterByCurrentFilters(source: List<Company>): List<Company> {
        println("Filtering ${source.size} companies with filters:")
        println("  Activity: $currentFilterActivity")
        println("  City: $currentFilterCity") 
        println("  RatingMin: $currentFilterRatingMin")
        println("  Experience: $currentFilterExperienceBand")
        println("  Certificates: $currentFilterCertificates")
        
        return source.filter { company ->
            var ok = true

            currentFilterActivity?.let { v ->
                if (v.isNotBlank()) {
                    val matches = company.activity.contains(v, ignoreCase = true)
                    println("  Company ${company.name}: activity '$v' matches '${company.activity}' = $matches")
                    ok = ok && matches
                }
            }
            currentFilterCity?.let { v ->
                if (v.isNotBlank()) {
                    val matches = company.location.contains(v, ignoreCase = true)
                    println("  Company ${company.name}: city '$v' matches '${company.location}' = $matches")
                    ok = ok && matches
                }
            }
            currentFilterRatingMin?.let { min ->
                val ratingValue = company.rating.replace(",", ".").toDoubleOrNull()
                val matches = ratingValue != null && ratingValue >= min
                println("  Company ${company.name}: rating $ratingValue >= $min = $matches")
                ok = ok && matches
            }
            currentFilterExperienceBand?.let { band ->
                if (band.isNotBlank()) {
                    val years = parseExperienceYears(company.experience)
                    val matches = when (band) {
                        "<5" -> years != null && years < 5
                        "5-10" -> years != null && years in 5..10
                        "10-15" -> years != null && years in 10..15
                        ">15" -> years != null && years > 15
                        else -> true
                    }
                    println("  Company ${company.name}: experience '$band' (${years} years) = $matches")
                    ok = ok && matches
                }
            }
            if (currentFilterCertificates.isNotEmpty()) {
                val matches = currentFilterCertificates.all { cert ->
                    company.certificates.contains(cert, ignoreCase = true)
                }
                println("  Company ${company.name}: certificates '$currentFilterCertificates' in '${company.certificates}' = $matches")
                ok = ok && matches
            }

            println("  Company ${company.name}: final result = $ok")
            ok
        }
    }

    private fun companyMatchesQuery(company: Company, query: String): Boolean {
        return company.name.lowercase().contains(query) ||
               company.short_description.lowercase().contains(query) ||
               company.activity.lowercase().contains(query) ||
               company.certificates.lowercase().contains(query) ||
               company.experience.lowercase().contains(query) ||
               company.location.lowercase().contains(query) ||
               company.rating.lowercase().contains(query)
    }

    private fun showLoadingState() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        emptyStateText.visibility = View.GONE
    }

    private fun showDataState(companies: List<Company>) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        emptyStateText.visibility = View.GONE

        companyAdapter.updateCompanies(companies)
        recyclerView.scrollToPosition(0)
    }

    private fun showEmptyState(message: String = "Нет данных для отображения") {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        emptyStateText.visibility = View.VISIBLE
        emptyStateText.text = message
    }

    private fun showKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT)
    }

}
