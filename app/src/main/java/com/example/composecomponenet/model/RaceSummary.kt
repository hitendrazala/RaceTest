package com.example.composecomponenet.model

data class RaceSummary(
    val race_id: String,
    val race_name: String,
    val race_number: Int,
    val meeting_id: String,
    val meeting_name: String,
    val category_id: String,
    val advertised_start: AdvertisedStart,
    val race_form: RaceForm,
    val venue_id: String,
    val venue_name: String,
    val venue_state: String,
    val venue_country: String
)
data class AdvertisedStart(
    val seconds: Long
)
data class RaceForm(
    val distance: Int,
    val distance_type: DistanceType,
    val distance_type_id: String,
    val track_condition: TrackCondition,
    val track_condition_id: String,
    val additional_data: String,
    val generated: Int,
    val silk_base_url: String
)
data class DistanceType(
    val id: String,
    val name: String,
    val short_name: String
)
data class TrackCondition(
    val id: String,
    val name: String,
    val short_name: String
)

data class RaceData(
    val status: Int,
    val data: RaceDataDetails
)
data class RaceDataDetails(
    val next_to_go_ids: List<String>,
    val race_summaries: Map<String, RaceSummary>
)