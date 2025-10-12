package com.example.diplom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CompanyAdapter : RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder>() {
    
    private var companies: MutableList<Company> = mutableListOf()

    class CompanyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.company_name)
        val shortDesc: TextView = itemView.findViewById(R.id.company_type)
        val activity: TextView = itemView.findViewById(R.id.activity)
        val certificates: TextView = itemView.findViewById(R.id.certificates)
        val experience: TextView = itemView.findViewById(R.id.experience)
        val location: TextView = itemView.findViewById(R.id.location)
        val rating: TextView = itemView.findViewById(R.id.raiting)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_company, parent, false)
        return CompanyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        val company = companies[position]
        holder.name.text = company.name
        holder.shortDesc.text = company.short_description
        holder.activity.text = company.activity
        holder.certificates.text = company.certificates
        holder.experience.text = company.experience
        holder.location.text = company.location
        holder.rating.text = company.rating
    }

    override fun getItemCount(): Int = companies.size
    
    fun updateCompanies(newCompanies: List<Company>) {
        companies.clear()
        companies.addAll(newCompanies)
        notifyDataSetChanged()
    }
}


