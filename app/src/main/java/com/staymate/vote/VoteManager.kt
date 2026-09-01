package com.staymate.vote

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class VoteManager {

    private val candidates = listOf(
        Candidate(1, "Akmal Irfan", "Party A"),
        Candidate(2, "Junior Jann", "Party B"),
        Candidate(3, "Ahmed Taqif", "Party C")
    )

    fun getCandidates(): List<Candidate> = candidates

    fun verifyAge(dobText: String): AgeResult { // to check the voter age
        if (dobText.isBlank()) {
            return AgeResult.Error("Please enter your date of birth.")
        }

        val age = parseAge(dobText) // calc the age and insert it to age

        if (age == null) {
            return AgeResult.Error("Invalid format. Use dd/MM/yyyy.") //if no input
        }

        if (age < 21) {
            return AgeResult.TooYoung(age) //if yes input
        }

        return AgeResult.Valid(age) //return input
    }

    fun castVote(candidateId: Int): VoteResult {
        val candidate = candidates.find { it.id == candidateId }
            ?: return VoteResult.Error("Invalid candidate.") //if input no correct
        return VoteResult.Success(candidate.name, candidate.party)
    }

    private fun parseAge(dobText: String): Int? {
        return try { //create date format
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            formatter.isLenient = false
            //parse the string to Date
            val dob = formatter.parse(dobText) ?: return null
            //get current date
            val today = Calendar.getInstance()

            if (dob.after(today.time)) return null
            //calc year different
            val dobCalendar = Calendar.getInstance().apply { time = dob }

            var age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)

            if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
                age--
            }

            age
        } catch (e: ParseException) {
            null
        }
    }

    sealed class AgeResult {
        data class Valid(val age: Int) : AgeResult() // valid age
        data class TooYoung(val age: Int) : AgeResult() // under 21 age
        data class Error(val message: String) : AgeResult() // invalid input
    }

    sealed class VoteResult {
        data class Success(
            val candidateName: String,
            val candidateParty: String
        ) : VoteResult()

        data class Error(val message: String) : VoteResult()
    }
}