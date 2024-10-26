package com.folioreader.model.locators

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.databind.ObjectMapper
import com.folioreader.util.ObjectMapperSingleton
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.Parceler
import org.readium.r2.shared.Locations
import org.readium.r2.shared.Locator
import org.readium.r2.shared.LocatorText

@JsonPropertyOrder("bookId", "href", "created", "locations")
@JsonIgnoreProperties(ignoreUnknown = true)
@Parcelize
open class ReadLocator : Locator, Parcelable {

    var bookId: String

    @Suppress("unused") // Required for fromJSON()
    constructor() : this("", "", 0, Locations())

    constructor(bookId: String, href: String, created: Long, locations: Locations) :
            this(bookId, href, created, "", locations, null)

    constructor(
        bookId: String, href: String, created: Long, title: String, locations: Locations,
        text: LocatorText?
    ) : super(href, created, title, locations, text) {
        this.bookId = bookId
    }

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readSerializable() as Locations,
        parcel.readSerializable() as LocatorText?
    )

    companion object : Parceler<ReadLocator> {

        @JvmField
        val LOG_TAG: String = ReadLocator::class.java.simpleName

        @JvmStatic
        fun fromJson(json: String?): ReadLocator? {
            return try {
                ObjectMapperSingleton.getObjectMapper()
                    .reader()
                    .forType(ReadLocator::class.java)
                    .readValue(json)
            } catch (e: Exception) {
                Log.e(LOG_TAG, "-> ", e)
                null
            }
        }

        override fun ReadLocator.write(dest: Parcel, flags: Int){
            dest?.writeString(bookId)
            dest?.writeString(href)
            dest?.writeLong(created)
            dest?.writeString(title)
            dest?.writeSerializable(locations)
            dest?.writeSerializable(text)
        }

        override fun create(parcel: Parcel): ReadLocator {
            return ReadLocator(parcel)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    fun toJson(): String? {

        return try {
            val objectMapper = ObjectMapper()
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
            objectMapper.writeValueAsString(this)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "-> ", e)
            null
        }
    }
}