package com.example.android.politicalpreparedness.network.models

import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.*
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable
import java.util.*

@Parcelize
@Entity(tableName = "election_table")
data class Election(
        @PrimaryKey val id: String,
        val name: String,
        val electionDay: String,
        @SerializedName("ocdDivisionId") val division: String
) : Parcelable