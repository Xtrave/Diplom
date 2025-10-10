package com.example.diplom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CompanyAdapter(private val companies: List<Company>) :
    RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder>() {

    class CompanyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.company_name)
        val type: TextView = itemView.findViewById(R.id.company_type)
        val details: TextView = itemView.findViewById(R.id.company_details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_company, parent, false)
        return CompanyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        val company = companies[position]
        holder.name.text = company.name
        holder.type.text = company.type
        holder.details.text = company.details
    }

    override fun getItemCount(): Int = companies.size
}


