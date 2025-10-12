package com.example.diplom

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
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

    private var allCompanies = mutableListOf<Company>()
    private var filteredCompanies = mutableListOf<Company>()

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

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        companyAdapter = CompanyAdapter()
        recyclerView.adapter = companyAdapter

        setupSearchInput()
        loadCompaniesFromFirebase()

        return view
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
                    filteredCompanies.addAll(allCompanies)
                    showDataState(filteredCompanies)
                    Toast.makeText(requireContext(), "Загружено ${allCompanies.size} компаний", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                if (exception.message?.contains("permission") == true ||
                    exception.message?.contains("PERMISSION_DENIED") == true) {
                    showErrorState("Нет прав доступа к данным. Проверьте правила безопасности Firebase.")
                } else {
                    showErrorState(exception.message ?: "Неизвестная ошибка")
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

        if (query.isBlank()) {
            filteredCompanies.addAll(allCompanies)
        } else {
            val searchQuery = query.lowercase().trim()

            for (company in allCompanies) {
                if (companyMatchesQuery(company, searchQuery)) {
                    filteredCompanies.add(company)
                }
            }
        }

        if (filteredCompanies.isEmpty() && !allCompanies.isEmpty()) {
            showEmptyState("По запросу \"$query\" ничего не найдено")
        } else {
            showDataState(filteredCompanies)
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
    }

    private fun showEmptyState(message: String = "Нет данных для отображения") {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        emptyStateText.visibility = View.VISIBLE
        emptyStateText.text = message
    }

    private fun showErrorState(errorMessage: String) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        emptyStateText.visibility = View.VISIBLE
        emptyStateText.text = "Ошибка загрузки данных"

        Toast.makeText(requireContext(), "Ошибка: $errorMessage", Toast.LENGTH_LONG).show()
    }

    private fun showKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchInput.windowToken, 0)
    }
}
