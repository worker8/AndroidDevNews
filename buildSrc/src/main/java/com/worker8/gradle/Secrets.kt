package com.worker8.gradle

import java.io.File
import java.util.*

object Secrets {
    private const val REDDIT_CLIENT_ID = "reddit_client_id"
    val redditClientId: String by lazy {
        secretsProperties().getProperty(REDDIT_CLIENT_ID)
    }

    private fun secretsProperties(): Properties {
        val filename = "secrets.properties"
        val file = File(filename)
        if (!file.exists()) {
            throw Error(
                "You need to prepare a file called $filename in the project root directory.\n" +
                        "and contain the Reddit Client ID.\n" +
                        "The content of the file should look something like:\n\n" +
                        "(project root)$ cat $filename\n" +
                        "$REDDIT_CLIENT_ID=abcdG9Lt9-ABCg\n"
            )
        }
        return file.toProperties()
    }

    fun File.toProperties() = Properties().apply {
        if (this@toProperties.exists()) {
            load(reader())
        }
    }
}