package com.example.peerprep.data.repository

import android.content.Context
import android.util.Log
import com.example.peerprep.R
import com.example.peerprep.data.util.CsvParser
import com.example.peerprep.domain.model.Department
import com.example.peerprep.domain.model.University
import com.example.peerprep.domain.repository.UniversityRepository
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset


class UniversityRepositoryImpl(
    private val context: Context
) : UniversityRepository {

    override suspend fun getUniversities(): List<University> {
        val universities = mutableListOf<University>()
        val inputStream = context.resources.openRawResource(R.raw.uni)
        val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
        var line: String?
        var currentUniversity: University? = null
        var departments = mutableListOf<Department>()

        while (reader.readLine().also { line = it } != null) {
            if (line.isNullOrBlank()) continue

            val fields = CsvParser.parseLine(line!!)
            if (fields.isEmpty()) continue

            val name = fields[0].trim('"')

            if (!name.startsWith("  ")) {
                if (currentUniversity != null) {
                    currentUniversity = currentUniversity.copy(departments = departments)
                    universities.add(currentUniversity)
                    Log.d("UniversityRepository", "Added university: ${currentUniversity.name} with ${departments.size} departments")
                }
                currentUniversity = University(name = name, departments = emptyList())
                departments = mutableListOf()
            } else {
                val department = Department(
                    name = name.trim(),
                    field = fields.getOrNull(1) ?: "",
                    rank = fields.getOrNull(2) ?: "",
                    score = fields.getOrNull(3) ?: ""
                )
                departments.add(department)
                Log.d("UniversityRepository", "Added department: ${department.name}")
            }
        }

        if (currentUniversity != null) {
            currentUniversity = currentUniversity.copy(departments = departments)
            universities.add(currentUniversity)
        }

        reader.close()
        return universities
    }
}

