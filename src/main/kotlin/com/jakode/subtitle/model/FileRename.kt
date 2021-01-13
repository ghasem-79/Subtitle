package com.jakode.subtitle.model

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.regex.Pattern

class FileRename(
    pathDir: String,
    var newName: String? = null,
    var videoFormat: String = "mkv",
    var subtitleFormat: String = "srt",
    var videoSubstringUnique: String = "S##E##",
    var subtitleSubstringUnique: String = "S##E##",
) {
    private val videoList by lazy { ArrayList<File>() }
    private val subtitleList by lazy { ArrayList<File>() }

    private var videoUniquePart: String? = null
    private var subtitleUniquePart: String? = null

    private var _allFiles: List<File> = emptyList()
    val allFiles get() = _allFiles

    val isEmptyFolder get() = allFiles.isEmpty()

    init {
        val dir = File(pathDir)
        _allFiles = dir.listFiles()!!.toList()
    }

    companion object {
        fun mapToFile(paths: List<String>): List<File> {
            return paths.map { File(it) }
        }

        fun rename(file: File, name: String, format: String) {
            val source = Paths.get(file.toURI())
            Files.move(
                source,
                source.resolveSibling("$name$format")
            )
        }

        fun deleteFile(file: File) =
            println(if (file.delete()) "File deleted successfully" else "Failed to delete the file")
    }

    fun rename(selectedFiles: MutableList<File>) {
        extractVS(selectedFiles)
        for (video in videoList) {
            val videoName = video.name

            for (subtitle in subtitleList) {
                val subtitleName = subtitle.name

                if (isValid(videoName, subtitleName)) { // matching video and sub
                    try {
                        if (newName != null) {
                            // Rename video and subtitle
                            val vName = "$newName.$videoUniquePart."
                            Companion.rename(video, vName, videoFormat)
                            Companion.rename(subtitle, vName, subtitleFormat)
                        } else { // Don't need new name and Only rename subtitle
                            val name = videoName.substring(0..videoName.length - (videoFormat.length + 1))
                            Companion.rename(subtitle, name, subtitleFormat)
                        }
                        // Delete renamed subtitle from subtitle list
                        subtitleList.remove(subtitle)
                        break
                    } catch (e: Exception) {
                        e.stackTrace
                    }
                }
            }
        }
        println("file renamed")
    }

    private fun extractVS(selectedFiles: MutableList<File>) {
        videoList.clear()
        subtitleList.clear()

        selectedFiles.forEach { file ->
            val format = file.name.split(".").last()

            if (format == videoFormat) {
                videoList.add(file)
            } else if (format == subtitleFormat) {
                subtitleList.add(file)
            }
        }
    }

    private fun isValid(video: String, sub: String): Boolean {
        // Convert uniques to regex
        val videoPattern = videoSubstringUnique.replace("#", ".")
        val subtitlePattern = subtitleSubstringUnique.replace("#", ".")

        // Checking for name of video and subtitle contains unique part
        if (Pattern.matches(".*$videoPattern.*", video) &&
            Pattern.matches(".*$subtitlePattern.*", sub)
        ) {
            videoUniquePart = findUniquePart(video, videoPattern)
            subtitleUniquePart = findUniquePart(sub, subtitlePattern)

            // This video and subtitle match
            if (videoUniquePart != null && subtitleUniquePart != null) {
                if (videoUniquePart!!.toUpperCase().contains(subtitleUniquePart!!.toUpperCase()))
                    return true
            }
        }
        return false
    }

    private fun findUniquePart(string: String, pattern: String): String? {
        val videoParts = string.split(".")
        for (part in videoParts) if (Pattern.matches(pattern, part)) return part
        return null
    }
}