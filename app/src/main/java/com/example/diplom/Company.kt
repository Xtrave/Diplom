package com.example.diplom

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize
@Parcelize
data class Company(
    val name: String = "",
    val short_description: String = "",
    val activity: String = "",
    val certificates: String = "",
    val experience: String = "",
    val location: String = "",
    val rating: String = "",
    val equipment: String = "",
    val phone: String = "",
    val email: String = ""
) : Parcelable {
    constructor() : this("", "", "", "", "", "", "", "")

    companion object {
        fun fromDocument(document: DocumentSnapshot): Company {
            val name = document.getString("name") ?:
                      document.getString("company_name") ?:
                      document.getString("title") ?: ""

            val shortDescription = document.getString("short_description") ?:
                                  document.getString("description") ?:
                                  document.getString("shortDesc") ?: ""

            val activity = document.getString("activity") ?:
                          document.getString("type") ?:
                          document.getString("business_type") ?: ""

            val certificates = document.getString("certificates") ?:
                              document.getString("cert") ?:
                              document.getString("certification") ?: ""

            val experience = document.getString("experience") ?:
                            document.getString("years") ?:
                            document.getString("work_years") ?: ""

            val location = document.getString("location") ?:
                          document.getString("city") ?:
                          document.getString("address") ?: ""

            val rating = document.getString("rating") ?:
                        document.getString("score") ?:
                        document.getString("rate") ?: ""

            val equipment = document.getString("equipment") ?: ""

            val phone = document.getString("phone") ?: ""

            val email = document.getString("email") ?: ""

            return Company(
                name = name,
                short_description = shortDescription,
                activity = activity,
                certificates = certificates,
                experience = experience,
                location = location,
                rating = rating,
                equipment = equipment,
                phone = phone,
                email = email
            )
        }
    }
}
